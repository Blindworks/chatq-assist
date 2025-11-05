import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export type TicketStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type TicketPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export interface SupportTicket {
  id?: number;
  tenantId?: string;
  customerName: string;
  customerEmail: string;
  customerPhone?: string;
  customerQuestion?: string;
  status: TicketStatus;
  priority: TicketPriority;
  createdAt?: string;
  updatedAt?: string;
  assignedTo?: string;
  notes?: string;
  sessionId?: string;
}

export interface TicketStats {
  total: number;
  open: number;
  inProgress: number;
  resolved: number;
  closed: number;
}

export interface TicketUpdateRequest {
  status?: TicketStatus;
  priority?: TicketPriority;
  assignedTo?: string;
  notes?: string;
}

interface PageResponse<T> {
  content: T[];
  pageable: any;
  totalElements: number;
  totalPages: number;
  last: boolean;
  size: number;
  number: number;
  sort: any;
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private readonly apiUrl = 'http://localhost:8080/api/tickets';
  private readonly tenantId = 'default-tenant';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'X-Tenant-ID': this.tenantId
    });
  }

  getTickets(status?: TicketStatus, page?: number, size?: number): Observable<SupportTicket[]> {
    let params = new HttpParams();

    if (status) {
      params = params.set('status', status);
    }
    if (page !== undefined) {
      params = params.set('page', page.toString());
    }
    if (size !== undefined) {
      params = params.set('size', size.toString());
    }

    return this.http.get<PageResponse<SupportTicket>>(this.apiUrl, {
      headers: this.getHeaders(),
      params
    }).pipe(
      map(response => response.content)
    );
  }

  getTicketById(id: number): Observable<SupportTicket> {
    return this.http.get<SupportTicket>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }

  updateTicket(id: number, updates: TicketUpdateRequest): Observable<SupportTicket> {
    return this.http.put<SupportTicket>(`${this.apiUrl}/${id}`, updates, {
      headers: this.getHeaders()
    });
  }

  deleteTicket(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }

  getTicketStats(): Observable<TicketStats> {
    return this.http.get<TicketStats>(`${this.apiUrl}/stats`, {
      headers: this.getHeaders()
    });
  }
}
