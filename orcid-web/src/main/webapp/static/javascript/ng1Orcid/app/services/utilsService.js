angular.module('orcidApp').factory(
    'utilsService', 
    function() {
        var utilsService = {
            addComma: function(str) {
                if (str.length > 0){
                    return str + ', ';
                } 
                return str;
            },
            contains: function(arr, obj) {
                var index = arr.length;
                while (index--) {
                    if (arr[index] === obj) {
                       return true;
                    }
                }
                return false;
            },
            emptyTextField: function(field) {
                if (field != null
                    && field.value != null
                    && field.value.trim() != '') {
                    return false;
                }
                return true;
            },

            fixZindexIE7: function(target, zindex){
                if(isIE() == 7){
                    $(target).each(
                        function(){
                            $(this).css('z-index', zindex);
                            --zindex;
                        }
                    );
                }
            },
            formatDate: function(oldDate) {
                var date = new Date(oldDate);
                var day = date.getDate();
                var month = date.getMonth() + 1;
                var year = date.getFullYear();
                if(month < 10) {
                    month = '0' + month;
                }
                if(day < 10) {
                    day = '0' + day;
                }
                return (year + '-' + month + '-' + day);
            },
            formatTime: function(unixTimestamp) {
                var date = new Date(unixTimestamp);
                return date.toUTCString();
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

            formColorBoxWidth: function() {
                return isMobile()? '100%': '800px';
            },

            getParameterByName: function( name ) {
                var _name = name,
                    regex = new RegExp("[\\?&]" + _name + "=([^&#]*)"),
                    results = regex.exec(location.search)
                ;
                
                _name = _name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
                
                return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
            },

            getScripts: function(scripts, callback) {
                var progress = 0;
                var internalCallback = function () {        
                    if (++progress == scripts.length - 1) {
                        callback();
                    }
                };    
                scripts.forEach(
                    function(script) {        
                        $.getScript(script, internalCallback);        
                    }
                );
            },

            isEmail: function(email) {
                var re = /\S+@\S+\.\S+/;
                return re.test(email);
            },

            isPrintView: function(path) {
                var re = new RegExp("(/print)(.*)?$");
                if (re.test(path)) {
                    return true;
                } else {
                    return false;
                }
            },

            openImportWizardUrl: function(url) {
                var win = window.open(url, "_target");
                setTimeout( function() {
                    if(!win || win.outerHeight === 0) {
                        //First Checking Condition Works For IE & Firefox
                        //Second Checking Condition Works For Chrome
                        window.location.href = url;
                    }
                }, 250);
                $.colorbox.close();
            }
        };
        return utilsService;
    }
);