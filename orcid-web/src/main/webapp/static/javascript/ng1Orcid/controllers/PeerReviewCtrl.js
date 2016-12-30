angular.module('orcidApp').controller('PeerReviewCtrl', ['$scope', '$compile', '$filter', 'workspaceSrvc', 'commonSrvc', 'peerReviewSrvc', function ($scope, $compile, $filter, workspaceSrvc, commonSrvc, peerReviewSrvc){
    $scope.workspaceSrvc = workspaceSrvc;
    $scope.peerReviewSrvc = peerReviewSrvc;
    $scope.editPeerReview = null;
    $scope.disambiguatedOrganization = null;
    $scope.addingPeerReview = false;
    $scope.editTranslatedTitle = false;
    $scope.editSources = {};
    $scope.showDetails = {};
    $scope.showPeerReviewDetails = new Array();
    $scope.showElement = {};
    $scope.sortState = new ActSortState(GroupedActivities.PEER_REVIEW);
    $scope.sortHideOption = true;
    $scope.displayURLPopOver = {};
    $scope.peerReviewImportWizard = false;
    $scope.wizardDescExpanded = {};
    $scope.noLinkFlag = true;
    
    $scope.sort = function(key) {
        $scope.sortState.sortBy(key);
    };
    
    $scope.addPeerReviewModal = function(data){
        if (data == undefined) {
            peerReviewSrvc.getBlankPeerReview(function(data) {
                $scope.editPeerReview = data;
                $scope.$apply(function() {                    
                    $scope.showAddPeerReviewModal();
                    $scope.bindTypeaheadForOrgs();
                });
            });
        }else{
            $scope.editPeerReview = data;
            $scope.showAddPeerReviewModal();    
        }       
    };
    
    $scope.showAddPeerReviewModal = function(data){
        $scope.editTranslatedTitle = false;
        $.colorbox({
            scrolling: true,
            html: $compile($('#add-peer-review-modal').html())($scope),
            onLoad: function() {$('#cboxClose').remove();},
            // start the colorbox off with the correct width
            width: formColorBoxResize(),
            onComplete: function() {
                //resize to insure content fits
            },
            onClosed: function() {
                //$scope.closeAllMoreInfo();
                $scope.peerReviewSrvc.loadPeerReviews(peerReviewSrvc.constants.access_type.USER);
            }
        });
    };
    
    $scope.addAPeerReview = function() {
        if ($scope.addingPeerReview) return; 
        $scope.addingPeerReview = true;
        $scope.editPeerReview.errors.length = 0;
        peerReviewSrvc.postPeerReview($scope.editPeerReview,
            function(data){             
                if (data.errors.length == 0) {
                    $scope.addingPeerReview = false;
                    $scope.$apply();
                    $.colorbox.close();
                    $scope.peerReviewSrvc.loadPeerReviews(peerReviewSrvc.constants.access_type.USER);                    
                } else {
                    $scope.editPeerReview = data;
                    commonSrvc.copyErrorsLeft($scope.editPeerReview, data);
                    $scope.addingPeerReview = false;
                    $scope.$apply();
                }
            },
            function() {
                // something bad is happening!
                $scope.addingPeerReview = false;
                console.log("error creating peer review");
            }
        );
    };
    
    $scope.openEditPeerReview = function(putCode){
        peerReviewSrvc.getEditable(putCode, function(data) {$scope.addPeerReviewModal(data);});        
    };
    
    $scope.closeModal = function() {
        $.colorbox.close();
    };
    
    $scope.serverValidate = function (relativePath) {
        $.ajax({
            url: getBaseUri() + '/' + relativePath,
            type: 'POST',
            data:  angular.toJson($scope.editPeerReview),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                commonSrvc.copyErrorsLeft($scope.editPeerReview, data);                
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("PeerReviewCtrl.serverValidate() error");
        });
    };
    
    $scope.removeDisambiguatedOrganization = function() {
        $scope.bindTypeaheadForOrgs();
        if ($scope.disambiguatedOrganization != undefined) delete $scope.disambiguatedOrganization;
        if ($scope.editPeerReview != undefined && $scope.editPeerReview.disambiguatedOrganizationSourceId != undefined) delete $scope.editPeerReview.disambiguatedOrganizationSourceId;
    };
    
    $scope.unbindTypeaheadForOrgs = function () {
        $('#organizationName').typeahead('destroy');
    };
    
    $scope.bindTypeaheadForOrgs = function () {
        var numOfResults = 100;
        $("#organizationName").typeahead({
            name: 'organizationName',
            limit: numOfResults,
            remote: {
                replace: function () {
                    var q = getBaseUri()+'/peer-reviews/disambiguated/name/';
                    if ($('#organizationName').val()) {
                        q += encodeURIComponent($('#organizationName').val());
                    }
                    q += '?limit=' + numOfResults;
                    return q;
                }
            },
            template: function (datum) {
                   var forDisplay =
                       '<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value+ '</span>'
                      +'<span style=\'font-size: 80%;\'>'
                      + ' <br />' + datum.city;
                   if(datum.region){
                       forDisplay += ", " + datum.region;
                   }
                   if (datum.orgType != null && datum.orgType.trim() != '')
                      forDisplay += ", " + datum.orgType;
                   forDisplay += '</span><hr />';

                   return forDisplay;
            }
        });
        $("#organizationName").bind("typeahead:selected", function(obj, datum) {
            $scope.selectOrganization(datum);
            $scope.$apply();
        });
    };
    
    $scope.selectOrganization = function(datum) {
        if (datum != undefined && datum != null) {
            $scope.editPeerReview.orgName.value = datum.value;
            if(datum.value)
                $scope.editPeerReview.orgName.errors = [];
            $scope.editPeerReview.city.value = datum.city;
            if(datum.city)
                $scope.editPeerReview.city.errors = [];
            if(datum.region)
                $scope.editPeerReview.region.value = datum.region;

            if(datum.country != undefined && datum.country != null) {
                $scope.editPeerReview.country.value = datum.country;
                $scope.editPeerReview.country.errors = [];
            }

            if (datum.disambiguatedOrganizationIdentifier != undefined && datum.disambiguatedOrganizationIdentifier != null) {
                $scope.getDisambiguatedOrganization(datum.disambiguatedOrganizationIdentifier);
                $scope.unbindTypeaheadForOrgs();
            }
        }
    };
    
    $scope.getDisambiguatedOrganization = function(id) {
        $.ajax({
            url: getBaseUri() + '/peer-reviews/disambiguated/id/' + id,
            dataType: 'json',
            type: 'GET',
            success: function(data) {
                if (data != null) {
                    $scope.disambiguatedOrganization = data;
                    $scope.editPeerReview.disambiguatedOrganizationSourceId = data.sourceId;
                    $scope.editPeerReview.disambiguationSource = data.sourceType;
                    $scope.$apply();
                }
            }
        }).fail(function(){
            console.log("error getDisambiguatedOrganization(id)");
        });
    };
    
    $scope.toggleTranslatedTitleModal = function(){
        $scope.editTranslatedTitle = !$scope.editTranslatedTitle;
        $('#translatedTitle').toggle();
        $.colorbox.resize();
    };

    $scope.addExternalIdentifier = function () {
        $scope.editPeerReview.externalIdentifiers.push({workExternalIdentifierId: {value: ""}, workExternalIdentifierType: {value: ""}, relationship: {value: "self"}, url: {value: ""}});
    };
    
    $scope.addSubjectExternalIdentifier = function () {
        $scope.editPeerReview.subjectForm.workExternalIdentifiers.push({workExternalIdentifierId: {value: ""}, workExternalIdentifierType: {value: ""}, relationship: {value: "self"}, url: {value: ""}});
    };
    
    $scope.deleteExternalIdentifier = function(obj) {
        var index = $scope.editPeerReview.externalIdentifiers.indexOf(obj);
        $scope.editPeerReview.externalIdentifiers.splice(index,1);
    };
    
    $scope.deleteSubjectExternalIdentifier = function(obj) {
        var index = $scope.editPeerReview.subjectForm.workExternalIdentifiers.indexOf(obj);
        $scope.editPeerReview.subjectForm.workExternalIdentifiers.splice(index,1);        
    };
   
    $scope.showDetailsMouseClick = function(groupId, $event){
        $event.stopPropagation();
        $scope.showDetails[groupId] = !$scope.showDetails[groupId];
    };
    
    $scope.showMoreDetails = function(putCode){
        $scope.showPeerReviewDetails.length = 0;
        $scope.showPeerReviewDetails[putCode] = true;   
    };
    
    $scope.hideMoreDetails = function(putCode){
        $scope.showPeerReviewDetails.length = 0;
        $scope.showPeerReviewDetails[putCode] = false;
    };
    
    $scope.deletePeerReviewConfirm = function(putCode, deleteGroup) {
        var peerReview = peerReviewSrvc.getPeerReview(putCode);
        var maxSize = 100;
        
        $scope.deletePutCode = putCode;
        $scope.deleteGroup = deleteGroup;
        
        if (peerReview.subjectName)
            $scope.fixedTitle = peerReview.subjectName.value;
        else {
            $scope.fixedTitle = '';
        }
        
        if($scope.fixedTitle.length > maxSize)
            $scope.fixedTitle = $scope.fixedTitle.substring(0, maxSize) + '...';
        $.colorbox({
            html : $compile($('#delete-peer-review-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
    };
    
    $scope.deleteByPutCode = function(putCode, deleteGroup) {
        if (deleteGroup)
           peerReviewSrvc.deleteGroupPeerReview(putCode);
        else
            peerReviewSrvc.deletePeerReview(putCode);
        $.colorbox.close();
    };
    
    $scope.userIsSource = function(peerReview) {
        if (peerReview.source == orcidVar.orcidId)
            return true;
        return false;
    };
    
    $scope.showTooltip = function (element){        
        $scope.showElement[element] = true;
    };

    $scope.hideTooltip = function (element){        
        $scope.showElement[element] = false;
    };
    
    $scope.fillUrl = function(extId) {
        if(extId != null) {
            var url = workIdLinkJs.getLink(extId.workExternalIdentifierId.value, extId.workExternalIdentifierType.value);           
            if(extId.url == null) {
                extId.url = {value:""};
            }
            extId.url.value=url;
        }
    };
    
    $scope.hideURLPopOver = function(id){
        $scope.displayURLPopOver[id] = false;
    };
    
    $scope.showURLPopOver = function(id){
        $scope.displayURLPopOver[id] = true;
    };
    
    $scope.moreInfoActive = function(groupID){
        if ($scope.moreInfo[groupID] == true || $scope.moreInfo[groupID] != null) return 'truncate-anchor';
    };
    
    $scope.showPeerReviewImportWizard = function(){
        if(!$scope.peerReviewImportWizard) {
            loadPeerReviewLinks();
        }
        $scope.peerReviewImportWizard = !$scope.peerReviewImportWizard;
    };
    
    $scope.toggleWizardDesc = function(id){
        $scope.wizardDescExpanded[id] = !$scope.wizardDescExpanded[id];
    };
    
    $scope.openImportWizardUrlFilter = function(url, param) {
        url = url + '?client_id='+param.clientId+'&response_type=code&scope='+param.redirectUris.redirectUri[0].scopeAsSingleString+'&redirect_uri='+param.redirectUris.redirectUri[0].value;
        openImportWizardUrl(url);
    };
        
    //Init
    $scope.peerReviewSrvc.loadPeerReviews(peerReviewSrvc.constants.access_type.USER);
    loadPeerReviewLinks();
    
    function loadPeerReviewLinks() {
        $.ajax({
            url: getBaseUri() + '/workspace/retrieve-peer-review-import-wizards.json',
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.peerReviewImportWizardList = data;
                if(data == null || data.length == 0) {
                    $scope.noLinkFlag = false;
                }
                $scope.peerReviewImportWizardList.sort(function(obj1, obj2){
                    if(obj1.displayName < obj2.displayName) {
                        return -1;
                    }
                    if(obj1.displayName > obj2.displayName) {
                        return 1;
                    }
                    return 0;
                });
                $scope.$apply();
            }
        }).fail(function(e) {
            // something bad is happening!
            console.log("PeerReviewImportWizardError");
            logAjaxError(e);
        });
    }
}]);