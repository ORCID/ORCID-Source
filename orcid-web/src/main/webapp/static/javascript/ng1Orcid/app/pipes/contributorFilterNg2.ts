declare var addComma: any;
declare var emptyTextField: any;

import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

@Pipe({
    name: "contributorFilter"
})

@Injectable()
export class ContributorFilterPipe implements PipeTransform {
    transform(ctrb: any): string {
        var out = '';
        if (!emptyTextField(ctrb.contributorRole)) {
        	out = out + ctrb.contributorRole.value;
        }
        if (!emptyTextField(ctrb.contributorSequence)) {
        	out = addComma(out) + ctrb.contributorSequence.value;
        }
        if (!emptyTextField(ctrb.orcid)) {
        	out = addComma(out) + ctrb.orcid.value;
        }
        if (!emptyTextField(ctrb.email)) {
        	out = addComma(out) + ctrb.email.value;
        }
        if (out.length > 0) {
        	out = '(' + out + ')';
        }
        return out;
    }
}
