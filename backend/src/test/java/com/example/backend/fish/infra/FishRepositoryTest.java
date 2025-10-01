package com.example.backend.fish.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.backend.fish.domain.Fish;
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class FishRepositoryTest {

  @Autowired
  FishRepository repo;

  @Test
  void saveAndFindAll() {
    var f = new Fish("Nemo", "Clownfish", 10, new BigDecimal("0.25"));
    repo.saveAndFlush(f);

    List<Fish> all = repo.findAll();
    assertThat(all).isNotEmpty();
    assertThat(all).allSatisfy(x -> {
      assertThat(x.getName()).isEqualTo("Nemo");
      assertThat(x.getSpecies()).isEqualTo("Clownfish");
      assertThat(x.getLength()).isEqualTo(10);
      assertThat(x.getWeight()).isEqualTo(new BigDecimal("0.25"));
    });
  }

  @Test
  void validationConstraintsAreEnforced() {
    var bad = new Fish("", "", 0, new BigDecimal("0.00"));
    assertThatThrownBy(() -> repo.saveAndFlush(bad))
        .isInstanceOf(ConstraintViolationException.class);
  }
}
