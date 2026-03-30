import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { BeneficioService } from '../../services/beneficio.service';

@Component({
  selector: 'app-transfer',
  templateUrl: './transfer.component.html'
})
export class TransferComponent {
  form: FormGroup;
  erro = '';
  sucesso = '';
  enviando = false;
  amountFormatado = '';

  constructor(
    private fb: FormBuilder,
    private service: BeneficioService,
    private router: Router
  ) {
    this.form = this.fb.group({
      fromId: [null, [Validators.required, Validators.min(1)]],
      toId:   [null, [Validators.required, Validators.min(1)]],
      amount: [null, [Validators.required, Validators.min(0.01)]]
    });
  }

  onAmountInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const apenasNumeros = input.value.replace(/\D/g, '');
    const numero = parseInt(apenasNumeros || '0', 10) / 100;
    this.amountFormatado = this.formatarMoeda(numero);
    this.form.get('amount')!.setValue(numero > 0 ? numero : null);
  }

  private formatarMoeda(valor: number): string {
    if (!valor || isNaN(valor)) return '';
    return valor.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  transferir(): void {
    if (this.form.invalid) return;
    this.enviando = true;
    this.erro = '';
    this.sucesso = '';

    this.service.transfer(this.form.value).subscribe({
      next: () => {
        this.sucesso = 'Transferência realizada com sucesso!';
        this.form.reset();
        this.amountFormatado = '';
        this.enviando = false;
      },
      error: (e: Error) => {
        this.erro = e.message;
        this.enviando = false;
      }
    });
  }

  voltar(): void {
    this.router.navigate(['/beneficios']);
  }
}
