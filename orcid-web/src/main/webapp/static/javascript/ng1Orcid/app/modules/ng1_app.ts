/**
 * This file defines the root module of the Angular 1 of the application.
 */
//import 'reflect-metadata';

import * as angular from 'angular'

import { EmailFrequencyCtrl } from './../controllers/EmailFrequencyCtrl.ts' 
import { EmailPreferencesCtrl } from './../controllers/EmailPreferencesCtrl.ts'

export const orcidApp = angular.module(
	'orcidApp', 
	[
		'ngCookies',
		'ngSanitize', 
		'ui.multiselect', 
		'vcRecaptcha',
		'ui.bootstrap',
		'EmailFrequencyCtrl',
		'EmailPreferencesCtrl' //Couldn't found this one in the freemarker code
	]
);
