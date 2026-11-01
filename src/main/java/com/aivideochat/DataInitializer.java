package com.aivideochat;

import com.aivideochat.entity.Admin;
import com.aivideochat.entity.Gift;
import com.aivideochat.repository.AdminRepository;
import com.aivideochat.repository.GiftRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final GiftRepository giftRepository;
    private final AdminRepository adminRepository;

    public DataInitializer(GiftRepository giftRepository, AdminRepository adminRepository) {
        this.giftRepository = giftRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(String... args) {

        // NOTE: AI profiles are NOT auto-seeded here so that initial setup starts blank.
        // The administrator can manually add and delete AI companions via the Admin Panel (/admin/profiles).

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
            System.out.println("🛡️ Default Admin created: ayanadmin");
        }
    }
}
