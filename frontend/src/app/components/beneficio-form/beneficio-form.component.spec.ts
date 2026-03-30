import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { BeneficioFormComponent } from './beneficio-form.component';
import { BeneficioService } from '../../services/beneficio.service';

describe('BeneficioFormComponent', () => {
  let fixture: ComponentFixture<BeneficioFormComponent>;
  let component: BeneficioFormComponent;
  let serviceSpy: jasmine.SpyObj<BeneficioService>;

  beforeEach(async () => {
    serviceSpy = jasmine.createSpyObj('BeneficioService', ['create', 'update', 'findById']);

    await TestBed.configureTestingModule({
      declarations: [BeneficioFormComponent],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [{ provide: BeneficioService, useValue: serviceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(BeneficioFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('formulário inválido sem nome e valor', () => {
    expect(component.form.invalid).toBeTrue();
  });

  it('formulário válido com nome e valor preenchidos', () => {
    component.form.setValue({ nome: 'Teste', descricao: '', valor: 100, ativo: true });
    expect(component.form.valid).toBeTrue();
  });

  it('salvar chama create quando não há id de edição', () => {
    serviceSpy.create.and.returnValue(of({ id: 1, nome: 'Teste', valor: 100 }));
    component.form.setValue({ nome: 'Teste', descricao: '', valor: 100, ativo: true });
    component.salvar();
    expect(serviceSpy.create).toHaveBeenCalled();
  });

  it('exibe erro quando create falha', () => {
    serviceSpy.create.and.returnValue(throwError(() => new Error('Erro ao salvar')));
    component.form.setValue({ nome: 'Teste', descricao: '', valor: 100, ativo: true });
    component.salvar();
    expect(component.erro).toBe('Erro ao salvar');
  });
});
