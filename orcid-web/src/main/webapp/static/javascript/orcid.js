GroupedActivities = {}
GroupedActivities.FUNDING = 'funding';
GroupedActivities.ABBR_WORK = 'abbrWork';
GroupedActivities.PEER_REVIEW = 'peerReview';
GroupedActivities.AFFILIATION = 'affiliation';
GroupedActivities.NG2_AFFILIATION = 'ng2_affiliation';


var ActSortState = function(groupType) {
    var _self = this;
    _self.type = groupType;    
    
    _self.reverseKey = {};
    _self.reverseKey['date']  = true;
    _self.reverseKey['title'] = false;
    _self.reverseKey['type']  = false;
    _self.reverseKey['groupName']  = false;    
    _self.reverseKey['startDate']  = false;
    _self.reverseKey['endDate']  = false;
    
    _self.predicateKey = 'date';
    if (_self.type == 'peerReview') {
        _self.predicateKey = 'groupName';
    } else if (_self.type == 'affiliation') {
        _self.predicateKey = 'endDate';
        _self.reverseKey['date']  = false;
        _self.reverseKey['endDate']  = true;        
    } else if (_self.type == 'ng2_affiliation') {
        _self.predicateKey = 'endDate';
        _self.reverseKey['endDate']  = true;
    }
    
    _self.predicate = this.predicateMap[_self.type][_self.predicateKey];
};

var sortPredicateMap = {};
sortPredicateMap[GroupedActivities.ABBR_WORK] = {};
sortPredicateMap[GroupedActivities.ABBR_WORK]['date'] = ['-dateSortString', 'title','getDefault().workType.value'];
sortPredicateMap[GroupedActivities.ABBR_WORK]['title'] = ['title', '-dateSortString','getDefault().workType.value'];
sortPredicateMap[GroupedActivities.ABBR_WORK]['type'] = ['getDefault().workType.value','title', '-dateSortString'];

sortPredicateMap[GroupedActivities.FUNDING] = {};
sortPredicateMap[GroupedActivities.FUNDING]['date'] = ['dateSortString', 'title','groupType'];
sortPredicateMap[GroupedActivities.FUNDING]['title'] = ['title', 'dateSortString','groupType'];
sortPredicateMap[GroupedActivities.FUNDING]['type'] = ['groupType','title', 'dateSortString'];

sortPredicateMap[GroupedActivities.AFFILIATION] = {};
sortPredicateMap[GroupedActivities.AFFILIATION]['endDate'] = ['-dateSortString', 'title'];
sortPredicateMap[GroupedActivities.AFFILIATION]['startDate'] = ['-getDefault().startDate.year', '-getDefault().startDate.month', '-getDefault().startDate.day', 'title'];
sortPredicateMap[GroupedActivities.AFFILIATION]['title'] = ['title', '-dateSortString'];

sortPredicateMap[GroupedActivities.PEER_REVIEW] = {};
sortPredicateMap[GroupedActivities.PEER_REVIEW]['groupName'] = ['groupName'];

sortPredicateMap[GroupedActivities.NG2_AFFILIATION] = {};
sortPredicateMap[GroupedActivities.NG2_AFFILIATION]['endDate'] = ['dateSortString', 'affiliationName.value'];
sortPredicateMap[GroupedActivities.NG2_AFFILIATION]['startDate'] = ['startDate.year', 'startDate.month', 'startDate.day', 'affiliationName.value'];
sortPredicateMap[GroupedActivities.NG2_AFFILIATION]['title'] = ['affiliationName.value', 'dateSortString'];

ActSortState.prototype.predicateMap = sortPredicateMap;

ActSortState.prototype.sortBy = function(key) { 
    if (this.predicateKey == key){
       this.reverse = !this.reverse;
       this.reverseKey[key] = !this.reverseKey[key];           
    }
    this.predicateKey = key;
    this.predicate = this.predicateMap[this.type][key];
};

(function($) {
 
    var toolTips = function(){
        $(".settings-button").tooltip({
            placement: "bottom"
        });       
                
        $(".back").tooltip({
            placement: "bottom"
        });
        
        $(".save").tooltip({
            placement: "bottom"
        });
        
        $(".edit").tooltip({
            placement: "bottom"
        });
        
        $(".revoke").tooltip({
            placement: "bottom"
        });
        
        $(".add").tooltip({
            placement: "bottom"
        });
        
        $(".delete").tooltip({
            placement: "bottom"
        });
    };

    var popupHandler = function() {
        positionPopup();
        $(".template-darken").fadeOut(1);
        $(".template-box").fadeOut(1);
        var closables = $(".close-template-popup, .template-darken");
        $(closables).on("click", function(e) {
            e.preventDefault();
            closePopup();
        });
        $(".open-template-popup").on("click", function(e) {
            e.preventDefault();
            openPopup();
        });
        $(window).resize(function() {
            positionPopup();
        });
    };

    var closePopup = function() {
        $(".template-darken").fadeOut(500);
        $(".template-box").fadeOut(500);

    };

    var openPopup = function() {
        $(".template-popup").show();
        $(".template-darken").fadeIn(500);
        $(".template-box").fadeIn(500);
    };

    var positionPopup = function() {
        var templateBox = $(".template-box");
        var w = $(templateBox).outerWidth();
        var h = $(templateBox).outerHeight();
        var ww = $(window).innerWidth();
        var wh = $(window).innerHeight();
        $(templateBox).css({
            "left": (ww/2) - (w/2) + "px",
            "top": (wh/2) - (h/2) + "px"
        });
    };

    /*============================================================
        Page initialisation
    ============================================================*/

    var init = function() {
        toolTips();
        popupHandler();
    };
    init();

})(jQuery);