declare var window: any;

import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "uri"
})

@Injectable()
export class UriPipe implements PipeTransform {
    transform(): any {
        return window.encodeURIComponent;
    }
}