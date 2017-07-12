/*
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */

/*
 * Structure of this file:
 * 
 *  - 4 - Angular Controllers
 *  - 6 - Angular Directives
 *  
 */

/*******************************************************************************
 * 4 - Angular Controllers
*******************************************************************************/

angular.module('orcidApp').controller('SwitchUserCtrl',['$scope', '$compile', '$document', function ($scope, $compile, $document){
    $scope.isDroppedDown = false;
    $scope.searchResultsCache = new Object();

    $scope.openMenu = function(event){
        $scope.isDroppedDown = true;
        event.stopPropagation();
    };

    $scope.getDelegates = function() {
        $.ajax({
            url: getBaseUri() + '/delegators/delegators-and-me.json',
            dataType: 'json',
            success: function(data) {
                $scope.delegators = data.delegators;
                $scope.searchResultsCache[''] = $scope.delegators;
                $scope.me = data.me;
                $scope.unfilteredLength = $scope.delegators != null ? $scope.delegators.delegationDetails.length : 0;
                $scope.$apply();
            }
        }).fail(function(e) {
            // something bad is happening!
            console.log("error with delegates");
            logAjaxError(e);
        });
    };

    $scope.search = function() {
        if($scope.searchResultsCache[$scope.searchTerm] === undefined) {
            if($scope.searchTerm === ''){
                $scope.getDelegates();
                $scope.searchResultsCache[$scope.searchTerm] = $scope.delegators;
            }
            else {
                $.ajax({
                    url: getBaseUri() + '/delegators/search/' + encodeURIComponent($scope.searchTerm) + '?limit=10',
                    dataType: 'json',
                    success: function(data) {
                        $scope.delegators = data;
                        $scope.searchResultsCache[$scope.searchTerm] = $scope.delegators;
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error searching for delegates");
                });
            }
        } else {
            $scope.delegators = $scope.searchResultsCache[$scope.searchTerm];
        }
    };

    $scope.switchUser = function(targetOrcid){
        $.ajax({
            url: getBaseUri() + '/switch-user?j_username=' + targetOrcid,
            dataType: 'json',
            complete: function(data) {
                window.location.reload();
            }
        });
    };

    $document.bind('click',
        function(event){
            if(event.target.id !== "delegators-search"){
                $scope.isDroppedDown = false;
                $scope.searchTerm = '';
                $scope.$apply();
            }
        });

    // init
    $scope.getDelegates();
}]);

angular.module('orcidApp').controller('statisticCtrl',['$scope', function ($scope){
    $scope.liveIds = 0;
    $scope.getLiveIds = function(){
        $.ajax({
            url: getBaseUri()+'/statistics/liveids.json',
            type: 'GET',
            dataType: 'html',
            success: function(data){
                $scope.liveIds = data;
                $scope.$apply($scope.liveIds);
            }
        }).fail(function(e) {
            // something bad is happening!
            console.log("Error getting statistics Live iDs total amount");
            logAjaxError(e);
        });
    };

    $scope.getLiveIds();
}]);





/**
 * Internal consortium controller
 */
angular.module('orcidApp').controller('internalConsortiumCtrl',['$scope', '$compile', function manageConsortiumCtrl($scope, $compile) {    
    $scope.showFindModal = false;
    $scope.consortium = null;

    $scope.toggleFindConsortiumModal = function() {
        $scope.showFindModal = !$scope.showFindModal;
    };
    
    /**
     * FIND
     */
    $scope.findConsortium = function() {
        $.ajax({
            url: getBaseUri()+'/manage-members/find-consortium.json?id=' + encodeURIComponent($scope.salesForceId),
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.consortium = data;
                $scope.$apply();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error finding the consortium");
        });
    };
    
    $scope.confirmUpdateConsortium = function() {
        $.colorbox({
            html : $compile($('#confirm-modal-consortium').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"450px" , height:"175px"});
    };
    
    $scope.updateConsortium = function() {
        $.ajax({
            url: getBaseUri()+'/manage-members/update-consortium.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'POST',
            dataType: 'json',
            data: angular.toJson($scope.consortium),
            success: function(data){
                $scope.$apply(function(){
                    if(data.errors.length == 0){
                        $scope.consortium = null;
                        $scope.success_edit_member_message = om.get('manage_member.edit_member.success');
                    } else {
                        $scope.consortium = data;
                    }
                });
                $scope.closeModal();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error updating the consortium");
        });
    };
    
    $scope.closeModal = function() {
        $.colorbox.close();
    };
    
}]);





/* Do not add anything below, see file structure at the top of this file */