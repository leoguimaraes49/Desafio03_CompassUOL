package net.javaguides.springboot.service.impl;

import net.javaguides.springboot.entity.Comment;
import net.javaguides.springboot.entity.Post;
import net.javaguides.springboot.entity.PostState;
import net.javaguides.springboot.repository.PostRepository;
import net.javaguides.springboot.service.ExternalApiClient;
import net.javaguides.springboot.service.PostService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);
    private final PostRepository postRepository;
    private final ExternalApiClient externalApiClient;

    public PostServiceImpl(PostRepository postRepository, ExternalApiClient externalApiClient) {
        this.postRepository = postRepository;
        this.externalApiClient = externalApiClient;
    }

    @Override
    @Transactional
    public void processPost(Long postId) {
        Post post = externalApiClient.fetchPostById(postId);
        Comment[] comments = externalApiClient.fetchCommentsByPostId(postId);
        if (post == null || comments == null || !postId.equals(post.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Invalid response from external API");
        }
        for (Comment comment : comments) {
            comment.setPost(post);
        }
        post.setComments(Arrays.asList(comments));
        post.setState(PostState.ENABLED);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void disablePost(Long postId) {
        Post post = findPost(postId);
        if (post.getState() != PostState.ENABLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Post is not enabled");
        }
        post.setState(PostState.DISABLED);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void reprocessPost(Long postId) {
        Post existing = findPost(postId);
        if (existing.getState() != PostState.ENABLED && existing.getState() != PostState.DISABLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Post cannot be reprocessed");
        }
        processPost(postId);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 3600000)
    public void scheduledFetchPosts() {
        for (long id = 1; id <= 100; id++) {
            try {
                processPost(id);
            } catch (RuntimeException ex) {
                log.warn("Could not process post {}: {}", id, ex.getMessage());
            }
        }
    }
}
