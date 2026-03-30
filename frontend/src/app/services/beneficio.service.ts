import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Beneficio, TransferRequest } from '../models/beneficio.model';

@Injectable({ providedIn: 'root' })
export class BeneficioService {

  private readonly url = `${environment.apiUrl}/beneficios`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.url).pipe(catchError(this.handleError));
  }

  findById(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.url}/${id}`).pipe(catchError(this.handleError));
  }

  create(b: Beneficio): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.url, b).pipe(catchError(this.handleError));
  }

  update(id: number, b: Beneficio): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.url}/${id}`, b).pipe(catchError(this.handleError));
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`).pipe(catchError(this.handleError));
  }

  transfer(req: TransferRequest): Observable<void> {
    return this.http.post<void>(`${this.url}/transfer`, req).pipe(catchError(this.handleError));
  }

  private handleError(err: HttpErrorResponse): Observable<never> {
    const msg = err.error?.message ?? err.message ?? 'Erro desconhecido';
    return throwError(() => new Error(msg));
  }
}
