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
        $("#signout").on("mouseenter", function(e) {
            $(".header .navigation > .menu").css({"background":"#a6ce39"});        
        });
        $("#signout").on("mouseout", function(e) {
            $(".header .navigation > .menu").css({"background":"#338caf"});
        });
    };
    
    /* Mobile Menu Handler */

    var mobileMenuHandler = function(){        
        
        var display = $('#mobile-menu-icon').css('display');    
        var originalMenu = $('.header .navigation').html(); //Menu data
        //var activeTrailMain = null;
         
        /* Managing window resizing for restore visibility of some elements due to Javascript actions over the styles for Mobie or Tablet views */
        
            $(window).bind('resize', function() {
                if(navigator.appVersion.indexOf("MSIE 7.") == -1){ //Not IE7                	
                	
                	var isMac = navigator.platform.toUpperCase().indexOf('MAC')>=0;                	
                	ww = $(window).width();
                	
	                if (ww > 750 && !isMac){ //Tablet ~ PC                
	                    $(".container .header .search form input[type='search']").blur();
	                    restoreDesktopUI();
	                }else if (ww > 752 && isMac){ //Mac OS
	                	$(".container .header .search form input[type='search']").blur();
	                    restoreDesktopUI();	                                    
	                }else{
	                	if(!$(".container .header .search form input[type='search']").is(":focus") && !$('select#language-codes').is(":focus")){ //This is to prevent hiding search and Language selector elements.
	                        hideMenuItems();  
	                    }
	                }
                }
            });    
        
        

        var restoreDesktopUI = function(){
                /* Restoring different elements of the Desktop layout, this is due the menu modification performed to adapt it to mobile devices */                
                if($('#mobile-menu-icon').css('display') == 'none'){
                	$('.container .header .search').css('display', 'block');
                    $('.container .header .search #form-search').css('display', 'block');
                    $('.container .header .search #languageCtrl').css('display', 'block');
                    $('.header .navigation > .menu').css('display', 'block');

                    /*Menu Visibility */
                    $('.header .navigation > .menu > li > .menu').css('display', 'block');

                    /* Restoring Tablet/Desktop Menu content */
                    $('.header .navigation').html(''); //Deleting DOM content
                    $('.header .navigation').prepend(originalMenu);
                    /*$(".container .header .conditions").css('display', 'block');*/
                }
        }
        

        var menuAdjustment = function(){            

            if(display == 'block'){ /* If the Mobile menu is ON */
                

                var topItems = $('.header .navigation > .menu > li > a');
                var topItemsLi = $('.header .navigation > .menu > li');

                var links = $('.header .navigation > .menu > li > .menu > li').has('ul').children('a');
                var toInject = $('.header .navigation > .menu > li > .menu > li > ul.menu');                           
               
                //Inject links to the Second Level
                for( var i = 0; i < topItems.length; ++i){                                
                    $(topItemsLi[i]).children().not("a").prepend('<li class="first expanded"><a href="'+topItems[i].href+'">'+topItems[i].text+'</li>');                    
                }

                for (var i = 0; i < links.length; ++i){
                    $(toInject[i]).prepend('<li class="leaf"><a href="'+links[i].href+'">'+links[i].text+'</li>');                   
                }

                $('.header > .row > .navigation > .menu > li > a:not(:last-child)').not("ul li ul li a").removeAttr('href');

                //Removing links for elements with three level menus.
                $('.header .navigation > .menu > li > .menu > li').has('ul').children('a').attr('href', '');

                

                /* First Level Tap´*/
                $('.header .navigation > .menu > li > a').live('click', function(event){                
                    //var activeTrailMain = $(this);                    
                    $('.header .navigation > .menu > li').removeClass('active-trail');

                });

                /* Second Level Tap */
                $('.header .navigation ul li ul li a').live('click', function(event){
                    event.preventDefault();
                    var hasChildren = $(this).parent().has('ul').length;
                    if(hasChildren){                    
                        var display = $(this).parent().children('ul').css('display');
                        if (display == 'none'){                                        
                            $(this).parent().children('ul').slideDown('slow');                        
                        }else{
                            $(this).parent().children('ul').slideUp('slow');
                        }     
                    }else{
                         window.location = this.href;                
                    }
                });    
            }
        };

        menuAdjustment(); //Trigger menu adjustment

        /* Menu icon */
        $('#mobile-menu-icon').live('click', function(event){        	
            event.preventDefault();
            tap('.container .header .navigation > .menu', this);            
        });        

        /* Search */ 
        $('#mobile-search').live('click', function(event){        	
            event.preventDefault();            
            tap('.container .header #form-search', this);            
        });

        /* Settings */
        $('#mobile-settings').live('click', function(event){
           event.preventDefault();
           tap('.container .header #languageCtrl', this);           
        });
        

        var tap = function(menuObject, menuButton){            
            var display = $(menuObject).css('display');            
            if(display == 'none'){
            	hideMenuItems(menuObject);
            	if($(menuButton).attr('id') == 'mobile-search' || $(menuButton).attr('id') == 'mobile-settings'){
            		$('.container .header #search').css('display', 'block');
            	}
                $(menuObject).css('display','block');
                $(menuButton).css('background','#939598');
            }else{
            	if($(menuButton).attr('id') == 'mobile-search' || $(menuButton).attr('id') == 'mobile-settings'){
            		$('.container .header #search').css('display', 'none');
            	}
                $(menuObject).css('display','none');
                $(menuButton).css('background','#338CAF');                 
            }
        };

        var hideMenuItems = function(menuObject){
            if($(menuObject).hasClass('menu')){ /* Menu button */
                $('.container .header #search').css('display', 'none');                                                
                $('.container .header #form-search').css('display', 'none');
                $('.header .navigation > #mobile-search').css('background', '#338CAF');
                $('.header .navigation > #mobile-settings').css('background','#338CAF');                
            }            
            
            if($(menuObject).attr('id') == 'form-search'){ /* Search Button */            
                $('.header .navigation > .menu').css('display', 'none');            
                $('#mobile-menu-icon').css('background', '#338CAF');
                $('.header .navigation > #mobile-settings').css('background', '#338CAF');
                $('#languageCtrl').css('display', 'none');
            
            }

            if($(menuObject).attr('id') == 'languageCtrl'){ /* Language Button */            
                $('.header .navigation > .menu').css('display', 'none');
                $('.container .header #search').css('display', 'none');	
                $('.container .header #form-search').css('display', 'none');
                $('#mobile-menu-icon').css('background', '#338CAF');
                $('#mobile-search').css('background', '#338CAF');
            
            }
            
            if(typeof menuObject == 'undefined'){
            
                $('.header .navigation > .menu').css('display', 'none');            
                $('.container .header #search').css('display', 'none');                
                $('.container .header .search #languageCtrl').css('display', 'none');
            
                $('.header .navigation > .mobile-menu-icon').css('background','#338CAF');
                $('.header .navigation > .mobile-search').css('background','#338CAF');
                $('.header .navigation > .mobile-settings').css('background','#338CAF');                            
            } 
        	
        };
    };    

    /*============================================================
        Page initialisation
    ============================================================*/

    var init = function() {
        //handleNews();
        searchFilters();
        //secondaryNavCleanup();
        toolTips();
        popupHandler();
        menuHack();
        mobileMenuHandler();      
    };

    init();

})(jQuery);