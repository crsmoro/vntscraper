import { CRUDResourceService } from '../resource/crud-resource-service';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { switchMap } from 'rxjs/operators';
import { of } from 'rxjs/observable/of';

@Component( {
    selector: 'seedbox-data-component',
    templateUrl: './seedbox-data.html'
} )
export class SeedboxDataComponent implements OnInit {

    private seedbox: any;

    constructor( private resource: CRUDResourceService, private route: ActivatedRoute, private router: Router ) {
        resource.setUrl( 'Seedboxes.vnt' );
    }
    ngOnInit(): void {
        this.route.params.pipe(
            switchMap(( params: Params ) => { if ( params.id ) { return this.resource.findOne( params.id ); } else { return of( {} ); } } )
            ).subscribe( seedbox => this.seedbox = seedbox );
    }

    onSubmit( form: NgForm ) {
        if ( form.valid ) {
            this.resource.save( this.seedbox ).subscribe( res => { this.router.navigate( ['/seedboxes'] ); }, err => alert( 'Erro ao salvar os dados' ) );;
        }
        return false;
    }
}