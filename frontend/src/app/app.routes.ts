import { Routes } from '@angular/router';

import { roleGuard } from './core/guards/role.guard';
import { AdminDashboardPage } from './features/admin/pages/admin-dashboard/admin-dashboard.page';
import { AdminUsersPage } from './features/admin/pages/admin-users/admin-users.page';
import { LoginPage } from './features/auth/pages/login/login.page';
import { WeeklyCalendarPage } from './features/calendar/pages/weekly-calendar/weekly-calendar.page';
import { MyReservationsPage } from './features/reservations/pages/my-reservations/my-reservations.page';
import { ResourceCatalogPage } from './features/resources/pages/resource-catalog/resource-catalog.page';
import { ResourceDetailPage } from './features/resources/pages/resource-detail/resource-detail.page';
import { ReservationDetailPage } from './features/reservations/pages/reservation-detail/reservation-detail.page';
import { AdminUserDetailPage } from './features/admin/pages/admin-user-detail/admin-user-detail.page';
import { AdminResourcesPage } from './features/admin/pages/admin-resources/admin-resources.page';
import { MainLayout } from './layout/main-layout/main-layout';
import { Error404Page } from './pages/error-404/error-404.page';
import { NotAllowedPage } from './pages/not-allowed/not-allowed.page';
import { NotFoundPage } from './pages/not-found/not-found.page';
import { ProfilePage } from './features/profile/pages/profile/profile.page';
import { RegisterPage } from './features/auth/pages/register/register.page';

export const routes: Routes = [
  {
    path: 'error-404',
    component: Error404Page,
  },
  {
    path: 'not-allowed',
    component: NotAllowedPage,
  },
  {
    path: 'not-found',
    component: NotFoundPage,
  },
  {
    path: 'login',
    component: LoginPage,
  },
  {
    path: 'register',
    component: RegisterPage,
  },
  {
    path: 'app',
    component: MainLayout,
    children: [
      {
        path: 'resources',
        component: ResourceCatalogPage,
        canActivate: [roleGuard],
        data: { role: 'STUDENT' },
      },
      {
        path: 'resources/:id',
        component: ResourceDetailPage,
        canActivate: [roleGuard],
        data: { role: 'STUDENT' },
      },
      {
        path: 'reservations',
        component: MyReservationsPage,
        canActivate: [roleGuard],
        data: { role: 'STUDENT' },
      },
      {
        path: 'reservations/:id',
        component: ReservationDetailPage,
        canActivate: [roleGuard],
        data: { role: 'STUDENT' },
      },
      {
        path: 'calendar',
        component: WeeklyCalendarPage,
        canActivate: [roleGuard],
        data: { role: 'STUDENT' },
      },
      {
        path: 'profile',
        component: ProfilePage,
        canActivate: [roleGuard],
      },
      {
        path: 'admin',
        component: AdminDashboardPage,
        canActivate: [roleGuard],
        data: { role: 'ADMIN' },
      },
      {
        path: 'admin/users',
        component: AdminUsersPage,
        canActivate: [roleGuard],
        data: { role: 'ADMIN' },
      },
      {
        path: 'admin/users/:id',
        component: AdminUserDetailPage,
        canActivate: [roleGuard],
        data: { role: 'ADMIN' },
      },
      {
        path: 'admin/resources',
        component: AdminResourcesPage,
        canActivate: [roleGuard],
        data: { role: 'ADMIN' },
      },
      { path: '', pathMatch: 'full', redirectTo: 'resources' },
    ],
  },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', component: NotFoundPage },
];
