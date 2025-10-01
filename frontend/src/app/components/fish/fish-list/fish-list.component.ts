import { AfterViewInit, Component, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule} from '@angular/material/sort';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormControl, FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { merge, startWith, switchMap, map, catchError, of } from 'rxjs';
import { FishService } from '../../../services/fish.service';
import { Fish } from '../../../models/fish.model';
import { FormGroupDirective } from '@angular/forms';

type FishForm = FormGroup<{
  name: FormControl<string>;
  species: FormControl<string>;
  length: FormControl<number>;
  weight: FormControl<number>;
}>;

@Component({
  selector: 'app-fish-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressBarModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './fish-list.component.html',
  styleUrls: ['./fish-list.component.scss'],
})

export class FishListComponent implements AfterViewInit{
  private fishService = inject(FishService);
  private fb = inject(NonNullableFormBuilder);

  displayedColumns = ['name', 'species', 'length', 'weight', 'actions'];
  rows = signal<Fish[]>([]);
  total = signal<number>(0);
  loading = signal<boolean>(true);
  pageSize = 10;

  private forms = new Map<string, FishForm>();
  showNewForm = signal(false);
  editingId = signal<string | null>(null);
  isEditing = (id: string) => this.editingId() === id;

  newForm: FishForm = this.fb.group({
    name: this.fb.control('', { validators: [Validators.required, Validators.maxLength(100)] }),
    species: this.fb.control('', { validators: [Validators.required, Validators.maxLength(100)] }),
    length: this.fb.control(0, { validators: [Validators.required, Validators.min(1)] }),
    weight: this.fb.control(0, { validators: [Validators.required, Validators.min(0.01)] }),
  });

  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild('createForm', { read: FormGroupDirective })
  createFormDir!: FormGroupDirective;

  ngAfterViewInit(): void {
    this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));

    merge(this.sort.sortChange, this.paginator.page)
      .pipe(
        startWith({}),
        switchMap(() => {
          this.loading.set(true);
          const sortExpr = `${this.sort.active || 'name'},${this.sort.direction || 'asc'}`;
          const page = this.paginator.pageIndex ?? 0;
          const size = this.paginator.pageSize || this.pageSize;
          return this.fishService.list(page, size, sortExpr);
        }),
        map(page => {
          this.total.set(page.totalElements ?? 0);
          return page.content ?? [];
        }),
        catchError(() => {
          this.total.set(0);
          return of([]);
        })
      )
      .subscribe(data => {
        this.rows.set(data);
        this.loading.set(false);
      });
  }

  private formFor(row: Fish): FishForm {
    let form = this.forms.get(row.id);
    if (!form) {
      form = this.fb.group({
        name: this.fb.control(row.name, { validators: [Validators.required, Validators.maxLength(100)] }),
        species: this.fb.control(row.species, { validators: [Validators.required, Validators.maxLength(100)] }),
        length: this.fb.control(row.length, { validators: [Validators.required, Validators.min(1)] }),
        weight: this.fb.control(row.weight, { validators: [Validators.required, Validators.min(0.01)] }),
      });
      this.forms.set(row.id, form);
    }
    return form;
  }

  startEdit(row: Fish) {
    const current = this.editingId();
    if (current && current !== row.id) {
      const prev = this.rows().find(r => r.id === current);
      if (prev) this.cancelEdit(prev);
    }
    this.formFor(row);
    this.editingId.set(row.id);
  }

  cancelEdit(row: Fish) {
    const form = this.forms.get(row.id);
    if (form) form.reset({
      name: row.name,
      species: row.species,
      length: row.length,
      weight: row.weight,
    });
    if (this.editingId() === row.id) this.editingId.set(null);
  }

  save(row: Fish) {
    const form = this.formFor(row);
    if (form.invalid) { form.markAllAsTouched(); return; }
    const { name, species, length, weight } = form.getRawValue();
    this.loading.set(true);
    this.fishService.update(row.id, { name, species, length, weight }).subscribe({
      next: () => {
        this.editingId.set(null);
        this._refreshCurrentPage();
      },
      error: () => this.loading.set(false),
    });
  }

  remove(row: Fish) {
    if (!confirm(`Delete "${row.name}"?`)) return;
    this.loading.set(true);
    this.fishService.delete(row.id).subscribe({
      next: () => this._refreshCurrentPage(),
      error: () => this.loading.set(false),
    });
  }

  create() {
    if (this.newForm.invalid) {
      this.newForm.markAllAsTouched();
      return;
    }
    const body = this.newForm.getRawValue();
    this.loading.set(true);
    this.fishService.create(body).subscribe({
      next: () => {
        this.createFormDir.resetForm({ name: '', species: '', length: 0, weight: 0 });
        this.showNewForm.set(false);
        this._refreshCurrentPage(true);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  private _refreshCurrentPage(goToFirst = false) {
    if (goToFirst && this.paginator.pageIndex !== 0) {
      this.paginator.firstPage();
    } else {
      this.paginator._changePageSize(this.paginator.pageSize);
    }
  }

  openCreateForm() {
    if (this.editingId()) return;
    this.showNewForm.set(true);
  }

  discardCreate() {
    if (this.createFormDir) {
      this.createFormDir.resetForm({ name: '', species: '', length: 0, weight: 0 });
    } else {
      this.newForm.reset({ name: '', species: '', length: 0, weight: 0 });
      Object.values(this.newForm.controls).forEach(c => { c.markAsPristine(); c.markAsUntouched(); });
    }
    this.showNewForm.set(false);
  }

  rowInvalid(row: Fish) {
    return this.formFor(row).invalid;
  }

  control<K extends keyof FishForm['controls']>(row: Fish, key: K): FishForm['controls'][K] {
    return this.formFor(row).controls[key];
  }
}
