import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { TransactionService } from '../../services/transaction.service';
import { ToastService } from '../../services/toast.service';
import { TransactionResponse } from '../../models/transaction.model';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.css'
})
export class TransactionsComponent implements OnInit {

  transactions$: Observable<TransactionResponse[]>;
  createForm: FormGroup;

  constructor(
    private txService: TransactionService,
    private toast: ToastService,
    private fb: FormBuilder
  ) {
    this.transactions$ = this.txService.transactions$;

    this.createForm = this.fb.group({
      identifier: ['', Validators.required],
      reference: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
      totalAmount: [null, [Validators.required, Validators.min(0.01)]],
      address: ['', Validators.maxLength(200)]
    });
  }

  ngOnInit(): void {
    this.txService.loadAll();
  }

  onCreate(): void {
    if (this.createForm.invalid) return;

    this.txService.create(this.createForm.value).subscribe({
      next: () => {
        this.toast.success('Compra exitosa');
        this.createForm.reset();
        this.txService.refresh();
      },
      error: err => {
        this.toast.error(err.error?.message || 'Error creando transaccion');
      }
    });
  }

  onCancel(reference: string): void {
    if (!confirm('Desea anular esta transaccion?')) return;

    this.txService.cancel(reference).subscribe({
      next: () => {
        this.toast.success('Transaccion anulada');
        this.txService.refresh();
      },
      error: err => {
        this.toast.error(err.error?.message || 'Error anulando transaccion');
      }
    });
  }
}
