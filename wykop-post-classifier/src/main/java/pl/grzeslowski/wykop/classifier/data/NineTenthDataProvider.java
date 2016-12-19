package pl.grzeslowski.wykop.classifier.data;

import com.google.common.base.Preconditions;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.grzeslowski.wykop.classifier.io.IoService;
import pl.grzeslowski.wykop.posts.Post;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
class NineTenthDataProvider implements DataProvider{
    private final IoService ioService;
    private final Word2Vec word2Vec;
    private final int batchSize;
    private final int maxWordsInPost;

    @Autowired
    public NineTenthDataProvider(IoService ioService, Word2Vec word2Vec,
                                 @Value("${test.batchSize}") int batchSize,
                                 @Value("${test.maxWordsInPost}") int maxWordsInPost) {
        this.ioService = checkNotNull(ioService);
        this.word2Vec = checkNotNull(word2Vec);
        this.batchSize = batchSize;
        Preconditions.checkArgument(batchSize > 0, "batchSize = " + batchSize);
        this.maxWordsInPost = maxWordsInPost;
        Preconditions.checkArgument(maxWordsInPost > 0, "maxWordsInPost = " + maxWordsInPost);
    }

    @Override
    public DataSetIterator newTrainData() {
        final Stream<Post> posts = ioService.findAllSites()
                .limit(findSplitPoint())
                .flatMap(site -> site.getPosts().stream());
        return new PostDataSetIterator(posts, word2Vec, batchSize, maxWordsInPost);
    }

    @Override
    public DataSetIterator newTestData() {
        final Stream<Post> posts = ioService.findAllSites()
                .skip(findSplitPoint())
                .flatMap(site -> site.getPosts().stream());
        return new PostDataSetIterator(posts, word2Vec, batchSize, maxWordsInPost);
    }

    private int findSplitPoint() {
        return (int) (ioService.findAllSites().count() * 9 / 10);
    }
}
