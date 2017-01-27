//import * as angular from 'angular'

//export const orcidApp = angular.module('orcidApp', ['ngCookies','ngSanitize', 'ui.multiselect', 'vcRecaptcha']);

//var orcidApp = angular.module('orcidApp', ['ngCookies','ngSanitize', 'ui.multiselect', 'vcRecaptcha']);
//var orcidNgModule = angular.module('orcidApp', ['ngCookies','ngSanitize', 'ui.multiselect', 'vcRecaptcha']);

//angular.bootstrap(document.body, ['orcidApp']);

/**
 * This file defines the root module of the Angular 1 of the application.
 */
//import 'reflect-metadata';

import * as angular from 'angular'
import 'angular-route'

// import app modules
//import {MessagesModule} from './messages';
//import {MenuModule} from './menu';

export const Ng1AppModule = angular.module('Ng1AppModule', ['ngRoute']);