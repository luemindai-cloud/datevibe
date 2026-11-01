package com.aivideochat.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileServingController {

    private final Path imageUploadDir;
    private final Path videoUploadDir;

    public FileServingController() {
        String projectRoot = System.getProperty("user.dir");
        this.imageUploadDir = Paths.get(projectRoot, "deedi-uploads", "images").toAbsolutePath().normalize();
        this.videoUploadDir = Paths.get(projectRoot, "deedi-uploads", "videos").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.imageUploadDir);
            Files.createDirectories(this.videoUploadDir);
        } catch (IOException e) {
            System.err.println("Failed to create upload directories: " + e.getMessage());
        }
    }

    /**
     * Serve images from deedi-uploads/images directory.
     * Falls back to a default classpath logo if the specific image file is not found.
     */
    @GetMapping("/api/image/{filename}")
    public ResponseEntity<byte[]> serveImage(@PathVariable String filename) {
        try {
            Path filePath = imageUploadDir.resolve(filename).normalize();

            // Security: Prevent path traversal attacks
            if (!filePath.startsWith(imageUploadDir)) {
                System.out.println("❌ SECURITY: Path traversal attempt blocked for: " + filename);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (Files.exists(filePath)) {
                byte[] fileContent = Files.readAllBytes(filePath);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", getContentType(filename));
                headers.add("Cache-Control", "max-age=3600");
                return ResponseEntity.ok().headers(headers).body(fileContent);
            }

            System.out.println("⚠️ Image not found on disk: " + filePath + ". Using classpath fallback.");
            // Classpath fallback image
            Resource fallback = new ClassPathResource("static/images/logo.jpeg");
            if (fallback.exists()) {
                byte[] fallbackBytes = fallback.getInputStream().readAllBytes();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", "image/jpeg");
                headers.add("Cache-Control", "max-age=3600");
                return ResponseEntity.ok().headers(headers).body(fallbackBytes);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            System.err.println("❌ Error serving image " + filename + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Serve videos from deedi-uploads/videos directory.
     * Falls back to classpath video if not found.
     */
    @GetMapping("/api/video/{filename}")
    public ResponseEntity<byte[]> serveVideo(@PathVariable String filename) {
        try {
            Path filePath = videoUploadDir.resolve(filename).normalize();

            // Security: Prevent path traversal attacks
            if (!filePath.startsWith(videoUploadDir)) {
                System.out.println("❌ SECURITY: Path traversal attempt blocked for video: " + filename);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (Files.exists(filePath)) {
                byte[] fileContent = Files.readAllBytes(filePath);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", getContentType(filename));
                headers.add("Cache-Control", "max-age=3600");
                return ResponseEntity.ok().headers(headers).body(fileContent);
            }

            System.out.println("⚠️ Video not found on disk: " + filePath + ". Checking classpath fallback.");
            Resource fallback = new ClassPathResource("static/videos/sofia.mp4");
            if (fallback.exists()) {
                byte[] fallbackBytes = fallback.getInputStream().readAllBytes();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", "video/mp4");
                headers.add("Cache-Control", "max-age=3600");
                return ResponseEntity.ok().headers(headers).body(fallbackBytes);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            System.err.println("❌ Error serving video " + filename + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lower.endsWith(".png")) {
            return "image/png";
        } else if (lower.endsWith(".gif")) {
            return "image/gif";
        } else if (lower.endsWith(".webp")) {
            return "image/webp";
        } else if (lower.endsWith(".mp4")) {
            return "video/mp4";
        } else if (lower.endsWith(".webm")) {
            return "video/webm";
        } else if (lower.endsWith(".mov")) {
            return "video/quicktime";
        } else if (lower.endsWith(".avi")) {
            return "video/x-msvideo";
        }
        return "application/octet-stream";
    }
}
