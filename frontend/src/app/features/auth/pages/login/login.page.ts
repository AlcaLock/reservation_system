import { Component, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LucideShieldCheck, LucideUsersRound } from '@lucide/angular';

import { SessionService, SessionUser } from '../../../../core/services/session.service';
import { AuthApiService } from '../../../../core/services/auth-api.service';
import { UserApiService } from '../../../../core/services/user-api.service';

type DemoRole = 'STUDENT' | 'ADMIN';

interface DemoAccount {
  role: DemoRole;
  name: string;
  email: string;
  title: string;
  description: string;
  password: string;
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
  private readonly userApi = inject(UserApiService);

  protected readonly isSubmitting = signal(false);
  protected readonly email = signal('');
  protected readonly password = signal('');
  protected readonly error = signal<string | null>(null);

  protected readonly demoAccounts: DemoAccount[] = [
    {
      role: 'STUDENT',
      name: 'Sofía Martínez',
      email: 'student@reservehub.demo',
      title: 'Estudiante',
      description: 'Consulta recursos, reserva laboratorios y revisa tus compromisos.',
      password: 'DemoPass123',
    },
    {
      role: 'ADMIN',
      name: 'Daniel Ruiz',
      email: 'admin@reservehub.demo',
      title: 'Administrador',
      description: 'Supervisa ocupación, usuarios y disponibilidad del campus.',
      password: 'DemoPass123',
    },
  ];

  protected useDemoAccount(account: DemoAccount): void {
    this.email.set(account.email);
    this.password.set(account.password);
    this.error.set(null);
  }

  protected submit(): void {
    const email = this.email().trim();
    if (!email || !this.password()) {
      this.error.set('Ingresa tu correo y contraseña para continuar.');
      return;
    }

    this.isSubmitting.set(true);
    this.error.set(null);
    this.authApi.login({ email, password: this.password() }).subscribe({
      next: (response) => {
        this.session.setTokens(response);
        this.userApi.getCurrent().subscribe({
          next: (user) => {
            const sessionUser: SessionUser = {
              id: user.id,
              firstName: user.firstName,
              lastName: user.lastName,
              email: user.email,
              role: user.role,
            };
            this.session.setCurrentUser(sessionUser);
            this.isSubmitting.set(false);
            void this.router.navigateByUrl(response.role === 'ADMIN' ? '/app/admin' : '/app/resources');
          },
          error: () => {
            this.session.logOut();
            this.isSubmitting.set(false);
            this.error.set('La sesión se creó, pero no fue posible cargar tu perfil.');
          },
        });
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        this.error.set(error.error?.message || 'No fue posible iniciar sesión.');
      },
    });
  }
}
