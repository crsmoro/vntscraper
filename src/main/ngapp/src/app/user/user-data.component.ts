import { CRUDResourceService } from '../resource/crud-resource-service';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { switchMap } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

@Component( {
    selector: 'user-data-component',
    templateUrl: './user-data.html'
} )
export class UserDataComponent implements OnInit {

    private user: any;

    constructor( private resource: CRUDResourceService, private route: ActivatedRoute, private router: Router ) {
        resource.setUrl( 'Users.vnt' );
    }
    ngOnInit(): void {
        this.route.params.pipe(
            switchMap(( params: Params ) => { if ( params.id ) { return this.resource.findOne( params.id ); } else { return of( {} ); } } )
            ).subscribe( user => this.user = user );
    }

    onSubmit( form: NgForm ) {
        if ( form.valid ) {
            this.resource.save( this.user ).subscribe( res => { this.router.navigate( ['/users'] ); }, err => alert( 'Erro ao salvar os dados' ) );;
        }
        return false;
    }
}