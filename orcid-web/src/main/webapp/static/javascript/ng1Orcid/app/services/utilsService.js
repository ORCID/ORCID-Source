angular.module('orcidApp').factory(
    'utilsService', 
    function() {
        var utilsService = {
            contains(arr, obj) {
                var index = arr.length;
                while (index--) {
                    if (arr[index] === obj) {
                       return true;
                    }
                }
                return false;
            },

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
            },

            getParameterByName: function( name ) {
                var _name = name,
                    regex = new RegExp("[\\?&]" + _name + "=([^&#]*)"),
                    results = regex.exec(location.search)
                ;
                
                _name = _name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
                
                return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
            }
        };
        return utilsService;
    }
);