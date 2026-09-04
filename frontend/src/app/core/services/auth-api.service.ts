import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  login(request: LoginRequest) { return this.http.post<AuthResponse>(`${this.baseUrl}/login`, request); }
  register(request: RegisterRequest) { return this.http.post<AuthResponse>(`${this.baseUrl}/register`, request); }
  refresh(refreshToken: string) { return this.http.post<AuthResponse>(`${this.baseUrl}/refresh`, { refreshToken }); }
  logout(refreshToken: string) { return this.http.post<void>(`${this.baseUrl}/logout`, { refreshToken }); }
}