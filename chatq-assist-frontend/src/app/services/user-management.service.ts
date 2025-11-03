import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserDto {
  id: number;
  username: string;
  email?: string;
  role: 'USER' | 'ADMIN' | 'SUPER_ADMIN' | 'TENANT_ADMIN' | 'TENANT_USER';
  enabled: boolean;
  tenantId: string;
  tenantName?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserRequest {
  username: string;
  password: string;
  email?: string;
  role: 'USER' | 'ADMIN' | 'SUPER_ADMIN' | 'TENANT_ADMIN' | 'TENANT_USER';
  tenantId: string;
  enabled?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(this.apiUrl);
  }

  getUsersByTenant(tenantId: string): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.apiUrl}/tenant/${tenantId}`);
  }

  getUser(id: number): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.apiUrl}/${id}`);
  }

  createUser(request: CreateUserRequest): Observable<UserDto> {
    return this.http.post<UserDto>(this.apiUrl, request);
  }

  updateUser(id: number, request: Partial<CreateUserRequest>): Observable<UserDto> {
    return this.http.put<UserDto>(`${this.apiUrl}/${id}`, request);
  }

  toggleUserStatus(id: number): Observable<UserDto> {
    return this.http.post<UserDto>(`${this.apiUrl}/${id}/toggle-status`, {});
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
