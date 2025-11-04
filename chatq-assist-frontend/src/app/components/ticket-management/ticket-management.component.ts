import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  TicketService,
  SupportTicket,
  TicketStats,
  TicketStatus,
  TicketPriority,
  TicketUpdateRequest
} from '../../services/ticket.service';

@Component({
  selector: 'app-ticket-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ticket-management.component.html',
  styleUrls: ['./ticket-management.component.css']
})
export class TicketManagementComponent implements OnInit {
  tickets: SupportTicket[] = [];
  filteredTickets: SupportTicket[] = [];
  stats: TicketStats = {
    total: 0,
    open: 0,
    inProgress: 0,
    resolved: 0,
    closed: 0
  };

  isLoading = false;
  error: string | null = null;

  // Filters
  searchQuery = '';
  filterStatus: TicketStatus | 'ALL' = 'ALL';
  sortBy: 'createdAt' | 'priority' | 'status' = 'createdAt';
  sortDirection: 'asc' | 'desc' = 'desc';

  // Modal state
  showDetailModal = false;
  selectedTicket: SupportTicket | null = null;
  editForm: TicketUpdateRequest = {};

  // Available options
  readonly statusOptions: TicketStatus[] = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];
  readonly priorityOptions: TicketPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

  constructor(private ticketService: TicketService) {}

  ngOnInit(): void {
    this.loadTickets();
    this.loadStats();
  }

  loadTickets(): void {
    this.isLoading = true;
    this.error = null;

    this.ticketService.getTickets().subscribe({
      next: (tickets) => {
        this.tickets = tickets;
        this.applyFiltersAndSort();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load tickets. Please try again.';
        this.isLoading = false;
        console.error('Error loading tickets:', err);
      }
    });
  }

  loadStats(): void {
    this.ticketService.getTicketStats().subscribe({
      next: (stats) => {
        this.stats = stats;
      },
      error: (err) => {
        console.error('Error loading ticket stats:', err);
      }
    });
  }

  applyFiltersAndSort(): void {
    let filtered = [...this.tickets];

    // Apply search filter
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(ticket =>
        ticket.customerName.toLowerCase().includes(query) ||
        ticket.customerEmail.toLowerCase().includes(query) ||
        (ticket.customerPhone && ticket.customerPhone.toLowerCase().includes(query)) ||
        (ticket.assignedTo && ticket.assignedTo.toLowerCase().includes(query))
      );
    }

    // Apply status filter
    if (this.filterStatus !== 'ALL') {
      filtered = filtered.filter(ticket => ticket.status === this.filterStatus);
    }

    // Apply sorting
    filtered.sort((a, b) => {
      let comparison = 0;

      switch (this.sortBy) {
        case 'createdAt':
          comparison = new Date(a.createdAt || 0).getTime() - new Date(b.createdAt || 0).getTime();
          break;
        case 'priority':
          const priorityOrder = { 'URGENT': 4, 'HIGH': 3, 'MEDIUM': 2, 'LOW': 1 };
          comparison = priorityOrder[a.priority] - priorityOrder[b.priority];
          break;
        case 'status':
          comparison = a.status.localeCompare(b.status);
          break;
      }

      return this.sortDirection === 'desc' ? -comparison : comparison;
    });

    this.filteredTickets = filtered;
  }

  onSearchChange(): void {
    this.applyFiltersAndSort();
  }

  onFilterChange(): void {
    this.applyFiltersAndSort();
  }

  onSortChange(): void {
    this.applyFiltersAndSort();
  }

  toggleSortDirection(): void {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    this.applyFiltersAndSort();
  }

  openTicketDetail(ticket: SupportTicket): void {
    this.selectedTicket = { ...ticket };
    this.editForm = {
      status: ticket.status,
      priority: ticket.priority,
      assignedTo: ticket.assignedTo || '',
      notes: ticket.notes || ''
    };
    this.showDetailModal = true;
  }

  closeDetailModal(): void {
    this.showDetailModal = false;
    this.selectedTicket = null;
    this.editForm = {};
  }

  saveTicket(): void {
    if (!this.selectedTicket || !this.selectedTicket.id) {
      return;
    }

    this.isLoading = true;

    this.ticketService.updateTicket(this.selectedTicket.id, this.editForm).subscribe({
      next: () => {
        this.loadTickets();
        this.loadStats();
        this.closeDetailModal();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to update ticket. Please try again.';
        this.isLoading = false;
        console.error('Error updating ticket:', err);
      }
    });
  }

  deleteTicket(ticket: SupportTicket): void {
    if (!ticket.id) {
      return;
    }

    const confirmMessage = `Are you sure you want to delete this ticket?\n\nCustomer: ${ticket.customerName}\nEmail: ${ticket.customerEmail}`;

    if (confirm(confirmMessage)) {
      this.isLoading = true;

      this.ticketService.deleteTicket(ticket.id).subscribe({
        next: () => {
          this.loadTickets();
          this.loadStats();
          if (this.selectedTicket?.id === ticket.id) {
            this.closeDetailModal();
          }
        },
        error: (err) => {
          this.error = 'Failed to delete ticket. Please try again.';
          this.isLoading = false;
          console.error('Error deleting ticket:', err);
        }
      });
    }
  }

  getStatusClass(status: TicketStatus): string {
    const statusClasses: Record<TicketStatus, string> = {
      'OPEN': 'status-open',
      'IN_PROGRESS': 'status-in-progress',
      'RESOLVED': 'status-resolved',
      'CLOSED': 'status-closed'
    };
    return statusClasses[status];
  }

  getPriorityClass(priority: TicketPriority): string {
    const priorityClasses: Record<TicketPriority, string> = {
      'LOW': 'priority-low',
      'MEDIUM': 'priority-medium',
      'HIGH': 'priority-high',
      'URGENT': 'priority-urgent'
    };
    return priorityClasses[priority];
  }

  getStatusLabel(status: TicketStatus): string {
    const statusLabels: Record<TicketStatus, string> = {
      'OPEN': 'Open',
      'IN_PROGRESS': 'In Progress',
      'RESOLVED': 'Resolved',
      'CLOSED': 'Closed'
    };
    return statusLabels[status];
  }

  formatDateTime(dateString?: string): string {
    if (!dateString) {
      return 'N/A';
    }
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatDate(dateString?: string): string {
    if (!dateString) {
      return 'N/A';
    }
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  clearError(): void {
    this.error = null;
  }
}
