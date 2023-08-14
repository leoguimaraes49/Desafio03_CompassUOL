
package net.javaguides.springboot.service.impl;

import net.javaguides.springboot.entity.Comment;
import net.javaguides.springboot.entity.Post;
import net.javaguides.springboot.entity.PostState;
import net.javaguides.springboot.repository.PostRepository;
import net.javaguides.springboot.service.ExternalApiClient;
import net.javaguides.springboot.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ExternalApiClient externalApiClient;

    @Override
    public void processPost(Long postId) {
    try {
        // Fetch basic post data from external API
        Post post = externalApiClient.fetchPostById(postId);
        post.setState(PostState.POST_OK);
        postRepository.save(post);

        // Fetch comments for the post from external API
        Comment[] comments = externalApiClient.fetchCommentsByPostId(postId);
        post.setComments(Arrays.asList(comments));
        post.setState(PostState.COMMENTS_OK);
        postRepository.save(post);

        // Enable the post
        post.setState(PostState.ENABLED);
        postRepository.save(post);
    } catch (Exception e) {
        // Log the exception
        System.out.println("Error processing post with ID: " + postId);
        e.printStackTrace();
    }
    }

    @Override
    public void disablePost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null && post.getState() == PostState.ENABLED) {
            post.setState(PostState.DISABLED);
            postRepository.save(post);
        }
    }

    @Override
    public void reprocessPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null && (post.getState() == PostState.ENABLED || post.getState() == PostState.DISABLED)) {
            // Here, we can add logic to reprocess the post. For simplicity, I am just updating its state to UPDATING.
            post.setState(PostState.UPDATING);
            postRepository.save(post);
        }
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Scheduled(fixedRate = 3600000)  // Every hour
    public void scheduledFetchPosts() {
        for (Long i = 1L; i <= 100; i++) {
            processPost(i);
        }
    }

}
