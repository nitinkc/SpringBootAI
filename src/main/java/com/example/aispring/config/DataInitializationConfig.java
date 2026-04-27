package com.example.aispring.config;

import com.example.aispring.model.Product;
import com.example.aispring.model.User;
import com.example.aispring.repository.ProductRepository;
import com.example.aispring.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for initializing test data
 */
@Configuration
public class DataInitializationConfig {
    
    /**
     * Initialize sample products and users on application startup
     */
    @Bean
    public CommandLineRunner initializeData(ProductRepository productRepository, 
                                           UserRepository userRepository) {
        return args -> {
            System.out.println("[INFO] Initializing sample data...");
            
            // Create sample products
            initializeProducts(productRepository);
            
            // Create sample users
            initializeUsers(userRepository);
            
            System.out.println("[INFO] Sample data initialization completed");
        };
    }
    
    private void initializeProducts(ProductRepository productRepository) {
        productRepository.saveAll(java.util.List.of(
            new Product(
                "Premium Laptop - 15\" Display",
                "High-performance laptop with RTX 4080, perfect for programming and video editing",
                1899.99,
                "Electronics",
                15
            ),
            new Product(
                "Wireless Noise-Canceling Headphones",
                "Professional-grade headphones with 40-hour battery life and ANC technology",
                349.99,
                "Audio",
                42
            ),
            new Product(
                "USB-C Hub with Power Delivery",
                "7-in-1 hub: USB 3.0, HDMI, SD card reader, 100W power delivery",
                79.99,
                "Accessories",
                78
            ),
            new Product(
                "4K USB Webcam",
                "Crystal clear 4K video, built-in microphone, perfect for streaming and video calls",
                179.99,
                "Electronics",
                31
            ),
            new Product(
                "Mechanical Gaming Keyboard",
                "RGB backlit, Cherry MX switches, programmable keys",
                149.99,
                "Peripherals",
                55
            ),
            new Product(
                "Professional Monitor Stand",
                "Adjustable height and tilt, supports up to 30\" monitors",
                59.99,
                "Accessories",
                87
            ),
            new Product(
                "Portable SSD 2TB",
                "Ultra-fast 1050MB/s read speed, compact design for on-the-go storage",
                249.99,
                "Storage",
                25
            ),
            new Product(
                "Desk Lamp with USB Charging",
                "LED task lamp with 3 brightness levels and integrated USB port",
                69.99,
                "Office",
                64
            ),
            new Product(
                "Wireless Mouse Ergonomic",
                "Comfortable grip design, precision tracking, 18-month battery",
                49.99,
                "Peripherals",
                120
            ),
            new Product(
                "Tablet 11-inch",
                "Lightweight tablet perfect for design work and content consumption",
                599.99,
                "Electronics",
                20
            )
        ));
        
        System.out.println("[INFO] Created 10 sample products");
    }
    
    private void initializeUsers(UserRepository userRepository) {
        userRepository.saveAll(java.util.List.of(
            new User(
                "john_tech",
                "john@example.com",
                "Purchased: Laptop (6 months ago), Headphones (3 months ago), USB Hub (1 month ago)",
                "Interested in: Tech electronics, programming tools, productivity gadgets"
            ),
            new User(
                "sarah_creative",
                "sarah@example.com",
                "Purchased: 4K Webcam (4 months ago), Monitor Stand (2 months ago)",
                "Interested in: Content creation, streaming equipment, design tools"
            ),
            new User(
                "mike_gamer",
                "mike@example.com",
                "Purchased: Gaming Keyboard (5 months ago), Wireless Mouse (2 months ago)",
                "Interested in: Gaming peripherals, performance upgrades, RGB lighting"
            )
        ));
        
        System.out.println("[INFO] Created 3 sample users");
    }
}
