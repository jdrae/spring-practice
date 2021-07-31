package com.practice.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component // 빈 등록 할때. 또는 SpringConfig 에 등록
public class TimeTraceAop {
	
	@Around("execution(* com.practice..*(..))") // 패키지 하위에 있는 것에 다 적용
	public Object execute(ProceedingJoinPoint joinPoint) throws Throwable{
		long start = System.currentTimeMillis();
		System.out.println("Start: " +joinPoint.toString());
		try {
			return joinPoint.proceed();
		} finally {
			long finish = System.currentTimeMillis();
			long timeMs = finish - start;
			System.out.println("End: "+joinPoint.toString() + " " + timeMs + "ms");
		}
		
	}
}

/*
 * 
 * 회원목록 조회할때
 * Start: execution(List com.practice.spring.controller.MemberController.retrieveMembers())
Start: execution(List com.practice.spring.service.MemberService.findMembers())
Start: execution(List org.springframework.data.jpa.repository.JpaRepository.findAll())
*/
