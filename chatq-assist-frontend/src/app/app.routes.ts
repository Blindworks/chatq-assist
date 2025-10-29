import { Routes } from '@angular/router';
import { ChatWidgetComponent } from './components/chat-widget/chat-widget.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { AnalyticsDashboardComponent } from './components/analytics-dashboard/analytics-dashboard.component';

export const routes: Routes = [
  { path: '', component: ChatWidgetComponent },
  { path: 'admin', component: AdminDashboardComponent },
  { path: 'analytics', component: AnalyticsDashboardComponent },
  { path: '**', redirectTo: '' }
];
