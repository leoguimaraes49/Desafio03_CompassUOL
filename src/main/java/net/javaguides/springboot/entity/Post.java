package net.javaguides.springboot.entity;
import net.javaguides.springboot.entity.PostHistory;
import java.util.ArrayList;
import java.util.Comparator;


import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHistory> history = new ArrayList<> ();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;

    @Enumerated(EnumType.STRING)
    private PostState state;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    public PostState getState() {
        return this.history.stream ()
                .max (Comparator.comparing (PostHistory::getDate))
                .map (PostHistory::getState)
                .orElse (null);
    }
}