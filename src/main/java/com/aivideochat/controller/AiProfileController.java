package com.aivideochat.controller;

import com.aivideochat.entity.AiProfile;
import com.aivideochat.repository.AiProfileRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@Controller
public class AiProfileController {

    private final AiProfileRepository aiProfileRepository;

    // Upload folders
    private final Path imageUploadPath =
            Paths.get("deedi-uploads/images");

    private final Path videoUploadPath =
            Paths.get("deedi-uploads/videos");


    public AiProfileController(
            AiProfileRepository aiProfileRepository) {

        this.aiProfileRepository = aiProfileRepository;
    }


    // =====================================================
    // USER PROFILE PAGE
    // =====================================================

    @GetMapping("/profile/{id}")
    public String showProfile(
            @PathVariable Long id,
            Model model) {

        AiProfile profile =
                aiProfileRepository.findById(id)
                        .orElse(null);

        if (profile == null) {
            return "redirect:/";
        }

        model.addAttribute("profile", profile);

        return "profile";
    }


    // =====================================================
    // ADMIN - VIEW AI PROFILES
    // =====================================================

    @GetMapping("/admin/profiles")
    public String adminProfiles(
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute(
                "profiles",
                aiProfileRepository.findAll()
        );

        return "admin/profiles";
    }


    // =====================================================
    // ADMIN - ADD PROFILE PAGE
    // =====================================================

    @GetMapping("/admin/profiles/add")
    public String showAddProfilePage(
            HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        return "admin/add-profile";
    }


    // =====================================================
    // ADMIN - ADD PROFILE WITH IMAGE + VIDEO
    // =====================================================

    @PostMapping("/admin/profiles/add")
    public String addProfile(

            @RequestParam String name,

            @RequestParam int age,

            @RequestParam String description,

            @RequestParam String personality,

            @RequestParam("profileImage")
            MultipartFile profileImage,

            @RequestParam("videoFile")
            MultipartFile videoFile,

            @RequestParam(defaultValue = "false")
            boolean online,

            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }


        // =========================
        // CHECK IMAGE
        // =========================

        if (profileImage == null ||
                profileImage.isEmpty()) {

            model.addAttribute(
                    "error",
                    "Please select a profile image."
            );

            return "admin/add-profile";
        }


        // =========================
        // CHECK VIDEO
        // =========================

        if (videoFile == null ||
                videoFile.isEmpty()) {

            model.addAttribute(
                    "error",
                    "Please select a video."
            );

            return "admin/add-profile";
        }


        try {

            // Create folders if they don't exist

            Files.createDirectories(imageUploadPath);

            Files.createDirectories(videoUploadPath);


            // =========================
            // IMAGE
            // =========================

            String imageExtension =
                    getExtension(
                            profileImage.getOriginalFilename()
                    );

            String imageFileName =
                    UUID.randomUUID()
                            + imageExtension;

            Path imagePath =
                    imageUploadPath.resolve(
                            imageFileName
                    );

            Files.copy(
                    profileImage.getInputStream(),
                    imagePath,
                    StandardCopyOption.REPLACE_EXISTING
            );


            // =========================
            // VIDEO
            // =========================

            String videoExtension =
                    getExtension(
                            videoFile.getOriginalFilename()
                    );

            String videoFileName =
                    UUID.randomUUID()
                            + videoExtension;

            Path videoPath =
                    videoUploadPath.resolve(
                            videoFileName
                    );

            Files.copy(
                    videoFile.getInputStream(),
                    videoPath,
                    StandardCopyOption.REPLACE_EXISTING
            );


            // =========================
            // SAVE PROFILE
            // =========================

            AiProfile profile =
                    new AiProfile(
                            name,
                            age,
                            description,
                            personality,
                            imageFileName,
                            online
                    );

            profile.setVideoFile(
                    videoFileName
            );

            aiProfileRepository.save(profile);


            return "redirect:/admin/profiles";


        } catch (IOException e) {

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "File upload failed. Please try again."
            );

            return "admin/add-profile";
        }
    }


    // =====================================================
    // ADMIN - DELETE AI PROFILE
    // =====================================================

    @PostMapping("/admin/profiles/delete/{id}")
    public String deleteProfile(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        if (aiProfileRepository.existsById(id)) {

            aiProfileRepository.deleteById(id);
        }

        return "redirect:/admin/profiles";
    }


    // =====================================================
    // GET FILE EXTENSION
    // =====================================================

    private String getExtension(String fileName) {

        if (fileName == null ||
                !fileName.contains(".")) {

            return "";
        }

        return fileName.substring(
                fileName.lastIndexOf(".")
        ).toLowerCase();
    }


    // =====================================================
    // ADMIN CHECK
    // =====================================================

    private boolean isAdmin(HttpSession session) {

        Boolean adminLoggedIn =
                (Boolean) session.getAttribute(
                        "adminLoggedIn"
                );

        return adminLoggedIn != null &&
                adminLoggedIn;
    }
}