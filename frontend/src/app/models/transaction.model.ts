export interface TransactionResponse {
  reference: string;
  status: string;
  totalAmount: number;
  address: string;
}

export interface CreateTransactionRequest {
  identifier: string;
  reference: string;
  totalAmount: number;
  address: string;
}
