package com.example.backend.controller;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BeneficioControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/beneficios retorna lista com seed")
    void list_retornaLista() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("GET /{id} existente retorna 200")
    void getById_existente() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("GET /{id} inexistente retorna 404")
    void getById_inexistente() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST / cria e retorna 201")
    void create_retorna201() throws Exception {
        BeneficioDTO dto = new BeneficioDTO();
        dto.setNome("Novo Beneficio");
        dto.setValor(new BigDecimal("250.00"));

        mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Novo Beneficio")));
    }

    @Test
    @DisplayName("POST / sem nome retorna 400")
    void create_semNome_retorna400() throws Exception {
        BeneficioDTO dto = new BeneficioDTO();
        dto.setNome("");
        dto.setValor(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /{id} atualiza e retorna 200")
    void update_retorna200() throws Exception {
        BeneficioDTO dto = new BeneficioDTO();
        dto.setNome("Atualizado");
        dto.setValor(new BigDecimal("1200.00"));

        mockMvc.perform(put("/api/v1/beneficios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Atualizado")));
    }

    @Test
    @DisplayName("DELETE /{id} retorna 204")
    void delete_retorna204() throws Exception {
        mockMvc.perform(delete("/api/v1/beneficios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /transfer válida retorna 204")
    void transfer_valida_retorna204() throws Exception {
        TransferRequestDTO dto = new TransferRequestDTO();
        dto.setFromId(1L);
        dto.setToId(2L);
        dto.setAmount(new BigDecimal("404.00"));

        mockMvc.perform(post("/api/v1/beneficios/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /transfer saldo insuficiente retorna 422")
    void transfer_saldoInsuficiente_retorna422() throws Exception {
        TransferRequestDTO dto = new TransferRequestDTO();
        dto.setFromId(1L);
        dto.setToId(2L);
        dto.setAmount(new BigDecimal("99999.00"));

        mockMvc.perform(post("/api/v1/beneficios/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }
}
