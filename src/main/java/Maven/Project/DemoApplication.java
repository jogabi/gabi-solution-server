package Maven.Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;                        // ⬅️ 추가
import org.springframework.web.servlet.config.annotation.CorsRegistry; // ⬅️ 추가
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // ⬅️ 추가

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	// ✅ 전역 CORS 설정 추가
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins(
								"http://localhost:5173",     // 로컬 개발
								"https://www.gabisolution.xyz", // 실제 배포 도메인
								"https://gabisolution.xyz"      // www 없는 도메인도 허용
						)
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.maxAge(3600);
			}
		};
	}
}
