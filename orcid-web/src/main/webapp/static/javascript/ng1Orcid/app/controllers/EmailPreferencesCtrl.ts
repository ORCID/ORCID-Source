import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const EmailPreferencesCtrl = angular.module('orcidApp').controller(
	'EmailPreferencesCtrl',
	[
		'$scope', 
		'prefsSrvc', 
		function (
			$scope, 
			prefsSrvc
		) {
    		$scope.prefsSrvc = prefsSrvc;
		}
	]
);

// This is the Angular 2 part of the module
@NgModule({})
export class EmailPreferencesCtrlNg2Module {}