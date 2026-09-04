import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { LucideSearch } from '@lucide/angular';

import { AdminApiService } from '../../../../core/services/admin-api.service';
import { User } from '../../../../core/models/user.model';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-users-page',
  imports: [FormsModule, RouterLink, LucideSearch],
  templateUrl: './admin-users.page.html',
  styleUrl: './admin-users.page.scss',
})
export class AdminUsersPage {
  private readonly adminApi = inject(AdminApiService);

  protected readonly users = signal<User[]>([]);
  protected readonly search = signal('');
  protected readonly isLoading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly updatingRoleId = signal<number | null>(null);
  protected readonly updatingStatusId = signal<number | null>(null);

  constructor() {
    this.loadUsers();
  }

  protected loadUsers(): void {
    this.isLoading.set(true);
    this.error.set(null);
    this.adminApi.getUsers(this.search()).subscribe({
      next: (users) => {
        this.users.set(users);
        this.isLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.error.set(error.error?.message || 'No fue posible cargar la lista de usuarios.');
        this.isLoading.set(false);
      },
    });
  }

  protected updateRole(user: User, role: User['role']): void {
    this.updatingRoleId.set(user.id);
    this.adminApi.updateUserRole(user.id, role).subscribe({
      next: (updated) => {
        this.users.update((items) => items.map((item) => item.id === updated.id ? updated : item));
        this.updatingRoleId.set(null);
      },
      error: (error: HttpErrorResponse) => {
        this.error.set(error.error?.message || 'No fue posible actualizar el rol del usuario.');
        this.updatingRoleId.set(null);
      },
    });
  }

  protected toggleStatus(user: User): void {
    this.updatingStatusId.set(user.id);
    this.adminApi.updateUserStatus(user.id, !user.enabled).subscribe({
      next: (updated) => {
        this.users.update((items) => items.map((item) => item.id === updated.id ? updated : item));
        this.updatingStatusId.set(null);
      },
      error: (error: HttpErrorResponse) => {
        this.error.set(error.error?.message || 'No fue posible actualizar el estado del usuario.');
        this.updatingStatusId.set(null);
      },
    });
  }

  protected fullName(user: User): string {
    return `${user.firstName} ${user.lastName}`;
  }

  protected roleLabel(role: User['role']): string {
    return role === 'ADMIN' ? 'Administrador' : 'Estudiante';
  }

  protected statusLabel(enabled: boolean): string {
    return enabled ? 'Activo' : 'Inactivo';
  }
}
