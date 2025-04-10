import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EventService } from '../../../services/event.service';
import { EowaEvent } from '../../../Model/EowaEvent';
import { from } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { HttpStatusCode } from '@angular/common/http';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialog } from '@angular/material/dialog';
import { PopUpInfoComponent } from '../../popup-info/popup-info.component';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { SelectionField } from '../../../Model/SelectionField';
import { Option } from '../../../Model/Option';
import { MatCheckboxModule } from '@angular/material/checkbox';
import {
  EventFieldComponent,
  OptionWithField,
} from '../event-field/event-field.component';
import { EventBlueprint } from '../../../Model/EventBlueprint';
import { MatSelectModule } from '@angular/material/select';
import { UserService } from '../../../services/user.service';
import { ProcessIndicatorComponent } from "../../process-indicator/process-indicator.component";

@Component({
  selector: 'app-view-event',
  standalone: true,
  imports: [
    MatButtonModule,
    MatChipsModule,
    MatFormFieldModule,
    FormsModule,
    MatIconModule,
    MatInputModule,
    MatCheckboxModule,
    EventFieldComponent,
    MatSelectModule,
    ProcessIndicatorComponent
],
  templateUrl: './view-event.component.html',
  styleUrl: './view-event.component.css',
})
export class ViewEventComponent implements OnInit {
  eventId?: number;

  event?: EowaEvent;

  fieldname?: string;

  fieldValue?: string;

  selectedByPoll: boolean = true;

  openForModification: boolean = false;

  userBlueprints?: EventBlueprint[] = [];

  selectedBlueprint?: EventBlueprint;

  blueprintName?: string;

  values: string[] = [];

  loading: boolean = true;

  constructor(
    private activatedRoute: ActivatedRoute,
    private eventservice: EventService,
    private dialog: MatDialog,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.eventId = this.activatedRoute.snapshot.queryParams['id'];
    this.eventservice.getEvent(this.eventId!).subscribe((response) => {
      from(response.json()).subscribe((event: EowaEvent) => {
        this.loading = false;
        this.event = event;
      });
    });
    this.eventservice.getBluePrintsForCurrentUser().subscribe((response) => {
      from(response.json()).subscribe((blueprints: EventBlueprint[]) => {
        this.userBlueprints = blueprints;
      });
    });
  }

  viewCalendar() {
    this.router.navigate(['view-calendar'], {
      queryParams: { id: this.event?.id },
    });
  }

  addCalendar() {
    this.router.navigate(['create-calendar'], {
      queryParams: { id: this.event?.id },
    });
  }

  addValue() {
    this.values.push(this.fieldValue!);
    this.fieldValue = '';
  }

  addField() {
    let options: Option[] = [];
    for (let value of this.values) {
      options.push({
        value: value,
        selected: false,
        voters: [],
      });
    }

    let field: SelectionField = {
      title: this.fieldname!,
      options: options,
      openForModification: this.openForModification,
      selectedByPoll: this.selectedByPoll,
    };

    this.eventservice
      .addFields(this.event!.id!, [field])
      .subscribe((response) => {
        let status = response.status;
        if (status == HttpStatusCode.Ok) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Success', content: 'Field added successfully' },
          });
          this.ngOnInit();
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: 'Error', content: message },
            });
          });
        }
      });

    this.fieldValue = '';
    this.fieldname = '';
    this.selectedByPoll = true;
    this.openForModification = false;
    this.values = [];
  }

  createBluePrint() {
    this.eventservice
      .createBlueprint(this.event!.id!, this.blueprintName!.replace(' ', '_'))
      .subscribe((response) => {
        let status = response.status;
        if (status == HttpStatusCode.Ok) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: {
              title: 'Success',
              content: 'Blueprint created successfully',
            },
          });
          this.ngOnInit();
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: ' Error', content: message },
            });
          });
        }
      });
    this.blueprintName = '';
  }

  addFieldsFromBluePrint() {
    this.eventservice
      .addFieldsFromBlueprint(this.event!.id!, this.selectedBlueprint!.id!)
      .subscribe((response) => {
        let status = response.status;
        if (status == HttpStatusCode.Ok) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Success', content: 'Fields added successfully' },
          });
          this.ngOnInit();
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: ' Error', content: message },
            });
          });
        }
      });
  }

  vote(ov: OptionWithField) {
    this.eventservice
      .voteForOption(this.event!.id!, ov.field.id!, ov.option.id!)
      .subscribe((response) => {
        let status = response.status;
        if (status == HttpStatusCode.Ok) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Success', content: 'Voted successfully' },
          });
          this.ngOnInit();
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: 'Error', content: message },
            });
          });
        }
      });
  }

  removeVote(ov: OptionWithField) {
    this.eventservice
      .removeOptionVote(this.event!.id!, ov.field.id!, ov.option.id!)
      .subscribe((response) => {
        let status = response.status;
        if (status == HttpStatusCode.Ok) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Success', content: 'Vote removed successfully' },
          });
          this.ngOnInit();
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: 'Error', content: message },
            });
          });
        }
      });
  }

  deleteField(field: SelectionField) {
    this.eventservice
      .removeFields(this.event!.id!, [field.id!])
      .subscribe((response) => {
        let status = response.status;
        if (status == HttpStatusCode.Ok) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Success', content: 'Field deleted successfully' },
          });
          this.ngOnInit();
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: 'Error', content: message },
            });
          });
        }
      });
  }

  deleteOption(ov: OptionWithField) {
    this.eventservice
      .removeOptions(this.event!.id!, ov.field.id!, [ov.option.id!])
      .subscribe((response) => {
        let status = response.status;
        if (status == HttpStatusCode.Ok) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Success', content: 'Option deleted successfully' },
          });
          this.ngOnInit();
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: 'Error', content: message },
            });
          });
        }
      });
  }

  addOption(ov: OptionWithField) {
    this.eventservice
      .addOptions(this.event!.id!, ov.field.id!, [ov.option])
      .subscribe((response) => {
        let status = response.status;
        if (status == HttpStatusCode.Ok) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Success', content: 'Option added successfully' },
          });
          this.ngOnInit();
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: 'Error', content: message },
            });
          });
        }
      });
  }

  selectOption(ov: OptionWithField) {
    this.eventservice
      .selectOption(this.event!.id!, ov.field.id!, ov.option.id!)
      .subscribe((response) => {
        let status = response.status;
        if (status == HttpStatusCode.Ok) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Success', content: 'Option selected successfully' },
          });
          this.ngOnInit();
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: 'Error', content: message },
            });
          });
        }
      });
  }

  finalizeEvent() {
    this.eventservice.finalizeEvent(this.event!.id!).subscribe((response) => {
      let status = response.status;
      if (status == HttpStatusCode.Ok) {
        const dialogRef = this.dialog.open(PopUpInfoComponent, {
          data: { title: 'Success', content: 'Event finalized successfully' },
        });
        this.ngOnInit();
      } else {
        from(response.text()).subscribe((message) => {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Error', content: message },
          });
        });
      }
    });
  }

  unfinalizeEvent() {
    this.eventservice.unFinalizeEvent(this.event!.id!).subscribe((response) => {
      let status = response.status;
      if (status == HttpStatusCode.Ok) {
        const dialogRef = this.dialog.open(PopUpInfoComponent, {
          data: { title: 'Success', content: 'Event unfinalized successfully' },
        });
        this.ngOnInit();
      } else {
        from(response.text()).subscribe((message) => {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Error', content: message },
          });
        });
      }
    });
  }

  isUserOwner(): boolean {
    return (
      this.event?.owner.username == this.userService.getCurrentUser()!.username
    );
  }
}
