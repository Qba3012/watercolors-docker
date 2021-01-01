package ogorkiewicz.jakub.mail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
 
    @Bean
	public Docket get() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.basePackage("ogorkiewicz.jakub.mail.resource"))
			.build()
			.useDefaultResponseMessages(false)
			.apiInfo(createApiInfo());
	}

	private ApiInfo createApiInfo() {
		return new ApiInfoBuilder()
			.title("Watercolors mailing service")
			.version("1.0")
			.contact(new Contact("Jakub Ogórkiewicz", "https://github.com/Qba3012", "jakubogorkiewicz89@gmail.com"))
			.build();
    }
    
}