import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BeneficioService } from '../../services/beneficio.service';

@Component({
  selector: 'app-beneficio-form',
  templateUrl: './beneficio-form.component.html'
})
export class BeneficioFormComponent implements OnInit {
  form!: FormGroup;
  editandoId: number | null = null;
  titulo = 'Novo Benefício';
  erro = '';
  sucesso = '';
  salvando = false;
  valorFormatado = '';

  constructor(
    private fb: FormBuilder,
    private service: BeneficioService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nome:     ['', [Validators.required, Validators.maxLength(100)]],
      descricao:['', Validators.maxLength(255)],
      valor:    [null, [Validators.required, Validators.min(0.01)]],
      ativo:    [true]
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editandoId = +id;
      this.titulo = 'Editar Benefício';
      this.service.findById(this.editandoId).subscribe({
        next: b => {
          this.form.patchValue(b);
          this.valorFormatado = this.formatarMoeda(String(b.valor));
        },
        error: (e: Error) => { this.erro = e.message; }
      });
    }
  }

  onValorInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const apenasNumeros = input.value.replace(/\D/g, '');
    const numero = parseInt(apenasNumeros || '0', 10) / 100;
    this.valorFormatado = this.formatarMoeda(String(numero));
    this.form.get('valor')!.setValue(numero > 0 ? numero : null);
  }

  private formatarMoeda(valor: string): string {
    const numero = parseFloat(valor);
    if (isNaN(numero)) return '';
    return numero.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  salvar(): void {
    if (this.form.invalid) return;
    this.salvando = true;
    const dados = this.form.value;
    const req = this.editandoId
      ? this.service.update(this.editandoId, dados)
      : this.service.create(dados);

    req.subscribe({
      next: () => this.router.navigate(['/beneficios']),
      error: (e: Error) => { this.erro = e.message; this.salvando = false; }
    });
  }

  cancelar(): void {
    this.router.navigate(['/beneficios']);
  }
}
