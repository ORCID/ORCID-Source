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
function openImportWizardUrl(url) {
    var win = window.open(url, "_target");
    setTimeout( function() {
        if(!win || win.outerHeight === 0) {
            //First Checking Condition Works For IE & Firefox
            //Second Checking Condition Works For Chrome
            window.location.href = url;
        }
    }, 250);
    $.colorbox.close();
};

function contains(arr, obj) {
    var index = arr.length;
    while (index--) {
       if (arr[index] === obj) {
           return true;
       }
    }
    return false;
}

function formatDate(oldDate) {
	var date = new Date(oldDate);
	var day = date.getDate();
	var month = date.getMonth() + 1;
	var year = date.getFullYear();
	if(month < 10) {
		month = '0' + month;
	}
	if(day < 10) {
		day = '0' + day;
	}
	return (year + '-' + month + '-' + day);
}


function getScripts(scripts, callback) {
    var progress = 0;
    var internalCallback = function () {        
        if (++progress == scripts.length - 1) {
            callback();
        }
    };    
    scripts.forEach(function(script) {        
        $.getScript(script, internalCallback);        
    });
};


var PRIVACY = {};
PRIVACY.PUBLIC = 'PUBLIC';
PRIVACY.LIMITED = 'LIMITED';
PRIVACY.PRIVATE = 'PRIVATE';

var GroupedActivities = function(type) {

    if (GroupedActivities.count == undefined)
        GroupedActivities.count = 1;
    else
        GroupedActivities.count ++;

    function getInstantiateCount() {
           var id = 0; // This is the private persistent value
           // The outer function returns a nested function that has access
           // to the persistent value.  It is this nested function we're storing
           // in the variable uniqueID above.
           return function() { return id++; };  // Return and increment
    }

    this.type = type;
    this._keySet = {};
    this.activities = {};
    this.activitiesCount = 0;
    this.activePutCode = null;
    this.defaultPutCode = null;
    this.dateSortString;
    this.groupId = GroupedActivities.count;
    this.groupDescription = null;
    this.groupType = null;
    this.groupRealId = null;
    this.title;
};

GroupedActivities.count = 0;
GroupedActivities.prototype.FUNDING = 'funding';
GroupedActivities.ABBR_WORK = 'abbrWork';
GroupedActivities.PEER_REVIEW = 'peerReview';
GroupedActivities.AFFILIATION = 'affiliation';

GroupedActivities.prototype.add = function(activity) {		
    // assumes works are added in the order of the display index desc
    // subsorted by the created date asc
    var identifiersPath = null;
    identifiersPath = this.getIdentifiersPath();        
    
    if(this.type == GroupedActivities.PEER_REVIEW) {    
    	var key = this.key(activity[identifiersPath]);
    	this.addKey(key);
    } else {
    	for (var idx in activity[identifiersPath]) {
    		this.addKey(this.key(activity[identifiersPath][idx]));
    	}
    }    
    
    this.activities[activity.putCode.value] = activity;
    if (this.defaultPutCode == null) {
        this.activePutCode = activity.putCode.value;
        this.makeDefault(activity.putCode.value);
    }
    this.activitiesCount++;
};

GroupedActivities.prototype.addKey = function(key) {
    if (this.hasKey(key)) return;
    this._keySet[key] = true;
    if (this.type == GroupedActivities.PEER_REVIEW)
    	this.groupRealId = key;
    return;
};

GroupedActivities.prototype.getActive = function() {
    return this.activities[this.activePutCode];
};

GroupedActivities.prototype.getDefault = function() {
    return this.activities[this.defaultPutCode];
};

GroupedActivities.prototype.getByPut = function(putCode) {
    return this.activities[putCode];
};

GroupedActivities.prototype.consistentVis = function() {
	
	if (this.type == GroupedActivities.FUNDING)
        var vis = this.getDefault().visibility.visibility;
    else
        var vis = this.getDefault().visibility;

    for (var idx in this.activities)
    	
        if (this.type == GroupedActivities.FUNDING) {
            if (this.activities[idx].visibility.visibility != vis)
                return false;
        } else {
            if (this.activities[idx].visibility != vis)
                return false;
        }
    return true;
};

GroupedActivities.prototype.getIdentifiersPath = function() {
    if (this.type == GroupedActivities.ABBR_WORK) return 'workExternalIdentifiers';
    if (this.type == GroupedActivities.PEER_REVIEW) return 'groupId';
    return 'externalIdentifiers';
};

/*
 * takes a activity and adds it to an existing group or creates
 * a new group
 */
GroupedActivities.group = function(activity, type, groupsArray) {
	var matches = new Array();
    // there are no possible keys for affiliations    
    if (type != GroupedActivities.AFFILIATION);
       for (var idx in groupsArray) {     	   
    	   if (groupsArray[idx].keyMatch(activity))
               matches.push(groupsArray[idx]);
       }           
    if (matches.length == 0) {
        var newGroup = new GroupedActivities(type);
        newGroup.add(activity);
        groupsArray.push(newGroup);
    }  else {
        var firstMatch = matches.shift();
        firstMatch.add(activity);
        // combine any remaining groups into the first group we found.
        for (var idx in matches) {
            var matchIndex = groupsArray.indexOf(matches[idx]);
            var curMatch = groupsArray[matchIndex];
            for (var idj in curMatch.activities)
                firstMatch.add(curMatch.activities[idj]);
            groupsArray.splice(matchIndex, 1);
        }
    }
};

GroupedActivities.prototype.hasKey = function(key) {
    if (key in this._keySet)
        return true;
    return false;
};

GroupedActivities.prototype.hasKeys = function(key) {
    if (Object.keys(this._keySet).length > 0)
        return true;
    return false;
};

GroupedActivities.prototype.hasUserVersion = function() {
    for (var idx in this.activities)
        if (this.activities[idx].source == orcidVar.orcidId)
            return true;
    return false;
};

GroupedActivities.prototype.hasPut = function(putCode) {
    if (this.activities[putCode] !== undefined)
                return true;
        return false;
};

GroupedActivities.prototype.key = function(activityIdentifiers) {
    var idPath;
    var idTypePath;
    var relationship = 'relationship';
    if (this.type == GroupedActivities.ABBR_WORK) {
        idPath = 'workExternalIdentifierId';
        idTypePath = 'workExternalIdentifierType';
    } else if (this.type == GroupedActivities.FUNDING) {
        idPath = 'value';
        idTypePath = 'type';
    } else if (this.type == GroupedActivities.AFFILIATION) {
        // we don't have external identifiers for affiliations yet
        idPath = null;
        idTypePath = null;
    } else if (this.type == GroupedActivities.PEER_REVIEW) {
    	idPath = 'value';
        idTypePath = 'value';
    }
    
    var key = '';
    
    if (this.type ==  GroupedActivities.PEER_REVIEW) {
    	if(activityIdentifiers != null && activityIdentifiers[idPath] != null)
    		key += activityIdentifiers[idPath];
	} else if (activityIdentifiers[idTypePath]) {    
    	// ISSN is misused too often to identify a work
    	if (activityIdentifiers[idTypePath].value != 'issn'
        		&& (activityIdentifiers[relationship] == null || activityIdentifiers[relationship].value != 'part-of')
        		&& activityIdentifiers[idPath] != null
        		&& activityIdentifiers[idPath].value != null
        		&& activityIdentifiers[idPath].value != '') {    		
            key = activityIdentifiers[idTypePath].value;
            // currently I've been told all know identifiers are case insensitive so we are
            // lowercase the value for consistency
            key += activityIdentifiers[idPath] != null ? activityIdentifiers[idPath].value.toLowerCase() : '';
        }
    }
    return key;
};

GroupedActivities.prototype.keyMatch = function(activity) {
	var identifiersPath = null;
    identifiersPath = this.getIdentifiersPath();    
    
    if(this.type == GroupedActivities.PEER_REVIEW) {    	
    	if(this.key(activity[identifiersPath]) == '' || typeof this.key(activity[identifiersPath].value) === undefined) return false;
    	if(this.key(activity[identifiersPath]) in this._keySet)
    		return true;
    } else {
    	for (var idx in activity[identifiersPath]) {    	    	        
        	if (this.key(activity[identifiersPath][idx]) == '') continue;
            if (this.key(activity[identifiersPath][idx]) in this._keySet)
                return true;        
        }
    }
        
    return false;
};

GroupedActivities.prototype.highestVis = function() {
    var vis = this.getDefault().visibility;
    for (var idx in this.activities)
        if (vis == PRIVACY.PUBLIC)
            return vis;
        else if (this.activities[putCode].visibility == PRIVACY.PUBLIC)
            return PRIVACY.PUBLIC;
        else if (this.activities[putCode].visibility == PRIVACY.LIMITED)
            vis = PRIVACY.LIMITED;
    return vis;
};

GroupedActivities.prototype.makeDefault = function(putCode) {
    this.defaultPutCode = putCode;
    this.dateSortString = this.activities[putCode].dateSortString;
    var act = this.activities[putCode];
    var title = null;
    // at some point we should make this easier by making all paths match
    if (this.type == GroupedActivities.ABBR_WORK) title = act.title;
    else if (this.type == GroupedActivities.FUNDING) title = act.fundingTitle.title;
    else if (this.type == GroupedActivities.AFFILIATION) title = act.affiliationName;
    else if (this.type == GroupedActivities.PEER_REVIEW) title = act.subjectName;
    this.title =  title != null ? title.value : null;
};

GroupedActivities.prototype.rmByPut = function(putCode) {
    var activity =  this.activities[putCode];
    delete this.activities[putCode];
    this.activitiesCount--;
    if (putCode == this.defaultPutCode) {
        // make the first one default
        for (var idx in this.activities) {
            this.defaultPutCode = idx;
            break;
        }
    }
    if (putCode == this.activePutCode)
        this.activePutCode = this.defaultPutCode;
    return activity;
};

var orcidNgModule = angular.module('orcidApp', ['ngCookies','ngSanitize', 'ui.multiselect', 'vcRecaptcha']);

orcidNgModule.directive('ngModelOnblur', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, elm, attr, ngModelCtrl) {
            if (attr.type === 'radio' || attr.type === 'checkbox') return;

            elm.unbind('input').unbind('keydown').unbind('change');

            elm.bind("keydown keypress", function(event) {
                if (event.which === 13) {
                    scope.$apply(function() {
                        ngModelCtrl.$setViewValue(elm.val());
                    });
                }
            });

            elm.bind('blur', function() {
                scope.$apply(function() {
                    ngModelCtrl.$setViewValue(elm.val());
                });
            });
        }
    };
});

orcidNgModule.directive('appFileTextReader', function($q){
        var slice = Array.prototype.slice;
        return {
            restrict: 'A',
            require: 'ngModel',
            scope: {
                updateFn: '&'
            },
            link: function(scope, element, attrs, ngModelCtrl){
                if(!ngModelCtrl) return;
                ngModelCtrl.$render = function(){};
                element.bind('change', function(event){
                    var element = event.target;
                    $q.all(slice.call(element.files, 0).map(readFile))
                    .then(function(values){
                        if(element.multiple){
                            for(v in values){
                                ngModelCtrl.$viewValue.push(values[v]);
                            }
                        }
                        else{
                            ngModelCtrl.$setViewValue(values.length ? values[0] : null);
                        }
                        scope.updateFn(scope);
                        element.value = null;
                    });
                    function readFile(file) {
                        var deferred = $q.defer();
                        var reader = new FileReader();
                        reader.onload = function(event){
                            deferred.resolve(event.target.result);
                        };
                        reader.onerror = function(event) {
                            deferred.reject(event);
                        };
                        reader.readAsText(file);
                        return deferred.promise;
                    }
                });//change
            }//link
        };//return
    });//appFilereader

//Thanks to: https://docs.angularjs.org/api/ng/service/$compile#attributes
orcidNgModule.directive('compile', function($compile) {
    // directive factory creates a link function
    return function(scope, element, attrs) {
      scope.$watch(
        function(scope) {
           // watch the 'compile' expression for changes
          return scope.$eval(attrs.compile);
        },
        function(value) {
          // when the 'compile' expression changes
          // assign it into the current DOM
          element.html(value);

          // compile the new DOM and link it to the current
          // scope.
          // NOTE: we only compile .childNodes so that
          // we don't get into infinite loop compiling ourselves
          $compile(element.contents())(scope);
        }
      );
    };
  });


var ActSortState = function(groupType) {
    this.type = groupType;    
    this.predicateKey = 'date';
    if (this.type == 'peerReview') this.predicateKey = 'groupName';
    this.reverseKey = {};
    this.reverseKey['date']  = false;
    this.reverseKey['title'] = false;
    this.reverseKey['type']  = false;
    this.reverseKey['groupName']  = false;
    this.predicate = this.predicateMap[this.type][this.predicateKey];
};


var sortPredicateMap = {};
sortPredicateMap[GroupedActivities.ABBR_WORK] = {};
sortPredicateMap[GroupedActivities.ABBR_WORK]['date'] = ['-dateSortString', 'title','getDefault().workType.value'];
sortPredicateMap[GroupedActivities.ABBR_WORK]['title'] = ['title', '-dateSortString','getDefault().workType.value'];
sortPredicateMap[GroupedActivities.ABBR_WORK]['type'] = ['getDefault().workType.value','title', '-dateSortString'];

sortPredicateMap[GroupedActivities.FUNDING] = {};
sortPredicateMap[GroupedActivities.FUNDING]['date'] = ['-dateSortString', 'title','getDefault().fundingTypeForDisplay'];
sortPredicateMap[GroupedActivities.FUNDING]['title'] = ['title', '-dateSortString','getDefault().fundingTypeForDisplay'];
sortPredicateMap[GroupedActivities.FUNDING]['type'] = ['getDefault().fundingTypeForDisplay','title', '-dateSortString'];

sortPredicateMap[GroupedActivities.AFFILIATION] = {};
sortPredicateMap[GroupedActivities.AFFILIATION]['date'] = ['-dateSortString', 'title'];
sortPredicateMap[GroupedActivities.AFFILIATION]['title'] = ['title', '-dateSortString'];

sortPredicateMap[GroupedActivities.PEER_REVIEW] = {};
sortPredicateMap[GroupedActivities.PEER_REVIEW]['groupName'] = ['groupName'];

ActSortState.prototype.predicateMap = sortPredicateMap;


ActSortState.prototype.sortBy = function(key) {	
        if (this.predicateKey == key){
           this.reverse = !this.reverse;
           this.reverseKey[key] = !this.reverseKey[key];           
        }
        this.predicateKey = key;
        this.predicate = this.predicateMap[this.type][key];
};


orcidNgModule.factory("actBulkSrvc", ['$rootScope', function ($rootScope) {
    var actBulkSrvc = {
            initScope: function($scope) {
                $scope.bulkEditShow = false;
                $scope.bulkEditMap = {};
                $scope.bulkChecked = false;
                $scope.bulkDisplayToggle = false;
                $scope.toggleSelectMenu = function(){                	
                    $scope.bulkDisplayToggle = !$scope.bulkDisplayToggle;                    
                };
            }
    };
    return actBulkSrvc;
}]);

orcidNgModule.factory("commonSrvc", ['$rootScope', function ($rootScope) {
    var commonSrvc = {
            copyErrorsLeft: function (data1, data2) {
                for (var key in data1) {
                    if (key == 'errors') {
                        data1.errors = data2.errors;
                    } else {
                        if (data1[key] != null && data1[key].errors !== undefined)
                        data1[key].errors = data2[key].errors;
                    };
                };
            }
    };
    return commonSrvc;
}]);


orcidNgModule.factory("affiliationsSrvc", ['$rootScope', function ($rootScope) {
    var serv = {
            educations: new Array(),
            employments: new Array(),
            loading: false,
            affiliationsToAddIds: null,
            addAffiliationToScope: function(path) {
                if( serv.affiliationsToAddIds.length != 0 ) {
                    var affiliationIds = serv.affiliationsToAddIds.splice(0,20).join();
                    var url = getBaseUri() + '/' + path + '?affiliationIds=' + affiliationIds;
                    
                    $.ajax({
                        url: url,                        
                        headers : {'Content-Type': 'application/json'},
                        method: 'GET',
                        success: function(data) {
                            for (i in data) {

                                if (data[i].affiliationType != null && data[i].affiliationType.value != null
                                        && data[i].affiliationType.value == 'education')
                                    GroupedActivities.group(data[i],GroupedActivities.AFFILIATION,serv.educations);
                                else if (data[i].affiliationType != null && data[i].affiliationType.value != null
                                        && data[i].affiliationType.value == 'employment')
                                    GroupedActivities.group(data[i],GroupedActivities.AFFILIATION,serv.employments);
                            };
                            if (serv.affiliationsToAddIds.length == 0) {
                                serv.loading = false;
                                $rootScope.$apply();
                            } else {
                                $rootScope.$apply();
                                setTimeout(function () {
                                    serv.addAffiliationToScope(path);
                                },50);
                            }
                        }
                    }).fail(function(e) {
                        console.log(e.statusText);
                    });
                } else {
                    serv.loading = false;
                };
            },
            setIdsToAdd: function(ids) {
                serv.affiliationsToAddIds = ids;
            },
            getAffiliations: function(path) {
                //clear out current affiliations
                serv.loading = true;
                serv.affiliationsToAddIds = null;
                serv.educations.length = 0;
                serv.employments.length = 0;
                //get affiliation ids
                $.ajax({
                    url: getBaseUri() + '/' + path,
                    dataType: 'json',
                    success: function(data) {
                        serv.affiliationsToAddIds = data;
                        serv.addAffiliationToScope('affiliations/affiliations.json');
                        $rootScope.$apply();
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error fetching affiliations");
                });
            },
            updateProfileAffiliation: function(aff) {
                $.ajax({
                    url: getBaseUri() + '/affiliations/affiliation.json',
                    type: 'PUT',
                    data: angular.toJson(aff),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if(data.errors.length != 0){
                            console.log("Unable to update profile affiliation.");
                        }
                        $rootScope.$apply();
                    }
                }).fail(function() {
                    console.log("Error updating profile affiliation.");
                });
            },
            deleteAffiliation: function(affiliation) {
                var arr = null;
                if (affiliation.affiliationType != null && affiliation.affiliationType.value != null
                        && affiliation.affiliationType.value == 'education')
                    arr = serv.educations;
                if (affiliation.affiliationType != null && affiliation.affiliationType.value != null
                        && affiliation.affiliationType.value == 'employment')
                    arr = serv.employments;
                var idx;
                for (var idx in arr) {
                    if (arr[idx].activePutCode == affiliation.putCode.value) {
                        break;
                    }
                }
                arr.splice(idx, 1);
                $.ajax({
                    url: getBaseUri() + '/affiliations/affiliations.json',
                    type: 'DELETE',
                    data: angular.toJson(affiliation),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if(data.errors.length != 0){
                            console.log("Unable to delete affiliation.");
                        }
                        $rootScope.$apply();
                    }
                }).fail(function() {
                    console.log("Error deleting affiliation.");
                });
            }
    };
    return serv;
}]);

orcidNgModule.factory("workspaceSrvc", ['$rootScope', function ($rootScope) {
    var serv = {
            displayEducation: true,
            displayEmployment: true,
            displayFunding: true,
            displayPersonalInfo: true,
            displayWorks: true,
            displayPeerReview: true,
            toggleEducation: function() {
                serv.displayEducation = !serv.displayEducation;
            },
            toggleEmployment: function() {
                serv.displayEmployment = !serv.displayEmployment;
            },
            toggleFunding: function() {
                serv.displayFunding = !serv.displayFunding;
            },
            togglePersonalInfo: function() {
                serv.displayPersonalInfo = !serv.displayPersonalInfo;
            },
            toggleWorks: function() {
                serv.displayWorks = !serv.displayWorks;
            },
            togglePeerReview: function() {            	
            	serv.displayPeerReview = !serv.displayPeerReview;
            },
            openEducation: function() {
                serv.displayEducation = true;
            },
            openFunding: function() {
                serv.displayFunding = true;
            },
            openEmployment: function() {
                serv.displayEmployment = true;
            },
            openPersonalInfo: function() {
                serv.displayPersonalInfo = true;
            },
            openWorks: function() {
                serv.displayWorks = true;
            },
            openPeerReview: function() {
                serv.displayPeerReview = true;
            },
            togglePeerReviews : function() {
            	serv.displayPeerReview = !serv.displayPeerReview;
            }
            
    };
    return serv;
}]);

/**
 * Fundings Service
 * */
orcidNgModule.factory("fundingSrvc", ['$rootScope', function ($rootScope) {
    var fundingSrvc = {
            fundings: new Array(),
            groups: new Array(),
            loading: false,
            constants: { 'access_type': { 'USER': 'user', 'ANONYMOUS': 'anonymous'}},
            fundingToAddIds: null,
            moreDetailsActive: false,
            addFundingToScope: function(path) {
                if( fundingSrvc.fundingToAddIds.length != 0 ) {
                    var fundingIds = fundingSrvc.fundingToAddIds.splice(0,20).join();
                    $.ajax({
                        url: getBaseUri() + '/' + path + '?fundingIds=' + fundingIds,
                        dataType: 'json',
                        success: function(data) {
                                for (i in data) {
                                    var funding = data[i];
                                    GroupedActivities.group(funding,GroupedActivities.FUNDING,fundingSrvc.groups);
                                };
                                if (fundingSrvc.fundingToAddIds.length == 0) {
                                    fundingSrvc.loading = false;
                                    $rootScope.$apply();
                                } else {
                                    $rootScope.$apply();
                                    setTimeout(function () {
                                        fundingSrvc.addFundingToScope(path);
                                    },50);
                                }
                        }
                    }).fail(function() {
                        console.log("Error fetching fundings");
                    });
                } else {
                    fundingSrvc.loading = false;
                };
            },
            createNew: function(work) {
                var cloneF = JSON.parse(JSON.stringify(work));
                cloneF.source = null;
                cloneF.putCode = null;
                for (var idx in cloneF.externalIdentifiers)
                    cloneF.externalIdentifiers[idx].putCode = null;
                return cloneF;
            },
            getEditable: function(putCode, callback) {
                // first check if they are the current source
                var funding = fundingSrvc.getFunding(putCode);
                if (funding.source == orcidVar.orcidId)
                    callback(funding);
                else {
                    var bestMatch = null;
                    var group = fundingSrvc.getGroup(putCode);
                    for (var idx in group.activitiess) {
                        if (group[idx].source == orcidVar.orcidId) {
                            bestMatch = callback(group[idx]);
                            break;
                        }
                    }
                    if (bestMatch == null) 
                        bestMatch = fundingSrvc.createNew(funding);
                    callback(bestMatch);
                };
            },
            deleteFunding: function(putCode) {
                var rmFunding;
                for (var idx in fundingSrvc.groups) {
                    if (fundingSrvc.groups[idx].hasPut(putCode)) {
                        rmFunding = fundingSrvc.groups[idx].rmByPut(putCode);
                        if (fundingSrvc.groups[idx].activitiesCount == 0)
                            fundingSrvc.groups.splice(idx,1);
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
                        if (fundingSrvc.groups[idx].hasPut(putCode))
                            return fundingSrvc.groups[idx].getByPut(putCode);
                }
                return null;
            },
            getFundings: function(path) {
                //clear out current fundings
                fundingSrvc.loading = true;
                fundingSrvc.fundingToAddIds = null;
                //new way
                fundingSrvc.groups.length = 0;
                //get funding ids
                $.ajax({
                    url: getBaseUri() + '/'  + path,
                    dataType: 'json',
                    success: function(data) {
                        fundingSrvc.fundingToAddIds = data;
                        fundingSrvc.addFundingToScope('fundings/fundings.json');
                        $rootScope.$apply();
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error fetching fundings");
                });
            },
            getGroup: function(putCode) {
                for (var idx in fundingSrvc.groups) {
                        if (fundingSrvc.groups[idx].hasPut(putCode))
                            return fundingSrvc.groups[idx];
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
                        if(data.errors.length != 0){
                            console.log("Unable to delete funding.");
                        } else {
                            // new groups
                            for (var idx in fundingSrvc.groups) {
                                if (fundingSrvc.groups[idx].hasPut(funding.putCode.value)) {
                                    rmWorks = fundingSrvc.groups[idx].rmByPut(funding.putCode.value);
                                    if (fundingSrvc.groups[idx].activitiesCount == 0)
                                        fundingSrvc.groups.splice(idx,1);
                                    break;
                                }
                            }
                        }
                        $rootScope.$apply();
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
                var idx;
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
                        if(data.errors.length != 0){
                            console.log("Unable to update profile funding.");
                        }
                        $rootScope.$apply();
                    }
                }).fail(function() {
                    console.log("Error updating profile funding.");
                });
            }
    };
    return fundingSrvc;
}]);

orcidNgModule.factory("worksSrvc", ['$rootScope', function ($rootScope) {
    var worksSrvc = {
            bibtexJson: {},
            constants: { 'access_type': { 'USER': 'user', 'ANONYMOUS': 'anonymous'}},
            groups: new Array(),
            quickRef: {},
            loading: false,
            loadingDetails: false,
            blankWork: null,
            details: new Object(), // we should think about putting details in the
            worksToAddIds: null,             
            addBibtexJson: function(dw) {
                if (dw.citation && dw.citation.citationType && dw.citation.citationType.value == 'bibtex') {
                    try {
                        worksSrvc.bibtexJson[dw.putCode.value] = bibtexParse.toJSON(dw.citation.citation.value);
                    } catch (err) {
                        worksSrvc.bibtexJson[dw.putCode.value] = null;
                        console.log("couldn't parse bibtex: " + dw.citation.citation.value);
                    };
                };
            },
            addAbbrWorksToScope: function(type) {
                if (type == worksSrvc.constants.access_type.USER)
                    var url = getBaseUri() + '/works/works.json?workIds=';
                else // use the anonymous url
                    var url = getBaseUri() + '/' + orcidVar.orcidId +'/works.json?workIds='; // public
                if(worksSrvc.worksToAddIds.length != 0 ) {
                    worksSrvc.loading = true;
                    var workIds = worksSrvc.worksToAddIds.splice(0,20).join();
                    $.ajax({
                        'url': url + workIds,
                        'dataType': 'json',
                        'success': function(data) {
                            $rootScope.$apply(function(){
                                for (i in data) {
                                    var dw = data[i];
                                    removeBadContributors(dw);
                                    removeBadExternalIdentifiers(dw);
                                    worksSrvc.addBibtexJson(dw);
                                    GroupedActivities.group(dw,GroupedActivities.ABBR_WORK,worksSrvc.groups);
                                };
                            });
                            if(worksSrvc.worksToAddIds.length == 0 ) {
                                worksSrvc.loading = false;
                                $rootScope.$apply();
                                fixZindexIE7('.workspace-public workspace-body-list li',99999);
                                fixZindexIE7('.workspace-toolbar',9999);
                            } else {
                                $rootScope.$apply();
                                setTimeout(function(){
                                    worksSrvc.addAbbrWorksToScope(type);
                                },50);
                            }
                        }
                    }).fail(function() {
                        //$rootScope.$apply(function() {
                            worksSrvc.loading = false;
                        //});
                        console.log("Error fetching works: " + workIds);
                    });
                } else {
                    worksSrvc.loading = false;
                };
            },
            createNew: function(work) {
                var cloneW = JSON.parse(JSON.stringify(work));
                cloneW.source = null;
                cloneW.putCode = null;
                cloneW.contributors = [];
                return cloneW;
            },
            copyEIs: function(from, to) {
                // add all identiifers
                if (to.workExternalIdentifiers == undefined)
                    to.workExternalIdentifiers = new Array();
                for (var idx in from.workExternalIdentifiers)
                    to.workExternalIdentifiers.push(JSON.parse(JSON.stringify(from.workExternalIdentifiers[idx])));
                return to;
            },
            getBlankWork: function(callback) {
                // if cached return clone of blank
                if (worksSrvc.blankWork != null)
                    callback(JSON.parse(JSON.stringify(worksSrvc.blankWork)));
                $.ajax({
                    url: getBaseUri() + '/works/work.json',
                    dataType: 'json',
                    success: function(data) {
                        blankWork =  data;
                        callback(data);
                    }
                }).fail(function() {
                    console.log("Error fetching blank work");
                });
            },
            getDetails: function(putCode, type, callback) {
                if (type == worksSrvc.constants.access_type.USER)
                    var url = getBaseUri() + '/works/getWorkInfo.json?workId=';
                else // use the anonymous url
                    var url = getBaseUri() + '/' + orcidVar.orcidId + '/getWorkInfo.json?workId='; // public
                if(worksSrvc.details[putCode] == undefined) {
                    $.ajax({
                        url: url + putCode,
                        dataType: 'json',
                        success: function(data) {
                            $rootScope.$apply(function () {
                                removeBadContributors(data);
                                removeBadExternalIdentifiers(data);
                                worksSrvc.addBibtexJson(data);
                                worksSrvc.details[putCode] = data;
                                if (callback != undefined) callback(worksSrvc.details[putCode]);
                            });
                        }
                    }).fail(function(){
                        // something bad is happening!
                        console.log("error fetching works");
                    });
                } else {
                    if (callback != undefined) callback(worksSrvc.details[putCode]);
                };
            },
            getEditable: function(putCode, callback) {
                // first check if they are the current source
                var work = worksSrvc.getDetails(putCode, worksSrvc.constants.access_type.USER, function(data) {
                    if (data.source == orcidVar.orcidId)
                        callback(data);
                    else
                        worksSrvc.getGroupDetails(putCode, worksSrvc.constants.access_type.USER, function () {
                            // in this case we want to open their version
                            // if they don't have a version yet then copy
                            // the current one
                            var bestMatch = null;
                            for (var idx in worksSrvc.details)
                                if (worksSrvc.details[idx].source == orcidVar.orcidId) {
                                    bestMatch = worksSrvc.details[idx];
                                    break;
                                }
                            if (bestMatch == null) {
                                bestMatch = worksSrvc.createNew(worksSrvc.details[putCode]);
                            }
                            callback(bestMatch);
                        });
                });
            },
            getGroup: function(putCode) {
                for (var idx in worksSrvc.groups) {
                        if (worksSrvc.groups[idx].hasPut(putCode))
                            return worksSrvc.groups[idx];
                }
                return null;
            },
            getGroupDetails: function(putCode, type, callback) {
                var group = worksSrvc.getGroup(putCode);
                var needsLoading =  new Array();
                for (var idx in group.activities) {
                    needsLoading.push(group.activities[idx].putCode.value)
                }

                var popFunct = function () {
                    if (needsLoading.length > 0)
                        worksSrvc.getDetails(needsLoading.pop(), type, popFunct);
                    else if (callback != undefined)
                        callback();
                };
                popFunct();
            },
            getWork: function(putCode) {
                for (var idx in worksSrvc.groups) {
                        if (worksSrvc.groups[idx].hasPut(putCode))
                            return worksSrvc.groups[idx].getByPut(putCode);
                }
                return null;
            },
            deleteGroupWorks: function(putCodes) {
                var rmWorks = new Array();
                var rmGroups = new Array();
                for (var idj in putCodes)
                    for (var idx in worksSrvc.groups) {
                        if (worksSrvc.groups[idx].hasPut(putCodes[idj])) {
                            rmGroups.push(idx);
                            for (var idj in worksSrvc.groups[idx].activities)
                                rmWorks.push(worksSrvc.groups[idx].activities[idj].putCode.value);
                        };
                    }
                while (rmGroups.length > 0) 
                    worksSrvc.groups.splice(rmGroups.pop(),1);
                worksSrvc.removeWorks(rmWorks);
            },
            deleteWork: function(putCode) {
                worksSrvc.removeWorks([putCode], function() {worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.USER);});
            },
            makeDefault: function(group, putCode) {
                group.makeDefault(putCode);
                $.ajax({
                    url: getBaseUri() + '/works/updateToMaxDisplay.json?putCode=' + putCode,
                    dataType: 'json',
                    success: function(data) {
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("some bad is hppending");
                });
            },
            loadAbbrWorks: function(access_type) {
                if (access_type == worksSrvc.constants.access_type.ANONYMOUS) {
                    worksSrvc.worksToAddIds = orcidVar.workIds;
                    worksSrvc.addAbbrWorksToScope(worksSrvc.constants.access_type.ANONYMOUS);
                } else {
                    worksSrvc.worksToAddIds = null;
                    worksSrvc.loading = true;
                    worksSrvc.groups = new Array();
                    worksSrvc.details = new Object();
                    $.ajax({
                        url: getBaseUri() + '/works/workIds.json',
                        dataType: 'json',
                        success: function(data) {
                            worksSrvc.worksToAddIds = data;
                            worksSrvc.addAbbrWorksToScope(worksSrvc.constants.access_type.USER);
                            $rootScope.$apply();
                        }
                    }).fail(function(){
                        // something bad is happening!
                        console.log("error fetching works");
                    });
                };
            },
            putWork: function(work,sucessFunc, failFunc) {
                $.ajax({
                    url: getBaseUri() + '/works/work.json',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    type: 'POST',
                    data: angular.toJson(work),
                    success: function(data) {
                        sucessFunc(data);
                    }
                }).fail(function(){
                    failFunc();
                });
            },
            removeWorks: function(putCodes,callback) {
                $.ajax({
                    url: getBaseUri() + '/works/' + putCodes.splice(0,150).join(),
                    type: 'DELETE',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if (putCodes.length > 0) 
                            worksSrvc.removeWorks(putCodes,callback);
                        else if (callback)
                            callback(data);
                    }
                }).fail(function() {
                    console.log("Error deleting works.");
                });
            },
            setGroupPrivacy: function(putCode, priv) {
                var group = worksSrvc.getGroup(putCode);
                var putCodes = new Array();
                for (var idx in group.activities) {
                    putCodes.push(group.activities[idx].putCode.value);
                    group.activities[idx].visibility = priv;
                }
                worksSrvc.updateVisibility(putCodes, priv);
            },
            setPrivacy: function(putCode, priv) {
                worksSrvc.updateVisibility([putCode], priv);
            },
            updateVisibility: function(putCodes, priv) {
                $.ajax({
                    url: getBaseUri() + '/works/' + putCodes.splice(0,150).join() + '/visibility/'+priv,
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if (putCodes.length > 0)
                            worksSrvc.updateVisibility(putCodes, priv);
                    }
                }).fail(function() {
                    console.log("Error updating profile work.");
                });
            },
            workCount: function() {
                var count = 0;
                for (var idx in worksSrvc.groups) {
                    count += worksSrvc.groups[idx].activitiesCount;
                }
                return count;
            },
            worksValidate: function(works,sucessFunc, failFunc) {
                $.ajax({
                    url: getBaseUri() + '/works/worksValidate.json',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    type: 'POST',
                    data: angular.toJson(works),
                    success: function(data) {
                        sucessFunc(data);
                    }
                }).fail(function(){
                    failFunc();
                });
            },
            getUniqueDois : function(putCode){
            	var dois = [];            	
            	var group = worksSrvc.getGroup(putCode);
            	
            	for (var idx in group.activities) {            		
            		for (i = 0; i <= group.activities[idx].workExternalIdentifiers.length - 1; i++) {
            			if (group.activities[idx].workExternalIdentifiers[i].workExternalIdentifierType.value == 'doi'){
            				if (isIndexOf.call(dois, group.activities[idx].workExternalIdentifiers[i].workExternalIdentifierId.value) == -1){
            					dois.push(group.activities[idx].workExternalIdentifiers[i].workExternalIdentifierId.value);
            				}
            			}
            		}
                }
            	
            	return dois;
            }
    };
    return worksSrvc;
}]);

orcidNgModule.factory("emailSrvc", function ($rootScope) {
    var serv = {
            emails: null,            
            inputEmail: null,
            delEmail: null,
            primaryEmail: null,
            popUp: false,
            addEmail: function() {            	
                $.ajax({
                    url: getBaseUri() + '/account/addEmail.json',
                    data:  angular.toJson(serv.inputEmail),
                    contentType: 'application/json;charset=UTF-8',
                    type: 'POST',
                    dataType: 'json',
                    success: function(data) {
                        serv.inputEmail = data;
                        if (serv.inputEmail.errors.length == 0) {
                            serv.initInputEmail();
                            serv.getEmails();
                        }
                        $rootScope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with multi email");
                });
            },
            getEmails: function(callback) {
            	
                $.ajax({
                    url: getBaseUri() + '/account/emails.json',
                    type: 'GET',
                    dataType: 'json',
                    success: function(data) {                    	
                        serv.emails = data;
                        for (var i in data.emails){
                            if (data.emails[i].primary){
                                serv.primaryEmail = data.emails[i];
                            }
                        }                                                
                        $rootScope.$apply();
                        if (callback)
                           callback(data);
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with multi email");
                });
            },
            deleteEmail: function (callback) {
                $.ajax({
                    url: getBaseUri() + '/account/deleteEmail.json',
                    type: 'DELETE',
                    data:  angular.toJson(serv.delEmail),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        serv.getEmails();
                        if (callback)
                               callback();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("$EmailEditCtrl.deleteEmail() error");
                });
            },
            initInputEmail: function () {
                serv.inputEmail = {"value":"","primary":false,"current":true,"verified":false,"visibility":"PRIVATE","errors":[]};
            },
            setPrivacy: function(email, priv) {
                email.visibility = priv;
                serv.saveEmail();
            },
            setPrimary: function(email) {
                for (i in serv.emails.emails) {
                    if (serv.emails.emails[i] == email) {
                        serv.emails.emails[i].primary = true;
                    } else {
                        serv.emails.emails[i].primary = false;
                    }
                }
                serv.saveEmail();
            },
            saveEmail: function(callback) {
                $.ajax({
                    url: getBaseUri() + '/account/emails.json',
                    type: 'POST',
                    data: angular.toJson(serv.emails),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        serv.data;
                        $rootScope.$apply();
                        if (callback)
                            callback(data);
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with multi email");
                });
            },
            verifyEmail: function(email, callback) {
                $.ajax({
                    url: getBaseUri() + '/account/verifyEmail.json',
                    type: 'get',
                    data:  { "email": email.value },
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if (callback)
                            callback(data);
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with multi email");
                });
            }

        };

    return serv;
});



orcidNgModule.factory("prefsSrvc", function ($rootScope) {
    var serv = {
            prefs: null,
            saved: false,
            getPrivacyPreferences: function() {
                $.ajax({
                    url: getBaseUri() + '/account/preferences.json',
                    dataType: 'json',
                    success: function(data) {
                        serv.prefs = data;
                        $rootScope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with prefs");
                });
            },
            savePrivacyPreferences: function() {
                $.ajax({
                    url: getBaseUri() + '/account/preferences.json',
                    type: 'POST',
                    data: angular.toJson(serv.prefs),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        serv.prefs = data;
                        serv.saved = true;
                        $rootScope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with prefs");
                });
            },
            clearMessage: function(){
                serv.saved = false;
            }
        };

        // populate the prefs
        serv.getPrivacyPreferences();

    return serv; 
});

orcidNgModule.factory("notificationsSrvc", ['$rootScope', '$q', function ($rootScope, $q) {
    var defaultMaxResults = 10;
    var serv = {
        loading: true,
        loadingMore: false,
        firstResult: 0,
        maxResults: defaultMaxResults,
        areMoreFlag: false,
        notifications: [],
        displayBody: {},
        unreadCount: 0,
        showArchived: false,
        bulkChecked: false,
        bulkArchiveMap: [],
        selectionActive: false,
        getNotifications: function() {
            var url = getBaseUri() + '/inbox/notifications.json?firstResult=' + serv.firstResult + '&maxResults=' + serv.maxResults;             
            if(serv.showArchived){
                url += "&includeArchived=true";
            }
            $.ajax({
                url: url,
                dataType: 'json',
                success: function(data) {
                    if(data.length === 0 || data.length < serv.maxResults){
                        serv.areMoreFlag = false;
                    }
                    else{
                        serv.areMoreFlag = true;
                    }
                    for(var i = 0; i < data.length; i++){                    	
                        serv.notifications.push(data[i]);
                    }
                    serv.loading = false;
                    serv.loadingMore = false;
                    $rootScope.$apply();
                    serv.resizeIframes();
                }
            }).fail(function() {
                serv.loading = false;
                serv.loadingMore = false;
                // something bad is happening!
                console.log("error with getting notifications");
            });
        },
        reloadNotifications: function() {
            serv.loading = true;
            serv.notifications.length = 0;
            serv.firstResult = 0;
            serv.maxResults = defaultMaxResults;
            serv.getNotifications();            
        },
        retrieveUnreadCount: function() {
            $.ajax({
                url: getBaseUri() + '/inbox/unreadCount.json',
                dataType: 'json',
                success: function(data) {
                    serv.unreadCount = data;                   
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with getting count of unread notifications");
            });
        },
        resizeIframes: function(){
        	var activeViews = serv.displayBody;
			for (key in activeViews){
				iframeResize(key);				
			}
        },
        getUnreadCount: function() {
            return serv.unreadCount;
        },
        showMore: function() {
            serv.loadingMore = true;
            serv.firstResult += serv.maxResults;
            serv.getNotifications();
        },
        areMore: function() {
            return serv.areMoreFlag;
        },
        flagAsRead: function(notificationId) {
            $.ajax({
                url: getBaseUri() + '/inbox/' + notificationId + '/read.json',
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    var updated = data;
                    for(var i = 0;  i < serv.notifications.length; i++){
                        var existing = serv.notifications[i];
                        if(existing.putCode === updated.putCode){
                            existing.readDate = updated.readDate;
                        }
                    }
                    serv.retrieveUnreadCount();
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error flagging notification as read");
            });
        },
        archive: function(notificationId) {        	
            $.ajax({
                url: getBaseUri() + '/inbox/' + notificationId + '/archive.json',
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    var updated = data;
                    for(var i = 0;  i < serv.notifications.length; i++){
                        var existing = serv.notifications[i];
                        if(existing.putCode === updated.putCode){
                            serv.notifications.splice(i, 1);
                            if(serv.firstResult > 0){
                                serv.firstResult--;
                            }
                            break;
                        }
                    }
                    serv.retrieveUnreadCount();
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error flagging notification as archived");
            });
        },
        toggleArchived: function(){
            serv.showArchived = !serv.showArchived;
            serv.reloadNotifications();
        },
        swapbulkChangeAll: function(){        	
            serv.bulkChecked = !serv.bulkChecked;
            if(serv.bulkChecked == false)
                serv.bulkArchiveMap.length = 0;
            else
                for (var idx in serv.notifications)
                    serv.bulkArchiveMap[serv.notifications[idx].putCode] = serv.bulkChecked;
                serv.selectionActive = true;
            
            
        },
        bulkArchive: function(){            
            var promises = [];
            var tmpNotifications = serv.notifications;
            
            function archive(notificationId){                
                var defer = $q.defer(notificationId);                
                $.ajax({
                    url: getBaseUri() + '/inbox/' + notificationId + '/archive.json',
                    type: 'POST',
                    dataType: 'json',
                    success: function(data) {
                        defer.resolve(notificationId);
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error flagging notification as archived");
                });                
                return defer.promise;
            }
            
            for (putCode in serv.bulkArchiveMap)
                if(serv.bulkArchiveMap[putCode])
                    promises.push(archive(putCode));            
            
            $q.all(promises).then(function(){
                serv.bulkArchiveMap.length = 0;
                serv.bulkChecked = false;
                serv.reloadNotifications();
            });
            
        },
        checkSelection: function(){
            
            var count = 0;
            var totalNotifications = 0;
            
            serv.selectionActive = false;            
            
            for (putCode in serv.bulkArchiveMap){                
                if(serv.bulkArchiveMap[putCode] == true){
                    serv.selectionActive = true;
                    count++;
                }
            }                      
            
            for (i = 0; i < serv.notifications.length; i++)                
                if (serv.notifications[i].archivedDate == null)
                    totalNotifications++;            
            
            totalNotifications == count ? serv.bulkChecked = true :	serv.bulkChecked = false;
            
        }
    };
    serv.getNotifications();    
    return serv;
}]);

orcidNgModule.factory("widgetSrvc", ['$rootScope', function ($rootScope) {
    var widgetSrvc = {
		locale: 'en',
        setLocale: function (locale) {
            widgetSrvc.locale = locale;
        }
    };
    return widgetSrvc;
}]);

orcidNgModule.factory("discoSrvc", ['$rootScope', 'widgetSrvc', function ($rootScope, widgetSrvc) {
    var serv = {
        feed: null,
        getDiscoFeed: function() {
            $.ajax({
                url: getBaseUri() + '/Shibboleth.sso/DiscoFeed',
                dataType: 'json',
                cache: true,
                success: function(data) {
                    serv.feed = data;
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with disco feed");
                serv.feed = [];
                $rootScope.$apply();
            });
        },
        getIdPName: function(entityId) {
            var locale = widgetSrvc.locale != null ? widgetSrvc.locale : "en";
            for(i in serv.feed) {
                var idp = serv.feed[i];
                if(entityId === idp.entityID) {
                    var name = idp.DisplayNames[0].value;
                    for(j in idp.DisplayNames){
                        var displayName = idp.DisplayNames[j];
                        if(locale === displayName.lang){
                            name = displayName.value;
                        }
                    }
                    return name;
                }
            }
            if(entityId === "facebook" || entityId === "google"){
                return entityId.charAt(0).toUpperCase() + entityId.slice(1);
            }
            return entityId;
        }
    };

    // populate the disco feed
    serv.getDiscoFeed();
    return serv; 
}]);


orcidNgModule.filter('urlProtocol', function(){
    return function(url){
    	if (url == null) return url;
    	if(!url.startsWith('http')) {    			
            if (url.startsWith('//')){            	
            	url = ('https:' == document.location.protocol ? 'https:' : 'http:') + url;
          	} else {
          	    url = 'http://' + url;    
          	}
        }
        return url;
    }
});

orcidNgModule.filter('latex', function(){
    return function(input){
        if (input == null) return "";
        return latexParseJs.decodeLatex(input);
    };
});


orcidNgModule.filter('ajaxFormDateToISO8601', function(){
    return function(input){
    	if (typeof input != 'undefined'){
	        var str = '';
	        if (input.year) str += input.year;
	        if (input.month) {
	            if (str.length > 0) str += '-';
	            str += Number(input.month).pad(2);
	        }
	        if (input.day) {
	            if (str.length > 0)
	                str += '-';
	            str += Number(input.day).pad(2);
	        }
	        return str;
    	} else {
    		return false;
    	}
    };
});

orcidNgModule.filter('humanDate', function($filter){
    var standardDateFilter = $filter('date');
    return function(input){
        var inputDate = new Date(input);
        var dateNow = new Date();
        var dateFormat = (inputDate.getYear() === dateNow.getYear() && inputDate.getMonth() === dateNow.getMonth() && inputDate.getDate() === dateNow.getDate())  ? 'HH:mm' : 'yyyy-MM-dd';
        return standardDateFilter(input, dateFormat);
    };
});


function formColorBoxWidth() {
    return isMobile()? '100%': '800px';
}

function formColorBoxResize() {
    if (isMobile())
        $.colorbox.resize({width: formColorBoxWidth(), height: '100%'});
    else
        // IE8 and below doesn't take auto height
        // however the default div height
        // is auto anyway
        $.colorbox.resize({width:'800px'});
}

function fixZindexIE7(target, zindex){
    if(isIE() == 7){
        $(target).each(function(){
            $(this).css('z-index', zindex);
            --zindex;
        });
    }
}

function emptyTextField(field) {
    if (field != null
            && field.value != null
            && field.value.trim() != '') return false;
    return true;
}

function addComma(str) {
    if (str.length > 0) return str + ', ';
    return str;
}

orcidNgModule.filter('contributorFilter', function(){
    return function(ctrb){
        var out = '';
        if (!emptyTextField(ctrb.contributorRole)) out = out + ctrb.contributorRole.value;
        if (!emptyTextField(ctrb.contributorSequence)) out = addComma(out) + ctrb.contributorSequence.value;
        if (!emptyTextField(ctrb.orcid)) out = addComma(out) + ctrb.orcid.value;
        if (!emptyTextField(ctrb.email)) out = addComma(out) + ctrb.email.value;
        if (out.length > 0) out = '(' + out + ')';
        return out;
    };
});


orcidNgModule.filter('workExternalIdentifierHtml', function($filter){
    return function(workExternalIdentifier, first, last, length, moreInfo){

        var output = '';
        var ngclass = '';
        var isPartOf = false;
        
        if (moreInfo == false || typeof moreInfo == 'undefined') ngclass = 'truncate-anchor';
        
        
        if(workExternalIdentifier.relationship != null && workExternalIdentifier.relationship.value == 'part-of')
        	isPartOf = true;
        
        if (workExternalIdentifier == null) return output;
        if (workExternalIdentifier.workExternalIdentifierId == null) return output;

        var id = workExternalIdentifier.workExternalIdentifierId.value;
        var type;

        if (workExternalIdentifier.workExternalIdentifierType != null)
            type = workExternalIdentifier.workExternalIdentifierType.value;
        if (type != null) {
        	if(isPartOf) 
        		output = output + "<span class='italic'>" + om.get("common.part_of") + " <span class='type'>" + type.toUpperCase() + "</span></span>: ";
        	else 
        		output = output + "<span class='type'>" + type.toUpperCase() + "</span>: ";
        }
        var link = null;

        if (workExternalIdentifier.url != null && workExternalIdentifier.url.value != '')
        	link = workExternalIdentifier.url.value;
        else link = workIdLinkJs.getLink(id,type); 
        	
        if (link != null){
        	link = $filter('urlProtocol')(link);
        	
            output = output + '<a href="' + link.replace(/'/g, "&#39;") + '" class ="' + ngclass + '"' + " target=\"_blank\" ng-mouseenter=\"showURLPopOver(work.putCode.value + $index)\" ng-mouseleave=\"hideURLPopOver(work.putCode.value + $index)\">" + id.escapeHtml() + '</a>';
            
        }else{
            output = output + id;        
        }
        
        output += '<div class="popover-pos">\
			<div class="popover-help-container">\
	        	<div class="popover bottom" ng-class="{'+"'block'"+' : displayURLPopOver[work.putCode.value + $index] == true}">\
					<div class="arrow"></div>\
					<div class="popover-content">\
				    	<a href="'+link+'" target="_blank" class="ng-binding">'+link+'</a>\
				    </div>\
				</div>\
			</div>\
	  </div>';

      return output;
    };
});

//Currently being used in Fundings only
orcidNgModule.filter('externalIdentifierHtml', ['fundingSrvc', '$filter', function(fundingSrvc, $filter){
    return function(externalIdentifier, first, last, length, type, moreInfo){
    	
    	var ngclass = '';
    	var output = '';

        if (externalIdentifier == null) return output;
        
        //If type is set always come: "grant_number"
        if (type != null) {
        	if (type.value == 'grant') {
        		output += om.get('funding.add.external_id.value.label.grant') + ": ";
        	} else if (type.value == 'contract') {
        		output += om.get('funding.add.external_id.value.label.contract') + ": ";
        	} else {
        		output += om.get('funding.add.external_id.value.label.award') + ": ";
        	}
        }         
        
        var value = null;        
        if(externalIdentifier.value != null){
        	value = externalIdentifier.value.value;
        }
        
        var link = null;
        if(externalIdentifier.url != null)
            link = externalIdentifier.url.value;
       
        if(link != null) {
        	
        	link = $filter('urlProtocol')(link);
        	
        	if(value != null) {
        		output += "<a href='" + link + "' class='truncate-anchor' target='_blank' ng-mouseenter='showURLPopOver(funding.putCode.value+ $index)' ng-mouseleave='hideURLPopOver(funding.putCode.value + $index)'>" + value + "</a>";
        	} else {
        		if(type != null) {
        			
        			if (moreInfo == false || typeof moreInfo == 'undefined') ngclass = 'truncate-anchor';
        			
        			if(type.value == 'grant') {
        				output = om.get('funding.add.external_id.url.label.grant') + ': <a href="' + link + '" class="' + ngclass + '"' + " target=\"_blank\" ng-mouseenter=\"showURLPopOver(funding.putCode.value + $index)\" ng-mouseleave=\"hideURLPopOver(funding.putCode.value + $index)\">" + link + "</a>";
        			} else if(type.value == 'contract') {
        				output = om.get('funding.add.external_id.url.label.contract') + ': <a href="' + link + '" class="' + ngclass + '"' + " target=\"_blank\" ng-mouseenter=\"showURLPopOver(funding.putCode.value + $index)\" ng-mouseleave=\"hideURLPopOver(funding.putCode.value + $index)\">" + link + "</a>";
        			} else {
        				output = om.get('funding.add.external_id.url.label.award') + ': <a href="' + link + '" class="' + ngclass + '"' + " target=\"_blank\" ng-mouseenter=\"showURLPopOver(funding.putCode.value + $index)\" ng-mouseleave=\"hideURLPopOver(funding.putCode.value + $index)\">" + link + "</a>";
        			}
        			
        		}        		
        	}
        } else if(value != null) {
        	output = output + " " + value;
        }
        output += '<div class="popover-pos">\
        				<div class="popover-help-container">\
				        	<div class="popover bottom" ng-class="{'+"'block'"+' : displayURLPopOver[funding.putCode.value + $index] == true}">\
								<div class="arrow"></div>\
								<div class="popover-content">\
							    	<a href="'+link+'" target="_blank" class="ng-binding">'+link+'</a>\
							    </div>\
							</div>\
						</div>\
				  </div>';
      
        
        //if (length > 1 && !last) output = output + ',';
        	return output;
    	};
}]);

orcidNgModule.filter('peerReviewExternalIdentifierHtml', function($filter){
    return function(peerReviewExternalIdentifier, first, last, length, moreInfo, own){
    	
    	
        var output = '';
        var ngclass = '';
        var isPartOf = false;
        var type = null;
        var link = null;
        ngclass = 'truncate';
        
        if (peerReviewExternalIdentifier == null) return output;
        
        if(peerReviewExternalIdentifier.relationship != null && peerReviewExternalIdentifier.relationship.value == 'part-of')
        	isPartOf = true;
        
        if (peerReviewExternalIdentifier.workExternalIdentifierId == null) return output;
        var id = peerReviewExternalIdentifier.workExternalIdentifierId.value;        
        
        if (peerReviewExternalIdentifier.workExternalIdentifierType != null)
            type = peerReviewExternalIdentifier.workExternalIdentifierType.value;
	        if (type != null) {
	        	if(isPartOf)
	        		output += "<span class='italic'>" + om.get("common.part_of") + " <span class='type'>" + type.toUpperCase() + "</span></span>: ";
	        	else 
	        		output += "<span class='type'>" + type.toUpperCase() + "</span>: ";
	        }
        
        if (peerReviewExternalIdentifier.url != null && peerReviewExternalIdentifier.url.value != '')
        	link = peerReviewExternalIdentifier.url.value;
        else link = workIdLinkJs.getLink(id,type); 
        	
        if (link != null){
        	link = $filter('urlProtocol')(link);
            output += '<a href="' + link.replace(/'/g, "&#39;") + '" class =""' + " target=\"_blank\" ng-mouseenter=\"showURLPopOver(peerReview.putCode.value + $index)\" ng-mouseleave=\"hideURLPopOver(peerReview.putCode.value + $index)\">" + id.escapeHtml() + '</a>' + ' | ' + '<a href="' + link.replace(/'/g, "&#39;") + '" class ="' + ngclass + '"' + " target=\"_blank\" ng-mouseenter=\"showURLPopOver(peerReview.putCode.value + $index)\" ng-mouseleave=\"hideURLPopOver(peerReview.putCode.value + $index)\">" + link.replace(/'/g, "&#39;") + '</a>';
        }else{
            output += id;        
        }
        
        if (length > 1 && !last) output = output + ',';
        
        output += '\
        <div class="popover-pos">\
			<div class="popover-help-container">\
	        	<div class="popover bottom" ng-class="{'+"'block'"+' : displayURLPopOver[peerReview.putCode.value + $index] == true}">\
					<div class="arrow"></div>\
					<div class="popover-content">\
				    	<a href="'+link+'" target="_blank">'+link+'</a>\
				    </div>\
				</div>\
			</div>\
	   </div>';
        
        if(own)
        	output = '<br/>' + output;
        
       return output;      
      
     
    };
});

function removeBadContributors(dw) {
    for (var idx in dw.contributors) {
        if (dw.contributors[idx].contributorSequence == null
            && dw.contributors[idx].email == null
            && dw.contributors[idx].orcid == null
            && dw.contributors[idx].creditName == null
            && dw.contributors[idx].contributorRole == null
            && dw.contributors[idx].creditNameVisibility == null) {
                dw.contributors.splice(idx,1);
            }
    }
}

function removeBadExternalIdentifiers(dw) {
    for(var idx in dw.workExternalIdentifiers) {
        if(dw.workExternalIdentifiers[idx].workExternalIdentifierType == null
            && dw.workExternalIdentifiers[idx].workExternalIdentifierId == null) {
            dw.workExternalIdentifiers.splice(idx,1);
        }
    }
}

function isEmail(email) {
    var re = /\S+@\S+\.\S+/;
    return re.test(email);
}

orcidNgModule.controller('EditTableCtrl', ['$scope', function ($scope) {

    // email edit row
    $scope.emailUpdateToggleText = function () {
        if ($scope.showEditEmail) $scope.emailToggleText = om.get("manage.editTable.hide");
        else $scope.emailToggleText = om.get("manage.editTable.edit");
    };

    $scope.toggleEmailEdit = function() {
        $scope.showEditEmail = !$scope.showEditEmail;
        $scope.emailUpdateToggleText();
    };
    
    $scope.openEmailEdit = function() {
        $scope.showEditEmail = true;
        $scope.emailUpdateToggleText();
        window.location.hash = "#editEmail"
    };

    // init email edit row
    $scope.showEditEmail = (window.location.hash === "#editEmail");
    $scope.emailUpdateToggleText();

    // password edit row
    $scope.passwordUpdateToggleText = function () {
        if ($scope.showEditPassword) $scope.passwordToggleText = om.get("manage.editTable.hide");
        else $scope.passwordToggleText = om.get("manage.editTable.edit");
    };

    $scope.togglePasswordEdit = function() {
        $scope.showEditPassword = !$scope.showEditPassword;
        $scope.passwordUpdateToggleText();
    };

    // init password row
    $scope.showEditPassword = (window.location.hash === "#editPassword");
    $scope.passwordUpdateToggleText();

    // deactivate edit row
    $scope.deactivateUpdateToggleText = function () {
        if ($scope.showEditDeactivate) $scope.deactivateToggleText = om.get("manage.editTable.hide");
        else $scope.deactivateToggleText = om.get("manage.editTable.deactivateRecord");
    };

    $scope.toggleDeactivateEdit = function() {
        $scope.showEditDeactivate = !$scope.showEditDeactivate;
        $scope.deactivateUpdateToggleText();
    };

    $scope.fixIE7zIndexes = function() {
        fixZindexIE7('tr', 999999);
        fixZindexIE7('#privacy-settings', 5000);
    };

    // init deactivate and Z-Indexes Fix
    $scope.showEditDeactivate = (window.location.hash === "#editDeactivate");
    $scope.deactivateUpdateToggleText();
    $scope.fixIE7zIndexes();

    // privacy preferences edit row
    $scope.privacyPreferencesUpdateToggleText = function () {
        if ($scope.showEditPrivacyPreferences) $scope.privacyPreferencesToggleText = om.get("manage.editTable.hide");
        else $scope.privacyPreferencesToggleText = om.get("manage.editTable.edit");
    };

    $scope.togglePrivacyPreferencesEdit = function() {
        $scope.showEditPrivacyPreferences = !$scope.showEditPrivacyPreferences;
        $scope.privacyPreferencesUpdateToggleText();
    };

    // init privacy preferences
    $scope.showEditPrivacyPreferences = (window.location.hash === "#editPrivacyPreferences");
    $scope.privacyPreferencesUpdateToggleText();

    // email preferences edit row
    $scope.emailPreferencesUpdateToggleText = function () {
        if ($scope.showEditEmailPreferences) $scope.emailPreferencesToggleText = om.get("manage.editTable.hide");
        else $scope.emailPreferencesToggleText = om.get("manage.editTable.edit");
    };

    $scope.toggleEmailPreferencesEdit = function() {
        $scope.showEditEmailPreferences = !$scope.showEditEmailPreferences;
        $scope.emailPreferencesUpdateToggleText();
    };
    
    // init email preferences
    $scope.showEditEmailPreferences = (window.location.hash === "#editEmailPreferences");
    $scope.emailPreferencesUpdateToggleText();
    
    // security question edit row
    $scope.securityQuestionUpdateToggleText = function () {
        if ($scope.showEditSecurityQuestion) $scope.securityQuestionToggleText = om.get("manage.editTable.hide");
        else $scope.securityQuestionToggleText = om.get("manage.editTable.edit");
    };

    $scope.toggleSecurityQuestionEdit = function() {
        $scope.showEditSecurityQuestion = !$scope.showEditSecurityQuestion;
        $scope.securityQuestionUpdateToggleText();
    };

    // init security question
    $scope.showEditSecurityQuestion = (window.location.hash === "#editSecurityQuestion");
    $scope.securityQuestionUpdateToggleText();

    /* Social Networks */

    $scope.socialNetworksUpdateToggleText = function () {
        if ($scope.showEditSocialSettings) $scope.socialNetworksToggleText = om.get("manage.socialNetworks.hide");
        else $scope.socialNetworksToggleText = om.get("manage.socialNetworks.edit");
    };

    $scope.toggleSocialNetworksEdit = function(){
        $scope.showEditSocialSettings = !$scope.showEditSocialSettings;
        $scope.socialNetworksUpdateToggleText();
    };   
    

    //init social networks row
    $scope.showEditSocialSettings = (window.location.hash === "#editSocialNetworks");
    $scope.socialNetworksUpdateToggleText();
}]);

orcidNgModule.controller('NotificationPreferencesCtrl',['$scope', '$compile', 'emailSrvc', 'prefsSrvc', 'emailSrvc',function ($scope, $compile, emailSrvc, prefsSrvc, emailSrvc) {
    $scope.prefsSrvc = prefsSrvc;
    $scope.emailSrvc = emailSrvc;
}]);

orcidNgModule.controller('EmailFrequencyCtrl',['$scope', '$compile', 'emailSrvc', 'prefsSrvc', function ($scope, $compile, emailSrvc, prefsSrvc) {
    $scope.prefsSrvc = prefsSrvc;
    $scope.emailSrvc = emailSrvc;
    
}]);

orcidNgModule.controller('EmailFrequencyLinkCtrl',['$scope','$rootScope', function ($scope, $rootScope) {
	$scope.getEmailFrequencies = function() {
		$.ajax({
            url: window.location.href + '/email-frequencies.json',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                $scope.emailFrequency = data;
                $rootScope.$apply();
            }
        }).fail(function() {
            console.log("error with frequency");
        });
	};
	
    $scope.saveEmailFrequencies = function() {
        $.ajax({
            url: window.location.href + '/email-frequencies.json',
            type: 'POST',
            data: angular.toJson($scope.emailFrequency),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.emailFrequency = data;
                $rootScope.$apply();
            }
        }).fail(function() {
            console.log("error with frequency");
        });
    };
    
    $scope.getEmailFrequencies();
}]);

orcidNgModule.controller('WorksPrivacyPreferencesCtrl',['$scope', 'prefsSrvc', function ($scope, prefsSrvc) {
    $scope.prefsSrvc = prefsSrvc;
    $scope.privacyHelp = {};
    $scope.showElement = {};

    $scope.toggleClickPrivacyHelp = function(key) {
        if (!document.documentElement.className.contains('no-touch'))
            $scope.privacyHelp[key]=!$scope.privacyHelp[key];
    };

    $scope.updateActivitiesVisibilityDefault = function(priv, $event) {
        $scope.prefsSrvc.prefs.activitiesVisibilityDefault.value = priv;
        $scope.prefsSrvc.savePrivacyPreferences();
    };
    
    $scope.showTooltip = function(el){
        $scope.showElement[el] = true;
    };
    
    $scope.hideTooltip = function(el){
        $scope.showElement[el] = false;
    };
}]);


orcidNgModule.controller('EmailPreferencesCtrl',['$scope', 'prefsSrvc', function ($scope, prefsSrvc) {
    $scope.prefsSrvc = prefsSrvc;
}]);


orcidNgModule.controller('DeactivateAccountCtrl', ['$scope', '$compile', function ($scope, $compile) {
    $scope.sendDeactivateEmail = function() {
        orcidGA.gaPush(['send', 'event', 'Disengagement', 'Deactivate_Initiate', 'Website']);
        $.ajax({
            url: getBaseUri() + '/account/send-deactivate-account.json',
            dataType: 'json',
            success: function(data) {
                $scope.primaryEmail = data.value;
                $.colorbox({
                    html : $compile($('#deactivate-account-modal').html())($scope)
                });
                $scope.$apply();
                $.colorbox.resize();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with change DeactivateAccount");
        });
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };
}]);


orcidNgModule.controller('SecurityQuestionEditCtrl', ['$scope', '$compile', function ($scope, $compile) {
    $scope.errors = null;
    $scope.password = null;
    $scope.securityQuestions = [];

    $scope.getSecurityQuestion = function() {
        $.ajax({
            url: getBaseUri() + '/account/security-question.json',
            dataType: 'json',
            success: function(data) {            	
                $scope.securityQuestionPojo = data;
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with security question.json");
        });
    };

    $scope.getSecurityQuestion();

    $scope.checkCredentials = function() {
        $scope.password=null;
        if(orcidVar.isPasswordConfirmationRequired){
            $.colorbox({
                html: $compile($('#check-password-modal').html())($scope)
            });
            $.colorbox.resize();
        }
        else{
            $scope.submitModal();
        }
    };

    $scope.submitModal = function() {
        $scope.securityQuestionPojo.password=$scope.password;
        $.ajax({
            url: getBaseUri() + '/account/security-question.json',
            type: 'POST',
            data: angular.toJson($scope.securityQuestionPojo),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
            	
                if(data.errors.length != 0) {
                    $scope.errors=data.errors;
                } else {
                    $scope.errors=null;
                }
                $scope.getSecurityQuestion();
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with security question");
        });
        $scope.password=null;
        $.colorbox.close();
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };
}]);


orcidNgModule.controller('PasswordEditCtrl', ['$scope', '$http', function ($scope, $http) {
    $scope.getChangePassword = function() {
        $.ajax({
            url: getBaseUri() + '/account/change-password.json',
            dataType: 'json',
            success: function(data) {
                $scope.changePasswordPojo = data;
                $scope.$apply();
                $scope.zIndexfixIE7();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with change password");
        });
    };

    $scope.getChangePassword();

    $scope.zIndexfixIE7 = function(){
        fixZindexIE7('#password-edit', 999999);
        fixZindexIE7('#password-edit .relative', 99999);
    };

    $scope.saveChangePassword = function() {
        $.ajax({
            url: getBaseUri() + '/account/change-password.json',
            type: 'POST',
            data: angular.toJson($scope.changePasswordPojo),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.changePasswordPojo = data;
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with edit password");
        });
    };
}]);

orcidNgModule.controller('EmailEditCtrl', ['$scope', '$compile', 'emailSrvc' ,function EmailEditCtrl($scope, $compile, emailSrvc) {
    $scope.emailSrvc = emailSrvc;
    $scope.privacyHelp = {};
    $scope.verifyEmailObject;
    $scope.showElement = {};
    $scope.isPassConfReq = orcidVar.isPasswordConfirmationRequired;
    $scope.notificationsEnabled = orcidVar.notificationsEnabled;
    $scope.baseUri = orcidVar.baseUri;
    $scope.showDeleteBox = false;
    $scope.showConfirmationBox = false;
    $scope.showEmailVerifBox = false;

    $scope.toggleClickPrivacyHelp = function(key) {
        if (!document.documentElement.className.contains('no-touch'))
            $scope.privacyHelp[key]=!$scope.privacyHelp[key];
    };

    $scope.getEmails = function() {
        emailSrvc.getEmails(function() {
                    if(isIE() == 7) $scope.fixZindexesIE7();
        });
    };

    //init
    $scope.password = null;
    $scope.curPrivToggle = null;
    emailSrvc.getEmails();
    emailSrvc.initInputEmail();

    $scope.fixZindexesIE7 =  function(){
        fixZindexIE7('.popover',2000);
        fixZindexIE7('.popover-help-container',3000);
        fixZindexIE7('#privacy-bar',500);
        fixZindexIE7('.emailVisibility',5000);
        fixZindexIE7('.col-md-3', 6000);
        fixZindexIE7('.row', 7000);
    };

    $scope.setPrivacy = function(email, priv, $event) {
        $event.preventDefault();
        email.visibility = priv;
        $scope.curPrivToggle = null;
        emailSrvc.saveEmail();
    };

    $scope.verifyEmail = function(email, popup) {
        
        $scope.verifyEmailObject = email;
        
        if(popup){
            emailSrvc.verifyEmail(email,function(data) {
                $scope.showEmailVerifBox = true;
                $scope.$apply();
                $.colorbox.resize();
           });    
        }else{
            emailSrvc.verifyEmail(email,function(data) {
                $.colorbox({
                    html : $compile($('#settings-verify-email-modal').html())($scope) 
                    //Name was changed to avoid conflicts with workspace verify email modal
                });
                $scope.$apply();
                $.colorbox.resize();
           });    
        }
        
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };
    
    $scope.closeDeleteBox = function(){
        $scope.showDeleteBox = false;
    }; 
    
    
    $scope.closeVerificationBox = function(){
        $scope.showEmailVerifBox = false;
    }


    $scope.submitModal = function (obj, $event) {
        emailSrvc.inputEmail.password = $scope.password;
        emailSrvc.addEmail();
        if(!$scope.emailSrvc.popUp){
            $.colorbox.close();    
        }
    };

    $scope.confirmDeleteEmail = function(email) {
        emailSrvc.delEmail = email;
        $.colorbox({
            html : $compile($('#delete-email-modal').html())($scope)
        });
        $.colorbox.resize();
    };
    
    $scope.confirmDeleteEmailInline = function(email, $event) {
        $event.preventDefault();
        $scope.showDeleteBox = true;
        emailSrvc.delEmail = email;        
    };

    $scope.deleteEmail = function () {
        emailSrvc.deleteEmail(function() {
            $scope.closeModal();
        });
    };
    
    $scope.deleteEmailInline = function () {
        emailSrvc.deleteEmail(function(){
            $scope.showDeleteBox = false;            
        });
    };

    $scope.checkCredentials = function(popup) {
        $scope.password = null;
        if(orcidVar.isPasswordConfirmationRequired){
            if (!popup){
                $.colorbox({
                    html: $compile($('#check-password-modal').html())($scope)
                });
                $.colorbox.resize();
            }else{
                $scope.showConfirmationBox = true;            
            }
        }else{
            $scope.submitModal();
        }
    };
    
    $scope.showTooltip = function(el){
    	$scope.showElement[el] = true;
    };
    
    $scope.hideTooltip = function(el){
    	$scope.showElement[el] = false;
    };
}]);

orcidNgModule.controller('WebsitesCtrl', ['$scope', '$compile', function WebsitesCtrl($scope, $compile) {
    $scope.showEdit = false;
    $scope.websitesForm = null;
    $scope.privacyHelp = false;
    $scope.showElement = {};
    $scope.defaultVisibility = null;
    $scope.newElementDefaultVisibility = null;
    $scope.orcidId = orcidVar.orcidId; //Do not remove
    
    $scope.openEdit = function() {
        $scope.addNew();
        $scope.showEdit = true;
    };

    $scope.close = function() {
        $scope.getWebsitesForm();
        $scope.showEdit = false;
    };

    $scope.addNew = function() {
        $scope.websitesForm.websites.push({ url: "", urlName: "" });
    };
    
    $scope.addNewModal = function() {
        var idx = $scope.getLastDisplayIndex();        
        var tmpObj = {"errors":[],"url":null,"urlName":null,"putCode":null,"visibility":{"errors":[],"required":true,"getRequiredMessage":null,"visibility":$scope.newElementDefaultVisibility},"source":null,"sourceName":null, "displayIndex": 0};
        tmpObj['displayIndex'] = idx + 1;
        $scope.websitesForm.websites.push(tmpObj);
        $scope.newInput = true; 
    };

    $scope.getWebsitesForm = function(){
        $.ajax({
            url: getBaseUri() + '/my-orcid/websitesForms.json',
            dataType: 'json',
            success: function(data) {
                $scope.websitesForm = data;
                $scope.newElementDefaultVisibility = $scope.websitesForm.visibility.visibility;
                var websites = $scope.websitesForm.websites;
                var len = websites.length;
                //Iterate over all elements to:
                // -> see if they have the same visibility, to set the default  visibility element
                // -> set the default protocol when needed
                if(len > 0) {
                	while (len--) {
                        if(websites[len].url != null) {
                            if (!websites[len].url.toLowerCase().startsWith('http')) {
                                websites[len].url = 'http://' + websites[len].url;
                            }                            
                        }     
                        
                        var itemVisibility = null;
                		if(websites[len].visibility != null && websites[len].visibility.visibility) {
                			itemVisibility = websites[len].visibility.visibility;
                		}
                		/**
                		 * The default visibility should be set only when all elements have the same visibility, so, we should follow this rules: 
                		 * 
                		 * Rules: 
                		 * - If the default visibility is null:
                		 * 	- If the item visibility is not null, set the default visibility to the item visibility
                		 * - If the default visibility is not null:
                		 * 	- If the default visibility is not equals to the item visibility, set the default visibility to null and stop iterating 
                		 * */
                		if($scope.defaultVisibility == null) {
                			if(itemVisibility != null) {
                				$scope.defaultVisibility = itemVisibility;
                			}                			
                		} else {
                			if(itemVisibility != null) {
                				if($scope.defaultVisibility != itemVisibility) {
                					$scope.defaultVisibility = null;
                    				break;
                				}
                			} else {
                				$scope.defaultVisibility = null;
                				break;
                			}
                		}                		
                        
                    }
                } else {
                	$scope.defaultVisibility = $scope.websitesForm.visibility.visibility;
                }
                
                
                $scope.$apply();
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching websites");
        });
    };

    $scope.deleteWebsite = function(website){
        var websites = $scope.websitesForm.websites;
        var websites = $scope.websitesForm.websites;
        var len = websites.length;
        while (len--) {
            if (websites[len] == website)
                websites.splice(len,1);
        }
    };

    $scope.setWebsitesForm = function(v2){        
        if(v2) {
        	$scope.websitesForm.visibility = null;
        } else {
        	//Set the default visibility to each of the elements
            if($scope.defaultVisibility != null) {
            	if($scope.websitesForm != null && $scope.websitesForm.websites != null) {
            		for(var i = 0; i < $scope.websitesForm.websites.length; i ++) {
            			if($scope.websitesForm.websites[i].visibility == null) {
            				$scope.websitesForm.websites[i].visibility = {"errors":[],"required":true,"getRequiredMessage":null,"visibility":"PUBLIC"};
            			}
            			$scope.websitesForm.websites[i].visibility.visibility = $scope.defaultVisibility; 
            		}
            	}
            }
        }            
        
        var websites = $scope.websitesForm.websites;
        var len = websites.length;
        while (len--) {
            if (websites[len].url == null || websites[len].url.trim() == '')
                websites.splice(len,1);
        }
        $.ajax({
            url: getBaseUri() + '/my-orcid/websitesForms.json',
            type: 'POST',
            data:  angular.toJson($scope.websitesForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.websitesForm = data;
                if(data.errors.length == 0) {
                    $scope.close();
                    $.colorbox.close();
                }                    
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("WebsiteCtrl.serverValidate() error");
        });
    };

    $scope.setPrivacy = function(priv, $event) {
        $event.preventDefault();
        $scope.defaultVisibility = priv;
    };
    
    $scope.setPrivacyModal = function(priv, $event, website) {        
        $event.preventDefault();
        
        var websites = $scope.websitesForm.websites;        
        var len = websites.length;
        
        while (len--) {
            if (websites[len] == website){
                websites[len].visibility.visibility = priv;
                $scope.websitesForm.websites = websites;
            }   
        }
    };
    
    $scope.showTooltip = function(elem){
    	$scope.showElement[elem] = true;
    }
    
    $scope.hideTooltip = function(elem){
    	$scope.showElement[elem] = false;
    }
    
    
    $scope.openEditModal = function(){        
        $.colorbox({
            scrolling: true,
            html: $compile($('#edit-websites').html())($scope),
            onLoad: function() {
                $('#cboxClose').remove();
                if ($scope.websitesForm.websites.length == 0){
                    $scope.addNewModal();
                }
                
            },
            width: formColorBoxResize(),
            onComplete: function() {
                    
            },
            onClosed: function() {
                $scope.getWebsitesForm();
            }            
        });
        $.colorbox.resize();
    }
    
    $scope.closeEditModal = function(){
        $.colorbox.close();
    }
    
    $scope.swap = function(idxA, valueA, idxB, valueB){        
        $scope.websitesForm.websites[idxA].displayIndex = valueB;
        $scope.websitesForm.websites[idxB].displayIndex = valueA;
    }    
    
    $scope.setPriorityUp = function(displayIndex){        
        var websites = $scope.websitesForm.websites;
        var len = websites.length;
        var current = 0;
        var valueB = 0;
        var idxB = 0;
        while (len--) {
            if (websites[len].displayIndex == displayIndex){
                var idxA = len;  
            }
            if (websites[len].displayIndex < displayIndex){
                current = websites[len].displayIndex;
                if (current > valueB){
                    valueB = current;
                    idxB = len;
                }
            }
        }
        $scope.swap(idxA, displayIndex, idxB, valueB);
    };
    
    $scope.setPriorityDown = function(displayIndex){
        var websites = $scope.websitesForm.websites;
        var len = websites.length;
        var current = 0;
        var valueB = $scope.getLastDisplayIndex();        
        var idxB = 0;        
        while (len--) {
            if (websites[len].displayIndex == displayIndex){
                var idxA = len;  
            }
            if (websites[len].displayIndex > displayIndex){
                current = websites[len].displayIndex;
                if (current <= valueB){
                    valueB = current;
                    idxB = len;
                }
            }
        }
        $scope.swap(idxA, displayIndex, idxB, valueB);
    };
    
    $scope.getLastDisplayIndex = function(){        
        var last = 0;
        var current = 0;
        
        var websites = $scope.websitesForm.websites;
        var len = websites.length;
        while (len--) {            
            current = websites[len].displayIndex;
            if (current > last){
                last = websites[len].displayIndex;
            }
        }       
        return last;
    };
    

    $scope.getWebsitesForm();
}]);

orcidNgModule.controller('KeywordsCtrl', ['$scope', '$compile', function ($scope, $compile) {
    $scope.showEdit = false;
    $scope.keywordsForm = null;
    $scope.privacyHelp = false;
    $scope.showElement = {};
    $scope.defaultVisibility = null;
    $scope.newElementDefaultVisibility = null;
    $scope.orcidId = orcidVar.orcidId; //Do not remove
    
    $scope.openEdit = function() {
        $scope.addNew();
        $scope.showEdit = true;
    };


    $scope.close = function() {
        $scope.getKeywordsForm();
        $scope.showEdit = false;
    };

    $scope.addNew = function() {
        $scope.keywordsForm.keywords.push({content: ""});
    };
    
    $scope.addNewModal = function() {        
        var idx = $scope.getLastDisplayIndex();
        var tmpObj = {"errors":[],"putCode":null,"content":"","visibility":{"errors":[],"required":true,"getRequiredMessage":null,"visibility":$scope.newElementDefaultVisibility},"displayIndex":0,"source":null,"sourceName":null};
        tmpObj['displayIndex'] = idx + 1;
        $scope.keywordsForm.keywords.push(tmpObj);
        $scope.newInput = true;
    };

    $scope.getKeywordsForm = function(){
        $.ajax({
            url: getBaseUri() + '/my-orcid/keywordsForms.json',
            dataType: 'json',
            success: function(data) {
                $scope.keywordsForm = data;
                $scope.newElementDefaultVisibility = $scope.keywordsForm.visibility.visibility;
                //If there is at least one element, iterate over them to see if they have the same visibility, to set the default  visibility element                
                if($scope.keywordsForm != null && $scope.keywordsForm.keywords != null && $scope.keywordsForm.keywords.length > 0) {
                	for(var i = 0; i < $scope.keywordsForm.keywords.length; i ++) {
                		var itemVisibility = null;
                		if($scope.keywordsForm.keywords[i].visibility != null && $scope.keywordsForm.keywords[i].visibility.visibility) {
                			itemVisibility = $scope.keywordsForm.keywords[i].visibility.visibility;
                		}
                		/**
                		 * The default visibility should be set only when all elements have the same visibility, so, we should follow this rules: 
                		 * 
                		 * Rules: 
                		 * - If the default visibility is null:
                		 * 	- If the item visibility is not null, set the default visibility to the item visibility
                		 * - If the default visibility is not null:
                		 * 	- If the default visibility is not equals to the item visibility, set the default visibility to null and stop iterating 
                		 * */
                		if($scope.defaultVisibility == null) {
                			if(itemVisibility != null) {
                				$scope.defaultVisibility = itemVisibility;
                			}                			
                		} else {
                			if(itemVisibility != null) {
                				if($scope.defaultVisibility != itemVisibility) {
                					$scope.defaultVisibility = null;
                    				break;
                				}
                			} else {
                				$scope.defaultVisibility = null;
                				break;
                			}
                		}                		
                    }
                } else {
                	$scope.defaultVisibility = $scope.keywordsForm.visibility.visibility;
                }
                                                                
                $scope.$apply();
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching keywords");
        });
    };

    $scope.deleteKeyword = function(keyword){
        var keywords = $scope.keywordsForm.keywords;
        var len = keywords.length;
        while (len--) {
            if (keywords[len] == keyword)
                keywords.splice(len,1);
        }
    };

    $scope.setKeywordsForm = function(v2){
        if (v2) {
        	$scope.keywordsForm.visibility = null;
        } else {
        	//Set the default visibility to each of the elements
            if($scope.defaultVisibility != null) {
            	if($scope.keywordsForm != null && $scope.keywordsForm.keywords != null) {
            		for(var i = 0; i < $scope.keywordsForm.keywords.length; i ++) {
            			if($scope.keywordsForm.keywords[i].visibility == null) {
            				$scope.keywordsForm.keywords[i].visibility = {"errors":[],"required":true,"getRequiredMessage":null,"visibility":"PUBLIC"};
            			}
            			$scope.keywordsForm.keywords[i].visibility.visibility = $scope.defaultVisibility; 
            		}
            	}
            }
        }
        
        $.ajax({
            url: getBaseUri() + '/my-orcid/keywordsForms.json',
            type: 'POST',
            data:  angular.toJson($scope.keywordsForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.keywordsForm = data;
                
                if(data.errors.length == 0){
                	$scope.close();
                    $.colorbox.close();
                }                   
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("KeywordsCtrl.serverValidate() error");
        });
    };

    $scope.setPrivacy = function(priv, $event) {
        $event.preventDefault();
        $scope.defaultVisibility = priv;
    };
    
    $scope.setPrivacyModal = function(priv, $event, keyword) {        
        $event.preventDefault();
        
        var keywords = $scope.keywordsForm.keywords;        
        var len = keywords.length;
        
        while (len--) {
            if (keywords[len] == keyword){
                keywords[len].visibility.visibility = priv;
                $scope.keywordsForm.keywords = keywords;
            }
        }
    };
    
    $scope.showTooltip = function(elem){
    	$scope.showElement[elem] = true;
    }
    
    $scope.hideTooltip = function(elem){
    	$scope.showElement[elem] = false;
    }
    
    $scope.openEditModal = function(){
        $.colorbox({
            scrolling: true,
            html: $compile($('#edit-keyword').html())($scope),
            onLoad: function() {
                $('#cboxClose').remove();
                if ($scope.keywordsForm.keywords.length == 0){
                    $scope.addNewModal();
                    $scope.newInput = true;
                }
            },
            width: formColorBoxResize(),
            onComplete: function() {
                    
            },
            onClosed: function() {
                $scope.getKeywordsForm();
            }            
        });
        $.colorbox.resize();
    }
    
    $scope.closeEditModal = function(){        
        $.colorbox.close();
    }
    
    $scope.swap = function(idxA, valueA, idxB, valueB){        
        $scope.keywordsForm.keywords[idxA].displayIndex = valueB;
        $scope.keywordsForm.keywords[idxB].displayIndex = valueA;
    }
    
    $scope.setPriorityUp = function(displayIndex){        
        var keywords = $scope.keywordsForm.keywords;
        var len = keywords.length;
        var current = 0;
        var valueB = 0;
        var idxB = 0;
        while (len--) {
            if (keywords[len].displayIndex == displayIndex){
                var idxA = len;  
            }
            if (keywords[len].displayIndex < displayIndex){
                current = keywords[len].displayIndex;
                if (current > valueB){
                    valueB = current;
                    idxB = len;
                }
            }
        }
        $scope.swap(idxA, displayIndex, idxB, valueB);
    }
    
    $scope.setPriorityDown = function(displayIndex){        
        var keywords = $scope.keywordsForm.keywords;
        var len = keywords.length;
        var current = 0;
        var valueB = $scope.getLastDisplayIndex();        
        var idxB = 0;        
        while (len--) {
            if (keywords[len].displayIndex == displayIndex){
                var idxA = len;  
            }
            if (keywords[len].displayIndex > displayIndex){
                current = keywords[len].displayIndex;
                if (current <= valueB){
                    valueB = current;
                    idxB = len;
                }
            }
        }
        $scope.swap(idxA, displayIndex, idxB, valueB);
    }
    
    $scope.getLastDisplayIndex = function(){        
        var last = 0;
        var current = 0;
        
        var keywords = $scope.keywordsForm.keywords;
        var len = keywords.length;
        while (len--) {            
            current = keywords[len].displayIndex;
            if (current > last){
                last = keywords[len].displayIndex;
            }
        }       
        return last;
    }    

    $scope.getKeywordsForm();
}]);

orcidNgModule.controller('NameCtrl', ['$scope', '$compile',function NameCtrl($scope, $compile) {
    $scope.showEdit = false;
    $scope.nameForm = null;
    $scope.privacyHelp = false;

    $scope.toggleEdit = function() {
        $scope.showEdit = !$scope.showEdit;
    };

    $scope.close = function() {
        $scope.getNameForm();
        $scope.showEdit = false;
    };

    $scope.getNameForm = function(){
        $.ajax({
            url: getBaseUri() + '/account/nameForm.json',
            dataType: 'json',
            success: function(data) {
                $scope.nameForm = data;
                $scope.$apply();
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching otherNames");
        });
    };

    $scope.setNameForm = function(){
        $.ajax({
            url: getBaseUri() + '/account/nameForm.json',
            type: 'POST',
            data:  angular.toJson($scope.nameForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.nameForm = data;
                if(data.errors.length == 0)
                   $scope.close();
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("OtherNames.serverValidate() error");
        });
    };

    $scope.setNamesVisibility = function(priv, $event) {
        $event.preventDefault();
        $scope.nameForm.namesVisibility.visibility = priv;
    };

    $scope.getNameForm();
}]);


orcidNgModule.controller('OtherNamesCtrl',['$scope', '$compile',function ($scope, $compile) {
    $scope.showEdit = false;
    $scope.otherNamesForm = null;
    $scope.privacyHelp = false;
    $scope.showElement = {};
    $scope.orcidId = orcidVar.orcidId; 
    $scope.defaultVisibility = null;
    $scope.newElementDefaultVisibility = null;
    
    $scope.openEdit = function() {
        $scope.addNew();
        $scope.showEdit = true;
    };

    $scope.close = function() {
        $scope.getOtherNamesForm();
        $scope.showEdit = false;
    };

    $scope.addNew = function() {
        $scope.otherNamesForm.otherNames.push({"errors":[],"content":"","putCode":null,"visibility":null});
    };
    
    $scope.addNewModal = function() {        
        var idx = $scope.getLastDisplayIndex();               
        var tmpObj = {"errors":[],"content":"","putCode":null,"visibility":{"errors":[],"required":true,"getRequiredMessage":null,"visibility":$scope.newElementDefaultVisibility},"displayIndex":1,"source":null,"sourceName":null};
        tmpObj['displayIndex'] = idx + 1;
        $scope.otherNamesForm.otherNames.push(tmpObj);        
        $scope.newInput = true;        
    };

    $scope.getOtherNamesForm = function(){
        $.ajax({
            url: getBaseUri() + '/my-orcid/otherNamesForms.json',
            dataType: 'json',
            success: function(data) {                
                $scope.otherNamesForm = data;   
                $scope.newElementDefaultVisibility = $scope.otherNamesForm.visibility.visibility;
                //If there is at least one element, iterate over them to see if they have the same visibility, to set the default  visibility element
                if($scope.otherNamesForm != null && $scope.otherNamesForm.otherNames != null && $scope.otherNamesForm.otherNames.length > 0) {
                	for(var i = 0; i < $scope.otherNamesForm.otherNames.length; i ++) {
                		var itemVisibility = null;
                		if($scope.otherNamesForm.otherNames[i].visibility != null && $scope.otherNamesForm.otherNames[i].visibility.visibility) {
                			itemVisibility = $scope.otherNamesForm.otherNames[i].visibility.visibility;
                		}
                		/**
                		 * The default visibility should be set only when all elements have the same visibility, so, we should follow this rules: 
                		 * 
                		 * Rules: 
                		 * - If the default visibility is null:
                		 * 	- If the item visibility is not null, set the default visibility to the item visibility
                		 * - If the default visibility is not null:
                		 * 	- If the default visibility is not equals to the item visibility, set the default visibility to null and stop iterating 
                		 * */
                		if($scope.defaultVisibility == null) {
                			if(itemVisibility != null) {
                				$scope.defaultVisibility = itemVisibility;
                			}                			
                		} else {
                			if(itemVisibility != null) {
                				if($scope.defaultVisibility != itemVisibility) {
                					$scope.defaultVisibility = null;
                    				break;
                				}
                			} else {
                				$scope.defaultVisibility = null;
                				break;
                			}
                		}                		
                    }
                } else {
                	$scope.defaultVisibility = $scope.otherNamesForm.visibility.visibility;
                }               
                
                $scope.$apply();                                
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching otherNames");
        });
    };

    $scope.deleteOtherName = function(otherName){
        var otherNames = $scope.otherNamesForm.otherNames;
        var len = otherNames.length;
        while (len--) {            
            if (otherNames[len] == otherName){                
                otherNames.splice(len,1);
            }
        }        
    };

    $scope.setOtherNamesForm = function(v2){       
    	//Remove once V2 API functionality is live
        if(v2) {
        	$scope.otherNamesForm.visibility = null;
        } else {
        	//Set the default visibility to each of the elements        	
            if($scope.defaultVisibility != null) {
            	if($scope.otherNamesForm != null && $scope.otherNamesForm.otherNames != null) {
            		for(var i = 0; i < $scope.otherNamesForm.otherNames.length; i ++) {
            			if($scope.otherNamesForm.otherNames[i].visibility == null) {
            				$scope.otherNamesForm.otherNames[i].visibility = {"errors":[],"required":true,"getRequiredMessage":null,"visibility":"PUBLIC"};
            			}
            			        			
            			$scope.otherNamesForm.otherNames[i].visibility.visibility = $scope.defaultVisibility; 
            		}
            	}
            }
        }        
        
        $.ajax({
            url: getBaseUri() + '/my-orcid/otherNamesForms.json',
            type: 'POST',
            data:  angular.toJson($scope.otherNamesForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {                
                $scope.otherNamesForm = data;
                if(data.errors.length == 0)
                    $scope.close();
                    $.colorbox.close();
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("OtherNames.serverValidate() error");
        });
    };
    
    $scope.showTooltip = function(elem){
    	$scope.showElement[elem] = true;
    };

    $scope.hideTooltip = function(elem){
    	$scope.showElement[elem] = false;	
    };

    $scope.setPrivacy = function(priv, $event) {
        $event.preventDefault();
        $scope.defaultVisibility = priv;
    };
    
    $scope.setPrivacyModal = function(priv, $event, otherName) {
        $event.preventDefault();
        var otherNames = $scope.otherNamesForm.otherNames;        
        var len = otherNames.length;
        
        while (len--) {
            if (otherNames[len] == otherName){
                otherNames[len].visibility.visibility = priv;
                $scope.otherNamesForm.otherNames = otherNames;
            }
        }
    };
    
    $scope.openEditModal = function(){                
        $.colorbox({
            scrolling: true,
            html: $compile($('#edit-aka').html())($scope),
            onLoad: function() {
                $('#cboxClose').remove();
                if ($scope.otherNamesForm.otherNames.length == 0){
                    $scope.addNewModal();
                    $scope.newInput = true;
                }    
            },
            width: formColorBoxResize(),
            onComplete: function() {
                    
            },
            onClosed: function() {
                $scope.getOtherNamesForm();
            }            
        });
        $.colorbox.resize();
    }
    
    $scope.closeEditModal = function(){        
        $.colorbox.close();
    }
    
    $scope.swap = function(idxA, valueA, idxB, valueB){        
       $scope.otherNamesForm.otherNames[idxA].displayIndex = valueB;
       $scope.otherNamesForm.otherNames[idxB].displayIndex = valueA;
    }    
    
    $scope.setPriorityUp = function(displayIndex){
        var otherNames = $scope.otherNamesForm.otherNames;
        var len = otherNames.length;
        var current = 0;
        var valueB = 0;
        var idxB = 0;
        while (len--) {
            if (otherNames[len].displayIndex == displayIndex){
                var idxA = len;  
            }
            if (otherNames[len].displayIndex < displayIndex){
                current = otherNames[len].displayIndex;
                if (current > valueB){
                    valueB = current;
                    idxB = len;
                }
            }
        }
        $scope.swap(idxA, displayIndex, idxB, valueB);
    }
    
    $scope.setPriorityDown = function(displayIndex){        
        var otherNames = $scope.otherNamesForm.otherNames;
        var len = otherNames.length;
        var current = 0;
        var valueB = $scope.getLastDisplayIndex();        
        var idxB = 0;        
        while (len--) {
            if (otherNames[len].displayIndex == displayIndex){
                var idxA = len;  
            }
            if (otherNames[len].displayIndex > displayIndex){
                current = otherNames[len].displayIndex;
                if (current <= valueB){
                    valueB = current;
                    idxB = len;
                }
            }
        }
        $scope.swap(idxA, displayIndex, idxB, valueB);
    }
    
    $scope.getLastDisplayIndex = function(){        
        var last = 0;
        var current = 0;
        
        var otherNames = $scope.otherNamesForm.otherNames;
        var len = otherNames.length;
        while (len--) {            
            current = otherNames[len].displayIndex;
            if (current > last){
                last = otherNames[len].displayIndex;
            }
        }       
        return last;
    }
    
    $scope.getOtherNamesForm();
}]);

orcidNgModule.controller('BiographyCtrl',['$scope', '$compile',function ($scope, $compile) {
    $scope.showEdit = false;
    $scope.biographyForm = null;
    $scope.lengthError = false;
    $scope.showElement = {};

    $scope.toggleEdit = function() {
        $scope.showEdit = !$scope.showEdit;
    };

    $scope.close = function() {
        $scope.showEdit = false;
    };

    $scope.cancel = function() {
        $scope.getBiographyForm();
        $scope.showEdit = false;
    };

    $scope.checkLength = function () {
        if ($scope.biographyForm != null)
            if ($scope.biographyForm.biography != null)
                if ($scope.biographyForm.biography.value != null)
                    if ($scope.biographyForm.biography.value.length > 5000) {
                        $scope.lengthError = true;
                    } else {
                        $scope.lengthError = false;
                    }
        return $scope.lengthError;
    };


    $scope.getBiographyForm = function(){
        $.ajax({
            url: getBaseUri() + '/account/biographyForm.json',
            dataType: 'json',
            success: function(data) {
                $scope.biographyForm = data;
                $scope.$apply();
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching BiographyForm");
        });
    };

    $scope.setBiographyForm = function(){
        if ($scope.checkLength()) return; // do nothing if there is a length error
        $.ajax({
            url: getBaseUri() + '/account/biographyForm.json',
            type: 'POST',
            data:  angular.toJson($scope.biographyForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.biographyForm = data;
                if(data.errors.length == 0)
                    $scope.close();
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("BiographyCtrl.serverValidate() error");
        });
    };

    $scope.setPrivacy = function(priv, $event) {
        $event.preventDefault();
        $scope.biographyForm.visiblity.visibility = priv;
        $scope.setBiographyForm();        
    };
    
    $scope.showTooltip = function(tp){
    	$scope.showElement[tp] = true;
    }
    
    $scope.hideTooltip = function(tp){
    	$scope.showElement[tp] = false;
    }


    $scope.getBiographyForm();

}]);

orcidNgModule.controller('CountryCtrl', ['$scope', '$compile',function ($scope, $compile) {
    $scope.showEdit = false;
    $scope.countryForm = null;
    $scope.privacyHelp = false;
    $scope.showElement = {};
    $scope.orcidId = orcidVar.orcidId;
    $scope.newInput = false;
    $scope.primary = true;
    $scope.defaultVisibility = null;
    $scope.newElementDefaultVisibility = null;
    
    $scope.openEdit = function() {
        $scope.showEdit = true;        
    };

    $scope.close = function() {
        $scope.showEdit = false;
    };

    $scope.getCountryForm = function(){
        $.ajax({
            url: getBaseUri() + '/account/countryForm.json',
            dataType: 'json',
            success: function(data) {
                $scope.countryForm = data;  
                $scope.newElementDefaultVisibility = $scope.countryForm.visibility.visibility;
                //If there is at least one element, iterate over them to see if they have the same visibility, to set the default  visibility element
                if($scope.countryForm != null && $scope.countryForm.addresses != null && $scope.countryForm.addresses.length > 0) {
                	for(var i = 0; i < $scope.countryForm.addresses.length; i ++) {
                		var itemVisibility = null;
                		if($scope.countryForm.addresses[i].visibility != null && $scope.countryForm.addresses[i].visibility.visibility) {
                			itemVisibility = $scope.countryForm.addresses[i].visibility.visibility;
                		}
                		/**
                		 * The default visibility should be set only when all elements have the same visibility, so, we should follow this rules: 
                		 * 
                		 * Rules: 
                		 * - If the default visibility is null:
                		 * 	- If the item visibility is not null, set the default visibility to the item visibility
                		 * - If the default visibility is not null:
                		 * 	- If the default visibility is not equals to the item visibility, set the default visibility to null and stop iterating 
                		 * */
                		if($scope.defaultVisibility == null) {
                			if(itemVisibility != null) {
                				$scope.defaultVisibility = itemVisibility;
                			}                			
                		} else {
                			if(itemVisibility != null) {
                				if($scope.defaultVisibility != itemVisibility) {
                					$scope.defaultVisibility = null;
                    				break;
                				}
                			} else {
                				$scope.defaultVisibility = null;
                				break;
                			}
                		}                		
                    }
                } else {
                	$scope.defaultVisibility = $scope.countryForm.visibility.visibility;
                }     
                $scope.$apply();                
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching external identifiers");
        });
    };

    $scope.toggleClickPrivacyHelp = function() {
        if (!document.documentElement.className.contains('no-touch'))
            $scope.privacyHelp=!$scope.privacyHelp;
    };

    $scope.setCountryForm = function(v2){        
        if(v2) {
        	$scope.countryForm.visibility = null;
        } else {
        	//Set the default visibility to each of the elements
            if($scope.defaultVisibility != null) {            	
            	if($scope.countryForm != null && $scope.countryForm.addresses != null) {
            		for(var i = 0; i < $scope.countryForm.addresses.length; i ++) {
            			if($scope.countryForm.addresses[i].visibility == null) {
            				$scope.countryForm.addresses[i].visibility = {"errors":[],"required":true,"getRequiredMessage":null,"visibility":"PUBLIC"};
            			}
            			        			
            			$scope.countryForm.addresses[i].visibility.visibility = $scope.defaultVisibility; 
            		}
            	}
            }
        }                    
        
        $.ajax({
            url: getBaseUri() + '/account/countryForm.json',
            type: 'POST',
            data:  angular.toJson($scope.countryForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.countryForm = data;
                if ($scope.countryForm.errors.length == 0){
                    $scope.close();
                    $scope.getCountryForm();                
                    $.colorbox.close();
                }else{
                    console.log($scope.countryForm.errors);
                }
                
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("CountryCtrl.serverValidate() error");
        });
    };
    
    $scope.closeModal = function(){
        $.colorbox.close();
    }

    $scope.setPrivacy = function(priv, $event) {
        $event.preventDefault();
        $scope.defaultVisibility = priv;
    };
    
    $scope.setPrivacyModal = function(priv, $event, country) {        
        $event.preventDefault();
        var countries = $scope.countryForm.addresses;        
        var len = countries.length;        
        while (len--) {
            if (countries[len] == country){            
                countries[len].visibility.visibility = priv;
                $scope.countryForm.addresses = countries;
            }
        }
    };
    
    $scope.showTooltip = function(elem){
    	$scope.showElement[elem] = true;
    }

    $scope.hideTooltip = function(elem){
    	$scope.showElement[elem] = false;	
    }
    
    $scope.openEditModal = function() {
        $.colorbox({
            scrolling: true,
            html: $compile($('#edit-country').html())($scope),
            onLoad: function() {
                $('#cboxClose').remove();
                if ($scope.countryForm.addresses.length == 0){
                    $scope.addNewModal();
                }
            },
 
            width: formColorBoxResize(),
            onComplete: function() {
                    
            },
            onClosed: function() {
                $scope.getCountryForm();
            }            
        });
        $.colorbox.resize();
    }
    
    $scope.closeEditModal = function(){
        $.colorbox.close();
    }
    
    $scope.deleteCountry = function(country){
        var countries = $scope.countryForm.addresses;
        var len = countries.length;
        while (len--) {
            if (countries[len] == country){
                countries.splice(len,1);
                $scope.countryForm.addresses = countries;
            }       
        }
    };
    
    $scope.addNewModal = function() {
        var tmpObj = {"errors":[],"iso2Country": null,"countryName":null,"putCode":null,"visibility":{"errors":[],"required":true,"getRequiredMessage":null,"visibility":$scope.newElementDefaultVisibility},"displayIndex":0,"source":null,"sourceName":null,"primary":false};
        var idx = $scope.getLastDisplayIndex();        
        tmpObj['displayIndex'] = idx + 1;
        $scope.countryForm.addresses.push(tmpObj);        
        $scope.newInput = true;
    };
    
    $scope.setPrimary = function(country){
        var countries = $scope.countryForm.addresses;
        
        var len = countries.length;
        
        while (len--) {
            if (countries[len] == country){
                countries[len].primary = true;
                $scope.countryForm.addresses = countries;
            }else{
                countries[len].primary = false;
            }
        }
    };
    
    $scope.swap = function(idxA, valueA, idxB, valueB){        
        $scope.countryForm.addresses[idxA].displayIndex = valueB;
        $scope.countryForm.addresses[idxB].displayIndex = valueA;
    };    
     
    $scope.setPriorityUp = function(displayIndex){
         var countries = $scope.countryForm.addresses;
         var len = countries.length;
         var current = 0;
         var valueB = 0;
         var idxB = 0;
         while (len--) {
             if (countries[len].displayIndex == displayIndex){
                 var idxA = len;  
             }
             if (countries[len].displayIndex < displayIndex){
                 current = countries[len].displayIndex;
                 if (current >= valueB){
                     valueB = current;                     
                     idxB = len;
                 }
             }
         }
         $scope.swap(idxA, displayIndex, idxB, valueB);
     };
     
     $scope.setPriorityDown = function(displayIndex){        
         var countries = $scope.countryForm.addresses;
         var len = countries.length;
         var current = 0;
         var valueB = $scope.getLastDisplayIndex();        
         var idxB = 0;        
         while (len--) {
             if (countries[len].displayIndex == displayIndex){
                 var idxA = len;  
             }
             if (countries[len].displayIndex > displayIndex){
                 current = countries[len].displayIndex;
                 if (current <= valueB){
                     valueB = current;
                     idxB = len;
                 }
             }
         }
         $scope.swap(idxA, displayIndex, idxB, valueB);
     };
     
     $scope.getLastDisplayIndex = function(){        
         var last = 0;
         var current = 0;
         var countries = $scope.countryForm.addresses;
         var len = countries.length;         
         while (len--) {            
             current = countries[len].displayIndex;
             if (current > last){
                 last = countries[len].displayIndex;
             }
         }         
         return last;
    };

    $scope.getCountryForm();

}]);


orcidNgModule.controller('ExternalIdentifierCtrl', ['$scope', '$compile', function ($scope, $compile){
    
	$scope.externalIdentifiersForm = null;
    $scope.orcidId = orcidVar.orcidId;
    $scope.primary = true;
    
    
    $scope.getExternalIdentifiersForm = function(){
        $.ajax({
            url: getBaseUri() + '/my-orcid/externalIdentifiers.json',
            dataType: 'json',
            success: function(data) {
                $scope.externalIdentifiersForm = data;
                $scope.displayIndexInit();
                $scope.$apply();
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching external identifiers");
        });
    }

    $scope.setExternalIdentifiersForm = function(v2){
        if(v2)
            $scope.externalIdentifiersForm.visibility = null;         
        
        $.ajax({
            url: getBaseUri() + '/my-orcid/externalIdentifiers.json',
            type: 'POST',
            data:  angular.toJson($scope.externalIdentifiersForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.externalIdentifiersForm = data;
                if ($scope.externalIdentifiersForm.errors.length == 0){                    
                    $scope.getExternalIdentifiersForm();                
                    $scope.closeEditModal();
                }else{
                    console.log($scope.externalIdentifiersForm.errors);
                }
                
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("ExternalIdentifierCtrl.serverValidate() error");
        });
    }
    
    
    $scope.setPrivacy = function(priv, $event) {
        $event.preventDefault();
        $scope.externalIdentifiersForm.visibility.visibility = priv;
    };
    
    $scope.setPrivacyModal = function(priv, $event, externalIdentifier) {        
        $event.preventDefault();        
        
        var externalIdentifiers = $scope.externalIdentifiersForm.externalIdentifiers;
        var len = externalIdentifiers.length;
        
        while (len--)
            if (externalIdentifiers[len] == externalIdentifier)            
                externalIdentifiers[len].visibility.visibility = priv;        
    };  
    
    
    $scope.openEditModal = function(){
        $.colorbox({
            scrolling: true,
            html: $compile($('#edit-external-identifiers').html())($scope),
            onLoad: function() {
                $('#cboxClose').remove();
            },
            width: formColorBoxResize(),
            onComplete: function() {

            },
            onClosed: function() {
                $scope.getExternalIdentifiersForm();
            }
        });
        $.colorbox.resize();
    }
    
    $scope.deleteExternalIdentifierConfirmation = function(idx){
        $scope.removeExternalIdentifierIndex = idx;
        $scope.removeExternalModalText = $scope.externalIdentifiersForm.externalIdentifiers[idx].reference;
        if ($scope.externalIdentifiersForm.externalIdentifiers[idx].commonName != null)
            $scope.removeExternalModalText = $scope.externalIdentifiersForm.externalIdentifiers[idx].commonName + ' ' + $scope.removeExternalModalText;
        $.colorbox({
            html: $compile($('#delete-external-id-modal').html())($scope)
        });
        $.colorbox.resize();
    }
    
    $scope.removeExternalIdentifier = function() {
        var externalIdentifier = $scope.externalIdentifiersForm.externalIdentifiers[$scope.removeExternalIdentifierIndex];
        $.ajax({
            url: getBaseUri() + '/my-orcid/externalIdentifiers.json',
            type: 'DELETE',
            data: angular.toJson(externalIdentifier),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data.errors.length != 0){
                    console.log("Unable to delete external identifier.");
                } else {
                    $scope.externalIdentifiersForm.externalIdentifiers.splice($scope.removeExternalIdentifierIndex, 1);
                    $scope.removeExternalIdentifierIndex = null;
                    $scope.$apply();
                }
            }
        }).fail(function() {
            console.log("Error deleting external identifier.");
        });
        $.colorbox.close();
    };
    
    //Person 2
    $scope.deleteExternalIdentifier = function(externalIdentifier){
        var externalIdentifiers = $scope.externalIdentifiersForm.externalIdentifiers;
        var len = externalIdentifiers.length;
        while (len--) {
            if (externalIdentifiers[len] == externalIdentifier){
                externalIdentifiers.splice(len,1);
                $scope.externalIdentifiersForm.externalIdentifiers = externalIdentifiers;
            }       
        }
    };
    
    $scope.swap = function(idxA, valueA, idxB, valueB){
        $scope.externalIdentifiersForm.externalIdentifiers[idxA].displayIndex = valueB;
        $scope.externalIdentifiersForm.externalIdentifiers[idxB].displayIndex = valueA;
    };
    
    
    $scope.setPriorityUp = function(displayIndex){
        var externalIdentifiers = $scope.externalIdentifiersForm.externalIdentifiers;
        var len = externalIdentifiers.length;
        var current = 0;
        var valueB = 0;
        var idxB = 0;
        while (len--) {
            if (externalIdentifiers[len].displayIndex == displayIndex){
                var idxA = len;
            }
            if (externalIdentifiers[len].displayIndex < displayIndex){
                current = externalIdentifiers[len].displayIndex;
                if (current > valueB){
                    valueB = current;
                    idxB = len;
                }
            }
        }
        $scope.swap(idxA, displayIndex, idxB, valueB);
    };
    
    $scope.setPriorityDown = function(displayIndex){

        var externalIdentifiers = $scope.externalIdentifiersForm.externalIdentifiers;
        
        var len = externalIdentifiers.length;
        
        var current = 0;
        var valueB = $scope.getLastDisplayIndex();
        var idxB = 0;
        while (len--) {
            if (externalIdentifiers[len].displayIndex == displayIndex){
                var idxA = len;
            }
            if (externalIdentifiers[len].displayIndex > displayIndex){
                current = externalIdentifiers[len].displayIndex;
                if (current <= valueB){
                    valueB = current;
                    idxB = len;
                }
            }
        }
        $scope.swap(idxA, displayIndex, idxB, valueB);
    };
    
    $scope.getLastDisplayIndex = function(){
        var last = 0;
        var current = 0;
        
        var externalIdentifiers = $scope.externalIdentifiersForm.externalIdentifiers;
        var len = externalIdentifiers.length;
        while (len--) {
            current = externalIdentifiers[len].displayIndex;
            if (current > last){
                last = externalIdentifiers[len].displayIndex;
            }
        }
        
        return last;
   };
   
   
   //To fix displayIndex values that comes with -1
   $scope.displayIndexInit = function(){
	   var externalIdentifiers = $scope.externalIdentifiersForm.externalIdentifiers;
	   var len = externalIdentifiers.length;
	   var displayIndex = 0;
	   var lastDisplayIndex = $scope.getLastDisplayIndex();
	   
	   if(lastDisplayIndex == -1)
		   displayIndex = 0;
	   else
		   displayIndex = lastDisplayIndex;
	   
       while (len--) {
    	    if (externalIdentifiers[len].displayIndex == -1){
    	    	displayIndex++;
            	externalIdentifiers[len].displayIndex = displayIndex;	
    	    }
       }
       $scope.externalIdentifiersForm.externalIdentifiers = externalIdentifiers;       
   }

    
   $scope.closeEditModal = function(){
	   $.colorbox.close();
   }
    
   //init
   $scope.getExternalIdentifiersForm();
   

}]);

orcidNgModule.controller('ResetPasswordCtrl', ['$scope', '$compile', 'commonSrvc',function ($scope, $compile, commonSrvc) {
    $scope.getResetPasswordForm = function(){
        $.ajax({
            url: getBaseUri() + '/password-reset.json',
            dataType: 'json',
            success: function(data) {
                $scope.resetPasswordForm = data;
                $scope.$apply();
            }
        }).fail(function(){
        // something bad is happening!
            console.log("error fetching password-reset.json");
        });
    };

    $scope.serverValidate = function () {
        $.ajax({
            url: getBaseUri() + '/reset-password-form-validate.json',
            type: 'POST',
            data:  angular.toJson($scope.resetPasswordForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                commonSrvc.copyErrorsLeft($scope.resetPasswordForm, data);
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("ResetPasswordCtrl.serverValidate() error");
        });
    };

    //init
    $scope.getResetPasswordForm();
}]);

orcidNgModule.controller('RegistrationCtrl', ['$scope', '$compile', 'commonSrvc', 'vcRecaptchaService', function ($scope, $compile, commonSrvc, vcRecaptchaService) {
	$scope.privacyHelp = {};
    $scope.recaptchaWidgetId = null;
    $scope.recatchaResponse = null;
    
    $scope.model = {
    	key: orcidVar.recaptchaKey
    };
    
    var loadDate = new Date();
    $scope.loadTime = loadDate.getTime();

    $scope.toggleClickPrivacyHelp = function(key) {
        if (!document.documentElement.className.contains('no-touch'))
            $scope.privacyHelp[key]=!$scope.privacyHelp[key];
    };

    $scope.getRegister = function(givenName, familyName, email, linkFlag){
        $.ajax({
            url: getBaseUri() + '/register.json',
            dataType: 'json',
            success: function(data) {
               $scope.register = data;
               $scope.register.givenNames.value=givenName;
               $scope.register.familyNames.value=familyName;
               $scope.register.email.value=email;
               $scope.register.linkType=linkFlag;
               $scope.$apply();

               if(email !== ''){
                   // Validate the email as soon as the user arrives at the screen, if it has been pre-populated.
                   $scope.serverValidate('Email');
               }
    
                // make sure inputs stayed trimmed
                $scope.$watch('register.email.value', function(newValue, oldValue) {
                    if(newValue !== oldValue) {
                        trimAjaxFormText($scope.register.email);
                        $scope.serverValidate('Email');
                    }
                }); // initialize the watch
    
                // make sure email is trimmed
                $scope.$watch('register.emailConfirm.value', function(newValue, oldValue) {
                    if(newValue !== oldValue){
                        trimAjaxFormText($scope.register.emailConfirm);
                        $scope.serverValidate('EmailConfirm');
                    }
                }); // initialize the watch
    
                $scope.$watch('register.givenNames.value', function() {
                    trimAjaxFormText($scope.register.givenNames);
                }); // initialize the watch
    
                $scope.$watch('register.familyNames.value', function() {
                     trimAjaxFormText($scope.register.familyNames);
                }); // initialize the watch
            }
        }).fail(function(){
        // something bad is happening!
            console.log("error fetching register.json");
        });
    };
    
    $scope.getDuplicates = function(){
        $.ajax({
            //url: getBaseUri() + 'dupicateResearcher.json?familyNames=test&givenNames=test',
            url: getBaseUri() + '/dupicateResearcher.json?familyNames=' + $scope.register.familyNames.value + '&givenNames=' + $scope.register.givenNames.value,
            dataType: 'json',
            success: function(data) {
                   $scope.duplicates = data;
                $scope.$apply();
            	var diffDate = new Date();
            	// reg was filled out to fast reload the page
            	if ($scope.loadTime + 5000 > diffDate.getTime()) {
            		window.location.reload();
            		return;
            	}
                if ($scope.duplicates.length > 0 ) {
                    $scope.showDuplicatesColorBox();
                } else {
                    $scope.postRegisterConfirm();
                }
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching dupicateResearcher.json");
            // continue to registration, as solr dup lookup failed.
            $scope.postRegisterConfirm();
        });
    };


    $scope.updateActivitiesVisibilityDefault = function(priv, $event) {
        $scope.register.activitiesVisibilityDefault.visibility = priv;
    };

    $scope.postRegister = function (linkFlag) {
        if (basePath.startsWith(baseUrl + 'oauth')) {
            var clientName = $('div#RegistrationCtr input[name="client_name"]').val();
            $scope.register.referredBy = $('div#RegistrationCtr input[name="client_id"]').val();
            var clientGroupName = $('div#RegistrationCtr input[name="client_group_name"]').val();
            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration-Submit' , 'OAuth ' + orcidGA.buildClientString(clientGroupName, clientName)]);
            $scope.register.creationType.value = "Member-referred";
        } else {
            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration-Submit', 'Website']);
            $scope.register.creationType.value = "Direct";
        }        
        
        $scope.register.grecaptcha.value = $scope.recatchaResponse; //Adding the response to the register object
        $scope.register.grecaptchaWidgetId.value = $scope.recaptchaWidgetId;
        console.log('link flag is : '+ linkFlag);
        $scope.register.linkType = linkFlag;
        $.ajax({
            url: getBaseUri() + '/register.json',
            type: 'POST',
            data:  angular.toJson($scope.register),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
            	console.log(data);
            	$scope.register = data;            	
            	$scope.$apply();                
            	if ($scope.register.errors == undefined || $scope.register.errors == undefined || $scope.register.errors.length == 0) {
                    if ($scope.register.errors.length == 0) {
                    	
                        $scope.showProcessingColorBox();
                        $scope.getDuplicates();
                    }
            	} else {
            		if ($scope.register.grecaptcha.errors.length == 0) angular.element(document.querySelector('#recaptcha')).remove();
                }
            }
        }).fail(function() {
            // something bad is happening!
            console.log("RegistrationCtrl.postRegister() error");
        });
    };

    $scope.postRegisterConfirm = function () {
        $scope.showProcessingColorBox();
        $scope.register.valNumClient = $scope.register.valNumServer / 2;
        var baseUri = getBaseUri();
        if($scope.register.linkType === 'shibboleth'){
            baseUri += '/shibboleth';
        }
        $.ajax({
            url: baseUri + '/registerConfirm.json',
            type: 'POST',
            data:  angular.toJson($scope.register),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if (basePath.startsWith(baseUrl + 'oauth')) {
                    var clientName = $('div#RegistrationCtr input[name="client_name"]').val();
                    var clientGroupName = $('div#RegistrationCtr input[name="client_group_name"]').val();
                    orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'OAuth '+ orcidGA.buildClientString(clientGroupName, clientName)]);
                }
                else
                    orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'Website']);
                orcidGA.windowLocationHrefDelay(data.url);
            }
        }).fail(function() {
            // something bad is happening!
            console.log("RegistrationCtrl.postRegister() error");
        });
    };

    $scope.serverValidate = function (field) {
        if (field === undefined) field = '';
        $.ajax({
            url: getBaseUri() + '/register' + field + 'Validate.json',
            type: 'POST',
            data:  angular.toJson($scope.register),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                commonSrvc.copyErrorsLeft($scope.register, data);
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("RegistrationCtrl.serverValidate() error");
        });
    };

    $scope.showProcessingColorBox = function () {
        $.colorbox({
            html : $('<div style="font-size: 50px; line-height: 60px; padding: 20px; text-align:center">' + om.get('common.processing') + '&nbsp;<i id="ajax-loader" class="glyphicon glyphicon-refresh spin green"></i></div>'),
            width: '400px',
            height:"100px",
            close: '',
            escKey:false,
            overlayClose:false,
            onComplete: function() {
                $.colorbox.resize({width:"400px" , height:"100px"});
            }
        });
    };

    $scope.showDuplicatesColorBox = function () {
        $.colorbox({
            html : $compile($('#duplicates').html())($scope),
            escKey:false,
            overlayClose:false,
            transition: 'fade',
            close: '',
            scrolling: true
                    });
        $scope.$apply();
        $.colorbox.resize({width:"780px" , height:"400px"});
    };

    $scope.hideProcessingColorBox = function () {
        $.colorbox.close();
    };

    $scope.isValidClass = function (cur) {
        if (cur === undefined) return '';
        var valid = true;
        if (cur.required && (cur.value == null || cur.value.trim() == '')) valid = false;
        if (cur.errors !== undefined && cur.errors.length > 0) valid = false;
        return valid ? '' : 'text-error';
    };
        
    $scope.setRecaptchaWidgetId = function (widgetId) {  
    	$scope.recaptchaWidgetId = widgetId;
    };

    $scope.setRecatchaResponse = function (response) {
        $scope.recatchaResponse = response;
    };
    //init
//    $scope.getRegister();
    //$scope.getDuplicates();

}]);


orcidNgModule.controller('ClaimCtrl', ['$scope', '$compile', 'commonSrvc', function ($scope, $compile, commonSrvc) {
    $scope.postingClaim = false;
    $scope.getClaim = function(){
        $.ajax({
            url: $scope.getClaimAjaxUrl(),
            dataType: 'json',
            success: function(data) {
               $scope.register = data;
            $scope.$apply();
            }
        }).fail(function(){
        // something bad is happening!
            console.log("error fetching register.json");
        });
    };

    $scope.postClaim = function () {
        if ($scope.postingClaim) return;
        $scope.postingClaim = true;
        $.ajax({
            url: $scope.getClaimAjaxUrl(),
            type: 'POST',
            data:  angular.toJson($scope.register),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.register = data;

                if ($scope.register.errors.length == 0) {
                    if ($scope.register.url != null) {
                        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'Website']);
                        orcidGA.windowLocationHrefDelay($scope.register.url);
                    }
                }
                $scope.postingClaim = false;
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("RegistrationCtrl.postRegister() error");
            $scope.postingClaim = false;
        });
    };

    $scope.getClaimAjaxUrl = function () {
        return window.location.href.split("?")[0]+".json";
    };

    $scope.updateActivitiesVisibilityDefault = function(priv, $event) {
        $scope.register.activitiesVisibilityDefault.visibility = priv;
    };

    $scope.serverValidate = function (field) {
        if (field === undefined) field = '';
        $.ajax({
            url: getBaseUri() + '/claim' + field + 'Validate.json',
            type: 'POST',
            data:  angular.toJson($scope.register),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                //alert(angular.toJson(data));
                commonSrvc.copyErrorsLeft($scope.register, data);
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("RegistrationCtrl.postRegisterValidate() error");
        });
    };

    $scope.isValidClass = function (cur) {
        if (cur === undefined) return '';
        var valid = true;
        if (cur.required && (cur.value == null || cur.value.trim() == '')) valid = false;
        if (cur.errors !== undefined && cur.errors.length > 0) valid = false;
        return valid ? '' : 'text-error';
    };

    //init
    $scope.getClaim();
}]);


orcidNgModule.controller('VerifyEmailCtrl', ['$scope', '$compile', 'emailSrvc', function ($scope, $compile, emailSrvc) {
    $scope.loading = true;
    $scope.getEmails = function() {
        $.ajax({
            url: getBaseUri() + '/account/emails.json',
            //type: 'POST',
            //data: $scope.emailsPojo,
            dataType: 'json',
            success: function(data) {
                $scope.emailsPojo = data;
                $scope.$apply();
                var primeVerified = false;
                for (i in $scope.emailsPojo.emails) {
                    if ($scope.emailsPojo.emails[i].primary) {
                        $scope.primaryEmail = $scope.emailsPojo.emails[i].value;
                        if ($scope.emailsPojo.emails[i].verified) primeVerified = true;
                    };
                };
                if (!primeVerified && !getBaseUri().contains("sandbox")) {
                    var colorboxHtml = $compile($('#verify-email-modal').html())($scope);
                    $scope.$apply();
                    $.colorbox({
                        html : colorboxHtml,
                        escKey:false,
                        overlayClose:false,
                        transition: 'fade',
                        close: '',
                        scrolling: false
                                });
                        $.colorbox.resize();
                };
                $scope.loading = false;
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with multi email");
        });
    };

    $scope.verifyEmail = function() {
        $.ajax({
            url: getBaseUri() + '/account/verifyEmail.json',
            type: 'get',
            data:  { "email": $scope.primaryEmail },
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                //alert( "Verification Email Send To: " + $scope.emailsPojo.emails[idx].value);
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with multi email");
        });
        var colorboxHtml = $compile($('#verify-email-modal-sent').html())($scope);

        $scope.emailSent = true;
        $.colorbox({
            html : colorboxHtml,
            escKey: true,
            overlayClose: true,
            transition: 'fade',
            close: '',
            scrolling: false
                    });
        $.colorbox.resize({height:"200px", width:"500px"});
    };

    $scope.closeColorBox = function() {
        $.ajax({
            url: getBaseUri() + '/account/delayVerifyEmail.json',
            type: 'get',
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                //alert( "Verification Email Send To: " + $scope.emailsPojo.emails[idx].value);
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with multi email");
        });
        $.colorbox.close();
    };

    $scope.emailSent = false;
    $scope.getEmails();
}]);

orcidNgModule.controller('ClaimThanks', ['$scope', '$compile', function ($scope, $compile) {
    $scope.showThanks = function () {
        var colorboxHtml;
            if ($scope.sourceGrantReadWizard.url == null)
                colorboxHtml = $compile($('#claimed-record-thanks').html())($scope);
            else
                colorboxHtml = $compile($('#claimed-record-thanks-source-grand-read').html())($scope);
        $.colorbox({
            html : colorboxHtml,
            escKey: true,
            overlayClose: true,
            transition: 'fade',
            close: '',
            scrolling: false
                    });
        $scope.$apply(); // this seems to make sure angular renders in the colorbox
        $.colorbox.resize();
    };

    $scope.getSourceGrantReadWizard = function(){
        $.ajax({
            url: getBaseUri() + '/my-orcid/sourceGrantReadWizard.json',
            dataType: 'json',
            success: function(data) {
                $scope.sourceGrantReadWizard = data;
                $scope.$apply();
                $scope.showThanks();
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching external identifiers");
        });

    };

    $scope.yes = function () {
        $.colorbox.close();
        var newWin = window.open($scope.sourceGrantReadWizard.url);
        if (!newWin) window.location.href = $scope.sourceGrantReadWizard.url;
        else newWin.focus();
    };

    $scope.close = function () {
        $.colorbox.close();
    };

    $scope.getSourceGrantReadWizard();
}]);

orcidNgModule.controller('PersonalInfoCtrl', ['$scope', '$compile', 'workspaceSrvc',function ($scope, $compile, workspaceSrvc){
    $scope.displayInfo = workspaceSrvc.displayPersonalInfo;
    $scope.toggleDisplayInfo = function () {
        $scope.displayInfo = !$scope.displayInfo;
    };
}]);

orcidNgModule.controller('WorkspaceSummaryCtrl', ['$scope', '$compile', 'affiliationsSrvc', 'fundingSrvc', 'worksSrvc', 'peerReviewSrvc', 'workspaceSrvc',function ($scope, $compile, affiliationsSrvc, fundingSrvc, worksSrvc, peerReviewSrvc, workspaceSrvc){
    $scope.workspaceSrvc = workspaceSrvc;
    $scope.worksSrvc = worksSrvc;
    $scope.affiliationsSrvc = affiliationsSrvc;
    $scope.fundingSrvc = fundingSrvc;
    $scope.peerReviewSrvc = peerReviewSrvc;
    $scope.showAddAlert = function () {
        if (worksSrvc.loading == false && affiliationsSrvc.loading == false && peerReviewSrvc.loading == false
                && worksSrvc.groups.length == 0
                && affiliationsSrvc.educations.length == 0
                && affiliationsSrvc.employments.length == 0
                && fundingSrvc.groups.length == 0
                && peerReviewSrvc.groups.lenght == 0)
            return true;
        return false;
    };
}]);

orcidNgModule.controller('PublicEduAffiliation', ['$scope', '$compile', '$filter', 'workspaceSrvc', 'affiliationsSrvc', function ($scope, $compile, $filter, workspaceSrvc , affiliationsSrvc){
    $scope.workspaceSrvc = workspaceSrvc;
    $scope.affiliationsSrvc = affiliationsSrvc;
    $scope.moreInfo = {};

    $scope.sortState = new ActSortState(GroupedActivities.AFFILIATION);
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

}]);

orcidNgModule.controller('PublicEmpAffiliation', ['$scope', '$compile', '$filter', 'workspaceSrvc', 'affiliationsSrvc', function ($scope, $compile, $filter, workspaceSrvc, affiliationsSrvc){
    $scope.workspaceSrvc = workspaceSrvc;
    $scope.affiliationsSrvc = affiliationsSrvc;
    $scope.moreInfo = {};

    $scope.sortState = new ActSortState(GroupedActivities.AFFILIATION);
    $scope.sort = function(key) {
        $scope.sortState.sortBy(key);
    };

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

    // remove once grouping is live
    $scope.showDetailsMouseClick = function(key, $event) {
        $event.stopPropagation();
        $scope.moreInfo[key]=!$scope.moreInfo[key];
    };

    $scope.closeMoreInfo = function(key) {
        $scope.moreInfo[key]=false;
    };

    affiliationsSrvc.setIdsToAdd(orcidVar.affiliationIdsJson);
    affiliationsSrvc.addAffiliationToScope(orcidVar.orcidId +'/affiliations.json');
}]);


orcidNgModule.controller('AffiliationCtrl', ['$scope', '$compile', '$filter', 'affiliationsSrvc', 'workspaceSrvc', 'commonSrvc', function ($scope, $compile, $filter, affiliationsSrvc, workspaceSrvc, commonSrvc){
    $scope.affiliationsSrvc = affiliationsSrvc;
    $scope.workspaceSrvc = workspaceSrvc;
    $scope.editAffiliation;
    $scope.privacyHelp = {};
    $scope.privacyHelpCurKey = null;
    $scope.moreInfo = {};
    $scope.moreInfoCurKey = null;
    $scope.showElement = {};

    $scope.sortState = new ActSortState(GroupedActivities.AFFILIATION);
    $scope.sort = function(key) {    	
        $scope.sortState.sortBy(key);
    };

    $scope.toggleClickPrivacyHelp = function(key) {
        if (!document.documentElement.className.contains('no-touch')) {
            if ($scope.privacyHelpCurKey != null
                    && $scope.privacyHelpCurKey != key) {
                $scope.privacyHelp[$scope.privacyHelpCurKey]=false;
            }
            $scope.privacyHelpCurKey = key;
            $scope.privacyHelp[key]=!$scope.privacyHelp[key];
        }

    };

    // remove once grouping is live
    $scope.toggleClickMoreInfo = function(key) {
        if (!document.documentElement.className.contains('no-touch')) {
            if ($scope.moreInfoCurKey != null
                    && $scope.moreInfoCurKey != key) {
                $scope.moreInfo[$scope.moreInfoCurKey]=false;
            }
            $scope.moreInfoCurKey = key;
            $scope.moreInfo[key]=!$scope.moreInfo[key];
        }
    };

    // remove once grouping is live
    $scope.moreInfoMouseEnter = function(key, $event) {
        $event.stopPropagation();
        if (document.documentElement.className.contains('no-touch')) {
            if ($scope.moreInfoCurKey != null
                    && $scope.moreInfoCurKey != key) {
                $scope.privacyHelp[$scope.moreInfoCurKey]=false;
            }
            $scope.moreInfoCurKey = key;
            $scope.moreInfo[key]=true;
        }
    };

    $scope.showDetailsMouseClick = function(key, $event) {
        $event.stopPropagation();
        $scope.moreInfo[key]=!$scope.moreInfo[key];
        /*
        if (document.documentElement.className.contains('no-touch')) {
            if ($scope.moreInfoCurKey != null
                    && $scope.moreInfoCurKey != key) {
                $scope.privacyHelp[$scope.moreInfoCurKey]=false;
            }
            $scope.moreInfoCurKey = key;
            $scope.moreInfo[key]=true;
        }
        */
    };

    $scope.closeMoreInfo = function(key) {
        $scope.moreInfo[key]=false;
    };


    $scope.showAddModal = function(){
        var numOfResults = 25;
        $.colorbox({
            html: $compile($('#add-affiliation-modal').html())($scope),            
            onComplete: function() {
                //resize to insure content fits
                formColorBoxResize();
                $scope.bindTypeahead();
            }
        });
    };

    $scope.bindTypeahead = function () {
        var numOfResults = 100;

        $("#affiliationName").typeahead({
            name: 'affiliationName',
            limit: numOfResults,
            remote: {
                url: getBaseUri()+'/affiliations/disambiguated/name/%QUERY?limit=' + numOfResults
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
        $("#affiliationName").bind("typeahead:selected", function(obj, datum) {
            $scope.selectAffiliation(datum);
            $scope.$apply();
        });
    };

    $scope.unbindTypeahead = function () {
        $('#affiliationName').typeahead('destroy');
    };

    $scope.selectAffiliation = function(datum) {
        if (datum != undefined && datum != null) {
            $scope.editAffiliation.affiliationName.value = datum.value;
            $scope.editAffiliation.city.value = datum.city;
            if(datum.city)
                $scope.editAffiliation.city.errors = [];
            $scope.editAffiliation.region.value = datum.region;
            if(datum.region)
                $scope.editAffiliation.region.errors = [];
            if(datum.country != undefined && datum.country != null) {
                $scope.editAffiliation.country.value = datum.country;
                $scope.editAffiliation.country.errors = [];
            }

            if (datum.disambiguatedAffiliationIdentifier != undefined && datum.disambiguatedAffiliationIdentifier != null) {
                $scope.getDisambiguatedAffiliation(datum.disambiguatedAffiliationIdentifier);
                $scope.unbindTypeahead();
            }
        }
    };

    $scope.getDisambiguatedAffiliation = function(id) {
        $.ajax({
            url: getBaseUri() + '/affiliations/disambiguated/id/' + id,
            dataType: 'json',
            type: 'GET',
            success: function(data) {
                if (data != null) {
                    $scope.disambiguatedAffiliation = data;
                    $scope.editAffiliation.orgDisambiguatedId.value = id;
                    $scope.editAffiliation.disambiguatedAffiliationSourceId = data.sourceId;
                    $scope.editAffiliation.disambiguationSource = data.sourceType;
                    $scope.$apply();
                }
            }
        }).fail(function(){
            console.log("error getDisambiguatedAffiliation(id)");
        });
    };

    $scope.removeDisambiguatedAffiliation = function() {
        $scope.bindTypeahead();
        if ($scope.disambiguatedAffiliation != undefined) delete $scope.disambiguatedAffiliation;
        if ($scope.editAffiliation != undefined && $scope.editAffiliation.disambiguatedAffiliationSourceId != undefined) delete $scope.editAffiliation.disambiguatedAffiliationSourceId;
        if ($scope.editAffiliation != undefined && $scope.editAffiliation.orgDisambiguatedId != undefined) delete $scope.editAffiliation.orgDisambiguatedId;
    };

    $scope.addAffiliationModal = function(type, affiliation){
        $scope.addAffType = type;
        if(affiliation === undefined) {
            $scope.removeDisambiguatedAffiliation();
            $.ajax({
                url: getBaseUri() + '/affiliations/affiliation.json',
                dataType: 'json',
                success: function(data) {
                    $scope.editAffiliation = data;
                    if (type != null)
                        $scope.editAffiliation.affiliationType.value = type;
                    $scope.$apply(function() {
                        $scope.showAddModal();
                    });
                }
            }).fail(function() {
                console.log("Error fetching affiliation: " + value);
            });
        } else {
            $scope.editAffiliation = affiliation;
            if($scope.editAffiliation.orgDisambiguatedId != null)
                $scope.getDisambiguatedAffiliation($scope.editAffiliation.orgDisambiguatedId.value);

            $scope.showAddModal();
        }
    };

    $scope.addAffiliation = function(){
        if ($scope.addingAffiliation) return; // don't process if adding affiliation
        $scope.addingAffiliation = true;
        $scope.editAffiliation.errors.length = 0;
        $.ajax({
            url: getBaseUri() + '/affiliations/affiliation.json',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            type: 'POST',
            data:  angular.toJson($scope.editAffiliation),
            success: function(data) {
                if (data.errors.length == 0){
                    $.colorbox.close();
                    $scope.addingAffiliation = false;
                    affiliationsSrvc.getAffiliations('affiliations/affiliationIds.json');
                } else {
                    $scope.editAffiliation = data;
                    commonSrvc.copyErrorsLeft($scope.editAffiliation, data);
                    $scope.addingAffiliation = false;
                    $scope.$apply();
                }
            }
        }).fail(function(){
            // something bad is happening!
            $scope.addingAffiliation = false;
            console.log("error adding affiliations");
        });
    };

    //For resizing color box in case of error
    $scope.$watch('addingAffiliation', function() {
         setTimeout(function(){
             $.colorbox.resize();;
         }, 50);
    });

    $scope.deleteAffiliation = function(aff) {
        $scope.deleAff = aff;

        if (aff.affiliationName && aff.affiliationName.value)
            $scope.fixedTitle = aff.affiliationName.value;
        else $scope.fixedTitle = '';
        var maxSize = 100;
        if($scope.fixedTitle.length > maxSize)
            $scope.fixedTitle = $scope.fixedTitle.substring(0, maxSize) + '...';
        $.colorbox({
            html : $compile($('#delete-affiliation-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
    };

    $scope.deleteAff = function(delAff) {
        affiliationsSrvc.deleteAffiliation(delAff);
        $.colorbox.close();
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };

    $scope.setAddAffiliationPrivacy = function(priv, $event) {
        $event.preventDefault();
        $scope.editAffiliation.visibility.visibility = priv;
    };

    $scope.setPrivacy = function(aff, priv, $event) {
        $event.preventDefault();
        aff.visibility.visibility = priv;
        affiliationsSrvc.updateProfileAffiliation(aff);
    };

    $scope.serverValidate = function (relativePath) {
        $.ajax({
            url: getBaseUri() + '/' + relativePath,
            type: 'POST',
            data:  angular.toJson($scope.editAffiliation),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                commonSrvc.copyErrorsLeft($scope.editAffiliation, data);
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("RegistrationCtrl.serverValidate() error");
        });
    };

    $scope.isValidClass = function (cur) {
        if (cur === undefined) return '';
        var valid = true;
        if (cur.required && (cur.value == null || cur.value.trim() == '')) valid = false;
        if (cur.errors !== undefined && cur.errors.length > 0) valid = false;
        return valid ? '' : 'text-error';
    };

    //init
    affiliationsSrvc.getAffiliations('affiliations/affiliationIds.json');

    $scope.openEditAffiliation = function(affiliation) {
        $scope.addAffiliationModal(affiliation.affiliationType.value, affiliation);
    };
    
    $scope.showTooltip = function (element){    	
        $scope.showElement[element] = true;
    };

    $scope.hideTooltip = function (element){    	
        $scope.showElement[element] = false;
    };
}]);

/**
 * Fundings Controller
 * */
orcidNgModule.controller('FundingCtrl',['$scope', '$compile', '$filter', 'fundingSrvc', 'workspaceSrvc', 'commonSrvc', function ($scope, $compile, $filter, fundingSrvc, workspaceSrvc, commonSrvc) {
    $scope.workspaceSrvc = workspaceSrvc;
    $scope.fundingSrvc = fundingSrvc;
    $scope.addingFunding = false;
    $scope.editFunding = null;
    $scope.disambiguatedFunding = null;
    $scope.moreInfo = {};
    $scope.editSources = {};
    $scope.privacyHelp = {};
    $scope.editTranslatedTitle = false;
    $scope.lastIndexedTerm = null;
    $scope.showElement = {};
    $scope.fundingImportWizard = false;
    $scope.wizardDescExpanded = {};
    $scope.displayURLPopOver = {};
    $scope.emptyExtId = {
            "errors": [],
            "type": {
                "errors": [],
                "value": "award",
                "required": true,
                "getRequiredMessage": null
            },
            "value": {
                "errors": [],
                "value": "",
                "required": true,
                "getRequiredMessage": null
            },
            "url": {
                "errors": [],
                "value": "",
                "required": true,
                "getRequiredMessage": null
            },
            "putCode": null
        };

    $scope.sortState = new ActSortState(GroupedActivities.FUNDING);
    $scope.sort = function(key) {
        $scope.sortState.sortBy(key);
    };

    // remove once grouping is live
    $scope.toggleClickMoreInfo = function(key) {
        if (!document.documentElement.className.contains('no-touch')) {
            if ($scope.moreInfoCurKey != null
                    && $scope.moreInfoCurKey != key) {
                $scope.moreInfo[$scope.moreInfoCurKey]=false;
            }
            $scope.moreInfoCurKey = key;
            $scope.moreInfo[key]=!$scope.moreInfo[key];
        }
    };

    $scope.hideSources = function(group) {
        $scope.editSources[group.groupId] = false;
        group.activePutCode = group.defaultPutCode;
    };

    $scope.showSources = function(group) {
        $scope.editSources[group.groupId] = true;
    };

    // remove once grouping is live
    $scope.moreInfoMouseEnter = function(key, $event) {
        $event.stopPropagation();
        if (document.documentElement.className.contains('no-touch')) {
            if ($scope.moreInfoCurKey != null
                    && $scope.moreInfoCurKey != key) {
                $scope.privacyHelp[$scope.moreInfoCurKey]=false;
            }
            $scope.moreInfoCurKey = key;
            $scope.moreInfo[key]=true;
        }
    };

    $scope.showDetailsMouseClick = function(key, $event) {
        $event.stopPropagation();
        $scope.moreInfo[key] = !$scope.moreInfo[key];        
    };

    $scope.closeMoreInfo = function(key) {
        $scope.moreInfo[key]=false;
    };

    $scope.addFundingModal = function(data){
        if(data == undefined) {
            $scope.removeDisambiguatedFunding();
            $.ajax({
                url: getBaseUri() + '/fundings/funding.json',
                dataType: 'json',
                success: function(data) {
                    $scope.$apply(function() {
                        $scope.editFunding = data;
                        $scope.showAddModal();
                    });
                }
            }).fail(function() {
                console.log("Error fetching funding: " + value);
            });
        } else {
            $scope.editFunding = data;
            if($scope.editFunding.externalIdentifiers == null || $scope.editFunding.externalIdentifiers.length == 0) {
                $scope.editFunding.externalIdentifiers.push($scope.emptyExtId);
            }
            $scope.showAddModal();
        }
    };

    $scope.showAddModal = function(){
        $scope.editTranslatedTitle = false;
        $.colorbox({
            html: $compile($('#add-funding-modal').html())($scope),
            width: formColorBoxResize(),
            onComplete: function() {
                //resize to insure content fits
                formColorBoxResize();
                $scope.bindTypeaheadForOrgs();
                $scope.bindTypeaheadForSubTypes();
            },
            onClosed: function() {
                $scope.closeAllMoreInfo();
                fundingSrvc.getFundings('fundings/fundingIds.json');
            }
        });
    };

    $scope.closeAllMoreInfo = function() {
        for (var idx in $scope.moreInfo)
            $scope.moreInfo[idx]=false;
    };

    $scope.putFunding = function(){
        if ($scope.addingFunding) return; // don't process if adding funding
        $scope.addingFunding = true;
        $scope.editFunding.errors.length = 0;
        $.ajax({
            url: getBaseUri() + '/fundings/funding.json',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            type: 'POST',
            data:  angular.toJson($scope.editFunding),
            success: function(data) {
                if (data.errors.length == 0){
                    $.colorbox.close();
                } else {
                    $scope.editFunding = data;
                    if($scope.editFunding.externalIdentifiers.length == 0) {
                        $scope.addFundingExternalIdentifier();
                    }
                    commonSrvc.copyErrorsLeft($scope.editFunding, data);
                }
                $scope.addingFunding = false;
                $scope.$apply();
            }
        }).fail(function(){
            // something bad is happening!
            $scope.addingFunding = false;
            console.log("error adding fundings");
        });
    };

    //Resizing window after error message is shown
    $scope.$watch('addingFunding', function() {
         setTimeout(function(){
             $.colorbox.resize();;
         }, 50);
     });

    $scope.showTemplateInModal = function(templateId) {
        $.colorbox({
            html : $compile($('#'+templateId).html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
    };

    $scope.openImportWizardUrl = function(url) {
        openImportWizardUrl(url);
    };

    $scope.bindTypeaheadForOrgs = function () {
        var numOfResults = 100;
        $("#fundingName").typeahead({
            name: 'fundingName',
            limit: numOfResults,
            remote: {
                replace: function () {
                    var q = getBaseUri()+'/fundings/disambiguated/name/';
                    if ($('#fundingName').val()) {
                        q += encodeURIComponent($('#fundingName').val());
                    }
                    q += '?limit=' + numOfResults + '&funders-only=true';
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
        $("#fundingName").bind("typeahead:selected", function(obj, datum) {
            $scope.selectFunding(datum);
            $scope.$apply();
        });
    };

    $scope.bindTypeaheadForSubTypes = function() {
        var numOfResults = 20;
        $("#organizationDefinedType").typeahead({
            name: 'organizationDefinedType',
            limit: numOfResults,
            remote: {
                replace: function () {
                    var q = getBaseUri()+'/fundings/orgDefinedSubType/';
                    if ($('#organizationDefinedType').val()) {
                        q += encodeURIComponent($('#organizationDefinedType').val());
                    }
                    q += '?limit=' + numOfResults;
                    return q;
                }
            },
            template: function (datum) {
                   var forDisplay =
                       '<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value + '</span><hr />';
                   return forDisplay;
            }
        });
        $("#organizationDefinedType").bind("typeahead:selected", function(obj, datum){
            $scope.selectOrgDefinedFundingSubType(datum);
            $scope.$apply();
        });
    };

    $scope.setSubTypeAsNotIndexed = function() {
        if($scope.lastIndexedTerm != $.trim($('#organizationDefinedType').val())) {
            console.log("value changed: " + $scope.lastIndexedTerm + " <-> " + $('#organizationDefinedType').val());
            $scope.editFunding.organizationDefinedFundingSubType.alreadyIndexed = false;
        }
    };

    $scope.selectOrgDefinedFundingSubType = function(subtype) {
        if (subtype != undefined && subtype != null) {
            $scope.editFunding.organizationDefinedFundingSubType.subtype.value = subtype.value;
            $scope.editFunding.organizationDefinedFundingSubType.alreadyIndexed = true;
            $scope.lastIndexedTerm = subtype.value;
            $scope.unbindTypeaheadForSubTypes();
        }
    };

    $scope.selectFunding = function(datum) {
        if (datum != undefined && datum != null) {
            $scope.editFunding.fundingName.value = datum.value;
            if(datum.value)
                $scope.editFunding.fundingName.errors = [];
            $scope.editFunding.city.value = datum.city;
            if(datum.city)
                $scope.editFunding.city.errors = [];
            $scope.editFunding.region.value = datum.region;

            if(datum.country != undefined && datum.country != null) {
                $scope.editFunding.country.value = datum.country;
                $scope.editFunding.country.errors = [];
            }

            if (datum.disambiguatedFundingIdentifier != undefined && datum.disambiguatedFundingIdentifier != null) {
                $scope.getDisambiguatedFunding(datum.disambiguatedFundingIdentifier);
                $scope.unbindTypeaheadForOrgs();
            }
        }
    };

    $scope.getDisambiguatedFunding = function(id) {
        $.ajax({
            url: getBaseUri() + '/fundings/disambiguated/id/' + id,
            dataType: 'json',
            type: 'GET',
            success: function(data) {
                if (data != null) {
                    $scope.disambiguatedFunding = data;
                    $scope.editFunding.disambiguatedFundingSourceId = data.sourceId;
                    $scope.editFunding.disambiguationSource = data.sourceType;
                    $scope.$apply();
                }
            }
        }).fail(function(){
            console.log("error getDisambiguatedFunding(id)");
        });
    };

    /*
    $scope.deleteFundingConfirm = function(funding) {
        $scope.delFunding = funding;

        $.colorbox({
            html : $compile($('#delete-funding-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
    };
    */

    $scope.deleteFundingConfirm = function(putCode, deleteGroup) {
        $scope.deletePutCode = putCode;
        $scope.deleteGroup = deleteGroup;
        var funding = fundingSrvc.getFunding(putCode);
        if (funding.fundingTitle && funding.fundingTitle.title)
            $scope.fixedTitle = funding.fundingTitle.title.value;
        else $scope.fixedTitle = '';
        var maxSize = 100;
        if($scope.fixedTitle.length > maxSize)
            $scope.fixedTitle = $scope.fixedTitle.substring(0, maxSize) + '...';
        $.colorbox({
            html : $compile($('#delete-funding-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
    };


    $scope.deleteFundingByPut = function(putCode, deleteGroup) {
        if (deleteGroup)
            fundingSrvc.deleteGroupFunding(putCode);
        else
            fundingSrvc.deleteFunding(putCode);
        $.colorbox.close();
    };

    //init
    fundingSrvc.getFundings('fundings/fundingIds.json');

    $scope.closeModal = function() {
        $.colorbox.close();
    };

    // Add privacy for new fundings
    $scope.setAddFundingPrivacy = function(priv, $event) {
        $event.preventDefault();
        $scope.editFunding.visibility.visibility = priv;
    };

    // Update privacy of an existing funding
    $scope.setPrivacy = function(funding, priv, $event) {
        $event.preventDefault();
        funding.visibility.visibility = priv;
        fundingSrvc.updateProfileFunding(funding);
    };

    $scope.removeDisambiguatedFunding = function() {
        $scope.bindTypeaheadForOrgs();
        if ($scope.disambiguatedFunding != undefined) delete $scope.disambiguatedFunding;
        if ($scope.editFunding != undefined && $scope.editFunding.disambiguatedFundingSourceId != undefined) delete $scope.editFunding.disambiguatedFundingSourceId;
    };

    $scope.isValidClass = function (cur) {
        if (cur === undefined) return '';
        var valid = true;
        if (cur.required && (cur.value == null || cur.value.trim() == '')) valid = false;
        if (cur.errors !== undefined && cur.errors.length > 0) valid = false;
        return valid ? '' : 'text-error';
    };

    // Server validations
    $scope.serverValidate = function (relativePath) {
        $.ajax({
            url: getBaseUri() + '/' + relativePath,
            type: 'POST',
            data:  angular.toJson($scope.editFunding),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                commonSrvc.copyErrorsLeft($scope.editFunding, data);
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("FundingCtrl.serverValidate() error");
        });
    };

    $scope.unbindTypeaheadForOrgs = function () {
        $('#fundingName').typeahead('destroy');
    };

    $scope.unbindTypeaheadForSubTypes = function () {
        $('#organizationDefinedType').typeahead('destroy');
    };

    $scope.addFundingExternalIdentifier = function () {
        $scope.editFunding.externalIdentifiers.push({type: {value: ""}, value: {value: ""}, url: {value: ""}, relationship: {value: "self"} });
    };

    $scope.deleteFundingExternalIdentifier = function(obj) {
        var index = $scope.editFunding.externalIdentifiers.indexOf(obj);
        $scope.editFunding.externalIdentifiers.splice(index,1);
    };

    $scope.toggleTranslatedTitleModal = function(){
        $scope.editTranslatedTitle = !$scope.editTranslatedTitle;
        $('#translatedTitle').toggle();
        $.colorbox.resize();
    };
    $scope.renderTranslatedTitleInfo = function(funding) {
        var info = null;
        if(funding != null && funding.fundingTitle != null && funding.fundingTitle.translatedTitle != null) {
            info = funding.fundingTitle.translatedTitle.content + ' - ' + funding.fundingTitle.translatedTitle.languageName;
        }
        return info;
    };

    $scope.typeChanged = function() {
        var selectedType = $scope.editFunding.fundingType.value;
        switch (selectedType){
        case 'award':
            $("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.award"));
            $("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.award"));
            $("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.award"));
            $("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.award"));
            $("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.award"));
            break;
        case 'contract':
            $("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.contract"));
            $("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.contract"));
            $("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.contract"));
            $("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.contract"));
            $("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.contract"));
            break;
        case 'grant':
            $("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.grant"));
            $("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.grant"));
            $("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.grant"));
            $("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.grant"));
            $("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.grant"));
            break;
        case 'salary-award':
            $("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.award"));
            $("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.award"));
            $("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.award"));
            $("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.award"));
            $("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.award"));
            break;
        default:
            $("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.grant"));
            $("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.grant"));
            $("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.grant"));
            $("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.grant"));
            $("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.grant"));
            break;
        }
    };

    $scope.openEditFunding = function(putCode) {
        fundingSrvc.getEditable(putCode, function(bestMatch) {
            $scope.addFundingModal(bestMatch);
        });
    };    
    
    $scope.showFundingImportWizard =  function() {
    	$scope.fundingImportWizard = !$scope.fundingImportWizard;    	    	
    	/*
        $.colorbox({
            html : $compile($('#import-funding-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
        */    	    	
    };
    
    $scope.toggleWizardDesc = function(id){
    	$scope.wizardDescExpanded[id] = !$scope.wizardDescExpanded[id];
    }
    
    $scope.showTooltip = function (key){
        $scope.showElement[key] = true;
    };

    $scope.hideTooltip = function (key){
        $scope.showElement[key] = false;
    };
    
    $scope.userIsSource = function(funding) {
        if (funding.source == orcidVar.orcidId)
            return true;
        return false;
    };
    
    $scope.hideURLPopOver = function(id){
    	$scope.displayURLPopOver[id] = false;
    };
    
    $scope.showURLPopOver = function(id){
    	$scope.displayURLPopOver[id] = true;
    };
    
    $scope.moreInfoActive = function(groupID){
    	if ($scope.moreInfo[groupID] == true || $scope.moreInfo[groupID] != null) return 'truncate-anchor';
    }
    
}]);

/**
 * Public Funding Controller
 * */
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

orcidNgModule.controller('PublicPeerReviewCtrl',['$scope', '$compile', '$filter', 'workspaceSrvc', 'peerReviewSrvc',function ($scope, $compile, $filter, workspaceSrvc, peerReviewSrvc) {
	 $scope.peerReviewSrvc = peerReviewSrvc;
	 $scope.workspaceSrvc  = workspaceSrvc;
	 $scope.showDetails = {};
	 $scope.showElement = {};
	 $scope.showPeerReviewDetails = new Array();
	 $scope.sortHideOption = true;
	 
	 $scope.sortState = new ActSortState(GroupedActivities.PEER_REVIEW);
     
	 $scope.sort = function(key) {
        $scope.sortState.sortBy(key);
     };
	 
     $scope.showDetailsMouseClick = function(groupId, $event){
     	$event.stopPropagation();
     	$scope.showDetails[groupId] = !$scope.showDetails[groupId];
     };
    
    $scope.showTooltip = function (element){    	
        $scope.showElement[element] = true;
    };

    $scope.hideTooltip = function (element){    	
        $scope.showElement[element] = false;
    };
    
    
    $scope.showMoreDetails = function(putCode){  
    	$scope.showPeerReviewDetails.length = 0;
    	$scope.showPeerReviewDetails[putCode] = true;   
    };
    
    $scope.hideMoreDetails = function(putCode){
    	$scope.showPeerReviewDetails.length = 0;
    	$scope.showPeerReviewDetails[putCode] = false;
    };
    
    //Init
    $scope.peerReviewSrvc.loadPeerReviews(peerReviewSrvc.constants.access_type.ANONYMOUS);	 	 
}]);

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

}]);

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

orcidNgModule.controller('WorkCtrl', ['$scope', '$compile', '$filter', 'worksSrvc', 'workspaceSrvc', 'actBulkSrvc', 'commonSrvc', '$timeout', '$q', 
                                      function ($scope, $compile, $filter, worksSrvc, workspaceSrvc, actBulkSrvc, commonSrvc, $timeout, $q) {
    actBulkSrvc.initScope($scope);
    $scope.canReadFiles = false;
    $scope.showBibtexImportWizard = false;
    $scope.textFiles = [];
    $scope.worksFromBibtex = null;
    $scope.workspaceSrvc = workspaceSrvc;
    $scope.worksSrvc = worksSrvc;
    $scope.showBibtex = {};
    $scope.editTranslatedTitle = false;
    $scope.types = null;
    $scope.privacyHelp = {};
    $scope.moreInfoOpen = false;
    $scope.moreInfo = {};
    $scope.editSources = {};
    $scope.bibtexParsingError = false;
    $scope.bibtextWork = false;
    $scope.bibtextWorkIndex = null;
    $scope.showElement = {};
    $scope.delCountVerify = 0;
    $scope.bulkDeleteCount = 0;
    $scope.bulkDeleteSubmit = false;
    $scope.workImportWizard = false;
    $scope.wizardDescExpanded = {};
    $scope.displayURLPopOver = {};
    $scope.workType = ['All'];
    $scope.geoArea = ['All'];
    $scope.badgesRequested = {};
    $scope.noLinkFlag = true;
    $scope.showBibtexExport = false;
    $scope.generatingBibtex = false;
    $scope.scriptsLoaded = false;
    $scope.bibtexGenerated = false;
    $scope.bibtexURL = "";
    $scope.bibtexExportError = false;
    $scope.bibtexURL = '';
    
    $scope.sortState = new ActSortState(GroupedActivities.ABBR_WORK);
    $scope.sort = function(key) {
        $scope.sortState.sortBy(key);
    };

    $scope.toggleBulkEdit = function() {
        if (!$scope.bulkEditShow) {
            $scope.bulkEditMap = {};
            $scope.bulkChecked = false;
            for (var idx in worksSrvc.groups)
                $scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value] = false;
        };
        $scope.bulkEditShow = !$scope.bulkEditShow;
        $scope.showBibtexImportWizard = false;
        $scope.workImportWizard = false;
        $scope.showBibtexExport = false;
    };

    $scope.bulkApply = function(func) {
        for (var idx in worksSrvc.groups)
            if ($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value])
                func(worksSrvc.groups[idx].getActive().putCode.value);
    };

    $scope.swapbulkChangeAll = function() {
        $scope.bulkChecked = !$scope.bulkChecked;
        for (var idx in worksSrvc.groups)
            $scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value] = $scope.bulkChecked;
        $scope.bulkDisplayToggle = false;
    };

    $scope.bulkChangeAll = function(bool) {
        $scope.bulkChecked = bool;
        $scope.bulkDisplayToggle = false;
        for (var idx in worksSrvc.groups)
            $scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value] = bool;
    };

    $scope.setBulkGroupPrivacy = function(priv) {
        var putCodes = new Array();
        for (var idx in worksSrvc.groups)
            if ($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value])
                for (var idj in worksSrvc.groups[idx].activities) {
                    putCodes.push(worksSrvc.groups[idx].activities[idj].putCode.value);
                    worksSrvc.groups[idx].activities[idj].visibility = priv;
                }
        worksSrvc.updateVisibility(putCodes, priv);
    };

    $scope.deleteBulk = function () {
        if ($scope.delCountVerify != parseInt($scope.bulkDeleteCount)) {
            $scope.bulkDeleteSubmit = true;
            return;
        }
        var delPuts = new Array();
        for (var idx in worksSrvc.groups)
            if ($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value])
                delPuts.push(worksSrvc.groups[idx].getActive().putCode.value);
        worksSrvc.deleteGroupWorks(delPuts);
        $.colorbox.close();
        $scope.bulkEditShow = false;
    };


    $scope.deleteBulkConfirm = function(idx) {
        $scope.delCountVerify = 0;
        $scope.bulkDeleteCount = 0;
        $scope.bulkDeleteSubmit = false;
        for (var idx in worksSrvc.groups)
            console.log($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value]);
        for (var idx in worksSrvc.groups)
            if ($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value])
                $scope.bulkDeleteCount++;

        $scope.bulkDeleteFunction = $scope.deleteBulk;

        $.colorbox({
            html: $compile($('#bulk-delete-modal').html())($scope)

        });
        $.colorbox.resize();
    };

    $scope.sortOtherLast = function(type) {
        if (type.key == 'other') return 'ZZZZZ';
        return type.value;
    };

    $scope.loadBibtexJs = function() {
        try {
            $scope.worksFromBibtex = new Array();
            $.each($scope.textFiles, function (index, bibtex) {
                var parsed = bibtexParse.toJSON(bibtex);
                if (parsed.length == 0) 
                    throw "bibtex parse return nothing";
                worksSrvc.getBlankWork(function(blankWork) {
                    var newWorks = new Array();
                    while (parsed.length > 0) {
                        var cur = parsed.shift();
                        var bibtexEntry = cur.entryType.toLowerCase();
                        if (bibtexEntry != 'preamble' && bibtexEntry != 'comment') //Filtering @PREAMBLE and @COMMENT
                            newWorks.push(populateWorkAjaxForm(cur,JSON.parse(JSON.stringify(blankWork))));
                    };
                    worksSrvc.worksValidate(newWorks, function(data) {
                        for (i in data)
                            $scope.worksFromBibtex.push(data[i]);
                        $scope.$apply();
                    });
                });
            });
            $scope.textFiles.length = 0;
            $scope.bibtexParsingError = false;
               
        } catch (err) {
            $scope.bibtexParsingError = true;
        };
    };

    $scope.rmWorkFromBibtex = function(work) {
        var index = $scope.worksFromBibtex.indexOf(work);
        $scope.worksFromBibtex.splice(index, 1);
    };

    $scope.editWorkFromBibtex = function(work) {
        $scope.bibtextWorkIndex = $scope.worksFromBibtex.indexOf(work);
        $scope.bibtextWork = true;
        $scope.addWorkModal($scope.worksFromBibtex[$scope.bibtextWorkIndex]);        
        
    };
    
    $scope.addWorkFromBibtex = function(work) {
        $scope.bibtextWorkIndex = $scope.worksFromBibtex.indexOf(work);    	
        $scope.editWork = $scope.worksFromBibtex[$scope.bibtextWorkIndex];
        $scope.bibtextWork = true;    	        
        $scope.putWork();        
    };
    
    $scope.saveAllFromBibtex = function(){
        var warksToSave =  new Array();
        angular.forEach($scope.worksFromBibtex, function( work, key ) {
            if (work.errors.length == 0) warksToSave.push(work);
        });
        var numToSave = warksToSave.length;
        angular.forEach( warksToSave, function( work, key ) {
            worksSrvc.putWork(work,function(data) {
                    index = $scope.worksFromBibtex.indexOf(work);
                    $scope.worksFromBibtex.splice(index, 1);
                    $scope.$apply();
                    numToSave--;
                    if (numToSave == 0)
                        $scope.worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.USER);
            });
        });
    };

    $scope.openBibTextWizard = function () {
        $scope.bibtexParsingError = false;
        $scope.showBibtexImportWizard = !($scope.showBibtexImportWizard);
        $scope.bulkEditShow = false;
        $scope.worksFromBibtex = null;
        $scope.workImportWizard = false;
        $scope.showBibtexExport = false;
    };

    $scope.bibtextCancel = function(){
        $scope.worksFromBibtex = null;
    };    

    // Check for the various File API support.
    if (window.File && window.FileReader && window.FileList && window.Blob) {
        $scope.canReadFiles = true;
    };

    $scope.toggleClickPrivacyHelp = function(key) {
        if (!document.documentElement.className.contains('no-touch'))
            $scope.privacyHelp[key]=!$scope.privacyHelp[key];
    };

    $scope.addExternalIdentifier = function () {
        $scope.editWork.workExternalIdentifiers.push({workExternalIdentifierId: {value: ""}, workExternalIdentifierType: {value: ""}, relationship: {value: "self"}, url: {value: ""}});
    };

    $scope.deleteExternalIdentifier = function(obj) {
        var index = $scope.editWork.workExternalIdentifiers.indexOf(obj);
        $scope.editWork.workExternalIdentifiers.splice(index,1);
    };

    $scope.deleteContributor = function(obj) {
        var index = $scope.editWork.contributors.indexOf(obj);
        $scope.editWork.contributors.splice(index,1);
    };

    $scope.userIsSource = function(work) {
        if (work.source == orcidVar.orcidId)
            return true;
        return false;
    };

    $scope.hasCombineableEIs = function(work) {
        if (work.workExternalIdentifiers != null)
            for (var idx in work.workExternalIdentifiers)
                if (work.workExternalIdentifiers[idx].workExternalIdentifierType.value != 'issn')
                    return true;
        return false;
    };

    $scope.canBeCombined = function(work) {
        if ($scope.userIsSource(work))
            return true;
        return $scope.hasCombineableEIs(work);
    };

    $scope.validCombineSel = function(selectedWork,work) {
        if ($scope.hasCombineableEIs(selectedWork))
            return $scope.userIsSource(work) || $scope.hasCombineableEIs(work);
        else
            return $scope.hasCombineableEIs(work);
    };

    $scope.combiningWorks = false;
    $scope.combined = function(work1, work2) {
        // no duplicate request;
        if ($scope.combiningWorks)
            return;
        $scope.combiningWorks = true;
        var putWork;
        if ($scope.userIsSource(work1)) {
            putWork = worksSrvc.copyEIs(work2, work1);
        } else if ($scope.userIsSource(work2)) {
            putWork = worksSrvc.copyEIs(work1, work2);
        } else {
            putWork = worksSrvc.createNew(work1);
            putWork = worksSrvc.copyEIs(work1, work2);
        }
        worksSrvc.putWork(
                putWork,
                function(data){
                    $scope.combiningWorks = false;
                    $scope.closeModal();
                },
                function() {
                    $scope.combiningWorks = false;
                }
            );
    };

    $scope.showCombineMatches = function(work1) {
        $scope.combineWork = work1;
        $.colorbox({
            scrolling: true,
            html: $compile($('#combine-work-template').html())($scope),
            onLoad: function() {$('#cboxClose').remove();},
            // start the colorbox off with the correct width
            width: formColorBoxResize(),
            onComplete: function() {$.colorbox.resize();},
            onClosed: function() {
                $scope.closeAllMoreInfo();
                $scope.worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.USER);
            }
        });
        return false;
    };

    $scope.showAddWorkModal = function(){
        $scope.editTranslatedTitle = false;
        $.colorbox({
            scrolling: true,
            html: $compile($('#add-work-modal').html())($scope),
            onLoad: function() {$('#cboxClose').remove();},
            // start the colorbox off with the correct width
            width: formColorBoxResize(),
            onComplete: function() {
                //resize to insure content fits
            },
            onClosed: function() {
                $scope.closeAllMoreInfo();
                $scope.worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.USER);
            }
        });
    };

    $scope.toggleTranslatedTitleModal = function(){
        $scope.editTranslatedTitle = !$scope.editTranslatedTitle;
        $('#translatedTitle').toggle();
        $.colorbox.resize();
    };

    $scope.bibtexShowToggle = function (putCode) {
        $scope.showBibtex[putCode] = !($scope.showBibtex[putCode]);
    };

    $scope.showWorkImportWizard =  function() {
    	if(!$scope.workImportWizard) {
    		loadWorkImportWizardList();
    	}
    	$scope.workImportWizard = !$scope.workImportWizard;
    };
    
    $scope.processWorkImportWizardList = function() {
    	$scope.workImportWizards = [];
    	for(var i = 0; i < $scope.workImportWizardsOriginal.length; i ++) {
    		for(var j = 0; j < $scope.workImportWizardsOriginal[i].redirectUris.redirectUri.length; j ++) {
    			if(($scope.selectedWorkType == 'All' || contains($scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].actType['import-works-wizard'], $scope.selectedWorkType))&&
    					($scope.selectedGeoArea == 'All' || contains($scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].geoArea['import-works-wizard'], $scope.selectedGeoArea))) {
    				$scope.workImportWizards.push($scope.workImportWizardsOriginal[i]);
    				break;
    			}
    		}
    	}
    	$scope.workImportWizards.sort(function(obj1, obj2){
    		if(obj1.displayName < obj2.displayName) {
    			return -1;
    		}
    		if(obj1.displayName > obj2.displayName) {
    			return 1;
    		}
    		return 0;
    	})
    }

    function loadWorkImportWizardList() {
    	$.ajax({
            url: getBaseUri() + '/workspace/retrieve-work-impor-wizards.json',
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
            	if(data == null || data.length == 0) {
                	$scope.noLinkFlag = false;
                }
            	
            	$scope.selectedWorkType = 'Articles';
                $scope.selectedGeoArea = 'Global';
            	$scope.workImportWizardsOriginal = data;
            	$scope.bulkEditShow = false;
            	$scope.showBibtexImportWizard = false;
            	for(var i = 0; i < $scope.workImportWizardsOriginal.length; i ++) {
            		for(var j = 0; j < $scope.workImportWizardsOriginal[i].redirectUris.redirectUri.length; j ++) {
            			$scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].actType =  JSON.parse($scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].actType);
            			$scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].geoArea =  JSON.parse($scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].geoArea);
            			for(var k = 0; k < $scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].actType['import-works-wizard'].length; k ++) {
            				if(!contains($scope.workType, $scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].actType['import-works-wizard'][k]))
            					$scope.workType.push($scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].actType['import-works-wizard'][k]);
            			}
            			
            			for(var k = 0; k < $scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].geoArea['import-works-wizard'].length; k ++) {
            				if(!contains($scope.geoArea, $scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].geoArea['import-works-wizard'][k]))
            					$scope.geoArea.push($scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].geoArea['import-works-wizard'][k]);
            			}
            		}
            	}
            	if(getParameterByName('import_works_wizard') != 'true') {
            		$scope.selectedWorkType = 'All';
                    $scope.selectedGeoArea = 'All';
            	}
            	$scope.processWorkImportWizardList();
            	$scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("WorkImportWizardError");
        });
    }
    
    $scope.addWorkModal = function(data){
        if (data == undefined) {
            worksSrvc.getBlankWork(function(data) {
                $scope.editWork = data;
                $scope.$apply(function() {
                    $scope.loadWorkTypes();
                    $scope.showAddWorkModal();
                });
            });
        } else {
            $scope.editWork = data;            
            $scope.loadWorkTypes();
            $scope.showAddWorkModal();
        }

    };

    $scope.openEditWork = function(putCode){
    	worksSrvc.getEditable(putCode, function(data) {$scope.addWorkModal(data);});
    };       

    $scope.putWork = function(){
        if ($scope.addingWork) return; // don't process if adding work
        $scope.addingWork = true;
        $scope.editWork.errors.length = 0;
        worksSrvc.putWork($scope.editWork,
            function(data){        	    
                if (data.errors.length == 0) {
                	if ($scope.bibtextWork == false){
                		$.colorbox.close();
                		$scope.addingWork = false;
                	} else {
                        $scope.worksFromBibtex.splice($scope.bibtextWorkIndex, 1);
                        $scope.bibtextWork = false;
                        $scope.addingWork = false;
                        $scope.$apply();
                        $.colorbox.close();
                        $scope.worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.USER);
                    }
                } else {
                    $scope.editWork = data;                    
                    commonSrvc.copyErrorsLeft($scope.editWork, data);
                    
                    $scope.addExternalIdentifier();
                    
                    $scope.addingWork = false;
                    $scope.$apply();
                    // make sure colorbox is shown if there are errors
                    if (!($("#colorbox").css("display")=="block"))
                        $scope.addWorkModal(data);
                }
            },
            function() {
                // something bad is happening!
                $scope.addingWork = false;
                console.log("error fetching works");
            }
        );
    };

    $scope.closeAllMoreInfo = function() {
        for (var idx in $scope.moreInfo)
            $scope.moreInfo[idx]=false;
    };

    $scope.validateCitation = function() {
        if ($scope.editWork.citation
                && $scope.editWork.citation.citation.value
                && $scope.editWork.citation.citation.value.length > 0
                && $scope.editWork.citation.citationType.value == 'bibtex') {
            try {
                var parsed = bibtexParse.toJSON($scope.editWork.citation.citation.value);
                if (parsed.length == 0) throw "bibtex parse return nothing";
                var index = $scope.editWork.citation.citation.errors.indexOf(om.get('manualWork.bibtext.notValid'));
                if (index > -1) {
                    $scope.editWork.citation.citation.errors.splice(index, 1);
                }
            } catch (err) {
                $scope.editWork.citation.citation.errors.push(om.get('manualWork.bibtext.notValid'));
            };
        };
    };

    $scope.renderTranslatedTitleInfo = function(putCode) {
        var info = null;

        if(putCode != null && $scope.worksSrvc.details[putCode] != null && $scope.worksSrvc.details[putCode].translatedTitle != null) {
            info = $scope.worksSrvc.details[putCode].translatedTitle.content + ' - ' + $scope.worksSrvc.details[putCode].translatedTitle.languageName;
        }

        return info;
    };

    $scope.loadWorkTypes = function(){
        var workCategory = "";
        $scope.types = null;
        if($scope.editWork != null && $scope.editWork.workCategory != null && $scope.editWork.workCategory.value != null && $scope.editWork.workCategory.value != "")
            workCategory = $scope.editWork.workCategory.value;
        else
            return; //do nothing if we have not types
        $.ajax({
            url: getBaseUri() + '/works/loadWorkTypes.json?workCategory=' + workCategory,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.$apply(function() {
                    $scope.types = data;
                    if($scope.editWork != null && $scope.editWork.workCategory != null) {
                        // if the edit works doesn't have a value that matches types
                        var hasType = false;
                        for (var idx in $scope.types)
                            if ($scope.types[idx].key == $scope.editWork.workType.value) hasType = true;
                        if(!hasType) {
                            switch ($scope.editWork.workCategory.value){
                            case "conference":
                                $scope.editWork.workType.value="conference-paper";
                                break;
                            case "intellectual_property":
                                $scope.editWork.workType.value="patent";
                                break;
                            case "other_output":
                                $scope.editWork.workType.value="data-set";
                                break;
                            case "publication":
                                $scope.editWork.workType.value="journal-article";
                                break;
                            }
                        }
                    }
                });

            }
        }).fail(function() {
            console.log("Error loading work types.");
        });

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
    
    //init
    $scope.worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.USER);
    loadWorkImportWizardList();

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
        $scope.moreInfo[group.groupId] = !$scope.moreInfo[group.groupId];
        for (var idx in group.activities)
            $scope.loadDetails(group.activities[idx].putCode.value, $event);
    };

    $scope.hideSources = function(group) {
        $scope.editSources[group.groupId] = false;
        group.activePutCode = group.defaultPutCode;
    };

    $scope.showSources = function(group) {
        $scope.editSources[group.groupId] = true;
    };

    $scope.loadDetails = function(putCode, event) {
        //Close any open popover
        $scope.closePopover(event);
        $scope.moreInfoOpen = true;
        //Display the popover
        $(event.target).next().css('display','inline');
        $scope.worksSrvc.getGroupDetails(putCode, worksSrvc.constants.access_type.USER);
    };


    $scope.loadWorkInfo = function(putCode, event) {
        //Close any open popover
        $scope.closePopover(event);
        $scope.moreInfoOpen = true;
        //Display the popover
        $(event.target).next().css('display','inline');
        if($scope.worksSrvc.details[putCode] == null) {
            $scope.worksSrvc.getGroupDetails(putCode, worksSrvc.constants.access_type.USER);
        } else {
            $(event.target).next().css('display','inline');
        }
    };

    $scope.closePopover = function(event) {
        $scope.moreInfoOpen = false;
        $('.work-more-info-container').css('display', 'none');
    };

    $scope.deleteWorkConfirm = function(putCode, deleteGroup) {
        $scope.deletePutCode = putCode;
        $scope.deleteGroup = deleteGroup;
        var work = worksSrvc.getWork(putCode);
        if (work.title)
            $scope.fixedTitle = work.title.value;
        else $scope.fixedTitle = '';
        var maxSize = 100;
        if($scope.fixedTitle.length > maxSize)
            $scope.fixedTitle = $scope.fixedTitle.substring(0, maxSize) + '...';
        $.colorbox({
            html : $compile($('#delete-work-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
    };

    $scope.deleteByPutCode = function(putCode, deleteGroup) {
        if (deleteGroup)
           worksSrvc.deleteGroupWorks(putCode);
        else
           worksSrvc.deleteWork(putCode);
        $.colorbox.close();
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };

    $scope.openImportWizardUrl = function(url) {
        openImportWizardUrl(url);
    };
    
    $scope.openImportWizardUrlFilter = function(url, param) {
    	url = url + '?client_id='+param.clientId+'&response_type=code&scope='+param.redirectUris.redirectUri[0].scopeAsSingleString+'&redirect_uri='+param.redirectUris.redirectUri[0].value;
    	openImportWizardUrl(url);
    };

    $scope.setAddWorkPrivacy = function(priv, $event) {
        $event.preventDefault();
        $scope.editWork.visibility = priv;
    };

    $scope.serverValidate = function (relativePath) {
        $.ajax({
            url: getBaseUri() + '/' + relativePath,
            type: 'POST',
            data:  angular.toJson($scope.editWork),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                commonSrvc.copyErrorsLeft($scope.editWork, data);
                if ( relativePath == 'works/work/citationValidate.json') {
                    $scope.validateCitation();
                }
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("WorkCtrl.serverValidate() error");
        });
    };

    $scope.isValidClass = function (cur) {
        if (cur === undefined || cur == null) return '';
        var valid = true;
        if (cur.required && (cur.value == null || cur.value.trim() == '')) valid = false;
        if (cur.errors !== undefined && cur.errors.length > 0) valid = false;
        return valid ? '' : 'text-error';
    };

    $scope.clearErrors = function() {
        $scope.editWork.workCategory.errors = [];
        $scope.editWork.workType.errors = [];
    };
    
    $scope.showTooltip = function (key){    	
        $scope.showElement[key] = true;    	
    };
    
    $scope.hideTooltip = function (key){    	
    	$scope.showElement[key] = false;
    };
    
    $scope.openFileDialog = function(){    	
    	$timeout(function() { //To avoid '$apply already in progress' error
    	    angular.element('#inputBibtex').trigger('click');
    	}, 0);
    };
    
    $scope.toggleWizardDesc = function(id){
    	$scope.wizardDescExpanded[id] = !$scope.wizardDescExpanded[id];
    };
    
    $scope.showURLPopOver = function(id){    	
    	$scope.displayURLPopOver[id] = true;
    }
    
    $scope.hideURLPopOver = function(id){    	
    	$scope.displayURLPopOver[id] = false;
    }
    
    $scope.showMozillaBadges = function(putCode){
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
    
    $scope.toggleBibtexExport = function(){
        $scope.bibtexParsingError = false;
        $scope.showBibtexImportWizard = false;
        $scope.bulkEditShow = false;        
        $scope.workImportWizard = false;
        $scope.showBibtexExport  = !$scope.showBibtexExport;
        $scope.bibtexExportError = false;
        $scope.bibtexGenerated = false;
        $scope.loadingScripts = false;
        $scope.scriptsLoaded = false;
    }
    
    $scope.openBibtexExportDialog = function(){
        
        $scope.loadingScripts = true;
        $scope.bibtexExportError = false; 
        $scope.scriptsLoaded = false;
        
        var swagger  = orcidVar.baseUri + "/static/javascript/orcid-js/swagger-js/browser/swagger-client.min.js";
        var xmle4x   = orcidVar.baseUri + "/static/javascript/orcid-js/citeproc-js/xmle4x.js";                
        var xmldom   = orcidVar.baseUri + "/static/javascript/orcid-js/citeproc-js/xmldom.js";
        var citeproc = orcidVar.baseUri + "/static/javascript/orcid-js/citeproc-js/citeproc.js";
        var orcidx   = orcidVar.baseUri + "/static/javascript/orcid-js/lib/orcid.js";
        var styles   = orcidVar.baseUri + "/static/javascript/orcid-js/lib/styles.js";
        
        var scripts = [swagger, xmle4x, xmldom, citeproc, orcidx, styles];
        
        getScripts(scripts, function(){
            $scope.$apply(function() {
                $scope.loadingScripts = false;
                $scope.scriptsLoaded = true;
                orcid.init(function(){
                    orcid.resolveCitations(orcidVar.orcidId, $scope.downloadBibtexExport, orcid.styleBibtex);
                });
            });            
        });
    };
    
    $scope.downloadBibtexExport = function(citations){
        $scope.bibtexGenerated = false;
        if (citations.length > 0){
            var text = "";
            for (c in citations){
                text += citations[c] +"\n"; 
            }
            text = text.replace(/<div class="csl-entry">/g, '');
            text = text.replace(/<\/div>/g, '');
            
            if(window.navigator.msSaveOrOpenBlob) {
                var fileData = [text];
                blobObject = new Blob(fileData, {type: 'text/plain'});
                window.navigator.msSaveOrOpenBlob(blobObject, "orcid.bib");                
            } else {
                $scope.bibtexGenerated = true;
                $scope.bibtexURL = "data:text/plain;charset=utf-8," + encodeURIComponent(text);   
            }
        }else{
            $scope.$apply(function() {
                $scope.bibtexExportError = true;
            });   
        }
    };
    
    
}]);

orcidNgModule.controller('PeerReviewCtrl', ['$scope', '$compile', '$filter', 'workspaceSrvc', 'commonSrvc', 'peerReviewSrvc', function ($scope, $compile, $filter, workspaceSrvc, commonSrvc, peerReviewSrvc){
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
        $scope.deletePutCode = putCode;
        $scope.deleteGroup = deleteGroup;
        var peerReview = peerReviewSrvc.getPeerReview(putCode);
        if (peerReview.subjectName)
            $scope.fixedTitle = peerReview.subjectName.value;
        else $scope.fixedTitle = '';
        var maxSize = 100;
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
        }).fail(function() {
            // something bad is happening!
            console.log("PeerReviewImportWizardError");
        });
    }
}]);


orcidNgModule.factory("peerReviewSrvc", ['$rootScope', function ($rootScope) {
    var peerReviewSrvc = {
    		constants: { 'access_type': { 'USER': 'user', 'ANONYMOUS': 'anonymous'}},
    		groups: new Array(),    		
    		loading: false,
            loadingDetails: false,
            quickRef: {},            
            loadingDetails: false,
            blankPeerReview: null,
            details: new Object(), // we should think about putting details in the
            peerReviewsToAddIds: null,
            peerReviewGroupDetailsRequested: new Array(),
            getBlankPeerReview: function(callback) {
            	 // if cached return clone of blank
                if (peerReviewSrvc.blankPeerReview != null)
                    callback(JSON.parse(JSON.stringify(peerReviewSrvc.blankPeerReview)));
    			$.ajax({
                    url: getBaseUri() + '/peer-reviews/peer-review.json',
                    dataType: 'json',
                    success: function(data) {
                    	callback(data);
                        $rootScope.$apply();
                    }
                }).fail(function() {
                    console.log("Error fetching blank Peer Review");
                });                
            },
            postPeerReview: function(peer_review, successFunc, failFunc) {            	
            	$.ajax({
                    url: getBaseUri() + '/peer-reviews/peer-review.json',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    type: 'POST',
                    data: angular.toJson(peer_review),
                    success: function(data) {
                    	successFunc(data);
                    }
                }).fail(function(){
                    failFunc();
                });
    		},
    		createNew: function(peerReview) {
                var cloneF = JSON.parse(JSON.stringify(peerReview));
                cloneF.source = null;
                cloneF.putCode = null;
                for (var idx in cloneF.externalIdentifiers)
                    cloneF.externalIdentifiers[idx].putCode = null;
                return cloneF;
            },                   
    		loadPeerReviews: function(access_type) {
    			if (access_type == peerReviewSrvc.constants.access_type.ANONYMOUS) {    				
    				peerReviewSrvc.peerReviewsToAddIds = orcidVar.PeerReviewIds;
    				peerReviewSrvc.addPeerReviewsToScope(peerReviewSrvc.constants.access_type.ANONYMOUS);
                } else {
                	peerReviewSrvc.peerReviewsToAddIds = null;
                	peerReviewSrvc.loading = true;
                	peerReviewSrvc.groups = new Array();
                	peerReviewSrvc.details = new Object();
                    $.ajax({
                        url: getBaseUri() + '/peer-reviews/peer-review-ids.json',
                        dataType: 'json',
                        success: function(data) {
                        	peerReviewSrvc.peerReviewsToAddIds = data;                        	
                        	peerReviewSrvc.addPeerReviewsToScope(peerReviewSrvc.constants.access_type.USER);
                            $rootScope.$apply();
                        }
                    }).fail(function(){
                        // something bad is happening!
                        console.log("error fetching Peer Review");
                    });
                };
    		},    		
    		addPeerReviewsToScope: function(type) {
                if (type == peerReviewSrvc.constants.access_type.USER)
                    var url = getBaseUri() + '/peer-reviews/get-peer-reviews.json?peerReviewIds=';
                else // use the anonymous url
                    var url = getBaseUri() + '/' + orcidVar.orcidId +'/peer-reviews.json?peerReviewIds=';
                if(peerReviewSrvc.peerReviewsToAddIds.length != 0 ) {
                	peerReviewSrvc.loading = true;
                    var peerReviewIds = peerReviewSrvc.peerReviewsToAddIds.splice(0,20).join();
                    $.ajax({
                        'url': url + peerReviewIds,
                        'dataType': 'json',
                        'success': function(data) {
                            $rootScope.$apply(function(){
                                for (i in data) {
                                    var dw = data[i];                                    
                                    removeBadExternalIdentifiers(dw);                                       
                                    GroupedActivities.group(dw,GroupedActivities.PEER_REVIEW,peerReviewSrvc.groups);
                                };
                            });
                            if(peerReviewSrvc.peerReviewsToAddIds.length == 0 ) {
                            	peerReviewSrvc.loading = false;
                                $rootScope.$apply();
                            } else {
                                $rootScope.$apply();
                                setTimeout(function(){
                                	peerReviewSrvc.addPeerReviewsToScope(type);
                                },50);
                            }
                        }
                    }).fail(function() {
                        //$rootScope.$apply(function() {
                        	peerReviewSrvc.loading = false;
                        //});
                        console.log("Error fetching Peer Review: " + peerReviewIds);
                    });
                } else {
                	peerReviewSrvc.loading = false;
                };
            },
            getGroup: function(putCode) {
                for (var idx in peerReviewSrvc.groups) {
                        if (peerReviewSrvc.groups[idx].hasPut(putCode))
                            return peerReviewSrvc.groups[idx];
                }
                return null;
            },
            getEditable: function(putCode, callback) {
                // first check if they are the current source
                var peerReview = peerReviewSrvc.getPeerReview(putCode);
                if (peerReview.source == orcidVar.orcidId)
                    callback(peerReview);
                else {
                    var bestMatch = null;
                    var group = peerReviewSrvc.getGroup(putCode);
                    for (var idx in group.activitiess) {
                        if (group[idx].source == orcidVar.orcidId) {
                            bestMatch = callback(group[idx]);
                            break;
                        }
                    }
                    if (bestMatch == null) 
                        bestMatch = peerReviewSrvc.createNew(peerReview);
                    	callback(bestMatch);
                	};
            },
            getPeerReview: function(putCode) {
                for (var idx in peerReviewSrvc.groups) {
                        if (peerReviewSrvc.groups[idx].hasPut(putCode))
                            return peerReviewSrvc.groups[idx].getByPut(putCode);
                }
                return null;
            },
            deleteGroupPeerReview: function(putCodes) {
                var rmPeerReview = new Array();
                var rmGroups = new Array();
                for (var idj in putCodes)
                    for (var idx in peerReviewSrvc.groups) {
                        if (peerReviewSrvc.groups[idx].hasPut(putCodes[idj])) {
                            rmGroups.push(idx);
                            for (var idj in peerReviewSrvc.groups[idx].activities)
                                rmPeerReview.push(peerReviewSrvc.groups[idx].activities[idj].putCode.value);
                        };
                    }
                while (rmGroups.length > 0) 
                	peerReviewSrvc.groups.splice(rmGroups.pop(),1);
                peerReviewSrvc.removePeerReview(rmPeerReview);
            },
            deletePeerReview: function(putCode) {
            	peerReviewSrvc.removePeerReview([putCode], function() {peerReviewSrvc.loadPeerReviews(peerReviewSrvc.constants.access_type.USER);});
            },
            makeDefault: function(group, putCode) {
            	group.makeDefault(putCode);
                $.ajax({
                    url: getBaseUri() + '/peer-reviews/updateToMaxDisplay.json?putCode=' + putCode,
                    type: 'GET',
                    dataType: 'json',
                    success: function(data) {
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("Error: peerReviewSrvc.makeDefault method");
                });
            },
            removePeerReview: function(putCodes,callback) {
                $.ajax({
                    url: getBaseUri() + '/peer-reviews/' + putCodes.splice(0,150).join(),
                    type: 'DELETE',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if (putCodes.length > 0) 
                        	peerReviewSrvc.removePeerReview(putCodes,callback);
                        else if (callback)
                            callback(data);
                    }
                }).fail(function() {
                    console.log("Error deleting Peer Review.");
                });
            },
            setGroupPrivacy: function(putCode, priv) {
                var group = peerReviewSrvc.getGroup(putCode);
                var putCodes = new Array();
                for (var idx in group.activities) {
                    putCodes.push(group.activities[idx].putCode.value);
                    group.activities[idx].visibility = priv;
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
            peerReviewCount: function() {
                var count = 0;
                for (var idx in peerReviewSrvc.groups) {
                    count += peerReviewSrvc.groups[idx].activitiesCount;
                }
                return count;
            },
            getPeerReviewGroupDetails: function(groupIDPutCode, putCode){
            	if (peerReviewSrvc.peerReviewGroupDetailsRequested.indexOf(groupIDPutCode) < 0){            		
            		peerReviewSrvc.peerReviewGroupDetailsRequested.push(groupIDPutCode);            		
            		var group = peerReviewSrvc.getGroup(putCode);
            		$.ajax({
                        url: getBaseUri() + '/public/group/' + groupIDPutCode,
                        dataType: 'json',
                        contentType: 'application/json;charset=UTF-8',
                        type: 'GET',
                        success: function(data) {
                        	$rootScope.$apply(function(){
                        		group.groupName = data.name;
                        		group.groupDescription = data.description;
                        		group.groupType = data.type;
                        	});
                        }
                    }).fail(function(xhr, status, error){
                        console.log("Error: " + status + "\nError: " + error + "\nError detail: " + xhr.responseText);
                    });
            		
            	}
            }
    };
    return peerReviewSrvc;
}]);


orcidNgModule.controller('SearchCtrl',['$scope', '$compile', function ($scope, $compile){
    $scope.hasErrors = false;
    $scope.results = new Array();
    $scope.numFound = 0;
    $scope.input = {};
    $scope.input.start = 0;
    $scope.input.rows = 10;
    $scope.input.text = $('#SearchCtrl').data('search-query');

    $scope.getResults = function(){
        $.ajax({
            url: orcidSearchUrlJs.buildUrl($scope.input),
            dataType: 'json',
            headers: { Accept: 'application/json'},
            success: function(data) {
                $('#ajax-loader').hide();
                var resultsContainer = data['orcid-search-results'];
                $scope.numFound = resultsContainer['num-found'];
                if(resultsContainer['orcid-search-result']){
                    $scope.results = $scope.results.concat(resultsContainer['orcid-search-result']);
                }
                if(!$scope.numFound){
                    $('#no-results-alert').fadeIn(1200);
                }
                $scope.areMoreResults = $scope.numFound > ($scope.input.start + $scope.input.rows);
                $scope.$apply();
                var newSearchResults = $('.new-search-result');
                if(newSearchResults.length > 0){
                    newSearchResults.fadeIn(1200);
                    newSearchResults.removeClass('new-search-result');
                    var newSearchResultsTop = newSearchResults.offset().top;
                    var showMoreButtonTop = $('#show-more-button-container').offset().top;
                    var bottom = $(window).height();
                    if(showMoreButtonTop > bottom){
                        $('html, body').animate(
                            {
                                scrollTop: newSearchResultsTop
                            },
                            1000,
                            'easeOutQuint'
                        );
                    }
                }
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error doing search");
        });
    };

    $scope.getFirstResults = function(){
        $('#no-results-alert').hide();
        $scope.results = new Array();
        $scope.numFound = 0;
        $scope.input.start = 0;
        $scope.input.rows = 10;
        $scope.areMoreResults = false;
        if($scope.isValid()){
            $scope.hasErrors = false;
            $('#ajax-loader').show();
            $scope.getResults();
        }
        else{
            $scope.hasErrors = true;
        }
    };

    $scope.getMoreResults = function(){
        $('#ajax-loader').show();
        $scope.input.start += 10;
        $scope.getResults();
    };

    $scope.concatPropertyValues = function(array, propertyName){
        if(typeof array === 'undefined'){
            return '';
        }
        else{
            return $.map(array, function(o){ return o[propertyName]; }).join(', ');
        }
    };

    $scope.areResults = function(){
        return $scope.results.length > 0;
    };

    $scope.isValid = function(){
        return orcidSearchUrlJs.isValidInput($scope.input);
    };

    $scope.isValidOrcidId = function(){
        if(typeof $scope.input.text === 'undefined' || $scope.input.text === null || $scope.input.text === '' || orcidSearchUrlJs.isValidOrcidId($scope.input.text)){
            return true;
        }
        return false;
    }

    // init
    if(typeof $scope.input.text !== 'undefined'){
        $('#ajax-loader').show();
        $scope.getResults();
    }
}]);

// Controller for delegate permissions that have been granted BY the current user
orcidNgModule.controller('DelegatesCtrl',['$scope', '$compile', function DelegatesCtrl($scope, $compile){
    $scope.results = new Array();
    $scope.numFound = 0;
    $scope.input = {};
    $scope.input.start = 0;
    $scope.input.rows = 10;
    $scope.showInitLoader = true;
    $scope.showLoader = false;
    $scope.effectiveUserOrcid = orcidVar.orcidId;
    $scope.realUserOrcid = orcidVar.realOrcidId;
    $scope.sort = {
        column: 'delegateSummary.creditName.content',
        descending: false
    };
    $scope.isPasswordConfirmationRequired = orcidVar.isPasswordConfirmationRequired;

    $scope.changeSorting = function(column) {
        var sort = $scope.sort;
        if (sort.column === column) {
            sort.descending = !sort.descending;
        } else {
            sort.column = column;
            sort.descending = false;
        }
    };

    $scope.search = function(){
        $scope.results = new Array();
        $scope.showLoader = true;
        $('#no-results-alert').hide();
        if(isEmail($scope.input.text)){
            $scope.numFound = 0;
            $scope.start = 0;
            $scope.areMoreResults = 0;
            $scope.searchByEmail();
        }
        else{
            $scope.getResults();
        }
    };

    $scope.searchByEmail = function(){
        $.ajax({
            url: $('body').data('baseurl') + "manage/search-for-delegate-by-email/" + encodeURIComponent($scope.input.text) + '/',
            dataType: 'json',
            headers: { Accept: 'application/json'},
            success: function(data) {
                $scope.confirmAddDelegateByEmail(data);
                $scope.showLoader = false;
                $scope.$apply();
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error doing search for delegate by email");
        });

    };

    $scope.getResults = function(rows){
        $.ajax({
            url: orcidSearchUrlJs.buildUrl($scope.input)+'&callback=?',
            dataType: 'json',
            headers: { Accept: 'application/json'},
            success: function(data) {
                var resultsContainer = data['orcid-search-results'];
                $scope.numFound = resultsContainer['num-found'];
                if(resultsContainer['orcid-search-result']){
                    $scope.numFound = resultsContainer['num-found'];
                    $scope.results = $scope.results.concat(resultsContainer['orcid-search-result']);
                }
                var tempResults = $scope.results;
                for(var index = 0; index < tempResults.length; index ++) {
                	if($scope.results[index]['orcid-profile']['orcid-bio']['personal-details'] == null) {
                		$scope.results.splice(index, 1);
                	} 
                }
                $scope.numFound = $scope.results.length;
                if(!$scope.numFound){
                    $('#no-results-alert').fadeIn(1200);
                }
                $scope.areMoreResults = $scope.numFound >= ($scope.start + $scope.rows);
                $scope.showLoader = false;
                $scope.$apply();
                var newSearchResults = $('.new-search-result');
                if(newSearchResults.length > 0){
                    newSearchResults.fadeIn(1200);
                    newSearchResults.removeClass('new-search-result');
                    var newSearchResultsTop = newSearchResults.offset().top;
                    var showMoreButtonTop = $('#show-more-button-container').offset().top;
                    var bottom = $(window).height();
                    if(showMoreButtonTop > bottom){
                        $('html, body').animate(
                            {
                                scrollTop: newSearchResultsTop
                            },
                            1000,
                            'easeOutQuint'
                        );
                    }
                }
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error doing search for delegates");
        });
    };

    $scope.getMoreResults = function(){
        $scope.showLoader = true;
        $scope.start += 10;
        $scope.getResults();
    };

    $scope.concatPropertyValues = function(array, propertyName){
        if(typeof array === 'undefined'){
            return '';
        }
        else{
            return $.map(array, function(o){ return o[propertyName]; }).join(', ');
        }
    };

    $scope.areResults = function(){
        return $scope.numFound != 0;
    };

    $scope.getDisplayName = function(result){
        var personalDetails = result['orcid-profile']['orcid-bio']['personal-details'];
        var name = "";
        if(personalDetails != null) {
        	var creditName = personalDetails['credit-name'];
            if(creditName != null){
                return creditName.value;
            }
            name = personalDetails['given-names'].value;
            if(personalDetails['family-name'] != null) {
            	name = name + ' ' + personalDetails['family-name'].value;
            }
        }
        return name;
    };

    $scope.confirmAddDelegateByEmail = function(emailSearchResult){
        $scope.errors = [];
        $scope.emailSearchResult = emailSearchResult;
        $.colorbox({
            html : $compile($('#confirm-add-delegate-by-email-modal').html())($scope),
            transition: 'fade',
            close: '',
            onLoad: function() {
                $('#cboxClose').remove();
            },
            onComplete: function() {$.colorbox.resize();},
            scrolling: true
        });
    };

    $scope.confirmAddDelegate = function(delegateName, delegateId, delegateIdx){
        $scope.errors = [];
        $scope.delegateNameToAdd = delegateName;
        $scope.delegateToAdd = delegateId;
        $scope.delegateIdx = delegateIdx;
        $.colorbox({
            html : $compile($('#confirm-add-delegate-modal').html())($scope),
            transition: 'fade',
            close: '',
            onLoad: function() {
                $('#cboxClose').remove();
            },
            onComplete: function() {$.colorbox.resize();},
            scrolling: true
        });
    };

    $scope.addDelegateByEmail = function(delegateEmail) {
        $scope.errors = [];
        var addDelegate = {};
        addDelegate.delegateEmail = $scope.input.text;
        addDelegate.password = $scope.password;
        $.ajax({
            url: $('body').data('baseurl') + 'account/addDelegateByEmail.json',
            type: 'POST',
            data: angular.toJson(addDelegate),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                if(data.errors.length === 0){
                    $scope.getDelegates();
                    $scope.$apply();
                    $scope.closeModal();
                }
                else{
                    $scope.errors = data.errors;
                    $scope.$apply();
                }
            }
        }).fail(function() {
            console.log("Error adding delegate.");
        });
    };

    $scope.addDelegate = function() {
        var addDelegate = {};
        addDelegate.delegateToManage = $scope.delegateToAdd;
        addDelegate.password = $scope.password;
        $.ajax({
            url: getBaseUri() + '/account/addDelegate.json',
            type: 'POST',
            data: angular.toJson(addDelegate),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                if(data.errors.length === 0){
                    $scope.getDelegates();
                    $scope.results.splice($scope.delegateIdx, 1);
                    $scope.$apply();
                    $scope.closeModal();
                }
                else{
                    $scope.errors = data.errors;
                    $scope.$apply();
                }
            }
        }).fail(function() {
            console.log("Error adding delegate.");
        });
    };

    $scope.confirmRevoke = function(delegateName, delegateId) {
        $scope.errors = [];
        $scope.delegateNameToRevoke = delegateName;
        $scope.delegateToRevoke = delegateId;
        $.colorbox({
            html : $compile($('#revoke-delegate-modal').html())($scope)

        });
        $.colorbox.resize();
    };

    $scope.revoke = function () {
        var revokeDelegate = {};
        revokeDelegate.delegateToManage = $scope.delegateToRevoke;
        revokeDelegate.password = $scope.password;
        $.ajax({
            url: getBaseUri() + '/account/revokeDelegate.json',
            type: 'POST',
            data:  angular.toJson(revokeDelegate),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                if(data.errors.length === 0){
                    $scope.getDelegates();
                    $scope.$apply();
                    $scope.closeModal();
                }
                else{
                    $scope.errors = data.errors;
                    $scope.$apply();
                }
            }
        }).fail(function() {
            // something bad is happening!
            console.log("$DelegateCtrl.revoke() error");
        });
    };

    $scope.getDelegates = function() {
        $.ajax({
            url: getBaseUri() + '/account/delegates.json',
            dataType: 'json',
            success: function(data) {
                $scope.delegatesByOrcid = {};
                $scope.delegation = data;
                if(data != null && data.givenPermissionTo != null){
                    for(var i=0; i < data.givenPermissionTo.delegationDetails.length; i++){
                        var delegate = data.givenPermissionTo.delegationDetails[i];
                        $scope.delegatesByOrcid[delegate.delegateSummary.orcidIdentifier.path] = delegate;
                    }
                }
                $scope.showInitLoader = false;
                $scope.$apply();
            }
        }).fail(function() {
            $scope.showInitLoader = false;
            // something bad is happening!
            console.log("error with delegates");
        });
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };

    // init
    $scope.getDelegates();

}]);

// Controller for delegate permissions that have been granted TO the current user
orcidNgModule.controller('DelegatorsCtrl',['$scope', '$compile', function ($scope, $compile){

    $scope.sort = {
            column: 'delegateSummary.creditName.content',
            descending: false
    };

    $scope.changeSorting = function(column) {
        var sort = $scope.sort;
        if (sort.column === column) {
            sort.descending = !sort.descending;
        } else {
            sort.column = column;
            sort.descending = false;
        }
    };

    $scope.getDelegators = function() {
        $.ajax({
            url: getBaseUri() + '/delegators/delegators-and-me.json',
            dataType: 'json',
            success: function(data) {
                $scope.delegators = data.delegators;
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with delegates");
        });
    };

    $scope.selectDelegator = function(datum) {
        window.location.href = getBaseUri() + '/switch-user?j_username=' + datum.orcid;
    };

    $("#delegatorsSearch").typeahead({
        name: 'delegatorsSearch',
        remote: {
            url: getBaseUri()+'/delegators/search-for-data/%QUERY?limit=' + 10
        },
        template: function (datum) {
            var forDisplay;
            if(datum.noResults){
                forDisplay = "<span class=\'no-delegator-matches\'>" + om.get('delegators.nomatches') + "</span>";
            }
            else{
                forDisplay =
                    '<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value + '</span>'
                    +'<span style=\'font-size: 80%;\'> (' + datum.orcid + ')</span>';
            }
            return forDisplay;
        }
    });
    $("#delegatorsSearch").bind("typeahead:selected", function(obj, datum) {
        if(!datum.noResults){
            $scope.selectDelegator(datum);
        }
        $scope.$apply();
    });

    // init
    $scope.getDelegators();

}]);

orcidNgModule.controller('SocialCtrl',['$scope', '$compile', 'discoSrvc', function SocialCtrl($scope, $compile, discoSrvc){
    $scope.showLoader = false;
    $scope.sort = {
        column: 'providerUserId',
        descending: false
    };
    $scope.isPasswordConfirmationRequired = orcidVar.isPasswordConfirmationRequired;

    $scope.changeSorting = function(column) {
        var sort = $scope.sort;
        if (sort.column === column) {
            sort.descending = !sort.descending;
        } else {
            sort.column = column;
            sort.descending = false;
        }
    };

    $scope.confirmRevoke = function(socialAccount) {
        $scope.errors = [];
        $scope.socialAccount = socialAccount;
        $scope.idToManage = socialAccount.id;
        $.colorbox({
            html : $compile($('#revoke-social-account-modal').html())($scope),            
            onComplete: function() {
                $.colorbox.resize({height:"200px", width:"500px"});        
            }
        });
        
    };

    $scope.revoke = function () {
        var revokeSocialAccount = {};
        revokeSocialAccount.idToManage = $scope.idToManage;
        revokeSocialAccount.password = $scope.password;
        $.ajax({
            url: getBaseUri() + '/account/revokeSocialAccount.json',
            type: 'POST',
            data:  angular.toJson(revokeSocialAccount),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                if(data.errors.length === 0){
                    $scope.getSocialAccounts();
                    $scope.$apply();
                    $scope.closeModal();
                    $scope.password = "";
                }
                else{
                    $scope.errors = data.errors;
                    $scope.$apply();
                }
            }
        }).fail(function() {
            // something bad is happening!
            console.log("$SocialCtrl.revoke() error");
        });
    };

    $scope.getSocialAccounts = function() {
        $.ajax({
            url: getBaseUri() + '/account/socialAccounts.json',
            dataType: 'json',
            success: function(data) {
                $scope.socialAccounts = data;
                $scope.populateIdPNames();
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error getting social accounts");
        });
    };
    
    $scope.$watch(function() { return discoSrvc.feed; }, function(){
        $scope.populateIdPNames();
        
    });
    
    $scope.populateIdPNames = function() {
        if(discoSrvc.feed != null) {
            for(i in $scope.socialAccounts){
                var account = $scope.socialAccounts[i];
                var name = discoSrvc.getIdPName(account.id.providerid);
                account.idpName = name;
            }
        }
    }

    $scope.closeModal = function() {
        $.colorbox.close();
    };
    // init
    $scope.getSocialAccounts();

}]);


// Controller for notifications
orcidNgModule.controller('NotificationsCtrl',['$scope', '$compile', 'notificationsSrvc', function ($scope, $compile, notificationsSrvc){
    $scope.displayBody = {};
    notificationsSrvc.displayBody = {};    
    $scope.notifications = notificationsSrvc.notifications;
    $scope.showMore = notificationsSrvc.showMore;
    $scope.areMore = notificationsSrvc.areMore;
    $scope.archive = notificationsSrvc.archive;
    $scope.getNotifications = notificationsSrvc.getNotifications;
    $scope.reloadNotifications = notificationsSrvc.reloadNotifications;
    $scope.notificationsSrvc = notificationsSrvc;
    $scope.bulkChecked = notificationsSrvc.bulkChecked;
    $scope.bulkArchiveMap = notificationsSrvc.bulkArchiveMap;    
    

    $scope.toggleDisplayBody = function (notificationId) {
        $scope.displayBody[notificationId] = !$scope.displayBody[notificationId];        
        notificationsSrvc.displayBody[notificationId] = $scope.displayBody[notificationId]; 
        notificationsSrvc.flagAsRead(notificationId);
        iframeResize(notificationId);
    };    
    
    $scope.$watch(function () { return notificationsSrvc.bulkChecked }, function (newVal, oldVal) {
        if (typeof newVal !== 'undefined') {
        	$scope.bulkChecked = notificationsSrvc.bulkChecked;
        }
    });

        
}]);

// Controller to show alert for unread notifications
orcidNgModule.controller('NotificationsAlertCtrl',['$scope', '$compile', 'notificationsSrvc', function ($scope, $compile, notificationsSrvc){
    $scope.getUnreadCount = notificationsSrvc.getUnreadCount;
    notificationsSrvc.retrieveUnreadCount();
}]);

orcidNgModule.controller('SwitchUserCtrl',['$scope', '$compile', '$document', function ($scope, $compile, $document){
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
        }).fail(function() {
            // something bad is happening!
            console.log("error with delegates");
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

orcidNgModule.controller('statisticCtrl',['$scope', function ($scope){
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
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error getting statistics Live iDs total amount");
        });
    };

    $scope.getLiveIds();
}]);

orcidNgModule.controller('languageCtrl',['$scope', '$cookies', 'widgetSrvc', function ($scope, $cookies, widgetSrvc) {
    var productionLangList =
        [
            {
                "value": "cs",
                "label": "čeština"
            },
            {
                "value": "en",
                "label": "English"
            },
            {
                "value": 'es',
                "label": 'Español'
            },
            {
                "value": 'fr',
                "label": 'Français'
            },
            {
                "value": 'it',
                "label": 'Italiano'
            },
            {
                "value": 'ja',
                "label": '日本語'
            },
            {
                "value": 'ko',
                "label": '한국어'
            },
            {
                "value": 'pt',
                "label": 'Português'
            },
            {
                "value": 'ru',
                "label": 'Русский'
            },
            {
                "value": 'zh_CN',
                "label": '简体中文'
            },
            {
                "value": 'zh_TW',
                "label": '繁體中文'
            }
        ];
    var testingLangList =
        [
            {
                "value": "cs",
                "label": "čeština"
            },
            {
                "value": "en",
                "label": "English"
            },
            {
                "value": 'es',
                "label": 'Español'
            },
            {
                "value": 'fr',
                "label": 'Français'
            },
            {
                "value": 'it',
                "label": 'Italiano'
            },
            {
                "value": 'ja',
                "label": '日本語'
            },
            {
                "value": 'ko',
                "label": '한국어'
            },
            {
                "value": 'pt',
                "label": 'Português'
            },
            {
                "value": 'ru',
                "label": 'Русский'
            },
            {
                "value": 'xx',
                "label": 'X'
            },
            {
                "value": 'zh_CN',
                "label": '简体中文'
            },
            {
                "value": 'zh_TW',
                "label": '繁體中文'
            }
        ];
    
    $scope.widgetSrvc = widgetSrvc;

    if (location == parent.location && window.location.hostname.toLowerCase() != "orcid.org")
        $scope.languages = testingLangList;
    else
        $scope.languages = productionLangList;

    //Load Language that is set in the cookie or set default language to english
    $scope.getCurrentLanguage = function(){
        $scope.language = $scope.languages[0]; //Default
        typeof($cookies.get('locale_v3')) !== 'undefined' ? locale_v3 = $cookies.get('locale_v3') : locale_v3 = "en"; //If cookie exists we get the language value from it        
        angular.forEach($scope.languages, function(value, key){ //angular.forEach doesn't support break
            if (value.value == locale_v3){
            	$scope.language = $scope.languages[key];
            	$scope.widgetSrvc.locale = $scope.language.value; 
            }
        });
    };

    $scope.getCurrentLanguage(); //Checking for the current language value

    $scope.selectedLanguage = function(){
        $.ajax({
            url: getBaseUri()+'/lang.json?lang=' + $scope.language.value + "&callback=?",
            type: 'GET',
            dataType: 'json',
            success: function(data){
                angular.forEach($scope.languages, function(value, key){
                    if(value.value == data.locale){
                        $scope.language = $scope.languages[key];                        
                        $scope.widgetSrvc.setLocale($scope.language.value);
                        window.location.reload(true);
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error setting up language cookie");
        });
    };
}]);

orcidNgModule.controller('adminVerifyEmailCtrl',['$scope','$compile', function ($scope,$compile){
    $scope.showSection = false;

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#verify_email_section').toggle();
    };

    $scope.verifyEmail = function(){
        $.ajax({
            url: getBaseUri()+'/admin-actions/admin-verify-email.json',
            type: 'POST',
            dataType: 'text',
            data: $scope.email,
            contentType: 'application/json;charset=UTF-8',
            success: function(data){
                $scope.$apply(function(){
                    $scope.result = data;
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error verifying the email address");
        });
    };
}]);

orcidNgModule.controller('profileDeactivationAndReactivationCtrl',['$scope', '$compile', function ($scope,$compile){
    $scope.orcidToDeactivate = null;
    $scope.orcidToReactivate = null;
    $scope.deactivatedAccount = null;
    $scope.reactivatedAccount = null;
    $scope.successMessage = null;
    $scope.deactivateMessage = om.get('admin.profile_deactivation.success');
    $scope.reactivateMessage = om.get('admin.profile_reactivation.success');
    $scope.showDeactivateModal = false;
    $scope.showReactivateModal = false;

    $scope.toggleDeactivationModal = function(){
        $scope.showDeactivateModal = !$scope.showDeactivateModal;
        $('#deactivation_modal').toggle();
    };

    $scope.toggleReactivationModal = function(){
        $scope.showReactivateModal = !$scope.showReactivateModal;
        $('#reactivation_modal').toggle();
    };

    $scope.deactivateAccount = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/deactivate-profile?orcid=' + $scope.orcidToDeactivate,
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.$apply(function(){
                    $scope.deactivatedAccount = data;
                    if($scope.deactivatedAccount.errors != null && $scope.deactivatedAccount.errors.length != 0){
                        $scope.closeModal();
                    } else {
                        $scope.orcidToDeactivate = null;
                        $scope.showSuccessMessage($scope.deactivateMessage);
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error deprecating the account");
        });
    };

    $scope.reactivateAccount = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/reactivate-profile?orcid=' + $scope.orcidToReactivate,
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.$apply(function(){
                    $scope.reactivatedAccount = data;
                    if($scope.reactivatedAccount.errors != null && $scope.reactivatedAccount.errors.length != 0){
                        $scope.closeModal();
                    } else {
                        $scope.orcidToReactivate = null;
                        $scope.showSuccessMessage($scope.reactivateMessage);
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error reactivating the account");
        });
    };

    $scope.confirmDeactivateAccount = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/deactivate-profile/check-orcid.json?orcid=' + $scope.orcidToDeactivate,
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.deactivatedAccount = data;
                if($scope.deactivatedAccount.errors != null && $scope.deactivatedAccount.errors.length != 0){
                    console.log($scope.deactivatedAccount.errors);
                } else {
                    $scope.showConfirmModal();
                }
                $scope.$apply();
            }
            }).fail(function(error) {
                // something bad is happening!
                console.log("Error deactivating the account");
            });
    };

    $scope.confirmReactivateAccount = function() {
        $.colorbox({
            html : $compile($('#confirm-reactivation-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"450px" , height:"225px"});
    };

    $scope.showConfirmModal = function() {
        $.colorbox({
            html : $compile($('#confirm-deactivation-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"525px" , height:"275px"});
    };

    $scope.showSuccessMessage = function(message){
        $scope.successMessage = message;
        $.colorbox({
            html : $compile($('#success-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"425px" , height:"225px"});
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };
}]);

orcidNgModule.controller('DeactivateProfileCtrl', ['$scope', function ($scope) {
	$scope.orcidsToDeactivate = "";
	$scope.showSection = false;

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#deactivation_modal').toggle();
    };

	
	$scope.deactivateOrcids = function() {
		$.ajax({
            url: getBaseUri()+'/admin-actions/deactivate-profiles.json',
            type: 'POST',
            dataType: 'json',
            data: $scope.orcidsToDeactivate,
            contentType: 'application/json;charset=UTF-8',
            success: function(data){
            	$scope.$apply(function(){
            		$scope.result = data;
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error re-sending claim emails");
        });
	}
}]);

orcidNgModule.controller('profileDeprecationCtrl',['$scope','$compile', function profileDeprecationCtrl($scope,$compile){
    $scope.deprecated_verified = false;
    $scope.primary_verified = false;
    $scope.deprecatedAccount = null;
    $scope.primaryAccount = null;
    $scope.showModal = false;

    $scope.toggleDeprecationModal = function(){
        $scope.showModal = !$scope.showModal;
        $('#deprecation_modal').toggle();
    };

    $scope.cleanup = function(orcid_type){
        $("#deprecated_orcid").removeClass("orcid-red-background-input");
        $("#primary_orcid").removeClass("orcid-red-background-input");
        if(orcid_type == 'deprecated'){
            if($scope.deprecated_verified == false)
                $("#deprecated_orcid").addClass("error");
            else
                $("#deprecated_orcid").removeClass("error");
        } else {
            if($scope.primary_verified == false)
                $("#primary_orcid").addClass("error");
            else
                $("#primary_orcid").removeClass("error");
        }
    };

    $scope.getAccountDetails = function (orcid, callback){
        $.ajax({
            url: getBaseUri()+'/admin-actions/deprecate-profile/check-orcid.json?orcid=' + orcid,
            type: 'GET',
            dataType: 'json',
            success: function(data){
                callback(data);
                $scope.$apply();
                }
            }).fail(function(error) {
                // something bad is happening!
                console.log("Error getting account details for: " + orcid);
            });
    };

    $scope.findAccountDetails = function(orcid_type){
        var orcid;
        var orcidRegex=new RegExp("(\\d{4}-){3,}\\d{3}[\\dX]");
        if(orcid_type == 'deprecated') {
            orcid = $scope.deprecatedAccount.orcid;
        } else {
            orcid = $scope.primaryAccount.orcid;
        }
        //Reset styles
        $scope.cleanup(orcid_type);
        if(orcidRegex.test(orcid)){
            $scope.getAccountDetails(orcid, function(data){
                if(orcid_type == 'deprecated') {
                    $scope.invalid_regex_deprecated = false;
                    if(data.errors.length != 0){
                        $scope.deprecatedAccount.errors = data.errors;
                        $scope.deprecatedAccount.givenNames = null;
                        $scope.deprecatedAccount.familyName = null;
                        $scope.deprecatedAccount.primaryEmail = null;
                        $scope.deprecated_verified = false;
                    } else {
                        $scope.deprecatedAccount.errors = null;
                        $scope.deprecatedAccount.givenNames = data.givenNames;
                        $scope.deprecatedAccount.familyName = data.familyName;
                        $scope.deprecatedAccount.primaryEmail = data.email;
                        $scope.deprecated_verified = true;
                        $scope.cleanup(orcid_type);
                    }
                } else {
                    $scope.invalid_regex_primary = false;
                    if(data.errors.length != 0){
                        $scope.primaryAccount.errors = data.errors;
                        $scope.primaryAccount.givenNames = null;
                        $scope.primaryAccount.familyName = null;
                        $scope.primaryAccount.primaryEmail = null;
                        $scope.primary_verified = false;
                    } else {
                        $scope.primaryAccount.errors = null;
                        $scope.primaryAccount.givenNames = data.givenNames;
                        $scope.primaryAccount.familyName = data.familyName;
                        $scope.primaryAccount.primaryEmail = data.email;
                        $scope.primary_verified = true;
                        $scope.cleanup(orcid_type);
                    }
                }
            });
        } else {
            if(orcid_type == 'deprecated') {
                if(!($scope.deprecatedAccount === undefined)){
                    $scope.invalid_regex_deprecated = true;
                    $scope.deprecatedAccount.errors = null;
                    $scope.deprecatedAccount.givenNames = null;
                    $scope.deprecatedAccount.familyName = null;
                    $scope.deprecatedAccount.primaryEmail = null;
                    $scope.deprecated_verified = false;
                }
            } else {
                if(!($scope.primaryAccount === undefined)){
                    $scope.invalid_regex_primary = true;
                    $scope.primaryAccount.errors = null;
                    $scope.primaryAccount.givenNames = null;
                    $scope.primaryAccount.familyName = null;
                    $scope.primaryAccount.primaryEmail = null;
                    $scope.primary_verified = false;
                }
            }
        }
    };

    $scope.confirmDeprecateAccount = function(){
        var isOk = true;
        $scope.errors = null;
        if($scope.deprecated_verified === undefined || $scope.deprecated_verified == false){
            $("#deprecated_orcid").addClass("error");
            $("#deprecated_orcid").addClass("orcid-red-background-input");
            isOk = false;
        }

        if($scope.primary_verified === undefined || $scope.primary_verified == false){
            $("#primary_orcid").addClass("error");
            $("#primary_orcid").addClass("orcid-red-background-input");
            isOk = false;
        }

        if(isOk){
            $.colorbox({
                html : $compile($('#confirm-deprecation-modal').html())($scope),
                    scrolling: true,
                    onLoad: function() {
                    $('#cboxClose').remove();
                },
                scrolling: true
            });

            $.colorbox.resize({width:"625px" , height:"400px"});
        }
    };

    $scope.deprecateAccount = function(){
        var deprecatedOrcid = $scope.deprecatedAccount.orcid;
        var primaryOrcid = $scope.primaryAccount.orcid;
        $.ajax({
            url: getBaseUri()+'/admin-actions/deprecate-profile/deprecate-profile.json?deprecated=' + deprecatedOrcid + '&primary=' + primaryOrcid,
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.$apply(function(){
                    if(data.errors.length != 0){
                        $scope.errors = data.errors;
                    } else {
                        $scope.showSuccessModal(deprecatedOrcid, primaryOrcid);
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error deprecating the account");
        });
    };

    $scope.showSuccessModal = function(deprecated, primary){
        $scope.successMessage = om.get('admin.profile_deprecation.deprecate_account.success_message').replace("{{0}}", deprecated).replace("{{1}}", primary);

        //Clean fields
        $scope.deprecated_verified = false;
        $scope.primary_verified = false;
        $scope.deprecatedAccount = null;
        $scope.primaryAccount = null;

        $.colorbox({
            html : $compile($('#success-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"450px" , height:"150px"});
    };

    $scope.closeModal = function() {
    	$scope.deprecated_verified = false;
        $scope.primary_verified = false;
        $scope.deprecatedAccount = null;
        $scope.primaryAccount = null;
        $scope.showModal = false;    	
        $.colorbox.close();
    };
}]);

orcidNgModule.controller('revokeApplicationFormCtrl',['$scope', '$compile', function ($scope,$compile){
    $scope.confirmRevoke = function(applicationSummary){
        $scope.appName = applicationSummary.name;
        $scope.appClientId = applicationSummary.orcidPath;
        $scope.appScopePaths = [];
        for (var scopePath in applicationSummary.scopePaths) {
        	 $scope.appScopePaths.push(scopePath);
    	}
        $.colorbox({
            html : $compile($('#confirm-revoke-access-modal').html())($scope),
            transition: 'fade',
            close: '',
            onLoad: function() {
                $('#cboxClose').remove();
            },
            onComplete: function() {$.colorbox.resize();},
            scrolling: true
        });
    };

    $scope.revokeAccess = function(){
        $.ajax({
            url: getBaseUri() + '/account/revoke-application.json?applicationOrcid='+$scope.appClientId+'&scopePaths='+$scope.appScopePaths,
            type: 'POST',
            success: function(data) {
                $scope.getApplications();
                $scope.$apply();
                $scope.closeModal();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("revokeApplicationFormCtrl.revoke() error");
        });
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };
    
    $scope.getApplications = function() {
	    $.ajax({
	        url: getBaseUri()+'/account/get-trusted-orgs.json',
	        type: 'GET',
	        dataType: 'json',
	        success: function(data){
	        	$scope.$apply(function(){
	        		for(var index1 = 0; index1 < data.length; index1 ++) {
	        			data[index1].approvalDate = formatDate(data[index1].approvalDate);	        			
	            	}
	        		$scope.applicationSummaryList = data;
	        	});
	        }
	    }).fail(function(error) {
	        // something bad is happening!
	        console.log("Error finding the information");
	    });
    }
    
    $scope.getApplicationUrlLink = function(application) {
        if(application.websiteValue != null) {
            if(application.websiteValue.lastIndexOf('http://') === -1 && application.websiteValue.lastIndexOf('https://') === -1) {
                return '//' + application.websiteValue;
            } else {
                return application.websiteValue;
            }
        }
        return '';
    }
    
    $scope.getApplications();
    
}]);

/**
 * Manage members controller
 * */
orcidNgModule.controller('manageMembersCtrl',['$scope', '$compile', function manageMembersCtrl($scope, $compile) {    
    $scope.showFindModal = false;
    $scope.success_message = null;
    $scope.client_id = null;
    $scope.client = null;
    $scope.showError = false;
    $scope.availableRedirectScopes = [];
    $scope.selectedScope = "";
    $scope.newMember = null;
    $scope.groups = [];
    $scope.importWorkWizard = {
    	'actTypeList' : ['Articles','Books','Data','Student Publications'],
		'geoAreaList' : ['Global', 'Africa', 'Asia', 'Australia', 'Europe', 'North America', 'South America']
    };

    $scope.toggleGroupsModal = function() {
        $scope.showAdminGroupsModal = !$scope.showAdminGroupsModal;
        $('#admin_groups_modal').toggle();
    };
    
    $scope.toggleFindModal = function() {
        $scope.showAdminGroupsModal = !$scope.showAdminGroupsModal;
        $('#find_edit_modal').toggle();
    };
    
    /**
     * FIND
     * */
    $scope.findAny = function() {
    	success_edit_member_message = null;
    	success_message = null;
    	$.ajax({
            url: getBaseUri()+'/manage-members/find.json?id=' + encodeURIComponent($scope.any_id),
            type: 'GET',
            dataType: 'json',
            success: function(data){
            	$scope.$apply(function(){  
                	if(data.client == true) {
                    	$scope.client = data.clientObject;
                    	$scope.member = null;
                    	for(var i = 0; i < $scope.client.redirectUris.length; i ++) {
                    		$scope.client.redirectUris[i].actType.value = JSON.parse($scope.client.redirectUris[i].actType.value);
                    		$scope.client.redirectUris[i].geoArea.value = JSON.parse($scope.client.redirectUris[i].geoArea.value);
                    	}
                    } else {
                    	$scope.client = null;
                    	$scope.member = data.memberObject;
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error finding the information");
        });
    };
    
    /**
     * MEMBERS
     * */
    $scope.getMember = function() {
        $.ajax({
            url: getBaseUri()+'/manage-members/member.json',
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.$apply(function(){
                    $scope.newMember = data;
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error getting emtpy group");
        });
    };

    $scope.addMember = function() {
        $.ajax({
            url: getBaseUri()+'/manage-members/create-member.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'POST',
            dataType: 'json',
            data: angular.toJson($scope.newMember),
            success: function(data){
                $scope.$apply(function(){
                    $scope.newMember = data;
                    if(data.errors.length != 0){

                    } else {
                        $scope.showSuccessModal();
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error deprecating the account");
        });
    };

    $scope.findMember = function() {
        $scope.success_edit_member_message = null;
        $.ajax({
            url: getBaseUri()+'/manage-members/find-member.json?orcidOrEmail=' + $scope.member_id,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                $scope.member = data;
                $scope.$apply();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error getting existing groups");
        });
    };

    $scope.updateMember = function() {
        $.ajax({
            url: getBaseUri()+'/manage-members/update-member.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'POST',
            dataType: 'json',
            data: angular.toJson($scope.member),
            success: function(data){
                $scope.$apply(function(){
                    if(data.errors.length == 0){
                        $scope.member = null;
                        $scope.success_edit_member_message = om.get('manage_member.edit_member.success');
                        $scope.member_id = null;
                    } else {
                        $scope.member = data;
                    }
                });
                $scope.closeModal();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error deprecating the account");
        });
    };

    /**
     * CLIENTS
     * */
    $scope.searchClient = function() {
        $scope.showError = false;
        $scope.client = null;
        $scope.success_message = null;
        $.ajax({
            url: getBaseUri()+'/manage-members/find-client.json?orcid=' + $scope.client_id,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                $scope.client = data;
                $scope.$apply();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error getting existing groups");
        });
    };

    //Load empty redirect uri
    $scope.addRedirectUri = function() {
        $.ajax({
            url: getBaseUri() + '/manage-members/empty-redirect-uri.json',
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.client.redirectUris.push(data);
                $scope.$apply();
            }
        }).fail(function() {
            console.log("Unable to fetch redirect uri scopes.");
        });
    };

    $scope.deleteRedirectUri = function($index){
        $scope.client.redirectUris.splice($index,1);
    };

    //Load the default scopes based n the redirect uri type selected
    $scope.loadDefaultScopes = function(rUri) {
        //Empty the scopes to update the default ones
        rUri.scopes = [];
        //Fill the scopes with the default scopes
        if(rUri.type.value == 'grant-read-wizard'){
            rUri.scopes.push('/orcid-profile/read-limited');
        } else if (rUri.type.value == 'import-works-wizard'){
            rUri.scopes.push('/orcid-profile/read-limited');
            rUri.scopes.push('/orcid-works/create');
        } else if (rUri.type.value == 'import-funding-wizard'){
            rUri.scopes.push('/orcid-profile/read-limited');
            rUri.scopes.push('/funding/create');
        } else if (rUri.type.value == 'import-peer-review-wizard'){
            rUri.scopes.push('/orcid-profile/read-limited');
            rUri.scopes.push('/peer-review/create');
        }
    };

    //Load the list of scopes for client redirect uris
    $scope.loadAvailableScopes = function(){
        $.ajax({
            url: getBaseUri() + '/group/developer-tools/get-available-scopes.json',
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.availableRedirectScopes = data;
            }
        }).fail(function() {
            console.log("Unable to fetch redirect uri scopes.");
        });
    };

    //Update client
    $scope.updateClient = function() {
    	var clientClone = JSON.parse(JSON.stringify($scope.client));
    	for(var i = 0; i < clientClone.redirectUris.length; i ++) {
    		clientClone.redirectUris[i].actType.value = JSON.stringify(clientClone.redirectUris[i].actType.value);
    		clientClone.redirectUris[i].geoArea.value = JSON.stringify(clientClone.redirectUris[i].geoArea.value);
    	}
        $.ajax({
            url: getBaseUri() + '/manage-members/update-client.json',
            type: 'POST',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            data: angular.toJson(clientClone),
            success: function(data) {
                if(data.errors.length == 0){
                    $scope.client = null;
                    $scope.client_id = "";
                    $scope.success_message = om.get('admin.edit_client.success');
                } else {
                    $scope.client.errors = data.errors;
                }
                $scope.$apply();
                $scope.closeModal();
            }
        }).fail(function() {
            console.log("Unable to update client.");
        });
    };

    //init
    $scope.loadAvailableScopes();
    $scope.getMember();

    /**
     * Colorbox
     * */
    //Confirm updating a client
    $scope.confirmUpdateClient = function() {
        $.colorbox({
            html : $compile($('#confirm-modal-client').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"450px" , height:"175px"});
    };

    //Confirm updating a member
    $scope.confirmUpdateMember = function() {
        $.colorbox({
            html : $compile($('#confirm-modal-member').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"450px" , height:"175px"});
    };

    //Display add member modal
    $scope.showAddMemberModal = function() {
        $scope.getMember();
        $.colorbox({
            html : $compile($('#add-new-member').html())($scope),
                onLoad: function() {
                $('#cboxClose').remove();
            }
        });

        $.colorbox.resize({width:"400px" , height:"500px"});
    };

    //Show success modal for groups
    $scope.showSuccessModal = function() {
        $.colorbox({
            html : $compile($('#new-group-info').html())($scope),
                onLoad: function() {
                $('#cboxClose').remove();
            }
        });

        $.colorbox.resize({width:"500px" , height:"500px"});
    };

    /**
     * General
     * */
    $scope.closeModal = function() {
        $.colorbox.close();
    };
    
    $scope.selectAll = function($event){
    	$event.target.select();
    };
}]);

orcidNgModule.controller('findIdsCtrl',['$scope','$compile', function findIdsCtrl($scope,$compile){
    $scope.emails = "";
    $scope.emailIdsMap = {};
    $scope.showSection = false;

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#find_ids_section').toggle();
    };

    $scope.findIds = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/find-id.json',
            type: 'POST',
            dataType: 'json',
            data: $scope.emails,
            contentType: 'application/json;charset=UTF-8',
            success: function(data){
                $scope.$apply(function(){
                    if(!$.isEmptyObject(data)) {
                        $scope.profileList = data;
                    } else {
                        $scope.profileList = null;
                    }
                    $scope.emails='';
                    $scope.showEmailIdsModal();
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error deprecating the account");
        });
    };

    $scope.showEmailIdsModal = function() {
        $.colorbox({
            html : $compile($('#email-ids-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        setTimeout(function(){$.colorbox.resize({width:"575px"});},100);
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };
}]);

orcidNgModule.controller('resetPasswordCtrl',['$scope', '$compile', function ($scope,$compile) {
    $scope.showSection = false;
    $scope.params = {orcidOrEmail:'',password:''};
    $scope.result = '';

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#reset_password_section').toggle();
    };

    $scope.randomString = function() {
        $scope.result = '';
        $.ajax({
            url: getBaseUri()+'/admin-actions/generate-random-string.json',
            type: 'GET',
            dataType: 'text',
            success: function(data){
                $scope.$apply(function(){
                    $scope.params.password=data;
                });
            }
        }).fail(function(error) {
            console.log(error);
            // something bad is happening!
            console.log("Error generating random string");
        });
    };

    $scope.resetPassword = function(){
        $scope.result = '';
        $.ajax({
            url: getBaseUri()+'/admin-actions/reset-password.json',
            type: 'POST',
            data: angular.toJson($scope.params),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'text',
            success: function(data){
                $scope.$apply(function(){
                    $scope.result=data;
                    $scope.params.orcidOrEmail='';
                    $scope.params.password='';
                });
                $scope.closeModal();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error generating random string");
        });
    };

    $scope.confirmResetPassword = function(){
        if($scope.params.orcidOrEmail != '' && $scope.params.password != '') {
            $.colorbox({
                html : $compile($('#confirm-reset-password').html())($scope),
                    scrolling: true,
                    onLoad: function() {
                    $('#cboxClose').remove();
                },
                scrolling: true
            });

            $.colorbox.resize({width:"450px" , height:"150px"});
        }
    };

    $scope.closeModal = function() {
        $scope.params.orcidOrEmail='';
        $scope.params.password='';
        $scope.result= '';
        $.colorbox.close();
    };
}]);

orcidNgModule.controller('removeSecQuestionCtrl',['$scope','$compile', function ($scope,$compile) {
    $scope.showSection = false;
    $scope.orcidOrEmail = '';
    $scope.result= '';

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#remove_security_question_section').toggle();
    };

    $scope.removeSecurityQuestion = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/remove-security-question.json',
            type: 'POST',
            data: $scope.orcidOrEmail,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'text',
            success: function(data){
                $scope.$apply(function(){
                    $scope.result=data;
                    $scope.orcid = '';
                });
                $scope.closeModal();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error generating random string");
        });
    };

    $scope.confirmRemoveSecurityQuestion = function(){
        if($scope.orcid != '') {
            $.colorbox({
                html : $compile($('#confirm-remove-security-question').html())($scope),
                    scrolling: true,
                    onLoad: function() {
                    $('#cboxClose').remove();
                },
                scrolling: true
            });

            $.colorbox.resize({width:"450px" , height:"150px"});
        }
    };

    $scope.closeModal = function() {
        $scope.orcidOrEmail = '';
        $scope.result= '';
        $.colorbox.close();
    };
}]);

orcidNgModule.controller('profileLockingCtrl', ['$scope', '$compile', function($scope, $compile){
	$scope.orcidToLock = '';
	$scope.orcidToUnlock = '';
	$scope.showLockModal = false;
	$scope.showUnlockModal = false;
	$scope.showLockPopover = false;
	$scope.profileDetails = null;
	$scope.message = '';
	
	$scope.toggleLockModal = function(){
        $scope.showLockModal = !$scope.showLockModal;
        $('#lock_modal').toggle();
    };
    
    $scope.toggleUnlockModal = function(){
        $scope.showUnlockModal = !$scope.showUnlockModal;
        $('#unlock_modal').toggle();
    };
    
    $scope.checkProfileToLock = function(){
    	$.ajax({
            url: getBaseUri()+'/admin-actions/check-account-to-lock.json',
            type: 'POST',
            data: $scope.orcidToLock,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data){            	
            	$scope.profileDetails=data;  
            	if($scope.profileDetails.errors.length) {
            		$scope.$apply();
            	}
            	else {
            		$scope.showConfirmModal(true);
            	}            		            
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error while loading info for the account to lock");
        });
    };
    
    $scope.checkProfileToUnlock = function(){
    	$.ajax({
            url: getBaseUri()+'/admin-actions/check-account-to-unlock.json',
            type: 'POST',
            data: $scope.orcidToUnlock,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data){            	
            	$scope.profileDetails=data;  
            	if($scope.profileDetails.errors.length) {
            		$scope.$apply();
            	}
            	else {
            		$scope.showConfirmModal(false);
            	}            		            
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error while loading info for the account to lock");
        });
    };
    
    $scope.showConfirmModal = function(isLockAction) {
    	$scope.showLockPopover = isLockAction;     	
        $.colorbox({
            html : $compile($('#confirm-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });
        $scope.$apply();
        $.colorbox.resize({width:"425px" , height:"285px"});
    };
    
    $scope.lockAccount = function() {
    	$.ajax({
            url: getBaseUri()+'/admin-actions/lock-account.json',
            type: 'POST',
            data: $scope.profileDetails.orcid,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'text',
            success: function(data){   
            	$scope.message = data;            	
            	$scope.orcidToLock = '';
            	$scope.$apply();
            	$scope.closeModal();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error while locking account");
        });
    };
    
    $scope.unlockAccount = function() {
    	$.ajax({
            url: getBaseUri()+'/admin-actions/unlock-account.json',
            type: 'POST',
            data: $scope.profileDetails.orcid,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'text',
            success: function(data){   
            	$scope.message = data;            	
            	$scope.orcidToUnlock = '';
            	$scope.$apply();
            	$scope.closeModal();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error while unlocking account");
        });
    };
    
    $scope.closeModal = function() {        
        $.colorbox.close();
    };
}]);

orcidNgModule.controller('profileReviewCtrl', ['$scope', '$compile', function($scope, $compile){
	$scope.orcidToReview = '';
	$scope.orcidToUnreview = '';
	$scope.showReviewModal = false;
	$scope.showUnreviewModal = false;
	$scope.showReviewPopover = false;
	$scope.profileDetails = null;
	$scope.message = '';
	
	$scope.toggleReviewModal = function(){
        $scope.showReviewModal = !$scope.showReviewModal;
        $('#review_modal').toggle();
    };
    
    $scope.toggleUnreviewModal = function(){
        $scope.showUnreviewModal = !$scope.showUnreviewModal;
        $('#unreview_modal').toggle();
    };
    
    $scope.checkProfileToReview = function(){
    	$.ajax({
            url: getBaseUri()+'/admin-actions/check-account-to-review.json',
            type: 'POST',
            data: $scope.orcidToReview,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data){            	
            	$scope.profileDetails=data;  
            	if($scope.profileDetails.errors.length) {
            		$scope.$apply();
            	}
            	else {
            		$scope.showConfirmModal(true);
            	}            		            
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error while loading info for the account to review");
        });
    };
    
    $scope.checkProfileToUnreview = function(){
    	$.ajax({
            url: getBaseUri()+'/admin-actions/check-account-to-unreview.json',
            type: 'POST',
            data: $scope.orcidToUnreview,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data){            	
            	$scope.profileDetails=data;  
            	if($scope.profileDetails.errors.length) {
            		$scope.$apply();
            	}
            	else {
            		$scope.showConfirmModal(false);
            	}            		            
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error while loading info for the account to unreview");
        });
    };
    
    $scope.showConfirmModal = function(isReviewAction) {
    	$scope.showReviewPopover = isReviewAction;     	
        $.colorbox({
            html : $compile($('#review-confirm-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });
        $scope.$apply();
        $.colorbox.resize({width:"425px" , height:"285px"});
    };
    
    $scope.reviewAccount = function() {
    	$.ajax({
            url: getBaseUri()+'/admin-actions/review-account.json',
            type: 'POST',
            data: $scope.profileDetails.orcid,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'text',
            success: function(data){   
            	$scope.message = data;            	
            	$scope.orcidToReview = '';
            	$scope.$apply();
            	$scope.closeModal();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error while reviewing account");
        });
    };
    
    $scope.unreviewAccount = function() {
    	$.ajax({
            url: getBaseUri()+'/admin-actions/unreview-account.json',
            type: 'POST',
            data: $scope.profileDetails.orcid,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'text',
            success: function(data){   
            	$scope.message = data;            	
            	$scope.orcidToUnreview = '';
            	$scope.$apply();
            	$scope.closeModal();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error while unlocking account");
        });
    };
    
    $scope.closeModal = function() {        
        $.colorbox.close();
    };
}]);

orcidNgModule.controller('lookupIdOrEmailCtrl',['$scope','$compile', function findIdsCtrl($scope,$compile){
    $scope.idOrEmails = "";
    $scope.emailIdsMap = {};
    $scope.showSection = false;

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#lookup_ids_section').toggle();
    };

    $scope.lookupIdOrEmails = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/lookup-id-or-emails.json',
            type: 'POST',
            dataType: 'text',
            data: $scope.idOrEmails,
            contentType: 'application/json;charset=UTF-8',
            success: function(data){
                $scope.$apply(function(){
                	console.log(data);
                    $scope.result = data;
                    $scope.idOrEmails='';
                    $scope.showEmailIdsModal();
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error deprecating the account");
        });
    };

    $scope.showEmailIdsModal = function() {
        $.colorbox({
            html : $compile($('#lookup-email-ids-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        setTimeout(function(){$.colorbox.resize({width:"575px"});},100);
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };
}]);

orcidNgModule.controller('ResendClaimCtrl', ['$scope', function ($scope) {
	$scope.emailIds = "";
	$scope.showSection = false;

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#batch_resend_section').toggle();
    };

	
	$scope.resendClaimEmails = function() {
		$.ajax({
            url: getBaseUri()+'/admin-actions/resend-claim.json',
            type: 'POST',
            dataType: 'json',
            data: $scope.emailIds,
            contentType: 'application/json;charset=UTF-8',
            success: function(data){
            	$scope.$apply(function(){
            		$scope.result = data;
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error re-sending claim emails");
        });
	}
}]);

orcidNgModule.controller('SSOPreferencesCtrl',['$scope', '$compile', '$sce', 'emailSrvc', function ($scope, $compile, $sce, emailSrvc) {
    $scope.noCredentialsYet = true;
    $scope.userCredentials = null;
    $scope.editing = false;
    $scope.hideGoogleUri = false;
    $scope.hideRunscopeUri = false;
    $scope.googleUri = 'https://developers.google.com/oauthplayground';
    $scope.runscopeUri = 'https://www.runscope.com/oauth_tool/callback';
    $scope.playgroundExample = '';
    $scope.googleExampleLink = 'https://developers.google.com/oauthplayground/#step1&scopes=/authenticate&oauthEndpointSelect=Custom&oauthAuthEndpointValue=[BASE_URI_ENCODE]/oauth/authorize&oauthTokenEndpointValue=[BASE_URI_ENCODE]/oauth/token&oauthClientId=[CLIENT_ID]&oauthClientSecret=[CLIENT_SECRET]&accessTokenType=bearer';
    $scope.sampleAuthCurl = '';
    $scope.sampleAuthCurlTemplate = "curl -i -L -k -H 'Accept: application/json' --data 'client_id=[CLIENT_ID]&client_secret=[CLIENT_SECRET]&grant_type=authorization_code&redirect_uri=[REDIRECT_URI]&code=REPLACE WITH OAUTH CODE' [BASE_URI]/oauth/token";
    $scope.runscopeExample = '';
    $scope.runscopeExampleLink = 'https://www.runscope.com/oauth2_tool';
    $scope.authorizeUrlBase = getBaseUri() + '/oauth/authorize';
    $scope.authorizeURLTemplate = $scope.authorizeUrlBase + '?client_id=[CLIENT_ID]&response_type=code&scope=/authenticate&redirect_uri=[REDIRECT_URI]';
    $scope.tokenURL = orcidVar.pubBaseUri + '/oauth/token';
    $scope.authorizeURL = '';
    $scope.selectedRedirectUri = '';
    $scope.creating = false;
    $scope.emailSrvc = emailSrvc;
    $scope.nameToDisplay = '';
    $scope.descriptionToDisplay = '';
    $scope.verifyEmailSent=false;
    $scope.accepted=false;
    $scope.expanded = false;    
    
    $scope.verifyEmail = function() {
        var funct = function() {
            $scope.verifyEmailObject = emailSrvc.primaryEmail;
            emailSrvc.verifyEmail(emailSrvc.primaryEmail,function(data) {
            	$scope.verifyEmailSent = true;    
            	$scope.$apply();                    
           });            
       };
       if (emailSrvc.primaryEmail == null)
              emailSrvc.getEmails(funct);
       else
           funct();
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };

    $scope.acceptTerms = function() {
    	$scope.mustAcceptTerms = false;
    	$scope.accepted = false;
    	$.colorbox({
            html : $compile($('#terms-and-conditions-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"590px"});
    };
    
    $scope.enableDeveloperTools = function() {
    	if($scope.accepted == true) {
    		$scope.mustAcceptTerms = false;
    		$.ajax({
                url: getBaseUri()+'/developer-tools/enable-developer-tools.json',
                contentType: 'application/json;charset=UTF-8',
                type: 'POST',
                success: function(data){
                    if(data == true){
                        window.location.href = getBaseUri()+'/developer-tools';
                    };
                }
            }).fail(function(error) {
                // something bad is happening!
                console.log("Error enabling developer tools");
            });
    	} else {
    		$scope.mustAcceptTerms = true;
    	}        
    };

    $scope.confirmDisableDeveloperTools = function() {
        $.colorbox({
            html : $compile($('#confirm-disable-developer-tools').html())($scope),
                onLoad: function() {
                $('#cboxClose').remove();
            }
        });
    };

    $scope.disableDeveloperTools = function() {
        $.ajax({
            url: getBaseUri()+'/developer-tools/disable-developer-tools.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'POST',
            success: function(data){
                if(data == true){
                    window.location.href = getBaseUri()+'/account';
                };
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error enabling developer tools");
        });
    };

    $scope.getSSOCredentials = function() {
        $.ajax({
            url: getBaseUri()+'/developer-tools/get-sso-credentials.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'GET',
            success: function(data){
                $scope.$apply(function(){
                    if(data != null && data.clientSecret != null) {
                        $scope.playgroundExample = '';
                        $scope.userCredentials = data;
                        $scope.hideGoogleUri = false;                        
                        $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[0];
                        for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
                            if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                                $scope.hideGoogleUri = true;
                            }

                            if($scope.userCredentials.redirectUris[i].value.value < $scope.selectedRedirectUri.value.value) {
                                $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[i];
                            }
                        }
                        $scope.updateSelectedRedirectUri();
                        $scope.setHtmlTrustedNameAndDescription();
                    } else {
                    	$scope.createCredentialsLayout();
                        $scope.noCredentialsYet = true;
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error obtaining SSO credentials");
            console.log(error);
        });
    };

    // Get an empty modal to add
    $scope.createCredentialsLayout = function(){
        $.ajax({
            url: getBaseUri() + '/developer-tools/get-empty-sso-credential.json',
            dataType: 'json',
            success: function(data) {
                $scope.$apply(function(){
                    $scope.hideGoogleUri = false;
                    $scope.creating = true;
                    $scope.userCredentials = data;
                });
            }
        }).fail(function() {
            console.log("Error fetching client");
        });
    };

    $scope.addRedirectURI = function() {
        $scope.userCredentials.redirectUris.push({value: '',type: 'default'});
        $scope.hideGoogleUri = false;
        for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
            if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                $scope.hideGoogleUri = true;
            }
        }
    };

    $scope.submit = function() {
        $.ajax({
            url: getBaseUri()+'/developer-tools/generate-sso-credentials.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'POST',
            dataType: 'json',
            data: angular.toJson($scope.userCredentials),
            success: function(data){
                $scope.$apply(function(){
                    $scope.playgroundExample = '';
                    $scope.userCredentials = data;
                    if(data.errors.length != 0){
                        //SHOW ERROR
                    } else {
                        $scope.hideGoogleUri = false;
                        $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[0];
                        for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
                            if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                                $scope.hideGoogleUri = true;
                            }

                            if($scope.userCredentials.redirectUris[i].value.value < $scope.selectedRedirectUri.value.value) {
                                $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[i];
                            }
                        }
                        $scope.updateSelectedRedirectUri();
                        $scope.setHtmlTrustedNameAndDescription();
                        $scope.creating = false;
                        $scope.noCredentialsYet = false;
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error creating SSO credentials");
        });
    };

    $scope.showRevokeModal = function() {
        $.colorbox({
            html : $compile($('#revoke-sso-credentials-modal').html())($scope),
                onLoad: function() {
                $('#cboxClose').remove();
            }
        });

        $.colorbox.resize({width:"450px" , height:"230px"});
    };

    $scope.revoke = function() {
        $.ajax({
            url: getBaseUri()+'/developer-tools/revoke-sso-credentials.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'POST',
            success: function(){
                $scope.$apply(function(){
                    $scope.userCredentials = null;
                    $scope.closeModal();
                    $scope.showReg = true;
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error revoking SSO credentials");
        });
    };

    $scope.showEditLayout = function() {
        //Hide the testing tools if they are already added
        for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
            if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                $scope.hideGoogleUri=true;
            } else if($scope.runscopeUri == $scope.userCredentials.redirectUris[i].value.value) {
                $scope.hideRunscopeUri=true;
            }
        }
        $scope.editing = true;
        $('.developer-tools .slidebox').slideDown();
        $('.tab-container .collapsed').css('display', 'none');
        $('.tab-container .expanded').css('display', 'inline').parent().css('background','#EBEBEB');
    };

    $scope.showViewLayout = function() {
        //Reset the credentials
        $scope.getSSOCredentials();
        $scope.editing = false;
        $scope.creating = false;
        $('.edit-details .slidebox').slideDown();
    };

    $scope.editClientCredentials = function() {
        $.ajax({
            url: getBaseUri()+'/developer-tools/update-user-credentials.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'POST',
            dataType: 'json',
            data: angular.toJson($scope.userCredentials),
            success: function(data){
                $scope.$apply(function(){
                    $scope.playgroundExample = '';
                    $scope.userCredentials = data;
                    if(data.errors.length != 0){
                        //SHOW ERROR
                    } else {
                        $scope.editing = false;
                        $scope.hideGoogleUri = false;
                        $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[0];
                        for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
                            if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                                $scope.hideGoogleUri = true;
                            }

                            if($scope.userCredentials.redirectUris[i].value.value < $scope.selectedRedirectUri.value.value) {
                                $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[i];
                            }
                        }

                        $scope.updateSelectedRedirectUri();
                        $scope.setHtmlTrustedNameAndDescription();
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error updating SSO credentials");
        });
    };

    $scope.deleteRedirectUri = function(idx) {
        $scope.userCredentials.redirectUris.splice(idx, 1);
        $scope.hideGoogleUri = false;
        for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
            if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                $scope.hideGoogleUri = true;
            }
        }
    };

    $scope.addTestRedirectUri = function(type) {
        var rUri = $scope.runscopeUri;
        if(type == 'google'){
            rUri = $scope.googleUri;
        }

        $.ajax({
            url: getBaseUri() + '/developer-tools/get-empty-redirect-uri.json',
            dataType: 'json',
            success: function(data) {
                data.value.value=rUri;
                $scope.$apply(function(){
                    if($scope.userCredentials.redirectUris.length == 1 && $scope.userCredentials.redirectUris[0].value.value == null) {
                        $scope.userCredentials.redirectUris[0].value.value = rUri;
                    } else {
                        $scope.userCredentials.redirectUris.push(data);
                    }
                    if(type == 'google') {
                        $scope.hideGoogleUri = true;
                    }
                });
            }
        }).fail(function() {
            console.log("Error fetching empty redirect uri");
        });
    };

    $scope.updateSelectedRedirectUri = function() {
        var clientId = $scope.userCredentials.clientOrcid.value;
        var selectedRedirectUriValue = $scope.selectedRedirectUri.value.value;
        var selectedClientSecret = $scope.userCredentials.clientSecret.value;

        //Build the google playground url example
        $scope.playgroundExample = '';

        if($scope.googleUri == selectedRedirectUriValue) {
            var example = $scope.googleExampleLink;
            example = example.replace('[BASE_URI_ENCODE]', encodeURI(getBaseUri()));
            example = example.replace('[CLIENT_ID]', clientId);
            example = example.replace('[CLIENT_SECRET]', selectedClientSecret);
            $scope.playgroundExample = example;
        }

        var example = $scope.authorizeURLTemplate;
        example = example.replace('BASE_URI]', orcidVar.baseUri);
        example = example.replace('[CLIENT_ID]', clientId);
        example = example.replace('[REDIRECT_URI]', selectedRedirectUriValue);
        $scope.authorizeURL = example;

        // rebuild sampel Auhtroization Curl
        var sampeleCurl = $scope.sampleAuthCurlTemplate;
        $scope.sampleAuthCurl = sampeleCurl.replace('[CLIENT_ID]', clientId)
            .replace('[CLIENT_SECRET]', selectedClientSecret)
            .replace('[BASE_URI]', orcidVar.baseUri)
            .replace('[REDIRECT_URI]', selectedRedirectUriValue);
    };

    $scope.confirmResetClientSecret = function() {
        $scope.clientSecretToReset = $scope.userCredentials.clientSecret;
        $.colorbox({
            html : $compile($('#reset-client-secret-modal').html())($scope),
            transition: 'fade',
            onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });
        $.colorbox.resize({width:"415px" , height:"250px"});
    };

    $scope.resetClientSecret = function() {    	
        $.ajax({
            url: getBaseUri() + '/developer-tools/reset-client-secret.json',
            type: 'POST',
            data: $scope.userCredentials.clientOrcid.value,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'text',
            success: function(data) {
                if(data) {
                    $scope.editing = false;
                    $scope.closeModal();
                    $scope.getSSOCredentials();
                } else
                    console.log('Unable to reset client secret');
            }
        }).fail(function() {
            console.log("Error resetting redirect uri");
        });
    };

    $scope.closeModal = function(){
        $.colorbox.close();
    };

    //init
    $scope.getSSOCredentials();

    $scope.setHtmlTrustedNameAndDescription = function() {
        //Trust client name and description as html since it has been already filtered
        $scope.nameToDisplay = $sce.trustAsHtml($scope.userCredentials.clientName.value);
        $scope.descriptionToDisplay = $sce.trustAsHtml($scope.userCredentials.clientDescription.value);
    };
    
    $scope.inputTextAreaSelectAll = function($event){
    	$event.target.select();
    }
    
    $scope.expand =  function(){
    	$scope.expanded = true;
    }
    
    $scope.collapse = function(){
    	$scope.expanded = false;
    }
    
    $scope.getClientUrl = function(userCredentials) {
        if(typeof userCredentials != undefined && userCredentials != null && userCredentials.clientWebsite != null && userCredentials.clientWebsite.value != null) {
            if(userCredentials.clientWebsite.value.lastIndexOf('http://') === -1 && userCredentials.clientWebsite.value.lastIndexOf('https://') === -1) {
                return '//' + userCredentials.clientWebsite.value;
            } else {
                return userCredentials.clientWebsite.value;
            }
        }
        return '';
    }
    
}]);

orcidNgModule.controller('ClientEditCtrl',['$scope', '$compile', function ($scope, $compile){
    $scope.clients = [];
    $scope.newClient = null;
    $scope.scopeSelectorOpen = false;
    $scope.selectedScopes = [];
    $scope.availableRedirectScopes = [];
    $scope.editing = false;
    $scope.creating = false;
    $scope.viewing = false;
    $scope.listing = true;
    $scope.hideGoogleUri = true;
    $scope.selectedRedirectUri = "";
    $scope.selectedScope = "";
    // Google example
    $scope.googleUri = 'https://developers.google.com/oauthplayground';
    $scope.playgroundExample = '';
    $scope.googleExampleLink = 'https://developers.google.com/oauthplayground/#step1&oauthEndpointSelect=Custom&oauthAuthEndpointValue=[BASE_URI_ENCODE]/oauth/authorize&oauthTokenEndpointValue=[BASE_URI_ENCODE]/oauth/token&oauthClientId=[CLIENT_ID]&oauthClientSecret=[CLIENT_SECRET]&accessTokenType=bearer&scope=[SCOPES]';
    // Curl example
    $scope.sampleAuthCurl = '';
    $scope.sampleAuthCurlTemplate = "curl -i -L -k -H 'Accept: application/json' --data 'client_id=[CLIENT_ID]&client_secret=[CLIENT_SECRET]&grant_type=authorization_code&redirect_uri=[REDIRECT_URI]&code=REPLACE WITH OAUTH CODE' [BASE_URI]/oauth/token";
    // Auth example
    $scope.authorizeUrlBase = getBaseUri() + '/oauth/authorize';
    $scope.authorizeURLTemplate = $scope.authorizeUrlBase + '?client_id=[CLIENT_ID]&response_type=code&redirect_uri=[REDIRECT_URI]&scope=[SCOPES]';
    // Token url
    $scope.tokenURL = orcidVar.pubBaseUri + '/oauth/token';
    $scope.expanded = false;

    // Get the list of clients associated with this user
    $scope.getClients = function(){
        $.ajax({
            url: getBaseUri() + '/group/developer-tools/get-clients.json',
            dataType: 'json',
            success: function(data) {
                $scope.$apply(function(){
                    $scope.clients = data;
                    $scope.creating = false;
                    $scope.editing = false;
                    $scope.viewing = false;
                    $scope.listing = true;
                    $scope.hideGoogleUri = false;
                });
            }
        }).fail(function() {
            alert("Error fetching clients.");
            console.log("Error fetching clients.");
        });
    };

    // Get an empty modal to add
    $scope.showAddClient = function(){
        $.ajax({
            url: getBaseUri() + '/group/developer-tools/client.json',
            dataType: 'json',
            success: function(data) {
                $scope.$apply(function() {
                    $scope.newClient = data;
                    $scope.creating = true;
                    $scope.listing = false;
                    $scope.editing = false;
                    $scope.viewing = false;
                    $scope.hideGoogleUri = false;
                });
            }
        }).fail(function() {
            console.log("Error fetching client");
        });
    };

    // Add a new uri input field to a new client
    $scope.addRedirectUriToNewClientTable = function(){
        $scope.newClient.redirectUris.push({value: {value: ''},type: {value: 'default'}, scopes: [], errors: [], actType: {value: ""}, geoArea: {value: ""}});
    };

    // Add a new uri input field to a existing client
    $scope.addUriToExistingClientTable = function(){
        $scope.clientToEdit.redirectUris.push({value: {value: ''},type: {value: 'default'}, scopes: [], errors: [], actType: {value: ""}, geoArea: {value: ""}});
    };

    // Delete an uri input field
    $scope.deleteUriOnNewClient = function(idx){
        $scope.newClient.redirectUris.splice(idx, 1);
        $scope.hideGoogleUri = false;
        if($scope.newClient.redirectUris != null && $scope.newClient.redirectUris.length > 0) {
            for(var i = 0; i < $scope.newClient.redirectUris.length; i++) {
                if($scope.newClient.redirectUris[i].value.value == $scope.googleUri) {
                    $scope.hideGoogleUri = true;
                    break;
                }
            }
        }
    };

    // Delete an uri input field
    $scope.deleteUriOnExistingClient = function(idx){
        $scope.clientToEdit.redirectUris.splice(idx, 1);
        $scope.hideGoogleUri = false;
        if($scope.clientToEdit.redirectUris != null && $scope.clientToEdit.redirectUris.length > 0) {
            for(var i = 0; i < $scope.clientToEdit.redirectUris.length; i++) {
                if($scope.clientToEdit.redirectUris[i].value.value == $scope.googleUri) {
                    $scope.hideGoogleUri = true;
                    break;
                }
            }
        }
    };

    $scope.addTestRedirectUri = function(type, edit) {
        var rUri = '';
        if(type == 'google'){
            rUri = $scope.googleUri;
        }

        $.ajax({
            url: getBaseUri() + '/developer-tools/get-empty-redirect-uri.json',
            dataType: 'json',
            success: function(data) {
                data.value.value=rUri;
                data.type.value='default';
                $scope.$apply(function(){
                    if(edit == 'true'){
                        if($scope.clientToEdit.redirectUris.length == 1 && $scope.clientToEdit.redirectUris[0].value.value == null) {
                            $scope.clientToEdit.redirectUris[0].value.value = rUri;
                        } else {
                            $scope.clientToEdit.redirectUris.push(data);
                        }
                    } else {
                        if($scope.newClient.redirectUris.length == 1 && $scope.newClient.redirectUris[0].value.value == null) {
                            $scope.newClient.redirectUris[0].value.value = rUri;
                        } else {
                            $scope.newClient.redirectUris.push(data);
                        }
                    }
                    if(type == 'google') {
                        $scope.hideGoogleUri = true;
                    }
                });
            }
        }).fail(function() {
            console.log("Error fetching empty redirect uri");
        });
    };

    // Display the modal to edit a client
    $scope.showEditClient = function(client) {
        // Copy the client to edit to a scope variable
        $scope.clientToEdit = client;
        $scope.editing = true;
        $scope.creating = false;
        $scope.listing = false;
        $scope.viewing = false;
        $scope.hideGoogleUri = false;

        if($scope.clientToEdit.redirectUris != null && $scope.clientToEdit.redirectUris.length > 0) {
            for(var i = 0; i < $scope.clientToEdit.redirectUris.length; i++) {
                if($scope.clientToEdit.redirectUris[i].value.value == $scope.googleUri) {
                    $scope.hideGoogleUri = true;
                    break;
                }
            }
        }
    };

    //Submits the client update request
    $scope.submitEditClient = function(){
        // Check which redirect uris are empty strings and remove them from the array
        for(var j = $scope.clientToEdit.length - 1; j >= 0 ; j--)    {
            if(!$scope.clientToEdit.redirectUris[j].value){
                $scope.clientToEdit.redirectUris.splice(j, 1);
            }
        }
        //Submit the update request
        $.ajax({
            url: getBaseUri() + '/group/developer-tools/edit-client.json',
            type: 'POST',
            data: angular.toJson($scope.clientToEdit),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data.errors != null && data.errors.length > 0){
                    $scope.clientToEdit = data;
                    $scope.$apply();
                } else {
                    //If everything worked fine, reload the list of clients
                    $scope.getClients();
                    $.colorbox.close();
                }
            }
        }).fail(function() {
            alert("An error occured updating the client");
            console.log("Error updating client information.");
        });
    };

    //Submits the new client request
    $scope.addClient = function(){
        // Check which redirect uris are empty strings and remove them from the array
        for(var j = $scope.newClient.redirectUris.length - 1; j >= 0 ; j--)    {
            if(!$scope.newClient.redirectUris[j].value){
                $scope.newClient.redirectUris.splice(j, 1);
            } else {
    			$scope.newClient.redirectUris[j].actType.value = JSON.stringify({"import-works-wizard" : ["Articles"]});
    			$scope.newClient.redirectUris[j].geoArea.value = JSON.stringify({"import-works-wizard" : ["Global"]});
            }
        }

        //Submit the new client request
        $.ajax({
            url: getBaseUri() + '/group/developer-tools/add-client.json',
            type: 'POST',
            data: angular.toJson($scope.newClient),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data.errors != null && data.errors.length > 0){
                    $scope.newClient = data;
                    $scope.$apply();
                } else {
                    //If everything worked fine, reload the list of clients
                    $scope.getClients();
                }
            }
        }).fail(function() {
            console.log("Error creating client information.");
        });
    };

    //Submits the updated client
    $scope.editClient = function() {
        // Check which redirect uris are empty strings and remove them from the array
        for(var j = $scope.clientToEdit.redirectUris.length - 1; j >= 0 ; j--)    {
            if(!$scope.clientToEdit.redirectUris[j].value){
                $scope.clientToEdit.redirectUris.splice(j, 1);
            } else if($scope.clientToEdit.redirectUris[j].actType.value == "") {
    			$scope.clientToEdit.redirectUris[j].actType.value = JSON.stringify({"import-works-wizard" : ["Articles"]});
    			$scope.clientToEdit.redirectUris[j].geoArea.value = JSON.stringify({"import-works-wizard" : ["Global"]});
            }
        }
        //Submit the edited client
        $.ajax({
            url: getBaseUri() + '/group/developer-tools/edit-client.json',
            type: 'POST',
            data: angular.toJson($scope.clientToEdit),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data.errors != null && data.errors.length > 0){
                    $scope.clientToEdit = data;
                    $scope.$apply();
                } else {
                    //If everything worked fine, reload the list of clients
                    $scope.getClients();
                }
            }
        }).fail(function() {
            console.log("Error editing client information.");
        });
    };

    // Display client details: Client ID and Client secret
    $scope.viewDetails = function(client) {
        // Set the client details
        $scope.clientDetails = client;
        // Set the first redirect uri selected
        if(client.redirectUris != null && client.redirectUris.length > 0) {
            $scope.selectedRedirectUri = client.redirectUris[0];
        } else {
            $scope.selectedRedirectUri = null;
        }

        $scope.editing = false;
        $scope.creating = false;
        $scope.listing = false;
        $scope.viewing = true;

        // Update the selected redirect uri
        if($scope.clientDetails != null){
            $scope.updateSelectedRedirectUri();
        }
    };

    $scope.updateSelectedRedirectUri = function() {
        var clientId = '';
        var selectedClientSecret = '';
        $scope.playgroundExample = '';
        var scope = $scope.selectedScope;

        if ($scope.clientDetails != null){
            clientId = $scope.clientDetails.clientId.value;
            selectedClientSecret = $scope.clientDetails.clientSecret.value;
        }

        if($scope.selectedRedirectUri.length != 0) {
            selectedRedirectUriValue = $scope.selectedRedirectUri.value.value;

            if($scope.googleUri == selectedRedirectUriValue) {
                var example = $scope.googleExampleLink;
                example = example.replace('[BASE_URI_ENCODE]', encodeURI(getBaseUri()));
                example = example.replace('[CLIENT_ID]', clientId);
                example = example.replace('[CLIENT_SECRET]', selectedClientSecret);
                if(scope != '')
                    example = example.replace('[SCOPES]', scope);
                $scope.playgroundExample = example.replace(/,/g,'%20');
            }

            var example = $scope.authorizeURLTemplate;
            example = example.replace('[BASE_URI]', orcidVar.baseUri);
            example = example.replace('[CLIENT_ID]', clientId);
            example = example.replace('[REDIRECT_URI]', selectedRedirectUriValue);
            if(scope != ''){
                example = example.replace('[SCOPES]', scope);
            }

            $scope.authorizeURL = example.replace(/,/g,'%20');    //replacing ,

            // rebuild sample Auhtroization Curl
            var sampleCurl = $scope.sampleAuthCurlTemplate;
            $scope.sampleAuthCurl = sampleCurl.replace('[CLIENT_ID]', clientId)
                .replace('[CLIENT_SECRET]', selectedClientSecret)
                .replace('[BASE_URI]', orcidVar.baseUri)
                .replace('[REDIRECT_URI]', selectedRedirectUriValue);
        }
    };

    $scope.showViewLayout = function() {
        $scope.editing = false;
        $scope.creating = false;
        $scope.listing = true;
        $scope.viewing = false;
    };

    //Load the list of scopes for client redirect uris
    $scope.loadAvailableScopes = function(){
        $.ajax({
            url: getBaseUri() + '/group/developer-tools/get-available-scopes.json',
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.availableRedirectScopes = data;
            }
        }).fail(function() {
            console.log("Unable to fetch redirect uri scopes.");
        });
    };


    $scope.getAvailableRedirectScopes = function() {
        var toRemove = '/authenticate';
        var result = [];

        result = jQuery.grep($scope.availableRedirectScopes, function(value) {
          return value != toRemove;
        });

        return result;
    };

    //Load the default scopes based n the redirect uri type selected
    $scope.loadDefaultScopes = function(rUri) {
        //Empty the scopes to update the default ones
        rUri.scopes = [];
        //Fill the scopes with the default scopes
        if(rUri.type.value == 'grant-read-wizard'){
            rUri.scopes.push('/orcid-profile/read-limited');
        } else if (rUri.type.value == 'import-works-wizard'){
            rUri.scopes.push('/orcid-profile/read-limited');
            rUri.scopes.push('/orcid-works/create');
        } else if (rUri.type.value == 'import-funding-wizard'){
            rUri.scopes.push('/orcid-profile/read-limited');
            rUri.scopes.push('/funding/create');
        }
    };

    //Mark an item as selected
    $scope.setSelectedItem = function(rUri){
        var scope = this.scope;
        if (jQuery.inArray( scope, rUri.scopes ) == -1) {
            rUri.scopes.push(scope);
        } else {
            rUri.scopes = jQuery.grep(rUri.scopes, function(value) {
                return value != scope;
              });
        }
        return false;
    };

    //Checks if an item is selected
    $scope.isChecked = function (rUri) {
        var scope = this.scope;
        if (jQuery.inArray( scope, rUri.scopes ) != -1) {
            return true;
        }
        return false;
    };

    // Checks if the scope checkbox should be disabled
    $scope.isDisabled = function (rUri) {
        if(rUri.type.value == 'grant-read-wizard')
            return true;
        return false;
    };

    //init
    $scope.getClients();
    $scope.loadAvailableScopes();

    $scope.confirmResetClientSecret = function() {
        $scope.resetThisClient = $scope.clientToEdit;
        $.colorbox({
            html : $compile($('#reset-client-secret-modal').html())($scope),
            transition: 'fade',
            onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });
        $.colorbox.resize({width:"415px" , height:"250px"});
    };

    $scope.resetClientSecret = function() {
        $.ajax({
            url: getBaseUri() + '/group/developer-tools/reset-client-secret.json',
            type: 'POST',
            data: $scope.resetThisClient.clientId.value,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'text',
            success: function(data) {
                if(data) {
                    $scope.editing = false;
                    $scope.creating = false;
                    $scope.listing = true;
                    $scope.viewing = false;

                    $scope.closeModal();
                    $scope.getClients();
                } else {
                    console.log('Unable to reset client secret');
                }
            }
        }).fail(function() {
            console.log("Error resetting redirect uri");
        });
    };

    $scope.closeModal = function(){
        $.colorbox.close();
    };
    
    $scope.inputTextAreaSelectAll = function($event){
    	$event.target.select();
    }
    
    $scope.expand =  function(){
    	$scope.expanded = true;
    }
    
    $scope.collapse = function(){
    	$scope.expanded = false;
    }
    
    $scope.getClientUrl = function(client) {
        if(client != null) {
            if(client.website != null){
                if(client.website.value != null) {
                    if(client.website.value.lastIndexOf('http://') === -1 && client.website.value.lastIndexOf('https://') === -1) {
                        return '//' + client.website.value;
                    } else {
                        return client.website.value;
                    }
                }
            }
        }
        return '';
    }
    
}]);

orcidNgModule.controller('CustomEmailCtrl',['$scope', '$compile',function ($scope, $compile) {
    $scope.customEmail = null;
    $scope.editedCustomEmail = null;
    $scope.customEmailList = [];
    $scope.showCreateButton = false;
    $scope.showEmailList = false;
    $scope.showCreateForm = false;
    $scope.showEditForm = false;
    $scope.clientId = null;
    
    $scope.init = function(client_id) {
    	$scope.clientId = client_id;
    	$scope.getCustomEmails();
    };
    
    $scope.getCustomEmails = function() {
        $.ajax({
            url: getBaseUri() + '/group/custom-emails/get.json?clientId=' + $scope.clientId,
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.customEmailList = [];
                $scope.showEmailList = false;
                $scope.showCreateForm = false;
                $scope.showEditForm = false;
                $scope.customEmail = null;
                $scope.editedCustomEmail = null;
                if(data != null && data.length > 0){
                    $scope.customEmailList = data;
                    $scope.showCreateForm = false;
                    $scope.showEditForm = false;
                    $scope.showEmailList = true;
                    $scope.showCreateButton = false;
                }  else {
                    $scope.showCreateButton = true;
                }
                $scope.$apply();
            }
        });
    };

    $scope.displayCreateForm = function() {
        $.ajax({
            url: getBaseUri() + '/group/custom-emails/get-empty.json?clientId=' + $scope.clientId,
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data.errors == null || data.errors.length == 0){
                    $scope.customEmail = data;
                    $scope.showCreateForm = true;
                    $scope.showEditForm = false;
                    $scope.showCreateButton = false;
                    $scope.showEmailList = false;
                    $scope.$apply();
                }
            }
        }).fail(function() {
            console.log("Error getting empty custom email.");
        });
    };

    $scope.saveCustomEmail = function() {
        $.ajax({
            url: getBaseUri() + '/group/custom-emails/create.json',
            type: 'POST',
            data: angular.toJson($scope.customEmail),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data.errors != null && data.errors.length > 0){
                    $scope.customEmail = data;
                    $scope.$apply();
                } else {
                    //If everything worked fine, reload the list of clients
                    $scope.getCustomEmails();
                }
            }
        }).fail(function() {
            alert("An error occured creating the custom email");
            console.log("An error occured creating the custom email.");
        });
    };

    $scope.showEditLayout = function(index) {
        $scope.showCreateForm = false;
        $scope.showEditForm = true;
        $scope.showCreateButton = false;
        $scope.showEmailList = false;
        $scope.editedCustomEmail = $scope.customEmailList[index];
    };

    $scope.editCustomEmail = function() {
        $.ajax({
            url: getBaseUri() + '/group/custom-emails/update.json',
            type: 'POST',
            data: angular.toJson($scope.editedCustomEmail),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data.errors != null && data.errors.length > 0){
                    $scope.editedCustomEmail = data;
                    $scope.$apply();
                } else {
                    //If everything worked fine, reload the list of clients
                    $scope.getCustomEmails();
                }
            }
        }).fail(function() {
            alert("An error occured creating the custom email");
            console.log("An error occured creating the custom email.");
        });
    };

    $scope.showViewLayout = function() {
        $scope.getCustomEmails();
    };

    $scope.confirmDeleteCustomEmail = function(index) {
        $scope.toDelete = $scope.customEmailList[index];
        $.colorbox({
            html : $compile($('#delete-custom-email').html())($scope),
            scrolling: true,
            onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"415px" , height:"175px"});
    };

    $scope.deleteCustomEmail = function(index) {
        $.ajax({
            url: getBaseUri() + '/group/custom-emails/delete.json',
            type: 'POST',
            data: angular.toJson($scope.toDelete),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data){
                    //If everything worked fine, reload the list of clients
                    $scope.getCustomEmails();
                    $scope.closeModal();
                } else {
                    console.log("Error deleting custom email");
                }
            }
        }).fail(function() {
            alert("An error occured creating the custom email");
            console.log("An error occured creating the custom email.");
        });
    };

    $scope.closeModal = function(){
        $.colorbox.close();
    };
}]);

orcidNgModule.controller('switchUserCtrl',['$scope','$compile',function ($scope,$compile){
    $scope.emails = "";
    $scope.orcidOrEmail = "";
    $scope.showSection = false;

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#switch_user_section').toggle();
    };
    
    $scope.switchUserAdmin = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/admin-switch-user?orcidOrEmail=' + $scope.orcidOrEmail,
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.$apply(function(){
                	if(!$.isEmptyObject(data)) {
                		if(!$.isEmptyObject(data.errorMessg)) {
                        	$scope.orcidMap = data;
                        	$scope.showSwitchErrorModal();
                        } else {
                        	window.location.replace("./account/admin-switch-user?orcid\=" + data.orcid);
                        }
                    } else {
                    	$scope.showSwitchInvalidModal();
                    }
                    $scope.orcidOrEmail='';
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error deprecating the account");
        });
    };
    
    $scope.showSwitchInvalidModal = function() {
    $.colorbox({
        html : $compile($('#switch-imvalid-modal').html())($scope),
            scrolling: false,
            onLoad: function() {
            $('#cboxClose').remove();
        },
        scrolling: false
    });

	    setTimeout(function(){$.colorbox.resize({width:"575px"});},100);
	};
    
    $scope.showSwitchErrorModal = function() {
        $.colorbox({
            html : $compile($('#switch-error-modal').html())($scope),
                scrolling: false,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: false
        });

        setTimeout(function(){$.colorbox.resize({width:"575px"});},100);
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };

}]);

orcidNgModule.controller('SocialNetworksCtrl',['$scope',function ($scope){
    $scope.twitter=false;

    $scope.checkTwitterStatus = function(){
        $.ajax({
            url: getBaseUri() + '/manage/twitter/check-twitter-status',
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'text',
            success: function(data) {
                if(data == "true")
                    $scope.twitter = true;
                else
                    $scope.twitter = false;
                $scope.$apply();
            }
        }).fail(function(){
            console.log("Unable to fetch user twitter status");
        });
    };

    $scope.updateTwitter = function() {
        if($scope.twitter == true) {
            $.ajax({
                url: getBaseUri() + '/manage/twitter',
                type: 'POST',
                contentType: 'application/json;charset=UTF-8',
                dataType: 'text',
                success: function(data) {
                    window.location = data;
                }
            }).fail(function() {
                console.log("Unable to enable twitter");
            });
        } else {
            $.ajax({
                url: getBaseUri() + '/manage/disable-twitter',
                type: 'POST',
                contentType: 'application/json;charset=UTF-8',
                dataType: 'text',
                success: function(data) {
                    if(data == "true"){
                        $scope.twitter = false;
                    } else {
                        $scope.twitter = true;
                    }

                    $scope.$apply();
                }
            }).fail(function() {
                console.log("Unable to disable twitter");
            });
        }
    };

    //init
    $scope.checkTwitterStatus();
}]);

orcidNgModule.controller('adminDelegatesCtrl',['$scope',function ($scope){
    $scope.showSection = false;
    $scope.managed_verified = false;
    $scope.trusted_verified = false;
    $scope.success = false;
    $scope.request = {trusted : {errors: [], value: ''}, managed : {errors: [], value: ''}};

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#delegates_section').toggle();
    };

    $scope.checkClaimedStatus = function (whichField){
        var orcidOrEmail = '';
        if(whichField == 'trusted') {
            $scope.trusted_verified = false;
            orcidOrEmail = $scope.request.trusted.value;
        } else {
            $scope.managed_verified = false;
            orcidOrEmail = $scope.request.managed.value;
        }

        $.ajax({
            url: getBaseUri()+'/admin-actions/admin-delegates/check-claimed-status.json?orcidOrEmail=' + orcidOrEmail,
            type: 'GET',
            dataType: 'json',
            success: function(data){
                    if(data) {
                        if(whichField == 'trusted') {
                            $scope.trusted_verified = true;
                        } else {
                            $scope.managed_verified = true;
                        }
                        $scope.$apply();
                    }
                }
            }).fail(function(error) {
                // something bad is happening!
                console.log("Error getting account details for: " + orcid);
            });
    };

    $scope.confirmDelegatesProcess = function() {
        $scope.success = false;
        $.ajax({
            url: getBaseUri()+'/admin-actions/admin-delegates',
            type: 'POST',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            data: angular.toJson($scope.request),
            success: function(data){
                    $scope.request = data;
                    if(data.successMessage) {
                        $scope.success = true;
                    }
                    $scope.$apply();
                }
            }).fail(function(error) {
                // something bad is happening!
                console.log("Error getting delegates request");
            });
    };
}]);

orcidNgModule.controller('OauthAuthorizationController',['$scope', '$compile', '$sce', 'commonSrvc', 'vcRecaptchaService', function ($scope, $compile, $sce, commonSrvc, vcRecaptchaService){
    $scope.showClientDescription = false;
    $scope.showRegisterForm = true;
    $scope.isOrcidPresent = false;
    $scope.authorizationForm = {};
    $scope.registrationForm = {};
    $scope.emailTrustAsHtmlErrors = [];
    $scope.enablePersistentToken = true;
    $scope.showLongDescription = {};
    $scope.recaptchaWidgetId = null;
    $scope.recatchaResponse = null;
    $scope.personalLogin = true;
    $scope.scriptsInjected = false;
    $scope.counter = 0;
    $scope.requestInfoForm = null;    
    $scope.showBulletIcon = false;
    $scope.showCreateIcon = false;
    $scope.showLimitedIcon = false;    
    $scope.showUpdateIcon = false;    
    $scope.gaString = null;
    
    $scope.model = {
		key: orcidVar.recaptchaKey
    };
    
    $scope.toggleClientDescription = function() {
        $scope.showClientDescription = !$scope.showClientDescription;
    };
    
    $scope.loadRequestInfoForm = function() {
    	$.ajax({
            url: getBaseUri() + '/oauth/custom/authorize/get_request_info_form.json',
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
            	angular.forEach(data.scopes, function (scope) {
            		if(scope.value.endsWith('/create')) {
            			$scope.showCreateIcon = true;
            		} else if(scope.value.endsWith('/update')) {
            			$scope.showUpdateIcon = true;
            		} else if(scope.value.endsWith('/read-limited')) {
            			$scope.showLimitedIcon = true;
            		} else {
            			$scope.showBulletIcon = true;
            		}
            	})
            	            	            	            		            		            	
            	$scope.requestInfoForm = data;            	
            	$scope.gaString = orcidGA.buildClientString($scope.requestInfoForm.memberName, $scope.requestInfoForm.clientName);            	
            	$scope.$apply();
            }
        }).fail(function() {
            console.log("An error occured initializing the form.");
        });
    };         
    
    //---------------------
    //-LOGIN AND AUTHORIZE-
    //---------------------
    $scope.loadAndInitLoginForm = function() {
        $scope.isOrcidPresent = false;
        $.ajax({
            url: getBaseUri() + '/oauth/custom/authorize/empty.json',
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.authorizationForm = data;                                
                if($scope.authorizationForm.userName.value) {
                	$scope.isOrcidPresent = true;
                    $scope.showRegisterForm = false;                    
                }
                // #show_login - legacy fragment id, we should remove this sometime
                // after November 2014 and only support &show_login=true
                if(window.location.href.endsWith('#show_login'))
                    $scope.showRegisterForm = false;
                else if(!$scope.isOrcidPresent)
                    $scope.showRegisterForm = !orcidVar.showLogin;                
                
                $scope.$apply();
            }
        }).fail(function() {
            console.log("An error occured initializing the form.");
        });
    };

    $scope.loginAndAuthorize = function() {
        $scope.authorizationForm.approved = true;
        //Fire GA sign-in-submit
        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit' , 'OAuth ' + $scope.gaString]);
        $scope.submitLogin();
    };
    
    $scope.loginSocial = function(idp) {
        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit' , 'OAuth ' + $scope.gaString]);
        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit-Social', idp ]);
        return false;
    };

    $scope.loginAndDeny = function() {
        $scope.authorizationForm.approved = false;
        $scope.submitLogin();
    };

    $scope.submitLogin = function() {
        var auth_scope_prefix = 'Authorize_';
        if($scope.enablePersistentToken) {
            $scope.authorizationForm.persistentTokenEnabled=true;
            auth_scope_prefix = 'AuthorizeP_';
        }        
        $.ajax({
            url: getBaseUri() + '/oauth/custom/login.json',
            type: 'POST',
            data: angular.toJson($scope.authorizationForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data) {
                    if(data.errors.length != 0) {
                        $scope.authorizationForm = data;
                        $scope.$apply();
                    } else {
                        //Fire google GA event
                        if($scope.authorizationForm.approved) {
                            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In' , 'OAuth ' + $scope.gaString]);
                            for(var i = 0; i < $scope.requestInfoForm.scopes.length; i++) {
                                orcidGA.gaPush(['send', 'event', 'RegGrowth', auth_scope_prefix + $scope.requestInfoForm.scopes[i].name, 'OAuth ' + $scope.gaString]);
                            }
                        } else {
                            //Fire GA authorize-deny
                            orcidGA.gaPush(['send', 'event', 'Disengagement', 'Authorize_Deny', 'OAuth ' + $scope.gaString]);
                        }
                        orcidGA.windowLocationHrefDelay(data.redirectUrl);
                    }
                } else {
                    console.log("Error authenticating the user");
                }

            }
        }).fail(function() {
            console.log("An error occured authenticating the user.");
        });
    };

    //------------------------
    //-REGISTER AND AUTHORIZE-
    //------------------------
    $scope.loadAndInitRegistrationForm = function() {
        $.ajax({
            url: getBaseUri() + '/oauth/custom/register/empty.json',
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.registrationForm = data;                            
                if($scope.registrationForm.email.value && !$scope.isOrcidPresent)
                    $scope.showRegisterForm = true;
                $scope.$apply();
            }
        }).fail(function() {
            console.log("An error occured initializing the registration form.");
        });
    };

    $scope.registerAndAuthorize = function() {
        $scope.registrationForm.approved = true;
        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration-Submit' , 'OAuth ' + $scope.gaString]);
        $scope.register();
    };

    $scope.registerAndDeny = function() {
        $scope.registrationForm.approved = false;
        $scope.register();
    };

    $scope.register = function() {
        if($scope.enablePersistentToken)
            $scope.registrationForm.persistentTokenEnabled=true;
        $scope.registrationForm.grecaptcha.value = $scope.recatchaResponse; //Adding the response to the register object
        $scope.registrationForm.grecaptchaWidgetId.value = $scope.recaptchaWidgetId;
        
        $.ajax({
            url: getBaseUri() + '/oauth/custom/register.json',
            type: 'POST',
            data:  angular.toJson($scope.registrationForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.registrationForm = data;
                if($scope.registrationForm.approved) {
                    if ($scope.registrationForm.errors == undefined || $scope.registrationForm.errors.length == 0) {
                        $scope.showProcessingColorBox();
                        $scope.getDuplicates();
                    } else {
                        if($scope.registrationForm.email.errors.length > 0) {
                            for(var i = 0; i < $scope.registrationForm.email.errors.length; i++){
                                $scope.emailTrustAsHtmlErrors[0] = $sce.trustAsHtml($scope.registrationForm.email.errors[i]);
                            }
                        } else {
                            $scope.emailTrustAsHtmlErrors = [];
                        }
                    }
                } else {
                    //Fire GA register deny
                    orcidGA.gaPush(['send', 'event', 'Disengagement', 'Authorize_Deny', 'OAuth ' + $scope.gaString]);
                    orcidGA.windowLocationHrefDelay($scope.registrationForm.redirectUri.value);
                }

                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("RegistrationCtrl.postRegister() error");
        });
    };

    $scope.getDuplicates = function(){
        $.ajax({
            url: getBaseUri() + '/dupicateResearcher.json?familyNames=' + $scope.registrationForm.familyNames.value + '&givenNames=' + $scope.registrationForm.givenNames.value,
            dataType: 'json',
            success: function(data) {
                   $scope.duplicates = data;
                $scope.$apply();
                if ($scope.duplicates.length > 0 ) {
                    $scope.showDuplicatesColorBox();
                } else {
                    $scope.postRegisterConfirm();
                }
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching dupicateResearcher.json");
            // continue to registration, as solr dup lookup failed.
            $scope.postRegisterConfirm();
        });
    };

    $scope.showDuplicatesColorBox = function () {
        $.colorbox({
            html : $compile($('#duplicates').html())($scope),
            escKey:false,
            overlayClose:false,
            transition: 'fade',
            close: '',
            scrolling: true
                    });
        $scope.$apply();
        $.colorbox.resize({width:"780px" , height:"400px"});
    };

    $scope.postRegisterConfirm = function () {
        var auth_scope_prefix = 'Authorize_';
        if($scope.enablePersistentToken)
            auth_scope_prefix = 'AuthorizeP_';
        $scope.showProcessingColorBox();
        
        $.ajax({
            url: getBaseUri() + '/oauth/custom/registerConfirm.json',
            type: 'POST',
            data:  angular.toJson($scope.registrationForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
            	$scope.requestInfoForm = data;
                orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'OAuth '+ $scope.gaString]);
                if($scope.registrationForm.approved) {
                    for(var i = 0; i < $scope.requestInfoForm.scopes.length; i++) {
                        orcidGA.gaPush(['send', 'event', 'RegGrowth', auth_scope_prefix + $scope.requestInfoForm.scopes[i].name, 'OAuth ' + $scope.gaString]);
                    }
                } else {
                    //Fire GA register deny
                    orcidGA.gaPush(['send', 'event', 'Disengagement', 'Authorize_Deny', 'OAuth ' + $scope.gaString]);
                }
                orcidGA.windowLocationHrefDelay($scope.requestInfoForm.redirectUrl);
            }
        }).fail(function() {
            // something bad is happening!
            console.log("OauthAuthorizationController.postRegister() error");
        });
    };

    $scope.serverValidate = function (field) {
        if (field === undefined) field = '';
        $.ajax({
            url: getBaseUri() + '/oauth/custom/register/validate' + field + '.json',
            type: 'POST',
            data:  angular.toJson($scope.registrationForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                commonSrvc.copyErrorsLeft($scope.registrationForm, data);
                if(field == 'Email') {
                    if ($scope.registrationForm.email.errors.length > 0) {
                        for(var i = 0; i < $scope.registrationForm.email.errors.length; i++){
                            $scope.emailTrustAsHtmlErrors[0] = $sce.trustAsHtml($scope.registrationForm.email.errors[i]);
                        }
                    } else {
                        $scope.emailTrustAsHtmlErrors = [];
                    }
                }
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("OauthAuthorizationController.serverValidate() error");
        });
    };

    $scope.updateActivitiesVisibilityDefault = function(priv, $event) {
        $scope.registrationForm.activitiesVisibilityDefault.visibility = priv;
    };

    //------------------------
    //------ AUTHORIZE -------
    //------------------------
    $scope.loadAndInitAuthorizationForm = function() {
        $.ajax({
            url: getBaseUri() + '/oauth/custom/authorize/empty.json',
            type: 'GET',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.authorizationForm = data;
            }
        }).fail(function() {
            console.log("An error occured initializing the form.");
        });
    };

    $scope.authorize = function() {
        $scope.authorizationForm.approved = true;
        $scope.authorizeRequest();
    };

    $scope.deny = function() {
        $scope.authorizationForm.approved = false;
        orcidGA.gaPush(['send', 'event', 'Disengagement', 'Authorize_Deny', 'OAuth ' + $scope.gaString]);
        $scope.authorizeRequest();
    };

    $scope.authorizeRequest = function() {
        var auth_scope_prefix = 'Authorize_';
        if($scope.enablePersistentToken) {
            $scope.authorizationForm.persistentTokenEnabled=true;
            auth_scope_prefix = 'AuthorizeP_';
        }
        var is_authorize = $scope.authorizationForm.approved;
        $.ajax({
            url: getBaseUri() + '/oauth/custom/authorize.json',
            type: 'POST',
            data: angular.toJson($scope.authorizationForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(is_authorize) {
                    for(var i = 0; i < $scope.requestInfoForm.scopes.length; i++) {
                        orcidGA.gaPush(['send', 'event', 'RegGrowth', auth_scope_prefix + $scope.requestInfoForm.scopes[i].name, 'OAuth ' + $scope.gaString]);
                    }
                }
                orcidGA.windowLocationHrefDelay(data.redirectUrl);
            }
        }).fail(function() {
            console.log("An error occured authorizing the user.");
        });
    };

    //------------------
    //------COMMON------
    //------------------
    $scope.switchForm = function() {
        $scope.showRegisterForm = !$scope.showRegisterForm;
        if (!$scope.personalLogin) 
        	$scope.personalLogin = true;
    };

    $scope.showProcessingColorBox = function () {
        $.colorbox({
            html : $('<div style="font-size: 50px; line-height: 60px; padding: 20px; text-align:center">' + om.get('common.processing') + '&nbsp;<i id="ajax-loader" class="glyphicon glyphicon-refresh spin green"></i></div>'),
            width: '400px',
            height:"100px",
            close: '',
            escKey:false,
            overlayClose:false,
            onComplete: function() {
                $.colorbox.resize({width:"400px" , height:"100px"});
            }
        });
    };

    $scope.showToLoginForm = function() {
        $scope.authorizationForm.userName.value=$scope.registrationForm.email.value;
        $scope.showRegisterForm = false;
    };

    $scope.toggleLongDescription = function(orcid_scope) {    	    	
    	$scope.showLongDescription[orcid_scope] = !$scope.showLongDescription[orcid_scope];
    };

    document.onkeydown = function(e) {
	    e = e || window.event;
	    if (e.keyCode == 13) {	    	
	    	if ( typeof location.search.split('client_id=')[1] == 'undefined' ){ //There is no clientID information		    	
	    		if ($scope.showRegisterForm == true){
		    		$scope.registerAndAuthorize();		    		
		    	} else{
		    		$scope.loginAndAuthorize();		    		
		    	}		    	
			} else{
	    		$scope.authorize();
	    	}
	    }
    };
    
    //---------------------
    //------Recaptcha------
    //---------------------   
    $scope.setRecaptchaWidgetId = function (widgetId) {
        $scope.recaptchaWidgetId = widgetId;        
    };

    $scope.setRecatchaResponse = function (response) {
        $scope.recatchaResponse = response;        
    };
    
    //------------------------
    //------OAuth Layout------
    //------------------------
    $scope.showPersonalLogin = function () {        
        $scope.personalLogin = true;
    };
    
    $scope.showInstitutionLogin = function () {
        $scope.personalLogin = false; //Hide Personal Login
        
        if(!$scope.scriptsInjected){ //If shibboleth scripts haven't been loaded yet.            
            $scope.addScript('/static/javascript/shibboleth-embedded-ds/1.1.0/idpselect_config.js', function(){
                $scope.addScript('/static/javascript/shibboleth-embedded-ds/1.1.0/idpselect.js', function(){
                    $scope.scriptsInjected = true;
                    $scope.$apply();
                    addShibbolethGa($scope.gaString);
                });
            });
        };
    };
    
    $scope.addScript = function(url, onLoadFunction){        
        var head = document.getElementsByTagName('head')[0];
        var script = document.createElement('script');
        script.src = getBaseUri() + url + '?v=' + orcidVar.version;
        script.onload =  onLoadFunction;
        head.appendChild(script); //Inject the script
    };
    
    //Init
    $scope.loadRequestInfoForm();    
    
}]);

orcidNgModule.controller('LoginLayoutController',['$scope', function ($scope){
    
    $scope.personalLogin = true; //Flag to show or not Personal or Institution Account Login
    $scope.scriptsInjected = false; //Flag to show or not the spinner
    $scope.counter = 0; //To hide the spinner when the second script has been loaded, not the first one.
    
    $scope.showPersonalLogin = function () {        
        $scope.personalLogin = true;        
    };
    
    $scope.showInstitutionLogin = function () {
        $scope.personalLogin = false; //Hide Personal Login
        
        if(!$scope.scriptsInjected){ //If shibboleth scripts haven't been loaded yet.            
            $scope.addScript('/static/javascript/shibboleth-embedded-ds/1.1.0/idpselect_config.js', function(){
                $scope.addScript('/static/javascript/shibboleth-embedded-ds/1.1.0/idpselect.js', function(){
                    $scope.scriptsInjected = true;
                    $scope.$apply();
                    addShibbolethGa($scope.gaString);
                });
            });
        };
    };
    
    $scope.addScript = function(url, onLoadFunction){        
        var head = document.getElementsByTagName('head')[0];
        var script = document.createElement('script');
        script.src = getBaseUri() + url + '?v=' + orcidVar.version;
        script.onload =  onLoadFunction;
        head.appendChild(script); //Inject the script
    };
    
    $scope.loginSocial = function(idp) {
        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit-Social', idp]);
        return false;
    };
    
}]);

orcidNgModule.controller('LinkAccountController',['$scope', 'discoSrvc', function ($scope, discoSrvc){
    
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


orcidNgModule.controller('EmailsCtrl',['$scope', 'emailSrvc', '$compile','prefsSrvc' ,function ($scope, emailSrvc, $compile, prefsSrvc){
	$scope.emailSrvc = emailSrvc;
	$scope.showEdit = false;
	$scope.showElement = {};

	emailSrvc.getEmails();
	
	$scope.openEdit = function(){
		$scope.showEdit = true;
	}
	
	$scope.close = function(){	    
		$scope.showEdit = false;
		prefsSrvc.saved = false;
	    $.colorbox.close();
	}
	    
	$scope.showTooltip = function(elem){
		$scope.showElement[elem] = true;
	}
	
	$scope.hideTooltip = function(elem){
		$scope.showElement[elem] = false;
	}
	
	$scope.openEditModal = function(){
	    var HTML = '<div class="lightbox-container"><div class="edit-record-emails"><div class="row bottomBuffer"><div class="col-md-12 col-sm-12 col-xs-12"><h1 class="lightbox-title pull-left"> Edit Emails </h1> </div></div><div class="row"> <div class="col-md-12 col-xs-12 col-sm-12"><table class="settings-table"><tr>' +
	    $('#edit-emails').html() +
	    '</tr></table></div></div><div class="row"><div class="col-md-12 col-sm-12 col-xs-12"><a ng-click="close()" class="cancel-option pull-right">'+om.get("manage.email.close")+'</a></div></div></div></div>';  
	    
	    $scope.emailSrvc.popUp = true;
	    
        $.colorbox({
            scrolling: true,
            html: $compile(HTML)($scope),
            onLoad: function() {                
                $('#cboxClose').remove();
            },
            width: formColorBoxResize(),
            onComplete: function() {
                $.colorbox.resize();
            },
            onClosed: function() {
                $scope.emailSrvc.popUp = false;        
            }            
        });
    }
	
}]);


/*Angular Multi-selectbox*/
angular.module('ui.multiselect', [])

.factory('optionParser', ['$parse', function ($parse) {

    //                      00000111000000000000022200000000000000003333333333333330000000000044000
    var TYPEAHEAD_REGEXP = /^\s*(.*?)(?:\s+as\s+(.*?))?\s+for\s+(?:([\$\w][\$\w\d]*))\s+in\s+(.*)$/;

    return {
      parse: function (input) {

        var match = input.match(TYPEAHEAD_REGEXP), modelMapper, viewMapper, source;
        if (!match) {
          throw new Error(
            "Expected typeahead specification in form of '_modelValue_ (as _label_)? for _item_ in _collection_'" +
              " but got '" + input + "'.");
        }

        return {
          itemName: match[3],
          source: $parse(match[4]),
          viewMapper: $parse(match[2] || match[1]),
          modelMapper: $parse(match[1])
        };
      }
    };
  }])

  .directive('multiselect', ['$parse', '$document', '$compile', '$interpolate', 'optionParser',

    function ($parse, $document, $compile, $interpolate, optionParser) {
      return {
        restrict: 'E',
        require: 'ngModel',
        link: function (originalScope, element, attrs, modelCtrl) {

          var exp = attrs.options,
            parsedResult = optionParser.parse(exp),
            isMultiple = attrs.multiple ? true : false,
            required = false,
            scope = originalScope.$new(),
            changeHandler = attrs.change || angular.noop;

          scope.items = [];
          scope.header = 'Select';
          scope.multiple = isMultiple;
          scope.disabled = false;

          originalScope.$on('$destroy', function () {
            scope.$destroy();
          });

          var popUpEl = angular.element('<multiselect-popup></multiselect-popup>');

          //required validator
          if (attrs.required || attrs.ngRequired) {
            required = true;
          }
          attrs.$observe('required', function(newVal) {
            required = newVal;
          });

          //watch disabled state
          scope.$watch(function () {
            return $parse(attrs.disabled)(originalScope);
          }, function (newVal) {
            scope.disabled = newVal;
          });

          //watch single/multiple state for dynamically change single to multiple
          scope.$watch(function () {
            return $parse(attrs.multiple)(originalScope);
          }, function (newVal) {
            isMultiple = newVal || false;
          });

          //watch option changes for options that are populated dynamically
          scope.$watch(function () {
            return parsedResult.source(originalScope);
          }, function (newVal) {
            if (angular.isDefined(newVal))
              parseModel();
          }, true);

          //watch model change
          scope.$watch(function () {
            return modelCtrl.$modelValue;
          }, function (newVal, oldVal) {
            //when directive initialize, newVal usually undefined. Also, if model value already set in the controller
            //for preselected list then we need to mark checked in our scope item. But we don't want to do this every time
            //model changes. We need to do this only if it is done outside directive scope, from controller, for example.
            if (angular.isDefined(newVal)) {
              markChecked(newVal);
              scope.$eval(changeHandler);
            }
            getHeaderText();
            modelCtrl.$setValidity('required', scope.valid());
          }, true);

          function parseModel() {
            scope.items.length = 0;
            var model = parsedResult.source(originalScope);
            if(!angular.isDefined(model)) return;
            for (var i = 0; i < model.length; i++) {
              var local = {};
              local[parsedResult.itemName] = model[i];
              scope.items.push({
                label: parsedResult.viewMapper(local),
                model: parsedResult.modelMapper(local),
                checked: false
              });
            }
          }

          parseModel();

          element.append($compile(popUpEl)(scope));

          function getHeaderText() {
            if (is_empty(modelCtrl.$modelValue)) return scope.header = attrs.msHeader || 'Select';

              if (isMultiple) {
                  if (attrs.msSelected) {
                      scope.header = $interpolate(attrs.msSelected)(scope);
                  } else {
                      scope.header = modelCtrl.$modelValue.length + ' ' + 'selected';
                  }

            } else {
              var local = {};
              local[parsedResult.itemName] = modelCtrl.$modelValue;
              scope.header = parsedResult.viewMapper(local);
            }
          }

          function is_empty(obj) {
            if (!obj) return true;
            if (obj.length && obj.length > 0) return false;
            for (var prop in obj) if (obj[prop]) return false;
            return true;
          };

          scope.valid = function validModel() {
            if(!required) return true;
            var value = modelCtrl.$modelValue;
            return (angular.isArray(value) && value.length > 0) || (!angular.isArray(value) && value != null);
          };

          function selectSingle(item) {
            if (item.checked) {
              scope.uncheckAll();
            } else {
              scope.uncheckAll();
              item.checked = !item.checked;
            }
            setModelValue(false);
          }

          function selectMultiple(item) {
            item.checked = !item.checked;
            setModelValue(true);
          }

          function setModelValue(isMultiple) {
            var value;

            if (isMultiple) {
              value = [];
              angular.forEach(scope.items, function (item) {
                if (item.checked) value.push(item.model);
              })
            } else {
              angular.forEach(scope.items, function (item) {
                if (item.checked) {
                  value = item.model;
                  return false;
                }
              })
            }
            modelCtrl.$setViewValue(value);
          }

          function markChecked(newVal) {
            if (!angular.isArray(newVal)) {
              angular.forEach(scope.items, function (item) {
                if (angular.equals(item.model, newVal)) {
                  scope.uncheckAll();
                  item.checked = true;
                  setModelValue(false);
                  return false;
                }
              });
            } else {
              angular.forEach(scope.items, function (item) {
                item.checked = false;
                angular.forEach(newVal, function (i) {
                  if (angular.equals(item.model, i)) {
                    item.checked = true;
                  }
                });
              });
            }
          }

          scope.checkAll = function () {
            if (!isMultiple) return;
            angular.forEach(scope.items, function (item) {
              item.checked = true;
            });
            setModelValue(true);
          };

          scope.uncheckAll = function () {
            angular.forEach(scope.items, function (item) {
              item.checked = false;
            });
            setModelValue(true);
          };

          scope.select = function (item) {
            if (isMultiple === false) {
              selectSingle(item);
              scope.toggleSelect();
            } else {
              selectMultiple(item);
            }
          }
        }
      };
    }])

  .directive('multiselectPopup', ['$compile','$document','$templateCache', function ($compile, $document, $templateCache) {
    return {
      restrict: 'E',
      scope: false,
      replace: true,
      template: $templateCache.get('multiselect'),
      link: function (scope, element, attrs) {

        scope.isVisible = false;

        scope.toggleSelect = function () {
          if (element.hasClass('open')) {
            element.removeClass('open');
            $document.unbind('click', clickHandler);
          } else {
            element.addClass('open');
            $document.bind('click', clickHandler);
            scope.focus();
          }
        };

        function clickHandler(event) {
          if (elementMatchesAnyInArray(event.target, element.find(event.target.tagName)))
            return;
          element.removeClass('open');
          $document.unbind('click', clickHandler);
          scope.$apply();
        }

        scope.focus = function focus(){
          var searchBox = element.find('input')[0];
          searchBox.focus();
        }

        var elementMatchesAnyInArray = function (element, elementArray) {
          for (var i = 0; i < elementArray.length; i++)
            if (element == elementArray[i])
              return true;
          return false;
        }
      }
    }
  }]);


orcidNgModule.controller('headerCtrl',['$scope', '$window', function ($scope, $window){	
	
	$scope.searchFilterChanged = false;
	$scope.filterActive = false;
	$scope.conditionsActive = false;
	$scope.menuVisible = false;
	$scope.secondaryMenuVisible = {};
	$scope.tertiaryMenuVisible = {};
	$scope.searchVisible = false;
	$scope.settingsVisible = false;
	
	$scope.searchFocus = function(){
		$scope.filterActive = true;
		$scope.conditionsActive = true;
	}
	
	$scope.searchBlur = function(){		
		$scope.hideSearchFilter();
		$scope.conditionsActive = false;		
	}
	
	$scope.filterChange = function(){
		$scope.searchFilterChanged = true;
	}
	
	$scope.hideSearchFilter = function(){
		var searchInputValue = document.getElementById("search-input").value;
		if (searchInputValue === ""){
			setTimeout(function() {
                if ($scope.searchFilterChanged === false) {
                	$scope.filterActive = false;
                }
            }, 3000);
		}
	}
	
	
	$scope.toggleMenu = function(){
		$scope.menuVisible = !$scope.menuVisible;
		$scope.searchVisible = false;
		$scope.settingsVisible = false;		
	}
	
	$scope.toggleSecondaryMenu = function(submenu){
		$scope.secondaryMenuVisible[submenu] = !$scope.secondaryMenuVisible[submenu];
	}
	
	$scope.toggleTertiaryMenu = function(submenu){
		$scope.tertiaryMenuVisible[submenu] = !$scope.tertiaryMenuVisible[submenu];
	}
	
	$scope.toggleSearch = function(){
		$scope.searchVisible = !$scope.searchVisible;
		$scope.menuVisible = false;		
		$scope.settingsVisible = false;
	}
	
	$scope.toggleSettings = function(){
		$scope.settingsVisible = !$scope.settingsVisible;
		$scope.menuVisible = false;
		$scope.searchVisible = false;
	}	
	
	$scope.handleMobileMenuOption = function($event){
		$event.preventDefault();
		var w = getWindowWidth();			
		if(w > 767) {				
			window.location = $event.target.getAttribute('href');
		}
	}
	
}]);

orcidNgModule.controller('widgetCtrl',['$scope', 'widgetSrvc', function ($scope, widgetSrvc){
	$scope.hash = orcidVar.orcidIdHash.substr(0, 6);
	$scope.showCode = false;
	$scope.widgetSrvc = widgetSrvc;
	
	$scope.widgetURLND = '<div style="width:100%;text-align:center"><iframe src="'+ getBaseUri() + '/static/html/widget.html?orcid=' + orcidVar.orcidId + '&t=' + $scope.hash + '&locale=' + $scope.widgetSrvc.locale + '" frameborder="0" height="310" width="210px" vspace="0" hspace="0" marginheight="5" marginwidth="5" scrolling="no" allowtransparency="true"></iframe></div>';
	
	$scope.inputTextAreaSelectAll = function($event){
    	$event.target.select();
    }
	
	$scope.toggleCopyWidget = function(){
		$scope.showCode = !$scope.showCode;
	}
	
	$scope.hideWidgetCode = function(){
		$scope.showCode = false;
	}
	
}]);

orcidNgModule.controller('PublicRecordCtrl',['$scope', '$compile',function ($scope, $compile) {
	$scope.showSources = new Array();
	$scope.showPopover = new Array();
	$scope.toggleSourcesDisplay = function(section){		
		$scope.showSources[section] = !$scope.showSources[section];		
	}
	
	$scope.showPopover = function(section){
		$scope.showPopover[section] = true;
	}	
	
	$scope.hidePopover = function(section){
		$scope.showPopover[section] = false;	
	}
}]);

orcidNgModule.directive('resize', function ($window) {
	return function ($scope, element) {
		var w = angular.element($window);
		/* Only used for detecting window resizing, the value returned by w.width() is not accurate, please refer to getWindowWidth() */
		$scope.getWindowWidth = function () {
			return { 'w': getWindowWidth() };
		};
		$scope.$watch($scope.getWindowWidth, function (newValue, oldValue) {			
            
			$scope.windowWidth = newValue.w;
			
            
            if($scope.windowWidth > 767){ /* Desktop view */
            	$scope.menuVisible = true;
            	$scope.searchVisible = true;
            	$scope.settingsVisible = true;
            }else{
            	$scope.menuVisible = false;
            	$scope.searchVisible = false;
            	$scope.settingsVisible = false;
            }
            
		}, true);
	
		w.bind('resize', function () {
			$scope.$apply();
		});
	}
});

orcidNgModule.filter('formatBibtexOutput', function () {
    return function (text) {
		var str = text.replace(/[\-?_?]/, ' ');
		return str.toUpperCase();
    };
});


orcidNgModule.filter('orderObjectBy', function() {
	  return function(items, field, reverse) {
	    var filtered = [];
	    angular.forEach(items, function(item) {
	      filtered.push(item);
	    });
	    filtered.sort(function (a, b) {
	      return (a[field] > b[field] ? 1 : -1);
	    });
	    if(reverse) filtered.reverse();
	    return filtered;
	  };
	});

/*
 * For forms submitted using the default submit function (Scope: document)
 * Not necessary to be inside an element, for inputs use ngEnter
 */
orcidNgModule.directive('ngEnterSubmit', function($document) {
    return {
        restrict: 'A',
        link: function(scope, element, attr) {
        	$document.bind("keydown keypress", function(event) {
                if (event.which === 13) {
                   element.submit();
                }
            });

        }
    };
});

/*
 * For forms submitted using a custom function, Scope: Document
 * 
 * Example:
 * <fn-form update-fn="theCustomFunction()">
 * 
 * </fn-form>
 * 
 */
orcidNgModule.directive('fnForm', function($document) {
    return {
        restrict: 'E',
        scope: {
            updateFn: '&'
        },
        link: function(scope, elm, attrs) { 
            $document.bind("keydown", function(event) {
                if (event.which === 13) {
                      scope.updateFn();                      
                      event.stopPropagation();
                }
            });
                    
        }
    }
});

/*
 * Scope: element
 */
orcidNgModule.directive('ngEnter', function() {
    return function(scope, element, attrs) {
        element.bind("keydown keypress", function(event) {
            if(event.which === 13) {            	
                scope.$apply(function(){
                    scope.$eval(attrs.ngEnter, {'event': event});
                });
                event.preventDefault();
                event.stopPropagation();
            }
        });
    };
});

/*Use instead ng-bind-html when you want to include directives inside the HTML to bind */
orcidNgModule.directive('bindHtmlCompile', ['$compile', function ($compile) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            scope.$watch(function () {
                return scope.$eval(attrs.bindHtmlCompile);
            }, function (value) {
                element.html(value);
                $compile(element.contents())(scope);
            });
        }
    };
}]);

orcidNgModule.directive('focusMe', function($timeout) {
    return {
      scope: { trigger: '=focusMe' },
      link: function(scope, element) {
        scope.$watch('trigger', function(value) {
          if(value === true) { 
            //console.log('trigger',value);
            //$timeout(function() {
              element[0].focus();
              scope.trigger = false;
            //});
          }
        });
      }
    };
  });
