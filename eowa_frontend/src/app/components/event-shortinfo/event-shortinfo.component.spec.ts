import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventShortinfoComponent } from './event-shortinfo.component';

describe('EventShortinfoComponent', () => {
  let component: EventShortinfoComponent;
  let fixture: ComponentFixture<EventShortinfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventShortinfoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EventShortinfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
