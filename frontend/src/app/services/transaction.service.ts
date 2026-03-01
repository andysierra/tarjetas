import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, forkJoin, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { ApiResponse } from '../models/api-response.model';
import { TransactionResponse, CreateTransactionRequest } from '../models/transaction.model';

@Injectable({ providedIn: 'root' })
export class TransactionService {

  private readonly apiUrl = 'http://localhost:8080/api/v1/transactions';
  private readonly storageKey = 'txRefs';

  private transactionsSubject = new BehaviorSubject<TransactionResponse[]>([]);
  transactions$ = this.transactionsSubject.asObservable();

  constructor(private http: HttpClient) {}

  private getStoredRefs(): string[] {
    return JSON.parse(localStorage.getItem(this.storageKey) || '[]');
  }

  private saveRef(ref: string): void {
    const refs = this.getStoredRefs();
    if (!refs.includes(ref)) {
      refs.push(ref);
      localStorage.setItem(this.storageKey, JSON.stringify(refs));
    }
  }

  create(request: CreateTransactionRequest): Observable<ApiResponse<TransactionResponse>> {
    return this.http.post<ApiResponse<TransactionResponse>>(this.apiUrl, request).pipe(
      tap(res => {
        if (res.code.startsWith('2')) {
          this.saveRef(res.data.reference);
        }
      })
    );
  }

  getByReference(reference: string): Observable<ApiResponse<TransactionResponse>> {
    return this.http.get<ApiResponse<TransactionResponse>>(`${this.apiUrl}/${reference}`);
  }

  cancel(reference: string): Observable<ApiResponse<TransactionResponse>> {
    return this.http.put<ApiResponse<TransactionResponse>>(`${this.apiUrl}/${reference}/cancel`, {});
  }

  loadAll(): void {
    const refs = this.getStoredRefs();
    if (refs.length === 0) {
      this.transactionsSubject.next([]);
      return;
    }

    forkJoin(
      refs.map(ref =>
        this.getByReference(ref).pipe(
          map(res => res.data),
          catchError(() => of(null))
        )
      )
    ).subscribe(txs => {
      this.transactionsSubject.next(txs.filter((t): t is TransactionResponse => t !== null));
    });
  }

  refresh(): void {
    this.loadAll();
  }
}
