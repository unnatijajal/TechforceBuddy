package com.techforcebuddybl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfig  extends WebMvcConfigurationSupport{

	@Override
	protected void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // apply to all enpoints
				.allowedOrigins("http://localhost:8081")
				.allowedMethods("GET","POST")
				.allowedHeaders("*")
				.allowCredentials(true);
		super.addCorsMappings(registry);
	}

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/pdf/**")
	            .addResourceLocations(System.getProperty("user.dir")+"/src/main/resources/pdf/");
	    super.addResourceHandlers(registry);
	}

}
