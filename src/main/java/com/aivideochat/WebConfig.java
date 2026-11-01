package com.aivideochat;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String projectRoot = System.getProperty("user.dir");
        File imageDir = new File(projectRoot, "deedi-uploads/images");
        File videoDir = new File(projectRoot, "deedi-uploads/videos");

        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        if (!videoDir.exists()) {
            videoDir.mkdirs();
        }

        String imageLocation = imageDir.toURI().toString();
        String videoLocation = videoDir.toURI().toString();

        // Static resources (CSS, JS, images, logos)
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0)
                .resourceChain(true);

        // Uploaded images & videos
        registry.addResourceHandler("/api/image/**")
                .addResourceLocations(imageLocation)
                .setCachePeriod(3600);

        registry.addResourceHandler("/api/video/**")
                .addResourceLocations(videoLocation)
                .setCachePeriod(3600);
    }
}
