angular.module('orcidApp').factory("clearMemberListFilterSrvc", ['$rootScope', function ($rootScope) {
    return {
          clearFilters : function ($scope){
              $scope.by_country = undefined;
              $scope.by_researchCommunity = undefined;
              $scope.activeLetter = '';
         }
     };
 }]);