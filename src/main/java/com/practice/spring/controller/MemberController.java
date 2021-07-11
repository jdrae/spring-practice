package com.practice.spring.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.practice.spring.domain.Member;
import com.practice.spring.service.MemberService;

@RestController
public class MemberController{
	
	private final MemberService memberService;
	
	@ Autowired // 주입에는 필드 주입, 생성자 주입, 세터 주입이 있다
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@GetMapping("/members")
	public List<Member> retrieveMembers(){
		return memberService.findMembers();
	}
	
	@PostMapping("/members/{name}")
	ResponseEntity<?> add(@PathVariable String name){
		Member member = new Member();
		member.setName(name);
		Long id = memberService.join(member);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(id).toUri();
		return ResponseEntity.created(location).build();
	}
}
