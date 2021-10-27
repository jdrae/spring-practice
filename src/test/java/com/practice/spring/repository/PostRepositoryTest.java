package com.practice.spring.repository;

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
		postRepository.save(new Post("test title","test contents","testauthor"));
		// when: 테스트 행위 선언
		List<Post> postList = postRepository.findAll();
		// then: 테스트 검증
		Post post = postList.get(0);
		System.out.println(post.getTitle() + post.getContent());
		assertThat(post.getTitle()).isEqualTo("test title");
		assertThat(post.getContent()).isEqualTo("test contents");
	}
}
