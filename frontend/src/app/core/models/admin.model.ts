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
  availableResources: number;
  maintenanceResources: number;
  inactiveResources: number;
  byType: {
    ROOM: number;
    LABORATORY: number;
    EQUIPMENT: number;
  };
  averageCapacity: number;
}
