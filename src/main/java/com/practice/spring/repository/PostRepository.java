package com.practice.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.spring.domain.Post;

// Repository 는 DAO 역할
public interface PostRepository extends JpaRepository<Post, Long> {
	// 기본적인 crud 메소드 자동 생성
	// @Repository 추가할 필요 없음
}
