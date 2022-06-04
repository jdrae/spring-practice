package com.practice.spring.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import com.practice.spring.domain.Member;

public class JpaMemberRepository implements MemberRepository {
	
	private final EntityManager em; // 자동으로 인젝션
	
	public JpaMemberRepository(EntityManager em) {
		this.em = em;
	}
	
	
	@Override
	public Member save(Member member) {
		em.persist(member);
		return member;
	}

	@Override
	public Optional<Member> findById(Long id) {
		Member member = em.find(Member.class, id); // pk 만
		return Optional.ofNullable(member);
	}

	@Override
	public Optional<Member> findByName(String name) {
		List<Member> result= em.createQuery("select m from Member m where m.name = :name", Member.class)
				.setParameter("name",name)
				.getResultList();
		return result.stream().findAny();
	}

	@Override
	public List<Member> findAll() {
		return em.createQuery("select m from Member m", Member.class) // jpql : 객체를 대상으로 쿼리를 날림. (테이블 대상 아님)
				.getResultList();
	}

}
