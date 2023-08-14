
package net.javaguides.springboot.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_history")
public class PostHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private PostState state;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    public PostHistory() {}

    public PostHistory(Post post, PostState state, Date date) {
        this.post = post;
        this.state = state;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PostState getState() {
        return state;
    }

    public void setState(PostState state) {
        this.state = state;
    }

    // Getters, Setters, and other necessary methods...
}
