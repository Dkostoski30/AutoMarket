import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthStore } from '../auth/auth.store';

/**
 * Role guard — checks if the authenticated user has required roles.
 * Usage: canActivate: [roleGuard('ADMIN')] or roleGuard('MODERATOR')
 */
export const roleGuard = (...requiredRoles: string[]): CanActivateFn => {
  return () => {
    const store  = inject(AuthStore);
    const router = inject(Router);
    const user   = store.user();

    if (!user) {
      return router.createUrlTree(['/auth/login']);
    }

    const hasRole = requiredRoles.some(role => user.roles.includes(`ROLE_${role}`));
    if (!hasRole) {
      return router.createUrlTree(['/']);
    }

    return true;
  };
};
