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
// Karma configuration
// Generated on Tue Apr 29 2014 18:34:03 GMT+0100 (BST)

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '../../..',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
        'src/main/webapp/static/javascript/jquery/1.8.1/jquery.min.js',
        'src/main/webapp/static/javascript/jqueryui/1.10.0/jquery-ui.min.js',
        'src/main/webapp/static/javascript/typeahead/0.9.3/typeahead.min.js',
        'src/main/webapp/static/javascript/angularjs/1.2.11/angular.min.js',
        'src/main/webapp/static/javascript/angularjs/1.2.11/angular-mocks.js',
        'src/main/webapp/static/javascript/angularjs/1.2.11/angular-cookies.min.js',
        'src/main/webapp/static/javascript/angularjs/1.2.11/angular-sanitize.min.js',
        'src/main/webapp/static/javascript/plugins.js',
        'src/main/webapp/static/javascript/orcid.js',
        'src/main/webapp/static/javascript/script.js',
        'src/main/webapp/static/javascript/angularOrcid.js',
        'src/test/javascript/*_tests.js'
    ],


    // list of files to exclude
    exclude: [
      
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
    
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['Chrome'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false
  });
};
