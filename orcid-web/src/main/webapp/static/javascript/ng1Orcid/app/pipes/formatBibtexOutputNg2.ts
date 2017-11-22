import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "formatBibtexOutput"
})

@Injectable()
export class FormatBibtexOutputPipe implements PipeTransform {
    transform(text: string): string {
        var str = text.replace(/[\-?_?]/, ' ');
        return str.toUpperCase();
    }
}