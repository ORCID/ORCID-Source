import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: 'dashToSpace'
})

@Injectable()
export class DashToSpacePipe implements PipeTransform {
    transform(value) : any {
        return value.replace("-", " ");
    }
}
