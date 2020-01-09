package com.gnp.autos.wsp.cotizador.eot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import com.gnp.autos.wsp.negocio.config.ConfigLog4j;
import com.gnp.autos.wsp.negocio.config.CorsConfig;

/**
 * The Class Application.
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@EnableFeignClients
@Import(value = {ConfigLog4j.class, CorsConfig.class})
public class Application {
    /** The spring enviroment. */
    @Autowired
    private Environment springEnviroment;

    /**
     * Gets the property.
     *
     * @param key the key
     * @return the property
     */
    public String getProperty(final String key) {
        return springEnviroment.getProperty(key);
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(final String... args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Web server factory customizer.
     *
     * @return the web server factory customizer
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> factory.setContextPath("/cotizadoreot");
    }
}