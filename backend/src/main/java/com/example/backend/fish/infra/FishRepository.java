package com.example.backend.fish.infra;

import com.example.backend.fish.domain.Fish;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FishRepository extends JpaRepository<Fish, UUID> {}
