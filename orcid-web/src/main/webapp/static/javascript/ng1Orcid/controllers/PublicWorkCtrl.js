orcidNgModule.controller('PublicWorkCtrl',['$scope', '$compile', '$filter', 'workspaceSrvc', 'worksSrvc',function ($scope, $compile, $filter, workspaceSrvc, worksSrvc) {
    $scope.worksSrvc = worksSrvc;
    $scope.workspaceSrvc = workspaceSrvc;
    $scope.showBibtex = {};
    $scope.moreInfoOpen = false;
    $scope.moreInfo = {};
    $scope.editSources = {};
    $scope.showElement = {};
    $scope.displayURLPopOver = {};
    $scope.badgesRequested = {};

    $scope.sortState = new ActSortState(GroupedActivities.ABBR_WORK);
    $scope.sort = function(key) {
        $scope.sortState.sortBy(key);
    };
    
    $scope.bibtexShowToggle = function (putCode) {
        $scope.showBibtex[putCode] = !($scope.showBibtex[putCode]);
    };

    $scope.renderTranslatedTitleInfo = function(putCode) {
        var info = null;

        if(putCode != null && $scope.worksSrvc.details[putCode] != null && $scope.worksSrvc.details[putCode].translatedTitle != null) {
            info = $scope.worksSrvc.details[putCode].translatedTitle.content + ' - ' + $scope.worksSrvc.details[putCode].translatedTitle.languageName;
        }

        return info;
    };

    $scope.worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.ANONYMOUS);

    // remove once grouping is live
    $scope.moreInfoClick = function(work, $event) {
        if (!document.documentElement.className.contains('no-touch'))
            $scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
    };

    // remove once grouping is live
    $scope.moreInfoMouseEnter = function(work, $event) {
        $event.stopPropagation();
        if (document.documentElement.className.contains('no-touch'))
            $scope.loadWorkInfo(work.putCode.value, $event);
        else
            $scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
    };

    $scope.showDetailsMouseClick = function(group, $event) {
            $event.stopPropagation();
        //if (document.documentElement.className.contains('no-touch'))
            $scope.moreInfo[group.groupId] = !$scope.moreInfo[group.groupId];
            //$scope.loadWorkInfo(work, $event);
            for (var idx in group.activities)
                $scope.loadDetails(group.activities[idx].putCode.value, $event);
        //else
            //$scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
    };

    $scope.loadDetails = function(putCode, event) {
        //Close any open popover
        $scope.closePopover(event);
        $scope.moreInfoOpen = true;
        //Display the popover
        $(event.target).next().css('display','inline');
        $scope.worksSrvc.getGroupDetails(putCode, worksSrvc.constants.access_type.ANONYMOUS);
    };

    $scope.hideSources = function(group) {
        $scope.editSources[group.groupId] = false;
        group.activePutCode = group.defaultPutCode;
    };

    $scope.showSources = function(group) {
        $scope.editSources[group.groupId] = true;
    };

    $scope.loadWorkInfo = function(putCode, event) {
        //Close any open popover
        $scope.closePopover(event);
        $scope.moreInfoOpen = true;
        //Display the popover
        $(event.target).next().css('display','inline');
        if($scope.worksSrvc.details[putCode] == null) {
            $scope.worksSrvc.getGroupDetails(putCode, worksSrvc.constants.access_type.ANONYMOUS);
        } else {
            $(event.target).next().css('display','inline');
        }
    };

    $scope.closePopover = function(event) {
        $scope.moreInfoOpen = false;
        $('.work-more-info-container').css('display', 'none');
    };

    $scope.showTooltip = function (element){        
        $scope.showElement[element] = true;
    };

    $scope.hideTooltip = function (element){        
        $scope.showElement[element] = false;
    };
    
    $scope.hideURLPopOver = function(id){
        $scope.displayURLPopOver[id] = false;
    };
    
    $scope.showURLPopOver = function(id){
        $scope.displayURLPopOver[id] = true;
    };
    
    $scope.showMozillaBadges = function(putCode){
        $scope.$watch(
            function () { 
                return document.getElementsByClassName('badge-container-' + putCode).length; 
            },
            function (newValue, oldValue) {
                if (newValue !== oldValue) {
                    if ($scope.badgesRequested[putCode] == null){
                        var dois = worksSrvc.getUniqueDois(putCode);
                        var c = document.getElementsByClassName('badge-container-' + putCode);
                        for (i = 0; i <= dois.length - 1; i++){
                            var code = 'var conf={"article-doi": "' + dois[i] + '", "container-class": "badge-container-' + putCode + '"};showBadges(conf);';
                            var s = document.createElement('script');
                            s.type = 'text/javascript';
                            try {
                              s.appendChild(document.createTextNode(code));
                              c[0].appendChild(s);
                            } catch (e) {
                              s.text = code;
                              c[0].appendChild(s);
                            }
                        }
                        $scope.badgesRequested[putCode] = true;
                    }
                }
            }
        );  
    };
    
}]);