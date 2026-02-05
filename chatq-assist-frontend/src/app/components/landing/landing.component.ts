import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ChatWidgetComponent } from '../chat-widget/chat-widget.component';
import { WidgetControlService } from '../../services/widget-control.service';
import { LucideAngularModule, MessageCircle, Shield, Zap, Users, CheckCircle, ArrowRight, Sparkles, Languages } from 'lucide-angular';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, ChatWidgetComponent, LucideAngularModule, TranslateModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent implements OnInit {
  readonly MessageCircle = MessageCircle;
  readonly Shield = Shield;
  readonly Zap = Zap;
  readonly Users = Users;
  readonly CheckCircle = CheckCircle;
  readonly ArrowRight = ArrowRight;
  readonly Sparkles = Sparkles;
  readonly Languages = Languages;

  currentLanguage: string = 'en';
  availableLanguages = [
    { code: 'en', label: 'English' },
    { code: 'de', label: 'Deutsch' }
  ];

  features = [
    {
      icon: this.Zap,
      titleKey: 'features.instantAnswers.title',
      descriptionKey: 'features.instantAnswers.description'
    },
    {
      icon: this.Shield,
      titleKey: 'features.gdprCompliant.title',
      descriptionKey: 'features.gdprCompliant.description'
    },
    {
      icon: this.Users,
      titleKey: 'features.humanHandoff.title',
      descriptionKey: 'features.humanHandoff.description'
    }
  ];

  benefitKeys = [
    'benefits.list.integration',
    'benefits.list.customizable',
    'benefits.list.multiTenant',
    'benefits.list.analytics',
    'benefits.list.knowledgeBase',
    'benefits.list.ticketing'
  ];

  constructor(
    private router: Router,
    private widgetControl: WidgetControlService,
    public translate: TranslateService
  ) {
    // Set default language
    translate.setDefaultLang('en');
    // Try to get saved language or use browser language
    const savedLang = localStorage.getItem('language');
    const browserLang = translate.getBrowserLang();
    this.currentLanguage = savedLang || (browserLang?.match(/en|de/) ? browserLang : 'en');
    translate.use(this.currentLanguage);
  }

  ngOnInit(): void {}

  switchLanguage(lang: string): void {
    this.currentLanguage = lang;
    this.translate.use(lang);
    localStorage.setItem('language', lang);
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  openChatWidget(): void {
    this.widgetControl.openWidget();
  }
}
