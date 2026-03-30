package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

/**
 * Serviço EJB responsável por operações de negócio envolvendo Beneficio.
 *
 * Correção aplicada em transfer():
 *  - Validação de existência dos registros antes de qualquer mutação.
 *  - Verificação de saldo antes de subtrair, lançando InsufficientBalanceException
 *    (RuntimeException) para forçar rollback automático pelo container (CMT).
 *  - Uso de LockModeType.OPTIMISTIC_FORCE_INCREMENT em ambos os registros para que
 *    a versão seja incrementada no commit, detectando qualquer escrita concorrente
 *    (lost-update) e lançando OptimisticLockException automaticamente pelo JPA.
 */
@Stateless
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser positivo.");
        }

        // Busca com locking otimista: ao commitar, o JPA valida que a versão não
        // foi alterada por outra transação concorrente e incrementa a coluna VERSION.
        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        Beneficio to   = em.find(Beneficio.class, toId,   LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        if (from == null) {
            throw new IllegalArgumentException("Beneficio de origem não encontrado (id=" + fromId + ")");
        }
        if (to == null) {
            throw new IllegalArgumentException("Beneficio de destino não encontrado (id=" + toId + ")");
        }

        // Verificação de saldo — impede que o valor fique negativo
        if (from.getValor().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(fromId);
        }

        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));

        // O merge não é estritamente necessário para entidades managed,
        // mas é mantido para clareza e compatibilidade com detached state.
        em.merge(from);
        em.merge(to);
    }
}
