package com.example.backend.controller;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferRequestDTO;
import com.example.backend.service.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficios")
@Tag(name = "Benefícios", description = "CRUD e transferência entre benefícios")
public class BeneficioController {

    private final BeneficioService service;

    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os benefícios")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<BeneficioDTO>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar benefício por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Benefício encontrado"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    public ResponseEntity<BeneficioDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo benefício")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<BeneficioDTO> create(@Valid @RequestBody BeneficioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflito de versão")
    })
    public ResponseEntity<BeneficioDTO> update(@PathVariable Long id,
                                               @Valid @RequestBody BeneficioDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover benefício")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transferir valor entre dois benefícios",
               description = "Valida saldo e usa locking otimista para evitar inconsistências.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Transferência realizada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflito de versão"),
        @ApiResponse(responseCode = "422", description = "Saldo insuficiente")
    })
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequestDTO dto) {
        service.transfer(dto.getFromId(), dto.getToId(), dto.getAmount());
        return ResponseEntity.noContent().build();
    }
}
