import { AppRoutingModule } from './app-routing.module';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { LoginModule } from './login/login.module';
import { MainRoutingModule } from './main/main-routing.module';
import { MainModule } from './main/main.module';
import { PageNotFoundComponent } from './not-found.component';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';

@NgModule( {
    imports: [
        BrowserModule,
        HttpModule,
        HttpClientModule,
        LoginModule,
        MainModule,
        AppRoutingModule
    ],
    declarations: [
        AppComponent,
        PageNotFoundComponent
    ],
    bootstrap: [AppComponent]
} )
export class AppModule { }
