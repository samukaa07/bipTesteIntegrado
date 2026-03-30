package com.example.ejb;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficioEjbServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private BeneficioEjbService service;

    private Beneficio from;
    private Beneficio to;

    @BeforeEach
    void setup() {
        from = new Beneficio();
        from.setId(1L);
        from.setNome("Conta Origem");
        from.setValor(new BigDecimal("1000.00"));
        from.setAtivo(true);

        to = new Beneficio();
        to.setId(2L);
        to.setNome("Conta Destino");
        to.setValor(new BigDecimal("500.00"));
        to.setAtivo(true);
    }

    @Test
    @DisplayName("Transferência válida: saldo é movido corretamente entre os benefícios")
    void transferencia_valida_moveSaldo() {
        when(em.find(Beneficio.class, 1L, LockModeType.OPTIMISTIC_FORCE_INCREMENT)).thenReturn(from);
        when(em.find(Beneficio.class, 2L, LockModeType.OPTIMISTIC_FORCE_INCREMENT)).thenReturn(to);
        when(em.merge(any(Beneficio.class))).thenAnswer(inv -> inv.getArgument(0));

        service.transfer(1L, 2L, new BigDecimal("300.00"));

        assertEquals(new BigDecimal("700.00"), from.getValor());
        assertEquals(new BigDecimal("800.00"), to.getValor());
        verify(em).merge(from);
        verify(em).merge(to);
    }

    @Test
    @DisplayName("Transferência bloqueada por saldo insuficiente: lança InsufficientBalanceException")
    void transferencia_saldoInsuficiente_lancaExcecao() {
        when(em.find(Beneficio.class, 1L, LockModeType.OPTIMISTIC_FORCE_INCREMENT)).thenReturn(from);
        when(em.find(Beneficio.class, 2L, LockModeType.OPTIMISTIC_FORCE_INCREMENT)).thenReturn(to);

        assertThrows(InsufficientBalanceException.class,
                () -> service.transfer(1L, 2L, new BigDecimal("1500.00")));

        // Nenhum merge deve ter ocorrido
        verify(em, never()).merge(any());
    }

    @Test
    @DisplayName("Origem inexistente: lança IllegalArgumentException")
    void transferencia_origemInexistente_lancaExcecao() {
        when(em.find(Beneficio.class, 99L, LockModeType.OPTIMISTIC_FORCE_INCREMENT)).thenReturn(null);
        when(em.find(Beneficio.class, 2L,  LockModeType.OPTIMISTIC_FORCE_INCREMENT)).thenReturn(to);

        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(99L, 2L, new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Destino inexistente: lança IllegalArgumentException")
    void transferencia_destinoInexistente_lancaExcecao() {
        when(em.find(Beneficio.class, 1L,  LockModeType.OPTIMISTIC_FORCE_INCREMENT)).thenReturn(from);
        when(em.find(Beneficio.class, 99L, LockModeType.OPTIMISTIC_FORCE_INCREMENT)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, 99L, new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Valor zero ou negativo: lança IllegalArgumentException antes de consultar banco")
    void transferencia_valorInvalido_lancaExcecaoImediata() {
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, 2L, BigDecimal.ZERO));

        // EntityManager não deve ter sido consultado
        verify(em, never()).find(any(), any(), any(LockModeType.class));
    }

    @Test
    @DisplayName("LockModeType.OPTIMISTIC_FORCE_INCREMENT é usado em ambas as consultas")
    void transferencia_usaLockingOtimista() {
        when(em.find(Beneficio.class, 1L, LockModeType.OPTIMISTIC_FORCE_INCREMENT)).thenReturn(from);
        when(em.find(Beneficio.class, 2L, LockModeType.OPTIMISTIC_FORCE_INCREMENT)).thenReturn(to);
        when(em.merge(any())).thenAnswer(inv -> inv.getArgument(0));

        service.transfer(1L, 2L, new BigDecimal("50.00"));

        verify(em).find(Beneficio.class, 1L, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        verify(em).find(Beneficio.class, 2L, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
    }
}
