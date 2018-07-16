angular.module('orcidApp').filter('workExternalIdentifierHtml', function($filter){
    return function(workExternalIdentifier, first, last, length, moreInfo){
        var id = null;
        var isPartOf = false;
        var link = null;
        var ngclass = '';
        var output = '';
        var type = null;
        
        if (moreInfo == false || typeof moreInfo == 'undefined') ngclass = 'truncate-anchor';
        
        if(workExternalIdentifier.relationship != null && workExternalIdentifier.relationship.value == 'part-of')
            isPartOf = true;        
        if (workExternalIdentifier == null){
            return output;
        } 
        if (workExternalIdentifier.externalIdentifierId == null) {
            return output;        
        }
        
        id = workExternalIdentifier.externalIdentifierId.value;
        type;
        
        if (workExternalIdentifier.externalIdentifierType != null) {
            type = workExternalIdentifier.externalIdentifierType.value;        
        }
        if (type != null && typeof type != 'undefined') {
            type.escapeHtml();
            if(isPartOf) {
                output = output + "<span class='italic'>" + om.get("common.part_of") + " <span class='type'>" + type.toUpperCase() + "</span></span>: ";
            }
            else {
                output = output + "<span class='type'>" + type.toUpperCase() + "</span>: ";
            }
        }
        
        if (workExternalIdentifier.url != null && workExternalIdentifier.url.value != '') {
            link = workExternalIdentifier.url.value;
        }
        else {
            link = workIdLinkJs.getLink(id,type);   
        }
        if (link != null) {         
            link = $filter('urlProtocol')(link);            
            output = output + '<a href="' + link.replace(/'/g, "&#39;") + '" class ="' + ngclass + '"' + " target=\"orcid.blank\" ng-mouseenter=\"showURLPopOver(work.putCode.value + $index)\" ng-mouseleave=\"hideURLPopOver(work.putCode.value + $index)\">" + id.escapeHtml() + '</a>';            
        } else {
            if( id ){
                output += id.escapeHtml();        
                
            }
        }
        
        if( link != null ) {
            output += '<div class="popover-pos">\
                <div class="popover-help-container">\
                    <div class="popover bottom" ng-class="{'+"'block'"+' : displayURLPopOver[work.putCode.value + $index] == true}">\
                        <div class="arrow"></div>\
                        <div class="popover-content">\
                            <a href="'+link+'" target="orcid.blank" class="ng-binding">'+link.escapeHtml()+'</a>\
                        </div>\
                    </div>\
                </div>\
          </div>';
       }
            
       return output;
    };
});