angular.module('orcidApp').filter('affiliationExternalIdentifierHtml', ['$filter', function($filter){
    return function(externalIdentifier, first, last, length, moreInfo){
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

        if(externalIdentifier.value != null){
            value = externalIdentifier.value.value;
        }
        
        if(externalIdentifier.url != null) {
            link = externalIdentifier.url.value;
        }
        
 
        if(link != null) {
            link = $filter('urlProtocol')(link);
            
            if(value != null) {
                output += "<a href='" + link + "' class='truncate-anchor' target='orcid.blank' ng-mouseenter='showURLPopOver(funding.putCode.value+ $index)' ng-mouseleave='hideURLPopOver(funding.putCode.value + $index)'>" + value.escapeHtml() + "</a>";
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
    };
}]);