package com.example.backend.fish.app;

import static com.example.backend.fish.api.FishDtos.*;

import com.example.backend.fish.domain.Fish;
import com.example.backend.fish.infra.FishRepository;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class FishService {

  private final FishRepository repo;

  public FishService(FishRepository repo) {
    this.repo = repo;
  }

  public Page<FishResponse> list(Pageable pageable) {
    return repo.findAll(pageable).map(this::toResponse);
  }

  public FishResponse get(UUID id) {
    var entity = repo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fish " + id + " not found"));
    return toResponse(entity);
  }

  public Created create(FishRequest req) {
    var entity = repo.save(new Fish(req.name(), req.species(), req.length(), req.weight()));
    return new Created(entity.getId(), URI.create("/api/fish/" + entity.getId()));
  }

  public FishResponse update(UUID id, FishRequest req) {
    var entity = repo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fish " + id + " not found"));
    entity.setName(req.name());
    entity.setSpecies(req.species());
    entity.setLength(req.length());
    entity.setWeight(req.weight());
    return toResponse(entity);
  }

  public void delete(UUID id) {
    if (!repo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fish " + id + " not found");
    }
    repo.deleteById(id);
  }

  private FishResponse toResponse(Fish f) {
    return new FishResponse(f.getId(), f.getName(), f.getSpecies(), f.getLength(), f.getWeight());
  }

  public record Created(UUID id, URI location) {}
}
