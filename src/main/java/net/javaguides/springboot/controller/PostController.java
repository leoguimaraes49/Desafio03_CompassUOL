
package net.javaguides.springboot.controller;

import net.javaguides.springboot.entity.Post;
import net.javaguides.springboot.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> processPost(@PathVariable Long postId) {
        if (postId < 1 || postId > 100) {
            return ResponseEntity.badRequest().body("Invalid postId. Must be between 1 and 100.");
        }
        postService.processPost(postId);
        return ResponseEntity.ok("Post processed successfully.");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> disablePost(@PathVariable Long postId) {
        if (postId < 1 || postId > 100) {
            return ResponseEntity.badRequest().body("Invalid postId. Must be between 1 and 100.");
        }
        postService.disablePost(postId);
        return ResponseEntity.ok("Post disabled successfully.");
    }

    @PutMapping("/{postId}")
    public ResponseEntity<String> reprocessPost(@PathVariable Long postId) {
        if (postId < 1 || postId > 100) {
            return ResponseEntity.badRequest().body("Invalid postId. Must be between 1 and 100.");
        }
        postService.reprocessPost(postId);
        return ResponseEntity.ok("Post reprocessed successfully.");
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }
}
