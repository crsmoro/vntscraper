import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { map } from 'rxjs/operators';

@Injectable()
export class AuthService {
  // store logged user
  user : any;
  // store the URL so we can redirect after logging in
  redirectUrl: string;
  
  constructor(private http : Http, private router: Router) {}

  login(username : string, password : string) {
    let body = new FormData();
    body.append('username', username);
    body.append('password', password);    
    this.http.post('Login.vnt', body).pipe(map(res => {
      if (res.json().success) {
        if (this.redirectUrl) {
          this.router.navigate([this.redirectUrl]);
        }
        else {
          this.router.navigate(['/home']);
        }
      }
    })).subscribe(res => {});
  }

  logout(): void {
    this.http.get('Logout.vnt').pipe(map(res => {
      let returnObject = res.json();
      if (returnObject.success) {
        this.user = null;
        this.router.navigate(['/login']);
      }
    })).subscribe(res => {});
  }
  
  checkLogin() : Observable<boolean> {
    return this.http.get('Session.vnt').pipe(map((response) => {
      let returnObject = response.json();
      this.user = returnObject.data;
      return returnObject.success;
    }));
  }
}