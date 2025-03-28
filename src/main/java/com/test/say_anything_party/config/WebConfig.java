package com.test.say_anything_party.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 경로에 대해 CORS 설정을 추가
        registry.addMapping("/**") // 모든 URL 경로에 대해 CORS 허용
                .allowedOrigins("http://127.0.0.1:5503", "http://localhost:3000") // 로컬 클라이언트 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용할 HTTP 메소드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 인증 정보를 허용
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") // 요청 URL 패턴
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/") // 실제 폴더 경로
                .setCachePeriod(3600); // 캐시 설정 (선택 사항)
    }
}
