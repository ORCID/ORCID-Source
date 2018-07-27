angular.module('orcidApp').factory("peerReviewSrvc", ['$rootScope', '$timeout', function ($rootScope, $timeout) {
    var peerReviewSrvc = {
            blankPeerReview: null,
            constants: { 'access_type': { 'USER': 'user', 'ANONYMOUS': 'anonymous'}},
            details: new Object(),
            groups: new Array(),            
            loading: false,
            loadingDetails: false,
            peerReviewGroupDetailsRequested: new Array(),
            peerReviewsToAddIds: null,
            quickRef: {},            

            createNew: function(peerReview) {
                var cloneF = JSON.parse(JSON.stringify(peerReview));
                cloneF.source = null;
                cloneF.putCode = null;
                for (var idx in cloneF.externalIdentifiers) {
                    cloneF.externalIdentifiers[idx].putCode = null;
                }
                return cloneF;
            },

            deleteGroupPeerReview: function(putCodes) {
                var rmGroups = new Array();
                var rmPeerReview = new Array();
                for (var idj in putCodes) {    
                    for (var idx in peerReviewSrvc.groups) {
                        if (peerReviewSrvc.groups[idx].hasPut(putCodes[idj])) {
                            rmGroups.push(idx);
                            for (var idk in peerReviewSrvc.groups[idx].activities) { //Updated var name, was repeated as idj, also updated the reference in activities to use idk instead, in case of errors.
                                rmPeerReview.push(peerReviewSrvc.groups[idx].activities[idk].putCode.value);
                            }
                        };
                    }
                }
                while (rmGroups.length > 0) {
                    peerReviewSrvc.groups.splice(rmGroups.pop(),1);
                }
                peerReviewSrvc.removePeerReview(rmPeerReview);
            },

            deletePeerReview: function(putCode, sortAsc) {
                peerReviewSrvc.removePeerReview([putCode], function() {peerReviewSrvc.getPeerReviews(sortAsc);});
            },

            
            getGroup: function(id) {
                for (var idx in peerReviewSrvc.groups) {
                    if (peerReviewSrvc.groups[idx].groupId == id) {
                        return peerReviewSrvc.groups[idx];
                    }
                }
                return null;
            },

            getPeerReview: function(putCode) {
                for (var idx in peerReviewSrvc.groups) {
                    for (var x in peerReviewSrvc.groups[idx].peerReviews) {
                        if (peerReviewSrvc.groups[idx].peerReviews[x].putCode == putCode) {
                            return peerReviewSrvc.groups[idx].peerReviews[x];
                        }
                    }
                }
                return null;
            },

            getPeerReviews: function(sortAsc) {
                $.ajax({
                    url: getBaseUri() + '/peer-reviews/peer-reviews.json?sortAsc=' + sortAsc,
                    dataType: 'json',
                    success: function(data) {
                        peerReviewSrvc.groups = data;
                    }
                }).fail(function(e){
                    // something bad is happening!
                    console.log("error fetching Peer Reviews");
                    logAjaxError(e);
                });
            },   
            
            getPublicPeerReviews: function(sortAsc) {
                $.ajax({
                    url: getBaseUri() + '/' + orcidVar.orcidId +'/peer-reviews.json?sortAsc=' + sortAsc,
                    dataType: 'json',
                    success: function(data) {
                        peerReviewSrvc.groups = data;
                    }
                }).fail(function(e){
                    // something bad is happening!
                    console.log("error fetching Peer Reviews");
                    logAjaxError(e);
                });
            },   

            peerReviewCount: function() {
                var count = 0;
                for (var idx in peerReviewSrvc.groups) {
                    count += peerReviewSrvc.groups[idx].peerReviewDuplicateGroups.length;
                }
                return count;
            },

            removePeerReview: function(putCodes,callback) {
                $.ajax({
                    url: getBaseUri() + '/peer-reviews/' + putCodes.splice(0,150).join(),
                    type: 'DELETE',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if (putCodes.length > 0) {
                            peerReviewSrvc.removePeerReview(putCodes,callback);
                        }
                        else if (callback){
                            callback(data);
                        }
                    }
                }).fail(function() {
                    console.log("Error deleting Peer Review.");
                });
            },
            
            makeDefault: function(peerReviewDuplicateGroup, preferredPutCode) {
                $.ajax({
                    url: getBaseUri() + '/peer-reviews/updateToMaxDisplay.json?putCode=' + preferredPutCode,
                    dataType: 'json',
                    success: function(data) {
                        peerReviewDuplicateGroup.activePutCode = preferredPutCode;
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("some bad is hppending");
                });
            },

            setGroupPrivacy: function(id, priv) {
                var group = peerReviewSrvc.getGroup(id);
                var putCodes = new Array();
                for (var idx in group.peerReviews) {
                    putCodes.push(group.peerReviews[idx].putCode.value);
                    group.peerReviews[idx].visibility.visibility = priv;
                }
                peerReviewSrvc.updateVisibility(putCodes, priv);
            },

            setPrivacy: function(putCode, priv) {
                peerReviewSrvc.updateVisibility([putCode], priv);
            },

            updateVisibility: function(putCodes, priv) {
                $.ajax({
                    url: getBaseUri() + '/peer-reviews/' + putCodes.splice(0,150).join() + '/visibility/'+priv.toLowerCase(),
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if (putCodes.length > 0)
                            peerReviewSrvc.updateVisibility(putCodes, priv);
                    }
                }).fail(function() {
                    console.log("Error updating profile Peer Review.");
                });
            },
            
            consistentVis: function(group) {
                var visibility = group.peerReviewDuplicateGroups[0].peerReviews[0].visibility.visibility;
                for(var i = 0; i < group.peerReviewDuplicateGroups.length; i++) {
                    for(var x = 0; x < group.peerReviewDuplicateGroups[i].peerReviews.length; x++) {
                        if (group.peerReviewDuplicateGroups[i].peerReviews[x].visibility.visibility != visibility) {
                            return false;
                        }
                    }
                }
                return true;
            },
            
    };
    return peerReviewSrvc;
}]);