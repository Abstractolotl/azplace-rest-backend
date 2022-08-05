package de.abstractolotl.azplace.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.version}")
    private String version;

    @Bean
    public OpenAPI springOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("AzPlace REST-Backend")
                        .description("REST Backend for az/place")
                        .license(new License()
                                .name("MIT")
                                .url("https://github.com/Abstractolotl/azplace-rest-backend/blob/main/LICENSE"))
                        .version(version));
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        String[] paths = { "/**"  };
        String[] packagedToMatch = { "de.abstractolotl.azplace" };
        return GroupedOpenApi
                .builder()
                .group("azplace")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch)
                .addOpenApiCustomiser(openApi ->  {
                    Info info = openApi.getInfo();
                    info.setDescription("REST Backend for az/place");
                }).build();
    }
}
