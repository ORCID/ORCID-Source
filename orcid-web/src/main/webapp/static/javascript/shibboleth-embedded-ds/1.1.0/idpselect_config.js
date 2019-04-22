 
/** @class IdP Selector UI */
function IdPSelectUIParms(){
    //
    // Adjust the following to fit into your local configuration
    //
    this.alwaysShow = true;          // If true, this will show results as soon as you start typing
    this.dataSource = '/Shibboleth.sso/DiscoFeed';   // Where to get the data from
    this.defaultLanguage = 'en';     // Language to use if the browser local doesnt have a bundle
    this.defaultLogo = '/static/img/idp-placeholder-logo.png';  // Replace with your own logo
    this.defaultLogoWidth = 1;
    this.defaultLogoHeight = 1 ;
    
    this.currentLocation = window.location.href;
    
  	this.defaultReturn = getBaseUri() + '/Shibboleth.sso/Login?SAMLDS=1&target=' + getBaseUri() + '/shibboleth/signin';       // If non null, then the default place to send users who are not 
    
    // Approaching via the Discovery Protocol for example
    //this.defaultReturn = "https://example.org/Shibboleth.sso/DS?SAMLDS=1&target=https://example.org/secure";
    this.ignoreURLParams = true;
    this.defaultReturnIDParam = null;
    this.helpURL = 'https://wiki.shibboleth.net/confluence/display/SHIB2/DSRoadmap';
    this.ie6Hack = null;             // An array of structures to disable when drawing the pull down (needed to 
                                     // handle the ie6 z axis problem
    this.insertAtDiv = 'idpSelect';  // The div where we will insert the data
    this.maxResults = 10;            // How many results to show at once or the number at which to
                                     // start showing if alwaysShow is false
    this.myEntityID = null;          // If non null then this string must match the string provided in the DS parms
    this.preferredIdP = null;        // Array of entityIds to always show
    this.hiddenIdPs = null;          // Array of entityIds to delete
    this.ignoreKeywords = false;     // Do we ignore the <mdui:Keywords/> when looking for candidates
    this.showListFirst = false;      // Do we start with a list of IdPs or just the dropdown
    this.samlIdPCookieTTL = 730;     // in days
    this.setFocusTextBox = true;     // Set to false to supress focus 
    this.testGUI = false;

    this.langBundles = {
        // Override all lang bundles with localized messages from ORCID back end. The user will get the language they have chosen from the drop down in the ORCID UI.
        en : messages,
        de : messages,
        ja : messages,
        "pt-br" : messages
    }
    //
    // The following should not be changed without changes to the css.  Consider them as mandatory defaults
    //
    this.maxPreferredIdPs = 3;
    this.maxIdPCharsButton = 33;
    this.maxIdPCharsDropDown = 58;
    this.maxIdPCharsAltTxt = 60;

    this.minWidth = 20;
    this.minHeight = 20;
    this.maxWidth = 115;
    this.maxHeight = 69;
    this.bestRatio = Math.log(80 / 60);
}
// Stop ugly error alerts. The error will be displayed inline anyway.
window.alert = function(message) {
    console.log("Alert: " + message);
};