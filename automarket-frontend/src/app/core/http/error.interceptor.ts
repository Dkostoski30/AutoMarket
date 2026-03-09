import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

/**
 * Global error interceptor — normalizes HTTP errors and could connect to a toast/notification service.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Log to console in dev — in prod this would go to a monitoring service
      if (error.status >= 500) {
        console.error('Server error:', error);
      }
      return throwError(() => error);
    })
  );
};
