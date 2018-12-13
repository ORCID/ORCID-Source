declare var Number: any;

import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "ajaxTickDateToISO8601"
})

@Injectable()
export class AjaxTickDateToISO8601Pipe implements PipeTransform {
    transform(input: any): string {
        if (typeof input != 'undefined'){
            input = new Date(input)
            var str = '';
            if (input.getFullYear()) str += input.getFullYear();
            if (input.getUTCMonth()) {
                str += '-';
                str += Number(input.getUTCMonth() + 1).pad(2);
            }
            if (input.getDay()) {
                str += '-';
                str += Number(input.getUTCDate()).pad(2);
            }
            return str;
        } else {
            return '';
        }
    }
}