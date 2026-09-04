import { computed, Injectable, signal } from '@angular/core';

import { User } from '../models/user.model';

export type SessionUser = Pick<User, 'id' | 'firstName' | 'lastName' | 'email' | 'role'>;

export interface SessionTokens {
  accessToken: string;
  refreshToken: string;
}

@Injectable({ providedIn: 'root' })
export class SessionService {
  private readonly storageKey = 'reservehub.session';
  private readonly tokensKey = 'reservehub.tokens';

  readonly currentUser = signal<SessionUser | null>(this.readStoredSession());
  readonly isAdmin = computed(() => this.currentUser()?.role === 'ADMIN');
  readonly isStudent = computed(() => this.currentUser()?.role === 'STUDENT');

  setCurrentUser(user: SessionUser | null): void {
    this.currentUser.set(user);

    if (!user) {
      localStorage.removeItem(this.storageKey);
      return;
    }

    localStorage.setItem(this.storageKey, JSON.stringify(user));
  }

  logOut(): void {
    this.setCurrentUser(null);
    localStorage.removeItem(this.tokensKey);
  }

  setTokens(tokens: SessionTokens): void { localStorage.setItem(this.tokensKey, JSON.stringify(tokens)); }
  getAccessToken(): string | null { return this.readTokens()?.accessToken || null; }
  getRefreshToken(): string | null { return this.readTokens()?.refreshToken || null; }

  private readTokens(): SessionTokens | null {
    const stored = localStorage.getItem(this.tokensKey);
    if (!stored) return null;
    try {
      const tokens = JSON.parse(stored) as SessionTokens;
      return tokens.accessToken && tokens.refreshToken ? tokens : null;
    } catch { return null; }
  }

  private readStoredSession(): SessionUser | null {
    const stored = localStorage.getItem(this.storageKey);
    if (!stored) {
      return null;
    }

    try {
      const parsed = JSON.parse(stored) as SessionUser;
      if (!parsed?.email || !parsed?.role) {
        return null;
      }
      return parsed;
    } catch {
      localStorage.removeItem(this.storageKey);
      return null;
    }
  }
}
