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


}
