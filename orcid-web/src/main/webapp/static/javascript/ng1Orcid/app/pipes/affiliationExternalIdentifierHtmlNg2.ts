import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

/*
import { UrlProtocolPipe }
    from '../urlProtocolNg2.ts';
*/

@Pipe({
    name: "affiliationExternalIdentifierHtml"
})

@Injectable()
export class AffiliationExternalIdentifierHtmlPipe implements PipeTransform {

    constructor( /*private urlProtocol: UrlProtocolPipe*/ ){

    }

    transform(externalIdentifier: any, putCode: any, index: any): string {
        console.log(externalIdentifier);
        var isPartOf = false;
        var link = null;
        var ngclass = '';
        var output = '';
        var value = null;    
        var type = null;    
        
        if (externalIdentifier == null) {
            return output;
        }

        if(externalIdentifier.relationship != null && externalIdentifier.relationship.value == 'part-of') {
            isPartOf = true;     
        }

        if(externalIdentifier.value != null){
            value = externalIdentifier.value.value;
        }
        
        if(externalIdentifier.url != null) {
            link = externalIdentifier.url.value;
        }

        if (externalIdentifier.type != null) {
            type = externalIdentifier.type.value;        
        }

        if (type != null && typeof type != 'undefined') {
            type.escapeHtml();
            if(isPartOf) {
                output += "<span class='italic'>" + om.get("common.part_of") + " " + type.toUpperCase() + "</span>: ";
            }
            else {
                output += type.toUpperCase() + ": ";
            }
        }
        
 
        if(link != null) {
            //link = null;//this.urlProtocol(link);
            
            if(value != null) {
                output += "<a href='" + link + "' class='truncate-anchor inline' target='orcid.blank' (mouseenter)='showAffiliationExtIdPopOver(" + putCode + index +")' (mouseleave)='hideAffiliationExtIdPopOver(" + putCode + index +")'>" + value.escapeHtml() + "</a>";
            }
        } else if(value != null) {
            output = output + " " + value.escapeHtml();
        }
        
        if( link != null ) {            
            output += '<div class="popover-pos">\
                        <div class="popover-help-container">\
                            <div class="popover bottom" [ngClass]="{'+"'block'"+' : displayAffiliationExtIdPopOver[' + putCode + index + '] == true}">\
                                <div class="arrow"></div>\
                                <div class="popover-content">\
                                    <a href="'+link+'" target="orcid.blank" class="ng-binding">'+link.escapeHtml()+'</a>\
                                </div>\
                            </div>\
                        </div>\
                  </div>';
        }
        console.log(output);
        return output;
    }
}