package com.practice.spring;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.practice.spring.repository.JpaMemberRepository;
import com.practice.spring.repository.MemberRepository;
import com.practice.spring.service.MemberService;

@Configuration
public class SpringConfig {
	// �ڵ����� repository �� ã��. 
	private final MemberRepository memberRepository;
	public SpringConfig(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	
	@Bean
	public MemberService memberService() {
		return new MemberService(memberRepository);
	}
}
