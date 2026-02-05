import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AnalyticsService, Analytics, FaqPerformance } from '../../services/analytics.service';
import { TenantContextService } from '../../services/tenant-context.service';
import { LucideAngularModule, BarChart3, MessageCircle, ThumbsUp, ThumbsDown, Mail, User, TrendingUp, Trophy } from 'lucide-angular';

@Component({
  selector: 'app-analytics-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  templateUrl: './analytics-dashboard.component.html',
  styleUrls: ['./analytics-dashboard.component.css']
})
export class AnalyticsDashboardComponent implements OnInit, OnDestroy {
  analytics: Analytics | null = null;
  isLoading: boolean = false;
  error: string | null = null;
  daysBack: number = 30;

  // For chart rendering
  chartDates: string[] = [];
  chartCounts: number[] = [];
  maxCount: number = 0;

  // Make Math available in template
  Math = Math;

  // Lucide icons
  readonly BarChart3 = BarChart3;
  readonly MessageCircle = MessageCircle;
  readonly ThumbsUp = ThumbsUp;
  readonly ThumbsDown = ThumbsDown;
  readonly Mail = Mail;
  readonly User = User;
  readonly TrendingUp = TrendingUp;
  readonly Trophy = Trophy;

  private tenantSubscription?: Subscription;

  constructor(
    private analyticsService: AnalyticsService,
    private tenantContext: TenantContextService
  ) {}

  ngOnInit(): void {
    this.loadAnalytics();

    // Subscribe to tenant changes
    this.tenantSubscription = this.tenantContext.selectedTenant$.subscribe(tenant => {
      if (tenant) {
        this.loadAnalytics();
      }
    });
  }

  ngOnDestroy(): void {
    if (this.tenantSubscription) {
      this.tenantSubscription.unsubscribe();
    }
  }

  loadAnalytics(): void {
    this.isLoading = true;
    this.error = null;

    this.analyticsService.getAnalytics(this.daysBack).subscribe({
      next: (data) => {
        this.analytics = data;
        this.prepareChartData();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load analytics. Please try again.';
        this.isLoading = false;
        console.error('Error loading analytics:', err);
      }
    });
  }

  onDaysBackChange(): void {
    this.loadAnalytics();
  }

  prepareChartData(): void {
    if (!this.analytics?.questionsByDate) return;

    const entries = Object.entries(this.analytics.questionsByDate);
    this.chartDates = entries.map(([date]) => this.formatDate(date));
    this.chartCounts = entries.map(([, count]) => count);
    this.maxCount = Math.max(...this.chartCounts, 1);
  }

  formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    return `${date.getMonth() + 1}/${date.getDate()}`;
  }

  getBarHeight(count: number): string {
    if (this.maxCount === 0) return '0%';
    return `${(count / this.maxCount) * 100}%`;
  }

  getFeedbackClass(rate: number): string {
    if (rate >= 75) return 'excellent';
    if (rate >= 50) return 'good';
    if (rate >= 25) return 'fair';
    return 'poor';
  }

  getSatisfactionEmoji(rate: number): string {
    if (rate >= 75) return '😊';
    if (rate >= 50) return '🙂';
    if (rate >= 25) return '😐';
    return '😞';
  }
}
