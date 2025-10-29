import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';

export interface ChatRequest {
  question: string;
  sessionId?: string;
  userEmail?: string;
}

export interface ChatResponse {
  sessionId: string;
  answer: string;
  confidenceScore: number;
  sources: SourceReference[];
  handoffTriggered: boolean;
  handoffMessage?: string;
}

export interface SourceReference {
  type: string;
  title: string;
  url?: string;
  id: number;
}

export interface StreamingChatResponse {
  type: 'token' | 'metadata' | 'messageId' | 'complete' | 'error';
  token?: string;
  metadata?: {
    sessionId: string;
    confidenceScore: number;
    sources?: SourceReference[];
    handoffTriggered: boolean;
  };
  messageId?: number;
  error?: string;
}

export interface MessageDto {
  id: number;
  role: 'USER' | 'ASSISTANT';
  content: string;
  confidenceScore?: number;
  faqEntryId?: number;
  createdAt: string;
}

export interface FeedbackRequest {
  messageId: number;
  feedbackType: 'POSITIVE' | 'NEGATIVE';
  comment?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8080/api/chat';

  constructor(private http: HttpClient) {}

  sendMessage(request: ChatRequest): Observable<ChatResponse> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'X-Tenant-ID': 'default-tenant'
    });

    return this.http.post<ChatResponse>(this.apiUrl, request, { headers });
  }

  sendMessageStreaming(request: ChatRequest): Observable<StreamingChatResponse> {
    const subject = new Subject<StreamingChatResponse>();

    fetch(`${this.apiUrl}/stream`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Tenant-ID': 'default-tenant'
      },
      body: JSON.stringify(request)
    }).then(response => {
      const reader = response.body?.getReader();
      const decoder = new TextDecoder();
      let buffer = '';

      const readStream = () => {
        reader?.read().then(({ done, value }) => {
          if (done) {
            subject.next({ type: 'complete' });
            subject.complete();
            return;
          }

          buffer += decoder.decode(value, { stream: true });
          const events = buffer.split('\n\n');

          // Keep the last incomplete event in buffer
          buffer = events.pop() || '';

          events.forEach(eventText => {
            if (!eventText.trim()) return;

            const lines = eventText.split('\n');
            let eventType = '';
            let eventData = '';

            lines.forEach(line => {
              if (line.startsWith('event:')) {
                eventType = line.substring(6).trim();
              } else if (line.startsWith('data:')) {
                // DON'T trim the data - preserve whitespace for tokens!
                eventData = line.substring(5);
              }
            });

            if (eventType && eventData !== '') {
              if (eventType === 'token') {
                // Token data is just a string, not JSON - preserve whitespace!
                subject.next({ type: 'token', token: eventData });
              } else if (eventType === 'metadata') {
                try {
                  const metadata = JSON.parse(eventData.trim());
                  subject.next({ type: 'metadata', metadata });
                } catch (e) {
                  console.error('Failed to parse metadata:', e);
                }
              } else if (eventType === 'messageId') {
                try {
                  const data = JSON.parse(eventData.trim());
                  subject.next({ type: 'messageId', messageId: data.messageId });
                } catch (e) {
                  console.error('Failed to parse messageId:', e);
                }
              } else if (eventType === 'message') {
                // Fallback message
                subject.next({ type: 'token', token: eventData });
              }
            }
          });

          readStream();
        }).catch(error => {
          subject.next({ type: 'error', error: error.message });
          subject.error(error);
        });
      };

      readStream();
    }).catch(error => {
      subject.next({ type: 'error', error: error.message });
      subject.error(error);
    });

    return subject.asObservable();
  }

  getHistory(sessionId: string): Observable<MessageDto[]> {
    const headers = new HttpHeaders({
      'X-Tenant-ID': 'default-tenant'
    });

    return this.http.get<MessageDto[]>(`${this.apiUrl}/history/${sessionId}`, { headers });
  }

  submitFeedback(request: FeedbackRequest): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'X-Tenant-ID': 'default-tenant'
    });

    return this.http.post(`${this.apiUrl}/feedback`, request, { headers });
  }
}
