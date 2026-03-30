import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BeneficioService } from './beneficio.service';
import { Beneficio, TransferRequest } from '../models/beneficio.model';

describe('BeneficioService', () => {
  let service: BeneficioService;
  let http: HttpTestingController;

  const base = '/api/v1/beneficios';

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(BeneficioService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('findAll faz GET e retorna lista', () => {
    const mock: Beneficio[] = [{ id: 1, nome: 'A', valor: 100 }];
    service.findAll().subscribe(list => expect(list.length).toBe(1));
    http.expectOne(base).flush(mock);
  });

  it('findById faz GET /{id}', () => {
    const mock: Beneficio = { id: 1, nome: 'A', valor: 100 };
    service.findById(1).subscribe(b => expect(b.nome).toBe('A'));
    http.expectOne(`${base}/1`).flush(mock);
  });

  it('create faz POST e retorna benefício criado', () => {
    const payload: Beneficio = { nome: 'Novo', valor: 200 };
    const resp: Beneficio = { ...payload, id: 3 };
    service.create(payload).subscribe(b => expect(b.id).toBe(3));
    const req = http.expectOne(base);
    expect(req.request.method).toBe('POST');
    req.flush(resp);
  });

  it('update faz PUT /{id}', () => {
    const payload: Beneficio = { nome: 'Upd', valor: 300 };
    service.update(1, payload).subscribe(b => expect(b.nome).toBe('Upd'));
    const req = http.expectOne(`${base}/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ ...payload, id: 1 });
  });

  it('delete faz DELETE /{id}', () => {
    service.delete(1).subscribe();
    const req = http.expectOne(`${base}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('transfer faz POST /transfer', () => {
    const t: TransferRequest = { fromId: 1, toId: 2, amount: 50 };
    service.transfer(t).subscribe();
    const req = http.expectOne(`${base}/transfer`);
    expect(req.request.method).toBe('POST');
    req.flush(null);
  });

  it('handleError retorna mensagem legível em caso de erro HTTP', () => {
    service.findAll().subscribe({
      error: (e: Error) => expect(e.message).toContain('Não encontrado')
    });
    http.expectOne(base).flush(
      { message: 'Não encontrado' },
      { status: 404, statusText: 'Not Found' }
    );
  });
});
