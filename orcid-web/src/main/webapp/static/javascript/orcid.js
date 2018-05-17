/*
 * GROUPINGS LOGIC
 */
var PRIVACY = {};
PRIVACY.PUBLIC = 'PUBLIC';
PRIVACY.LIMITED = 'LIMITED';
PRIVACY.PRIVATE = 'PRIVATE';

var GroupedActivitiesUtil = function() {};

GroupedActivitiesUtil.prototype.group = function(activity, type, groupsArray) {
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

GroupedActivitiesUtil.prototype.rmByPut = function(putCode, type, groupsArray) {
    for (var idx in groupsArray) {
        if (groupsArray[idx].hasPut(putCode)) {
           groupsArray[idx].rmByPut(putCode);
           if (groupsArray[idx].activitiesCount == 0)
               groupsArray.splice(idx,1);
           else {
               var orphans = groupsArray[idx].unionCheck();
               for (var idj in orphans)
                   groupedActivitiesUtil.group(orphans[idj], type, groupsArray);
           }
        }
    }
};

var groupedActivitiesUtil = new GroupedActivitiesUtil();

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
    this.dateSortString = null;
    this.groupId = GroupedActivities.count;
    this.groupDescription = null;
    this.groupType = null;
    this.groupRealId = null;
    this.title = null;
};

GroupedActivities.count = 0;
GroupedActivities.prototype.FUNDING = 'funding';
GroupedActivities.ABBR_WORK = 'abbrWork';
GroupedActivities.PEER_REVIEW = 'peerReview';
GroupedActivities.AFFILIATION = 'affiliation';
GroupedActivities.NG2_AFFILIATION = 'ng2_affiliation';


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
    var vis = this.getDefault().visibility.visibility;
    
    for (var idx in this.activities) {
        if (this.activities[idx].visibility.visibility != vis) {
            return false;
        }
    }
                       
    return true;
};

GroupedActivities.prototype.getIdentifiersPath = function() {
    if (this.type == GroupedActivities.ABBR_WORK) return 'workExternalIdentifiers';
    if (this.type == GroupedActivities.PEER_REVIEW) return 'groupId';
    return 'externalIdentifiers';
};

/*
* takes a activity and adds it to an existing group or creates a new group
*/

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
        if ((activityIdentifiers[relationship] == null || activityIdentifiers[relationship].value != 'part-of')
                && activityIdentifiers[idPath] != null
                && activityIdentifiers[idPath].value != null
                && activityIdentifiers[idPath].value != '') {           
            key = activityIdentifiers[idTypePath].value;
            // Removed conversion to lower case as per card: https://trello.com/c/b7jLWgNq/3070-api-groups-are-case-sensitive-ui-groups-are-not
            key += activityIdentifiers[idPath] != null ? activityIdentifiers[idPath].value : '';
            
            
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

// makes sure the current set if a valid union of activities
// and returns any activity removed from the group
GroupedActivities.prototype.unionCheck = function() {
    //alert('here unionCheck');
    var rmActs = new Array();
    var tempGroups = new Array();
    for (var idx in this.activities)
        groupedActivitiesUtil.group(this.activities[idx],this.type, tempGroups);
    for (var idx in tempGroups)
        if (idx == 0) {
           this._keySet = tempGroups[idx]._keySet; 
        } else { 
           for (var jdx in tempGroups[idx].activities) {
              rmActs.push(tempGroups[idx].activities[jdx]);
              this.rmByPut(tempGroups[idx].activities[jdx].putCode.value);
           }
        }
    return rmActs;
};

var ActSortState = function(groupType) {
    var _self = this;
    _self.type = groupType;    
    
    _self.reverseKey = {};
    _self.reverseKey['date']  = true;
    _self.reverseKey['title'] = false;
    _self.reverseKey['type']  = false;
    _self.reverseKey['groupName']  = false;    
    _self.reverseKey['startDate']  = false;
    _self.reverseKey['endDate']  = false;
    
    _self.predicateKey = 'date';
    if (_self.type == 'peerReview') {
        _self.predicateKey = 'groupName';
    } else if (_self.type == 'affiliation') {
        _self.predicateKey = 'endDate';
        _self.reverseKey['date']  = false;
        _self.reverseKey['endDate']  = true;        
    }  else if (_self.type == 'ng2_affiliation') {
        _self.predicateKey = 'endDate';
        _self.reverseKey['endDate']  = false;
    }  
    
    _self.predicate = this.predicateMap[_self.type][_self.predicateKey];
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
sortPredicateMap[GroupedActivities.AFFILIATION]['endDate'] = ['-dateSortString', 'title'];
sortPredicateMap[GroupedActivities.AFFILIATION]['startDate'] = ['-getDefault().startDate.year', '-getDefault().startDate.month', '-getDefault().startDate.day', 'title'];
sortPredicateMap[GroupedActivities.AFFILIATION]['title'] = ['title', '-dateSortString'];

sortPredicateMap[GroupedActivities.PEER_REVIEW] = {};
sortPredicateMap[GroupedActivities.PEER_REVIEW]['groupName'] = ['groupName'];

sortPredicateMap[GroupedActivities.NG2_AFFILIATION] = {};
sortPredicateMap[GroupedActivities.NG2_AFFILIATION]['endDate'] = ['dateSortString', 'affiliationName.value'];
sortPredicateMap[GroupedActivities.NG2_AFFILIATION]['startDate'] = ['startDate.year', 'startDate.month', 'startDate.day', 'affiliationName.value'];
sortPredicateMap[GroupedActivities.NG2_AFFILIATION]['title'] = ['affiliationName.value', 'dateSortString'];

ActSortState.prototype.predicateMap = sortPredicateMap;

ActSortState.prototype.sortBy = function(key) { 
    if (this.predicateKey == key){
       this.reverse = !this.reverse;
       this.reverseKey[key] = !this.reverseKey[key];           
    }
    this.predicateKey = key;
    this.predicate = this.predicateMap[this.type][key];
};

(function($) {
 

    var showingTemplateMenu = false;

    var toolTips = function(){
        $(".settings-button").tooltip({
            placement: "bottom"
        });       
                
        $(".back").tooltip({
            placement: "bottom"
        });
        
        $(".save").tooltip({
            placement: "bottom"
        });
        
        $(".edit").tooltip({
            placement: "bottom"
        });
        
        $(".revoke").tooltip({
            placement: "bottom"
        });
        
        $(".add").tooltip({
            placement: "bottom"
        });
        
        $(".delete").tooltip({
            placement: "bottom"
        });
    };

    var secondaryNavCleanup = function() {
        var items = $(".main > .menu > li.active-trail > ul > li");
        var count = $(items).length;
        var current = $(".main > .menu > li.active-trail").find("li.active-trail a:first").parent().index() + 1;
        if (items) {
            if (current !== count && $(items).children("a").hasClass("active")) {
                $(items[current]).children("a").css({"border-left-color":"#fff"});
            }
            if (current === count) {
                $(items[current-1]).css({"border-right-color" : "#fff"});
            }
        }
    };

    var popupHandler = function() {
        positionPopup();
        $(".template-darken").fadeOut(1);
        $(".template-box").fadeOut(1);
        var closables = $(".close-template-popup, .template-darken");
        $(closables).on("click", function(e) {
            e.preventDefault();
            closePopup();
        });
        $(".open-template-popup").on("click", function(e) {
            e.preventDefault();
            openPopup();
        });
        $(window).resize(function() {
            positionPopup();
        });
    };

    var closePopup = function() {
        $(".template-darken").fadeOut(500);
        $(".template-box").fadeOut(500);

    };

    var openPopup = function() {
        $(".template-popup").show();
        $(".template-darken").fadeIn(500);
        $(".template-box").fadeIn(500);
    };

    var positionPopup = function() {
        var templateBox = $(".template-box");
        var w = $(templateBox).outerWidth();
        var h = $(templateBox).outerHeight();
        var ww = $(window).innerWidth();
        var wh = $(window).innerHeight();
        $(templateBox).css({
            "left": (ww/2) - (w/2) + "px",
            "top": (wh/2) - (h/2) + "px"
        });
    };

    var menuHack = function() {
        $("#signin").on("mouseenter", function(e) {
            $(".header .navigation>.menu").css({"background":"#a6ce39"});        
        });
        $("#signin").on("mouseout", function(e) {
            $(".header .navigation > .menu").css({"background":"#338caf"});
        });
    };
    
    /*============================================================
        Page initialisation
    ============================================================*/

    var init = function() {
        toolTips();
        popupHandler();
        //menuHack();        
        //menuHandler();
    };

    
    
    init();

})(jQuery);