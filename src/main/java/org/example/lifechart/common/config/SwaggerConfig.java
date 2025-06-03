package org.example.lifechart.common.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI lifeChartAppOpenAPI() {
		return new OpenAPI()
			.info(new Info() // API 기본 정보
				.title("LifeChart API")
				.description("팀 프로젝트 - Spring Boot 기반 백엔드 API 명세")
				.version("v0.1.0")
				.contact(new Contact()
					.name("lifeChart")
					.email("rlqor968@gmail.com"))
				.license(new License()
					.name("MIT License")
					.url("https://opensource.org/licenses/MIT")))
			.addSecurityItem(new SecurityRequirement().addList("bearerAuth")) // 보안 스키마 적용.
			.components(new Components() // JWT 인증 설정. Swagger UI에서 JWT 토큰을 입력받아 API 테스트에 사용할 수 있도록 해줌
				.addSecuritySchemes("bearerAuth",
					new SecurityScheme()
						.name("Authorization")
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")
				))
			.externalDocs(new ExternalDocumentation() // 외부 문서 링크
				.description("깃허르 레포지토리")
				.url("깃 주소"));
	}

	@Bean
	public GroupedOpenApi publicApi() { // GroupedOpenApi: 여러 개의 API 그룹을 만들 수 있게 해줌
		return GroupedOpenApi.builder()
			.group("LifeChart 전체 API") // Swagger UI에서 이 그룹명으로 표시됨
			.pathsToMatch("/**") // 모든 경로를 문서화 > 모든 API 엔드포인트 문서화 (여러 그룹으로 나누고 싶다면 pathsToMatch("/auth/**"), pathsToMatch("/user/**") 등으로 구분할 수 있음)
			.build();
	}
}
