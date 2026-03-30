import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BeneficioListComponent } from './components/beneficio-list/beneficio-list.component';
import { BeneficioFormComponent } from './components/beneficio-form/beneficio-form.component';
import { TransferComponent } from './components/transfer/transfer.component';

const routes: Routes = [
  { path: '',              redirectTo: 'beneficios', pathMatch: 'full' },
  { path: 'beneficios',   component: BeneficioListComponent },
  { path: 'beneficios/novo',        component: BeneficioFormComponent },
  { path: 'beneficios/:id/editar',  component: BeneficioFormComponent },
  { path: 'transferencia',          component: TransferComponent },
  { path: '**',            redirectTo: 'beneficios' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
