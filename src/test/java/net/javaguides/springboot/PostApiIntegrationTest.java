package net.javaguides.springboot;

import net.javaguides.springboot.entity.Comment;
import net.javaguides.springboot.entity.Post;
import net.javaguides.springboot.repository.PostRepository;
import net.javaguides.springboot.service.ExternalApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb-integration")
@AutoConfigureMockMvc
class PostApiIntegrationTest {

    @Autowired private MockMvc mvc;
    @Autowired private PostRepository posts;
    @MockBean private ExternalApiClient externalApi;

    @BeforeEach
    void setUp() {
        posts.deleteAll();
        reset(externalApi);
        Post post = new Post();
        post.setId(1L);
        post.setTitle("First post");
        post.setBody("A body");
        Comment comment = new Comment();
        comment.setId(11L);
        comment.setName("Comment");
        comment.setBody("A reply");
        when(externalApi.fetchPostById(1L)).thenReturn(post);
        when(externalApi.fetchCommentsByPostId(1L)).thenReturn(new Comment[]{comment});
    }

    @Test
    void collectsPersistsAndReturnsTheCommentsWithoutRecursion() throws Exception {
        mvc.perform(post("/posts/1")).andExpect(status().isOk());
        mvc.perform(get("/posts")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].state").value("ENABLED"))
                .andExpect(jsonPath("$[0].comments[0].id").value(11))
                .andExpect(jsonPath("$[0].comments[0].post").doesNotExist());
    }

    @Test
    void disablesAndReprocessesAnExistingPost() throws Exception {
        mvc.perform(post("/posts/1")).andExpect(status().isOk());
        mvc.perform(delete("/posts/1")).andExpect(status().isOk());
        mvc.perform(get("/posts")).andExpect(jsonPath("$[0].state").value("DISABLED"));
        mvc.perform(delete("/posts/1")).andExpect(status().isConflict());
        mvc.perform(put("/posts/1")).andExpect(status().isOk());
        mvc.perform(get("/posts")).andExpect(jsonPath("$[0].state").value("ENABLED"));
        verify(externalApi, times(2)).fetchPostById(1L);
        verify(externalApi, times(2)).fetchCommentsByPostId(1L);
    }

    @Test
    void keepsExistingPostWhenReprocessingFails() throws Exception {
        mvc.perform(post("/posts/1")).andExpect(status().isOk());
        mvc.perform(delete("/posts/1")).andExpect(status().isOk());
        when(externalApi.fetchCommentsByPostId(1L)).thenThrow(new RestClientException("network failure"));
        mvc.perform(put("/posts/1")).andExpect(status().isBadGateway());
        mvc.perform(get("/posts")).andExpect(jsonPath("$[0].state").value("DISABLED"))
                .andExpect(jsonPath("$[0].comments[0].id").value(11));
    }

    @Test
    void reportsInvalidMissingAndUnavailablePosts() throws Exception {
        mvc.perform(post("/posts/101")).andExpect(status().isBadRequest());
        mvc.perform(delete("/posts/1")).andExpect(status().isNotFound());
        when(externalApi.fetchPostById(anyLong())).thenThrow(new RestClientException("network failure"));
        mvc.perform(post("/posts/1")).andExpect(status().isBadGateway());
        mvc.perform(get("/posts")).andExpect(jsonPath("$.length()").value(0));
    }
}
