import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ChatWidgetComponent } from '../chat-widget/chat-widget.component';
import { WidgetControlService } from '../../services/widget-control.service';
import { LucideAngularModule, MessageCircle, Shield, Zap, Users, CheckCircle, ArrowRight, Sparkles } from 'lucide-angular';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, ChatWidgetComponent, LucideAngularModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent {
  readonly MessageCircle = MessageCircle;
  readonly Shield = Shield;
  readonly Zap = Zap;
  readonly Users = Users;
  readonly CheckCircle = CheckCircle;
  readonly ArrowRight = ArrowRight;
  readonly Sparkles = Sparkles;

  features = [
    {
      icon: this.Zap,
      title: 'Instant Answers',
      description: 'Get immediate responses to your questions 24/7 with our intelligent FAQ system.'
    },
    {
      icon: this.Shield,
      title: 'GDPR Compliant',
      description: 'Your privacy matters. All data processing follows strict GDPR guidelines.'
    },
    {
      icon: this.Users,
      title: 'Human Handoff',
      description: 'Seamlessly connect with a real person when you need personalized assistance.'
    }
  ];

  benefits = [
    'Easy integration into your website',
    'Customizable design and branding',
    'Multi-tenant support',
    'Real-time chat analytics',
    'Document-based knowledge base',
    'Ticket management system'
  ];

  constructor(
    private router: Router,
    private widgetControl: WidgetControlService
  ) {}

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  openChatWidget(): void {
    this.widgetControl.openWidget();
  }
}
