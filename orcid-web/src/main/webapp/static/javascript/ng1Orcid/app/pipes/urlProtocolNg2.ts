import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

import { CommonService }
    from './../shared/common.service.ts';

@Pipe({
    name: "urlProtocol"
})

@Injectable()
export class UrlProtocolPipe implements PipeTransform {
    constructor(private commonService: CommonService){
    }

    transform(url: string): string {
        return this.commonService.addUrlProtocol(url);
    }
}