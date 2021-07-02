package com.practice.spring.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practice.spring.domain.Member;
import com.practice.spring.repository.MemberRepository;

@Service
public class MemberService {
	private final MemberRepository memberRepository;
	
	@Autowired
	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	
	// 회원가입
	public Long join(Member member) {
		validateDuplicateMember(member);
		memberRepository.save(member);
		return member.getId();
	}
	
	private void validateDuplicateMember(Member member) {
		memberRepository.findByName(member.getName())
		.ifPresent(m ->{
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		});
	}

	// 조회
	public List<Member> findMembers(){
		return memberRepository.findAll();
	}
	
	
	// 상세
	public Optional<Member> findOne(Long memberId){
		return memberRepository.findById(memberId);
	}
}
