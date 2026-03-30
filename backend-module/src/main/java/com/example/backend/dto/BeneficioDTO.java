package com.example.backend.dto;

import com.example.backend.model.Beneficio;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class BeneficioDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.00", inclusive = false, message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    private Boolean ativo;
    private Long version;

    public BeneficioDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public static BeneficioDTO from(Beneficio b) {
        BeneficioDTO dto = new BeneficioDTO();
        dto.setId(b.getId());
        dto.setNome(b.getNome());
        dto.setDescricao(b.getDescricao());
        dto.setValor(b.getValor());
        dto.setAtivo(b.getAtivo());
        dto.setVersion(b.getVersion());
        return dto;
    }

    public void applyTo(Beneficio b) {
        b.setNome(this.nome);
        b.setDescricao(this.descricao);
        b.setValor(this.valor);
        if (this.ativo != null) b.setAtivo(this.ativo);
    }
}
