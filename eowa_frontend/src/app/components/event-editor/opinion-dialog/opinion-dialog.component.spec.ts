import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OpinionDialogComponent } from './opinion-dialog.component';

describe('OpinionDialogComponent', () => {
  let component: OpinionDialogComponent;
  let fixture: ComponentFixture<OpinionDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OpinionDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OpinionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
