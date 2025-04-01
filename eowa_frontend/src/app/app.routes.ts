import { Routes } from '@angular/router';
import { SignupComponent } from './components/signup/signup.component';
import { LoginComponent } from './components/login/login.component';
import { JoinEventComponent } from './components/join-event/join-event.component';
import { CreateEventComponent } from './components/create-event/create-event.component';
import { MyEventsComponent } from './components/my-events/my-events.component';
import { CreateCalendarComponent } from './components/create-calendar/create-calendar.component';
import { ViewEventComponent } from './components/event-editor/view-event/view-event.component';
import { ViewCalendarComponent } from './components/event-editor/view-calendar/view-calendar.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
    {path: 'signup', component: SignupComponent},
    {path: 'login', component: LoginComponent},
    {path: 'join-event', component: JoinEventComponent, canActivate: [authGuard]},
    {path: 'create-event', component: CreateEventComponent, canActivate: [authGuard]},
    {path: 'my-events', component: MyEventsComponent, canActivate: [authGuard]},
    {path: 'create-calendar', component: CreateCalendarComponent, canActivate: [authGuard]},
    {path: 'view-event', component: ViewEventComponent, canActivate: [authGuard]},
    {path: 'view-calendar', component: ViewCalendarComponent, canActivate: [authGuard]}
];
