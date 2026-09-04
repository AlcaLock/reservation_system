import { Component, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { SessionService } from '../../../../core/services/session.service';
import { UserApiService } from '../../../../core/services/user-api.service';

@Component({
  selector: 'app-profile-page',
  imports: [FormsModule],
  template: `
    <section class="profile-page">
      <header class="page-header">
        <div><p class="eyebrow">Cuenta</p><h1>Mi perfil</h1><p class="description">Actualiza tus datos personales y contraseña.</p></div>
      </header>
      @if (isLoading()) { <div class="message" role="status">Cargando perfil...</div> }
      @if (message(); as text) { <div class="message success" role="status">{{ text }}</div> }
      @if (error(); as text) { <div class="message error" role="alert">{{ text }}</div> }
      @if (!isLoading()) {
        <form class="profile-form" (ngSubmit)="save()">
          <label>Correo electrónico<input [value]="email()" readonly /></label>
          <label>Nombre<input name="firstName" [ngModel]="firstName()" (ngModelChange)="firstName.set($event)" required maxlength="80" /></label>
          <label>Apellido<input name="lastName" [ngModel]="lastName()" (ngModelChange)="lastName.set($event)" required maxlength="80" /></label>
          <label>Nueva contraseña <span class="hint">opcional</span><input name="password" type="password" [ngModel]="password()" (ngModelChange)="password.set($event)" minlength="8" maxlength="72" /></label>
          <button class="save-button" type="submit" [disabled]="isSaving()">{{ isSaving() ? 'Guardando...' : 'Guardar cambios' }}</button>
        </form>
      }
    </section>
  `,
  styles: [`
    :host { display: block; }
    .profile-page { width: min(720px, 90vw); margin: 0 auto; padding: 54px 0 80px; }
    .eyebrow { margin: 0 0 8px; color: var(--primary); font-size: .75rem; font-weight: 800; letter-spacing: .1em; text-transform: uppercase; }
    h1 { margin: 0; color: var(--ink); font-size: clamp(2rem, 4vw, 3rem); }
    .description { margin: 12px 0 30px; color: var(--muted); line-height: 1.55; }
    .profile-form { display: grid; gap: 18px; padding: 28px; border: 1px solid var(--line); border-radius: 8px; background: var(--surface); box-shadow: 0 8px 22px rgba(37, 70, 105, .06); }
    label { display: grid; gap: 7px; color: var(--muted); font-size: .78rem; font-weight: 700; }
    input { min-height: 44px; padding: 0 12px; border: 1px solid var(--line); border-radius: 5px; background: #fff; color: var(--ink); font: inherit; }
    input[readonly] { background: #f3f6fa; color: var(--muted); }
    .hint { font-weight: 400; }
    .save-button { min-height: 44px; border: 1px solid var(--primary); border-radius: 5px; background: var(--primary); color: #fff; font: inherit; font-weight: 700; cursor: pointer; }
    .save-button:disabled { cursor: wait; opacity: .7; }
    .message { margin-bottom: 16px; padding: 13px 16px; border: 1px solid var(--line); border-radius: 6px; color: var(--muted); }
    .message.success { border-color: #b9d8cb; background: #eef8f4; color: #236b5e; }
    .message.error { border-color: #e9c3bd; background: #fff5f3; color: var(--danger); }
  `],
})
export class ProfilePage {
  private readonly userApi = inject(UserApiService);
  private readonly session = inject(SessionService);

  protected readonly isLoading = signal(true);
  protected readonly isSaving = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly message = signal<string | null>(null);
  protected readonly email = signal('');
  protected readonly firstName = signal('');
  protected readonly lastName = signal('');
  protected readonly password = signal('');

  constructor() {
    this.userApi.getCurrent().subscribe({
      next: (user) => { this.email.set(user.email); this.firstName.set(user.firstName); this.lastName.set(user.lastName); this.isLoading.set(false); },
      error: (error: HttpErrorResponse) => { this.error.set(error.error?.message || 'No fue posible cargar tu perfil.'); this.isLoading.set(false); },
    });
  }

  protected save(): void {
    this.isSaving.set(true);
    this.error.set(null);
    this.userApi.updateProfile({ firstName: this.firstName().trim(), lastName: this.lastName().trim(), password: this.password() || undefined }).subscribe({
      next: (user) => {
        this.session.setCurrentUser({ id: user.id, firstName: user.firstName, lastName: user.lastName, email: user.email, role: user.role });
        this.password.set(''); this.message.set('Perfil actualizado correctamente.'); this.isSaving.set(false);
      },
      error: (error: HttpErrorResponse) => { this.error.set(error.error?.message || 'No fue posible actualizar tu perfil.'); this.isSaving.set(false); },
    });
  }
}