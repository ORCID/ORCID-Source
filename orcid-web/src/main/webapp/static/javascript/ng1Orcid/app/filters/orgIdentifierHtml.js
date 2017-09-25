angular.module('orcidApp').filter('orgIdentifierHtml', ['affiliationsSrvc', '$filter', function(affiliationsSrvc, $filter){
    return function(value, type, putCode){
        var GRID_BASE_URL = "https://www.grid.ac/institutes/";
        var TEST_BASE_URL = "https://orcid.org/";
        var link = null;
        var output = '';       

        if (value == null) {
            return output;
        }

        //disambiguation-source is not enumerated; using types that currently exist in orcid db
        if (type != null) {
            if (type == 'TEST') {
                link = TEST_BASE_URL + value;
                output += '<div class="col-md-2 no-padding-right">Test Id:</div>';
            } else if (type == 'FUNDREF') {
                link = value;
                output += '<div class="col-md-2 no-padding-right">' + om.get('affiliation.org_id.value.label.fundref') + ": </div>";
            } else if (type.value == 'GRID') {
                link = GRID_BASE_URL + value;
                output += '<div class="col-md-2 no-padding-right">' + om.get('affiliation.org_id.value.label.grid') + ": </div>";
            } else if (type == 'RINGGOLD') {
                output += '<div class="col-md-2 no-padding-right">' + om.get('affiliation.org_id.value.label.ringgold') + ": </div>";
            } else {
                link = null;
                output += '<div class="col-md-2 no-padding-right">' + type + ": </div>";
            }
            
        }         
        
        if(link != null) {
            if(value != null) {
                output += '<div class="col-md-10  no-padding"><a href=' + link + "' class='truncate-anchor' target='orcid.blank' ng-mouseenter='showURLPopOver(" + putCode + ")' ng-mouseleave='hideURLPopOver(" + putCode + ")'>" + value.escapeHtml() + "</a>";
            }
        } else if(value != null) {
            output = output + " " + value.escapeHtml();
        }
        
        if( link != null ) {            
            output += '<div class="popover-pos">\
                        <div class="popover-help-container">\
                            <div class="popover bottom" ng-class="{'+"'block'"+' : displayURLPopOver[' + putCode + '] == true}">\
                                <div class="arrow"></div>\
                                <div class="popover-content">\
                                    <a href="'+link+'" target="orcid.blank" class="ng-binding">'+link.escapeHtml()+'</a>\
                                </div>\
                            </div>\
                        </div>\
                        </div>\
                  </div>';
        }
        
        return output;
    };
}]);