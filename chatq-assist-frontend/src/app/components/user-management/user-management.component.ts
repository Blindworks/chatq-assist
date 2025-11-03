import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserManagementService, UserDto, CreateUserRequest } from '../../services/user-management.service';
import { TenantService, TenantDto } from '../../services/tenant.service';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: UserDto[] = [];
  tenants: TenantDto[] = [];
  loading = false;
  error: string | null = null;
  showCreateForm = false;
  editingUser: UserDto | null = null;
  selectedTenantFilter = '';

  newUser: CreateUserRequest = {
    username: '',
    password: '',
    email: '',
    role: 'TENANT_USER',
    tenantId: '',
    enabled: true
  };

  roles = [
    { value: 'SUPER_ADMIN', label: 'Super Admin' },
    { value: 'TENANT_ADMIN', label: 'Tenant Admin' },
    { value: 'TENANT_USER', label: 'Tenant User' },
    { value: 'ADMIN', label: 'Admin (Legacy)' },
    { value: 'USER', label: 'User (Legacy)' }
  ];

  constructor(
    private userService: UserManagementService,
    private tenantService: TenantService
  ) {}

  ngOnInit() {
    this.loadTenants();
    this.loadUsers();
  }

  loadTenants() {
    this.tenantService.getAllTenants().subscribe({
      next: (data) => {
        this.tenants = data;
      },
      error: (err) => {
        console.error('Fehler beim Laden der Tenants:', err);
      }
    });
  }

  loadUsers() {
    this.loading = true;
    this.error = null;

    const request = this.selectedTenantFilter
      ? this.userService.getUsersByTenant(this.selectedTenantFilter)
      : this.userService.getAllUsers();

    request.subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Fehler beim Laden der Benutzer: ' + err.message;
        this.loading = false;
      }
    });
  }

  openCreateForm() {
    this.showCreateForm = true;
    this.editingUser = null;
    this.resetForm();
  }

  openEditForm(user: UserDto) {
    this.editingUser = user;
    this.showCreateForm = true;
    this.newUser = {
      username: user.username,
      password: '', // Don't populate password
      email: user.email || '',
      role: user.role,
      tenantId: user.tenantId,
      enabled: user.enabled
    };
  }

  closeForm() {
    this.showCreateForm = false;
    this.editingUser = null;
    this.resetForm();
  }

  resetForm() {
    this.newUser = {
      username: '',
      password: '',
      email: '',
      role: 'TENANT_USER',
      tenantId: this.tenants.length > 0 ? this.tenants[0].tenantId : '',
      enabled: true
    };
  }

  saveUser() {
    if (!this.newUser.username || !this.newUser.tenantId) {
      this.error = 'Benutzername und Tenant sind erforderlich';
      return;
    }

    if (!this.editingUser && !this.newUser.password) {
      this.error = 'Passwort ist für neue Benutzer erforderlich';
      return;
    }

    this.loading = true;
    this.error = null;

    const request = this.editingUser
      ? this.userService.updateUser(this.editingUser.id, this.newUser)
      : this.userService.createUser(this.newUser);

    request.subscribe({
      next: () => {
        this.loadUsers();
        this.closeForm();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Fehler beim Speichern: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }

  toggleStatus(user: UserDto) {
    if (!confirm(`${user.username} ${user.enabled ? 'deaktivieren' : 'aktivieren'}?`)) {
      return;
    }

    this.userService.toggleUserStatus(user.id).subscribe({
      next: () => this.loadUsers(),
      error: (err) => {
        this.error = 'Fehler beim Ändern des Status: ' + err.message;
      }
    });
  }

  deleteUser(user: UserDto) {
    if (!confirm(`${user.username} wirklich löschen? Dies kann nicht rückgängig gemacht werden!`)) {
      return;
    }

    this.userService.deleteUser(user.id).subscribe({
      next: () => this.loadUsers(),
      error: (err) => {
        this.error = 'Fehler beim Löschen: ' + (err.error?.message || err.message);
      }
    });
  }

  onTenantFilterChange() {
    this.loadUsers();
  }

  getRoleLabel(role: string): string {
    const roleObj = this.roles.find(r => r.value === role);
    return roleObj ? roleObj.label : role;
  }

  getRoleBadgeClass(role: string): string {
    switch (role) {
      case 'SUPER_ADMIN': return 'role-super-admin';
      case 'TENANT_ADMIN': return 'role-tenant-admin';
      case 'TENANT_USER': return 'role-tenant-user';
      default: return 'role-legacy';
    }
  }
}
