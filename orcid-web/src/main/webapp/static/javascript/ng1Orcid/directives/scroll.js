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

orcidNgModule.directive('scroll', function () {
    return {
        restrict: 'A',
        link: function ($scope, element, attrs) {
        	$scope.scrollTop = 0;
            var raw = element[0];
            element.bind('scroll', function () {
            	$scope.scrollTop = raw.scrollTop;
                //$scope.$apply(attrs.scroll);
            });
        }
    }
});