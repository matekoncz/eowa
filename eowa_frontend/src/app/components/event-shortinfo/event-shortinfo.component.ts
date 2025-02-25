import { EowaEvent } from '../../Model/EowaEvent';
import {
  ChangeDetectionStrategy,
  Component,
  signal,
  Input,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { Router } from '@angular/router';

@Component({
  selector: 'app-event-shortinfo',
  standalone: true,
  imports: [MatExpansionModule, MatButtonModule],
  templateUrl: './event-shortinfo.component.html',
  styleUrl: './event-shortinfo.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EventShortinfoComponent {

  @Input() event?: EowaEvent;

  @Input() role?: string;

  readonly panelOpenState = signal(false);

  constructor(private router: Router) {}

  openEvent(): void {
    this.router.navigate(['/view-event'], {
      queryParams: { id: this.event?.id },
    });
  }
}
