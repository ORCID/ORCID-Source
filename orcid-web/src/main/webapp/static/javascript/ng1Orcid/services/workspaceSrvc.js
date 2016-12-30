angular.module('orcidApp').factory("workspaceSrvc", ['$rootScope', function ($rootScope) {
    var serv = {
        displayEducation: true,
        displayEmployment: true,
        displayFunding: true,
        displayPersonalInfo: true,
        displayWorks: true,
        displayPeerReview: true,
        toggleEducation: function() {
            serv.displayEducation = !serv.displayEducation;
        },
        toggleEmployment: function() {
            serv.displayEmployment = !serv.displayEmployment;
        },
        toggleFunding: function() {
            serv.displayFunding = !serv.displayFunding;
        },
        togglePersonalInfo: function() {
            serv.displayPersonalInfo = !serv.displayPersonalInfo;
        },
        toggleWorks: function() {
            serv.displayWorks = !serv.displayWorks;
        },
        togglePeerReview: function() {              
            serv.displayPeerReview = !serv.displayPeerReview;
        },
        openEducation: function() {
            serv.displayEducation = true;
        },
        openFunding: function() {
            serv.displayFunding = true;
        },
        openEmployment: function() {
            serv.displayEmployment = true;
        },
        openPersonalInfo: function() {
            serv.displayPersonalInfo = true;
        },
        openWorks: function() {
            serv.displayWorks = true;
        },
        openPeerReview: function() {
            serv.displayPeerReview = true;
        },
        togglePeerReviews : function() {
            serv.displayPeerReview = !serv.displayPeerReview;
        }   
    };
    return serv;
}]);