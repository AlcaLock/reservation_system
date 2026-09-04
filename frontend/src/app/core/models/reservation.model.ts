export interface CreateReservationRequest {
  userId: number;
  resourceId: number;
  startTime: string;
  endTime: string;
  purpose: string;
}

export interface Reservation {
  id: number;
  userId: number;
  resourceId: number;
  startTime: string;
  endTime: string;
  purpose: string;
  status: 'ACTIVE' | 'CANCELLED' | 'COMPLETED';
}