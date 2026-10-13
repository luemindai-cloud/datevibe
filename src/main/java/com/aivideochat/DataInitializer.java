package com.aivideochat;

import com.aivideochat.entity.AiProfile;
import com.aivideochat.repository.AiProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.aivideochat.entity.Gift;
import com.aivideochat.repository.GiftRepository;

import com.aivideochat.entity.Admin;
import com.aivideochat.repository.AdminRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AiProfileRepository aiProfileRepository;
    private final GiftRepository giftRepository;
    private final AdminRepository adminRepository;
    

    public DataInitializer(AiProfileRepository aiProfileRepository,GiftRepository giftRepository,
    		AdminRepository adminRepository) {
        this.aiProfileRepository = aiProfileRepository;
        this.giftRepository = giftRepository;
        this.adminRepository = adminRepository;

    }

    @Override
    public void run(String... args) {

        if (aiProfileRepository.count() == 0) {

            AiProfile sofia = new AiProfile(
                "Sofia",
                24,
                "Sweet, friendly and loves interesting conversations.",
                "Sweet and caring",
                "083a8963-a6e5-4fa8-a1f9-734b791ecdcd.jpeg",
                true
            );

            sofia.setVideoFile("68b4fb8d-8ad3-48e9-95ea-0ecba43fae80.mp4");

            aiProfileRepository.save(sofia);


            AiProfile emma = new AiProfile(
                "Emma",
                23,
                "Fun, playful and always ready for a conversation.",
                "Playful and energetic",
                "3787450d-cbc5-4c22-9c5c-dece43ed9cdc.jpeg",
                true
            );

            emma.setVideoFile("9cadbe5c-3ae2-4584-b852-6bf0fedef95f.mp4");

            aiProfileRepository.save(emma);


            AiProfile mia = new AiProfile(
                "Mia",
                25,
                "Calm, caring and enjoys meaningful conversations.",
                "Calm and caring",
                "38c1f6f1-3765-4ecc-abc7-2e153d00d63d.jpeg",
                true
            );

            mia.setVideoFile("cd0d0ac6-29c9-493d-8391-f6ef7ee37ea3.mp4");

            aiProfileRepository.save(mia);


            AiProfile lily = new AiProfile(
                "Lily",
                22,
                "Cheerful, energetic and loves meeting new people.",
                "Cheerful and adventurous",
                "42a8212d-e76c-48b4-8ebb-dee2696a5c1c.jpg",
                true
            );

            lily.setVideoFile("d2f0d0bf-93d8-4a18-88d0-004e5acb5d9a.mp4");

            aiProfileRepository.save(lily);


            System.out.println(
                "AI profiles created successfully!"
            );

        } else {

            for (AiProfile profile : aiProfileRepository.findAll()) {

                if (profile.getName().equalsIgnoreCase("Sofia")) {
                    profile.setProfileImage("083a8963-a6e5-4fa8-a1f9-734b791ecdcd.jpeg");
                    profile.setVideoFile("68b4fb8d-8ad3-48e9-95ea-0ecba43fae80.mp4");
                }

                if (profile.getName().equalsIgnoreCase("Emma")) {
                    profile.setProfileImage("3787450d-cbc5-4c22-9c5c-dece43ed9cdc.jpeg");
                    profile.setVideoFile("9cadbe5c-3ae2-4584-b852-6bf0fedef95f.mp4");
                }

                if (profile.getName().equalsIgnoreCase("Mia")) {
                    profile.setProfileImage("38c1f6f1-3765-4ecc-abc7-2e153d00d63d.jpeg");
                    profile.setVideoFile("cd0d0ac6-29c9-493d-8391-f6ef7ee37ea3.mp4");
                }

                if (profile.getName().equalsIgnoreCase("Lily")) {
                    profile.setProfileImage("42a8212d-e76c-48b4-8ebb-dee2696a5c1c.jpg");
                    profile.setVideoFile("d2f0d0bf-93d8-4a18-88d0-004e5acb5d9a.mp4");
                }

                aiProfileRepository.save(profile);
            }

            System.out.println(
                "Existing AI profiles updated with video files!"
            );
        }
        
        if (giftRepository.count() == 0) {

            giftRepository.save(new Gift("Rose", "🌹", 10));
            giftRepository.save(new Gift("Kiss", "💋", 30));
            giftRepository.save(new Gift("Heart", "❤️", 50));
            giftRepository.save(new Gift("Lollipop", "🍭", 20));
            giftRepository.save(new Gift("Ice Cream", "🍦", 40));
            giftRepository.save(new Gift("Balloons", "🎈", 50));
            giftRepository.save(new Gift("Cake", "🎂", 100));
            giftRepository.save(new Gift("Lipstick", "💄", 100));
            giftRepository.save(new Gift("Teddy Bear", "🧸", 150));
            giftRepository.save(new Gift("Flowers", "🌷", 200));
            giftRepository.save(new Gift("Ring", "💍", 300));
            giftRepository.save(new Gift("Crown", "👑", 500));
            giftRepository.save(new Gift("Diamond", "💎", 1000));
            giftRepository.save(new Gift("Unicorn", "🦄", 1500));
            giftRepository.save(new Gift("Love Gift", "💝", 2000));
            giftRepository.save(new Gift("Bouquet", "💐", 300));
            giftRepository.save(new Gift("High Heels", "👠", 500));
            giftRepository.save(new Gift("Pearl", "🦪", 400));
            giftRepository.save(new Gift("Fireworks", "🎆", 250));
            giftRepository.save(new Gift("Love Heart", "💖", 100));
            giftRepository.save(new Gift("Luxury Bag", "👜", 800));
            giftRepository.save(new Gift("Luxury Dress", "👗", 1000));

            System.out.println("🎁 22 gifts added successfully!");
        }
        
        if (adminRepository.count() == 0) {

            Admin admin = new Admin(
                    "ayanadmin",
                    "ayandeediowner@916"
            );
            adminRepository.save(admin);
        }
    }
}