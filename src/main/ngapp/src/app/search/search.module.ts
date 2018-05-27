import { TorrentDetailComponent, SafeHtmlPipe } from './torrent-detail.component';
import { SearchComponent} from './search.component';
import { NgModule }       from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap'
import { CRUDResourceService } from '../resource/crud-resource-service';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { NgSelectModule } from '@ng-select/ng-select';
import { JobModule } from '../job/job.module'

@NgModule({
  imports: [
    NgbModule,
    BrowserModule,
    FormsModule,
    NgSelectModule,
    JobModule
  ],
  declarations: [
    SearchComponent,
    TorrentDetailComponent,
    SafeHtmlPipe
  ],
  providers : [CRUDResourceService]
})
export class SearchModule { }