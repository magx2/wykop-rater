package pl.grzeslowski.wykop.classifier.data;

import com.google.common.base.Preconditions;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.grzeslowski.wykop.classifier.io.IoService;
import pl.grzeslowski.wykop.posts.Post;
import pl.grzeslowski.wykop.posts.Site;

import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
class NineTenthDataProvider implements DataProvider {
    private static final Logger log = LoggerFactory.getLogger(NineTenthDataProvider.class);
    private final IoService ioService;
    private final Word2Vec word2Vec;
    private final int batchSize;
    private final int minWordsInPost;
    private final int maxWordsInPost;

    @Autowired
    public NineTenthDataProvider(IoService ioService, Word2Vec word2Vec,
                                 @Value("${test.batchSize}") int batchSize,
                                 @Value("${test.maxWordsInPost}") int maxWordsInPost,
                                 @Value("${test.minWordsInPost}") int minWordsInPost) {
        this.ioService = checkNotNull(ioService);
        this.word2Vec = checkNotNull(word2Vec);
        this.batchSize = batchSize;
        Preconditions.checkArgument(batchSize > 0, "batchSize = " + batchSize);
        this.maxWordsInPost = maxWordsInPost;
        Preconditions.checkArgument(maxWordsInPost > 0, "maxWordsInPost = " + maxWordsInPost);
        this.minWordsInPost = minWordsInPost;
        Preconditions.checkArgument(minWordsInPost > 0, "minWordsInPost = " + maxWordsInPost);
        Preconditions.checkArgument(maxWordsInPost >= minWordsInPost, "maxWordsInPost = " + maxWordsInPost + " < minWordsInPost = " + minWordsInPost);
    }

    @Override
    public DataSetIterator newTrainData() {
        final Stream<Post> posts = findAllSites()
                .limit(findSplitPoint())
                .flatMap(site -> site.getPosts().stream());
        return new PostDataSetIterator(posts, word2Vec, batchSize, maxWordsInPost);
    }

    private Stream<Site> findAllSites() {
        return ioService.findAllSites()
                .filter(post -> post.getContent().split("\\s").length > minWordsInPost);
    }

    @Override
    public DataSetIterator newTestData() {
        final Stream<Post> posts = findAllSites()
                .skip(findSplitPoint())
                .flatMap(site -> site.getPosts().stream());
        return new PostDataSetIterator(posts, word2Vec, batchSize, maxWordsInPost);
    }

    private int findSplitPoint() {
        final long count = findAllSites().count();
        log.info("There is {} examples", count);
        return (int) (count * 9 / 10);
    }
}
