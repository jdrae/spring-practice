package com.example.restservice;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController // the class as a controller where every method returns a domain object instead of a view
public class GreetingController {
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	
	@GetMapping("/greeting") // e.g. @PostMapping for POST, @RequestMapping(method=GET)
	public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
		// The object data will be written directly to the HTTP response as JSON(automatically)
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
	
	
	
}
