package com.practice.spring.service;

import org.springframework.transaction.annotation.Transactional;

import com.practice.spring.domain.Post;
import com.practice.spring.repository.PostRepository;

@Transactional
public class PostService {
	private PostRepository postRepository;
	public PostService(PostRepository postRepository) {
		this.postRepository = postRepository;
	}
	
	// 글 저장
	public Long savePost(Post post) {
		postRepository.save(post);
		return post.getId();
	}
}
