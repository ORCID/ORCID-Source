/**
 * This file defines the root module of the Angular 1 of the application.
 */
//import 'reflect-metadata';

//import * as angular from 'angular'

import * as _angular_ from 'angular';

declare global {
  const angular: typeof _angular_;
}

export const orcidApp = angular.module('orcidApp', ['ngCookies','ngSanitize', 'ui.multiselect', 'vcRecaptcha','ui.bootstrap']);
