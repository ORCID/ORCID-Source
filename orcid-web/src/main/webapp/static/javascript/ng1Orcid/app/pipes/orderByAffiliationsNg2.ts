import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "orderByAffiliations"
})

//@Injectable()
export class OrderByAffiliationsPipe implements PipeTransform {

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
                //Handle affiliations sort by startDate and endDate
                if(temp=='startDate' || temp=='endDate'){
                    argsStringArray += '["dateSortString"]';   
                } else {
                    argsStringArray += '["' + temp[j] + '"]';
                }
                if( j == temp.length - 1 ){ //all sublevels added, lets concat with A and B
                    if( ascending ) {
                        //Handle affiliations sort by startDate
                        if(temp=='startDate'){
                            logicalOperation += 'String(a.defaultAffiliation' + argsStringArray + ').substring(2,11).localeCompare(String(b.defaultAffiliation' + argsStringArray + ').substring(2,11))';
                        } else if (temp=='title'){
                            logicalOperation += 'String(a.defaultAffiliation.affiliationName.value).localeCompare(String(b.defaultAffiliation.affiliationName.value))';
                        } else {
                            logicalOperation += 'String(a.defaultAffiliation' + argsStringArray + ').localeCompare(String(b.defaultAffiliation' + argsStringArray + '))';
                        }
                    } else {
                        //Handle affiliations sort by startDate
                        if(temp=='startDate'){
                            logicalOperation += 'String(b.defaultAffiliation' + argsStringArray + ').substring(2,11).localeCompare(String(a.defaultAffiliation' + argsStringArray + ').substring(2,11))';
                        } else if (temp=='title'){
                            logicalOperation += 'String(b.defaultAffiliation.affiliationName.value).localeCompare(String(a.defaultAffiliation.affiliationName.value))';
                        } else{
                            logicalOperation += 'String(b.defaultAffiliation' + argsStringArray + ').localeCompare(String(a.defaultAffiliation' + argsStringArray + '))';
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

                //Handle affiliations sort by startDate
                if(args=='startDate'){
                    if( ascending ){
                        if ( a.defaultAffiliation['dateSortString'].substring(2,11) < b.defaultAffiliation['dateSortString'].substring(2,11) ){
                            return -1;
                        }else if( a.defaultAffiliation['dateSortString'].substring(2,11) > b.defaultAffiliation['dateSortString'].substring(2,11) ){
                            return 1;
                        }else{
                            return 0;    
                        }
                    } else {
                        if ( a.defaultAffiliation['dateSortString'].substring(2,11) < b.defaultAffiliation['dateSortString'].substring(2,11) ){
                            return 1;
                        }else if( a.defaultAffiliation['dateSortString'].substring(2,11) > b.defaultAffiliation['dateSortString'].substring(2,11) ){
                            return -1;
                        }else{
                            return 0;    
                        }
                    } 
                //Handle affiliations sort by endDate
                } else if(args=='endDate'){
                    if( ascending ){
                        if ( a.defaultAffiliation['dateSortString'] < b.defaultAffiliation['dateSortString'] ){
                            return -1;
                        }else if( a.defaultAffiliation['dateSortString'] > b.defaultAffiliation['dateSortString']){
                            return 1;
                        }else{
                            return 0;    
                        }
                    } else {
                        if ( a.defaultAffiliation['dateSortString'] < b.defaultAffiliation['dateSortString'] ){
                            return 1;
                        }else if( a.defaultAffiliation['dateSortString'] > b.defaultAffiliation['dateSortString']){
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
                let result = eval(logicalOperation);
                return result;
            }
        });
        
        return array;
    }
}