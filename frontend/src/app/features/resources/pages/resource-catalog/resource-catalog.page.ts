import { Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  LucideCalendarDays,
  LucideCircleCheckBig,
  LucideClock3,
  LucideFlaskConical,
  LucideMapPin,
  LucideMonitorUp,
  LucideSearch,
  LucideSlidersHorizontal,
  LucideUsers,
  LucideX,
} from '@lucide/angular';

import { Resource, ResourceFilters, ResourceStatus, ResourceType } from '../../../../core/models/resource.model';
import { CreateReservationRequest } from '../../../../core/models/reservation.model';
import { ResourceApiService } from '../../../../core/services/resource-api.service';
import { ReservationApiService } from '../../../../core/services/reservation-api.service';
import { SessionService } from '../../../../core/services/session.service';

@Component({
  selector: 'app-resource-catalog-page',
  imports: [
    FormsModule,
    RouterLink,
    LucideCalendarDays,
    LucideCircleCheckBig,
    LucideClock3,
    LucideFlaskConical,
    LucideMapPin,
    LucideMonitorUp,
    LucideSearch,
    LucideSlidersHorizontal,
    LucideUsers,
    LucideX,
  ],
  templateUrl: './resource-catalog.page.html',
  styleUrl: './resource-catalog.page.scss',
})
export class ResourceCatalogPage {
  protected readonly resources = signal<Resource[]>([]);
  protected readonly isLoading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly search = signal('');
  protected readonly type = signal<ResourceType | ''>('');
  protected readonly status = signal<ResourceStatus | ''>('AVAILABLE');
  protected readonly minCapacity = signal<number | null>(null);
  protected readonly selectedResource = signal<Resource | null>(null);
  protected readonly reservationStart = signal(this.defaultStartTime());
  protected readonly reservationEnd = signal(this.defaultEndTime());
  protected readonly reservationPurpose = signal('');
  protected readonly isSubmittingReservation = signal(false);
  protected readonly reservationError = signal<string | null>(null);
  protected readonly reservationSuccess = signal<string | null>(null);
  protected readonly availableCount = computed(() =>
    this.resources().filter((resource) => resource.status === 'AVAILABLE').length,
  );

  private readonly resourceApi = inject(ResourceApiService);
  private readonly reservationApi = inject(ReservationApiService);
  private readonly session = inject(SessionService);

  constructor() { this.loadResources(); }

  protected loadResources(): void {
    this.isLoading.set(true);
    this.error.set(null);
    this.resourceApi.getAll(this.filters()).subscribe({
      next: (resources) => { this.resources.set(resources); this.isLoading.set(false); },
      error: () => {
        this.error.set('No fue posible cargar los recursos. Verifica que el backend esté ejecutándose en el puerto 8080.');
        this.isLoading.set(false);
      },
    });
  }

  protected clearFilters(): void {
    this.search.set('');
    this.type.set('');
    this.status.set('AVAILABLE');
    this.minCapacity.set(null);
    this.loadResources();
  }

  protected openReservation(resource: Resource): void {
    this.selectedResource.set(resource);
    this.reservationPurpose.set('');
    this.reservationError.set(null);
    this.reservationSuccess.set(null);
  }

  protected closeReservation(): void {
    if (!this.isSubmittingReservation()) this.selectedResource.set(null);
  }

  protected createReservation(): void {
    const resource = this.selectedResource();
    const purpose = this.reservationPurpose().trim();
    if (!resource || !purpose) { this.reservationError.set('Indica el propósito de la reserva.'); return; }
    if (this.reservationEnd() <= this.reservationStart()) {
      this.reservationError.set('La hora de finalización debe ser posterior a la hora de inicio.');
      return;
    }

    this.isSubmittingReservation.set(true);
    this.reservationError.set(null);
    const currentUser = this.session.currentUser();
    if (!currentUser) { this.handleReservationError('Tu sesión ha expirado. Inicia sesión nuevamente.'); return; }
    const request: CreateReservationRequest = {
      userId: currentUser.id, resourceId: resource.id, startTime: this.reservationStart(), endTime: this.reservationEnd(), purpose,
    };
    this.reservationApi.create(request).subscribe({
      next: () => { this.isSubmittingReservation.set(false); this.reservationSuccess.set('Reserva creada correctamente.'); this.loadResources(); },
      error: (error: HttpErrorResponse) => this.handleReservationError(this.errorMessage(error)),
    });
  }

  protected statusLabel(status: ResourceStatus): string {
    return { AVAILABLE: 'Disponible', MAINTENANCE: 'Mantenimiento', INACTIVE: 'Inactivo' }[status];
  }
  protected typeLabel(type: ResourceType): string { return { ROOM: 'Sala', LABORATORY: 'Laboratorio', EQUIPMENT: 'Equipo' }[type]; }
  protected capacityLabel(capacity: number): string { return `${capacity} ${capacity === 1 ? 'persona' : 'personas'}`; }

  private handleReservationError(message: string): void { this.isSubmittingReservation.set(false); this.reservationError.set(message); }
  private errorMessage(error: HttpErrorResponse): string { return error.error?.message || 'No fue posible crear la reserva. Inténtalo nuevamente.'; }
  private defaultStartTime(): string { const start = new Date(); start.setDate(start.getDate() + 1); start.setHours(10, 0, 0, 0); return this.toDateTimeLocal(start); }
  private defaultEndTime(): string { const end = new Date(); end.setDate(end.getDate() + 1); end.setHours(12, 0, 0, 0); return this.toDateTimeLocal(end); }
  private toDateTimeLocal(date: Date): string { return new Date(date.getTime() - date.getTimezoneOffset() * 60_000).toISOString().slice(0, 16); }
  private filters(): ResourceFilters {
    return { search: this.search().trim() || undefined, type: this.type() || undefined, status: this.status() || undefined, minCapacity: this.minCapacity() || undefined };
  }
}
