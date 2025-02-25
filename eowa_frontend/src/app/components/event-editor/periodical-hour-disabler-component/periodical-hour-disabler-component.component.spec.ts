import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PeriodicalHourDisablerComponentComponent } from './periodical-hour-disabler-component.component';

describe('PeriodicalHourDisablerComponentComponent', () => {
  let component: PeriodicalHourDisablerComponentComponent;
  let fixture: ComponentFixture<PeriodicalHourDisablerComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PeriodicalHourDisablerComponentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PeriodicalHourDisablerComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
