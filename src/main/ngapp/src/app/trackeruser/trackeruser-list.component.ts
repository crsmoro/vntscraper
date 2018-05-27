import { CRUDResourceService } from '../resource/crud-resource-service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'trackeruser-list-component',
  templateUrl: './trackeruser-list.html'
})
export class TrackerUserListComponent implements OnInit {

  private trackerUsers: any[] = [];

  constructor(private resource: CRUDResourceService) {
    resource.setUrl('TrackerUsers.vnt');
  }
  ngOnInit(): void {    this.resource.findAll().subscribe(trackerUsers => this.trackerUsers = trackerUsers);
  }
  
  remove(trackerUser : any) : void {
    if (confirm('Are you sure?')) {
      this.resource.remove(trackerUser.id).subscribe(res => { if (res) { this.trackerUsers = this.trackerUsers.filter(trackerUserFilter => trackerUser.id != trackerUserFilter.id); } }, err => alert('Error removing tracker user'));      
    }
  }

}