import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DocumentService, DocumentDto, DocumentIngestRequest } from '../../services/document.service';

@Component({
  selector: 'app-document-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './document-management.component.html',
  styleUrls: ['./document-management.component.css']
})
export class DocumentManagementComponent implements OnInit {
  documents: DocumentDto[] = [];
  isLoading = false;
  error: string | null = null;

  // Upload form
  showUploadForm = false;
  uploadTitle = '';
  uploadType: 'PDF' | 'DOCX' | 'TXT' | 'URL' = 'PDF';
  selectedFile: File | null = null;
  uploadUrl = '';
  isUploading = false;

  constructor(private documentService: DocumentService) {}

  ngOnInit() {
    this.loadDocuments();

    // Refresh every 5 seconds to update processing status
    setInterval(() => {
      this.loadDocuments();
    }, 5000);
  }

  loadDocuments() {
    this.documentService.getAllDocuments().subscribe({
      next: (docs) => {
        this.documents = docs;
      },
      error: (err) => {
        console.error('Failed to load documents:', err);
        this.error = 'Failed to load documents';
      }
    });
  }

  toggleUploadForm() {
    this.showUploadForm = !this.showUploadForm;
    if (this.showUploadForm) {
      this.resetUploadForm();
    }
  }

  resetUploadForm() {
    this.uploadTitle = '';
    this.uploadType = 'PDF';
    this.selectedFile = null;
    this.uploadUrl = '';
    this.error = null;
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;

      // Auto-fill title from filename
      if (!this.uploadTitle) {
        this.uploadTitle = file.name.replace(/\.[^/.]+$/, '');
      }
    }
  }

  uploadDocument() {
    if (!this.uploadTitle) {
      this.error = 'Please enter a title';
      return;
    }

    this.isUploading = true;
    this.error = null;

    if (this.uploadType === 'URL') {
      // Ingest from URL
      if (!this.uploadUrl) {
        this.error = 'Please enter a URL';
        this.isUploading = false;
        return;
      }

      const request: DocumentIngestRequest = {
        title: this.uploadTitle,
        sourceUrl: this.uploadUrl,
        documentType: 'URL'
      };

      this.documentService.ingestFromUrl(request).subscribe({
        next: (doc) => {
          console.log('URL ingestion started:', doc);
          this.documents.unshift(doc);
          this.toggleUploadForm();
          this.isUploading = false;
        },
        error: (err) => {
          console.error('Failed to ingest URL:', err);
          this.error = 'Failed to ingest URL: ' + (err.error?.message || err.message);
          this.isUploading = false;
        }
      });
    } else {
      // Upload file
      if (!this.selectedFile) {
        this.error = 'Please select a file';
        this.isUploading = false;
        return;
      }

      this.documentService.uploadDocument(this.selectedFile, this.uploadTitle, this.uploadType).subscribe({
        next: (doc) => {
          console.log('Document uploaded:', doc);
          this.documents.unshift(doc);
          this.toggleUploadForm();
          this.isUploading = false;
        },
        error: (err) => {
          console.error('Failed to upload document:', err);
          this.error = 'Failed to upload document: ' + (err.error?.message || err.message);
          this.isUploading = false;
        }
      });
    }
  }

  deleteDocument(id: number) {
    if (!confirm('Are you sure you want to delete this document?')) {
      return;
    }

    this.documentService.deleteDocument(id).subscribe({
      next: () => {
        console.log('Document deleted:', id);
        this.documents = this.documents.filter(d => d.id !== id);
      },
      error: (err) => {
        console.error('Failed to delete document:', err);
        this.error = 'Failed to delete document';
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'COMPLETED': return 'status-completed';
      case 'PROCESSING': return 'status-processing';
      case 'PENDING': return 'status-pending';
      case 'FAILED': return 'status-failed';
      default: return '';
    }
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'COMPLETED': return '✓';
      case 'PROCESSING': return '⟳';
      case 'PENDING': return '⋯';
      case 'FAILED': return '✗';
      default: return '';
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString();
  }
}
