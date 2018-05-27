import { CRUDResourceService } from '../resource/crud-resource-service';
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { Http, Response } from '@angular/http';
import { map, switchMap } from 'rxjs/operators';

@Component( {
    selector: 'job-data-component',
    templateUrl: './job-data.html'
} )
export class JobDataComponent implements OnInit {

    @Input()
    private data?: any;
    
    private job: any;

    private trackers : Observable<any[]>;

    private trackerUsers : Observable<any[]>;

    private categories : Observable<any[]>;

    private serviceParsers : Observable<any[]>;

    private torrentFields : any[] = [{field:'tracker', label:'Tracker'}, {field:'name', label:'Name'}, {field:'year', label:'Year'}, {field:'size', label:'Size'}, {field:'added', label:'Added'}, {field:'category', label:'Category'}];
    
    @Output('done')
    private doneEvent? : EventEmitter<void> = new EventEmitter<void>();

    constructor(private http: Http, private resource: CRUDResourceService, private route: ActivatedRoute, private router: Router ) {
        resource.setUrl( 'Jobs.vnt' );
        this.trackers = this.http.get('Trackers.vnt').pipe(map((response:Response) => response.json()));
        this.trackerUsers = of([]);
        this.categories = of([]);
        this.serviceParsers = this.http.get('ServiceParsers.vnt').pipe(map((response:Response) => response.json()));
    }

    ngOnInit(): void {
        this.route.params.pipe(
            switchMap(( params: Params ) => { if ( params.id ) { return this.resource.findOne( params.id) } else { return this.data ? of(this.data) : of( { queryParameters : {} } ); } } )
            ).subscribe( job => { job.tracker = !job.tracker && job.trackerUser ? job.trackerUser.tracker : job.tracker; this.job = job; this.trackerSelected(); }, e => {console.log(e) });
    }

    onSubmit( form: NgForm ) {
        if ( form.valid ) {
            this.resource.save( this.job ).subscribe( res => { if (!this.data) { this.router.navigate( ['/jobs'] ); } this.doneEvent.emit(); }, err => alert( 'Erro ao salvar os dados' ) );;
        }
        return false;
    }
    
    close() {
        if (!this.data) {
            this.router.navigate(['/jobs']);
        }
        this.doneEvent.emit();
    }
    
    trackerSelected() {
        this.trackerUsers = this.http.get('TrackerUsers.vnt?tracker=' + this.job.tracker).pipe(map((response:Response) => response.json()));
        this.categories = this.http.get('TrackerCategories.vnt?tracker=' + this.job.tracker).pipe(map((response:Response) => response.json()));
    }
    
    torrentFieldSelected(event : any) {
        if (event.target.value) {
            if (!this.job.queryParameters.torrentFilters) {
                this.job.queryParameters.torrentFilters = [];
            }
            this.job.queryParameters.torrentFilters.push({field : event.target.value, operation:'', value:''});
            event.target.options[0].selected = true;
        }
    }
    
    removeTorrentFilter(event : any, i : number) {
        this.job.queryParameters.torrentFilters.splice(i, 1);
    }
    
    findTorrentFieldLabel(field:string) : string {
        let item = this.torrentFields.find(torrentField => torrentField.field == field);
        return item ? item.label : '';
    }
    
    trackByIndex(index: number, obj: any): any {
        return index;
    }
}