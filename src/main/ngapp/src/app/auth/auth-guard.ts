import { Injectable } from '@angular/core';
import {
  CanActivate, Router,
  ActivatedRouteSnapshot,
  RouterStateSnapshot
} from '@angular/router';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs/Observable';
import { map } from 'rxjs/operators';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>|Promise<boolean>|boolean {
    let url: string = state.url;

    return this.checkLogin(url);
  }

  checkLogin(url: string): Observable<boolean> {
    this.authService.redirectUrl = url;
    
    return this.authService.checkLogin().pipe(map(success => {
      if (!success) {
          console.log(this.authService.redirectUrl);
          this.router.navigate(['/login']);
          return false;          
      }
      else {
        return true;
      }
    }));
  }
}