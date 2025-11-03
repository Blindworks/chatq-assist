import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TenantDto } from '../../services/tenant.service';
import { TenantSettings, WidgetConfig } from '../../models/widget-config.model';
import { ChatWidgetComponent } from '../chat-widget/chat-widget.component';

@Component({
  selector: 'app-widget-customizer',
  standalone: true,
  imports: [CommonModule, FormsModule, ChatWidgetComponent],
  templateUrl: './widget-customizer.component.html',
  styleUrls: ['./widget-customizer.component.css']
})
export class WidgetCustomizerComponent implements OnInit {
  @Input() tenant!: TenantDto;

  showPreview: boolean = false;
  previewConfig: WidgetConfig | null = null;

  widgetSettings: TenantSettings = {
    widget: {
      position: 'bottom-right',
      primaryColor: '#667eea',
      secondaryColor: '#764ba2',
      showLogo: true,
      enableFeedback: true
    },
    companyName: '',
    welcomeMessage: 'How can we help you?',
    logoUrl: ''
  };

  embedCode: string = '';
  showEmbedCode: boolean = false;

  ngOnInit() {
    this.loadTenantSettings();
    this.generateEmbedCode();
  }

  loadTenantSettings() {
    if (this.tenant.settings) {
      try {
        const settings = typeof this.tenant.settings === 'string'
          ? JSON.parse(this.tenant.settings)
          : this.tenant.settings;

        this.widgetSettings = {
          ...this.widgetSettings,
          ...settings,
          widget: {
            ...this.widgetSettings.widget,
            ...(settings.widget || {})
          }
        };
      } catch (e) {
        console.error('Failed to parse tenant settings:', e);
      }
    }

    // Use tenant name as fallback for companyName
    if (!this.widgetSettings.companyName) {
      this.widgetSettings.companyName = this.tenant.name;
    }
  }

  generateEmbedCode() {
    const config = {
      tenantId: this.tenant.tenantId,
      apiKey: this.tenant.apiKey,
      ...this.widgetSettings,
      ...this.widgetSettings.widget
    };

    this.embedCode = `<!-- ChatQ Assist Widget -->
<script>
  window.chatqConfig = ${JSON.stringify(config, null, 2)};
</script>
<script src="https://your-domain.com/chatq-widget.js"></script>
<!-- End ChatQ Assist Widget -->`;
  }

  copyEmbedCode() {
    navigator.clipboard.writeText(this.embedCode).then(() => {
      alert('Embed code copied to clipboard!');
    }).catch(err => {
      console.error('Failed to copy:', err);
    });
  }

  saveSettings() {
    // This would call the tenant service to update settings
    console.log('Saving widget settings:', this.widgetSettings);
    this.generateEmbedCode();
    alert('Widget settings saved! (Note: Backend integration pending)');
  }

  previewWidget() {
    // Show inline preview with the actual Angular widget component
    this.previewConfig = {
      tenantId: this.tenant.tenantId,
      apiKey: this.tenant.apiKey,
      ...this.widgetSettings,
      primaryColor: this.widgetSettings.widget?.primaryColor,
      secondaryColor: this.widgetSettings.widget?.secondaryColor,
      position: this.widgetSettings.widget?.position,
      showLogo: this.widgetSettings.widget?.showLogo,
      showThemeToggle: this.widgetSettings.widget?.showThemeToggle,
      enableFeedback: this.widgetSettings.widget?.enableFeedback
    };

    this.showPreview = true;
  }

  closePreview() {
    this.showPreview = false;
    this.previewConfig = null;
  }
}
