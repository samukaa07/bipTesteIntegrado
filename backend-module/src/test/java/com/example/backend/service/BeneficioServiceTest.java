package com.example.backend.service;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.exception.InsufficientBalanceException;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

    @Mock
    private BeneficioRepository repository;

    @InjectMocks
    private BeneficioService service;

    private Beneficio beneficioA;
    private Beneficio beneficioB;

    @BeforeEach
    void setup() {
        beneficioA = new Beneficio();
        beneficioA.setId(1L);
        beneficioA.setNome("Beneficio A");
        beneficioA.setValor(new BigDecimal("1000.00"));
        beneficioA.setAtivo(true);

        beneficioB = new Beneficio();
        beneficioB.setId(2L);
        beneficioB.setNome("Beneficio B");
        beneficioB.setValor(new BigDecimal("500.00"));
        beneficioB.setAtivo(true);
    }

    @Test
    @DisplayName("findAll retorna lista de DTOs")
    void findAll_retornaLista() {
        when(repository.findAll()).thenReturn(List.of(beneficioA, beneficioB));
        assertEquals(2, service.findAll().size());
    }

    @Test
    @DisplayName("findById existente retorna DTO")
    void findById_existente() {
        when(repository.findById(1L)).thenReturn(Optional.of(beneficioA));
        BeneficioDTO dto = service.findById(1L);
        assertEquals(1L, dto.getId());
    }

    @Test
    @DisplayName("findById inexistente lança EntityNotFoundException")
    void findById_inexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    @DisplayName("create persiste e retorna DTO")
    void create_persiste() {
        BeneficioDTO dto = new BeneficioDTO();
        dto.setNome("Novo");
        dto.setValor(new BigDecimal("200.00"));

        when(repository.save(any())).thenAnswer(inv -> {
            Beneficio b = inv.getArgument(0);
            b.setId(3L);
            return b;
        });

        assertEquals(3L, service.create(dto).getId());
    }

    @Test
    @DisplayName("update modifica campos")
    void update_modificaCampos() {
        when(repository.findById(1L)).thenReturn(Optional.of(beneficioA));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BeneficioDTO dto = new BeneficioDTO();
        dto.setNome("Atualizado");
        dto.setValor(new BigDecimal("999.00"));

        assertEquals("Atualizado", service.update(1L, dto).getNome());
    }

    @Test
    @DisplayName("delete remove entidade")
    void delete_remove() {
        when(repository.findById(1L)).thenReturn(Optional.of(beneficioA));
        service.delete(1L);
        verify(repository).delete(beneficioA);
    }

    @Test
    @DisplayName("transfer válida move saldo corretamente")
    void transfer_valida() {
        when(repository.findById(1L)).thenReturn(Optional.of(beneficioA));
        when(repository.findById(2L)).thenReturn(Optional.of(beneficioB));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.transfer(1L, 2L, new BigDecimal("300.00"));

        assertEquals(new BigDecimal("700.00"), beneficioA.getValor());
        assertEquals(new BigDecimal("800.00"), beneficioB.getValor());
    }

    @Test
    @DisplayName("transfer com saldo insuficiente lança exceção")
    void transfer_saldoInsuficiente() {
        when(repository.findById(1L)).thenReturn(Optional.of(beneficioA));
        when(repository.findById(2L)).thenReturn(Optional.of(beneficioB));
        assertThrows(InsufficientBalanceException.class,
                () -> service.transfer(1L, 2L, new BigDecimal("9999.00")));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("transfer com amount zero lança exceção antes de consultar banco")
    void transfer_amountInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, 2L, BigDecimal.ZERO));
        verify(repository, never()).findById(any());
    }
}
