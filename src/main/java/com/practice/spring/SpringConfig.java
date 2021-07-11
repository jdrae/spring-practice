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
	private EntityManager em;
	
	@Autowired
	public SpringConfig(EntityManager em) {
		this.em = em;
	}
	
	@Bean
	public MemberService memberService() {
		return new MemberService(memberRepository());
	}
	
	@Bean
	public MemberRepository memberRepository() {
		return new JpaMemberRepository(em); 
	}
}
