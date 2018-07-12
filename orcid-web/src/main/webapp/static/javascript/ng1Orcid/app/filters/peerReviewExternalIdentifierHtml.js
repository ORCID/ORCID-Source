angular.module('orcidApp').filter('peerReviewExternalIdentifierHtml', function($filter){
    return function(peerReviewExternalIdentifier, first, last, length, moreInfo, own){
        
        var id = null;
        var output = '';
        var ngclass = '';
        var isPartOf = false;
        var type = null;
        var link = null;
        ngclass = 'truncate';
        
        if (peerReviewExternalIdentifier == null) return output;
        
        if(peerReviewExternalIdentifier.relationship != null && peerReviewExternalIdentifier.relationship.value == 'part-of')
            isPartOf = true;
        
        if (peerReviewExternalIdentifier.externalIdentifierId == null) return output;
        id = peerReviewExternalIdentifier.externalIdentifierId.value;        
        
        if (peerReviewExternalIdentifier.externalIdentifierType != null)
            type = peerReviewExternalIdentifier.externalIdentifierType.value;
            if (type != null) {
                if(isPartOf)
                    output += "<span class='italic'>" + om.get("common.part_of") + " <span class='type'>" + type.toUpperCase().escapeHtml() + "</span></span>: ";
                else 
                    output += "<span class='type'>" + type.toUpperCase().escapeHtml() + "</span>: ";
            }
        
        if (peerReviewExternalIdentifier.url != null && peerReviewExternalIdentifier.url.value != '')
            link = peerReviewExternalIdentifier.url.value;
        else link = workIdLinkJs.getLink(id,type); 
            
        if (link != null){
            link = $filter('urlProtocol')(link);
            output += '<a href="' + link.replace(/'/g, "&#39;") + '" class =""' + " target=\"orcid.blank\" ng-mouseenter=\"showURLPopOver(peerReview.putCode.value + $index)\" ng-mouseleave=\"hideURLPopOver(peerReview.putCode.value + $index)\">" + id.escapeHtml() + '</a>';
        }else{
            if( id ) {
                output += id.escapeHtml();        
            }
        }
        
        if (length > 1 && !last) output = output + ',';
        
        
        
        if (link != null){
            output += '\
            <div class="popover-pos">\
                <div class="popover-help-container">\
                    <div class="popover bottom" ng-class="{'+"'block'"+' : displayURLPopOver[peerReview.putCode.value + $index] == true}">\
                        <div class="arrow"></div>\
                        <div class="popover-content">\
                            <a href="'+link+'" target="orcid.blank">'+link.escapeHtml()+'</a>\
                        </div>\
                    </div>\
                </div>\
           </div>';
        }
        
       if(own)
            output = '<br/>' + output;
        
       return output;      
      
     
    };
});