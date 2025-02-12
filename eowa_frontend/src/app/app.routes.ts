import { Routes } from '@angular/router';
import { SignupComponent } from './components/signup/signup.component';
import { LoginComponent } from './components/login/login.component';
import { JoinEventComponent } from './components/join-event/join-event.component';
import { CreateEventComponent } from './components/create-event/create-event.component';

export const routes: Routes = [
    {path: 'signup', component: SignupComponent},
    {path: 'login', component: LoginComponent},
    {path: 'join-event', component: JoinEventComponent},
    {path: 'create-event', component: CreateEventComponent},
];
