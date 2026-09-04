import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { CreateReservationRequest, Reservation } from '../models/reservation.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReservationApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/reservations`;

  create(request: CreateReservationRequest) {
    return this.http.post<Reservation>(this.baseUrl, request);
  }

  getMine(userId: number) {
    const params = new HttpParams().set('userId', userId);
    return this.http.get<Reservation[]>(`${this.baseUrl}/my`, { params });
  }

  getById(id: number) { return this.http.get<Reservation>(`${this.baseUrl}/${id}`); }

  getByTimeRange(start: string, end: string) {
    const params = new HttpParams().set('start', start).set('end', end);
    return this.http.get<Reservation[]>(this.baseUrl, { params });
  }

  cancel(id: number) {
    return this.http.patch<Reservation>(`${this.baseUrl}/${id}/cancel`, {});
  }
}