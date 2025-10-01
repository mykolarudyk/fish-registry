import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { FishListComponent } from './fish-list.component';
import { FishService } from '../../../services/fish.service';

class MockFishService {
  list = jasmine.createSpy('list').and.returnValue(of({
    content: [
      { id: '1', name: 'Cod',    species: 'Gadus morhua', length: 80, weight: 5.5 },
      { id: '2', name: 'Salmon', species: 'Salmo salar',  length: 75, weight: 4.2 },
    ],
    totalElements: 2
  }));
  create = jasmine.createSpy('create').and.returnValue(of({
    id: '3', name: 'Perch', species: 'Perca fluviatilis', length: 25, weight: 0.5
  }));
  update = jasmine.createSpy('update').and.returnValue(of({ id: '1' }));
  delete = jasmine.createSpy('delete').and.returnValue(of(void 0));
}

function getButtonByText(root: HTMLElement, text: string): HTMLButtonElement {
  const btns = Array.from(root.querySelectorAll('button')) as HTMLButtonElement[];
  const found = btns.find(b => (b.textContent || '').trim() === text);
  if (!found) throw new Error(`Button "${text}" not found`);
  return found;
}

describe('FishListComponent', () => {
  let fixture: ComponentFixture<FishListComponent>;
  let component: FishListComponent;
  let service: MockFishService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FishListComponent, NoopAnimationsModule],
      providers: [{ provide: FishService, useClass: MockFishService }],
    }).compileComponents();

    fixture = TestBed.createComponent(FishListComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(FishService) as unknown as MockFishService;
    fixture.detectChanges();
  });

  it('opens and discards the create form', fakeAsync(() => {
    tick(); fixture.detectChanges();

    const addBtn = getButtonByText(fixture.nativeElement, 'Add fish');
    addBtn.click(); fixture.detectChanges();

    let form = fixture.nativeElement.querySelector('form.fish-form');
    expect(form).toBeTruthy();

    const discardBtn = getButtonByText(fixture.nativeElement, 'Discard');
    discardBtn.click(); fixture.detectChanges();

    form = fixture.nativeElement.querySelector('form.fish-form');
    expect(form).toBeFalsy();
  }));

  it('creates a fish and hides the form', fakeAsync(() => {
    tick(); fixture.detectChanges();

    component.openCreateForm(); fixture.detectChanges();

    component.newForm.setValue({
      name: 'Perch', species: 'Perca fluviatilis', length: 25, weight: 0.5
    });
    component.create();
    tick(); fixture.detectChanges();

    expect(service.create).toHaveBeenCalledWith({
      name: 'Perch', species: 'Perca fluviatilis', length: 25, weight: 0.5
    });
    expect(component.showNewForm()).toBeFalse();
  }));

  it('allows editing exactly one row at a time', fakeAsync(() => {
    tick(); fixture.detectChanges();

    const [first, second] = component.rows();
    component.startEdit(first);
    expect(component.isEditing(first.id)).toBeTrue();

    component.startEdit(second);
    expect(component.isEditing(first.id)).toBeFalse();
    expect(component.isEditing(second.id)).toBeTrue();
  }));

  it('saves an edited row via service.update', fakeAsync(() => {
    tick(); fixture.detectChanges();

    const row = component.rows()[0];
    component.startEdit(row);
    component.control(row, 'name').setValue('Atlantic Cod');
    component.save(row);
    tick(); fixture.detectChanges();

    expect(service.update).toHaveBeenCalledWith(row.id, jasmine.objectContaining({
      name: 'Atlantic Cod'
    }));
    expect(component.editingId()).toBeNull();
  }));

  it('deletes a row when confirmed', fakeAsync(() => {
    spyOn(window, 'confirm').and.returnValue(true);
    tick(); fixture.detectChanges();

    const row = component.rows()[0];
    component.remove(row);
    tick(); fixture.detectChanges();

    expect(service.delete).toHaveBeenCalledWith(row.id);
  }));
});
