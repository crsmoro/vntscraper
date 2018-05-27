import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, CanActivate } from '@angular/router';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs/Observable';
import { map } from 'rxjs/operators';


@Injectable()
export class AuthAdminGuard implements CanActivate {
  constructor(private authService: AuthService) { }

  canActivate(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | Observable<boolean> {
    return this.authService.user ? this.authService.user.admin : this.authService.checkLogin().map(response => this.authService.user.admin);
  }
}