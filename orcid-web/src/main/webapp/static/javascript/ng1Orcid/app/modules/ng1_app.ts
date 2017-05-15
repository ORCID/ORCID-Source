//angular.bootstrap(document.body, ['orcidApp']);

/**
 * This file defines the root module of the Angular 1 of the application.
 */
//import 'reflect-metadata';

import * as angular from 'angular'
//import 'angular-route'


export const orcidApp = angular.module('orcidApp', ['ngCookies','ngSanitize', 'ui.multiselect', 'vcRecaptcha','ui.bootstrap']);

//var orcidNgModule = angular.module('orcidApp', ['ngCookies','ngSanitize', 'ui.multiselect', 'vcRecaptcha','ui.bootstrap']);

