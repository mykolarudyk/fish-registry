package com.example.backend.fish.app;

import static com.example.backend.fish.api.FishDtos.*;

import com.example.backend.fish.domain.Fish;
import com.example.backend.fish.infra.FishRepository;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    return toResponse(repo.findById(id).orElseThrow(() ->
        new NoSuchElementException("Fish " + id + " not found")));
  }

  public Created create(FishRequest req) {
    var entity = repo.save(new Fish(req.name(), req.species(), req.lengthCm(), req.weightKg()));
    return new Created(entity.getId(), URI.create("/api/fish/" + entity.getId()));
  }

  public FishResponse update(UUID id, FishRequest req) {
    var entity = repo.findById(id).orElseThrow(() ->
        new NoSuchElementException("Fish " + id + " not found"));
    entity.setName(req.name());
    entity.setSpecies(req.species());
    entity.setLengthCm(req.lengthCm());
    entity.setWeightKg(req.weightKg());
    return toResponse(entity);
  }

  public void delete(UUID id) {
    if (!repo.existsById(id)) throw new NoSuchElementException("Fish " + id + " not found");
    repo.deleteById(id);
  }

  private FishResponse toResponse(Fish f) {
    return new FishResponse(f.getId(), f.getName(), f.getSpecies(), f.getLengthCm(), f.getWeightKg());
  }

  public record Created(UUID id, URI location) {}
}
