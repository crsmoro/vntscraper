import { Component, ViewChild } from '@angular/core';
import { CRUDResourceService } from '../resource/crud-resource-service';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { HttpClient, HttpParams } from '@angular/common/http'
import { NgSelectComponent } from '@ng-select/ng-select'
import { NgForm } from '@angular/forms'

@Component( {
    selector: 'search-component',
    templateUrl: './search.html'
} )
export class SearchComponent {

    private search: string = '';

    private categories: Observable<any[]>;

    private trackers: any[];

    private seedboxes: any[];

    private queryParameters: any = {};

    private torrentFields: any[] = [{ field: 'tracker', label: 'Tracker' }, { field: 'name', label: 'Name' }, { field: 'size', label: 'Size' }, { field: 'added', label: 'Added' }, { field: 'category', label: 'Category' }];

    private selectedTracker: any;

    private totalSearchs: number = 0;

    private loading: boolean = false;

    private sendingTorrentToSeedbox:any = {};

    private urlSearchParams: URLSearchParams;

    private results: any[] = [];

    private job: any;

    private selectedTorrent : any;

    @ViewChild( 'torrentField' )
    private torrentField: NgSelectComponent;

    constructor( private resource: CRUDResourceService, private httpClient: HttpClient ) {
        this.resource.setUrl( 'Trackers.vnt' );
        this.resource.findAll().subscribe( trackers => {
            this.trackers = trackers.filter( tracker => tracker.avaliable );
        } );
        this.categories = of( [] );
        this.categories = this.httpClient.get( 'TrackerCategories.vnt' );
        this.httpClient.get( 'Seedboxes.vnt' ).subscribe((seedboxes:any[]) => this.seedboxes = seedboxes);
    }

    searchTrackers( form: NgForm ) {
        if ( form.valid ) {
            this.results = [];
            this.urlSearchParams = new URLSearchParams();
            this.urlSearchParams.append( 'search', this.search );
            if ( this.queryParameters ) {
                if ( this.queryParameters.trackerCategories ) {
                    this.queryParameters.trackerCategories.forEach(( trackerCategory: any ) => {
                        this.urlSearchParams.append( 'category', trackerCategory.tracker + '|' + trackerCategory.code );
                    } );
                }
                if ( this.queryParameters.torrentFilters ) {
                    this.queryParameters.torrentFilters.forEach(( torrentFilter: any ) => {
                        this.urlSearchParams.append( 'torrentfiltername', torrentFilter.field );
                        this.urlSearchParams.append( 'torrentfilteroperation', torrentFilter.operation );
                        this.urlSearchParams.append( 'torrentfiltervalue', torrentFilter.value );
                    } );
                }
            }
            if ( this.selectedTracker ) {
                this.loading = true;
                this.totalSearchs = 1;
                this.searchTracker( this.selectedTracker );
            }
            else {
                this.totalSearchs = this.trackers.length;
                this.loading = true;
                this.trackers.forEach( tracker => {
                    this.searchTracker( tracker );
                } );
            }
        }
    }

    searchTracker( tracker: any, page?: number ) {
        this.urlSearchParams.set( 'tracker', tracker.name );
        if ( page ) {
            this.urlSearchParams.set( 'page', page.toString() );
        }
        this.httpClient.post( 'SearchTrackers.vnt', this.urlSearchParams.toString(), {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
            responseType: 'json'
        } ).subscribe(( data: any ) => {
            if ( data && data.success ) {
                let result = this.results.find(( value: any ) => value.tracker.name == data.data.tracker.name );
                if ( !result ) {
                    result = {
                        tracker: data.data.tracker,
                        page: 0,
                        torrents: data.data.torrents
                    };
                    this.results.push( result );
                }
                else {
                    result.page = page ? page : 0;
                    result.torrents = data.data.torrents;
                }
            }
            else if ( data && data.error ) {
                alert( data.error.message );
                console.error( data.error.stack );
            }
            else {
                alert( 'Everything went wrong, run to the mountains!' );
            }
        },
            ( error: any ) => { 
                this.totalSearchs--;
                if ( this.totalSearchs <= 0 ) {
                    this.loading = false;
                    console.log( 'cabo tudo' );
                }
            },
            () => {
                this.totalSearchs--;
                if ( this.totalSearchs <= 0 ) {
                    this.loading = false;
                    console.log( 'cabo tudo' );
                }
            } );
    }

    selectTracker( tracker: any ) {
        if (!this.selectedTracker || !tracker || this.selectedTracker.name != tracker.name) {
            this.categories = this.httpClient.get( 'TrackerCategories.vnt?tracker=' + ( tracker ? tracker.name : '' ) );
            this.selectedTracker = tracker;
            if (this.queryParameters && this.queryParameters.trackerCategories && tracker) {
                this.queryParameters.trackerCategories = [];
            }
        }
    }

    torrentFieldSelected( item: any ) {
        if ( item ) {
            if ( !this.queryParameters.torrentFilters ) {
                this.queryParameters.torrentFilters = [];
            }
            this.queryParameters.torrentFilters.push( { field: item.field, operation: '', value: '' } );
            this.torrentField.clearModel();
        }
    }

    removeTorrentFilter( event: any, i: number ) {
        this.queryParameters.torrentFilters.splice( i, 1 );
    }

    findTorrentFieldLabel( field: string ): string {
        let item = this.torrentFields.find( torrentField => torrentField.field == field );
        return item ? item.label : '';
    }

    trackByIndex( index: number, obj: any ): any {
        return index;
    }

    priorPage( tracker: string, page: number ) {
        page--;
        if ( page <= 0 ) {
            page = 0;
        }
        this.searchTracker( tracker, page );
    }

    nextPage( tracker: string, page: number ) {
        page++;
        this.searchTracker( tracker, page );
    }

    readableFileSize( size: number ): string {
        var units = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
        var i = 0;
        while ( size >= 1024 ) {
            size /= 1024;
            ++i;
        }
        return size.toFixed( 1 ) + ' ' + units[i];
    }

    createSchedule( tracker: string ): void {
        this.job = {
            queryParameters: this.queryParameters
        }
        this.job.queryParameters.search = this.search;
        this.job.tracker = tracker;
    }

    doneSchedule() {
        this.job = undefined;
    }

    downloadTorrent( torrent: any ) {
        location.href = 'DownloadTorrent.vnt?chave=' + torrent.chave;
    }
    
    uploadTorrent(seedbox:any, torrent:any) {
        if (confirm('Are you sure?')) {
            this.sendingTorrentToSeedbox[torrent.id] = true;
            this.httpClient.get('UploadTorrentToSeedbox.vnt?seedbox=' + seedbox.id + '&chave=' + torrent.chave).subscribe((data:any) => {
                if (data.success) {
                    alert('Torrent sent with success');
                }
                else {
                    alert('Something went wrong when sending torrent to the seedbox');
                }
                this.sendingTorrentToSeedbox[torrent.id] = false;
            }, (error) => { this.sendingTorrentToSeedbox[torrent.id] = false; });
        }
    }
    
    viewDetails(torrent:any) {
        this.selectedTorrent = torrent;
    }
    
    doneDetail() {
        this.selectedTorrent = undefined;
    }
}