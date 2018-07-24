declare var $: any;
declare var colorbox: any;
declare var isMobile: any;

import { Injectable } from '@angular/core';
import { HttpClient, HttpClientModule, HttpHeaders, HttpParams } 
     from '@angular/common/http';

import { Observable, Subject } 
    from 'rxjs';

@Injectable()
export class CommonService {
    private shownElement: any;

    constructor(
        private http: HttpClient
    ) {
        this.shownElement = [];
    }

    addComma(str): string {
        if (str.length > 0){
            return str + ', ';
        } 
        return str;
    };

    addUrlProtocol(url: string): string {
        if (url == null) {
            return url;
        }
        if(!url.startsWith('http')) {               
            if (url.startsWith('//')){              
                url = ('https:' == document.location.protocol ? 'https:' : 'http:') + url;
            } else {
                url = 'http://' + url;    
            }
        }
        return url;
    };

    contains(arr, obj): boolean {
        let index = arr.length;
        while (index--) {
            if (arr[index] === obj) {
               return true;
            }
        }
        return false;
    };

    copyErrorsLeft( data1, data2 ): void {
        for (let key in data1) {
            if (key == 'errors') {
                data1.errors = data2.errors;
            } else if (key == 'emailsAdditional'){
                for (var index in data1.emailsAdditional) {
                    if (data1.emailsAdditional[index] != null) {
                        if(data2.emailsAdditional[index] == undefined){
                            data1.emailsAdditional[index].errors = null;
                        } else {
                            data1.emailsAdditional[index].errors = data2.emailsAdditional[index].errors;
                        }
                    }
                }
            } else {
                if (data1[key] != null && data1[key].errors !== undefined) {
                    data1[key].errors = data2[key].errors;
                }
            };
        };
    };

    emptyTextField(field): boolean {
        if (field != null
            && field.value != null
            && field.value.trim() != '') {
            return false;
        }
        return true;
    };

    formatDate(oldDate): string {
        let date:any = new Date(oldDate);
        let day:any = date.getDate();
        let month:any = date.getMonth() + 1;
        let year:any = date.getFullYear();
        if(month < 10) {
            month = '0' + month;
        }
        if(day < 10) {
            day = '0' + day;
        }
        return (year + '-' + month + '-' + day);
    };

    formatTime(unixTimestamp): string {
        var date = new Date(unixTimestamp);
        return date.toUTCString();
    };

    formColorBoxResize(): void {
        if ( isMobile() ) {
            $.colorbox.resize({width: this.formColorBoxWidth(), height: '100%'});
        }
        else {
            // IE8 and below doesn't take auto height
            // however the default div height
            // is auto anyway
            $.colorbox.resize({width:'800px'});
            
        }
    };

    formColorBoxWidth(): string {
        return isMobile()? '100%': '800px';
    };

    getDisambiguatedOrgDetails(type, value): Observable<any>{
        return this.http.get( 
            getBaseUri() + '/orgs/disambiguated/' + type + '/' + value
        )
    };

    getNormalizedExtId(type, value): Observable<any>{
        return this.http.get( 
            getBaseUri() + '/identifiers/norm/' + type + '?value=' + value
        )
    };

    getParameterByName( name ): any {
        let _name = name,
            regex = new RegExp("[\\?&]" + _name + "=([^&#]*)"),
            results = regex.exec(location.search)
        ;
        
        _name = _name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    };

    getScripts(scripts, callback): void {
        let progress = 0;
        let internalCallback = function () {        
            if (++progress == scripts.length - 1) {
                callback();
            }
        };    
        scripts.forEach(
            function(script) {        
                $.getScript(script, internalCallback);        
            }
        );
    };

    hideTooltip(elem): void{
        this.shownElement[elem] = false;
    };

    isEmail(email): boolean {
        let re = /\S+@\S+\.\S+/;
        return re.test(email);
    };

    isPrintView(path): boolean {
        let re = new RegExp("(/print)(.*)?$");
        if (re.test(path)) {
            return true;
        } else {
            return false;
        }
    };

    isValidClass(cur) : string{
        let valid : boolean;
        if (cur === undefined) {
            return '';
        }
        valid = true;
        if (cur.required && (cur.value == null || cur.value.trim() == '')) {
            valid = false;
        }
        if (cur.errors !== undefined && cur.errors.length > 0) {
            valid = false;
        }
        return valid ? '' : 'text-error';
    };

    openImportWizardUrl(url): void {
        let win = window.open(url, "_target");
        setTimeout( function() {
            if(!win || win.outerHeight === 0) {
                //First Checking Condition Works For IE & Firefox
                //Second Checking Condition Works For Chrome
                window.location.href = url;
            }
        }, 250);
        $.colorbox.close();
    };

    showPrivacyHelp(elem, event, offsetArrow): void{
        let top = $(event.target.parentNode).parent().prop('offsetTop');
        let left = $(event.target.parentNode).parent().prop('offsetLeft');
        let scrollTop = $('.fixed-area').scrollTop();
        
        if (elem === '-privacy'){
            $('.edit-record .bulk-privacy-bar .popover-help-container').css({
                top: -75,
                left: 512
            });
        }else{
            if (elem.indexOf('@') > -1) {
                left = 530; //Emails modal fix
            }
            $('.edit-record .record-settings .popover-help-container').css({
                top: top - scrollTop - 160,
                left: left + 25
            });             
        }
        $('.edit-record .record-settings .popover-help-container .arrow').css({                    
            left: offsetArrow
        }); 
        this.shownElement[elem] = true;
    };

    showTooltip(elem, event, topOffset, leftOffset, arrowOffset): void {
        let top = $(event.target.parentNode).parent().prop('offsetTop');
        let left = $(event.target.parentNode).parent().prop('offsetLeft');   
        let scrollTop = $('.fixed-area').scrollTop();

        //console.log('showtooltip', elem, event, topOffset, leftOffset, arrowOffset, top, left, scrollTop);
        
        $('.edit-record .popover-tooltip').css({
            top: top - scrollTop - topOffset,
            left: left + leftOffset
        });
        
        $('.edit-record .popover-tooltip .arrow').css({                
            left: arrowOffset
        });            
        
        this.shownElement[elem] = true;
    };

    trimAjaxFormText(pojoMember) {
        if (pojoMember != null && pojoMember.value != null && (pojoMember.value.charAt(0) == ' ' || pojoMember.value.charAt(pojoMember.value.length - 1) == ' ')) {
            pojoMember.value = pojoMember.value.trim();
        }
    };            
}
