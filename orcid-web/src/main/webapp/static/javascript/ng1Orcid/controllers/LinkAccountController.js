angular.module('orcidApp').controller('LinkAccountController',['$scope', 'discoSrvc', function ($scope, discoSrvc){
    
    $scope.loadedFeed = false;
    
    $scope.linkAccount = function(idp, linkType) {
        var eventAction = linkType === 'shibboleth' ? 'Sign-In-Link-Federated' : 'Sign-In-Link-Social';
        orcidGA.gaPush(['send', 'event', 'Sign-In-Link', eventAction, idp]);
        return false;
    };
    
    $scope.setEntityId = function(entityId) {
        $scope.entityId = entityId;
    }
    
    $scope.$watch(function() { return discoSrvc.feed; }, function(){
        $scope.idpName = discoSrvc.getIdPName($scope.entityId);
        if(discoSrvc.feed != null) {
            $scope.loadedFeed = true;
        }
    });
    
}]);