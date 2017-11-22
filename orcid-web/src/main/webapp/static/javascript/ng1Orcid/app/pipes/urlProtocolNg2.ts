import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "urlProtocol"
})

@Injectable()
export class UrlProtocolPipe implements PipeTransform {
    transform(url: string): string {
        if (url == null) {
            return url;
        }
        if(!url.startsWith('http')) {               
            if (url.startsWith('//')){              
                url = ('https:' == document.location.protocol ? 'https:' : 'http:') + url;
            } else {
                url = 'http://' + url;    
            }
        }
        return url;
    }
}