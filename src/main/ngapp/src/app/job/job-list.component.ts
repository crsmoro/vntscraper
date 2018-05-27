import { CRUDResourceService } from '../resource/crud-resource-service';
import { Component, OnInit } from '@angular/core';

@Component( {
    selector: 'job-list-component',
    templateUrl: './job-list.html'
} )
export class JobListComponent implements OnInit {

    private jobs: any[] = [];

    constructor( private resource: CRUDResourceService ) {
        resource.setUrl( 'Jobs.vnt' );
    }
    ngOnInit(): void {        this.resource.findAll().subscribe( jobs => this.jobs = jobs );
    }

    remove( job: any ): void {
        if ( confirm( 'Are you sure?' ) ) {
            this.resource.remove( job.id ).subscribe( res => {
                if ( res ) {
                    this.jobs = this.jobs.filter( trackerUserFilter => job.id != trackerUserFilter.id );
                }
            }, err => alert( 'Error removing job' ) );
        }
    }

}