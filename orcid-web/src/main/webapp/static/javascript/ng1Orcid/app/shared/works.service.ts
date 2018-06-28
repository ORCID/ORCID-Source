declare var bibtexParse: any;

import { Injectable } 
    from '@angular/core';

import { HttpClient, HttpClientModule, HttpHeaders } 
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
        this.offset = 0;
        this.showLoadMore = false;
        this.url = getBaseUri() + '/my-orcid/worksForms.json';
    }

    addAbbrWorksToScope( type, sort, sortAsc): Observable<any> {
        this.details = new Object();
        let url = getBaseUri();
        if (type == this.constants.access_type.USER) {
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

    addBibtexJson(dw): void {
        if (dw.citation && dw.citation.citationType && dw.citation.citationType.value == 'bibtex') {
            try {
                this.bibtexJson[dw.putCode.value] = bibtexParse.toJSON(dw.citation.citation.value);
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

    handleWorkGroupData(data, callback?): void {
        if (this.groups == undefined) {
            this.groups = new Array();
        }
        this.groups = this.groups.concat(data.workGroups);
        this.groupsLabel = this.groups.length + " of " + data.totalGroups;
        this.showLoadMore = this.groups.length < data.totalGroups;
        this.loading = false;
        this.offset = data.nextOffset;
        
        if (callback != undefined) {
            callback();
        }
    }

    formatExternalIDType(model): Observable<any> {
        /* Move to component
        if (!model){
            return "";
        }
        if ($scope.externalIDNamesToDescriptions[model]){
            return $scope.externalIDNamesToDescriptions[model].description;
        }
        */
        
        return this.http.get( 
            getBaseUri()+'/works/idTypes.json?query=' + model
        )
        .pipe(
            tap(
                ()=> {
                    /*
                    for (var key in data) {
                          $scope.externalIDNamesToDescriptions[data[key].name] = data[key];
                      }
                  $scope.externalIDTypeCache[model] = ajax;
                    return $scope.externalIDNamesToDescriptions[model].description; 
                    */
                }
            )
        ); 
    };

    getBlankWork(callback?): Observable<any> {
        let worksSrvc = { //FIX
            blankWork: null
        }

        // if cached return clone of blank
        if (worksSrvc.blankWork != null){
            callback(JSON.parse(JSON.stringify(worksSrvc.blankWork)));
        }

        return this.http.get( 
            getBaseUri() + '/works/work.json'
        )
        .pipe(
            tap(
                (data) => {
                    //blankWork =  data;                      
                }
            )
        )  
        ;
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

    getExternalIDTypes(query): Observable<any>{  

        return this.http.get(
            getBaseUri()+'/works/idTypes.json?query='+query
        )
        .pipe(
            tap(
                (data) => {
                    /*
                    for (var key in data) {
                      $scope.externalIDNamesToDescriptions[data[key].name] = data[key];
                      }  
                      */               
                }
            )
        );
    };

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

    loadAllWorkGroups(sort, sortAsc, callback): any {
        this.details = new Object();
        this.groups = new Array();
        
        let url = getBaseUri() + '/works/allWorks.json?sort=' + sort + '&sortAsc=' + sortAsc;
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
            if(dw.workExternalIdentifiers[idx].workExternalIdentifierType == null
                && dw.workExternalIdentifiers[idx].workExternalIdentifierId == null) {
                dw.workExternalIdentifiers.splice(idx,1);
            }
        }
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
        /* Move to controller
        var workCategory = "";
        if(this.editWork != null && this.editWork.workCategory != null && this.editWork.workCategory.value != null && this.editWork.workCategory.value != ""){
            workCategory = this.editWork.workCategory.value;
        }
        else{
            return; //do nothing if we have not types
        }
        */
        return this.http.get(
            getBaseUri() + '/works/loadWorkTypes.json?workCategory=' + workCategory
        )
        .pipe(
            tap(
                (data) => {
                    /*
                    $scope.types = data;
                    if($scope.editWork != null && $scope.editWork.workCategory != null) {
                        // if the edit works doesn't have a value that matches types
                        var hasType = false;
                        for (var idx in $scope.types){
                            if ($scope.types[idx].key == $scope.editWork.workType.value) hasType = true;
                        }
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
                    */                   
                }
            )
        );

    };

    makeDefault(group, putCode): any {
        return this.http.get(
            getBaseUri() + '/works/updateToMaxDisplay.json?putCode=' + putCode
        )
        .pipe(
            tap(
                (data) => {
                    //group.defaultWork = worksSrvc.getWork(putCode);
                    //group.activePutCode = group.defaultWork.putCode.value;                    
                }
            )
        );
    }

    putWork(work, sucessFunc, failFunc): any {
        let encoded_data = JSON.stringify(work);
        return this.http.post( 
            getBaseUri() + '/works/work.json', 
            encoded_data, 
            { headers: this.headers }
        );
    }

    removeWorks(putCodes,callback?): Observable<any> {

        return this.http.delete( 
            getBaseUri() + '/works/' + putCodes.splice(0,150).join(),             
            { headers: this.headers }
        )
        .pipe(
            tap(
                (data) => {
                    /*
                        if (putCodes.length > 0) {
                        worksSrvc.removeWorks(putCodes,callback);
                    }
                    else if (callback) {
                        callback(data);
                    }
                    */                       
                }
            )
        )  
    }

    resetWorkGroups(): void {
        this.offset = 0;
        this.groups = new Array();
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

    updateVisibility(putCodes, priv): Observable<any> {
        let url = getBaseUri() + '/works/' + putCodes.splice(0,150).join() + '/visibility/'+priv;

        return this.http.get(
            url
        )
    }


    workCount( worksSrvc ): Number {
        var count = 0;
        for (var idx in worksSrvc.groups) {
            count += worksSrvc.groups[idx].activitiesCount;
        }
        return count;
    }

    worksValidate(obj,sucessFunc?, failFunc?): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        return this.http.post( 
            getBaseUri() + '/works/worksValidate.json', 
            encoded_data, 
            { headers: this.headers }
        )
        .pipe(
            tap(
                ()=> {
                    //sucessFunc(data);                    
                }
            )
        );

    }
     
    notifyOther(data: any): void {
        if (data) {
            this.notify.next(data);
        }
    }
    
}
