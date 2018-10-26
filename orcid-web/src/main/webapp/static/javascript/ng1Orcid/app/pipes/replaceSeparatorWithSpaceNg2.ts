import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

import { CommonService }
    from './../shared/common.service.ts';

@Pipe({
    name: "replaceSeparatorWithSpace"
})

@Injectable()
export class ReplaceSeparatorWithSpacePipe implements PipeTransform {
    constructor(private commonService: CommonService){
    }

    transform(str: string): string {
        return this.commonService.replaceSeparatorWithSpace(str);
    }
}