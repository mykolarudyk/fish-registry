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
            new Fish("Luna", "Trout", 42, new BigDecimal("1.05")),
            new Fish("Nemo", "Clownfish", 15, new BigDecimal("0.15")),
            new Fish("Dory", "Blue Tang", 18, new BigDecimal("0.20")),
            new Fish("Goldie", "Goldfish", 10, new BigDecimal("0.10")),
            new Fish("Bubbles", "Betta", 8, new BigDecimal("0.08")),
            new Fish("Finn", "Guppy", 5, new BigDecimal("0.05")),
            new Fish("Marlin", "Snapper", 60, new BigDecimal("2.50")),
            new Fish("Coral", "Grouper", 80, new BigDecimal("4.00")),
            new Fish("Shadow", "Catfish", 50, new BigDecimal("1.80")),
            new Fish("Flash", "Mackerel", 45, new BigDecimal("1.20")),
            new Fish("Sunny", "Tuna", 90, new BigDecimal("5.00")),
            new Fish("Rocky", "Bass", 40, new BigDecimal("1.00")),
            new Fish("Zeus", "Pike", 75, new BigDecimal("3.50")),
            new Fish("Athena", "Perch", 30, new BigDecimal("0.70")),
            new Fish("Cleo", "Carp", 35, new BigDecimal("0.90"))
        ));
      }
    };
  }
}
