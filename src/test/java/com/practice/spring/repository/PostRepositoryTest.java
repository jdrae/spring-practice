package com.practice.spring.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.practice.spring.domain.Post;

@SpringBootTest
public class PostRepositoryTest {
	@Autowired
	PostRepository postRepository;
	
	@AfterEach // 테스트 메소드가 끝날 때마다 호출
	public void cleanup() {
		// repository 전체 비움
		postRepository.deleteAll();
	}
	
	@Test
	public void 게시글저장_불러오기() {
		// BDD: behavior driven development
		// given: 테스트 환경 구축
		//postRepository.save(new Post("test title","test contents","testauthor"));
		postRepository.save(Post.builder()
				.title("test title")
				.content("test contents")
				.author("test author")
				.build());
		// when: 테스트 행위 선언
		List<Post> postList = postRepository.findAll();
		// then: 테스트 검증
		Post post = postList.get(postList.size() - 1);
		System.out.println(post.getTitle() + post.getContent());
		assertThat(post.getTitle()).isEqualTo("test title");
		assertThat(post.getContent()).isEqualTo("test contents");
	}
	
	@Test
	public void BaseTimeEntity_등록() {
		// given
		LocalDateTime now = LocalDateTime.now();
		postRepository.save(Post.builder()
				.title("test title")
				.content("test contents")
				.author("test author")
				.build());
		// when
		List<Post> postList = postRepository.findAll();
		// then
		Post post = postList.get(postList.size() - 1);
		assertThat(post.getCreatedDate()).isAfterOrEqualTo(now);
		assertThat(post.getModifiedDate()).isAfterOrEqualTo(now);
	}
}
