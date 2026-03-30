package com.example.ejb;

/**
 * Lançada quando a conta de origem não possui saldo suficiente para a transferência.
 * Por ser uma RuntimeException, garante que a transação CMT seja marcada para rollback
 * automaticamente pelo container Jakarta EE.
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(Long fromId) {
        super("Saldo insuficiente na conta de origem (id=" + fromId + ")");
    }
}
