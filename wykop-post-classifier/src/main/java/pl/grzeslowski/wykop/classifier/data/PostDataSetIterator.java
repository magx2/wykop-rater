package pl.grzeslowski.wykop.classifier.data;

import com.google.common.base.Preconditions;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.grzeslowski.wykop.posts.Post;
import pl.grzeslowski.wykop.posts.Score;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.stream;

class PostDataSetIterator implements DataSetIterator {
    private static final Logger log = LoggerFactory.getLogger(PostDataSetIterator.class);
    public static final int LABELS_SIZE = 3;
    private final Iterator<Post> iterator;
    private final Word2Vec word2Vec;
    private final int batchSize;
    private final int maxWordsInPost;
    private final int layerSize;

    PostDataSetIterator(Stream<Post> posts, Word2Vec word2Vec, int batchSize, int maxWordsInPost) {
        this.iterator = posts.iterator();
        this.word2Vec = checkNotNull(word2Vec);
        this.batchSize = batchSize;
        this.maxWordsInPost = maxWordsInPost;
        this.layerSize = word2Vec.lookupTable().layerSize();
    }

    @Override
    public DataSet next(int howMuchToTake) {
        Preconditions.checkArgument(iterator.hasNext(), "Iterator has no more elements!");

        List<Post> toProcess = new ArrayList<>(howMuchToTake);
        for (int sample = 0; sample < howMuchToTake && iterator.hasNext(); sample++) {
            toProcess.add(iterator.next());
        }

        final List<PostVectorizedContent> vectors = toProcess.stream()
                .map(this::words2vectors)
                .filter(vs -> !vs.vectorizedContent.isEmpty())
                .collect(Collectors.toList());

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        final int maxLength = vectors.stream()
                .map(p -> p.vectorizedContent)
                .mapToInt(List::size)
                .max()
                .getAsInt();

        if (maxLength > maxWordsInPost) {
            log.warn("There are post(s) that word count is bigger than maxWordsInPost! " +
                    "maxLength = {} > maxWordsInPost = {}", maxLength, maxWordsInPost);
        }

        List<PostVectorizedContent> trimmedLengthVectors = vectors.stream()
                .map(p -> new PostVectorizedContent(p.post, trimLengthOfVector(p.vectorizedContent)))
                .collect(Collectors.toList());

        final INDArray features = Nd4j.zeros(toProcess.size(), layerSize, maxLength);
        final INDArray labels = Nd4j.zeros(toProcess.size(), LABELS_SIZE, maxLength);

        INDArray featuresMask = Nd4j.zeros(toProcess.size(), maxLength);
        INDArray labelsMask = Nd4j.zeros(toProcess.size(), maxLength);

        int[] temp = new int[2];
        for (int i = 0; i < trimmedLengthVectors.size(); i++) {
            final PostVectorizedContent postVectorizedContent = trimmedLengthVectors.get(i);
            final List<INDArray> postContent = postVectorizedContent.vectorizedContent;
            final Post post = postVectorizedContent.post;

            temp[0] = i;

            // putting into features and featuresMask
            for (int k = 0; k < postContent.size(); k++) {
                INDArray vector = postContent.get(k);
                features.put(new INDArrayIndex[]{NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.point(k)}, vector);

                temp[1] = k;
                featuresMask.putScalar(temp, 1.0);
            }

            // putting into labels and labelsMask
            int idx = findLabelId(post.getScore());
            int lastIdx = postContent.size();
            labels.putScalar(new int[]{i, idx, lastIdx - 1}, 1.0);   //Set label: [0,1] for negative, [1,0] for positive
            labelsMask.putScalar(new int[]{i, lastIdx - 1}, 1.0);
        }

        return new DataSet(features, labels, featuresMask, labelsMask);
    }

    private int findLabelId(Score score) {
        if (score.isPositive()) {
            return 0;
        } else if (score.isNeutral()) {
            return 1;
        } else if (score.isNegative()) {
            return 2;
        } else {
            throw new IllegalStateException("This score is neither positive, negative nor neutral. Score = " + score);
        }
    }

    private PostVectorizedContent words2vectors(Post post) {
        final List<INDArray> vectorizedContent = stream(post.getContent().split("\\s")) // on white char
                .filter(word2Vec::hasWord)
                .map(word2Vec::getWordVectorMatrix)
                .collect(Collectors.toList());
        return new PostVectorizedContent(post, vectorizedContent);
    }

    private List<INDArray> trimLengthOfVector(List<INDArray> vector) {
        if (vector.size() > maxWordsInPost) {
            return vector.subList(0, maxWordsInPost);
        } else {
            return vector;
        }
    }

    @Override
    public int totalExamples() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int inputColumns() {
        return layerSize;
    }

    @Override
    public int totalOutcomes() {
        return LABELS_SIZE;
    }

    @Override
    public boolean resetSupported() {
        return false;
    }

    @Override
    public boolean asyncSupported() {
        return false;
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int batch() {
        return batchSize;
    }

    @Override
    public int cursor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int numExamples() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPreProcessor(DataSetPreProcessor preProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataSetPreProcessor getPreProcessor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getLabels() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public DataSet next() {
        return next(batch());
    }

    private static class PostVectorizedContent {
        private final Post post;
        private final List<INDArray> vectorizedContent;

        private PostVectorizedContent(Post post, List<INDArray> vectorizedContent) {
            this.post = post;
            this.vectorizedContent = vectorizedContent;
        }
    }
}
