import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TenantContextService } from './tenant-context.service';

export interface DocumentDto {
  id: number;
  title: string;
  sourceUrl?: string;
  documentType: 'URL' | 'PDF' | 'DOCX' | 'TXT' | 'SITEMAP';
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  chunkCount?: number;
  errorMessage?: string;
  createdAt: string;
  updatedAt: string;
}

export interface DocumentIngestRequest {
  title: string;
  sourceUrl: string;
  documentType: 'URL' | 'SITEMAP';
}

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private apiUrl = 'http://localhost:8080/api/documents';

  constructor(
    private http: HttpClient,
    private tenantContext: TenantContextService
  ) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'X-Tenant-ID': this.tenantContext.getSelectedTenantId()
    });
  }

  /**
   * Upload a document file (PDF, DOCX, TXT)
   */
  uploadDocument(file: File, title: string, documentType: 'PDF' | 'DOCX' | 'TXT'): Observable<DocumentDto> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('title', title);
    formData.append('documentType', documentType);

    // For FormData, we need to set headers differently (no Content-Type)
    const headers = new HttpHeaders({
      'X-Tenant-ID': this.tenantContext.getSelectedTenantId()
    });

    return this.http.post<DocumentDto>(`${this.apiUrl}/upload`, formData, { headers });
  }

  /**
   * Ingest document from URL
   */
  ingestFromUrl(request: DocumentIngestRequest): Observable<DocumentDto> {
    return this.http.post<DocumentDto>(`${this.apiUrl}/ingest`, request, { headers: this.getHeaders() });
  }

  /**
   * Get all documents
   */
  getAllDocuments(): Observable<DocumentDto[]> {
    return this.http.get<DocumentDto[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  /**
   * Get document by ID
   */
  getDocument(id: number): Observable<DocumentDto> {
    return this.http.get<DocumentDto>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  /**
   * Delete document
   */
  deleteDocument(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }
}
