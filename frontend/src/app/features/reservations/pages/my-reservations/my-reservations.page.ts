import { Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { LucideCircleX } from '@lucide/angular';

import { Resource } from '../../../../core/models/resource.model';
import { Reservation } from '../../../../core/models/reservation.model';
import { ReservationApiService } from '../../../../core/services/reservation-api.service';
import { ResourceApiService } from '../../../../core/services/resource-api.service';
import { SessionService } from '../../../../core/services/session.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-my-reservations-page',
  imports: [RouterLink, LucideCircleX],
  templateUrl: './my-reservations.page.html',
  styleUrl: './my-reservations.page.scss',
})
export class MyReservationsPage {
  protected readonly resources = signal<Resource[]>([]);
  protected readonly reservations = signal<Reservation[]>([]);
  protected readonly isLoading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly cancellingReservationId = signal<number | null>(null);
  protected readonly activeReservations = computed(() => this.reservations().filter((reservation) => reservation.status === 'ACTIVE'));
  protected readonly reservationHistory = computed(() => this.reservations().filter((reservation) => reservation.status !== 'ACTIVE'));

  private readonly reservationApi = inject(ReservationApiService);
  private readonly resourceApi = inject(ResourceApiService);
  private readonly session = inject(SessionService);

  constructor() { this.loadReservations(); }

  protected loadReservations(): void {
    this.isLoading.set(true);
    this.error.set(null);
    this.resourceApi.getAll({}).subscribe({
      next: (resources) => {
        this.resources.set(resources);
          const currentUser = this.session.currentUser();
          if (!currentUser) { this.loadError('Tu sesión ha expirado. Inicia sesión nuevamente.'); return; }
          this.reservationApi.getMine(currentUser.id).subscribe({
            next: (reservations) => { this.reservations.set(reservations); this.isLoading.set(false); },
            error: (error: HttpErrorResponse) => this.loadError(this.errorMessage(error)),
          });
      },
      error: (error: HttpErrorResponse) => this.loadError(this.errorMessage(error)),
    });
  }

  protected cancelReservation(reservation: Reservation): void {
    this.cancellingReservationId.set(reservation.id);
    this.reservationApi.cancel(reservation.id).subscribe({
      next: () => { this.cancellingReservationId.set(null); this.loadReservations(); },
      error: (error: HttpErrorResponse) => { this.cancellingReservationId.set(null); this.error.set(this.errorMessage(error)); },
    });
  }

  protected resourceName(resourceId: number): string { return this.resources().find((resource) => resource.id === resourceId)?.name || 'Recurso reservado'; }
  protected reservationStatusLabel(status: Reservation['status']): string { return { ACTIVE: 'Activa', CANCELLED: 'Cancelada', COMPLETED: 'Completada' }[status]; }
  protected reservationDateLabel(dateTime: string): string {
    return new Intl.DateTimeFormat('es-CR', { day: 'numeric', month: 'short', year: 'numeric', hour: 'numeric', minute: '2-digit' }).format(new Date(dateTime));
  }

  private loadError(message: string): void { this.error.set(message); this.isLoading.set(false); }
  private errorMessage(error: HttpErrorResponse): string { return error.error?.message || 'No fue posible cargar las reservas. Inténtalo nuevamente.'; }
}
