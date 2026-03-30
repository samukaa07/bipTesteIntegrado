package com.example.backend.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(Long fromId) {
        super("Saldo insuficiente na conta de origem (id=" + fromId + ")");
    }
}
