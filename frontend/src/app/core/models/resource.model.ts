export type ResourceType = 'ROOM' | 'LABORATORY' | 'EQUIPMENT';
export type ResourceStatus = 'AVAILABLE' | 'MAINTENANCE' | 'INACTIVE';

export interface Resource {
  id: number;
  name: string;
  description: string | null;
  type: ResourceType;
  capacity: number;
  location: string;
  status: ResourceStatus;
}

export interface ResourceFilters {
  search?: string;
  type?: ResourceType;
  status?: ResourceStatus;
  minCapacity?: number;
}