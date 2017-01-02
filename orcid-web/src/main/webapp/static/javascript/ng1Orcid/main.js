/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};

/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {

/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;

/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};

/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);

/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;

/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}


/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;

/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;

/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";

/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	__webpack_require__(1)

/***/ },
/* 1 */
/***/ function(module, exports, __webpack_require__) {

	var map = {
		"./appFileTextReader.js": 2,
		"./bindHtmlCompile.js": 3,
		"./compile.js": 4,
		"./fnForm.js": 5,
		"./focusMe.js": 6,
		"./modalEmailUnVerified/modalEmailUnVerified.js": 7,
		"./ngEnter.js": 8,
		"./ngEnterSubmit.js": 9,
		"./ngModelOnblur.js": 10,
		"./resize.js": 11,
		"./scroll.js": 12
	};
	function webpackContext(req) {
		return __webpack_require__(webpackContextResolve(req));
	};
	function webpackContextResolve(req) {
		return map[req] || (function() { throw new Error("Cannot find module '" + req + "'.") }());
	};
	webpackContext.keys = function webpackContextKeys() {
		return Object.keys(map);
	};
	webpackContext.resolve = webpackContextResolve;
	module.exports = webpackContext;
	webpackContext.id = 1;


/***/ },
/* 2 */
/***/ function(module, exports) {

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

	angular.module('orcidApp').directive('appFileTextReader', function($q){
	    var slice = Array.prototype.slice;
	    return {
	        restrict: 'A',
	        require: 'ngModel',
	        scope: {
	            updateFn: '&'
	        },
	        link: function(scope, element, attrs, ngModelCtrl){
	            if(!ngModelCtrl) return;
	            ngModelCtrl.$render = function(){};
	            element.bind('change', function(event){
	                var element = event.target;
	                $q.all(slice.call(element.files, 0).map(readFile))
	                .then(function(values){
	                    if(element.multiple){
	                        for(v in values){
	                            ngModelCtrl.$viewValue.push(values[v]);
	                        }
	                    }
	                    else{
	                        ngModelCtrl.$setViewValue(values.length ? values[0] : null);
	                    }
	                    scope.updateFn(scope);
	                    element.value = null;
	                });
	                function readFile(file) {
	                    var deferred = $q.defer();
	                    var reader = new FileReader();
	                    reader.onload = function(event){
	                        deferred.resolve(event.target.result);
	                    };
	                    reader.onerror = function(event) {
	                        deferred.reject(event);
	                    };
	                    reader.readAsText(file);
	                    return deferred.promise;
	                }
	            });//change
	        }//link
	    };//return
	});//appFilereader

/***/ },
/* 3 */
/***/ function(module, exports) {

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

	/*Use instead ng-bind-html when you want to include directives inside the HTML to bind */
	angular.module('orcidApp').directive('bindHtmlCompile', ['$compile', function ($compile) {
	    return {
	        restrict: 'A',
	        link: function (scope, element, attrs) {
	            scope.$watch(function () {
	                return scope.$eval(attrs.bindHtmlCompile);
	            }, function (value) {
	                element.html(value);
	                $compile(element.contents())(scope);
	            });
	        }
	    };
	}]);

/***/ },
/* 4 */
/***/ function(module, exports) {

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

	//Thanks to: https://docs.angularjs.org/api/ng/service/$compile#attributes
	angular.module('orcidApp').directive('compile', function($compile) {
	  // directive factory creates a link function
	  return function(scope, element, attrs) {
	    scope.$watch(
	      function(scope) {
	         // watch the 'compile' expression for changes
	        return scope.$eval(attrs.compile);
	      },
	      function(value) {
	        // when the 'compile' expression changes
	        // assign it into the current DOM
	        element.html(value);

	        // compile the new DOM and link it to the current
	        // scope.
	        // NOTE: we only compile .childNodes so that
	        // we don't get into infinite loop compiling ourselves
	        $compile(element.contents())(scope);
	      }
	    );
	  };
	});

/***/ },
/* 5 */
/***/ function(module, exports) {

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

	/*
	 * For forms submitted using a custom function, Scope: Document
	 * 
	 * Example:
	 * <fn-form update-fn="theCustomFunction()">
	 * 
	 * </fn-form>
	 * 
	 */
	angular.module('orcidApp').directive('fnForm', function($document) {
	    return {
	        restrict: 'E',
	        scope: {
	            updateFn: '&'
	        },
	        link: function(scope, elm, attrs) { 
	            $document.bind("keydown", function(event) {
	                if (event.which === 13) {
	                      scope.updateFn();                      
	                      event.stopPropagation();
	                }
	            });
	                    
	        }
	    }
	});

/***/ },
/* 6 */
/***/ function(module, exports) {

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

	angular.module('orcidApp').directive('focusMe', function($timeout) {
	    return {
	      scope: { trigger: '=focusMe' },
	      link: function(scope, element) {
	        $timeout( //[fn], [delay], [invokeApply], [Pass]
	            function(){
	                if (scope.trigger) {
	                    element[0].focus();
	                    scope.trigger = false;
	                }
	            },
	            1000
	        );
	      }
	    };
	});

/***/ },
/* 7 */
/***/ function(module, exports) {

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

	/*
	 * For modal with email verification validation
	 */

	angular.module('orcidApp').directive(
	    'modalEmailUnVerified', 
	    [
	        '$compile',
	        '$rootScope',
	        '$timeout',
	        function( $compile, $rootScope, $timeout ) {
	            $scope.content = {
	                btncancel : om.get("orcid.frontend.freemarker.btncancel"),
	                emailValue : "",
	                ensure_future_access : om.get("orcid.frontend.workspace.ensure_future_access"),
	                ensure_future_access2 : om.get("orcid.frontend.workspace.ensure_future_access2"),
	                ensure_future_access3 : om.get("orcid.frontend.workspace.ensure_future_access3"),
	                ensure_future_access4 : om.get("orcid.frontend.workspace.ensure_future_access4"),
	                ensure_future_access5 : om.get("orcid.frontend.workspace.ensure_future_access5"),
	                knowledgebase : om.get("orcid.frontend.link.url.knowledgebase"), 
	                send_verification : om.get("orcid.frontend.workspace.send_verification"),
	                support : om.get("orcid.frontend.link.email.support"),
	                your_primary_email : om.get("orcid.frontend.workspace.your_primary_email")
	            };

	            var closeModal = function(){
	                $.colorbox.remove();
	                $('modal-email-un-verified').html('<div id="modal-email-unverified-container"></div>');
	            };


	            var openModal = function( scope, data ){
	                content.emailValue = emailVerifiedObj.emails[0].value;
	                emailVerifiedObj = data;

	                $.colorbox(
	                    {
	                        //html : $compile( $('#modal-email-unverified-container').html() )(scope),
	                        html : $('#modal-email-unverified-container').html(),
	                        escKey: true,
	                        overlayClose: true,
	                        transition: 'fade',
	                        close: '',
	                        scrolling: false
	                    }
	                );
	                $.colorbox.resize({height:"250px", width:"500px"});
	            };

	            var verifyEmail = function( scope ){
	                var colorboxHtml = null;
	                $.ajax({
	                    url: getBaseUri() + '/account/verifyEmail.json',
	                    type: 'get',
	                    data:  { "email": emailVerifiedObj.emails[0].value },
	                    contentType: 'application/json;charset=UTF-8',
	                    dataType: 'json',
	                    success: function(data) {
	                        //alert( "Verification Email Send To: " + $scope.emailsPojo.emails[idx].value);
	                    }
	                }).fail(function() {
	                    // something bad is happening!
	                    console.log("error with multi email");
	                });
	                
	                colorboxHtml = $compile($('#verify-email-modal-sent').html())(scope);

	                $.colorbox({
	                    html : colorboxHtml,
	                    escKey: true,
	                    overlayClose: true,
	                    transition: 'fade',
	                    close: '',
	                    scrolling: false
	                });
	                $.colorbox.resize({height:"200px", width:"500px"});
	            };

	            function link( scope, element, attrs ) {

	                scope.verifyEmail = function() {
	                    verifyEmail( scope );
	                };

	                scope.closeColorBox = function() {
	                    closeModal();
	                };

	                scope.openModal = function( scope, data ){
	                    openModal( scope, data );
	                }

	                scope.$on(
	                    'emailVerifiedObj',
	                    function(event, data){
	                        if (data.flag == false ) {
	                            scope.openModal( scope, data ); 
	                        }
	                        else {
	                            scope.closeColorBox(); 
	                        }
	                    }

	                );
	            };

	            return {
	                link: link,
	                templateUrl: 'modalEmailUnVerified.html',
	                transclude: true
	            };
	        }
	    ]
	);

/***/ },
/* 8 */
/***/ function(module, exports) {

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

	/*
	 * Scope: element
	 */
	angular.module('orcidApp').directive('ngEnter', function() {
	    return function(scope, element, attrs) {
	        element.bind("keydown keypress", function(event) {
	            if(event.which === 13) {            	
	                scope.$apply(function(){
	                    scope.$eval(attrs.ngEnter, {'event': event});
	                });
	                event.preventDefault();
	                event.stopPropagation();
	            }
	        });
	    };
	});

/***/ },
/* 9 */
/***/ function(module, exports) {

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

	/*
	 * For forms submitted using the default submit function (Scope: document)
	 * Not necessary to be inside an element, for inputs use ngEnter
	 */
	angular.module('orcidApp').directive('ngEnterSubmit', function($document) {
	    return {
	        restrict: 'A',
	        link: function(scope, element, attr) {
	        	$document.bind("keydown keypress", function(event) {
	                if (event.which === 13) {
	                   element.submit();
	                }
	            });

	        }
	    };
	});

/***/ },
/* 10 */
/***/ function(module, exports) {

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

	angular.module('orcidApp').directive('ngModelOnblur', function() {
	    return {
	        restrict: 'A',
	        require: 'ngModel',
	        link: function(scope, elm, attr, ngModelCtrl) {
	            if (attr.type === 'radio' || attr.type === 'checkbox') return;

	            elm.unbind('input').unbind('keydown').unbind('change');

	            elm.bind("keydown keypress", function(event) {
	                if (event.which === 13) {
	                    scope.$apply(function() {
	                        ngModelCtrl.$setViewValue(elm.val());
	                    });
	                }
	            });

	            elm.bind('blur', function() {
	                scope.$apply(function() {
	                    ngModelCtrl.$setViewValue(elm.val());
	                });
	            });
	        }
	    };
	});

/***/ },
/* 11 */
/***/ function(module, exports) {

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

	angular.module('orcidApp').directive('resize', function ($window) {
		return function ($scope, element) {
			var w = angular.element($window);
			/* Only used for detecting window resizing, the value returned by w.width() is not accurate, please refer to getWindowWidth() */
			$scope.getWindowWidth = function () {
				return { 'w': getWindowWidth() };
			};
			$scope.$watch($scope.getWindowWidth, function (newValue, oldValue) {			
	            
				$scope.windowWidth = newValue.w;
				
	            
	            if($scope.windowWidth > 767){ /* Desktop view */
	            	$scope.menuVisible = true;
	            	$scope.searchVisible = true;
	            	$scope.settingsVisible = true;
	            }else{
	            	$scope.menuVisible = false;
	            	$scope.searchVisible = false;
	            	$scope.settingsVisible = false;
	            }
	            
			}, true);
		
			w.bind('resize', function () {
				$scope.$apply();
			});
		}
	});

/***/ },
/* 12 */
/***/ function(module, exports) {

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

	angular.module('orcidApp').directive('scroll', function () {
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

/***/ }
/******/ ]);