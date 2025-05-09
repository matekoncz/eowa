import { Component, Input, OnInit, Output } from '@angular/core';
import { SelectionField } from '../../../Model/SelectionField';
import { Option } from '../../../Model/Option';
import { MatButtonModule } from '@angular/material/button';
import { UserService } from '../../../services/user.service';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-event-field',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, FormsModule, MatInputModule,MatTooltipModule],
  templateUrl: './event-field.component.html',
  styleUrl: './event-field.component.css',
})
export class EventFieldComponent implements OnInit {
  @Input() field?: SelectionField;

  @Input() isUserOwner: boolean = false;

  @Input() isEventFinalized: boolean = true;

  @Output() voteEvent = new Subject<OptionWithField>();

  @Output() removeVoteEvent = new Subject<OptionWithField>();

  @Output() deleteFieldEvent = new Subject<SelectionField>();

  @Output() addOptionEvent = new Subject<OptionWithField>();

  @Output() deleteOptionEvent = new Subject<OptionWithField>();

  @Output() selectOptionEvent = new Subject<OptionWithField>();

  sortedOptions: Option[] = [];

  newValue: string = '';

  editMode: boolean = false;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.sortOptions();
  }

  sortOptions() {
    let options = this.field!.options;
    options.sort((a, b) => b.voters.length - a.voters.length);
    this.sortedOptions = options;
  }

  vote(option: Option) {
    this.voteEvent.next({ option: option, field: this.field! });
  }

  removeVote(option: Option) {
    this.removeVoteEvent.next({ option: option, field: this.field! });
  }

  alreadyVoted(option: Option): boolean {
    let usernames: string[] = [];
    usernames = option.voters.map((voter) => voter.username!);
    return usernames.includes(this.userService.getCurrentUser()!.username!);
  }

  deleteField() {
    this.deleteFieldEvent.next(this.field!);
  }

  addOption() {
    let option: Option = {
      value: this.newValue,
      voters: [],
      selected: false,
    };
    this.addOptionEvent.next({ option: option, field: this.field! });
  }

  deleteOption(option: Option) {
    this.deleteOptionEvent.next({ option: option, field: this.field! });
  }

  changeEditMode() {
    this.editMode = !this.editMode;
  }

  selectOption(option: Option) {
    this.selectOptionEvent.next({ option: option, field: this.field! });
  }
}

export interface OptionWithField {
  option: Option;
  field: SelectionField;
}
