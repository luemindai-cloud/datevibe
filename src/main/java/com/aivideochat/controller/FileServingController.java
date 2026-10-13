package com.aivideochat.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileServingController {

    /**
     * Serve images from deedi-uploads/images directory
     * Usage: <img src="/api/image/filename.jpg">
     */
    @GetMapping("/api/image/{filename}")
    public ResponseEntity<byte[]> serveImage(@PathVariable String filename) {
        try {
            // Get project root directory
            String projectRoot = System.getProperty("user.dir");
            Path imageUploadPath = Paths.get(projectRoot, "deedi-uploads", "images");
            Path filePath = imageUploadPath.resolve(filename).normalize();
            
            System.out.println("========== IMAGE REQUEST ==========");
            System.out.println("Requested filename: " + filename);
            System.out.println("Project root: " + projectRoot);
            System.out.println("Image upload path: " + imageUploadPath.toAbsolutePath());
            System.out.println("Full file path: " + filePath.toAbsolutePath());
            
            // Security: Prevent path traversal attacks
            if (!filePath.getParent().equals(imageUploadPath.toAbsolutePath())) {
                System.out.println("❌ SECURITY: Path traversal attempt blocked!");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            File file = filePath.toFile();
            if (!file.exists()) {
                System.out.println("❌ Image not found at: " + filePath.toAbsolutePath());
                System.out.println("File exists check: " + Files.exists(filePath));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            System.out.println("✅ Image loaded successfully, size: " + fileContent.length + " bytes");
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", getContentType(filename));
            headers.add("Cache-Control", "max-age=3600");
            
            System.out.println("========== END IMAGE REQUEST ==========\n");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);

        } catch (IOException e) {
            System.out.println("❌ IOException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Serve videos from deedi-uploads/videos directory
     * Usage: <video src="/api/video/filename.mp4">
     */
    @GetMapping("/api/video/{filename}")
    public ResponseEntity<byte[]> serveVideo(@PathVariable String filename) {
        try {
            // Get project root directory
            String projectRoot = System.getProperty("user.dir");
            Path videoUploadPath = Paths.get(projectRoot, "deedi-uploads", "videos");
            Path filePath = videoUploadPath.resolve(filename).normalize();
            
            System.out.println("========== VIDEO REQUEST ==========");
            System.out.println("Requested filename: " + filename);
            System.out.println("Project root: " + projectRoot);
            System.out.println("Video upload path: " + videoUploadPath.toAbsolutePath());
            System.out.println("Full file path: " + filePath.toAbsolutePath());
            
            // Security: Prevent path traversal attacks
            if (!filePath.getParent().equals(videoUploadPath.toAbsolutePath())) {
                System.out.println("❌ SECURITY: Path traversal attempt blocked!");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            File file = filePath.toFile();
            if (!file.exists()) {
                System.out.println("❌ Video not found at: " + filePath.toAbsolutePath());
                System.out.println("File exists check: " + Files.exists(filePath));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            System.out.println("✅ Video loaded successfully, size: " + fileContent.length + " bytes");
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", getContentType(filename));
            headers.add("Cache-Control", "max-age=3600");
            
            System.out.println("========== END VIDEO REQUEST ==========\n");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);

        } catch (IOException e) {
            System.out.println("❌ IOException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Determine MIME type based on file extension
     */
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
        } else if (lower.endsWith(".avi")) {
            return "video/x-msvideo";
        } else if (lower.endsWith(".mov")) {
            return "video/quicktime";
        } else if (lower.endsWith(".webm")) {
            return "video/webm";
        }
        
        return "application/octet-stream";
    }
}
