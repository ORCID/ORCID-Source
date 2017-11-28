declare var latexParseJs: any;

import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "latex"
})

@Injectable()
export class LatexPipe implements PipeTransform {
    transform(input: string): string {
        if (input == null) {
        	return "";
        } 
        return latexParseJs.decodeLatex(input);
    }
}
