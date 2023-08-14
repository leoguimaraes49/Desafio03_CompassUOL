
package net.javaguides.springboot.repository;

import net.javaguides.springboot.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Additional custom methods if needed
}
