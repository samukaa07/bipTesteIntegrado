package com.example.backend.service;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.exception.InsufficientBalanceException;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class BeneficioService {

    private final BeneficioRepository repository;

    public BeneficioService(BeneficioRepository repository) {
        this.repository = repository;
    }

    public List<BeneficioDTO> findAll() {
        return repository.findAll().stream().map(BeneficioDTO::from).toList();
    }

    public BeneficioDTO findById(Long id) {
        return BeneficioDTO.from(getOrThrow(id));
    }

    public BeneficioDTO create(BeneficioDTO dto) {
        Beneficio b = new Beneficio();
        dto.applyTo(b);
        if (b.getAtivo() == null) b.setAtivo(true);
        return BeneficioDTO.from(repository.save(b));
    }

    public BeneficioDTO update(Long id, BeneficioDTO dto) {
        Beneficio b = getOrThrow(id);
        dto.applyTo(b);
        return BeneficioDTO.from(repository.save(b));
    }

    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("O valor da transferência deve ser positivo.");

        Beneficio from = getOrThrow(fromId);
        Beneficio to   = getOrThrow(toId);

        if (from.getValor().compareTo(amount) < 0)
            throw new InsufficientBalanceException(fromId);

        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));

        repository.save(from);
        repository.save(to);
    }

    private Beneficio getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Beneficio não encontrado (id=" + id + ")"));
    }
}
