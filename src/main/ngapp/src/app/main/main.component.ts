import { AuthService } from '../auth/auth.service';
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector : 'main',
  templateUrl : './main.html'
})
export class MainComponent implements OnInit {
  
  isAdmin : boolean = false;
  jobCount : number = 0;
  
  constructor(private authService: AuthService, private httpClient : HttpClient) {}
  
  logout() {
    this.authService.logout();
  }
  ngOnInit(): void {    this.isAdmin = this.authService.user.admin;
    this.httpClient.get('Jobs.vnt?count=true').subscribe(data => this.jobCount = parseInt(data + ''));
  }
  
}