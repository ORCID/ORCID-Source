//Use in child modules that do not import CommonNg2 and do not have access to orderByNg2 pipe

import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "orderBy"
})

//@Injectable()
export class OrderByPipe implements PipeTransform {

    transform(array: Array<string>, args: any, ascending: boolean): Array<string> {

        let logicalOperation: any = '';

        if (array==null) {
            return null;
        }
        if ( ascending == null ){
            ascending = true;
        }
        
        for ( let i = 0; i < args.length; i++ ){
            
            let argsStringArray: any = '';
            let temp = args[i].split('.');

            for ( let j = 0; j < temp.length; j++ ){
                argsStringArray += '["' + temp[j] + '"]';
                if( j == temp.length - 1 ){ //all sublevels added, lets concat with A and B
                    
                    /*if( i == 0) { //yes, i, not j, because the first condition is different than the rest
                        logicalOperation += 'b' + argsStringArray + ' - a' + argsStringArray;
                    } else {
                        logicalOperation += 'String(a' + argsStringArray + ').localeCompare(String(b' + argsStringArray + '))';
                    }*/
                    if( ascending ) {
                        logicalOperation += 'String(a' + argsStringArray + ').localeCompare(String(b' + argsStringArray + '))';
                    } else {
                        logicalOperation += 'String(b' + argsStringArray + ').localeCompare(String(a' + argsStringArray + '))';
                    }
                
                }
            }
            if( i < args.length - 1){ //We add an || logical operator if there is more than one order condition, except for the last condition
                logicalOperation += ' || '
            }
        }

        //Multiple level sort for string and objects with multiple arguments
        array.sort((a: any, b: any): any => {
            
            if ( typeof(args) == 'string' ) {
                if( ascending ){
                    if ( a[args] < b[args] ){
                        return -1;
                    }else if( a[args] > b[args] ){
                        return 1;
                    }else{
                        return 0;    
                    }
                } else {
                    if ( a[args] < b[args] ){
                        return 1;
                    }else if( a[args] > b[args] ){
                        return -1;
                    }else{
                        return 0;    
                    }
                }                
            } else if ( typeof(args) == 'object' ) {
                let result = eval(logicalOperation);
                return result;
            }
        });
        
        
        return array;
    }
}