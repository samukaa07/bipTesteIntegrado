import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio.model';

@Component({
  selector: 'app-beneficio-list',
  templateUrl: './beneficio-list.component.html'
})
export class BeneficioListComponent implements OnInit {
  beneficios: Beneficio[] = [];
  erro = '';
  sucesso = '';
  carregando = false;

  constructor(
    private service: BeneficioService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando = true;
    this.service.findAll().subscribe({
      next: data => { this.beneficios = data; this.carregando = false; },
      error: (e: Error) => { this.erro = e.message; this.carregando = false; }
    });
  }

  editar(id: number): void {
    this.router.navigate(['/beneficios', id, 'editar']);
  }

  excluir(b: Beneficio): void {
    if (!confirm(`Excluir "${b.nome}"?`)) return;
    this.service.delete(b.id!).subscribe({
      next: () => {
        this.sucesso = `"${b.nome}" excluído com sucesso.`;
        this.carregar();
      },
      error: (e: Error) => { this.erro = e.message; }
    });
  }

  novo(): void {
    this.router.navigate(['/beneficios/novo']);
  }
}
