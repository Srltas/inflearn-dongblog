package com.dongblog.dongblog.api.service;

import com.dongblog.dongblog.api.domain.Post;
import com.dongblog.dongblog.api.exception.PostNotFound;
import com.dongblog.dongblog.api.repository.PostRepository;
import com.dongblog.dongblog.api.request.PostCreate;
import com.dongblog.dongblog.api.request.PostEdit;
import com.dongblog.dongblog.api.request.PostSearch;
import com.dongblog.dongblog.api.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void test1() {
        //given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        //when
        postService.write(postCreate);

        //then
        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test2() {
        //given
        Post requestPost = Post.builder()
                .title("123456789012345")
                .content("bar")
                .build();
        postRepository.save(requestPost);

        //when
        PostResponse response = postService.get(requestPost.getId());

        //then
        assertNotNull(response);
        assertEquals("1234567890", response.getTitle());
        assertEquals("bar", response.getContent());
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void test3() {
        //given
        List<Post> requestPosts = IntStream.range(1, 20)
                        .mapToObj(i -> Post.builder()
                                .title("동블로그 제목" + i)
                                .content("내일도 출근" + i)
                                .build())
                        .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .size(10)
                .build();

        //when
        List<PostResponse> posts = postService.getList(postSearch);

        //then
        assertEquals(10L, posts.size());
        assertEquals("동블로그 제목19", posts.get(0).getTitle());
    }

    @Test
    @DisplayName("글 제목 수정")
    void test4() {
        //given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("동블로그")
                .content("bar")
                .build();

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post changePost = postRepository.findById(post.getId())
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않습니다. id=" + post.getId()));

        assertEquals("동블로그", changePost.getTitle());
    }

    @Test
    @DisplayName("글 내용 수정")
    void test5() {
        //given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("foo")
                .content("내일도 출근")
                .build();

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post changePost = postRepository.findById(post.getId())
                .orElseThrow(() -> new IllegalArgumentException("글이 존재하지 않습니다. id=" + post.getId()));

        assertEquals("내일도 출근", changePost.getContent());
    }

    @Test
    @DisplayName("게시글 삭제")
    void test6() {
        //given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);

        //when
        postService.delete(post.getId());

        //then
        assertEquals(0, postRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void test7() {
        //given
        Post post = Post.builder()
                .title("동블로그")
                .content("내일도 출근..")
                .build();
        postRepository.save(post);

        //expected
        PostNotFound e = assertThrows(PostNotFound.class, () -> postService.get(post.getId() + 1L));
        assertEquals("존재하지 않는 글입니다.", e.getMessage());
    }

    @Test
    @DisplayName("게시글 삭제 - 존재하지 않는 글")
    void test8() {
        //given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);

        //expected
        assertThrows(PostNotFound.class, () -> postService.delete(post.getId() + 1L));
    }

    @Test
    @DisplayName("글 내용 수정 - 존재하지 않는 글")
    void test9() {
        //given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("foo")
                .content("내일도 출근")
                .build();

        //expected
        assertThrows(PostNotFound.class, () -> postService.edit(post.getId() + 1L, postEdit));
    }
}