/**
 * ChatQ Assist - Embeddable Chat Widget
 * Version: 1.0.0
 *
 * Usage:
 * <script>
 *   window.chatqConfig = {
 *     tenantId: 'your-tenant-id',
 *     apiKey: 'your-api-key',
 *     primaryColor: '#007bff',
 *     companyName: 'Your Company',
 *     welcomeMessage: 'How can we help you?'
 *   };
 * </script>
 * <script src="https://your-domain.com/chatq-widget.js"></script>
 */

(function() {
  'use strict';

  // Default configuration
  const defaultConfig = {
    tenantId: 'default-tenant',
    apiUrl: 'http://localhost:8080/api',
    primaryColor: '#667eea',
    secondaryColor: '#764ba2',
    companyName: 'ChatQ Assist',
    welcomeMessage: 'How can we help you?',
    placeholderText: 'Type your message...',
    position: 'bottom-right',
    showLogo: true,
    showThemeToggle: true,
    enableFeedback: true
  };

  // Merge user config with defaults
  const config = Object.assign({}, defaultConfig, window.chatqConfig || {});

  // Validate required fields
  if (!config.tenantId) {
    console.error('ChatQ Widget: tenantId is required in chatqConfig');
    return;
  }

  // Create widget container
  const widgetContainer = document.createElement('div');
  widgetContainer.id = 'chatq-widget-container';
  document.body.appendChild(widgetContainer);

  // Load Angular widget bundle
  function loadWidget() {
    // In production, this would load the compiled Angular bundle
    // For now, we'll create a simple iframe that points to the Angular app

    const iframe = document.createElement('iframe');
    iframe.id = 'chatq-widget-iframe';
    iframe.style.cssText = `
      position: fixed;
      ${config.position.includes('bottom') ? 'bottom: 24px;' : 'top: 24px;'}
      ${config.position.includes('right') ? 'right: 24px;' : 'left: 24px;'}
      width: 380px;
      height: 600px;
      border: none;
      z-index: 999999;
      border-radius: 16px;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      display: none;
    `;

    // Build URL with config parameters
    const params = new URLSearchParams();
    Object.keys(config).forEach(key => {
      if (config[key] !== undefined && config[key] !== null) {
        params.append(key, config[key]);
      }
    });

    iframe.src = `http://localhost:4200?${params.toString()}`;
    widgetContainer.appendChild(iframe);

    // Create floating button
    createFloatingButton(iframe);
  }

  function createFloatingButton(iframe) {
    const button = document.createElement('button');
    button.id = 'chatq-widget-button';
    button.innerHTML = `
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
      </svg>
    `;

    button.style.cssText = `
      position: fixed;
      ${config.position.includes('bottom') ? 'bottom: 24px;' : 'top: 24px;'}
      ${config.position.includes('right') ? 'right: 24px;' : 'left: 24px;'}
      width: 60px;
      height: 60px;
      border-radius: 50%;
      border: none;
      cursor: pointer;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      transition: all 0.3s ease;
      z-index: 999999;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, ${config.primaryColor} 0%, ${config.secondaryColor} 100%);
      color: white;
    `;

    button.addEventListener('click', function() {
      const isOpen = iframe.style.display !== 'none';
      iframe.style.display = isOpen ? 'none' : 'block';
      button.style.display = isOpen ? 'flex' : 'none';
    });

    button.addEventListener('mouseenter', function() {
      button.style.transform = 'scale(1.1)';
      button.style.boxShadow = '0 6px 16px rgba(0, 0, 0, 0.2)';
    });

    button.addEventListener('mouseleave', function() {
      button.style.transform = 'scale(1)';
      button.style.boxShadow = '0 4px 12px rgba(0, 0, 0, 0.15)';
    });

    widgetContainer.appendChild(button);

    // Listen for messages from iframe to close widget
    window.addEventListener('message', function(event) {
      if (event.data.type === 'chatq-close-widget') {
        iframe.style.display = 'none';
        button.style.display = 'flex';
      }
    });
  }

  // Initialize widget when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', loadWidget);
  } else {
    loadWidget();
  }

  // Expose API for programmatic control
  window.ChatQWidget = {
    open: function() {
      const iframe = document.getElementById('chatq-widget-iframe');
      const button = document.getElementById('chatq-widget-button');
      if (iframe && button) {
        iframe.style.display = 'block';
        button.style.display = 'none';
      }
    },
    close: function() {
      const iframe = document.getElementById('chatq-widget-iframe');
      const button = document.getElementById('chatq-widget-button');
      if (iframe && button) {
        iframe.style.display = 'none';
        button.style.display = 'flex';
      }
    },
    toggle: function() {
      const iframe = document.getElementById('chatq-widget-iframe');
      if (iframe) {
        const isOpen = iframe.style.display !== 'none';
        this[isOpen ? 'close' : 'open']();
      }
    }
  };
})();
