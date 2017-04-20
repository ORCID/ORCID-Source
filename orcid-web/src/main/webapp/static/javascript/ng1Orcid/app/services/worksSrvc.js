angular.module('orcidApp').factory("worksSrvc", ['$rootScope', function ($rootScope) {
    var worksSrvc = {
        bibtexJson: {},
        blankWork: null,
        constants: { 'access_type': { 'USER': 'user', 'ANONYMOUS': 'anonymous'}},
        details: new Object(), // we should think about putting details in the
        groups: new Array(),
        labelsMapping: {
            "default": {
                types: [
                    {
                        type: "all",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.defaultTitle"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.defaultTitlePlaceholder")
                    }
                ]
            }, 
            "publication": {
                types: [
                    {
                        type: "book",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "book-chapter",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleBook"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleBookPlaceholder")
                    },
                    {
                        type: "book-review",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "dictionary-entry",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "dissertation",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleInstitution"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleInstitutionPlaceholder")
                    },
                    {
                        type: "edited-book",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "encyclopedia-entry",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "journal-article",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.journalTitle"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.journalTitlePlaceholder")
                    },
                    {
                        type: "journal-issue",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.journalTitle"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.journalTitlePlaceholder")
                    },
                    {
                        type: "magazine-article",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleMagazineArticle"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleMagazineArticlePlaceholder")
                    },
                    {
                        type: "manual",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "newsletter-article",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleNewsletter"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleNewsletterPlaceholder")
                    },
                    {
                        type: "newspaper-article",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleNewspaper"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleNewspaperPlaceholder")
                    },
                    {
                        type: "online-resource",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "report",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleInstitution"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleInstitutionPlaceholder")
                    },
                    {
                        type: "research-tool",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleInstitution"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleInstitutionPlaceholder")
                    },
                    {
                        type: "supervised-student-publication",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleInstitution"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleInstitutionPlaceholder")
                    },
                    {
                        type: "test",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleInstitution"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleInstitutionPlaceholder")
                    },
                    {
                        type: "translation",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "website",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "working-paper",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleInstitution"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleInstitutionPlaceholder")
                    }
                ]
            },
            "conference": {
                types: [
                    {
                        type: "conference-abstract",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleConference"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleConferencePlaceholder")
                    },
                    {
                        type: "conference-paper",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleConference"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleConferencePlaceholder")
                    },
                    {
                        type: "conference-poster",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitleConference"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleConferencePlaceholder")
                    }
                ]
            },
            "intellectual_property": {
                types: [
                    {
                        type: "disclosure",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleIntellectualPropertyPlaceholder")
                    },
                    {
                        type: "license",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleIntellectualPropertyPlaceholder")
                    },
                    {
                        type: "patent",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleIntellectualPropertyPlaceholder")
                    },
                    {
                        type: "registered-copyright",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleIntellectualPropertyPlaceholder")
                    }
                ]
            },
            "other_output": {
                types: [
                    {
                        type: "artistic-performance",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "data-set",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "invention",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "lecture-speech",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "research-technique",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "spin-off-company",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "standards-and-policy",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "technical-standard",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisherPlaceholder")
                    },
                    {
                        type: "other",
                        titleLabel: om.get("orcid.frontend.manual_work_form_contents.labelTitlePublisher"),
                        titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.labelTitleOtherPlaceholder")
                    }
                ]
            }
        },
        loading: false,
        loadingDetails: false,
        quickRef: {},
        worksToAddIds: null,

        addAbbrWorksToScope: function(type) {
            var url = getBaseUri();
            var workIds = "";
            if (type == worksSrvc.constants.access_type.USER) {
                url += '/works/works.json?workIds=';
            }
            else {
                url += '/' + orcidVar.orcidId +'/works.json?workIds='; // public
            } // use the anonymous url

            if(worksSrvc.worksToAddIds.length != 0 ) {
                worksSrvc.loading = true;
                workIds = worksSrvc.worksToAddIds.splice(0,20).join();
                
                $.ajax({
                    'url': url + workIds,
                    'dataType': 'json',
                    'success': function(data) {
                        $rootScope.$apply(function(){
                            var dw = null;
                            for (var i in data) {
                                dw = data[i];
                                removeBadContributors(dw);
                                removeBadExternalIdentifiers(dw);
                                worksSrvc.addBibtexJson(dw);
                                groupedActivitiesUtil.group(dw,GroupedActivities.ABBR_WORK,worksSrvc.groups);
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
                }).fail(function(e) {
                    worksSrvc.loading = false;
                    console.log("Error fetching works: " + workIds);
                    logAjaxError(e);
                });
            } else {
                worksSrvc.loading = false;
            };
        },

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

        copyEIs: function(from, to) {
            // add all identiifers
            if (to.workExternalIdentifiers == undefined) {
                to.workExternalIdentifiers = new Array();
            }
            for (var idx in from.workExternalIdentifiers){
                to.workExternalIdentifiers.push(JSON.parse(JSON.stringify(from.workExternalIdentifiers[idx])));
            }
            return to;
        },

        createNew: function(work) {
            var cloneW = JSON.parse(JSON.stringify(work));
            cloneW.source = null;
            cloneW.putCode = null;
            cloneW.contributors = [];
            return cloneW;
        },

        deleteGroupWorks: function(putCodes) {
            var rmWorks = [];
            var rmGroups = [];
            for (var i in putCodes) {
                for (var j in worksSrvc.groups) {
                    if (worksSrvc.groups[j].hasPut(putCodes[i])) {
                        rmGroups.push(j);
                        for (var k in worksSrvc.groups[j].activities){
                            rmWorks.push(worksSrvc.groups[j].activities[k].putCode.value);
                        }
                    };
                }
            }
            while (rmGroups.length > 0) {
                worksSrvc.groups.splice(rmGroups.pop(),1);
            }
            worksSrvc.removeWorks(rmWorks);
        },

        deleteWork: function(putCode) {
            worksSrvc.removeWorks([putCode], function() {
                groupedActivitiesUtil.rmByPut(putCode, GroupedActivities.ABBR_WORK, worksSrvc.groups);
                $rootScope.$apply();
            });
        },

        getBlankWork: function(callback) {
            // if cached return clone of blank
            if (worksSrvc.blankWork != null){
                callback(JSON.parse(JSON.stringify(worksSrvc.blankWork)));
            }
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
            var url = getBaseUri();
            if (type == worksSrvc.constants.access_type.USER){
                url += '/works/getWorkInfo.json?workId=';
            }
            else {// use the anonymous url
                url += '/' + orcidVar.orcidId + '/getWorkInfo.json?workId='; // public
            }
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
                            if (callback != undefined) {
                                callback(worksSrvc.details[putCode]);
                            } 
                        });
                    }
                }).fail(function(e){
                    // something bad is happening!
                    console.log("error fetching works");
                    logAjaxError(e);
                });
            } else {
                if (callback != undefined){
                    callback(worksSrvc.details[putCode]);
                }
            };
        },
        getEditable: function(putCode, callback) {
            // first check if they are the current source
            var work = worksSrvc.getDetails(
                putCode, worksSrvc.constants.access_type.USER, 
                function(data) {
                    if (data.source == orcidVar.orcidId){
                        callback(data);
                    }
                    else{
                        worksSrvc.getGroupDetails(
                            putCode, 
                            worksSrvc.constants.access_type.USER, 
                            function () {
                                // in this case we want to open their version
                                // if they don't have a version yet then copy
                                // the current one
                                var bestMatch = null;
                                for (var idx in worksSrvc.details) {    
                                    if (worksSrvc.details[idx].source == orcidVar.orcidId) {
                                        bestMatch = worksSrvc.details[idx];
                                        break;
                                    }
                                }
                                if (bestMatch == null) {
                                    bestMatch = worksSrvc.createNew(worksSrvc.details[putCode]);
                                }
                                callback(bestMatch);
                            }
                        );
                    }
                }
            );
        },

        getGroup: function(putCode) {
            for (var idx in worksSrvc.groups) {
                if (worksSrvc.groups[idx].hasPut(putCode)){
                    return worksSrvc.groups[idx];
                }
            }
            return null;
        },

        getGroupDetails: function(putCode, type, callback) {
            var group = worksSrvc.getGroup(putCode);
            var needsLoading =  new Array();
            
            var popFunct = function () {
                if (needsLoading.length > 0) {
                    worksSrvc.getDetails(needsLoading.pop(), type, popFunct);
                }
                else if (callback != undefined) {
                    callback();
                }
            };

            for (var idx in group.activities) {
                needsLoading.push(group.activities[idx].putCode.value)
            }

            popFunct();
        },

        getLabelMapping: function(workCategory, workType){
            var result = this.labelsMapping.default.types[0];
            var tempI = null;

            if( this.labelsMapping[workCategory] != undefined ){
                tempI = this.labelsMapping[workCategory].types;
                for( var i = 0; i < tempI.length; i++) {
                    if( tempI[i].type == workType ) {
                        result = tempI[i];
                    }
                }
            }
            return result;
        },  
       
        getUniqueDois : function(putCode){
            var dois = [];              
            var group = worksSrvc.getGroup(putCode);
            for (var idx in group.activities) {                 
                for (var i = 0; i <= group.activities[idx].workExternalIdentifiers.length - 1; i++) {
                    if (group.activities[idx].workExternalIdentifiers[i].workExternalIdentifierType.value == 'doi'){
                        if (isIndexOf.call(dois, group.activities[idx].workExternalIdentifiers[i].workExternalIdentifierId.value) == -1){
                            dois.push(group.activities[idx].workExternalIdentifiers[i].workExternalIdentifierId.value);
                        }
                    }
                }
            }
            
            return dois;
        },

        getWork: function(putCode) {
            for (var idx in worksSrvc.groups) {
                if (worksSrvc.groups[idx].hasPut(putCode)) {
                    return worksSrvc.groups[idx].getByPut(putCode);
                }
            }
            return null;
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
                }).fail(function(e){
                    // something bad is happening!
                    console.log("error fetching works");
                    logAjaxError(e);
                });
            };
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
                    if (putCodes.length > 0) {
                        worksSrvc.removeWorks(putCodes,callback);
                    }
                    else if (callback) {
                        callback(data);
                    }
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
                    if (putCodes.length > 0) {
                        worksSrvc.updateVisibility(putCodes, priv);
                    }
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
        }
    };
    return worksSrvc;
}]);