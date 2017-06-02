/**
 * This file defines the root module of the Angular 1 of the application.
 */
//import 'reflect-metadata';

import * as angular from 'angular'
import * as vcRecaptcha from 'angular-recaptcha'
import * as ngCookies from 'angular-cookies'
import * as ngSanitize from 'angular-sanitize'
import typeahead from 'angular-ui-bootstrap/src/typeahead/index-nocss.js';


//import { EmailFrequencyCtrl } from './../controllers/EmailFrequencyCtrl.ts' 


//need to import ui.bootstrap and ui.multiselect correctly
export const orcidApp = angular.module(
    'orcidApp', 
    [
        ngCookies,
        ngSanitize, 
        vcRecaptcha,
        //'ui.multiselect', 
        //typeahead//,
        //'EmailFrequencyCtrl'
        //EmailFrequencyModule.name
    ]
);