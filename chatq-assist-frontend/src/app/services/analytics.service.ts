import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface FeedbackMetrics {
  totalFeedback: number;
  positiveFeedback: number;
  negativeFeedback: number;
  positiveRate: number;
  negativeRate: number;
}

export interface FaqPerformance {
  faqId: number;
  question: string;
  usageCount: number;
  averageConfidence?: number;
  positiveFeedbackCount: number;
  negativeFeedbackCount: number;
  satisfactionRate: number;
}

export interface ConversationMetrics {
  totalConversations: number;
  activeConversations: number;
  closedConversations: number;
  handedOffConversations: number;
  averageMessagesPerConversation: number;
  totalMessages: number;
}

export interface Analytics {
  totalQuestions?: number;
  answeredQuestions?: number;
  handoffCount?: number;
  deflectionRate?: number;
  averageConfidence?: number;
  feedbackMetrics: FeedbackMetrics;
  topFaqs: FaqPerformance[];
  conversationMetrics: ConversationMetrics;
  questionsByDate: { [key: string]: number };
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private apiUrl = `${environment.apiUrl}/analytics`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'X-Tenant-ID': 'default-tenant'
    });
  }

  getAnalytics(daysBack: number = 30): Observable<Analytics> {
    const params = new HttpParams().set('daysBack', daysBack.toString());
    return this.http.get<Analytics>(this.apiUrl, {
      headers: this.getHeaders(),
      params: params
    });
  }
}
