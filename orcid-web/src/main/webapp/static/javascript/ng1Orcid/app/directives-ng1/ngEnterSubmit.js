/*
 * For forms submitted using the default submit function (Scope: document)
 * Not necessary to be inside an element, for inputs use ngEnter
 */
angular.module('orcidApp').directive('ngEnterSubmit', function($document) {
    return {
        restrict: 'A',
        link: function(scope, element, attr) {
            $(document).unbind("keydown.ngEnterSubmit keypress.ngEnterSubmit");
            $document.bind("keydown.ngEnterSubmit keypress.ngEnterSubmit", function(event) {
                if (event.which === 13) {
                   element.submit();
                }
            });
        }
    };
});