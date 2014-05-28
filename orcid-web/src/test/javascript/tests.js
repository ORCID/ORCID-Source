/*
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
describe("Sanity check", function() {
	// Example test from docs
	it("contains spec with an expectation", function() {
		expect(true).toBe(true);
	});
});

describe("A dummy function to check can test orcid code", function() {
	// Test a dummy function that I added to script.js
	it("test method returns success string", function() {
		expect(myTest()).toBe("a success");
	});

});

// An attempt at testing an angularjs controller
describe('MainCtrl', function(){
    var scope;//we'll use this scope in our tests
 
    //mock Application to allow us to inject our own dependencies
    beforeEach(angular.mock.module('orcidApp'));
    //mock the controller for the same reason and include $rootScope and $controller
    beforeEach(angular.mock.inject(function($rootScope, $controller){
        //create an empty scope
        scope = $rootScope.$new();
        //declare the controller and inject our empty scope
        $controller('QuickSearchCtrl', {$scope: scope});
    }));
    // tests start here
    it('should have variable text = "Hello World!"', function(){
        expect(scope.rows).toBe(10);
    });
    
});