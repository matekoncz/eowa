import { CanActivateFn, Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
   let userService = inject(UserService);
   let router = inject(Router);
   if(userService.getCurrentUser() === null) {
      router.navigate(['/login']);
      return false;
   }
   return true;
};
