import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { TransferComponent } from './transfer.component';
import { BeneficioService } from '../../services/beneficio.service';

describe('TransferComponent', () => {
  let fixture: ComponentFixture<TransferComponent>;
  let component: TransferComponent;
  let serviceSpy: jasmine.SpyObj<BeneficioService>;

  beforeEach(async () => {
    serviceSpy = jasmine.createSpyObj('BeneficioService', ['transfer']);

    await TestBed.configureTestingModule({
      declarations: [TransferComponent],
      imports: [ReactiveFormsModule, RouterTestingModule],
      providers: [{ provide: BeneficioService, useValue: serviceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(TransferComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('formulário inválido quando vazio', () => {
    expect(component.form.invalid).toBeTrue();
  });

  it('transferir chama service com dados corretos', () => {
    serviceSpy.transfer.and.returnValue(of(void 0));
    component.form.setValue({ fromId: 1, toId: 2, amount: 100 });
    component.transferir();
    expect(serviceSpy.transfer).toHaveBeenCalledWith({ fromId: 1, toId: 2, amount: 100 });
    expect(component.sucesso).toContain('sucesso');
  });

  it('exibe erro quando transfer falha (ex: saldo insuficiente)', () => {
    serviceSpy.transfer.and.returnValue(throwError(() => new Error('Saldo insuficiente')));
    component.form.setValue({ fromId: 1, toId: 2, amount: 99999 });
    component.transferir();
    expect(component.erro).toBe('Saldo insuficiente');
  });

  it('amount negativo invalida o formulário', () => {
    component.form.setValue({ fromId: 1, toId: 2, amount: -10 });
    expect(component.form.invalid).toBeTrue();
  });
});
