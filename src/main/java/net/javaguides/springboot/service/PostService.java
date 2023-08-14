
package net.javaguides.springboot.service;

import net.javaguides.springboot.entity.Post;
import java.util.List;

public interface PostService {
    void processPost(Long postId);
    void disablePost(Long postId);
    void reprocessPost(Long postId);
    List<Post> getAllPosts();
}
