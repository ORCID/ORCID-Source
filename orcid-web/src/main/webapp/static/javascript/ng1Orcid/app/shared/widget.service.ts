import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';





import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class WidgetService {
    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();
    public locale: string;


    constructor( private http: HttpClient ){
        this.locale = 'en';
    }

    getLocale(): string {
        return this.locale;
    }

    
    setLocale(locale): void {
        this.locale = locale;
    }

    notifyOther(): void {
        this.notify.next();
    }


}
