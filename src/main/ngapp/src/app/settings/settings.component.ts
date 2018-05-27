import { Component, OnInit } from '@angular/core';
import { Http } from '@angular/http';

@Component({
  selector : 'settings-component',
  templateUrl : './settings.html'
})
export class SettingsComponent implements OnInit {
  preferences : any;
  
  settingsGeneral : any;
  
  settingsEmail : any;
  
  editGeneral(general : any) {
    this.settingsGeneral = general;
  }
  
  editEmail(general : any) {
    this.settingsEmail = general;
    if (!this.settingsEmail.mailConfig) {
      this.settingsEmail.mailConfig = {};
    }
  }
  
  constructor(private http : Http) {
    
  }
  ngOnInit(): void {    this.http.get('Preferences.vnt').subscribe(res => {
      let data = res.json();
      this.preferences = data && data.length > 0 ? data[0] : {};
    });

  } 
}