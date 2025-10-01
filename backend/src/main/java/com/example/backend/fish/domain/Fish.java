package com.example.backend.fish.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fish")
public class Fish {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false, length = 100)
  @NotBlank @Size(max = 100)
  private String name;

  @Column(nullable = false, length = 100)
  @NotBlank @Size(max = 100)
  private String species;

  @Column(nullable = false)
  @Min(1)
  private int length;

  @Column(nullable = false, precision = 10, scale = 2)
  @DecimalMin(value = "0.01")
  private BigDecimal weight;

  protected Fish() {}

  public Fish(String name, String species, int length, BigDecimal weight) {
    this.name = name;
    this.species = species;
    this.length = length;
    this.weight = weight;
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getSpecies() { return species; }
  public void setSpecies(String species) { this.species = species; }
  public int getLength() { return length; }
  public void setLength(int length) { this.length = length; }
  public BigDecimal getWeight() { return weight; }
  public void setWeight(BigDecimal weight) { this.weight = weight; }
}
