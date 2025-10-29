import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface FaqEntry {
  id?: number;
  question: string;
  answer: string;
  tags?: string[];
  isActive?: boolean;
  displayOrder?: number;
  usageCount?: number;
  createdAt?: string;
  updatedAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class FaqService {
  private apiUrl = `${environment.apiUrl}/faq`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'X-Tenant-ID': 'default-tenant'
    });
  }

  getAllFaqs(): Observable<FaqEntry[]> {
    return this.http.get<FaqEntry[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  createFaq(faq: FaqEntry): Observable<FaqEntry> {
    return this.http.post<FaqEntry>(this.apiUrl, faq, { headers: this.getHeaders() });
  }

  updateFaq(id: number, faq: FaqEntry): Observable<FaqEntry> {
    return this.http.put<FaqEntry>(`${this.apiUrl}/${id}`, faq, { headers: this.getHeaders() });
  }

  deleteFaq(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }
}
