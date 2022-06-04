package com.practice.spring.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.practice.spring.domain.Member;
import com.practice.spring.repository.MemberRepository;

@SpringBootTest
@Transactional
public class MemberServiceIT {
	// SpringConfig 에서 가져옴
	@Autowired MemberService memberService;
	@Autowired MemberRepository memberRepository;
	
	@Test
	void join() {
		// given
		Member member = new Member();
		member.setName("hello");
		
		// when
		Long saveId = memberService.join(member);
		
		// then
		Member findMember = memberService.findOne(saveId).get();
		assertThat(member.getName()).isEqualTo(findMember.getName());
		
	}
	
	@Test
	public void 중복회원예외() {
		//given
		Member member1 = new Member();
		member1.setName("spring");
		Member member2 = new Member();
		member2.setName("spring");
	
		//when
		memberService.join(member1);
		IllegalStateException e = assertThrows(IllegalStateException.class, () ->memberService.join(member2));
		assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
	}
	
	@Test
	void findMembers() {
		
	}
	
	@Test
	void findOne() {
		
	}

}
