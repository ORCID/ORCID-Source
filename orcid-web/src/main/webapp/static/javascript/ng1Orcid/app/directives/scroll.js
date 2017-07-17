angular.module('orcidApp').directive('scroll', function () {
    return {
        restrict: 'A',
        link: function ($scope, element, attrs) {
            $scope.scrollTop = 0;
            var raw = element[0];
            element.bind('scroll', function () {
                $scope.scrollTop = raw.scrollTop;
                // $scope.$apply(attrs.scroll);
            });
        }
    }
});