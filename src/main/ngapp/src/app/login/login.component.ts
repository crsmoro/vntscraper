import { AuthService } from '../auth/auth.service';
import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'login-component',
  templateUrl: './login.html'
})
export class LoginComponent {
  
  constructor(private authService: AuthService) { }
  
  onSubmit(loginForm: NgForm) {
    if(loginForm.valid) {
      this.authService.login(loginForm.value.username, loginForm.value.password);
    }
  }
}
