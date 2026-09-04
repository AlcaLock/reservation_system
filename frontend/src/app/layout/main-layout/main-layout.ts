import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { LucideLogOut, LucideUsersRound } from '@lucide/angular';

import { SessionService } from '../../core/services/session.service';
import { AuthApiService } from '../../core/services/auth-api.service';

@Component({
  selector: 'app-main-layout',
  imports: [RouterLink, RouterLinkActive, RouterOutlet, LucideLogOut, LucideUsersRound],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.scss',
})
export class MainLayout {
  private readonly router = inject(Router);
  private readonly session = inject(SessionService);
  private readonly authApi = inject(AuthApiService);

  protected readonly currentUser = this.session.currentUser;
  protected readonly isAdmin = this.session.isAdmin;
  protected readonly userLabel = computed(() => {
    const user = this.currentUser();
    if (!user) {
      return 'Invitado';
    }
    return `${user.firstName} ${user.lastName}`;
  });

  protected openProfile(): void { void this.router.navigateByUrl('/app/profile'); }

  protected logout(): void {
    const refreshToken = this.session.getRefreshToken();
    if (refreshToken) this.authApi.logout(refreshToken).subscribe({ error: () => undefined });
    this.session.logOut();
    this.router.navigateByUrl('/login');
  }
}
