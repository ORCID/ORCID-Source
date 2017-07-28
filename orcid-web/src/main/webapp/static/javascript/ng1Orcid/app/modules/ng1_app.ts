/**
 * This file defines the root module of the Angular 1 of the application.
 */
//import 'reflect-metadata';

import * as $ from 'jquery'
import * as angular from 'angular'
import * as vcRecaptcha from 'angular-recaptcha'
import * as ngCookies from 'angular-cookies'
import * as ngSanitize from 'angular-sanitize'
import * as uibootstraptypeahead from 'angular-ui-bootstrap'

import 'angular-route'

import { BiographyModule } from './biography/biography.ts';
import { WidgetModule } from './widget/widget.ts'

export const orcidApp = angular.module(
    'orcidApp', 
    [
    	//'ngRoute',
        ngCookies,
        ngSanitize, 
        vcRecaptcha,
        uibootstraptypeahead,
        BiographyModule.name,
        WidgetModule.name
    ]
);
/*
//For future routing
orcidApp.config(($locationProvider) => {
	$locationProvider.html5Mode(true)
});
*/