package com.practice.spring.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity // 테이블과 링크될 클래스(언더스코어 네이밍)
public class Post extends BaseTimeEntity{
	@Id // pk
	@GeneratedValue // pk 생성규칙. 기본 auto_increment
	private Long id;
	
	@Column(length=500, nullable = false) // varchar(255) 길이 늘림
	private String title;
	
	@Column(columnDefinition="TEXT",nullable=false) // 문자열 타입 text
	private String content;
	
	// 굳이 선언안해도 필드는 컬럼
	private String author;
	
	// 생성자에 입력해야할 값이 많은 경우 
	// Lombok 라이브러리의 @Builder 사용 (cf. 빌더 패턴)
	// 가독성이 좋아지며 파라미터 위치를 헷갈리지 않기 때문
	@Builder
	public Post(String title, String content, String author) {
		this.title = title;
		this.content = content;
		this.author = author;
	}
	
	// arg 를 받는 생성자가 있으면
	// 꼭 기본생성자 추가: No default constructor for entity
	// 또는 @NoArgsConstructor 추가
	// Entity 클래스를 프로젝트 코드상에서 기본생성자로 생성하는 것은 막되, JPA에서 Entity 클래스를 생성하는것은 허용
	// protected Post() {
	// }

	// setter 는 직접 사용하기보다 값을 변경하려는 이유가 적힌 메소드를 생성하고 수정
	// ex. updateTitle() 에서 title 수정
}
