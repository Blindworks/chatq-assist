import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TenantService, TenantDto, CreateTenantRequest } from '../../services/tenant.service';

@Component({
  selector: 'app-tenant-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tenant-management.component.html',
  styleUrls: ['./tenant-management.component.css']
})
export class TenantManagementComponent implements OnInit {
  tenants: TenantDto[] = [];
  loading = false;
  error: string | null = null;
  showCreateForm = false;
  editingTenant: TenantDto | null = null;

  newTenant: CreateTenantRequest = {
    name: '',
    contactEmail: '',
    domain: '',
    maxUsers: 10,
    maxDocuments: 100
  };

  constructor(private tenantService: TenantService) {}

  ngOnInit() {
    this.loadTenants();
  }

  loadTenants() {
    this.loading = true;
    this.error = null;

    this.tenantService.getAllTenants().subscribe({
      next: (data) => {
        this.tenants = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Fehler beim Laden der Tenants: ' + err.message;
        this.loading = false;
      }
    });
  }

  openCreateForm() {
    this.showCreateForm = true;
    this.editingTenant = null;
    this.resetForm();
  }

  openEditForm(tenant: TenantDto) {
    this.editingTenant = tenant;
    this.showCreateForm = true;
    this.newTenant = {
      name: tenant.name,
      contactEmail: tenant.contactEmail,
      domain: tenant.domain || '',
      maxUsers: tenant.maxUsers,
      maxDocuments: tenant.maxDocuments,
      settings: tenant.settings
    };
  }

  closeForm() {
    this.showCreateForm = false;
    this.editingTenant = null;
    this.resetForm();
  }

  resetForm() {
    this.newTenant = {
      name: '',
      contactEmail: '',
      domain: '',
      maxUsers: 10,
      maxDocuments: 100
    };
  }

  saveTenant() {
    if (!this.newTenant.name || !this.newTenant.contactEmail) {
      this.error = 'Name und E-Mail sind erforderlich';
      return;
    }

    this.loading = true;
    this.error = null;

    const request = this.editingTenant
      ? this.tenantService.updateTenant(this.editingTenant.id, this.newTenant)
      : this.tenantService.createTenant(this.newTenant);

    request.subscribe({
      next: () => {
        this.loadTenants();
        this.closeForm();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Fehler beim Speichern: ' + err.error?.message || err.message;
        this.loading = false;
      }
    });
  }

  toggleStatus(tenant: TenantDto) {
    if (!confirm(`${tenant.name} ${tenant.isActive ? 'deaktivieren' : 'aktivieren'}?`)) {
      return;
    }

    this.tenantService.toggleTenantStatus(tenant.id).subscribe({
      next: () => this.loadTenants(),
      error: (err) => {
        this.error = 'Fehler beim Ändern des Status: ' + err.message;
      }
    });
  }

  regenerateApiKey(tenant: TenantDto) {
    if (!confirm(`API-Key für ${tenant.name} neu generieren? Der alte Key wird ungültig!`)) {
      return;
    }

    this.tenantService.regenerateApiKey(tenant.id).subscribe({
      next: () => this.loadTenants(),
      error: (err) => {
        this.error = 'Fehler beim Generieren des API-Keys: ' + err.message;
      }
    });
  }

  deleteTenant(tenant: TenantDto) {
    if (!confirm(`${tenant.name} wirklich löschen? Dies kann nicht rückgängig gemacht werden!`)) {
      return;
    }

    this.tenantService.deleteTenant(tenant.id).subscribe({
      next: () => this.loadTenants(),
      error: (err) => {
        this.error = 'Fehler beim Löschen: ' + err.error?.message || err.message;
      }
    });
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      alert('In Zwischenablage kopiert!');
    });
  }
}
