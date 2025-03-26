import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetBestTimeIntervalsDialogComponent } from './get-best-time-intervals-dialog.component';

describe('GetBestTimeIntervalsDialogComponent', () => {
  let component: GetBestTimeIntervalsDialogComponent;
  let fixture: ComponentFixture<GetBestTimeIntervalsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GetBestTimeIntervalsDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GetBestTimeIntervalsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
