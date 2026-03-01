import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [AsyncPipe],
  template: `
    @if (toast$ | async; as toast) {
      <div class="toast" [class]="'toast show ' + toast.type">
        {{ toast.message }}
      </div>
    }
  `,
  styles: [`
    .toast {
      position: fixed;
      bottom: 2rem;
      right: 2rem;
      padding: 0.8rem 1.5rem;
      border-radius: 6px;
      color: white;
      font-weight: 600;
      font-size: 0.9rem;
      z-index: 200;
      max-width: 400px;
    }
    .success { background: #2e7d32; }
    .error { background: #c62828; }
  `]
})
export class ToastComponent {
  private toastService = inject(ToastService);
  toast$ = this.toastService.toast$;
}
