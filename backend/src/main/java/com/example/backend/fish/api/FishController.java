package com.example.backend.fish.api;

import static com.example.backend.fish.api.FishDtos.*;

import com.example.backend.fish.app.FishService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fish")
@Validated
public class FishController {

  private final FishService svc;

  public FishController(FishService svc) {
    this.svc = svc;
  }

  @GetMapping
  public Page<FishResponse> list(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
    return svc.list(pageable);
  }

  @GetMapping("{id}")
  public FishResponse get(@PathVariable UUID id) {
    return svc.get(id);
  }

  @PostMapping
  public ResponseEntity<FishResponse> create(@RequestBody @Valid FishRequest req) {
    var created = svc.create(req);
    return ResponseEntity.created(created.location()).body(svc.get(created.id()));
  }

  @PutMapping("{id}")
  public FishResponse update(@PathVariable UUID id, @RequestBody @Valid FishRequest req) {
    return svc.update(id, req);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    svc.delete(id);
    return ResponseEntity.noContent().build();
  }
}
