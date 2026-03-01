import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { CardService } from '../../services/card.service';
import { ToastService } from '../../services/toast.service';
import { CardResponse } from '../../models/card.model';

@Component({
  selector: 'app-cards',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cards.component.html',
  styleUrl: './cards.component.css'
})
export class CardsComponent implements OnInit {

  cards$: Observable<CardResponse[]>;
  createForm: FormGroup;
  enrollForm: FormGroup;
  showEnrollModal = false;
  enrollIdentifier = '';

  constructor(
    private cardService: CardService,
    private toast: ToastService,
    private fb: FormBuilder
  ) {
    this.cards$ = this.cardService.cards$;

    this.createForm = this.fb.group({
      pan: ['', [Validators.required, Validators.pattern(/^\d{16,19}$/)]],
      cardholderName: ['', [Validators.required, Validators.maxLength(150)]],
      cardholderId: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(15)]],
      cardType: ['', Validators.required],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]]
    });

    this.enrollForm = this.fb.group({
      validationNumber: [null, [Validators.required, Validators.min(1), Validators.max(100)]]
    });
  }

  ngOnInit(): void {
    this.cardService.loadAll();
  }

  onCreate(): void {
    if (this.createForm.invalid) return;

    this.cardService.create(this.createForm.value).subscribe({
      next: res => {
        this.toast.success(`Tarjeta creada. Numero de validacion: ${res.data.validationNumber}`);
        this.createForm.reset();
        this.cardService.refresh();
      },
      error: err => {
        this.toast.error(err.error?.message || 'Error creando tarjeta');
      }
    });
  }

  openEnroll(identifier: string): void {
    this.enrollIdentifier = identifier;
    this.enrollForm.reset();
    this.showEnrollModal = true;
  }

  closeEnroll(): void {
    this.showEnrollModal = false;
  }

  onEnroll(): void {
    if (this.enrollForm.invalid) return;

    this.cardService.enroll(this.enrollIdentifier, this.enrollForm.value).subscribe({
      next: () => {
        this.toast.success('Tarjeta enrolada');
        this.closeEnroll();
        this.cardService.refresh();
      },
      error: err => {
        this.toast.error(err.error?.message || 'Error enrolando tarjeta');
      }
    });
  }

  onDelete(identifier: string): void {
    if (!confirm('Desea eliminar esta tarjeta?')) return;

    this.cardService.delete(identifier).subscribe({
      next: () => {
        this.toast.success('Tarjeta eliminada');
        this.cardService.refresh();
      },
      error: err => {
        this.toast.error(err.error?.message || 'Error eliminando tarjeta');
      }
    });
  }
}
