import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-error-404-page',
  imports: [RouterLink],
  template: `
    <main class="state-page">
      <section class="state-panel">
        <img src="assets/error-404.jpg" alt="Error 404" class="state-image" />
        <p class="state-code">ERROR 404</p>
        <h1>No pudimos encontrar ese recurso</h1>
        <p class="state-copy">El servidor no encontró la información solicitada. Intenta regresar y continuar desde allí.</p>
        <a routerLink="/app/resources" class="state-action">Ir a recursos</a>
      </section>
    </main>
  `,
  styles: [`
    :host { display: block; }
    .state-page { min-height: 100vh; display: grid; place-items: center; padding: 32px 20px; background: #f3f6fa; }
    .state-panel { width: min(100%, 560px); padding: 36px; text-align: center; background: #fff; border: 1px solid #dbe3ec; border-radius: 8px; box-shadow: 0 18px 48px rgba(31, 42, 58, .10); }
    .state-image { display: block; width: min(100%, 320px); max-height: 280px; object-fit: contain; margin: 0 auto 12px; }
    .state-code { margin: 0; color: #bf4a4a; font: 500 13px/1 'DM Mono', monospace; letter-spacing: 2px; }
    h1 { margin: 14px 0 8px; color: #1f2a3a; font: 800 clamp(24px, 5vw, 36px)/1.1 'Plus Jakarta Sans', sans-serif; }
    .state-copy { margin: 0 0 24px; color: #64748b; font: 400 15px/1.6 'Plus Jakarta Sans', sans-serif; }
    .state-action { display: inline-flex; align-items: center; justify-content: center; min-height: 44px; padding: 0 20px; color: #fff; background: #245c8e; border-radius: 6px; font: 700 14px 'Plus Jakarta Sans', sans-serif; text-decoration: none; }
    .state-action:hover { background: #1b466d; }
  `],
})
export class Error404Page {}
