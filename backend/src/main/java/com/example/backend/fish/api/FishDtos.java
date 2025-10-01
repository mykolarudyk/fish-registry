package com.example.backend.fish.api;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public final class FishDtos {

  public record FishRequest(
      @NotBlank @Size(max = 100) String name,
      @NotBlank @Size(max = 100) String species,
      @Min(1) int length,
      @DecimalMin("0.01") BigDecimal weight
  ) {}

  public record FishResponse(
      UUID id, String name, String species, int length, BigDecimal weight
  ) {}
}
