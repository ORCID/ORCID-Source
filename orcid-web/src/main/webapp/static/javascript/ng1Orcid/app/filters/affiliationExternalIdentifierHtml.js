angular.module('orcidApp').filter('affiliationExternalIdentifierHtml', ['$filter', function($filter){
    return function(externalIdentifier, putCode, index){
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

        if(externalIdentifier.externalIdentifierId != null){
            value = externalIdentifier.externalIdentifierId.value;
        }
        
        if(externalIdentifier.url != null) {
            link = externalIdentifier.url.value;
        }

        if (externalIdentifier.externalIdentifierType != null) {
            type = externalIdentifier.externalIdentifierType.value;        
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
            link = $filter('urlProtocol')(link);
            
            if(value != null) {
                output += "<a href='" + link + "' class='truncate-anchor inline' target='orcid.blank' ng-mouseenter='showAffiliationExtIdPopOver(" + putCode + index +")' ng-mouseleave='hideAffiliationExtIdPopOver(" + putCode + index +")'>" + value.escapeHtml() + "</a>";
            }
        } else if(value != null) {
            output = output + " " + value.escapeHtml();
        }
        
        if( link != null ) {            
            output += '<div class="popover-pos">\
                        <div class="popover-help-container">\
                            <div class="popover bottom" ng-class="{'+"'block'"+' : displayAffiliationExtIdPopOver[' + putCode + index + '] == true}">\
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