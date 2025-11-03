import { Component, OnInit, OnChanges, SimpleChanges, Input, ElementRef, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, ChatRequest, ChatResponse } from '../../services/chat.service';
import { WidgetConfig } from '../../models/widget-config.model';

interface Message {
  role: 'user' | 'assistant';
  content: string;
  sources?: any[];
  id?: number; // Message ID for feedback
  feedbackGiven?: 'POSITIVE' | 'NEGATIVE' | null;
}

@Component({
  selector: 'app-chat-widget',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-widget.component.html',
  styleUrls: ['./chat-widget.component.css']
})
export class ChatWidgetComponent implements OnInit, OnChanges {
  @Input() config: WidgetConfig = {
    tenantId: 'default-tenant',
    primaryColor: '#007bff',
    secondaryColor: '#6c757d',
    companyName: 'ChatQ Assist',
    welcomeMessage: 'How can we help you?',
    placeholderText: 'Type your message...',
    position: 'bottom-right',
    showLogo: true,
    showThemeToggle: true,
    enableFeedback: true
  };

  isOpen = false;
  messages: Message[] = [];
  currentMessage = '';
  sessionId: string | null = null;
  isLoading = false;
  theme: 'light' | 'dark' = 'light';

  constructor(
    private chatService: ChatService,
    private elementRef: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit() {
    // Load config from window.chatqConfig if embedded
    if (typeof window !== 'undefined' && (window as any).chatqConfig) {
      this.config = { ...this.config, ...(window as any).chatqConfig };
    }

    // Apply custom CSS variables for branding
    this.applyBranding();

    // Check for saved session
    this.sessionId = localStorage.getItem('chatq-session-id');

    // Load conversation history if session exists
    if (this.sessionId) {
      this.loadConversationHistory();
    }

    // Check system theme preference
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      this.theme = 'dark';
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    // Re-apply branding when config changes
    if (changes['config']) {
      console.log('Config changed, reapplying branding:', this.config);
      this.applyBranding();
    }
  }

  applyBranding() {
    const hostElement = this.elementRef.nativeElement;

    if (this.config.primaryColor) {
      this.renderer.setStyle(hostElement, '--chatq-primary-color', this.config.primaryColor);
    }
    if (this.config.secondaryColor) {
      this.renderer.setStyle(hostElement, '--chatq-secondary-color', this.config.secondaryColor);
    }
    if (this.config.headerBackgroundColor) {
      this.renderer.setStyle(hostElement, '--chatq-header-bg', this.config.headerBackgroundColor);
    }
    if (this.config.headerTextColor) {
      this.renderer.setStyle(hostElement, '--chatq-header-text', this.config.headerTextColor);
    }
    if (this.config.userMessageColor) {
      this.renderer.setStyle(hostElement, '--chatq-user-msg-bg', this.config.userMessageColor);
    }
    if (this.config.botMessageColor) {
      this.renderer.setStyle(hostElement, '--chatq-bot-msg-bg', this.config.botMessageColor);
    }

    console.log('Applied branding with colors:', {
      primary: this.config.primaryColor,
      secondary: this.config.secondaryColor
    });
  }

  loadConversationHistory() {
    if (!this.sessionId) return;

    this.chatService.getHistory(this.sessionId).subscribe({
      next: (history) => {
        console.log('Loaded conversation history:', history);
        this.messages = history.map(msg => ({
          role: msg.role === 'USER' ? 'user' : 'assistant',
          content: msg.content,
          sources: [],
          id: msg.id,
          feedbackGiven: null
        }));
      },
      error: (error) => {
        console.error('Failed to load conversation history:', error);
        // Clear invalid session
        localStorage.removeItem('chatq-session-id');
        this.sessionId = null;
      }
    });
  }

  toggleWidget() {
    this.isOpen = !this.isOpen;
  }

  toggleTheme() {
    this.theme = this.theme === 'light' ? 'dark' : 'light';
  }

  sendMessage() {
    if (!this.currentMessage.trim() || this.isLoading) {
      return;
    }

    const userMessage: Message = {
      role: 'user',
      content: this.currentMessage
    };
    this.messages.push(userMessage);

    const request: ChatRequest = {
      question: this.currentMessage,
      sessionId: this.sessionId || undefined
    };

    this.currentMessage = '';
    this.isLoading = true;

    // Add placeholder for assistant response (will be filled with streaming tokens)
    const assistantMessage: Message = {
      role: 'assistant',
      content: '',
      sources: []
    };
    this.messages.push(assistantMessage);

    // Use streaming API
    this.chatService.sendMessageStreaming(request).subscribe({
      next: (streamEvent) => {
        if (streamEvent.type === 'token' && streamEvent.token) {
          // Append token exactly as received from backend
          console.log('Received token:', JSON.stringify(streamEvent.token));
          assistantMessage.content += streamEvent.token;
        } else if (streamEvent.type === 'metadata' && streamEvent.metadata) {
          // Update session and metadata
          this.sessionId = streamEvent.metadata.sessionId;
          localStorage.setItem('chatq-session-id', streamEvent.metadata.sessionId);

          if (streamEvent.metadata.sources) {
            assistantMessage.sources = streamEvent.metadata.sources;
          }

          if (streamEvent.metadata.handoffTriggered) {
            this.messages.push({
              role: 'assistant',
              content: 'Ich verbinde Sie mit einem Mitarbeiter.'
            });
          }
        } else if (streamEvent.type === 'messageId' && streamEvent.messageId) {
          // Store message ID for feedback
          assistantMessage.id = streamEvent.messageId;
          assistantMessage.feedbackGiven = null;
        } else if (streamEvent.type === 'complete') {
          this.isLoading = false;
        } else if (streamEvent.type === 'error') {
          console.error('Streaming error:', streamEvent.error);
          assistantMessage.content = 'Sorry, I encountered an error. Please try again.';
          this.isLoading = false;
        }
      },
      error: (error) => {
        console.error('Chat error:', error);
        assistantMessage.content = 'Sorry, I encountered an error. Please try again.';
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  handleKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  giveFeedback(message: Message, feedbackType: 'POSITIVE' | 'NEGATIVE') {
    if (!message.id) {
      console.warn('Cannot give feedback: message has no ID');
      return;
    }

    // Prevent duplicate feedback
    if (message.feedbackGiven) {
      console.log('Feedback already given for this message');
      return;
    }

    this.chatService.submitFeedback({
      messageId: message.id,
      feedbackType: feedbackType
    }).subscribe({
      next: () => {
        message.feedbackGiven = feedbackType;
        console.log('Feedback submitted successfully:', feedbackType);
      },
      error: (error) => {
        console.error('Failed to submit feedback:', error);
      }
    });
  }
}
