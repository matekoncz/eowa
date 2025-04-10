import { AfterViewInit, Component, EventEmitter, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-process-indicator',
  standalone: true,
  imports: [],
  templateUrl: './process-indicator.component.html',
  styleUrl: './process-indicator.component.css'
})
export class ProcessIndicatorComponent implements OnInit{

  counter = 0;

  getPoints(){
    let text = "";
    for (let i = 0; i < this.counter; i++){
      text += ". ";
    }

    return text;
  }

  ngOnInit(): void {
      //this.count()
  }

  count(){
    setTimeout(() => {
      this.counter = (this.counter+1);
      console.log(this.counter);
      this.count();
    }, 100);
  }
  

}
