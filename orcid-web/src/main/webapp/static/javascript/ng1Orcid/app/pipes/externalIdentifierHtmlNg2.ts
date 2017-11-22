import { Injectable, Pipe, PipeTransform } 
    from '@angular/core';

/*
import { UrlProtocolPipe }
    from './../urlProtocolNg2.ts';
*/
@Pipe({
    name: "externalIdentifierHtml"
})

@Injectable()
export class ExternalIdentifierHtmlPipe implements PipeTransform {

    constructor( /*private urlProtocol: UrlProtocolPipe */ ){

    }

    transform(externalIdentifier: any, first: any, last: any, length: any, type: any, moreInfo: any): string {
        var isPartOf = false;
        var link = null;
        var ngclass = '';
        var output = '';
        var value = null;        

        if (externalIdentifier == null) {
            return output;
        }

        if(externalIdentifier.relationship != null && externalIdentifier.relationship.value == 'part-of') {
            isPartOf = true;     
        }

        // If type is set always come: "grant_number"
        if (type != null) {
            if(isPartOf){
                output += "<span class='italic'>" + om.get("common.part_of") + "</span>&nbsp";
            }
            if (type.value == 'grant') {
                output += om.get('funding.add.external_id.value.label.grant') + ": ";
            } else if (type.value == 'contract') {
                output += om.get('funding.add.external_id.value.label.contract') + ": ";
            } else {
                output += om.get('funding.add.external_id.value.label.award') + ": ";
            }
            
        }         
        
        if(externalIdentifier.value != null){
            value = externalIdentifier.value.value;
        }
        
        if(externalIdentifier.url != null) {
            link = externalIdentifier.url.value;
        }
 
        if(link != null) {
            link = null;//this.urlProtocol(link);
            
            if(value != null) {
                output += "<a href='" + link + "' class='truncate-anchor' target='orcid.blank' ng-mouseenter='showURLPopOver(funding.putCode.value+ $index)' ng-mouseleave='hideURLPopOver(funding.putCode.value + $index)'>" + value.escapeHtml() + "</a>";
            } else {
                if(type != null) {
                    if (moreInfo == false || typeof moreInfo == 'undefined') {
                        ngclass = 'truncate-anchor';
                    }
                    
                    if(type.value == 'grant') {
                        output = om.get('funding.add.external_id.url.label.grant') + ': <a href="' + link + '" class="' + ngclass + '"' + " target=\"orcid.blank\" ng-mouseenter=\"showURLPopOver(funding.putCode.value + $index)\" ng-mouseleave=\"hideURLPopOver(funding.putCode.value + $index)\">" + link.escapeHtml() + "</a>";
                    } else if(type.value == 'contract') {
                        output = om.get('funding.add.external_id.url.label.contract') + ': <a href="' + link + '" class="' + ngclass + '"' + " target=\"orcid.blank\" ng-mouseenter=\"showURLPopOver(funding.putCode.value + $index)\" ng-mouseleave=\"hideURLPopOver(funding.putCode.value + $index)\">" + link.escapeHtml() + "</a>";
                    } else {
                        output = om.get('funding.add.external_id.url.label.award') + ': <a href="' + link + '" class="' + ngclass + '"' + " target=\"orcid.blank\" ng-mouseenter=\"showURLPopOver(funding.putCode.value + $index)\" ng-mouseleave=\"hideURLPopOver(funding.putCode.value + $index)\">" + link.escapeHtml() + "</a>";
                    }
                    
                }               
            }
        } else if(value != null) {
            output = output + " " + value.escapeHtml();
        }
        
        if( link != null ) {            
            output += '<div class="popover-pos">\
                        <div class="popover-help-container">\
                            <div class="popover bottom" ng-class="{'+"'block'"+' : displayURLPopOver[funding.putCode.value + $index] == true}">\
                                <div class="arrow"></div>\
                                <div class="popover-content">\
                                    <a href="'+link+'" target="orcid.blank" class="ng-binding">'+link.escapeHtml()+'</a>\
                                </div>\
                            </div>\
                        </div>\
                  </div>';
        }
        
        return output;
    }
}