package com.swiftlyassess.vehicle_manager_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class VehicleManagerSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(VehicleManagerSpringbootApplication.class, args);
	}
        
    /**
     *
     * @return
     */
    @Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/v1/vehicles/").allowedOrigins("http://localhost:5173");
			}
		};
	}

}
