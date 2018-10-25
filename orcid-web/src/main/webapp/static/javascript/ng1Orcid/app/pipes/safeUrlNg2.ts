import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

import { DomSanitizer} from '@angular/platform-browser';

@Pipe({
    name: "safeUrl"
})

@Injectable()
export class SafeUrlPipe implements PipeTransform {
    constructor(private sanitizer: DomSanitizer) {}
    transform(url) {
        console.log("safe url pipe");
        return this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }
}