import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';





import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class FundingService {
    private fundingToAddIds: any;
    private groups: any;
    private headers: HttpHeaders;
    private loading: any;
    private urlFundingsById: string;
    private urlFundingsId: string;

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
        this.fundingToAddIds = null;
        this.groups = null;
        this.urlFundingsById = getBaseUri() + '/fundings/fundings.json?fundingIds=';
        this.urlFundingsId = getBaseUri() + '/fundings/fundingIds.json';
    }

    getFundingsById( idList ) {
        this.loading = true;
        this.fundingToAddIds = null;
        console.log('getFundingsById', this.urlFundingsById + idList);
        return this.http.get(
            this.urlFundingsById + idList
        )
        
        /*
        if( fundingSrvc.fundingToAddIds.length != 0 ) {
                var fundingIds = fundingSrvc.fundingToAddIds.splice(0,20).join();
                $.ajax({
                    url: getBaseUri() + '/' + path + '?fundingIds=' + fundingIds,
                    dataType: 'json',
                    success: function(data) {
                        for (var i in data) {
                            var funding = data[i];
                            groupedActivitiesUtil.group(funding,GroupedActivities.FUNDING,fundingSrvc.groups);
                        };
                        if (fundingSrvc.fundingToAddIds.length == 0) {
                            $timeout(function() {
                              fundingSrvc.loading = false;
                            });
                        } else {
                            $timeout(function () {
                                fundingSrvc.addFundingToScope(path);
                            },50);
                        }
                    }
                }).fail(function(e) {
                    console.log("Error fetching fundings");
                    logAjaxError(e);
                });
            } else {
                fundingSrvc.loading = false;
            };
            */
    }

    getFundingsId() {
        this.loading = true;
        this.fundingToAddIds = null;
        //this.groups.length = 0;
        return this.http.get(
            this.urlFundingsId
        )
        
        /*
        getFundings: function(path) {
            //clear out current fundings
            
            //new way
            
            //get funding ids
            $.ajax({
                url: getBaseUri() + '/'  + path,
                dataType: 'json',
                success: function(data) {
                    $timeout(function(){
                        fundingSrvc.fundingToAddIds = data;
                        fundingSrvc.addFundingToScope('fundings/fundings.json');
                    });
                }
            }).fail(function(e){
                // something bad is happening!
                console.log("error fetching fundings");
                logAjaxError(e);
            });
        },
        */
    }

}

/*

angular.module('orcidApp').factory("fundingSrvc", ['$rootScope', '$timeout', function ($rootScope, $timeout) {
    var fundingSrvc = {
        constants: { 'access_type': { 'USER': 'user', 'ANONYMOUS': 'anonymous'}},
        fundings: new Array(),
        fundingToAddIds: null,
        groups: new Array(),
        loading: false,
        moreDetailsActive: false,
        
        addFundingToScope: function(path) {
            if( fundingSrvc.fundingToAddIds.length != 0 ) {
                var fundingIds = fundingSrvc.fundingToAddIds.splice(0,20).join();
                $.ajax({
                    url: getBaseUri() + '/' + path + '?fundingIds=' + fundingIds,
                    dataType: 'json',
                    success: function(data) {
                        for (var i in data) {
                            var funding = data[i];
                            groupedActivitiesUtil.group(funding,GroupedActivities.FUNDING,fundingSrvc.groups);
                        };
                        if (fundingSrvc.fundingToAddIds.length == 0) {
                            $timeout(function() {
                              fundingSrvc.loading = false;
                            });
                        } else {
                            $timeout(function () {
                                fundingSrvc.addFundingToScope(path);
                            },50);
                        }
                    }
                }).fail(function(e) {
                    console.log("Error fetching fundings");
                    logAjaxError(e);
                });
            } else {
                fundingSrvc.loading = false;
            };
        },
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
*/