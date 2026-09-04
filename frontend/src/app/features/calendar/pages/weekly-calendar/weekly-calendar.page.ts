import { Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { LucideChevronLeft, LucideChevronRight, LucideRotateCcw } from '@lucide/angular';

import { Reservation } from '../../../../core/models/reservation.model';
import { Resource, ResourceType } from '../../../../core/models/resource.model';
import { ReservationApiService } from '../../../../core/services/reservation-api.service';
import { ResourceApiService } from '../../../../core/services/resource-api.service';

interface CalendarDay {
  date: Date;
  label: string;
  shortDate: string;
}

@Component({
  selector: 'app-weekly-calendar-page',
  imports: [LucideChevronLeft, LucideChevronRight, LucideRotateCcw],
  templateUrl: './weekly-calendar.page.html',
  styleUrl: './weekly-calendar.page.scss',
})
export class WeeklyCalendarPage {
  protected readonly weekStart = signal(this.startOfWeek(new Date()));
  protected readonly resources = signal<Resource[]>([]);
  protected readonly reservations = signal<Reservation[]>([]);
  protected readonly isLoading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly hours = Array.from({ length: 13 }, (_, index) => index + 7);
  protected readonly days = computed(() => this.daysForWeek(this.weekStart()));
  protected readonly weekLabel = computed(() => {
    const start = this.weekStart();
    const end = this.addDays(start, 6);
    const formatter = new Intl.DateTimeFormat('es-CR', { day: 'numeric', month: 'short' });
    return `${formatter.format(start)} - ${formatter.format(end)}`;
  });

  private readonly reservationApi = inject(ReservationApiService);
  private readonly resourceApi = inject(ResourceApiService);

  constructor() { this.loadWeek(); }

  protected previousWeek(): void { this.weekStart.set(this.addDays(this.weekStart(), -7)); this.loadWeek(); }
  protected nextWeek(): void { this.weekStart.set(this.addDays(this.weekStart(), 7)); this.loadWeek(); }
  protected currentWeek(): void { this.weekStart.set(this.startOfWeek(new Date())); this.loadWeek(); }

  protected reservationsForDay(day: CalendarDay): Reservation[] {
    return this.reservations().filter((reservation) => this.isSameDate(new Date(reservation.startTime), day.date));
  }

  protected reservationStyle(reservation: Reservation): { top: string; height: string } {
    const start = new Date(reservation.startTime);
    const end = new Date(reservation.endTime);
    const minuteOffset = (start.getHours() - 7) * 60 + start.getMinutes();
    const duration = Math.max(30, (end.getTime() - start.getTime()) / 60_000);
    return { top: `${Math.max(0, minuteOffset)}px`, height: `${duration}px` };
  }

  protected resourceName(resourceId: number): string { return this.resource(resourceId)?.name || 'Recurso reservado'; }
  protected resourceTypeClass(resourceId: number): string { return this.resource(resourceId)?.type.toLowerCase() || 'room'; }
  protected reservationTime(reservation: Reservation): string {
    const formatter = new Intl.DateTimeFormat('es-CR', { hour: 'numeric', minute: '2-digit' });
    return `${formatter.format(new Date(reservation.startTime))} - ${formatter.format(new Date(reservation.endTime))}`;
  }

  private loadWeek(): void {
    this.isLoading.set(true);
    this.error.set(null);
    const start = this.weekStart();
    const end = this.addDays(start, 7);
    this.resourceApi.getAll({}).subscribe({
      next: (resources) => {
        this.resources.set(resources);
        this.reservationApi.getByTimeRange(this.toApiDate(start), this.toApiDate(end)).subscribe({
          next: (reservations) => {
            this.reservations.set(reservations.filter((reservation) => reservation.status === 'ACTIVE'));
            this.isLoading.set(false);
          },
          error: (error: HttpErrorResponse) => this.loadError(error),
        });
      },
      error: (error: HttpErrorResponse) => this.loadError(error),
    });
  }

  private resource(resourceId: number): Resource | undefined { return this.resources().find((resource) => resource.id === resourceId); }
  private loadError(error: HttpErrorResponse): void { this.error.set(error.error?.message || 'No fue posible cargar el calendario.'); this.isLoading.set(false); }
  private startOfWeek(date: Date): Date { const result = new Date(date); const offset = (result.getDay() + 6) % 7; result.setDate(result.getDate() - offset); result.setHours(0, 0, 0, 0); return result; }
  private addDays(date: Date, amount: number): Date { const result = new Date(date); result.setDate(result.getDate() + amount); return result; }
  private daysForWeek(start: Date): CalendarDay[] {
    const formatter = new Intl.DateTimeFormat('es-CR', { weekday: 'short' });
    return Array.from({ length: 7 }, (_, index) => {
      const date = this.addDays(start, index);
      return { date, label: formatter.format(date).replace('.', ''), shortDate: String(date.getDate()) };
    });
  }
  private isSameDate(first: Date, second: Date): boolean { return first.getFullYear() === second.getFullYear() && first.getMonth() === second.getMonth() && first.getDate() === second.getDate(); }
  private toApiDate(date: Date): string { return new Date(date.getTime() - date.getTimezoneOffset() * 60_000).toISOString().slice(0, 19); }
}