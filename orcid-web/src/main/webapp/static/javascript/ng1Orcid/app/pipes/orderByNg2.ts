import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "orderBy"
})

//@Injectable()
export class OrderByPipe implements PipeTransform {
    transform(array: Array<string>, args: string): Array<string> {
        if (array==null) {
            return null;
        }
        array.sort((a: any, b: any) => {
            if ( a[args] < b[args] ){
                return -1;
            }else if( a[args] > b[args] ){
                return 1;
            }else{
                return 0;    
            }
        });
        return array;
    }
}