import { Injectable } from '@angular/core';
import { ApiService, Controller } from './api.service';

@Injectable({
  providedIn: 'root',
})
export class MailService {
  constructor(private apiservice: ApiService) {}

  getUnreadMails() {
    return this.apiservice.get(Controller.MAIL, '/get-unread');
  }

  getEveryMail() {
    return this.apiservice.get(Controller.MAIL, '/get-all');
  }

  readMail(id: number) {
    return this.apiservice.put(Controller.MAIL, '/read/' + id);
  }
}
