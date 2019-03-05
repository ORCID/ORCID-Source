import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

import { CommonService }
    from './../shared/common.service';

@Pipe({
    name: "extractContentFromBody"
})

@Injectable()
export class ExtractContentFromBodyPipe implements PipeTransform {
    constructor(private commonService: CommonService){
    }

    transform(html: string): string {
        console.log(html);
        return this.commonService.extractContentFromBody(html);
    }
}