import { CRUDResourceService } from '../resource/crud-resource-service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'user-list-component',
  templateUrl: './user-list.html'
})
export class UserListComponent implements OnInit {

  private users: any[] = [];

  constructor(private resource: CRUDResourceService) {
    resource.setUrl('Users.vnt');
  }
  ngOnInit(): void {    this.resource.findAll().subscribe(users => this.users = users);
  }
  
  remove(user : any) : void {
    if (confirm('Are you sure?')) {
      this.resource.remove(user.id).subscribe(res => { if (res) { this.users = this.users.filter(userFilter => user.id != userFilter.id); } }, err => alert('Error removing user'));      
    }
  }

}