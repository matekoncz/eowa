import { Routes } from '@angular/router';
import { SignupComponent } from './components/signup/signup.component';
import { LoginComponent } from './components/login/login.component';
import { JoinEventComponent } from './components/join-event/join-event.component';
import { CreateEventComponent } from './components/create-event/create-event.component';
import { MyEventsComponent } from './components/my-events/my-events.component';
import { CreateCalendarComponent } from './components/create-calendar/create-calendar.component';
import { ViewEventComponent } from './components/event-editor/view-event/view-event.component';
import { ViewCalendarComponent } from './components/event-editor/view-calendar/view-calendar.component';

export const routes: Routes = [
    {path: 'signup', component: SignupComponent},
    {path: 'login', component: LoginComponent},
    {path: 'join-event', component: JoinEventComponent},
    {path: 'create-event', component: CreateEventComponent},
    {path: 'my-events', component: MyEventsComponent},
    {path: 'create-calendar', component: CreateCalendarComponent},
    {path: 'view-event', component: ViewEventComponent},
    {path: 'view-calendar', component: ViewCalendarComponent}
];
