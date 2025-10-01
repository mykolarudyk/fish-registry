import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FishService } from './fish.service';

describe('FishService', () => {
  let svc: FishService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [FishService],
    });
    svc = TestBed.inject(FishService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('list() calls GET /fish with paging params and returns the page', () => {
    let result: any;
    svc.list(1, 20, 'name,desc').subscribe(r => (result = r));

    const req = http.expectOne(r => r.method === 'GET' && r.url.endsWith('/fish'));
    expect(req.request.params.get('page')).toBe('1');
    expect(req.request.params.get('size')).toBe('20');
    expect(req.request.params.get('sort')).toBe('name,desc');

    req.flush({
      content: [{ id: '1', name: 'Cod', species: 'Gadus', length: 80, weight: 5.5 }],
      totalElements: 1,
    });

    expect(result).toBeTruthy();
    expect(result.content.length).toBe(1);
    expect(result.totalElements).toBe(1);
  });

  it('create() POSTs to /fish', () => {
    let created: any;
    svc.create({ name: 'Perch', species: 'Perca', length: 25, weight: 0.5 }).subscribe(r => (created = r));

    const req = http.expectOne(r => r.method === 'POST' && r.url.endsWith('/fish'));
    expect(req.request.body).toEqual({ name: 'Perch', species: 'Perca', length: 25, weight: 0.5 });
    req.flush({ id: '123', name: 'Perch', species: 'Perca', length: 25, weight: 0.5 });

    expect(created).toBeTruthy();
    expect(created.id).toBe('123');
  });

  it('update() PUTs to /fish/{id}', () => {
    let updated: any;
    svc.update('abc', { name: 'X', species: 'Y', length: 10, weight: 1 }).subscribe(r => (updated = r));

    const req = http.expectOne(r => r.method === 'PUT' && r.url.endsWith('/fish/abc'));
    expect(req.request.body).toEqual({ name: 'X', species: 'Y', length: 10, weight: 1 });
    req.flush({ id: 'abc', name: 'X', species: 'Y', length: 10, weight: 1 });

    expect(updated).toBeTruthy();
    expect(updated.id).toBe('abc');
  });

  it('delete() DELETEs /fish/{id}', () => {
    let done = false;
    svc.delete('abc').subscribe(() => (done = true));

    const req = http.expectOne(r => r.method === 'DELETE' && r.url.endsWith('/fish/abc'));
    expect(req.request.method).toBe('DELETE');
    req.flush({});

    expect(done).toBeTrue();
  });
});
