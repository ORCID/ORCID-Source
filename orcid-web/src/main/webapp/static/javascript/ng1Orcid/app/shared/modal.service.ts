import { Injectable } 
    from '@angular/core';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map } 
    from 'rxjs/operators';

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
