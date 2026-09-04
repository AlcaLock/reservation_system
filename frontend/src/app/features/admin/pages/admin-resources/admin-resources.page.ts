import { Component, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { Resource, ResourceStatus, ResourceType } from '../../../../core/models/resource.model';
import { ResourceApiService, ResourceRequest } from '../../../../core/services/resource-api.service';

@Component({
  selector: 'app-admin-resources-page',
  imports: [FormsModule],
  templateUrl: './admin-resources.page.html',
  styleUrl: './admin-resources.page.scss',
})
export class AdminResourcesPage {
  private readonly resourceApi = inject(ResourceApiService);

  protected readonly resources = signal<Resource[]>([]);
  protected readonly isLoading = signal(true);
  protected readonly isSaving = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly feedback = signal<string | null>(null);
  protected readonly editingId = signal<number | null>(null);
  protected readonly name = signal('');
  protected readonly description = signal('');
  protected readonly type = signal<ResourceType>('ROOM');
  protected readonly capacity = signal<number | null>(1);
  protected readonly location = signal('');

  constructor() {
    this.loadResources();
  }

  protected loadResources(): void {
    this.isLoading.set(true);
    this.error.set(null);
    this.resourceApi.getAll({}).subscribe({
      next: (resources) => {
        this.resources.set(resources);
        this.isLoading.set(false);
      },
      error: (error: HttpErrorResponse) => this.handleError(error, 'No fue posible cargar los recursos.'),
    });
  }

  protected startCreate(): void {
    this.editingId.set(null);
    this.name.set('');
    this.description.set('');
    this.type.set('ROOM');
    this.capacity.set(1);
    this.location.set('');
    this.feedback.set(null);
    this.error.set(null);
  }

  protected startEdit(resource: Resource): void {
    this.editingId.set(resource.id);
    this.name.set(resource.name);
    this.description.set(resource.description || '');
    this.type.set(resource.type);
    this.capacity.set(resource.capacity);
    this.location.set(resource.location);
    this.feedback.set(null);
    this.error.set(null);
  }

  protected cancelEdit(): void {
    this.editingId.set(null);
    this.feedback.set(null);
  }

  protected saveResource(): void {
    const request: ResourceRequest = {
      name: this.name().trim(),
      description: this.description().trim(),
      type: this.type(),
      capacity: this.capacity() || 0,
      location: this.location().trim(),
    };

    if (!request.name || !request.location || request.capacity < 1) {
      this.error.set('Completa nombre, ubicación y una capacidad válida.');
      return;
    }

    this.isSaving.set(true);
    this.error.set(null);
    const request$ = this.editingId() === null
      ? this.resourceApi.create(request)
      : this.resourceApi.update(this.editingId() as number, request);

    request$.subscribe({
      next: (resource) => {
        this.resources.update((items) => {
          const currentId = this.editingId();
          return currentId === null
            ? [resource, ...items]
            : items.map((item) => item.id === resource.id ? resource : item);
        });
        this.feedback.set(currentIdMessage(this.editingId()));
        this.isSaving.set(false);
        this.editingId.set(null);
      },
      error: (error: HttpErrorResponse) => this.handleSaveError(error),
    });
  }

  protected changeStatus(resource: Resource, status: ResourceStatus): void {
    this.resourceApi.updateStatus(resource.id, status).subscribe({
      next: (updated) => this.resources.update((items) => items.map((item) => item.id === updated.id ? updated : item)),
      error: (error: HttpErrorResponse) => this.handleError(error, 'No fue posible actualizar el estado del recurso.'),
    });
  }

  protected statusLabel(status: ResourceStatus): string {
    return { AVAILABLE: 'Disponible', MAINTENANCE: 'Mantenimiento', INACTIVE: 'Inactivo' }[status];
  }

  protected typeLabel(type: ResourceType): string {
    return { ROOM: 'Sala', LABORATORY: 'Laboratorio', EQUIPMENT: 'Equipo' }[type];
  }

  protected errorMessage(error: HttpErrorResponse, fallback: string): string {
    return error.error?.message || fallback;
  }

  private handleSaveError(error: HttpErrorResponse): void {
    this.isSaving.set(false);
    this.error.set(this.errorMessage(error, 'No fue posible guardar el recurso.'));
  }

  private handleError(error: HttpErrorResponse, fallback: string): void {
    this.isLoading.set(false);
    this.error.set(this.errorMessage(error, fallback));
  }
}

function currentIdMessage(editingId: number | null): string {
  return editingId === null ? 'Recurso creado correctamente.' : 'Recurso actualizado correctamente.';
}
