package ogorkiewicz.jakub.mail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
					.allowedMethods("GET","HEAD","OPTIONS")
					.allowedOrigins("https://mojeakwarele.pl","https://www.mojeakwarele.pl","https://adm.mojeakwarele.pl","https://api.mojeakwarele.pl");
                
                registry.addMapping("/**")
					.allowedMethods("POST","PUT","DELETE","HEAD","OPTIONS")
					.allowedOrigins("https://adm.mojeakwarele.pl","https://api.mojeakwarele.pl");
            }
		};
	}

}