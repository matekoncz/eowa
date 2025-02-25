import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopUpInfoComponent } from './popup-info.component';

describe('PopupInfoComponent', () => {
  let component: PopUpInfoComponent;
  let fixture: ComponentFixture<PopUpInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PopUpInfoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PopUpInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
