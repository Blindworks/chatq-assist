import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { TenantDto } from './tenant.service';

@Injectable({
  providedIn: 'root'
})
export class TenantContextService {
  private selectedTenantSubject = new BehaviorSubject<TenantDto | null>(null);
  public selectedTenant$: Observable<TenantDto | null> = this.selectedTenantSubject.asObservable();

  constructor() {
    // Load from localStorage if available
    const savedTenantId = localStorage.getItem('selectedTenantId');
    if (savedTenantId) {
      // We'll load the full tenant data when the app initializes
      // For now, just store the tenantId
    }
  }

  setSelectedTenant(tenant: TenantDto | null): void {
    this.selectedTenantSubject.next(tenant);
    if (tenant) {
      localStorage.setItem('selectedTenantId', tenant.tenantId);
    } else {
      localStorage.removeItem('selectedTenantId');
    }
  }

  getSelectedTenant(): TenantDto | null {
    return this.selectedTenantSubject.value;
  }

  getSelectedTenantId(): string {
    const tenant = this.selectedTenantSubject.value;
    return tenant ? tenant.tenantId : 'default-tenant';
  }

  clearSelectedTenant(): void {
    this.selectedTenantSubject.next(null);
    localStorage.removeItem('selectedTenantId');
  }
}
