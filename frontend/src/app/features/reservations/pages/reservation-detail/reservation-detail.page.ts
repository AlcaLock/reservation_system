import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Reservation } from '../../../../core/models/reservation.model';
import { ReservationApiService } from '../../../../core/services/reservation-api.service';

@Component({ selector: 'app-reservation-detail-page', imports: [DatePipe, RouterLink], template: `<section class="detail"><a routerLink="/app/reservations">← Volver a mis reservas</a>@if (reservation(); as item) {<p class="eyebrow">RESERVA #{{ item.id }}</p><h1>{{ item.purpose }}</h1><dl><dt>Inicio</dt><dd>{{ item.startTime | date:'medium' }}</dd><dt>Fin</dt><dd>{{ item.endTime | date:'medium' }}</dd><dt>Estado</dt><dd>{{ item.status }}</dd><dt>Recurso</dt><dd>#{{ item.resourceId }}</dd></dl>} @else if (error()) {<p role="alert">{{ error() }}</p>} @else {<p role="status">Cargando reserva...</p>}</section>`, styles: [`.detail{width:min(760px,90vw);margin:0 auto;padding:54px 0;color:var(--ink)}a{color:var(--primary);font-weight:700}.eyebrow{margin-top:36px;color:var(--primary);font-size:.75rem;font-weight:800;letter-spacing:.1em}h1{font-size:clamp(2rem,5vw,3.5rem)}dl{display:grid;grid-template-columns:max-content 1fr;gap:14px;margin-top:30px;padding:24px;border:1px solid var(--line);border-radius:8px;background:var(--surface)}dt{color:var(--muted);font-weight:700}dd{margin:0}`] })
export class ReservationDetailPage {
  private readonly route = inject(ActivatedRoute); private readonly api = inject(ReservationApiService);
  protected readonly reservation = signal<Reservation | null>(null); protected readonly error = signal<string | null>(null);
  constructor() { this.api.getById(Number(this.route.snapshot.paramMap.get('id'))).subscribe({ next: (item) => this.reservation.set(item), error: () => this.error.set('No se encontró la reserva.') }); }
}
