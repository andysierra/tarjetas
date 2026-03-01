import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, forkJoin, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { ApiResponse } from '../models/api-response.model';
import { CardResponse, CreateCardRequest, EnrollCardRequest } from '../models/card.model';

@Injectable({ providedIn: 'root' })
export class CardService {

  private readonly apiUrl = 'http://localhost:8080/api/v1/cards';
  private readonly storageKey = 'cardIds';

  private cardsSubject = new BehaviorSubject<CardResponse[]>([]);
  cards$ = this.cardsSubject.asObservable();

  constructor(private http: HttpClient) {}

  private getStoredIds(): string[] {
    return JSON.parse(localStorage.getItem(this.storageKey) || '[]');
  }

  private saveId(id: string): void {
    const ids = this.getStoredIds();
    if (!ids.includes(id)) {
      ids.push(id);
      localStorage.setItem(this.storageKey, JSON.stringify(ids));
    }
  }

  private removeId(id: string): void {
    const ids = this.getStoredIds().filter(i => i !== id);
    localStorage.setItem(this.storageKey, JSON.stringify(ids));
  }

  create(request: CreateCardRequest): Observable<ApiResponse<CardResponse>> {
    return this.http.post<ApiResponse<CardResponse>>(this.apiUrl, request).pipe(
      tap(res => {
        if (res.code.startsWith('2')) {
          this.saveId(res.data.identifier);
        }
      })
    );
  }

  enroll(identifier: string, request: EnrollCardRequest): Observable<ApiResponse<CardResponse>> {
    return this.http.put<ApiResponse<CardResponse>>(`${this.apiUrl}/${identifier}/enroll`, request);
  }

  getByIdentifier(identifier: string): Observable<ApiResponse<CardResponse>> {
    return this.http.get<ApiResponse<CardResponse>>(`${this.apiUrl}/${identifier}`);
  }

  delete(identifier: string): Observable<ApiResponse<CardResponse>> {
    return this.http.delete<ApiResponse<CardResponse>>(`${this.apiUrl}/${identifier}`).pipe(
      tap(res => {
        if (res.code.startsWith('2')) {
          this.removeId(identifier);
        }
      })
    );
  }

  loadAll(): void {
    const ids = this.getStoredIds();
    if (ids.length === 0) {
      this.cardsSubject.next([]);
      return;
    }

    forkJoin(
      ids.map(id =>
        this.getByIdentifier(id).pipe(
          map(res => ({ ...res.data, identifier: id })),
          catchError(() => of(null))
        )
      )
    ).subscribe(cards => {
      this.cardsSubject.next(cards.filter((c): c is CardResponse => c !== null));
    });
  }

  refresh(): void {
    this.loadAll();
  }
}
