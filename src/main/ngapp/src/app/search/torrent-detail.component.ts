import { Component, Input, OnInit, Output, EventEmitter, PipeTransform, Pipe } from '@angular/core';
import { HttpClient } from '@angular/common/http'
import { SearchComponent } from './search.component';

import { DomSanitizer } from '@angular/platform-browser'

@Pipe( { name: 'safeHtml' } )
export class SafeHtmlPipe implements PipeTransform {
    constructor( private sanitized: DomSanitizer ) { }
    transform( value: any, ...args: any[] ): any {
        return this.sanitized.bypassSecurityTrustHtml( value );
    }
}

@Component( {
    selector: 'torrent-detail-component',
    template: `
    <div class="card">
      <h5 class="card-header">{{torrent?.name}}</h5>
      <div class="card-body">
        <h5 class="card-title">{{torrent?.category}} - {{torrent?.added}} - {{parent.readableFileSize(torrent.size)}}</h5>
        <div *ngIf="!detailLoaded" class="alert alert-info"><h4>Loading More Info <i class="fa fa-circle-o-notch fa-spin fa-fw"></i></h4></div>
        <div *ngIf="detailLoaded && !movie && !hideNoMore" class="alert alert-warning"><h4>No movie information to load</h4></div>
        <div class="media" *ngIf="movie">
          <img class="align-self-start" [class.mr-3]="movie.poster" src="{{movie.poster}}" alt="{{movie.title}}">
          <div class="media-body">
            <h5 class="my-0"><a href="{{torrent.imdbLink}}" target="_blank" class="text-body" style="text-decoration: none;" alt="Open IMDB Link" title="Open IMDB Link">{{movie.title}}</a> ({{movie.year}}) {{movie.imdbRating}}/10 ({{movie.imdbVotes}} votes)</h5>
            <h6 class="mt-0"><b>{{movie.originalTitle}}</b></h6>
            <p>{{movie.plot}}</p>
            <p>{{movie.runtime}} min</p>
          </div>
        </div>
        <div *ngIf="detailLoaded" class="card bg-light my-3">
          <h5 class="card-header clickable" alt="Expand/Reduce" title="Expand/Reduce" (click)="showTorrentPage=!showTorrentPage">Torrent Info <i class="fa fa-arrows-alt" aria-hidden="true"></i></h5>
          <div *ngIf="showTorrentPage" class="card-body" [innerHtml]="torrent?.content | safeHtml"></div>
        </div>
        <div class="list-group">
            <a class="clickable list-group-item list-group-item-action" (click)="parent.downloadTorrent(torrent)"><i class="fa fa-cloud-download text-primary" data-toggle="tooltip" data-placement="top" title="Download Torrent"></i> Download Torrent</a>
            <a class="clickable list-group-item list-group-item-action" *ngFor="let seedbox of parent.seedboxes" (click)="parent.uploadTorrent(seedbox, torrent)"><i class="fa fa-cloud-upload text-secondary"></i> Upload to {{seedbox.name}}</a>
            <a (click)="closeDetail()" class="clickable list-group-item list-group-item-action"><i class="fa fa-window-close text-danger" aria-hidden="true"></i> Close details</a>
        </div>
      </div>
    </div>
    `
} )
export class TorrentDetailComponent implements OnInit {

    @Input()
    private torrent: any;

    @Input()
    private parent: SearchComponent;

    @Output( 'done' )
    private doneEvent: EventEmitter<void> = new EventEmitter<void>();

    private detailLoaded: boolean;

    private movie: any;

    private hideNoMore: boolean;

    ngOnInit(): void {
        this.httpClient.get( 'TorrentDetails.vnt?chave=' + this.torrent.chave ).subscribe(( d: any ) => {
            if ( d.success ) {
                this.torrent = d.data.torrent;
                this.movie = d.data.movie;
            }
            this.detailLoaded = true;
            if ( !this.movie ) {
                this.hideNoMoreData();
            }
        }, error => {
            this.detailLoaded = true;
            this.hideNoMoreData();
        } );
    }

    constructor( private httpClient: HttpClient ) {

    }

    closeDetail(): void {
        this.doneEvent.emit();
    }

    hideNoMoreData() {
        setTimeout(() => {
            this.hideNoMore = true;
        }, 5000 );
    }
}