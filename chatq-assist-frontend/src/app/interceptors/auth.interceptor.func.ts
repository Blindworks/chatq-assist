import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Get the auth token from the service
  const token = authService.getToken();
  console.log('Functional Interceptor - Token from storage:', token ? 'exists' : 'missing');
  console.log('Functional Interceptor - Request URL:', req.url);

  // Clone the request and add the authorization header and tenant ID if token exists
  let authReq = req;
  if (token) {
    // Get tenant ID from localStorage or use default
    const tenantId = localStorage.getItem('tenant_id') || 'system';

    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
        'X-Tenant-ID': tenantId
      }
    });
    console.log('Functional Interceptor - Added Authorization header and X-Tenant-ID:', tenantId);
  } else {
    console.warn('Functional Interceptor - No token found, request will be sent without auth');
  }

  // Handle the request and catch errors
  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Unauthorized - token might be expired or invalid
        console.warn('Unauthorized request, logging out');
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
