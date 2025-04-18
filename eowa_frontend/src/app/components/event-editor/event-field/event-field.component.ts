import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SelectionField } from '../../../Model/SelectionField';
import { Option } from '../../../Model/Option';
import { MatButtonModule } from '@angular/material/button';
import { UserService } from '../../../services/user.service';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';

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

  @Output() voteEvent = new EventEmitter<OptionWithField>();

  @Output() removeVoteEvent = new EventEmitter<OptionWithField>();

  @Output() deleteFieldEvent = new EventEmitter<SelectionField>();

  @Output() addOptionEvent = new EventEmitter<OptionWithField>();

  @Output() deleteOptionEvent = new EventEmitter<OptionWithField>();

  @Output() selectOptionEvent = new EventEmitter<OptionWithField>();

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
    this.voteEvent.emit({ option: option, field: this.field! });
  }

  removeVote(option: Option) {
    this.removeVoteEvent.emit({ option: option, field: this.field! });
  }

  alreadyVoted(option: Option): boolean {
    let usernames: string[] = [];
    usernames = option.voters.map((voter) => voter.username!);
    return usernames.includes(this.userService.getCurrentUser()!.username!);
  }

  deleteField() {
    this.deleteFieldEvent.emit(this.field!);
  }

  addOption() {
    let option: Option = {
      value: this.newValue,
      voters: [],
      selected: false,
    };
    this.addOptionEvent.emit({ option: option, field: this.field! });
  }

  deleteOption(option: Option) {
    this.deleteOptionEvent.emit({ option: option, field: this.field! });
  }

  changeEditMode() {
    this.editMode = !this.editMode;
  }

  selectOption(option: Option) {
    this.selectOptionEvent.emit({ option: option, field: this.field! });
  }
}

export interface OptionWithField {
  option: Option;
  field: SelectionField;
}
