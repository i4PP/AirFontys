package me.abdul.orderapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class OrderApiApplication {

    @Value("${cors.origin}")
    private String corsOrigin;

    public static void main(String[] args) {
        SpringApplication.run(OrderApiApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        String corsOrigin = this.corsOrigin;

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("*")
                        .allowedOrigins(corsOrigin)
                        .allowCredentials(true);
            }
        };
    }

}
