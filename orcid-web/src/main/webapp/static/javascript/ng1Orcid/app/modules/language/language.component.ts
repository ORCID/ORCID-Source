declare var $window: any;
declare var orcidVar: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { LanguageService } 
    from '../../shared/language.service.ts';

import { WidgetService } 
    from '../../shared/widget.service.ts';


@Component({
    selector: 'language-ng2',
    template:  scriptTmpl("language-ng2-template")
})
export class LanguageComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    language: any;
    productionLangList: any;
    testingLangList: any;
    languages: any;

    constructor(
        private languageService: LanguageService,
        private widgetSrvc: WidgetService
    ) {
        this.language = {};
        this.languages = [];
        this.productionLangList =
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
        this.testingLangList =
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
                    "value": 'lr',
                    "label": 'lr'
                },
                {
                    "value": 'pt',
                    "label": 'Português'
                },
                {
                    "value": 'rl',
                    "label": 'rl'
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
        
    }

    getCookie(cname): any {
        var name = cname + "=";
        var decodedCookie = decodeURIComponent(document.cookie);
        var ca = decodedCookie.split(';');
        for(var i = 0; i <ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        return "";
    }

    getCurrentLanguage(): void{
        let locale_v3: any;

        if(this.languages != undefined && this.languages.length > 0){
            this.language = "en"; //Default
            let cookie = this.getCookie('locale_v3');
            let tempLanguages = this.languages;
            
            typeof(cookie) !== 'undefined' ? locale_v3 = cookie : locale_v3 = "en";   

            tempLanguages.forEach(
                function(value, key) {
                    if (value.value == locale_v3){
                        this.language = this.languages[key].value;
                    }

                }.bind(this)
            );
        
        }

        
    };

    selectedLanguage(): void {

        this.languageService.selectedLanguage( this.language )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.languages.forEach(
                    function(value, key){
                        var params;
                        if(value.value == data.locale){
                            this.language = this.languages[key].value;                        
                            this.widgetSrvc.setLocale(this.language.value);
                            //In case some parameters were sent via URL
                            params = window.location.href.split("?")[1];
                            if (typeof params != 'undefined'){
                                params = params.split("&");
                                //Removing language parameter (lang=[code]) if it exists
                                for ( var i = 0; i < params.length; i++ ){
                                    if(params[i].indexOf("lang=") > -1){
                                        params.splice(i, 1);    
                                    }
                                }
                                
                                if ( params.length > 0 ) {                                
                                    window.location.href = window.location.href.split("?")[0] + '?' + params.join("&");
                                } else {
                                    window.location.href = window.location.href.split("?")[0];
                                }
                                
                            }else{
                                window.location.reload(true);
                            }
                        }
                    }.bind(this)
                );
            },
            error => {
                //console.log('getWebsitesFormError', error);
            } 
        );
    };



    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        
        if (location == parent.location 
            && window.location.hostname.toLowerCase() != "orcid.org"){
            //add AR if LANG_AR togglz feature enabled
            //remove after AR is live and add to testingLangList
            if (orcidVar.features['LANG_AR'] == true){
                this.testingLangList.unshift({
                    "value": "ar",
                    "label": "العربية"
                });
            }
            this.languages = this.testingLangList;
        }
        else{
            //add AR if LANG_AR togglz feature enabled
            //remove after AR is live and add to productionLangList
            if (orcidVar.features['LANG_AR'] == true){
                this.productionLangList.unshift({
                    "value": "ar",
                    "label": "العربية"
                });
            }
            this.languages = this.productionLangList;
        }

        this.getCurrentLanguage();
        
        
    }; 
}
