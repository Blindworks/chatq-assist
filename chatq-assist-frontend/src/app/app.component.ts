import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  template: `
    <div class="app-container">
      <nav class="app-nav" *ngIf="showNav()">
        <div class="nav-left">
          <a routerLink="/" class="nav-link">Chat Widget</a>
          <a routerLink="/admin" class="nav-link" *ngIf="authService.isAuthenticated()">Admin Dashboard</a>
          <a routerLink="/analytics" class="nav-link" *ngIf="authService.isAuthenticated()">Analytics</a>
        </div>
        <div class="nav-right" *ngIf="authService.isAuthenticated()">
          <span class="user-info">{{ authService.getUsername() }}</span>
          <button (click)="logout()" class="logout-btn">Logout</button>
        </div>
        <div class="nav-right" *ngIf="!authService.isAuthenticated()">
          <a routerLink="/login" class="nav-link">Login</a>
        </div>
      </nav>
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
    }
    .app-nav {
      background: #2d3748;
      padding: 12px 24px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 24px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    .nav-left, .nav-right {
      display: flex;
      gap: 16px;
      align-items: center;
    }
    .nav-link {
      color: white;
      text-decoration: none;
      font-weight: 500;
      padding: 8px 16px;
      border-radius: 6px;
      transition: background 0.2s;
    }
    .nav-link:hover {
      background: rgba(255,255,255,0.1);
    }
    .user-info {
      color: #a0aec0;
      font-size: 14px;
      margin-right: 8px;
    }
    .logout-btn {
      background: rgba(255,255,255,0.1);
      color: white;
      border: none;
      padding: 8px 16px;
      border-radius: 6px;
      cursor: pointer;
      font-weight: 500;
      transition: background 0.2s;
    }
    .logout-btn:hover {
      background: rgba(255,255,255,0.2);
    }
  `]
})
export class AppComponent {
  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  showNav(): boolean {
    // Don't show nav on login page
    return !this.router.url.includes('/login');
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
