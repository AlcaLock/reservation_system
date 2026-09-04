import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';
import { AdminDashboardSummary, ResourceStatisticsSummary } from '../models/admin.model';

@Injectable({ providedIn: 'root' })
export class AdminApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/admin`;

  getDashboard() {
    return this.http.get<AdminDashboardSummary>(`${this.baseUrl}/dashboard`);
  }

  getUsers(search?: string) {
    let params = new HttpParams();
    if (search?.trim()) {
      params = params.set('search', search.trim());
    }
    return this.http.get<User[]>(`${this.baseUrl}/users`, { params });
  }

  getUserById(id: number) { return this.http.get<User>(`${this.baseUrl}/users/${id}`); }

  updateUserRole(id: number, role: User['role']) {
    return this.http.patch<User>(`${this.baseUrl}/users/${id}/role`, { role });
  }

  updateUserStatus(id: number, enabled: boolean) {
    return this.http.patch<User>(`${this.baseUrl}/users/${id}/status`, { enabled });
  }

  getResourceStatistics() {
    return this.http.get<ResourceStatisticsSummary>(`${this.baseUrl}/resources/statistics`);
  }
}
