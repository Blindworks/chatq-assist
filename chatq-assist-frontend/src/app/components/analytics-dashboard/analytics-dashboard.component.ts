import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AnalyticsService, Analytics, FaqPerformance } from '../../services/analytics.service';

@Component({
  selector: 'app-analytics-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './analytics-dashboard.component.html',
  styleUrls: ['./analytics-dashboard.component.css']
})
export class AnalyticsDashboardComponent implements OnInit {
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

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.loadAnalytics();
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
