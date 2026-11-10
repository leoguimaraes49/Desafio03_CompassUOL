package net.javaguides.springboot.service;

import net.javaguides.springboot.entity.Comment;
import net.javaguides.springboot.entity.Post;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalApiClient {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private final RestTemplate restTemplate;

    public ExternalApiClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
    }

    public Post fetchPostById(Long postId) {
        return restTemplate.getForObject(BASE_URL + "/posts/" + postId, Post.class);
    }

    public Comment[] fetchCommentsByPostId(Long postId) {
        return restTemplate.getForObject(BASE_URL + "/posts/" + postId + "/comments", Comment[].class);
    }
}
