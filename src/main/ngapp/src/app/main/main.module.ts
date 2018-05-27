import { SearchModule } from '../search/search.module';
import { SettingsModule } from '../settings/settings.module';
import { UserModule } from '../user/user.module';
import { SeedboxModule } from '../seedbox/seedbox.module';
import { TrackerUserModule } from '../trackeruser/trackeruser.module';
import { JobModule } from '../job/job.module';
import { MainRoutingModule } from './main-routing.module';
import { MainComponent } from './main.component';
import { NgModule }       from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap'

@NgModule({
  imports: [
    NgbModule.forRoot(),
    MainRoutingModule,
    BrowserModule,
    SearchModule,
    SettingsModule,
    UserModule,
    SeedboxModule,
    TrackerUserModule,
    JobModule
  ],
  declarations: [
    MainComponent
  ]
})
export class MainModule {}