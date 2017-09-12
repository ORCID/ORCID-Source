/**
 * This file defines the root module of the Angular 1 of the application.
 */

//Angular and other libraries imports
import * as $ from 'jquery'
import * as angular from 'angular'
import * as ngCookies from 'angular-cookies'
import * as ngSanitize from 'angular-sanitize'
import * as uibootstraptypeahead from 'angular-ui-bootstrap'
import * as vcRecaptcha from 'angular-recaptcha'
import 'angular-route'

//User generated imports
import { BiographyModule } from './biography/biography.ts';
import { CountryModule } from './country/country.ts';
import { ModalModule } from './modalNg2/modal-ng.ts';
import { PrivacytoggleModule } from './privacytoggle/privacyToggle.ts';
import { WidgetModule } from './widget/widget.ts';
import { WorksPrivacyPreferencesModule } from './worksPrivacyPreferences/worksPrivacyPreferences.ts'

export const orcidApp = angular.module(
    'orcidApp', 
    [
        ngCookies,
        ngSanitize, 
        vcRecaptcha,
        uibootstraptypeahead,
        BiographyModule.name,
        CountryModule.name,
        ModalModule.name,
        PrivacytoggleModule.name,
        WidgetModule.name,
        WorksPrivacyPreferencesModule.name
    ]
);

