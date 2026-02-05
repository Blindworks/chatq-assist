import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { FaqService, FaqEntry } from '../../services/faq.service';
import { AuthService } from '../../services/auth.service';
import { DocumentManagementComponent } from '../document-management/document-management.component';
import { TenantManagementComponent } from '../tenant-management/tenant-management.component';
import { UserManagementComponent } from '../user-management/user-management.component';
import { AnalyticsDashboardComponent } from '../analytics-dashboard/analytics-dashboard.component';
import { TicketManagementComponent } from '../ticket-management/ticket-management.component';
import { ChatWidgetComponent } from '../chat-widget/chat-widget.component';
import {
  LucideAngularModule,
  MessageSquare,
  Settings,
  BarChart3,
  MessageCircle,
  Ticket,
  FileText,
  Building2,
  Users,
  LogOut,
  Eye,
  EyeOff,
  Pencil,
  Trash2,
  Plus,
  X
} from 'lucide-angular';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    DocumentManagementComponent,
    TenantManagementComponent,
    UserManagementComponent,
    AnalyticsDashboardComponent,
    TicketManagementComponent,
    ChatWidgetComponent,
    LucideAngularModule
  ],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  // Tab state
  activeTab: 'faqs' | 'documents' | 'tenants' | 'users' | 'analytics' | 'tickets' | 'chatwidget' = 'faqs';

  faqs: FaqEntry[] = [];
  filteredFaqs: FaqEntry[] = [];
  searchQuery: string = '';
  filterActive: string = 'all'; // 'all' | 'active' | 'inactive'
  isLoading: boolean = false;
  error: string | null = null;

  // Form state
  showForm: boolean = false;
  editingFaq: FaqEntry | null = null;
  formData: FaqEntry = this.getEmptyFaq();

  // Lucide icons
  readonly MessageSquare = MessageSquare;
  readonly Settings = Settings;
  readonly BarChart3 = BarChart3;
  readonly MessageCircle = MessageCircle;
  readonly Ticket = Ticket;
  readonly FileText = FileText;
  readonly Building2 = Building2;
  readonly Users = Users;
  readonly LogOut = LogOut;
  readonly Eye = Eye;
  readonly EyeOff = EyeOff;
  readonly Pencil = Pencil;
  readonly Trash2 = Trash2;
  readonly Plus = Plus;
  readonly X = X;

  constructor(
    private faqService: FaqService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadFaqs();
  }

  switchTab(tab: 'faqs' | 'documents' | 'tenants' | 'users' | 'analytics' | 'tickets' | 'chatwidget'): void {
    this.activeTab = tab;
  }

  loadFaqs(): void {
    this.isLoading = true;
    this.error = null;

    this.faqService.getAllFaqs().subscribe({
      next: (faqs) => {
        this.faqs = faqs.sort((a, b) => {
          // Sort by displayOrder, then by usageCount desc
          if (a.displayOrder !== undefined && b.displayOrder !== undefined) {
            return a.displayOrder - b.displayOrder;
          }
          return (b.usageCount || 0) - (a.usageCount || 0);
        });
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load FAQs. Please try again.';
        this.isLoading = false;
        console.error('Error loading FAQs:', err);
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.faqs];

    // Apply search filter
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(faq =>
        faq.question.toLowerCase().includes(query) ||
        faq.answer.toLowerCase().includes(query) ||
        (faq.tags && faq.tags.some(tag => tag.toLowerCase().includes(query)))
      );
    }

    // Apply active filter
    if (this.filterActive === 'active') {
      filtered = filtered.filter(faq => faq.isActive === true);
    } else if (this.filterActive === 'inactive') {
      filtered = filtered.filter(faq => faq.isActive === false);
    }

    this.filteredFaqs = filtered;
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  openCreateForm(): void {
    this.editingFaq = null;
    this.formData = this.getEmptyFaq();
    this.showForm = true;
  }

  openEditForm(faq: FaqEntry): void {
    this.editingFaq = faq;
    this.formData = { ...faq, tags: faq.tags ? [...faq.tags] : [] };
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editingFaq = null;
    this.formData = this.getEmptyFaq();
  }

  saveFaq(): void {
    if (!this.formData.question?.trim() || !this.formData.answer?.trim()) {
      alert('Question and Answer are required!');
      return;
    }

    this.isLoading = true;

    if (this.editingFaq && this.editingFaq.id) {
      // Update existing FAQ
      this.faqService.updateFaq(this.editingFaq.id, this.formData).subscribe({
        next: () => {
          this.loadFaqs();
          this.closeForm();
        },
        error: (err) => {
          this.error = 'Failed to update FAQ.';
          this.isLoading = false;
          console.error('Error updating FAQ:', err);
        }
      });
    } else {
      // Create new FAQ
      this.faqService.createFaq(this.formData).subscribe({
        next: () => {
          this.loadFaqs();
          this.closeForm();
        },
        error: (err) => {
          this.error = 'Failed to create FAQ.';
          this.isLoading = false;
          console.error('Error creating FAQ:', err);
        }
      });
    }
  }

  deleteFaq(faq: FaqEntry): void {
    if (!faq.id) return;

    if (confirm(`Are you sure you want to delete this FAQ?\n\nQuestion: ${faq.question}`)) {
      this.isLoading = true;

      this.faqService.deleteFaq(faq.id).subscribe({
        next: () => {
          this.loadFaqs();
        },
        error: (err) => {
          this.error = 'Failed to delete FAQ.';
          this.isLoading = false;
          console.error('Error deleting FAQ:', err);
        }
      });
    }
  }

  toggleActive(faq: FaqEntry): void {
    if (!faq.id) return;

    const updatedFaq = { ...faq, isActive: !faq.isActive };

    this.faqService.updateFaq(faq.id, updatedFaq).subscribe({
      next: () => {
        this.loadFaqs();
      },
      error: (err) => {
        this.error = 'Failed to toggle FAQ status.';
        console.error('Error toggling FAQ:', err);
      }
    });
  }

  addTag(): void {
    const tagInput = prompt('Enter a new tag:');
    if (tagInput && tagInput.trim()) {
      if (!this.formData.tags) {
        this.formData.tags = [];
      }
      if (!this.formData.tags.includes(tagInput.trim())) {
        this.formData.tags.push(tagInput.trim());
      }
    }
  }

  removeTag(tag: string): void {
    if (this.formData.tags) {
      this.formData.tags = this.formData.tags.filter(t => t !== tag);
    }
  }

  private getEmptyFaq(): FaqEntry {
    return {
      question: '',
      answer: '',
      tags: [],
      isActive: true,
      displayOrder: 0
    };
  }

  // Role-based access control methods
  canAccessTenantManagement(): boolean {
    return this.authService.hasAccessToTenantManagement();
  }

  canAccessUserManagement(): boolean {
    return this.authService.hasAccessToUserManagement();
  }

  getUserRole(): string | null {
    return this.authService.getRole();
  }

  getUsername(): string | null {
    return this.authService.getUsername();
  }

  getPageTitle(): string {
    const titles: { [key: string]: string } = {
      'analytics': 'Analytics Dashboard',
      'faqs': 'FAQ Management',
      'tickets': 'Support Tickets',
      'documents': 'Document Management',
      'tenants': 'Tenant Management',
      'users': 'User Management',
      'chatwidget': 'Chat Widget'
    };
    return titles[this.activeTab] || 'Dashboard';
  }

  getUserInitials(): string {
    const username = this.getUsername();
    if (!username) return 'U';

    const parts = username.split(/[\s_.-]+/);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return username.substring(0, 2).toUpperCase();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
