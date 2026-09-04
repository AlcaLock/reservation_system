import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Resource } from '../../../../core/models/resource.model';
import { ResourceApiService } from '../../../../core/services/resource-api.service';

@Component({ selector: 'app-resource-detail-page', imports: [RouterLink], template: `<section class="detail"><a routerLink="/app/resources">← Volver al catálogo</a>@if (resource(); as item) {<p class="eyebrow">{{ item.type }}</p><h1>{{ item.name }}</h1><p>{{ item.description || 'Sin descripción adicional.' }}</p><dl><dt>Ubicación</dt><dd>{{ item.location }}</dd><dt>Capacidad</dt><dd>{{ item.capacity }} personas</dd><dt>Estado</dt><dd>{{ item.status }}</dd></dl>} @else if (error()) {<p role="alert">{{ error() }}</p>} @else {<p role="status">Cargando recurso...</p>}</section>`, styles: [`.detail{width:min(760px,90vw);margin:0 auto;padding:54px 0;color:var(--ink)}a{color:var(--primary);font-weight:700}.eyebrow{margin-top:36px;color:var(--primary);font-size:.75rem;font-weight:800;letter-spacing:.1em}h1{font-size:clamp(2rem,5vw,3.5rem)}p{color:var(--muted);line-height:1.6}dl{display:grid;grid-template-columns:max-content 1fr;gap:14px;margin-top:30px;padding:24px;border:1px solid var(--line);border-radius:8px;background:var(--surface)}dt{color:var(--muted);font-weight:700}dd{margin:0}`] })
export class ResourceDetailPage {
  private readonly route = inject(ActivatedRoute); private readonly api = inject(ResourceApiService);
  protected readonly resource = signal<Resource | null>(null); protected readonly error = signal<string | null>(null);
  constructor() { this.api.getById(Number(this.route.snapshot.paramMap.get('id'))).subscribe({ next: (item) => this.resource.set(item), error: () => this.error.set('No se encontró el recurso.') }); }
}
