import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found-page',
  imports: [RouterLink],
  template: `
    <main class="state-page">
      <section class="state-panel">
        <img src="assets/not-found.jpg" alt="Página no encontrada" class="state-image" />
        <p class="state-code">NOT FOUND</p>
        <h1>Esta página no existe</h1>
        <p class="state-copy">La dirección que intentaste abrir no está disponible.</p>
        <a routerLink="/login" class="state-action">Volver al inicio</a>
      </section>
    </main>
  `,
  styles: [`
    :host { display: block; }
    .state-page { min-height: 100vh; display: grid; place-items: center; padding: 32px 20px; background: #f3f6fa; }
    .state-panel { width: min(100%, 560px); padding: 36px; text-align: center; background: #fff; border: 1px solid #dbe3ec; border-radius: 8px; box-shadow: 0 18px 48px rgba(31, 42, 58, .10); }
    .state-image { display: block; width: min(100%, 320px); max-height: 280px; object-fit: contain; margin: 0 auto 12px; }
    .state-code { margin: 0; color: #245c8e; font: 500 13px/1 'DM Mono', monospace; letter-spacing: 2px; }
    h1 { margin: 14px 0 8px; color: #1f2a3a; font: 800 clamp(24px, 5vw, 36px)/1.1 'Plus Jakarta Sans', sans-serif; }
    .state-copy { margin: 0 0 24px; color: #64748b; font: 400 15px/1.6 'Plus Jakarta Sans', sans-serif; }
    .state-action { display: inline-flex; align-items: center; justify-content: center; min-height: 44px; padding: 0 20px; color: #fff; background: #245c8e; border-radius: 6px; font: 700 14px 'Plus Jakarta Sans', sans-serif; text-decoration: none; }
    .state-action:hover { background: #1b466d; }
  `],
})
export class NotFoundPage {}
