import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessIndicatorComponent } from './process-indicator.component';

describe('ProcessIndicatorComponent', () => {
  let component: ProcessIndicatorComponent;
  let fixture: ComponentFixture<ProcessIndicatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProcessIndicatorComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProcessIndicatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
