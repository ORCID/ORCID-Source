orcidNgModule.controller('PublicFundingCtrl',['$scope', '$compile', '$filter', 'workspaceSrvc', 'fundingSrvc', function ($scope, $compile, $filter, workspaceSrvc, fundingSrvc){
    $scope.fundingSrvc = fundingSrvc;
    $scope.workspaceSrvc = workspaceSrvc;
    $scope.moreInfo = {};
    $scope.editSources = {};
    $scope.showElement = {};
    $scope.displayURLPopOver = {};

    $scope.sortState = new ActSortState(GroupedActivities.FUNDING);
    $scope.sort = function(key) {
        $scope.sortState.sortBy(key);
    };
    
    // remove once grouping is live
    $scope.toggleClickMoreInfo = function(key) {
        if (!document.documentElement.className.contains('no-touch'))
            $scope.moreInfo[key]=!$scope.moreInfo[key];
    };

    // remove once grouping is live
    $scope.moreInfoMouseEnter = function(key, $event) {
        $event.stopPropagation();
        if (document.documentElement.className.contains('no-touch'))
            $scope.moreInfo[key]=true;
    };

    $scope.showDetailsMouseClick = function(key, $event) {              
        $event.stopPropagation();
        $scope.moreInfo[key] = !$scope.moreInfo[key];
    };

    $scope.closeMoreInfo = function(key) {
        $scope.moreInfo[key]=false;
    };

    fundingSrvc.setIdsToAdd(orcidVar.fundingIdsJson);
    fundingSrvc.addFundingToScope(orcidVar.orcidId +'/fundings.json');

    $scope.renderTranslatedTitleInfo = function(funding) {
        var info = null;
        if(funding != null && funding.fundingTitle != null && funding.fundingTitle.translatedTitle != null) {
            info = funding.fundingTitle.translatedTitle.content + ' - ' + funding.fundingTitle.translatedTitle.languageName;
        }
        return info;
    };
    
    $scope.showTooltip = function (key){
        $scope.showElement[key] = true;
    };

    $scope.hideTooltip = function (key){        
        $scope.showElement[key] = false;
    };
    
    $scope.showSources = function(group) {
        $scope.editSources[group.groupId] = true;
    };
    
    $scope.hideSources = function(group) {
        $scope.editSources[group.groupId] = false;
        group.activePutCode = group.defaultPutCode;
    };
    
    $scope.hideURLPopOver = function(id){
        $scope.displayURLPopOver[id] = false;
    };
    
    $scope.showURLPopOver = function(id){
        $scope.displayURLPopOver[id] = true;
    };

}]);