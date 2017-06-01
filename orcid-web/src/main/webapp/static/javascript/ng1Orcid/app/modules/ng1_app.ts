/**
 * This file defines the root module of the Angular 1 of the application.
 */
//import 'reflect-metadata';

import * as angular from 'angular'
import * as vcRecaptcha from 'angular-recaptcha'


//import { EmailFrequencyCtrl } from './../controllers/EmailFrequencyCtrl.ts' 

export const orcidApp = angular.module(
    'orcidApp', 
    [
        'ngCookies',
        'ngSanitize', 
        'ui.multiselect', 
        vcRecaptcha,
        'ui.bootstrap'//,
        //'EmailFrequencyCtrl'
        //EmailFrequencyModule.name
    ]
);