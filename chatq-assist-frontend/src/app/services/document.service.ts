import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

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

  constructor(private http: HttpClient) {}

  /**
   * Upload a document file (PDF, DOCX, TXT)
   */
  uploadDocument(file: File, title: string, documentType: 'PDF' | 'DOCX' | 'TXT'): Observable<DocumentDto> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('title', title);
    formData.append('documentType', documentType);

    return this.http.post<DocumentDto>(`${this.apiUrl}/upload`, formData);
  }

  /**
   * Ingest document from URL
   */
  ingestFromUrl(request: DocumentIngestRequest): Observable<DocumentDto> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<DocumentDto>(`${this.apiUrl}/ingest`, request, { headers });
  }

  /**
   * Get all documents
   */
  getAllDocuments(): Observable<DocumentDto[]> {
    return this.http.get<DocumentDto[]>(this.apiUrl);
  }

  /**
   * Get document by ID
   */
  getDocument(id: number): Observable<DocumentDto> {
    return this.http.get<DocumentDto>(`${this.apiUrl}/${id}`);
  }

  /**
   * Delete document
   */
  deleteDocument(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
