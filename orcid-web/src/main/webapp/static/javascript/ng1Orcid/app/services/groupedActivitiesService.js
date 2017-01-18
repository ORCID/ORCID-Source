angular.module('orcidApp').factory(
    'GroupedActivities', 
    function( type ) {
        var GroupedActivities = {
            _keySet : {},
            ABBR_WORK : 'abbrWork',
            activePutCode : null,
            activities : {},
            activitiesCount : 0,
            AFFILIATION : 'affiliation',
            count : 0,
            dateSortString : null,
            defaultPutCode : null,
            FUNDING : 'funding',
            groupDescription : null,
            groupId : this.count,
            groupRealId : null,
            groupType : null,
            PEER_REVIEW : 'peerReview',
            title : null,
            type : type,

            add: function( activity ){
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
            },

            addKey : function(key) {
                if (this.hasKey(key)){
                    return;
                } 
                this._keySet[key] = true;
                if (this.type == GroupedActivities.PEER_REVIEW) {
                    this.groupRealId = key;
                }
                return;
            },

            consistentVis : function() {
                var vis = null;
                if (this.type == GroupedActivities.FUNDING)
                    vis = this.getDefault().visibility.visibility;
                else
                    vis = this.getDefault().visibility;

                for (var idx in this.activities) {
                    if (this.type == GroupedActivities.FUNDING) {
                        if (this.activities[idx].visibility.visibility != vis){
                            return false;
                        }
                    } else {
                        if (this.activities[idx].visibility != vis) {
                            return false;
                        }
                    }
                }
                return true;
            },

            getActive : function() {
                return this.activities[this.activePutCode];
            },

            getByPut : function(putCode) {
                return this.activities[putCode];
            },

            getDefault : function() {
                return this.activities[this.defaultPutCode];
            },

            getIdentifiersPath : function() {
                if (this.type == GroupedActivities.ABBR_WORK) {
                    return 'workExternalIdentifiers';
                }
                if (this.type == GroupedActivities.PEER_REVIEW) {
                    return 'groupId';
                } 
                return 'externalIdentifiers';
            },

            getInstantiateCount: function() {
                var id = 0; // This is the private persistent value
                // The outer function returns a nested function that has access
                // to the persistent value.  It is this nested function we're storing
                // in the variable uniqueID above.
                return function() { 
                    return id++; 
                };  // Return and increment
            }
        };
        return GroupedActivities;
    }
);
