/**
 * This file defines the root module of the Angular 1 of the application.
 */
//import 'reflect-metadata';

import * as angular from 'angular'
import * as vcRecaptcha from 'angular-recaptcha'
import * as ngCookies from 'angular-cookies'
import * as ngSanitize from 'angular-sanitize'
import * as uibootstraptypeahead from 'angular-ui-bootstrap'

import { BiographyCtrl } from './biography/biography.component.ts';


//import { EmailFrequencyCtrl } from './../controllers/EmailFrequencyCtrl.ts' 

export const orcidApp = angular.module(
    'orcidApp', 
    [
        ngCookies,
        ngSanitize, 
        vcRecaptcha,
        uibootstraptypeahead,
        BiographyCtrl.name
    ]
);