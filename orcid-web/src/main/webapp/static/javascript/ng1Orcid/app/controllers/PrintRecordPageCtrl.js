angular.module('orcidApp').controller('PrintRecordPageCtrl',['$scope', '$compile', '$window', function ($scope, $compile, $window) {

    $(document).ready(
        function() {
            printFrameReadyToPrint(printFunc); 
        }
    );

    var printFunc = function() {
        window.print();
        setTimeout(window.close, 0);
    }

    var printFrameReadyToPrint = function (func) {
        console.log("print frame ready to print function");
        // Step 1: make sure angular 1 is ready by putting a function on the angular apply queue
        angular.element(document.documentElement).scope().$root.$apply(
            function() {
                // Step 2: if JQuery has any outstanding request repeat otherwise call otherwise print
                $.active>0?setTimeout(printFrameReadyToPrint):printFunc();
            }
        );
    }

}]);