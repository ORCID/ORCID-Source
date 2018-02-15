import { Injectable } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Observable';

import { Subject }
    from 'rxjs/Subject';

import 'rxjs/Rx';

@Injectable()
export class ModalService {

    private notify = new Subject<any>();
    
    notifyObservable$ = this.notify.asObservable();

    constructor(){

    }

    notifyOther(data: any): void {
        if (data) {
            this.notify.next(data);
        }
    }
}
