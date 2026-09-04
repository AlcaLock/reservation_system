import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { Resource, ResourceFilters, ResourceStatus, ResourceType } from '../models/resource.model';
import { environment } from '../../../environments/environment';

export interface ResourceRequest {
  name: string;
  description: string;
  type: ResourceType;
  capacity: number;
  location: string;
}

@Injectable({ providedIn: 'root' })
export class ResourceApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/resources`;

  getAll(filters: ResourceFilters) {
    let params = new HttpParams();

    if (filters.search) params = params.set('search', filters.search);
    if (filters.type) params = params.set('type', filters.type);
    if (filters.status) params = params.set('status', filters.status);
    if (filters.minCapacity) params = params.set('minCapacity', filters.minCapacity);

    return this.http.get<Resource[]>(this.baseUrl, { params });
  }

  getById(id: number) { return this.http.get<Resource>(`${this.baseUrl}/${id}`); }

  create(request: ResourceRequest) {
    return this.http.post<Resource>(this.baseUrl, request);
  }

  update(id: number, request: ResourceRequest) {
    return this.http.put<Resource>(`${this.baseUrl}/${id}`, request);
  }

  updateStatus(id: number, status: ResourceStatus) {
    return this.http.patch<Resource>(`${this.baseUrl}/${id}/status`, { status });
  }
}