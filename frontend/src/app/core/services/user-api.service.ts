import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { User } from '../models/user.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserApiService {
  private readonly http = inject(HttpClient);

  getCurrent() {
    return this.http.get<User>(`${environment.apiUrl}/users/me`);
  }

  updateProfile(request: { firstName: string; lastName: string; password?: string }) {
    return this.http.put<User>(`${environment.apiUrl}/users/me`, request);
  }
}