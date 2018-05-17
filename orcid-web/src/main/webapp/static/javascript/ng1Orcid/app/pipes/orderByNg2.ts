import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "orderBy"
})

//@Injectable()
export class OrderByPipe implements PipeTransform {

    transform(array: Array<string>, args: any, ascending: boolean): Array<string> {
        //console.log(array[0].activities[array[0].activePutCode].startDate.year);
        console.log(args);

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
                if(temp=='startDate' || temp=='endDate'){
                    argsStringArray += '["dateSortString"]';  
                } else {
                    argsStringArray += '["' + temp[j] + '"]';
                }
                if( j == temp.length - 1 ){ //all sublevels added, lets concat with A and B
                    console.log(temp);
                    if( ascending ) {
                        if(temp=='startDate'){
                            logicalOperation += 'String(a' + argsStringArray + ').substring(2,11).localeCompare(String(b' + argsStringArray + ').substring(2,11))';

                        } else {
                            logicalOperation += 'String(a' + argsStringArray + ').localeCompare(String(b' + argsStringArray + '))';
                        }
                    } else {
                        if(temp=='startDate'){
                            logicalOperation += 'String(b' + argsStringArray + ').substring(2,11).localeCompare(String(a' + argsStringArray + ').substring(2,11))';

                        } else{
                            logicalOperation += 'String(b' + argsStringArray + ').localeCompare(String(a' + argsStringArray + '))';
                        }
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
                if(args=='startDate'){
                    if( ascending ){
                        if ( a['dateSortString'].substring(2,11) < b['dateSortString'].substring(2,11) ){
                            return -1;
                        }else if( a['dateSortString'].substring(2,11) > b['dateSortString'].substring(2,11) ){
                            return 1;
                        }else{
                            return 0;    
                        }
                    } else {
                        if ( a['dateSortString'].substring(2,11) < b['dateSortString'].substring(2,11) ){
                            return 1;
                        }else if( a['dateSortString'].substring(2,11) > b['dateSortString'].substring(2,11) ){
                            return -1;
                        }else{
                            return 0;    
                        }
                    } 

                } else if(args=='endDate'){
                    if( ascending ){
                        if ( a['dateSortString'] < b['dateSortString'] ){
                            return -1;
                        }else if( a['dateSortString'] > b['dateSortString']){
                            return 1;
                        }else{
                            return 0;    
                        }
                    } else {
                        if ( a['dateSortString'] < b['dateSortString'] ){
                            return 1;
                        }else if( a['dateSortString'] > b['dateSortString']){
                            return -1;
                        }else{
                            return 0;    
                        }
                    } 

                } else {
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
                }               
            } else if ( typeof(args) == 'object' ) {
                console.log(logicalOperation);
                let result = eval(logicalOperation);
                return result;
            }
        });
        
        
        return array;
    }
}