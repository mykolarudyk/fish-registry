package com.example.backend.fish.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.backend.fish.api.FishDtos.FishRequest;
import com.example.backend.fish.domain.Fish;
import com.example.backend.fish.infra.FishRepository;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class FishServiceTest {

  @Mock FishRepository repo;
  @InjectMocks FishService svc;

  @Test
  void listMapsEntitiesToResponses() {
    var e = new Fish("A", "B", 12, bd("1.23"));
    var page = new PageImpl<>(java.util.List.of(e), PageRequest.of(0, 20), 1);
    when(repo.findAll(any(Pageable.class))).thenReturn(page);

    var res = svc.list(PageRequest.of(0, 20));
    assertThat(res.getTotalElements()).isEqualTo(1);
    assertThat(res.getContent()).hasSize(1);
    assertThat(res.getContent().get(0).name()).isEqualTo("A");
  }

  @Test
  void getReturnsMappedResponse() {
    var e = new Fish("A", "B", 12, bd("1.23"));
    var id = UUID.randomUUID();
    e.setId(id);
    when(repo.findById(id)).thenReturn(Optional.of(e));

    var res = svc.get(id);
    assertThat(res.id()).isEqualTo(id);
    assertThat(res.length()).isEqualTo(12);
    assertThat(res.weight()).isEqualByComparingTo("1.23");
  }

  @Test
  void getThrows404WhenMissing() {
    var id = UUID.randomUUID();
    when(repo.findById(id)).thenReturn(Optional.empty());

    var ex = assertThrows(ResponseStatusException.class, () -> svc.get(id));
    assertThat(ex.getStatusCode().value()).isEqualTo(404);
  }

  @Test
  void createSavesAndReturnsLocation() {
    var req = new FishRequest("X", "Y", 5, bd("0.50"));
    var saved = new Fish(req.name(), req.species(), req.length(), req.weight());
    saved.setId(UUID.randomUUID());
    when(repo.save(any(Fish.class))).thenReturn(saved);

    var created = svc.create(req);
    assertThat(created.id()).isEqualTo(saved.getId());
    assertThat(created.location()).isEqualTo(URI.create("/api/fish/" + saved.getId()));
  }

  @Test
  void updateRewritesFields() {
    var id = UUID.randomUUID();
    var existing = new Fish("A", "B", 10, bd("1.00"));
    existing.setId(id);
    when(repo.findById(id)).thenReturn(Optional.of(existing));

    var req = new FishRequest("A2", "B2", 20, bd("2.00"));
    var res = svc.update(id, req);

    assertThat(res.name()).isEqualTo("A2");
    assertThat(res.species()).isEqualTo("B2");
    assertThat(res.length()).isEqualTo(20);
    assertThat(res.weight()).isEqualByComparingTo("2.00");
  }

  @Test
  void updateThrows404WhenMissing() {
    var id = UUID.randomUUID();
    when(repo.findById(id)).thenReturn(Optional.empty());

    var req = new FishRequest("A", "B", 10, bd("1.00"));
    var ex = assertThrows(ResponseStatusException.class, () -> svc.update(id, req));
    assertThat(ex.getStatusCode().value()).isEqualTo(404);
  }

  @Test
  void deleteWhenExistsDeletes() {
    var id = UUID.randomUUID();
    when(repo.existsById(id)).thenReturn(true);

    svc.delete(id);
    verify(repo).deleteById(id);
  }

  @Test
  void deleteThrows404WhenMissing() {
    var id = UUID.randomUUID();
    when(repo.existsById(id)).thenReturn(false);

    var ex = assertThrows(ResponseStatusException.class, () -> svc.delete(id));
    assertThat(ex.getStatusCode().value()).isEqualTo(404);
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
