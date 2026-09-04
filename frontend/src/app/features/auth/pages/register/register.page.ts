import { Component, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthApiService } from '../../../../core/services/auth-api.service';
import { SessionService } from '../../../../core/services/session.service';

@Component({
  selector: 'app-register-page',
  imports: [FormsModule, RouterLink],
  template: `
    <main class="register-shell">
      <section class="register-panel">
        <p class="eyebrow">ReserveHub</p>
        <h1>Crear cuenta</h1>
        <p class="lead">Regístrate para consultar recursos y gestionar tus reservas.</p>
        @if (error(); as message) { <p class="feedback error" role="alert">{{ message }}</p> }
        <form (ngSubmit)="submit()">
          <label>Nombre<input name="firstName" [ngModel]="firstName()" (ngModelChange)="firstName.set($event)" required maxlength="80" /></label>
          <label>Apellido<input name="lastName" [ngModel]="lastName()" (ngModelChange)="lastName.set($event)" required maxlength="80" /></label>
          <label>Correo electrónico<input name="email" type="email" [ngModel]="email()" (ngModelChange)="email.set($event)" required maxlength="150" /></label>
          <label>Contraseña<input name="password" type="password" [ngModel]="password()" (ngModelChange)="password.set($event)" required minlength="8" maxlength="72" /></label>
          <button type="submit" [disabled]="isSubmitting()">{{ isSubmitting() ? 'Creando cuenta...' : 'Crear cuenta' }}</button>
        </form>
        <a routerLink="/login">Ya tengo una cuenta</a>
      </section>
    </main>
  `,
  styles: [`
    :host { display: block; }
    .register-shell { min-height: 100vh; display: grid; place-items: center; padding: 28px 20px; background: var(--page); }
    .register-panel { width: min(100%, 480px); padding: 34px; border: 1px solid var(--line); border-radius: 8px; background: var(--surface); box-shadow: 0 18px 48px rgba(31, 42, 58, .1); }
    .eyebrow { margin: 0 0 8px; color: var(--primary); font-size: .75rem; font-weight: 800; letter-spacing: .1em; text-transform: uppercase; }
    h1 { margin: 0; color: var(--ink); font-size: 2.3rem; }
    .lead { margin: 12px 0 24px; color: var(--muted); line-height: 1.5; }
    form { display: grid; gap: 16px; }
    label { display: grid; gap: 7px; color: var(--muted); font-size: .78rem; font-weight: 700; }
    input { min-height: 44px; padding: 0 12px; border: 1px solid var(--line); border-radius: 5px; font: inherit; }
    button { min-height: 46px; border: 0; border-radius: 5px; background: var(--primary); color: #fff; font: inherit; font-weight: 700; cursor: pointer; }
    button:disabled { opacity: .7; cursor: wait; }
    a { display: block; margin-top: 20px; color: var(--primary); text-align: center; font-size: .86rem; font-weight: 700; }
    .feedback { padding: 12px; border-radius: 5px; font-size: .84rem; }
    .feedback.error { border: 1px solid #e9c3bd; background: #fff5f3; color: var(--danger); }
  `],
})
export class RegisterPage {
  private readonly authApi = inject(AuthApiService);
  private readonly session = inject(SessionService);
  private readonly router = inject(Router);

  protected readonly firstName = signal('');
  protected readonly lastName = signal('');
  protected readonly email = signal('');
  protected readonly password = signal('');
  protected readonly isSubmitting = signal(false);
  protected readonly error = signal<string | null>(null);

  protected submit(): void {
    this.isSubmitting.set(true);
    this.error.set(null);
    this.authApi.register({ firstName: this.firstName().trim(), lastName: this.lastName().trim(), email: this.email().trim(), password: this.password() }).subscribe({
      next: (response) => {
        this.session.setTokens(response);
        this.session.setCurrentUser({ id: response.userId, firstName: this.firstName().trim(), lastName: this.lastName().trim(), email: response.email, role: response.role });
        void this.router.navigateByUrl('/app/resources');
      },
      error: (error: HttpErrorResponse) => { this.isSubmitting.set(false); this.error.set(error.error?.message || 'No fue posible crear la cuenta.'); },
    });
  }
}
