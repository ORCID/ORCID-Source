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
(function($) {
 

    var showingTemplateMenu = false;

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

    var secondaryNavCleanup = function() {
        var items = $(".main > .menu > li.active-trail > ul > li");
        var count = $(items).length;
        var current = $(".main > .menu > li.active-trail").find("li.active-trail a:first").parent().index() + 1;
        if (items) {
            if (current !== count && $(items).children("a").hasClass("active")) {
                $(items[current]).children("a").css({"border-left-color":"#fff"});
            }
            if (current === count) {
                $(items[current-1]).css({"border-right-color" : "#fff"});
            }
        }
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

    var menuHack = function() {
        $("#signin").on("mouseenter", function(e) {
            $(".header .navigation>.menu").css({"background":"#a6ce39"});        
        });
        $("#signin").on("mouseout", function(e) {
            $(".header .navigation > .menu").css({"background":"#338caf"});
        });
    };
    
    /*============================================================
        Page initialisation
    ============================================================*/

    var init = function() {
        toolTips();
        popupHandler();
        //menuHack();        
        //menuHandler();
    };

    
    
    init();

})(jQuery);

/*============================================================
Print public record
============================================================*/
function printPublicRecord(url){
    $('#printRecord').click(function(evt) {
        evt.preventDefault();
        $('body').append('<iframe src="' + url + '" id="printRecordFrame" name="printRecordFrame"></iframe>');
        $('#printRecordFrame').bind('load', 
            function() { 
                window.frames['printRecordFrame'].focus(); 
                window.frames['printRecordFrame'].print(); 
            }
        );
    }); 
}