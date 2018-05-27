import { CRUDResourceService } from '../resource/crud-resource-service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'seedbox-list-component',
  templateUrl: './seedbox-list.html'
})
export class SeedboxListComponent implements OnInit {

  private seedboxes: any[] = [];

  constructor(private resource: CRUDResourceService) {
    resource.setUrl('Seedboxes.vnt');
  }
  ngOnInit(): void {    this.resource.findAll().subscribe(seedboxes => this.seedboxes = seedboxes);
  }
  
  remove(seedbox : any) : void {
    if (confirm('Are you sure?')) {
      this.resource.remove(seedbox.id).subscribe(res => { if (res) { this.seedboxes = this.seedboxes.filter(seedboxFilter => seedbox.id != seedboxFilter.id); } }, err => alert('Error removing seedbox'));      
    }
  }

}