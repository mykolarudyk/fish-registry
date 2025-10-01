package com.example.backend.fish.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.backend.fish.api.FishDtos.FishRequest;
import com.example.backend.fish.api.FishDtos.FishResponse;
import com.example.backend.fish.app.FishService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(FishController.class)
class FishControllerTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  @MockitoBean
  private FishService svc;

  @Test
  void listReturnsPage() throws Exception {
    var resp = new FishResponse(UUID.randomUUID(), "A", "B", 10, bd("1.00"));
    var page = new PageImpl<>(List.of(resp), PageRequest.of(0, 20), 1);
    when(svc.list(any(Pageable.class))).thenReturn(page);

    mvc.perform(get("/api/fish"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("A"))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void getByIdReturnsEntity() throws Exception {
    var id = UUID.randomUUID();
    var resp = new FishResponse(id, "A", "B", 10, bd("1.00"));
    when(svc.get(id)).thenReturn(resp);

    mvc.perform(get("/api/fish/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.length").value(10));
  }

  @Test
  void getMissingReturns404() throws Exception {
    var id = UUID.randomUUID();
    when(svc.get(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Fish " + id + " not found"));

    mvc.perform(get("/api/fish/{id}", id))
        .andExpect(status().isNotFound());
  }

  @Test
  void createReturns201WithLocationAndBody() throws Exception {
    var id = UUID.randomUUID();
    var req = new FishRequest("A", "B", 10, bd("1.00"));
    var resp = new FishResponse(id, "A", "B", 10, bd("1.00"));

    when(svc.create(any(FishRequest.class))).thenReturn(new com.example.backend.fish.app.FishService.Created(id, URI.create("/api/fish/" + id)));
    when(svc.get(id)).thenReturn(resp);

    mvc.perform(post("/api/fish")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/fish/" + id))
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("A"));
  }

  @Test
  void validationErrorReturns400() throws Exception {
    var badJson = """
      {"name":"","species":"","length":0,"weight":0}
      """;
    mvc.perform(post("/api/fish")
            .contentType(MediaType.APPLICATION_JSON)
            .content(badJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateReturnsUpdatedBody() throws Exception {
    var id = UUID.randomUUID();
    var req = new FishRequest("A2", "B2", 20, bd("2.00"));
    var resp = new FishResponse(id, "A2", "B2", 20, bd("2.00"));

    when(svc.update(eq(id), any(FishRequest.class))).thenReturn(resp);

    mvc.perform(put("/api/fish/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("A2"))
        .andExpect(jsonPath("$.length").value(20))
        .andExpect(jsonPath("$.weight").value(2.0));
  }

  @Test
  void deleteReturns204() throws Exception {
    var id = UUID.randomUUID();
    mvc.perform(delete("/api/fish/{id}", id))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteMissingReturns404() throws Exception {
    var id = UUID.randomUUID();
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Fish " + id + " not found"))
        .when(svc).delete(id);

    mvc.perform(delete("/api/fish/{id}", id))
        .andExpect(status().isNotFound());
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
