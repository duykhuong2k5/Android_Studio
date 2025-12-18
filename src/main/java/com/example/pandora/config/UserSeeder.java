package com.example.pandora.config;

import com.example.pandora.model.User;
import com.example.pandora.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder implements CommandLineRunner {

    private final UserRepository repo;

    public UserSeeder(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;

        User u = new User();
        u.setFullName("Test Kid");
        u.setEmail("kid@test.com");
        u.setPassword("123456");
        u.setRole("ROLE_CUSTOMER");

        repo.save(u);
    }
}
