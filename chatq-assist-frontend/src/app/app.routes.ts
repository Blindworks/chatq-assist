import { Routes } from '@angular/router';
import { ChatWidgetComponent } from './components/chat-widget/chat-widget.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { AnalyticsDashboardComponent } from './components/analytics-dashboard/analytics-dashboard.component';
import { TicketManagementComponent } from './components/ticket-management/ticket-management.component';
import { LoginComponent } from './components/login/login.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: ChatWidgetComponent },
  { path: 'login', component: LoginComponent },
  {
    path: 'admin',
    component: AdminDashboardComponent,
    canActivate: [AuthGuard],
    data: { requiresAdmin: true }
  },
  {
    path: 'analytics',
    component: AnalyticsDashboardComponent,
    canActivate: [AuthGuard],
    data: { requiresAdmin: true }
  },
  {
    path: 'tickets',
    component: TicketManagementComponent,
    canActivate: [AuthGuard],
    data: { requiresAdmin: true }
  },
  { path: '**', redirectTo: '' }
];
