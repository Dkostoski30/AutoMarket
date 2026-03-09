import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthStore } from '../auth/auth.store';
import { AuthService } from '../auth/auth.service';

/**
 * Functional HTTP interceptor that:
 * 1. Attaches the Bearer JWT to all outgoing API requests
 * 2. On 401, attempts a token refresh once and retries the original request
 * 3. On refresh failure, clears the session and lets the error propagate
 */
export const jwtInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const store = inject(AuthStore);
  const authService = inject(AuthService);

  const accessToken = store.accessToken();
  const authorizedReq = accessToken ? addToken(req, accessToken) : req;

  return next(authorizedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && store.hasStoredSession()) {
        return authService.refresh().pipe(
          switchMap(pair => next(addToken(req, pair.accessToken))),
          catchError(refreshError => {
            authService.logout();
            return throwError(() => refreshError);
          })
        );
      }
      return throwError(() => error);
    })
  );
};

function addToken(req: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
}
