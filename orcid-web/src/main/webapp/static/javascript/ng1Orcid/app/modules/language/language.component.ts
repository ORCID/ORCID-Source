declare var $window: any;
declare var orcidVar: any;

//Import all the angular components

import { NgFor, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

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
        this.language = null;
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
        this.languages = null;
    }

    getCurrentLanguage(): void{
        let locale_v3: any;

        this.language = this.languages[0]; //Default

        
        /*
        typeof($cookies.get('locale_v3')) !== 'undefined' ? locale_v3 = $cookies.get('locale_v3') : locale_v3 = "en"; //If cookie exists we get the language value from it        
        
        angular.forEach($scope.languages, function(value, key){ //angular.forEach doesn't support break
            if (value.value == locale_v3){
                $scope.language = $scope.languages[key];
                $scope.widgetSrvc.locale = $scope.language.value; 
            }
        });
        */
    };

    selectedLanguage(): void {

        this.languageService.selectedLanguage( this.language.value )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.languages.forEach(function(value, key){
                    var params;
                    if(value.value == data.locale){
                        this.language = this.languages[key];                        
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
                });
            },
            error => {
                console.log('getWebsitesFormError', error);
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
        /*
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
        */
        
    }; 
}