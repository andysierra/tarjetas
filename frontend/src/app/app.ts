import { Component } from '@angular/core';
import { CardsComponent } from './components/cards/cards.component';
import { TransactionsComponent } from './components/transactions/transactions.component';
import { ToastComponent } from './components/shared/toast.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CardsComponent, TransactionsComponent, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
