import { HttpContextToken, HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';

import { SessionService } from '../services/session.service';
import { AuthApiService } from '../services/auth-api.service';

const retriedAfterRefresh = new HttpContextToken<boolean>(() => false);

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const token = inject(SessionService).getAccessToken();
  const session = inject(SessionService);
  const authApi = inject(AuthApiService);
  const router = inject(Router);
  if (!token || request.url.includes('/auth/')) return next(request);
  return next(request.clone({ setHeaders: { Authorization: `Bearer ${token}` } })).pipe(
    catchError((error: HttpErrorResponse) => {
      const refreshToken = session.getRefreshToken();
      if (error.status !== 401 || !refreshToken || request.context.get(retriedAfterRefresh)) return throwError(() => error);

      return authApi.refresh(refreshToken).pipe(
        switchMap((response) => {
          session.setTokens(response);
          return next(request.clone({
            context: request.context.set(retriedAfterRefresh, true),
            setHeaders: { Authorization: `Bearer ${response.accessToken}` },
          }));
        }),
        catchError((refreshError: HttpErrorResponse) => {
          session.logOut();
          void router.navigateByUrl('/login');
          return throwError(() => refreshError);
        }),
      );
    }),
  );
};