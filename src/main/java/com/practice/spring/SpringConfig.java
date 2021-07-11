package com.practice.spring;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.practice.spring.repository.JdbcTemplateMemberRepository;
import com.practice.spring.repository.MemberRepository;
import com.practice.spring.service.MemberService;

@Configuration
public class SpringConfig {
	private final DataSource dataSource;
	
	public SpringConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Bean
	public MemberService memberService() {
		return new MemberService(memberRepository());
	}
	
	@Bean
	public MemberRepository memberRepository() {
		return new JdbcTemplateMemberRepository(dataSource); 
	}
}
