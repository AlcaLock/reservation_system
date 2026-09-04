import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AdminApiService } from '../../../../core/services/admin-api.service';
import { User } from '../../../../core/models/user.model';

@Component({ selector: 'app-admin-user-detail-page', imports: [RouterLink], template: `<section class="detail"><a routerLink="/app/admin/users">← Volver a usuarios</a>@if (user(); as item) {<p class="eyebrow">USUARIO #{{ item.id }}</p><h1>{{ item.firstName }} {{ item.lastName }}</h1><dl><dt>Correo</dt><dd>{{ item.email }}</dd><dt>Rol</dt><dd>{{ item.role }}</dd><dt>Estado</dt><dd>{{ item.enabled ? 'Activo' : 'Inactivo' }}</dd></dl>} @else if (error()) {<p role="alert">{{ error() }}</p>} @else {<p role="status">Cargando usuario...</p>}</section>`, styles: [`.detail{width:min(760px,90vw);margin:0 auto;padding:54px 0;color:var(--ink)}a{color:var(--primary);font-weight:700}.eyebrow{margin-top:36px;color:var(--primary);font-size:.75rem;font-weight:800;letter-spacing:.1em}h1{font-size:clamp(2rem,5vw,3.5rem)}dl{display:grid;grid-template-columns:max-content 1fr;gap:14px;margin-top:30px;padding:24px;border:1px solid var(--line);border-radius:8px;background:var(--surface)}dt{color:var(--muted);font-weight:700}dd{margin:0}`] })
export class AdminUserDetailPage {
  private readonly route = inject(ActivatedRoute); private readonly api = inject(AdminApiService);
  protected readonly user = signal<User | null>(null); protected readonly error = signal<string | null>(null);
  constructor() { this.api.getUserById(Number(this.route.snapshot.paramMap.get('id'))).subscribe({ next: (item) => this.user.set(item), error: () => this.error.set('No se encontró el usuario.') }); }
}
