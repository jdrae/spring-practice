package com.practice.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.practice.spring.repository.MemberRepository;
import com.practice.spring.repository.PostRepository;
import com.practice.spring.service.MemberService;
import com.practice.spring.service.PostService;

@Configuration
public class SpringConfig {
	// �ڵ����� repository �� ã��. 
	private final MemberRepository memberRepository;
	private final PostRepository postRepository;
	
	@Autowired
	public SpringConfig(MemberRepository memberRepository, PostRepository postRepository) {
		this.memberRepository = memberRepository;
		this.postRepository = postRepository;
	}
	
	@Bean
	public MemberService memberService() {
		return new MemberService(memberRepository);
	}
	
	@Bean
	public PostService postService() {
		return new PostService(postRepository);
	}
}
