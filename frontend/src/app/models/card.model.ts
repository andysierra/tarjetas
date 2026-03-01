export interface CardResponse {
  maskedPan: string;
  validationNumber: number;
  identifier: string;
  cardholderName: string;
  cardholderId: string;
  phoneNumber: string;
  status: string;
}

export interface CreateCardRequest {
  pan: string;
  cardholderName: string;
  cardholderId: string;
  cardType: string;
  phoneNumber: string;
}

export interface EnrollCardRequest {
  validationNumber: number;
}
