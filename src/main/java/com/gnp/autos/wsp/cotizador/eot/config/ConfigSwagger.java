package com.gnp.autos.wsp.cotizador.eot.config;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Class ConfigSwagger.
 */
@Configuration
@EnableSwagger2
public class ConfigSwagger {

    /**
     * Posts api.
     *
     * @return the docket
     */
    @Bean
    public Docket postsApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().paths(postPaths()).build();
    }

    /**
     * Post paths.
     *
     * @return the predicate
     */
    private static Predicate<String> postPaths() {
        return or(regex("/api/posts.*"), regex("/cotizar.*"));
    }

    /**
     * Api info.
     *
     * @return the api info
     */
    private static ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("WSP-Cotizador-EOT")
                .description("Referencia API WSP-Cotizador-EOT para desarrolladores")
                .termsOfServiceUrl("http://www.gnp.com.mx").version("1.0").build();
    }
}
