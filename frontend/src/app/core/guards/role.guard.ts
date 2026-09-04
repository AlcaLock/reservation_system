import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { SessionService } from '../services/session.service';

export const roleGuard: CanActivateFn = (route) => {
  const session = inject(SessionService);
  const router = inject(Router);

  const user = session.currentUser();
  const requiredRole = route.data['role'] as 'ADMIN' | 'STUDENT' | undefined;

  if (!user) {
    return router.createUrlTree(['/login']);
  }

  if (requiredRole && user.role !== requiredRole) {
    return router.createUrlTree(['/not-allowed']);
  }

  return true;
};
