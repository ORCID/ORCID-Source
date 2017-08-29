angular.module('orcidApp').factory("commonSrvc", ['$rootScope', '$window', function ($rootScope, $window) {
    var commonSrvc = {
        copyErrorsLeft: function (data1, data2) {
            for (var key in data1) {
                if (key == 'errors') {
                    data1.errors = data2.errors;
                } else if (key == 'emailsAdditional'){
                    for (var index in data1.emailsAdditional) {
                        if (data1.emailsAdditional[index] != null) {
                            if(data2.emailsAdditional[index] == undefined){
                                data1.emailsAdditional[index].errors = null;
                            } else {
                                data1.emailsAdditional[index].errors = data2.emailsAdditional[index].errors;
                            }
                        }
                    }
                } else {
                    if (data1[key] != null && data1[key].errors !== undefined) {
                        data1[key].errors = data2[key].errors;
                    }
                };
            };
        },
        shownElement: [],        
        showPrivacyHelp: function(elem, event, offsetArrow){
            var top = angular.element(event.target.parentNode).parent().prop('offsetTop');
            var left = angular.element(event.target.parentNode).parent().prop('offsetLeft');
            var scrollTop = angular.element('.fixed-area').scrollTop();
            
            if (elem === '-privacy'){
                angular.element('.edit-record .bulk-privacy-bar .popover-help-container').css({
                    top: -75,
                    left: 512
                });
            }else{
                if (elem.indexOf('@') > -1) {
                    left = 530; //Emails modal fix
                }
                angular.element('.edit-record .record-settings .popover-help-container').css({
                    top: top - scrollTop - 160,
                    left: left + 25
                });             
            }
            angular.element('.edit-record .record-settings .popover-help-container .arrow').css({                    
                left: offsetArrow
            }); 
            commonSrvc.shownElement[elem] = true;
        },
        showTooltip: function(elem, event, topOffset, leftOffset, arrowOffset){
            var top = angular.element(event.target.parentNode).parent().prop('offsetTop');
            var left = angular.element(event.target.parentNode).parent().prop('offsetLeft');    
            var scrollTop = angular.element('.fixed-area').scrollTop();
            
            angular.element('.edit-record .popover-tooltip').css({
                top: top - scrollTop - topOffset,
                left: left + leftOffset
            });
            
            angular.element('.edit-record .popover-tooltip .arrow').css({                
                left: arrowOffset
            });            
            
            commonSrvc.shownElement[elem] = true;
       },
       hideTooltip: function(elem){
           commonSrvc.shownElement[elem] = false;
       }
    };
    return commonSrvc;
}]);