declare var angular: any;

import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "unique"
})

@Injectable()
export class UniquePipe implements PipeTransform {
    transform(items: any, filterOn: any): any {
        if (filterOn === false) {
            return items;
        }

        if (
            (filterOn || angular.isUndefined(filterOn)) 
            && angular.isArray(items)) {
                var hashCheck = {}, newItems = [];

                var extractValueToCompare = function (item) {
                    if (angular.isObject(item) && angular.isString(filterOn)) {
                        return item[filterOn];
                    } else {
                        return item;
                    }
                };

                angular.forEach(items, function (item) {
                var valueToCheck, isDuplicate = false;

                    for (var i = 0; i < newItems.length; i++) {
                        if (angular.equals(extractValueToCompare(newItems[i]), extractValueToCompare(item))) {
                            isDuplicate = true;
                            break;
                        }
                    }
                    if (!isDuplicate && item[filterOn]!=null 
                        && item[filterOn]!=undefined) {
                        //console.log(item);
                        newItems.push(item);
                    }

                });
                items = newItems;
        }
        return items;
    }
}