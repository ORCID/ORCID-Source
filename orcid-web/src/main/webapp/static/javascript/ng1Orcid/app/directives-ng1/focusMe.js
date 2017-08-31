angular.module('orcidApp').directive(
    'focusMe', 
    function($timeout) {
        return {
            scope: { trigger: '=focusMe' },
            link: function(scope, element) {
                $timeout( //[fn], [delay], [invokeApply], [Pass]
                    function(){
                        if (scope.trigger) {
                            element[0].focus();
                            scope.trigger = false;
                        }
                    },
                    1000
                );
            }
        };
    }
);