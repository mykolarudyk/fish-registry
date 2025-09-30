import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FishList } from './fish-list';

describe('FishList', () => {
  let component: FishList;
  let fixture: ComponentFixture<FishList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FishList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FishList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
