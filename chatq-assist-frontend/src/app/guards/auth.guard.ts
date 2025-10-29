import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (this.authService.isAuthenticated()) {
      // Check if route requires admin role
      const requiresAdmin = route.data['requiresAdmin'];
      if (requiresAdmin && !this.authService.isAdmin()) {
        console.warn('Access denied: Admin role required');
        this.router.navigate(['/login']);
        return false;
      }
      return true;
    }

    // Not logged in, redirect to login page with return URL
    console.log('Not authenticated, redirecting to login');
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
}
