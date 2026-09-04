export interface DailyReservationCount {
  date: string;
  count: number;
}

export interface ResourceUsageStat {
  resourceId: number;
  resourceName: string;
  usageCount: number;
}

export interface AdminDashboardSummary {
  totalUsers: number;
  enabledUsers: number;
  totalResources: number;
  availableResources: number;
  totalReservations: number;
  activeReservations: number;
  currentOccupancyPercentage: number;
  reservationsByDay: DailyReservationCount[];
  mostUsedResources: ResourceUsageStat[];
  recentAlerts: string[];
}

export interface ResourceStatisticsSummary {
  totalResources: number;
  resourcesWithoutReservations: number;
  resourcesByStatus: ResourceStatusCount[];
  resourcesByType: ResourceTypeCount[];
  mostUsedResources: ResourceUsageStat[];
}

export interface ResourceStatusCount { status: 'AVAILABLE' | 'MAINTENANCE' | 'INACTIVE'; resources: number; }
export interface ResourceTypeCount { type: 'ROOM' | 'LABORATORY' | 'EQUIPMENT'; resources: number; }
