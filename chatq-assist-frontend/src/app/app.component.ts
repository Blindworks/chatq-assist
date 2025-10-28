import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatWidgetComponent } from './components/chat-widget/chat-widget.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ChatWidgetComponent],
  template: `<app-chat-widget></app-chat-widget>`,
  styles: []
})
export class AppComponent {}
