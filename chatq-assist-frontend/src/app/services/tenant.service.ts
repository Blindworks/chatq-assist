import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TenantDto {
  id: number;
  tenantId: string;
  name: string;
  contactEmail: string;
  domain?: string;
  apiKey?: string;
  isActive: boolean;
  maxUsers: number;
  maxDocuments: number;
  settings?: string;
  createdAt: string;
  updatedAt: string;
  currentUserCount?: number;
  currentDocumentCount?: number;
}

export interface CreateTenantRequest {
  tenantId?: string;
  name: string;
  contactEmail: string;
  domain?: string;
  maxUsers?: number;
  maxDocuments?: number;
  settings?: string;
}

@Injectable({
  providedIn: 'root'
})
export class TenantService {
  private apiUrl = 'http://localhost:8080/api/tenants';

  constructor(private http: HttpClient) {}

  getAllTenants(): Observable<TenantDto[]> {
    return this.http.get<TenantDto[]>(this.apiUrl);
  }

  getActiveTenants(): Observable<TenantDto[]> {
    return this.http.get<TenantDto[]>(`${this.apiUrl}/active`);
  }

  getTenant(id: number): Observable<TenantDto> {
    return this.http.get<TenantDto>(`${this.apiUrl}/${id}`);
  }

  getTenantByTenantId(tenantId: string): Observable<TenantDto> {
    return this.http.get<TenantDto>(`${this.apiUrl}/by-tenant-id/${tenantId}`);
  }

  createTenant(request: CreateTenantRequest): Observable<TenantDto> {
    return this.http.post<TenantDto>(this.apiUrl, request);
  }

  updateTenant(id: number, request: CreateTenantRequest): Observable<TenantDto> {
    return this.http.put<TenantDto>(`${this.apiUrl}/${id}`, request);
  }

  toggleTenantStatus(id: number): Observable<TenantDto> {
    return this.http.post<TenantDto>(`${this.apiUrl}/${id}/toggle-status`, {});
  }

  regenerateApiKey(id: number): Observable<TenantDto> {
    return this.http.post<TenantDto>(`${this.apiUrl}/${id}/regenerate-api-key`, {});
  }

  deleteTenant(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
