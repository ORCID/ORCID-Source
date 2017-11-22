declare var Date: any;

import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "humanDate"
})

@Injectable()
export class HumanDatePipe implements PipeTransform {
    //var standardDateFilter = $filter('date');

    transform(input: any): any {
        var inputDate = new Date(input);
        var dateNow = new Date();
        var dateFormat = (inputDate.getYear() === dateNow.getYear() && inputDate.getMonth() === dateNow.getMonth() && inputDate.getDate() === dateNow.getDate())  ? 'HH:mm' : 'yyyy-MM-dd';
        //return standardDateFilter(input, dateFormat);
        return input; //Remove this pipe and use ng2?
    }
}
