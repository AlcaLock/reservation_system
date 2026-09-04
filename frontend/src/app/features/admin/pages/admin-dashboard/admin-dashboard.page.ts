import { Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { LucideAlertTriangle, LucideArrowRight, LucideCalendarRange, LucideUsers } from '@lucide/angular';

import { AdminDashboardSummary, ResourceStatisticsSummary } from '../../../../core/models/admin.model';
import { AdminApiService } from '../../../../core/services/admin-api.service';
import { SessionService } from '../../../../core/services/session.service';

@Component({
  selector: 'app-admin-dashboard-page',
  imports: [LucideAlertTriangle, LucideArrowRight, LucideCalendarRange, LucideUsers],
  templateUrl: './admin-dashboard.page.html',
  styleUrl: './admin-dashboard.page.scss',
})
export class AdminDashboardPage {
  private readonly adminApi = inject(AdminApiService);
  private readonly session = inject(SessionService);

  protected readonly dashboard = signal<AdminDashboardSummary | null>(null);
  protected readonly resourceStats = signal<ResourceStatisticsSummary | null>(null);
  protected readonly isLoading = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly summaryCards = computed(() => {
    const data = this.dashboard();
    if (!data) {
      return [];
    }

    return [
      { label: 'Usuarios activos', value: `${data.enabledUsers}/${data.totalUsers}`, tone: 'primary' },
      { label: 'Recursos disponibles', value: `${data.availableResources}/${data.totalResources}`, tone: 'success' },
      { label: 'Reservas activas', value: String(data.activeReservations), tone: 'warning' },
      { label: 'Ocupación actual', value: `${data.currentOccupancyPercentage}%`, tone: 'info' },
    ];
  });

  constructor() {
    this.loadDashboard();
  }

  protected loadDashboard(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.adminApi.getDashboard().subscribe({
      next: (dashboard) => {
        this.dashboard.set(dashboard);
        this.adminApi.getResourceStatistics().subscribe({
          next: (stats) => {
            this.resourceStats.set(stats);
            this.isLoading.set(false);
          },
          error: (err: HttpErrorResponse) => this.handleError(err),
        });
      },
      error: (err: HttpErrorResponse) => this.handleError(err),
    });
  }

  protected shortDate(value: string): string {
    return new Intl.DateTimeFormat('es-CR', { day: 'numeric', month: 'short' }).format(new Date(value));
  }

  protected barHeight(count: number): number {
    return Math.min(100, count * 18);
  }

  protected fullName(): string {
    const user = this.session.currentUser();
    if (!user) {
      return 'Administrador';
    }
    return `${user.firstName} ${user.lastName}`;
  }

  private handleError(error: HttpErrorResponse): void {
    this.error.set(error.error?.message || 'No fue posible cargar el dashboard administrativo.');
    this.isLoading.set(false);
  }
}
