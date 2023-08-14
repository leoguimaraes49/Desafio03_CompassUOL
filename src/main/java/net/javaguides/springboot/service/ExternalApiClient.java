
package net.javaguides.springboot.service;

import net.javaguides.springboot.entity.Comment;
import net.javaguides.springboot.entity.Post;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalApiClient {

    private final String BASE_URL = "https://jsonplaceholder.typicode.com";

    @Cacheable(cacheNames = "posts", key = "#postId")
    public Post fetchPostById(Long postId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(BASE_URL + "/posts/" + postId, Post.class);
    }

    @Cacheable(cacheNames = "comments", key = "#postId")
    public Comment[] fetchCommentsByPostId(Long postId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(BASE_URL + "/posts/" + postId + "/comments", Comment[].class);
    }
}
