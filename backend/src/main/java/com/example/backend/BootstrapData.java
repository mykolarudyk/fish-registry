package com.example.backend;

import com.example.backend.fish.domain.Fish;
import com.example.backend.fish.infra.FishRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapData {
  @Bean
  CommandLineRunner seedFish(FishRepository repo) {
    return args -> {
      if (repo.count() == 0) {
        repo.saveAll(List.of(
            new Fish("Bella", "Salmon", 70, new BigDecimal("3.20")),
            new Fish("Spike", "Cod", 55, new BigDecimal("2.10")),
            new Fish("Luna", "Trout", 42, new BigDecimal("1.05"))
        ));
      }
    };
  }
}