import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Page } from '../../shared/page.model';
import { Fish } from './fish.model';

@Injectable({ providedIn: 'root' })
export class FishService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/fish`;

  list(page = 0, size = 10, sort = 'name,asc'): Observable<Page<Fish>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    return this.http.get<Page<Fish>>(this.base, { params });
  }

  //Useful for detailed page of 1 fish
  get(id: string) {
    return this.http.get<Fish>(`${this.base}/${id}`);
  }

  create(body: Omit<Fish, 'id'>) {
    return this.http.post<Fish>(this.base, body as any);
  }

  update(id: string, body: Omit<Fish, 'id'>) {
    return this.http.put<Fish>(`${this.base}/${id}`, body as any);
  }

  delete(id: string) {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
