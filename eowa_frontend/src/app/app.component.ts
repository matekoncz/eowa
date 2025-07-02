import { AfterContentInit, AfterViewInit, Component, inject, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, RouterModule} from '@angular/router';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatMenuModule} from '@angular/material/menu';
import { MatDialog } from '@angular/material/dialog';
import { LogoutDialogComponent } from './components/logout-dialog/logout-dialog.component';
import { AuthService } from './services/auth.service';
import {AuthStatus} from './services/auth.service';
import { MailService } from './services/mail.service';
import { from } from 'rxjs';
import { Mail } from './Model/Mail';
import { PopUpInfoComponent } from './components/popup-info/popup-info.component';
import { EventService } from './services/event.service';
import { UserService } from './services/user.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, MatToolbarModule, MatButtonModule, MatIconModule, MatMenuModule, RouterModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements AfterContentInit{
  title = 'eowa';
  dialog = inject(MatDialog);
  AuthStatus = AuthStatus;

  constructor(private router: Router, private authservice: AuthService, private mailservice: MailService, private userservice: UserService){}
  
  ngAfterContentInit(): void {
    if(this.userservice.getCurrentUser()){
      this.getUnreadMails();
    }
  }


  getUnreadMails(){
    this.mailservice.getUnreadMails().subscribe((response) => {
      let status = response.status;
      if(status == 200){
        from(response.json()).subscribe((mails: Mail[]) => {
          mails.forEach((mail) => {
            console.log(mail.content);
             const dialogref = this.dialog.open(PopUpInfoComponent,{data:{title: mail.title, html: mail.content}});

             dialogref.afterClosed().subscribe(() => {
              this.mailservice.readMail(mail.id!)
             })
          });
        });
      }
  });
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(LogoutDialogComponent);

    dialogRef.afterClosed().subscribe(() => {
    });
  }

  getAuthStatus(){
    return this.authservice.getAuthStatus();
  }

  url(){
    return this.router.url;
  }

  getProfileText(): string {
    let user = this.userservice.getCurrentUser();
    if (user) {
      return user.username!;
    } else {
      return "unauthenticated";
    }
  }
}
