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
        var url = getBaseUri() + '/works/allWorks.json?sort=' + sort + '&sortAsc=' + sortAsc;
        this.loading = true;
        $.ajax({
            'url': url,
            'dataType': 'json',
            'success': function(data) {
                this.handleWorkGroupData(data, callback);
            }
        }).fail(function(e) {
            this.loading = false;
            console.log("Error fetching works");
            logAjaxError(e);
        });
    }

    loadWorkImportWizardList(): Observable<any> {
        let url = getBaseUri() + '/workspace/retrieve-work-import-wizards.json';

        return this.http.get(
            url
        )
        
    }

    makeDefault(group, putCode): any {
        /*
        $.ajax({
            url: getBaseUri() + '/works/updateToMaxDisplay.json?putCode=' + putCode,
            dataType: 'json',
            success: function(data) {
                group.defaultWork = worksSrvc.getWork(putCode);
                group.activePutCode = group.defaultWork.putCode.value;
            }
        }).fail(function(){
            // something bad is happening!
            console.log("some bad is hppending");
        });
        */
    }

    putWork(work,sucessFunc,failFunc): any {
        /*
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
        */
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

    resetWorkGroups(): void {
        this.offset = 0;
        this.groups = new Array();
    }

    notifyOther(data: any): void {
        if (data) {
            this.notify.next(data);
        }
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
}
