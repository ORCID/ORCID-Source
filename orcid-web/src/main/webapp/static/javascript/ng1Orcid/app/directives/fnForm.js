/*
 * For forms submitted using a custom function, Scope: Document
 * 
 * Example:
 * <fn-form update-fn="theCustomFunction()">
 * 
 * </fn-form>
 * 
 */
angular.module('orcidApp').directive('fnForm', function($document) {
    return {
        restrict: 'E',
        scope: {
            updateFn: '&'
        },
        link: function(scope, elm, attrs) {

            $(document).unbind("keydown.keydownUpfateFn");

            $document.bind(
                "keydown.keydownUpfateFn",
                function(event) {
                    if (event.which === 13) {
                        scope.updateFn();                 
                        event.stopPropagation();
                    }
                }
            );                   
        }
    }
});