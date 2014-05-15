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
if (!(window.console && console.log)) {
	console = {
		log : function() {
		},
		debug : function() {
		},
		info : function() {
		},
		warn : function() {
		},
		error : function() {
		}
	};
};

// add number padding function 
Number.prototype.pad = function(size) {
    var s = String(this);
    if(typeof(size) !== "number"){size = 2;}

    while (s.length < size) {s = "0" + s;}
    return s;
}

// add new method to string
if (typeof String.prototype.startsWith != 'function') {
  String.prototype.startsWith = function (str){
    return this.slice(0, str.length) == str;
  };
}

//add new method to string
if (typeof String.prototype.contains != 'function') {
	  String.prototype.contains = function (str){
	    return this.indexOf(str) != -1; 
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

//test for touch events support and if not supported, attach .no-touch class to the HTML tag.
if (!("ontouchstart" in document.documentElement)) {
document.documentElement.className += " no-touch";
}

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
	this.buildClientString = function (clientGroupName, clientName) {
		return  clientGroupName + ' - '+ clientName
	};
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

var OrcidMessage = function () {
	//nothing to init now
};

OrcidMessage.prototype.get =  function (name) {
	return orcidVar.jsMessages.messages[name];
};

var om = new OrcidMessage();

/* 
 * every 15 seconds check and make sure
 * the user is logged in. This should keep 
 * their session going and if they get logged
 * out (server restart ect...) it will redir 
 * them to the signin page.
 */
function getBaseUri() {
	return 'https:' == document.location.protocol ? orcidVar.baseUri : orcidVar.baseUriHttp;
}


function checkOrcidLoggedIn() {
	$.ajax({
		url: getBaseUri() + '/userStatus.json?callback=?',
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

function logOffReload() {
	$.ajax({
        url: baseUrl + 'userStatus.json?logUserOut=true',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
        	window.location.reload();
        }
	}).fail(function() { 
    	// something bad is happening!
    	window.location.reload();
    });
};


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
    			wHtml = wHtml + om.get('common.js.domain.warn.template').replace('{{curentDomian}}',window.location.hostname);
    			wHtml = wHtml + '</strong> ';
    			//don't let the warning be disabled for test-warn-dismiss
    			if (window.location.hostname.toLowerCase() != "sandbox-1.orcid.org" && window.location.hostname.toLowerCase() != "sandbox.orcid.org") {
    				wHtml = wHtml + ' <div style="float: right" class="small"><a href="#" id="test-warn-dismiss">'
    				wHtml = wHtml + om.get('common.cookies.click_dismiss');
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

	if (!!navigator.userAgent.match(/Trident\/7\./)) {
		//IE 11
		oldBrowserFlag = false; 
	} else if ($.browser.msie && parseInt($.browser.version,10)<8) {
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
			wHtml = wHtml + om.get('common.old.browser');
			wHtml = wHtml + '</strong>';
			wHtml = wHtml + '</div>';
			$('body').prepend(wHtml);
	}
	
	
	$('form#loginForm').submit(function() {
		if($('form#loginForm').attr('disabled')){
			return false;
		}
		if (basePath.startsWith(baseUrl + 'oauth')) { 
			var clientName = $('form#loginForm input[name="client_name"]').val();
			var clientGroupName = $('form#loginForm input[name="client_group_name"]').val();
		    orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'Sign-In-Submit', 'OAuth ' + orcidGA.buildClientString(clientGroupName, clientName)]);
		} else
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
	        	    if (basePath.startsWith(baseUrl + 'oauth/signin')) {
	        	    	var clientName = $('form#loginForm input[name="client_name"]').val();
	        	    	var clientGroupName = $('div#RegistrationCtr input[name="client_group_name"]').val();
	        		    orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'Sign-In', 'OAuth '+ orcidGA.buildClientString(clientGroupName, clientName)]);
	        	    } else
	        	    	orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'Sign-In', 'Website']);
	        	    orcidGA.windowLocationHrefDelay(data.url + window.location.hash);
	            } else {
	            	if ($('form#loginForm #login-error-mess').length == 0) {
	            		var message;
	            		if(data.deprecated){
	            			if(data.primary)
	            				message = om.get('orcid.frontend.security.deprecated_with_primary').replace("{{primary}}", data.primary);	            				
	            			else
	            				message = om.get('orcid.frontend.security.deprecated');
	            		} else if(data.unclaimed){
	            			var resendClaimUrl = window.location + "/../resend-claim";
	            			var userId = $('#userId').val();
                            if(userId.indexOf('@') != -1){
	            		        resendClaimUrl += '?email=' + encodeURIComponent(userId);	
	            		    }
	            		    message = om.get('orcid.frontend.security.unclaimed_exists').replace("{{resendClaimUrl}}",resendClaimUrl);  
	            		} else{
	            			message = om.get('orcid.frontend.security.bad_credentials'); 
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
	
	$('.btn-update:not(.no-icon), .update, #update-personal-modal-link').colorbox({
		iframe: true,
		scrolling: true,		
		height: function(){
			 isMobile() ? heightsize = 1150 : heightsize = 600;
			return heightsize;
		},
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
	
	// Search hack
	
	$('#form-search').on('submit', function (e){
		if ($('[name="huh_radio"]:checked', this).val() === "registry") {
			e.preventDefault();
			window.location = baseUrl + "orcid-search/quick-search/?searchQuery=" + encodeURIComponent($('[type="search"]', this).val());
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

/* START: workIdLinkJs v0.0.5 */
/* https://github.com/ORCID/workIdLinkJs */

/* browser and NodeJs compatible */
(function(exports){

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

   //add new method to string
   if (typeof String.prototype.trim != 'function') {  
      String.prototype.trim = function () {  
         return this.replace(/^\s+|\s+$/g,'');  
      };  
   }

   var typeMap = {};
   
   typeMap.hasOwnProperty = function(property) {
      return typeMap[property] !== undefined;
   };
   
   typeMap['arxiv'] = function (id) {
      if (id.toLowerCase().startsWith('arxiv.org')) return 'http://' + id;
      if (id.startsWith('arXiv:')) return 'http://arxiv.org/abs/' + id.substring(6);
      return 'http://arxiv.org/abs/' + id;
   };
   
   typeMap['asin'] = function (id) {
      if (id.toLowerCase().startsWith('amazon.') || id.startsWith('www.amazon.')) return 'http://' + id;
      return 'http://www.amazon.com/dp/' + id;
   };

   typeMap['bibcode'] = function (id) {
      if (id.toLowerCase().startsWith('adsabs.harvard.edu')) return 'http://' + id;
      return 'http://adsabs.harvard.edu/abs/' + id;
   };
   
   typeMap['doi'] = function (id) {
      if (id.toLowerCase().startsWith('dx.doi.org') || id.startsWith('dx.doi.org')) return 'http://' + id;
      return 'http://dx.doi.org/' + id;
   };

   typeMap['isbn'] = function (id) {
      if (id.toLowerCase().startsWith('amazon.com/dp/') || id.toLowerCase().startsWith('www.worldcat.org')) return 'http://' + id;
      return 'http://www.worldcat.org/isbn/' + id.replace(/\-/g, '');
   };

   typeMap['jfm'] = function (id) {
      if (id.toLowerCase().startsWith('www.zentralblatt-math.org')) return 'http://' + id;
      return 'http://www.zentralblatt-math.org/zmath/en/search/?q=an:' + id + '&format=complete';
   };

   typeMap['jstor'] = function (id) {
      if (id.toLowerCase().startsWith('dx.doi.org') || id.startsWith('www.jstor.org')) return 'http://' + id;
      return 'http://www.jstor.org/stable/' + id;
   };

   typeMap['lccn'] = function (id) {
      if (id.toLowerCase().startsWith('lccn.loc.gov')) return 'http://' + id;
      return 'http://lccn.loc.gov/' + id;
   };

   typeMap['mr'] = function (id) {
      id = id.match(/[^\(]*/)[0];
      if (id.toLowerCase().startsWith('ams.org/mathscinet-getitem')) return 'http://' + id;
      return 'http://www.ams.org/mathscinet-getitem?mr=' + id;
   };

   typeMap['oclc'] = function (id) {
      if (id.toLowerCase().startsWith('worldcat.org')) return 'http://' + id;
      return 'http://www.worldcat.org/oclc/' + id;
   };

   typeMap['ol'] = function (id) {
      if (id.toLowerCase().startsWith('openlibrary.org/b/')) return 'http://' + id;
      return 'http://openlibrary.org/b/' + id;
   };
 
   typeMap['osti'] = function (id) {
      if (id.toLowerCase().startsWith('www.osti.gov')) return 'http://' + id;
      return 'http://www.osti.gov/energycitations/product.biblio.jsp?osti_id=' + id;
   };

   typeMap['pmc'] = function (id) {
      if (id.toLowerCase().startsWith('pmc')) return 'http://europepmc.org/articles/' + id;
      if (id.toLowerCase().startsWith('www.ncbi.nlm.nih.gov')) return 'http://' + id;
      return 'http://www.ncbi.nlm.nih.gov/pubmed/' + id;
   };

   /* 
    * We need a method of determining www.ncbi.nlm.nih.gov identifiers
    * vs europepmc.org identifiers
    * http://www.ncbi.nlm.nih.gov/pubmed/
    * http://europepmc.org/abstract/med/
    */
   typeMap['pmid'] = function (id) {
      if (id.toLowerCase().startsWith('www.ncbi.nlm.nih.gov')) return 'http://' + id;
      if (id.toLowerCase().startsWith('europepmc.org')) return 'http://' + id;
      return 'http://europepmc.org/abstract/med/' + id;
   };

   typeMap['rfc'] = function (id) {
      id = id.replace(/\s/g,'');
      id = id.toLowerCase();
      if (id.toLowerCase().startsWith('www.rfc-editor.org/rfc/')) return 'http://' + id;
      return 'http://www.rfc-editor.org/rfc/' + id + '.txt';
   };

   typeMap['ssrn'] = function (id) {
      if (id.toLowerCase().startsWith('papers.ssrn.com')) return 'http://' + id;
      return 'http://papers.ssrn.com/abstract_id=' + id;
   };

   typeMap['zbl'] = function (id) {
      if (id.toLowerCase().startsWith('zentralblatt-math.org')) return 'http://' + id;
      return 'http://zentralblatt-math.org/zmath/en/search/?q=an:' + id + '&format=complete';
   };

   exports.getLink = function(id, type) {
      if (id == null) return null;
      id = id.trim();
      if (id.startsWith('http:') || id.startsWith('https:')) return id;
      if (type == null) return null;
      type = type.toLowerCase();
      if (!typeMap.hasOwnProperty(type)) return null;
      return typeMap[type](id);
    };

   exports.getTypes = function() {
      var types = '';
      for(i in typeMap) {
          if (i != 'hasOwnProperty')
          types = types + ' ' + i;
      }
      return types;
   }

})(typeof exports === 'undefined'? this['workIdLinkJs']={}: exports);

/* END: workIdLinkJs */




$(function (){
	$('*[wiJs-data]').each(function(index) {
		var $this = $(this);
		var id = $this.text();
		var type = $this.attr('wiJs-data');
		var link = workIdLinkJs.getLink(id,type);
		if (link != null) 
		/* using native element innerHtml to prevent script injection, as innerHTML ignores 
		 * script tags */
		$this.get(0).innerHTML = "<a href='" + link + "' target='_blank'>" + id + "</a>";
	});
});

/* start bibtexParse 0.0.7 */

//Original work by Henrik Muehe (c) 2010
//
//CommonJS port by Mikola Lysenko 2013
//
//Port to Browser lib by ORCID / RCPETERS
//
//Issues:
//no comment handling within strings
//no string concatenation
//no variable values yet
//Grammar implemented here:
//bibtex -> (string | preamble | comment | entry)*;
//string -> '@STRING' '{' key_equals_value '}';
//preamble -> '@PREAMBLE' '{' value '}';
//comment -> '@COMMENT' '{' value '}';
//entry -> '@' key '{' key ',' key_value_list '}';
//key_value_list -> key_equals_value (',' key_equals_value)*;
//key_equals_value -> key '=' value;
//value -> value_quotes | value_braces | key;
//value_quotes -> '"' .*? '"'; // not quite
//value_braces -> '{' .*? '"'; // not quite
(function(exports) {

	function BibtexParser() {
		this.pos = 0;
		this.input = "";

		this.entries = new Array();
		this.strings = {
			JAN : "January",
			FEB : "February",
			MAR : "March",
			APR : "April",
			MAY : "May",
			JUN : "June",
			JUL : "July",
			AUG : "August",
			SEP : "September",
			OCT : "October",
			NOV : "November",
			DEC : "December"
		};

		this.currentEntry = "";

		this.setInput = function(t) {
			this.input = t;
		};

		this.getEntries = function() {
			return this.entries;
		};

		this.isWhitespace = function(s) {
			return (s == ' ' || s == '\r' || s == '\t' || s == '\n');
		};

		this.match = function(s) {
			this.skipWhitespace();
			if (this.input.substring(this.pos, this.pos + s.length) == s) {
				this.pos += s.length;
			} else {
				throw "Token mismatch, expected " + s + ", found "
						+ this.input.substring(this.pos);
			};
			this.skipWhitespace();
		};

		this.tryMatch = function(s) {
			this.skipWhitespace();
			if (this.input.substring(this.pos, this.pos + s.length) == s) {
				return true;
			} else {
				return false;
			};
			this.skipWhitespace();
		};

		/* when search for a match all text can be ignored, not just white space */
		this.matchAt = function() {
			while (this.input.length > this.pos && this.input[this.pos] != '@') {
				this.pos++;
			};

			if (this.input[this.pos] == '@') {
				return true;
			};
			return false;
		};

		this.skipWhitespace = function() {
			while (this.isWhitespace(this.input[this.pos])) {
				this.pos++;
			};
			if (this.input[this.pos] == "%") {
				while (this.input[this.pos] != "\n") {
					this.pos++;
				};
				this.skipWhitespace();
			};
		};

		this.value_braces = function() {
			var bracecount = 0;
			this.match("{");
			var start = this.pos;
			while (true) {
				if (this.input[this.pos] == '}'
						&& this.input[this.pos - 1] != '\\') {
					if (bracecount > 0) {
						bracecount--;
					} else {
						var end = this.pos;
						this.match("}");
						return this.input.substring(start, end);
					};
				} else if (this.input[this.pos] == '{') {
					bracecount++;
				} else if (this.pos >= this.input.length - 1) {
					throw "Unterminated value";
				};
				this.pos++;
			};
		};

		this.value_comment = function() {
			var str = '';
			var brcktCnt = 0;
			while (!(this.tryMatch("}") && brcktCnt == 0)) {
				str = str + this.input[this.pos];
				if (this.input[this.pos] == '{')
					brcktCnt++;
				if (this.input[this.pos] == '}')
					brcktCnt--;
				if (this.pos >= this.input.length - 1) {
					throw "Unterminated value:" + this.input.substring(start);
				};
				this.pos++;
			};
			return str;
		};

		this.value_quotes = function() {
			this.match('"');
			var start = this.pos;
			while (true) {
				if (this.input[this.pos] == '"'
						&& this.input[this.pos - 1] != '\\') {
					var end = this.pos;
					this.match('"');
					return this.input.substring(start, end);
				} else if (this.pos >= this.input.length - 1) {
					throw "Unterminated value:" + this.input.substring(start);
				};
				this.pos++;
			};
		};

		this.single_value = function() {
			var start = this.pos;
			if (this.tryMatch("{")) {
				return this.value_braces();
			} else if (this.tryMatch('"')) {
				return this.value_quotes();
			} else {
				var k = this.key();
				if (this.strings[k]) {
					return this.strings[k];
				} else if (k.match("^[0-9]+$")) {
					return k;
				} else {
					throw "Value expected:" + this.input.substring(start);
				};
			};
		};

		this.value = function() {
			var values = [];
			values.push(this.single_value());
			while (this.tryMatch("#")) {
				this.match("#");
				values.push(this.single_value());
			};
			return values.join("");
		};

		this.key = function() {
			var start = this.pos;
			while (true) {
				if (this.pos >= this.input.length) {
					throw "Runaway key";
				};

				if (this.input[this.pos].match("[a-zA-Z0-9+_:\\./-]")) {
					this.pos++;
				} else {
					return this.input.substring(start, this.pos);
				};
			};
		};

		this.key_equals_value = function() {
			var key = this.key();
			if (this.tryMatch("=")) {
				this.match("=");
				var val = this.value();
				return [ key, val ];
			} else {
				throw "... = value expected, equals sign missing:"
						+ this.input.substring(this.pos);
			};
		};

		this.key_value_list = function() {
			var kv = this.key_equals_value();
			this.currentEntry['entryTags'] = {};
			this.currentEntry['entryTags'][kv[0]] = kv[1];
			while (this.tryMatch(",")) {
				this.match(",");
				// fixes problems with commas at the end of a list
				if (this.tryMatch("}")) {
					break;
				};
				kv = this.key_equals_value();
				this.currentEntry['entryTags'][kv[0]] = kv[1];
			};
		};

		this.entry_body = function(d) {
			this.currentEntry = {};
			this.currentEntry['citationKey'] = this.key();
			this.currentEntry['entryType'] = d.substring(1);
			this.match(",");
			this.key_value_list();
			this.entries.push(this.currentEntry);
		};

		this.directive = function() {
			this.match("@");
			return "@" + this.key();
		};

		this.string = function() {
			var kv = this.key_equals_value();
			this.strings[kv[0]] = kv[1];
		};

		this.preamble = function() {
			this.currentEntry = {};
			this.currentEntry['entryType'] = 'PREAMBLE';
			this.currentEntry['entry'] = this.value();
			this.entries.push(this.currentEntry);
		};

		this.comment = function() {
			this.currentEntry = {};
			this.currentEntry['entryType'] = 'COMMENT';
			this.currentEntry['entry'] = this.value_comment();
			this.entries.push(this.currentEntry);
		};

		this.entry = function(d) {
			this.entry_body(d);
		};

		this.bibtex = function() {
			while (this.matchAt()) {
				var d = this.directive();
				this.match("{");
				if (d == "@STRING") {
					this.string();
				} else if (d == "@PREAMBLE") {
					this.preamble();
				} else if (d == "@COMMENT") {
					this.comment();
				} else {
					this.entry(d);
				}
				this.match("}");
			};
		};
	};

	exports.toJSON = function(input) {
		var b = new BibtexParser();
		b.setInput(input);
		b.bibtex();
		return b.entries;
	};

})(typeof exports === 'undefined' ? this['bibtexParse'] = {} : exports);

/* end bibtexParse */



/* Mobile detection, useful for colorbox lightboxes resizing */
function isMobile(){	
    (/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test((navigator.userAgent||navigator.vendor||window.opera)) || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test((navigator.userAgent||navigator.vendor||window.opera).substr(0, 4)))
    ? im = true
    : im = false;
    return im;
};

function isIE() {
  var myNav = navigator.userAgent.toLowerCase();
  return (myNav.indexOf('msie') != -1) ? parseInt(myNav.split('msie')[1]) : false;
}

function getWindowWidth() {
	var windowWidth = 0;
	if (typeof(window.innerWidth) == 'number') {
		windowWidth = window.innerWidth;
	}
	else {
		if (document.documentElement && document.documentElement.clientWidth) {
			windowWidth = document.documentElement.clientWidth;
		} else {
			if (document.body && document.body.clientWidth) {
				windowWidth = document.body.clientWidth;
			}
		}
	}
	return windowWidth;
};


function tabletDesktopActionButtons($event){
	var thisWidth = getWindowWidth();	
	if(thisWidth >= 767){
		$('.action-button-bar').addClass('tablet-desktop-display');
	}else{ //Mobile
		$('.action-button-bar').removeClass('tablet-desktop-display');		
	}
}
