import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { map } from 'rxjs/operators';

@Injectable()
export class CRUDResourceService {

  private url: string;

  private headers = new Headers({ 'Content-Type': 'application/json' });

  constructor(private http: Http) { }

  private returnRealURL(url?: string): string {
    let localUrl = this.url;
    if (!localUrl) {
      localUrl = this.url;
    }
    return localUrl;
  }

  setUrl(url: string): void {
    this.url = url;
  }

  getUrl(): string {
    return this.url;
  }

  findOne(id: number, url?: string): Observable<any> {
    return this.http.get(this.returnRealURL(url) + '?id=' + id).pipe(map(response => response.json()));
  }

  findAll(url?: string): Observable<Array<any>> {
    return this.http.get(this.returnRealURL(url)).pipe(map(response => response.json()));
  }

  remove(id: number, url?: string): Observable<boolean> {
    return this.http.delete(this.returnRealURL(url) + '?id=' + id).pipe(map(response => response.json().success));
  }

  save(model: any, url?: string): Observable<any> {
    return this.http.post(this.returnRealURL(url), JSON.stringify(model), this.headers).pipe(map(response => response.json()));
  }
}