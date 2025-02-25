import { Component, EventEmitter, inject, Input, Output, OnInit } from '@angular/core';
import { Hour } from '../../../Model/Hour';
import { NumberToHourPipe } from "../../../pipes/number-to-hour.pipe";
import { CommonModule } from '@angular/common';
import { User } from '../../../Model/User';
import { Opinion, UserOpinion, UserOpinionLookup } from '../../../Model/Opinion';
import { MatDialog } from '@angular/material/dialog';
import { OpinionDialogComponent } from '../opinion-dialog/opinion-dialog.component';
import { EventService } from '../../../services/event.service';
import { UserService } from '../../../services/user.service';
import { OpinionMode } from '../view-calendar/view-calendar.component';
import { coerceStringArray } from '@angular/cdk/coercion';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-hour',
  standalone: true,
  imports: [NumberToHourPipe,CommonModule],
  templateUrl: './hour.component.html',
  styleUrl: './hour.component.css'
})
export class HourComponent implements OnInit{

  @Input() hour?: Hour;

  @Input() editMode: Boolean = false;

  @Input() participantNumber = 0;

  @Input() date?: Date;

  @Input() wholeDayChanged?: Subject<void>

  @Input() opinionMode = OpinionMode.DEFAULT

  @Output() hourChanged = new EventEmitter<Hour>();

  @Output() opinionSet = new EventEmitter<Opinion>();


  dialog = inject(MatDialog)

  satisfaction: Satisfaction = Satisfaction.NEUTRAL;

  constructor(private userservice: UserService){}

  ngOnInit(): void {
    this.wholeDayChanged?.subscribe(()=>{
      this.addOpinion(this.opinionMode.valueOf())
    })
  }

  public get Satisfaction(){
    return Satisfaction;
  }

  hourClicked(){
    if(!this.hour?.enabled){
      return;
    }
    if(this.editMode) {
      this.hour!.enabled = !this.hour!.enabled;
      this.hourChanged.emit(this.hour!);
    } else {
      if(this.opinionMode!=OpinionMode.DEFAULT){
        this.addOpinion(this.opinionMode.valueOf())
      } else{
        this.openOpinionDialog();
      }
      
    }
  }

  calculateSatisfaction(): Satisfaction{
    if(this.hour != null && this.hour.enabled){
      if(this.hour.opinions.length != 0){
        let satisfactionLevel = 0;
        for(let opinion of this.hour.opinions){
          if(opinion.userOpinion == UserOpinion.GOOD){
            satisfactionLevel += 1
          }
          if(opinion.userOpinion == UserOpinion.TOLERABLE){
            satisfactionLevel += 0.5
          }
          
        }
        satisfactionLevel = (satisfactionLevel/this.participantNumber);
        if(satisfactionLevel == 1){
          return Satisfaction.PERFECT
        }
        if(satisfactionLevel > 0.75){
          return Satisfaction.TERRIFIC
        }
        if(satisfactionLevel > 0.5){
          return Satisfaction.GOOD
        }
        if(satisfactionLevel > 0.25){
          return Satisfaction.BAD
        }
        if(satisfactionLevel > 0){
          return Satisfaction.TERRIBLE
        }
        if(satisfactionLevel == 0){
          return Satisfaction.HORRIBLE
        }
      }
    }
    return Satisfaction.NEUTRAL
  }

  openOpinionDialog() {
    const numberToHourPipe = new NumberToHourPipe();
    let dialogRef = this.dialog.open(OpinionDialogComponent,{data: {
      title: this.date!.toDateString()+" "+numberToHourPipe.transform(this.hour!.number),
      hour: this.hour,
      participantNumber: this.participantNumber
    }})

    dialogRef.afterClosed().subscribe((result: string)=>{
      if(result == undefined || result == null){
        return;
      }
      this.addOpinion(result)
    })
  }

  addOpinion(opinionType: string){
    let opinion: Opinion  = {
      userOpinion: UserOpinionLookup[opinionType as keyof typeof UserOpinion],
      number: this.hour?.numberInTotal,
      user: this.userservice.getCurrentUser()
    }
    this.opinionSet.emit(opinion);
    this.setUserOpinion(opinion);
  }

  setUserOpinion(opinion: Opinion) {
    for(let i = 0;i< this.hour!.opinions.length;i++){
      let existingOpinion = this.hour!.opinions[i]
      if(existingOpinion.user.username == opinion.user.username){
        this.hour!.opinions[i] = opinion;
        return;
      }
    }
    this.hour?.opinions.push(opinion)
  }
}


export enum Satisfaction{
  HORRIBLE,
  TERRIBLE,
  BAD,
  NEUTRAL,
  GOOD,
  TERRIFIC,
  PERFECT
}


