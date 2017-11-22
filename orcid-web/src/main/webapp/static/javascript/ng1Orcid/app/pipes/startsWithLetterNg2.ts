import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "startsWithLetter"
})

@Injectable()
export class StartsWithLetterPipe implements PipeTransform {
    transform(items: any, letter: any): any {
        var filtered = [];
        var letterMatch = new RegExp(letter, 'i');
        var item = null;
        for (var i = 0; i < items.length; i++) {
            item = items[i];
            if (letterMatch.test(item.name.substring(0, 1))) {
                filtered.push(item);
            }
        }
        return filtered;
    }
}