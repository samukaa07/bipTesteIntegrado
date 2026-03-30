import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <nav>
      <span class="brand">💼 Benefícios</span>
      <a routerLink="/beneficios" routerLinkActive="active">Lista</a>
      <a routerLink="/beneficios/novo" routerLinkActive="active">Novo</a>
      <a routerLink="/transferencia" routerLinkActive="active">Transferência</a>
    </nav>
    <router-outlet></router-outlet>
  `
})
export class AppComponent {}
