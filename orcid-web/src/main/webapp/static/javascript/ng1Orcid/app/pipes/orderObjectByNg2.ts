import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "orderObjectBy"
})

@Injectable()
export class OrderObjectByPipe implements PipeTransform {
    transform(items: any, field: any, reverse: any): any {
        console.log('OrderObjectByPipe', items, field, reverse);
        var filtered = [];
        items.forEach(
            function(item) {
                filtered.push(item);
            }
        );
        filtered.sort(function (a, b) {
          return (a[field] > b[field] ? 1 : -1);
        });
        if(reverse) {
            filtered.reverse();
        } 
        return filtered;
    }
}