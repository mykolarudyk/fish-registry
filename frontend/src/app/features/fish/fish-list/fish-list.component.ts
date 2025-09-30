import { AfterViewInit, Component, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule, Sort } from '@angular/material/sort';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { merge, startWith, switchMap, map, catchError, of } from 'rxjs';
import { FishService } from '../fish.service';
import { Fish } from '../fish.model';

@Component({
  selector: 'app-fish-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatPaginatorModule, MatSortModule, MatProgressBarModule],
  templateUrl: './fish-list.component.html',
  styleUrls: ['./fish-list.component.scss']
})
export class FishListComponent implements AfterViewInit {
  private svc = inject(FishService);

  displayedColumns = ['name', 'species', 'lengthCm', 'weightKg'];
  rows = signal<Fish[]>([]);
  total = signal<number>(0);
  loading = signal<boolean>(true);
  pageSize = 10;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  ngAfterViewInit(): void {
    // react to paginator or sorter changes
    merge(
      this.sort.sortChange.pipe(startWith({ active: 'name', direction: 'asc' } as Sort)),
      this.paginator.page.pipe(startWith({ pageIndex: 0, pageSize: this.pageSize }))
    ).pipe(
      // on any change, load from server
      switchMap(() => {
        this.loading.set(true);
        const sortExpr = `${this.sort.active || 'name'},${this.sort.direction || 'asc'}`;
        return this.svc.list(this.paginator.pageIndex, this.paginator.pageSize, sortExpr);
      }),
      map(page => {
        this.total.set(page.totalElements);
        return page.content;
      }),
      catchError(() => {
        this.total.set(0);
        return of([]);
      })
    ).subscribe(data => {
      this.rows.set(data);
      this.loading.set(false);
    });
  }
}
