export interface WidgetConfig {
  tenantId: string;
  apiKey?: string;

  // Branding
  primaryColor?: string;
  secondaryColor?: string;
  headerBackgroundColor?: string;
  headerTextColor?: string;
  userMessageColor?: string;
  botMessageColor?: string;
  logoUrl?: string;
  companyName?: string;
  welcomeMessage?: string;
  placeholderText?: string;

  // Positioning
  position?: 'bottom-right' | 'bottom-left' | 'top-right' | 'top-left';

  // Features
  showLogo?: boolean;
  showThemeToggle?: boolean;
  enableFeedback?: boolean;

  // API endpoint (for custom deployments)
  apiUrl?: string;
}

export interface TenantSettings {
  theme?: string;
  brandColor?: string;
  logoUrl?: string;
  companyName?: string;
  welcomeMessage?: string;
  widget?: {
    position?: string;
    primaryColor?: string;
    secondaryColor?: string;
    showLogo?: boolean;
    enableFeedback?: boolean;
  };
}
