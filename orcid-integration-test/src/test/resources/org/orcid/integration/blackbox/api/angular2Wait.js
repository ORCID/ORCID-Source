/*
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
var callback = arguments[arguments.length - 1];
var rootSelector = 'body';
var ng12Hybrid = true;

// Taken from protractor clientsidescripts.js

//console.log('starting angular 2 wait script');
var el = document.querySelector(rootSelector);

try {
  if (!ng12Hybrid && window.getAngularTestability) {
    window.getAngularTestability(el).whenStable(callback);
    return;
  }
  if (!window.angular) {
    throw new Error('window.angular is undefined.  This could be either ' +
        'because this is a non-angular page or because your test involves ' +
        'client-side navigation, which can interfere with Protractor\'s ' +
        'bootstrapping.  See http://git.io/v4gXM for details');
  }
  if (angular.getTestability) {
    angular.getTestability(el).whenStable(callback);
  } else {
    if (!angular.element(el).injector()) {
      throw new Error('root element (' + rootSelector + ') has no injector.' +
         ' this may mean it is not inside ng-app.');
    }
    angular.element(el).injector().get('$browser').
        notifyWhenNoOutstandingRequests(callback);
  }
} catch (err) {
  console.log('err is ' + err);
  callback(err.message);
}
//console.log('finished angular 2 wait script');