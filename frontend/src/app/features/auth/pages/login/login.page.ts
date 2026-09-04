import { Component, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LucideShieldCheck, LucideUsersRound } from '@lucide/angular';

import { SessionService, SessionUser } from '../../../../core/services/session.service';
import { AuthApiService } from '../../../../core/services/auth-api.service';

type DemoRole = 'STUDENT' | 'ADMIN';

interface DemoAccount {
  role: DemoRole;
  name: string;
  email: string;
  title: string;
  description: string;
}

@Component({
  selector: 'app-login-page',
  imports: [FormsModule, RouterLink, LucideShieldCheck, LucideUsersRound],
  templateUrl: './login.page.html',
  styleUrl: './login.page.scss',
})
export class LoginPage {
  private readonly router = inject(Router);
  private readonly session = inject(SessionService);
  private readonly authApi = inject(AuthApiService);

  protected readonly selectedRole = signal<DemoRole>('STUDENT');
  protected readonly isSubmitting = signal(false);
  protected readonly password = signal('DemoPass123');
  protected readonly error = signal<string | null>(null);

  protected readonly demoAccounts: DemoAccount[] = [
    {
      role: 'STUDENT',
      name: 'Sofía Martínez',
      email: 'student@reservehub.demo',
      title: 'Estudiante',
      description: 'Consulta recursos, reserva laboratorios y revisa tus compromisos.',
    },
    {
      role: 'ADMIN',
      name: 'Daniel Ruiz',
      email: 'admin@reservehub.demo',
      title: 'Administrador',
      description: 'Supervisa ocupación, usuarios y disponibilidad del campus.',
    },
  ];

  protected selectRole(role: DemoRole): void {
    this.selectedRole.set(role);
  }

  protected submit(): void {
    const user = this.demoAccounts.find((account) => account.role === this.selectedRole());
    if (!user) {
      return;
    }

    this.isSubmitting.set(true);
    this.error.set(null);
    this.authApi.login({ email: user.email, password: this.password() }).subscribe({
      next: (response) => {
        this.session.setTokens(response);
        const sessionUser: SessionUser = {
          id: response.userId,
          firstName: user.name.split(' ')[0],
          lastName: user.name.split(' ').slice(1).join(' '),
          email: response.email,
          role: response.role,
        };
        this.session.setCurrentUser(sessionUser);
        this.isSubmitting.set(false);
        void this.router.navigateByUrl(response.role === 'ADMIN' ? '/app/admin' : '/app/resources');
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        this.error.set(error.error?.message || 'No fue posible iniciar sesión.');
      },
    });
  }
}
