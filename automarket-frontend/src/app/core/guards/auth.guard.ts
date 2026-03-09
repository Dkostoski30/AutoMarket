import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthStore } from '../auth/auth.store';

export const authGuard: CanActivateFn = (route, state) => {
  const store  = inject(AuthStore);
  const router = inject(Router);

  if (store.isLoggedIn() || store.hasStoredSession()) {
    return true;
  }

  return router.createUrlTree(['/auth/login'], { queryParams: { returnUrl: state.url } });
};
