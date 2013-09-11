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

//IE7 hack
if ( ! window.console ) console = { log: function(){} };

// add new method to string
if (typeof String.prototype.startsWith != 'function') {
  String.prototype.startsWith = function (str){
    return this.slice(0, str.length) == str;
  };
}

//add new method to string
if (typeof String.prototype.endsWith != 'function') {
	  String.prototype.endsWith = function (str){
	    return this.slice(-str.length) == str;
	  };
}

if (typeof String.prototype.trim != 'function') {  
  String.prototype.trim = function () {  
	return this.replace(/^\s+|\s+$/g,'');  
  };  
}

// This is to prevent IE from caching ajax request via jquery
$.ajaxSetup({ cache: false });

// function for javascript cookies
var OrcidCookie = new function () {
    this.getCookie =  function (c_name) {
    	var i,x,y,ARRcookies=document.cookie.split(";");
    	for (i=0;i<ARRcookies.length;i++) {
    	    x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
    	    y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
    	    x=x.replace(/^\s+|\s+$/g,"");
    	    if (x==c_name) {
    	       return unescape(y);
    	    }
    	}
    };
    
    this.setCookie =  function (c_name,value,exdays) {
       var exdate=new Date();
       exdate.setDate(exdate.getDate() + exdays);
       var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
       document.cookie=c_name + "=" + c_value + ";path=/";
    };
};

var OrcidGA = function () {	
	this.gaPush = function (trackArray) {
		if (typeof _gaq != 'undefined') {
			_gaq.push(trackArray);
			console.log("_gap.push for " + trackArray);
		} else {
			// if it's a function and _gap isn't available run (typically only on dev)
			if (typeof trackArray === 'function') trackArray();	
			console.log("no _gap.push for " + trackArray);
		}
	};
	
    // Delays are async functions used to make sure event track que has cleared
	// See https://developers.google.com/analytics/devguides/collection/gajs/methods/gaJSApi_gaq
	//
	// Additionally adding in delay: http://support.google.com/analytics/answer/1136920?hl=en
	this.gaFormSumbitDelay = function ($el) {
		if (!$el instanceof jQuery) {
			$el = $(el);
		}
		this.gaPush(function() {
			console.log("_gap.push executing $el.submit()");
			setTimeout(function() {
			    $el.submit();
				}, 100); 
		});
		return false;
	};
	
	this.windowLocationHrefDelay = function (url) {
		this.gaPush(function() {
			console.log("_gap.push has executing window.location.href " + url);
			setTimeout(function() {
				window.location.href = url;
				}, 100); 
		});
		return false;
	};
};

var orcidGA = new OrcidGA();

var OrcidMessage = function (messageUrl) {
	this.localData = null;
	this.load(messageUrl);
};

OrcidMessage.prototype.load = function (messageUrl) {
	(function (orcidMessage) {
		$.ajax({
			url:messageUrl,
			async: false,
			dataType: 'json',
			success:function(data) {
			    orcidMessage.localData = data;
	        }
		});
	})(this);
};

OrcidMessage.prototype.get =  function (name) {
	return this.localData.messages[name];
};

OrcidMessage.instance = null;

OrcidMessage.getInstance = function() {
	console.log("OrcidMessage singleton"); 
	if (this.instance == null) {
		messageUrl = orcidVar.baseUri + "/lang.json";	
		return this.instance = new OrcidMessage(messageUrl);
	}
	return this.instance;
};

/* 
 * every 15 seconds check and make sure
 * the user is logged in. This should keep 
 * their session going and if they get logged
 * out (server restart ect...) it will redir 
 * them to the signin page.
 */
function checkOrcidLoggedIn() {
	$.ajax({
		url: orcidVar.baseUri + '/userStatus.json?callback=?',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
        	if ( data.loggedIn == false
        			&& (basePath.startsWith(baseUrl + 'my-orcid')
        			     || basePath.startsWith(baseUrl + 'account'))) {
        		console.log("loggedOutRedir " + data);
        		window.location.href = baseUrl + "signin"; 
        	}
        	
        }
    }).fail(function() { 
    	// something bad is happening!
    	console.log("error with loggin check on :" + window.location.href);
    });

}

var OM = OrcidMessage;


/* used for triming org.orcid.pojo.ajaxForm.Text *
 * trim the value if it has spaces */
function trimAjaxFormText(pojoMember) {
  if (pojoMember != null 
		  && pojoMember.value != null
		  && ( pojoMember.value.charAt(0) == ' '
				  || pojoMember.value.charAt(pojoMember.value.length - 1) == ' '))
	  pojoMember.value = pojoMember.value.trim();
}


// jquery ready
$(function () {
	
	// Common
	
	window.baseUrl = $('body').data('baseurl');
    window.basePath = window.location.pathname; 	
    
    // fire off login check, if this page wasn't loaded via iframe (or html5 foo)
    if (location == parent.location) {
        checkOrcidLoggedIn();
        setInterval(checkOrcidLoggedIn,15000);
    }    
    
    // if not iframed check if not orcid.org
    if (location == parent.location && window.location.hostname.toLowerCase() != "orcid.org") {
     	
    	var cookieName = "testWarningCookie";
    	var warnMessCookie=OrcidCookie.getCookie(cookieName);
    	if (!warnMessCookie) {
    		var wHtml = '<div class="alert" id="test-warn-div">';
    			wHtml = wHtml + '<strong>';
    			wHtml = wHtml + OM.getInstance().get('common.js.domain.warn.template').replace('{{curentDomian}}',window.location.hostname);
    			wHtml = wHtml + '</strong> ';
    			//don't let the warning be disabled for test-warn-dismiss
    			if (window.location.hostname.toLowerCase() != "sandbox-1.orcid.org") {
    				wHtml = wHtml + ' <div style="float: right" class="small"><a href="#" id="test-warn-dismiss">'
    				wHtml = wHtml + OM.getInstance().get('common.cookies.click_dismiss');
    				wHtml = wHtml + '</a></div>';
    			}
    			wHtml = wHtml + '</div>';
    		$(wHtml).insertBefore('body');
    		$("#test-warn-dismiss").click(function(){
    			$("#test-warn-div").remove();
    			OrcidCookie.setCookie(cookieName,"dont show message",365);
    			return false;
    		});
    	}    
 
    }
     
    $('#denialForm').submit(function() {
    	if (window.location != window.parent.location) parent.$.colorbox.close();
    	return true;
    });
    
    // track when deactived people are pushed to signin page
    if (window.location.href.endsWith("signin#deactivated")) {
    	orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Deactivate_Complete', 'Website']);
    }
    
    // if on signin or register do cookie check
	if ( basePath.startsWith(baseUrl + 'register') 
    	 || basePath.startsWith(baseUrl + 'signin')
    	 || basePath.startsWith(baseUrl + 'oauth/signin')) {

		OrcidCookie.setCookie("cookieTest","test",1);
		if (OrcidCookie.getCookie("cookieTest")) {
			// delete test cookie
			OrcidCookie.setCookie("cookieTest","test",-1);
		} else {
			$('#cookie-check-msg').css("display","inline");	
		}
    }
		
	// jquery browser is deprecated, when you upgrade 
	// to 1.9 or higher you will need to use the pluggin
	var oldBrowserFlag =  false;

	if ($.browser.msie && parseInt($.browser.version,10)<8) {
		oldBrowserFlag = true;
	} else if (/chrom(e|ium)/.test(navigator.userAgent.toLowerCase()) && parseInt($.browser.version,10)<22 ) {
		oldBrowserFlag = true;
    } else if ($.browser.mozilla && parseInt($.browser.version,10)<15) {
    	oldBrowserFlag = true;
    }  else if ($.browser.opera && parseInt($.browser.version,10)<12) {
    	oldBrowserFlag = true;
    } else if ($.browser.safari && parseInt($.browser.version,10)<6) {
    	oldBrowserFlag = true;
    }
	
	if (oldBrowserFlag && location == parent.location) {
		var wHtml = '<div class="alert" id="browser-warn-div">';
			wHtml = wHtml + '<strong>'; 
			wHtml = wHtml + OM.getInstance().get('common.old.browser');
			wHtml = wHtml + '</strong>';
			wHtml = wHtml + '</div>';
			$('body').prepend(wHtml);
	}
	
	
	$('form#loginForm').submit(function() {
		if($('form#loginForm').attr('disabled')){
			return false;
		}
		if (basePath.startsWith(baseUrl + 'oauth')) 
		    orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'Sign-In-Submit', 'OAuth']);
	    else
	    	orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'Sign-In-Submit', 'Website']);	
		$('form#loginForm').attr('disabled', 'disabled');
		$('#ajax-loader').show();
		$.ajax({
	        url: baseUrl + 'signin/auth.json',
	        type: 'POST',
	        data: $('form#loginForm').serialize(),
	        dataType: 'json',
	        success: function(data) {
	        	$('#ajax-loader').hide();
	        	$('form#loginForm').removeAttr('disabled');
	            if (data.success) {
	        	    if (basePath.startsWith(baseUrl + 'oauth/signin')) 
	        		    orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'Sign-In', 'OAuth']);
	        	    else
	        	    	orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'Sign-In', 'Website']);
	        	    orcidGA.windowLocationHrefDelay(data.url);
	            } else {
	            	if ($('form#loginForm #login-error-mess').length == 0) {
	            		var message;
	            		if(data.unclaimed){
	            			var resendClaimUrl = window.location + "/../resend-claim";
	            			var userId = $('#userId').val();
                            if(userId.indexOf('@') != -1){
	            		        resendClaimUrl += '?email=' + encodeURIComponent(userId);	
	            		    }
	            		    message = OM.getInstance().get('orcid.frontend.security.unclaimed_exists').replace("{{resendClaimUrl}}",resendClaimUrl);  
	            		} else if(data.deprecated){
	            			if(data.primary)
	            				message = OM.getInstance().get('orcid.frontend.security.deprecated_with_primary').replace("{{primary}}", data.primary);	            				
	            			else
	            				message = OM.getInstance().get('orcid.frontend.security.deprecated');
	            		} else{
	            			message = OM.getInstance().get('orcid.frontend.security.bad_credentials'); 
	            		}
		            	$("<div class='alert' id='login-error-mess'>"+ message + "</div>")
		            	    .hide()
		            	    .appendTo('form#loginForm')
		            	    .fadeIn('fast');
	            	} else {
	            		$('form#loginForm #login-error-mess').fadeOut('fast', 
	            				function() { 
	            					$($('form#loginForm #login-error-mess')).fadeIn('fast');
	            		});
	            	}
	        	};
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	window.location.reload();
	    });
		return false;
	});
	
	    
    $('.delete-url').live('click', function (e) {
        e.preventDefault();
        $(this).closest('p').fadeOut(300, function() { $(this).closest('p').remove(); });
    });
	
	var hideThing = function (e, selector, className) {
		var p = $(selector + "." + className);
		if (p.length == 0) {
			return;
		}
		if (!$.contains(p.get(0), e.target)) {
			p.removeClass(className);
		}
	};
	
	// Privacy toggle

	$('body').on('mousedown', function (e) {
		hideThing(e, '.privacy-group', 'open');
		hideThing(e, '.popover', 'show');
	});
		
	$('body').on('mousedown', function (e) {
		hideThing(e, '.privacy-group', 'open');
		hideThing(e, '.popover', 'show');
	});
	
	
	var btnClassR = /(btn-\w+)/;
	
	var getBtnClass = function (el) {
		var r = btnClassR.exec(el.className);
		return (r ? r[0]: "");
	};

	function privacyBind() {
		
		$('.privacy-group').each(function (i, el) {
			var $el = $(el),
				current = "",
				toggle = $el.find('.privacy-toggle');
			$el.on('click', '.privacy-toggle', function (e) {
				e.preventDefault();
				if ($el.hasClass('open')) {
					return $el.removeClass('open');
				}
				$('.privacy-group.open').removeClass('open');
				current = getBtnClass(this);
				$el.toggleClass('open');
			});
			$el.on('click', '.btn-privacy', function (e) {
				e.preventDefault();
				var f = toggle.closest('form');
				var s;
				var priAct = $(this).attr('href').replace("#","");
				if (f.length && (f.attr('action') == 'save-current-works')) {
					s = $('select', toggle.closest('label'));
					s.val(priAct);
                    showChangeMessage();
                    $el.removeClass('open');
                    toggle.removeClass(current).addClass(getBtnClass(this));
                    toggle.html($(this).html());
				} else {
					var s = toggle.closest('.privacy-tool').prev('.visibility-lbl').find('select');
					if (s.length) {
						s.val(priAct);
					}
					toggle.removeClass(current).addClass(getBtnClass(this));
					toggle.html($(this).html());
					$el.removeClass('open');
				}
			});
		});
		
	}
	
	privacyBind();


	
	var ps = $(".password-strength").passStrength();
	ps.on('keyup', function (e) {
		if ((location != parent.location) && !this.changed) {
			var i = $('.popover.show iframe', parent.document);
			i.height(i.contents().height());
			this.changed = 1;
		}
	});

	// Manage
	
	// Popovers
		
	if (parent !== window) {
		var popover = parent.$('.popover-large:visible');
		if (popover.length) {
			popover.find('iframe').height($('body').outerHeight());
		}
	}
	
	
	// Workspace
	
	// lightboxes
	
	$('.colorbox').colorbox();
	
	
	top.colorOnCloseBoxDest = top.location;
	
	$('.btn-update:not(.no-icon),.update').colorbox({
		iframe: true,
		height: 600,
		width: 990,
		close: '',
		onClosed: function () {
			top.location = top.colorOnCloseBoxDest;
		}
	});
	
	$('#upate-personal-modal-link').colorbox({
		iframe: true,
		height: 600,
		width: 990,
		close: '',
		onClosed: function () {
			top.location = top.colorOnCloseBoxDest;
		}
	});
	
	$('.colorbox-modal').colorbox({
		inline: true,
		close: 'x'
	});
		
	$('body').on('click', '.colorbox-close', function (e){
		$.colorbox.close();
	});
	
	if ($('#loginForm').length && (window.location != window.parent.location)) {
		window.parent.location.reload();
	}
	
    $('.close-button').on('click', function(e) {
        parent.location.reload();
    });
	
	$('.colorbox-add').colorbox({
		height: 400,
		href: baseUrl + "account/search-for-delegates #add-an-individual"
	});
	
	
	$('.workspace-header').on('click', '.overview-title,.overview-count', function (e) {
		e.preventDefault();
		var el = $($(this).attr('href')).addClass('workspace-accordion-active');
		el.find('.workspace-accordion-content').slideDown(150);
		$('body,window').animate({ 
			scrollTop: el.offset().top
		}, 500);
	});
	
	
	
	// Search hack
	
	$('#form-search').on('submit', function (e){
		if ($('[name="huh_radio"]:checked', this).val() === "registry") {
			e.preventDefault();
			window.location = baseUrl + "orcid-search/quick-search/?searchQuery=" + $('[type="search"]', this).val();
		}
	});
			
	
	
	// delgates
	$('#searchForDelegatesForm').live('submit', function(e){
		e.preventDefault();
		console.log($(this).serialize(), baseUrl + 'manage/search-for-delegates')
		$.post(baseUrl + 'manage/search-for-delegates', $(this).serialize(), function(data) {
			$('#searchResults').html(data);
		});
	});
	
});