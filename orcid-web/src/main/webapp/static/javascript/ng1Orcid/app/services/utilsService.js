angular.module('orcidApp').factory(
    'utilsService', 
    function() {
        var utilsService = {
            formColorBoxResize: function() {
                if (isMobile()) {
                    $.colorbox.resize({width: formColorBoxWidth(), height: '100%'});
                }
                else {
                    // IE8 and below doesn't take auto height
                    // however the default div height
                    // is auto anyway
                    $.colorbox.resize({width:'800px'});
                    
                }
            }
        };
        return utilsService;
    }
);