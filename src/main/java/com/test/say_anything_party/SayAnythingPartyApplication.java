package com.test.say_anything_party;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // <--- 이 패키지 기준으로 @ComponentScan 수행
public class SayAnythingPartyApplication {
	public static void main(String[] args) {
		SpringApplication.run(SayAnythingPartyApplication.class, args);
	}
}