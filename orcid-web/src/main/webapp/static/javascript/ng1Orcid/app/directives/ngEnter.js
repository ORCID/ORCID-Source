/*
 * Scope: element
 */
angular.module('orcidApp').directive('ngEnter', function() {
    return function(scope, element, attrs) {
        $(document).unbind("keydown.ngEnter keypress.ngEnter");
        element.bind("keydown.ngEnter keypress.ngEnter", function(event) {
            if(event.which === 13) {                
                scope.$apply(function(){
                    scope.$eval(attrs.ngEnter, {'event': event});
                });
                event.preventDefault();
                event.stopPropagation();
            }
        });
    };
});