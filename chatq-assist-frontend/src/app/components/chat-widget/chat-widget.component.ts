import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, ChatRequest, ChatResponse } from '../../services/chat.service';

interface Message {
  role: 'user' | 'assistant';
  content: string;
  sources?: any[];
}

@Component({
  selector: 'app-chat-widget',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-widget.component.html',
  styleUrls: ['./chat-widget.component.css']
})
export class ChatWidgetComponent implements OnInit {
  isOpen = false;
  messages: Message[] = [];
  currentMessage = '';
  sessionId: string | null = null;
  isLoading = false;
  theme: 'light' | 'dark' = 'light';

  constructor(private chatService: ChatService) {}

  ngOnInit() {
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

  loadConversationHistory() {
    if (!this.sessionId) return;

    this.chatService.getHistory(this.sessionId).subscribe({
      next: (history) => {
        console.log('Loaded conversation history:', history);
        this.messages = history.map(msg => ({
          role: msg.role === 'USER' ? 'user' : 'assistant',
          content: msg.content,
          sources: []
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
}
