package com.practice.spring.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.practice.spring.domain.Post;
import com.practice.spring.service.PostService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@RestController
@AllArgsConstructor
public class PostController {	

	private final PostService postService;
	
	@PostMapping("/posts")
	ResponseEntity<?> add(@RequestBody PostSaveRequestDto dto){
		Long id = postService.savePost(dto.toEntity());
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(id).toUri();
		return ResponseEntity.created(location).build();
	}
	
	// Entity 객체를 직접 받지 않고 dto 를 만들어서 dto 변경 가능하게끔
	// dto: data transfer object
	@Getter
	@Setter
	@NoArgsConstructor
	static class PostSaveRequestDto{
		private String title;
		private String content;
		private String author;
		
		// controller 에서 @RequestBody 로 외부에서 데이터를 받으면
		// 기본생성자 + set 메소드 통해서만 값이 할당됨
		// 이때는 setter 허용
		public Post toEntity() {
			System.out.println(title+content+author);
			return new Post(title,content,author);
		}
	}
}
