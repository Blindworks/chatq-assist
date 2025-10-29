import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  template: `
    <div class="app-container">
      <nav class="app-nav">
        <a routerLink="/" class="nav-link">Chat Widget</a>
        <a routerLink="/admin" class="nav-link">Admin Dashboard</a>
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
      gap: 24px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
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
  `]
})
export class AppComponent {}
