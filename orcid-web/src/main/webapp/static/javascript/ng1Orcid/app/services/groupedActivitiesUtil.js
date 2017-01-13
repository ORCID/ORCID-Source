/*
angular.module('orcidApp').factory(
    'GroupedActivitiesUtil', 
    function() {
        var GroupedActivitiesUtil = {
            group: function( activity, type, groupsArray ) {
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
            },

            rmByPut: function( putCode, type, groupsArray ) {
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
            }
        };
        return GroupedActivitiesUtil;
    }
);
*/