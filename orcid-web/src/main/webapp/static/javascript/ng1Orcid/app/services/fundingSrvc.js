/**
 * Fundings Service
 * */
angular.module('orcidApp').factory("fundingSrvc", ['$rootScope', '$timeout', function ($rootScope, $timeout) {
    var fundingSrvc = {
        constants: { 'access_type': { 'USER': 'user', 'ANONYMOUS': 'anonymous'}},
        fundings: new Array(),
        fundingToAddIds: null,
        groups: new Array(),
        loading: false,
        moreDetailsActive: false,
        

        createNew: function(work) {
            var cloneF = JSON.parse(JSON.stringify(work));
            cloneF.source = null;
            cloneF.putCode = null;
            for (var idx in cloneF.externalIdentifiers){
                cloneF.externalIdentifiers[idx].putCode = null;
            }
            return cloneF;
        },
        getEditable: function(putCode, callback) {
            // first check if they are the current source
            var funding = fundingSrvc.getFunding(putCode);
            if (funding.source == orcidVar.orcidId){
                callback(funding);
            }
            else {
                var bestMatch = null;
                var group = fundingSrvc.getGroup(putCode);
                for (var idx in group.activitiess) {
                    if (group[idx].source == orcidVar.orcidId) {
                        bestMatch = callback(group[idx]);
                        break;
                    }
                }
                if (bestMatch == null) {
                    bestMatch = fundingSrvc.createNew(funding);
                }
                callback(bestMatch);
            };
        },
        deleteFunding: function(putCode) {
            var rmFunding;
            for (var idx in fundingSrvc.groups) {
                if (fundingSrvc.groups[idx].hasPut(putCode)) {
                    rmFunding = fundingSrvc.groups[idx].getByPut(putCode);
                    break;
                };
            };
            // remove work on server
            fundingSrvc.removeFunding(rmFunding);
        },
        deleteGroupFunding: function(putCode) {
            var idx;
            var rmWorks;
            for (var idx in fundingSrvc.groups) {
                if (fundingSrvc.groups[idx].hasPut(putCode)) {
                   for (var idj in fundingSrvc.groups[idx].activities) {
                       fundingSrvc.removeFunding(fundingSrvc.groups[idx].activities[idj]);
                    }
                    fundingSrvc.groups.splice(idx,1);
                    break;
                }
            }
        },
        fundingCount: function() {
            var count = 0;
            for (var idx in fundingSrvc.groups) {
                count += fundingSrvc.groups[idx].activitiesCount;
            }
            return count;
        },
        getFunding: function(putCode) {
            for (var idx in fundingSrvc.groups) {
                if (fundingSrvc.groups[idx].hasPut(putCode)){
                    return fundingSrvc.groups[idx].getByPut(putCode);
                }
            }
            return null;
        },
        getFundings: function(sort, sortAsc) {
            fundingSrvc.loading = true;
            fundingSrvc.fundingToAddIds = null;
            fundingSrvc.groups.length = 0;
            $.ajax({
                url: getBaseUri() + '/fundings.json?sort=' + sort + '&sortAsc=' + sortAsc,
                dataType: 'json',
                success: function(data) {
                    fundingSrvc.groups = data;
                    fundingSrvc.loading = false;
                }
            }).fail(function(e){
                console.log("error fetching fundings");
                logAjaxError(e);
            });
        },
        
        getGroup: function(putCode) {
            for (var idx in fundingSrvc.groups) {
                if (fundingSrvc.groups[idx].hasPut(putCode)){
                    return fundingSrvc.groups[idx];
                }
            }
            return null;
        },
        makeDefault: function(group, putCode) {
            group.makeDefault(putCode);
            $.ajax({
                url: getBaseUri() + '/fundings/updateToMaxDisplay.json?putCode=' + putCode,
                dataType: 'json',
                success: function(data) {
                }
            }).fail(function(){
                // something bad is happening!
                console.log("some bad is hppending");
            });
        },
        removeFunding: function(funding) {
            $.ajax({
                url: getBaseUri() + '/fundings/funding.json',
                type: 'DELETE',
                data: angular.toJson(funding),
                contentType: 'application/json;charset=UTF-8',
                dataType: 'json',
                success: function(data) {
                    $timeout(function(){
                        if (data.errors.length != 0){
                           console.log("Unable to delete funding.");
                        }
                        else{
                           groupedActivitiesUtil.rmByPut(funding.putCode.value, GroupedActivities.FUNDING,fundingSrvc.groups);
                        }
                    });  
                }
            }).fail(function() {
                console.log("Error deleting funding.");
            });
        },
        setIdsToAdd: function(ids) {
            fundingSrvc.fundingToAddIds = ids;
        },
        setGroupPrivacy: function(putCode, priv) {
            var group = fundingSrvc.getGroup(putCode);
            for (var idx in group.activities) {
                var curPutCode = group.activities[idx].putCode.value;
                fundingSrvc.setPrivacy(curPutCode, priv);
            }
        },
        setPrivacy: function(putCode, priv) {
            var funding = fundingSrvc.getFunding(putCode);
            funding.visibility.visibility = priv;
            fundingSrvc.updateProfileFunding(funding);
        },
        updateProfileFunding: function(funding) {
            $.ajax({
                url: getBaseUri() + '/fundings/funding.json',
                type: 'PUT',
                data: angular.toJson(funding),
                contentType: 'application/json;charset=UTF-8',
                dataType: 'json',
                success: function(data) {
                    $timeout(function(){
                        if(data.errors.length != 0){
                            console.log("Unable to update profile funding.");
                        }
                    }); 
                }
            }).fail(function() {
                console.log("Error updating profile funding.");
            });
        }
    };
    return fundingSrvc;
}]);