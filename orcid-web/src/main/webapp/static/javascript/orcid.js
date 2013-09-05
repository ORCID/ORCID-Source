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
(function($) {

    var searchFilterChanged = false;
    var showingTemplateMenu = false;    
    
    var footerPlacement = function() {
        if (window.innerHeight > $("body").height()) {
            $(".footer").addClass("fixed");
        }
        $(window).resize(function(e) {
            if (window.innerHeight > $("body").height()) {
                $(".footer").addClass("fixed");
            }
            else {
                $(".footer").removeClass("fixed");
            }
        });
    };

    var handleNews = function() {
        $(".news-section").hide();
        if (window.location.hash) {
            $(".news-filter-header a[href='" + window.location.hash + "']").addClass("active");
            $(window.location.hash).show();
        } else {
            $(".news-filter-header a:first").addClass("active");
            $(".news-section:first").show();
        }
        $(".news-filter-header a").live("click", function(e) {
            if (window.location.href.indexOf("?l") === -1) {
                e.preventDefault();
            }
            $(this).parent().siblings().children("a").removeClass("active");
            $(this).addClass("active");
            var target = $(this).attr("href");
            $(".news-section").hide();
            $(target).show();
        });
    };

    var searchFilters = function() {
        $("input[type=search]").live("focus", function(e) {
            $(".search_options").show();
            $(".conditions").animate({"height":"22px"}, 200);
        });
        $("input[name=huh_radio]").live("click", function(e) {
            searchFilterChanged = true;
        });
        $("input[type=search]").live("blur", function(e) {
            hideSearchFilter();
            setTimeout(function () {
                $(".conditions").animate({"height":"0px"}, 200);
            }, 50);
        });
        var hideSearchFilter = function() {
            if ($("input[type=search]").val() === "") {
                setTimeout(function() {
                    if (searchFilterChanged === false) {
                        $(".search_options").fadeOut(500);
                    }
                }, 3000);
            }
        };
    };

    var toolTips = function() {
        $(".settings-button").tooltip({
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
        $(closables).live("click", function(e) {
            e.preventDefault();
            closePopup();
        });
        $(".open-template-popup").live("click", function(e) {
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
        $(".main > .menu > li.last").on("mouseenter", function(e) {
            $(".main > .menu").css({"background":"#a6ce39"});
        });
        $(".main > .menu > li.last.leaf").on("mouseout", function(e) {
            $(".main > .menu").css({"background":"#338caf"});
        });
    };

    /*============================================================
        Page initialisation
    ============================================================*/

    var init = function() {
        // footerPlacement();
        //handleNews();
        searchFilters();
        //secondaryNavCleanup();
        toolTips();
        popupHandler();
        menuHack();
    };

    init();

})(jQuery);