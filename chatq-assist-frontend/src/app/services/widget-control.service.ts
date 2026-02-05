import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WidgetControlService {
  private widgetOpenSubject = new BehaviorSubject<boolean>(false);
  public widgetOpen$ = this.widgetOpenSubject.asObservable();

  openWidget(): void {
    this.widgetOpenSubject.next(true);
  }

  closeWidget(): void {
    this.widgetOpenSubject.next(false);
  }

  toggleWidget(): void {
    this.widgetOpenSubject.next(!this.widgetOpenSubject.value);
  }
}
