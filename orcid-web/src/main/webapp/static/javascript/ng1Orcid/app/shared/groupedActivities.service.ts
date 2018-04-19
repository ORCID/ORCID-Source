import { Injectable } 
    from '@angular/core';

import { HttpClient } 
     from '@angular/common/http';


import { Observable } 
    from 'rxjs/Observable';

import 'rxjs/Rx';

@Injectable()
export class GroupedActivitiesUtilService {
    private count: any;
    private ABBR_WORK: any;
    private PEER_REVIEW: any;
    private AFFILIATION: any;
    private FUNDING: any;

    private type: any;
    private _keySet: any;
    private activities: any;
    private activitiesCount: any;
    private activePutCode: any;
    private defaultPutCode: any;
    private dateSortString = null;
    private groupId: any;
    private groupDescription: any;
    private groupType: any;
    private groupRealId: any;
    private title: any;

    constructor( type ){
        this.count = 0;
        this.FUNDING = 'funding';
        this.ABBR_WORK = 'abbrWork';
        this.PEER_REVIEW = 'peerReview';
        this.AFFILIATION = 'affiliation';

        this._keySet = {};
        this.activities = {};
        this.activePutCode = null;
        this.activitiesCount = 0;
        this.dateSortString = null;
        this.defaultPutCode = null;
        this.groupId = this.count;
        this.groupDescription = null;
        this.groupRealId = null;
        this.groupType = null;
        this.title = null;
        this.type = type;

        //This seems fishy and wrong
        if (this.count == undefined) {
            this.count = 1;
        }
        else {
            this.count ++;
        }

    }
    add( activity ): void {      
        // assumes works are added in the order of the display index desc
        // subsorted by the created date asc
        var identifiersPath = null;
        identifiersPath = this.getIdentifiersPath();        
        
        if(this.type == this.PEER_REVIEW) {    
            var key = this.key(activity[identifiersPath]);
            this.addKey(key);
        } else {
            for (let idx in activity[identifiersPath]) {
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

    addKey(key): any {
        if ( this.hasKey(key) ){
            return;
        }
        this._keySet[key] = true;
        if (this.type == this.PEER_REVIEW) {
            this.groupRealId = key;
        }
        return;
    };

    consistentVis(): boolean {
        var vis = null;
        if (this.type == this.FUNDING){
            vis = this.getDefault().visibility.visibility;
        }
        else {
            vis = this.getDefault().visibility;
        }

        for (var idx in this.activities) {
   
            if (this.type == this.FUNDING) {
                if (this.activities[idx].visibility.visibility != vis)
                    return false;
            } else {
                if (this.activities[idx].visibility != vis)
                    return false;
            }
        }
        return true;
    };

    getActive(): any {
    return this.activities[this.activePutCode];
    };
    
    getByPut(putCode): any {
        return this.activities[putCode];
    };

    getDefault(): any {
        return this.activities[this.defaultPutCode];
    };

    getIdentifiersPath(): string {
        if (this.type == this.ABBR_WORK) {
            return 'workExternalIdentifiers';
        }
        if (this.type == this.PEER_REVIEW) {
            return 'groupId';
        }
        return 'externalIdentifiers';
    };

    getInstantiateCount(): any {
        var id = 0; // This is the private persistent value
        // The outer function returns a nested function that has access
        // to the persistent value.  It is this nested function we're storing
        // in the variable uniqueID above.
        return function() { 
            return id++; 
        };  // Return and increment
    }

    group(activity, type, groupsArray): any {
        var matches = new Array();
        // there are no possible keys for affiliations    
        //if (type != this.AFFILIATION);
       for (var idx in groupsArray) {          
           if (groupsArray[idx].keyMatch(activity))
               matches.push(groupsArray[idx]);
       }           
        if (matches.length == 0) {
            var newGroup = new GroupedActivitiesUtilService(type);
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

    /*
    * takes a activity and adds it to an existing group or creates a new group
    */
    hasKey(key): boolean {
        if (key in this._keySet){
            return true;
        }
        return false;
    };

    hasKeys(key): boolean {
        if (Object.keys(this._keySet).length > 0) {
            return true;
        }
        return false;
    };

    hasPut(putCode): boolean {
        if (this.activities[putCode] !== undefined) {
            return true;
            
        }
        return false;
    };

    hasUserVersion(): boolean {
        for (var idx in this.activities) {
            if (this.activities[idx].source == orcidVar.orcidId)
                return true;
        }
        return false;
    };

    key(activityIdentifiers): string {
        let idPath;
        let idTypePath;
        let key = '';
        let relationship = 'relationship';
        
        if (this.type == this.ABBR_WORK) {
            idPath = 'workExternalIdentifierId';
            idTypePath = 'workExternalIdentifierType';
        } else if (this.type == this.FUNDING) {
            idPath = 'value';
            idTypePath = 'type';
        } else if (this.type == this.AFFILIATION) {
            // we don't have external identifiers for affiliations yet
            idPath = null;
            idTypePath = null;
        } else if (this.type == this.PEER_REVIEW) {
            idPath = 'value';
            idTypePath = 'value';
        }
        
        if (this.type ==  this.PEER_REVIEW) {
            if(activityIdentifiers != null 
                && activityIdentifiers[idPath] != null) {
                key += activityIdentifiers[idPath];
            }
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

    keyMatch(activity): boolean {
        let identifiersPath = this.getIdentifiersPath();

        if(this.type == this.PEER_REVIEW) {        
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

    /*
    highestVis() {
        var vis = this.getDefault().visibility;
        for (var idx in this.activities) {

            if (vis == this.PUBLIC) {
                return vis;
            }
            else if (this.activities[putCode].visibility == PRIVACY.PUBLIC) {
                return PRIVACY.PUBLIC;
            }
            else if (this.activities[putCode].visibility == PRIVACY.LIMITED) {
                vis = PRIVACY.LIMITED;
            }
        }
        return vis;
    };
    */

    makeDefault(putCode): void {
        let act = this.activities[putCode];
        let title = null;
        
        this.defaultPutCode = putCode;
        this.dateSortString = this.activities[putCode].dateSortString;
        
        // at some point we should make this easier by making all paths match
        if (this.type == this.ABBR_WORK) {
            title = act.title;
        }
        else if (this.type == this.FUNDING) {
            title = act.fundingTitle.title;
        }
        else if (this.type == this.AFFILIATION) {
            title = act.affiliationName;
        }
        else if (this.type == this.PEER_REVIEW) {
            title = act.subjectName;
        }
        this.title =  title != null ? title.value : null;
    };

    rmByPut(putCode, type?, groupsArray?): any {
        if ( type && groupsArray ) {
            for (let idx in groupsArray) {
                if (groupsArray[idx].hasPut(putCode)) {
                    groupsArray[idx].rmByPut(putCode);
                    if (groupsArray[idx].activitiesCount == 0)
                        groupsArray.splice(idx,1);
                    else {
                        var orphans = groupsArray[idx].unionCheck();
                        for (var idj in orphans) {
                           //this.group(orphans[idj], type, groupsArray);
                        }
                    }
                }
            }
        } else {
            let activity =  this.activities[putCode];
            delete this.activities[putCode];
            this.activitiesCount--;
            if (putCode == this.defaultPutCode) {
                // make the first one default
                for (var idx in this.activities) {
                    this.defaultPutCode = idx;
                    break;
                }
            }
            if (putCode == this.activePutCode) {
                this.activePutCode = this.defaultPutCode;
            }
            return activity;
        }
    };

    /*
    makes sure the current set if a valid union of activities and returns any activity removed from the group
    */
    unionCheck(): any {
        //alert('here unionCheck');
        var rmActs = new Array();
        var tempGroups = new Array();

        for (let idx in this.activities) {
            //this.group(this.activities[idx],this.type, tempGroups);
        }
        for (let idx in tempGroups) {

            if (idx == '0') {
               this._keySet = tempGroups[idx]._keySet; 
            } else { 
                for (var jdx in tempGroups[idx].activities) {
                    rmActs.push(tempGroups[idx].activities[jdx]);
                    this.rmByPut(tempGroups[idx].activities[jdx].putCode.value);
                }
            }
        }
        return rmActs;
    };
}

