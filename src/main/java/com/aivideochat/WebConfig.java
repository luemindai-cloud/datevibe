package com.aivideochat;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Get the project root (working directory)
        String projectRoot = System.getProperty("user.dir");
        
        // Build absolute paths for uploads
        File imageDir = new File(projectRoot, "deedi-uploads/images");
        File videoDir = new File(projectRoot, "deedi-uploads/videos");

        // Ensure directories exist
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        if (!videoDir.exists()) {
            videoDir.mkdirs();
        }

        // Convert to proper file URI format
        // file:/// format with forward slashes for Windows compatibility
        String imagePath = "file:///" + imageDir.getAbsolutePath().replace("\\", "/") + "/";
        String videoPath = "file:///" + videoDir.getAbsolutePath().replace("\\", "/") + "/";

        System.out.println("========== RESOURCE PATHS ==========");
        System.out.println("IMAGE UPLOAD PATH = " + imagePath);
        System.out.println("VIDEO UPLOAD PATH = " + videoPath);
        System.out.println("IMAGE DIR EXISTS = " + imageDir.exists());
        System.out.println("VIDEO DIR EXISTS = " + videoDir.exists());
        System.out.println("====================================");

        // Static resources (CSS, JS, images, logos) - map /static/** to classpath:/static/
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0)
                .resourceChain(true);

        // AI profile images - map /api/image/** to deedi-uploads/images/
        registry.addResourceHandler("/api/image/**")
                .addResourceLocations(imagePath)
                .setCachePeriod(3600);

        // AI profile videos - map /api/video/** to deedi-uploads/videos/
        registry.addResourceHandler("/api/video/**")
                .addResourceLocations(videoPath)
                .setCachePeriod(3600);
    }
}