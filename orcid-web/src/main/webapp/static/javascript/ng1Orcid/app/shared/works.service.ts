declare var bibtexParse: any;

import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders, HttpParams } 
     from '@angular/common/http';

import { Observable, Subject } 
    from 'rxjs';

import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class WorksService {
    private headers: HttpHeaders;
    private notify = new Subject<any>();
    private offset: Number;
    private url: string;

    public bibtexJson: any;
    public constants = { 'access_type': { 'USER': 'user', 'ANONYMOUS': 'anonymous'}};
    public details: any;
    public groups: any;
    public groupsLabel: any;
    public labelsMapping: any;
    public loading: boolean;
    public showLoadMore: boolean;
    
    notifyObservable$ = this.notify.asObservable();

    constructor( private http: HttpClient ){
        this.bibtexJson = {};
        this.constants = { 
            'access_type': { 
                'USER': 'user', 
                'ANONYMOUS': 'anonymous'
            }
        };
        this.details = new Object();
        this.groups = new Array();
        this.groupsLabel = null;
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );

        this.labelsMapping = {
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
        }

        this.offset = 0;
        this.showLoadMore = false;
        this.url = getBaseUri() + '/my-orcid/worksForms.json';
    }

    addBibtexJson(dw): void {
        if (dw.citation && dw.citation.citationType && dw.citation.citationType.value == 'bibtex') {
            try {

                this.bibtexJson[dw.putCode.value] = bibtexParse.toJSON(dw.citation.citation.value);
                for (var idx in this.bibtexJson[dw.putCode.value]) {
                    //make entryTags array so that template will parse correctly
                    this.bibtexJson[dw.putCode.value][idx].entryTags = Array.of(this.bibtexJson[dw.putCode.value][idx].entryTags); 
                }
                 
            } catch (err) {
                this.bibtexJson[dw.putCode.value] = null;
                console.log("couldn't parse bibtex: " + dw.citation.citation.value);
            };
        };
    }

    consistentVis(group): boolean {
        let visibility = group.works[0].visibility.visibility;
        for(let i = 0; i < group.works.length; i++) {
            if (group.works[i].visibility.visibility != visibility) {
                return false;
            }
        }
        return true;
    }

    getBibtexExport(): Observable<any> {
        return this.http.get( 
            getBaseUri() + '/works/works.bib', { responseType: 'text'}
        )
    }

    getBlankWork(callback?): Observable<any> {
        return this.http.get( 
            getBaseUri() + '/works/work.json'
        )
    };

    getDetails(putCode, type): Observable <any> {
        let url = getBaseUri();
        if (type == this.constants.access_type.USER) {
            url += '/works/getWorkInfo.json?workId=' + putCode;
        } else {
            url += '/' + orcidVar.orcidId + '/getWorkInfo.json?workId=' + putCode;
        }
        return this.http.get(
                url
            ) 
    }

    getExternalIdTypes(term): any {  
        return this.http.get(
            getBaseUri()+'/works/idTypes.json?query='+term
        )
    };

    getGroup(putCode): any {
        for (var idx in this.groups) {
            for (var y in this.groups[idx].works) {
                if (this.groups[idx].works[y].putCode.value == putCode) {
                    return this.groups[idx];
                }
            }
        }
        return null;
    }

    getGroupDetails(putCode, type, callback?): void {
        let group = this.getGroup(putCode);
        let needsLoading =  new Array();
        
        let popFunct = function () {
            if (needsLoading.length > 0) {
                this.getDetails(needsLoading.pop(), type, popFunct);
            }
            else if (callback != undefined) {
                callback();
            }
        };

        for (var idx in group.works) {
            needsLoading.push(group.works[idx].putCode.value)
        }

        popFunct();
    }

    getLabelMapping (workCategory, workType): any {
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
    }

    getWork(putCode): any {
        for (let j in this.groups) {
            for (var k in this.groups[j].works) {
                if (this.groups[j].works[k].putCode.value == putCode) {
                    return this.groups[j].works[k];
                }
            }
        }
        return null;
    }

    getWorksPage( accessType, sort, sortAsc): Observable<any> {
        this.details = new Object();
        let url = getBaseUri();
        if (accessType == this.constants.access_type.USER) {
            url += '/works/worksPage.json';
        } else {
            url += '/' + orcidVar.orcidId +'/worksPage.json';
        }
        url += '?offset=' + this.offset + '&sort=' + sort + '&sortAsc=' + sortAsc;
        this.loading = true;

        return this.http.get(
            url
        )       
    }

    handleWorkGroupData(data, callback?): void {
        if (this.groups == undefined) {
            this.groups = new Array();
        }
        this.groups = this.groups.concat(data.groups);
        this.groupsLabel = this.groups.length + " of " + data.totalGroups;
        this.showLoadMore = this.groups.length < data.totalGroups;
        this.loading = false;
        this.offset = data.nextOffset;
        
        if (callback != undefined) {
            callback();
        }
    }

    loadAllWorkGroups(sort, sortAsc, callback?): any {
        this.details = new Object();
        this.groups = new Array();
        
        let url = getBaseUri() + '/works/allWorks.json?sort=' + sort + '&sortAsc=' + sortAsc;
        this.loading = true;

        return this.http.get(
            url
        )  
    }

    loadWorkImportWizardList(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/workspace/retrieve-work-import-wizards.json'
        )
        .pipe(
            tap(
                () => {
                    /*
                    if(data == null || data.length == 0) {
                        $scope.noLinkFlag = false;
                    }
                    $scope.selectedWorkType = om.get('workspace.works.import_wizzard.all');
                    $scope.selectedGeoArea = om.get('workspace.works.import_wizzard.all');
                    $scope.workImportWizardsOriginal = data;
                    $scope.bulkEditShow = false;
                    $scope.showBibtexImportWizard = false;
                    for(var idx in data) {                            
                        for(var i in data[idx].actTypes) {
                            if(!utilsService.contains($scope.workType, data[idx].actTypes[i])) {
                                $scope.workType.push(data[idx].actTypes[i]);
                            }                                
                        }
                        for(var j in data[idx].geoAreas) {
                            if(!utilsService.contains($scope.geoArea, data[idx].geoAreas[j])) {
                                $scope.geoArea.push(data[idx].geoAreas[j]);
                            }                                
                        }                            
                    }
                    */
                }
            )
        ); 
    }

    loadWorkTypes( workCategory ): Observable<any>{
        return this.http.get(
            getBaseUri() + '/works/loadWorkTypes.json?workCategory=' + workCategory
        )
    };

    notifyOther(data: any): void {
        if (data) {
            this.notify.next(data);
        }
    }

    postWork(work): any {
        let encoded_data = JSON.stringify(work);
        return this.http.post( 
            getBaseUri() + '/works/work.json', 
            encoded_data, 
            { headers: this.headers }
        );
    }

    resetWorkGroups(): void {
        this.offset = 0;
        this.groups = new Array();
    }

    refreshWorkGroups(sort, sortAsc): Observable<any>  {
        this.details = new Object();
        this.groups = new Array();
        let url = getBaseUri() + '/works/refreshWorks.json?limit=' + this.offset + '&sort=' + sort + '&sortAsc=' + sortAsc;
        this.loading = true;
        return this.http.get(
            url
        ) 
    }

    removeBadContributors(dw): void {
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

    removeBadExternalIdentifiers(dw): void {
        for(var idx in dw.workExternalIdentifiers) {
            if(dw.workExternalIdentifiers[idx].url == null) {
                dw.workExternalIdentifiers[idx].url = {value:""};
            }
            if(dw.workExternalIdentifiers[idx].externalIdentifierType == null
                && dw.workExternalIdentifiers[idx].externalIdentifierId == null) {
                dw.workExternalIdentifiers.splice(idx,1);
            }
        }
    }

    removeWorks(putCodes): Observable<any> {
        return this.http.delete( 
            getBaseUri() + '/works/' + putCodes.splice(0,150).join(),             
            { headers: this.headers }
        ) 
    }

    resolveExtId(extId): Observable<any> {
        let params = new HttpParams().set('value', extId.externalIdentifierId.value);
        return this.http.get( 
            getBaseUri() + '/works/id/'+ extId.externalIdentifierType.value, 
            { params: params }
        );
    }

    serverValidate( obj, relativePath ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/' + relativePath, 
            encoded_data, 
            { headers: this.headers }
        )
        .pipe(
            tap(
                ()=> {
                    if ( relativePath == 'works/work/citationValidate.json') {
                        //this.validateCitation();
                    }
                }
            )
        );
    }

    setData( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            this.url, 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    updateToMaxDisplay(putCode): Observable<any> {
        return this.http.get(
            getBaseUri() + '/works/updateToMaxDisplay.json?putCode=' + putCode
        )
    }

    updateVisibility(putCodes, priv): Observable<any> {
        let url = getBaseUri() + '/works/' + putCodes.splice(0,150).join() + '/visibility/'+priv;

        return this.http.get(
            url
        )
    }

    worksValidate(obj,sucessFunc?, failFunc?): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/works/worksValidate.json', 
            encoded_data, 
            { headers: this.headers }
        )
    }
    
}
