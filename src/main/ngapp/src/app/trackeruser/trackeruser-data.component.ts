import { CRUDResourceService } from '../resource/crud-resource-service';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { Http } from '@angular/http';
import { map, switchMap } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

@Component( {
    selector: 'trackeruser-data-component',
    templateUrl: './trackeruser-data.html'
} )
export class TrackerUserDataComponent implements OnInit {

    private trackerUser: any;

    private trackers : Observable<any[]>;

    constructor(private http: Http, private resource: CRUDResourceService, private route: ActivatedRoute, private router: Router ) {
        resource.setUrl( 'TrackerUsers.vnt' );
        this.trackers = this.http.get('Trackers.vnt').pipe(map(response => response.json()));
    }
    ngOnInit(): void {
        this.route.params.pipe(
            switchMap(( params: Params ) => { if ( params.id ) { return this.resource.findOne( params.id ); } else { return of( {} ); } } )
            ).subscribe( trackerUser => this.trackerUser = trackerUser );
    }

    onSubmit( form: NgForm ) {
        if ( form.valid ) {
            this.resource.save( this.trackerUser ).subscribe( res => { this.router.navigate( ['/trackerusers'] ); }, err => alert( 'Erro ao salvar os dados' ) );;
        }
        return false;
    }
}