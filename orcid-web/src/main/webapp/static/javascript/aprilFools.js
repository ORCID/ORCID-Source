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

if(typeof OrcidCookie == "undefined") {
	// function for javascript cookies
	var OrcidCookie = new function () {
	    this.getCookie =  function (c_name) {
	    	var i,x,y,ARRcookies=document.cookie.split(";");
	    	for (i=0;i<ARRcookies.length;i++) {
	    	    x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
	    	    y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
	    	    x=x.replace(/^\s+|\s+jQuery/g,"");
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
}


// jquery ready
jQuery(function () {
    
    // Aprils fools!
    var today = new Date();
    var aprSecond = new Date(2013,3,01);
    var march31 = new Date(2013,2,31); // coded to march 26 for testing until final release
    var isParent = (location == parent.location);
    var isAprilFools = (march31 < today && today < aprSecond);
    var hasAprilFoolsFlag = (window.location.search.indexOf("aprilFools=true") != -1);
    var isDev = (window.location.pathname.startsWith("/orcid-web"));
    var isOAuth = (window.location.pathname.startsWith("/oauth") || window.location.pathname.startsWith("/orcid-web/oauth"));
    
    var aprilFoolsOrcidWeb = "";
    if (isDev) {
    	aprilFoolsOrcidWeb = 'http://' + window.location.host +  "/orcid-web";
    } else {
    	aprilFoolsOrcidWeb = 'https://' + window.location.host;
    }
    
    
    function pingJavaAppAndDrupal(lang) {
 		//hack in case there are multipule locale cookies
		OrcidCookie.setCookie("locale_v2",lang);

		
		//set drupal side
		jQuery.ajax({
			url: "/about?lang="+lang, // I would prefer a must shorter faster page (json maybe)
			async: false,
			dataType: 'json',
			success:function(data) {
				// do nothing
	        }
		});    	

		//set the java side
		jQuery.ajax({
			url: aprilFoolsOrcidWeb + "/lang.json?lang="+lang,
			async: false,
			dataType: 'json',
			success:function(data) {
	        }
		});
		
    }
    
    function reloadVsRefresh(params) {
    	if (isOAuth) {
    		//oauth pages need reloads
    		window.location.reload(true);
    	} else {
    		window.location.href = window.location.pathname+params;
    	}
    	
    }

	var afCookie = OrcidCookie.getCookie("aprilFools");
    if (isParent && (isAprilFools || hasAprilFoolsFlag || afCookie)){ 

        var locale = 'en';
    	var localeCookie = OrcidCookie.getCookie("locale_v2");
    	if (localeCookie) locale = localeCookie;
    	
    	console.log(afCookie);
    	if (!afCookie) {
    		//haven't been prank
    		OrcidCookie.setCookie("aprilFools","pranked",14);
    		reloadVsRefresh("?lang=orc");
    		return;
    	}
    	
    	//reset to pranked for goWay state
    	if (window.location.search.indexOf("aprilFools=true") != -1) {
    		OrcidCookie.setCookie("aprilFools","pranked",14);
    		afCookie = OrcidCookie.getCookie("aprilFools");
    	}
    	
    	var enSelected = "selected";
    	var orcSelected ="";
    	if (locale == 'orc') {
        	enSelected = "";
    		orcSelected = "selected";
    	}
    	
    	if (afCookie != "goAway") {
	    	jQuery('body').append( jQuery(
	    			
	    			   "<div style='width: 300px;margin: 0 auto; position:fixed; bottom: 0px; left: 0px;'>" 
	    			 + " <img src='"+aprilFoolsOrcidWeb+"/static/img/orc.png'  width='300' height='300' style='text-align:center; display: inline-block;float:left;margin-left: 10px;'/>" 
	    			 + " <div style='position:absolute; left:80px; bottom: 170px;'>"
	    			 + "		Read in:"
	    			 + "		<select id='orcPreviewSel' style='width: 100px;'>"
	    			 + "            <option value='en' " + enSelected + ">english</option>"
		    		 + "            <option value='orc' " + orcSelected +">orc/troll</option>"
	    			 + "		</select>"
	    			 + "	</p>"
	    			 + " </div>"
	    			 + "</div>"
		    		 + "<div style=' widh: 100px; position: fixed; left:285px; bottom: 20px; background: #ffffff;'>"
		    		 + "     <a href='' id='orcPreviewGoAway'>I hate orcs, go away!</a>"
	    			 + "</div>"));
	    		    	
	    	jQuery('#orcPreviewSel').change(function(e) {
	    		var lang = jQuery('#orcPreviewSel').val();
	    		pingJavaAppAndDrupal(lang);
	    		reloadVsRefresh("?lang=" + lang);
	    	});
	    	
	    	jQuery('#orcPreviewGoAway').click(function(e) {
	    		e.preventDefault();  
	    		OrcidCookie.setCookie("aprilFools","goAway",14);
	    		pingJavaAppAndDrupal('en');
	    		reloadVsRefresh("?lang=en");
	    	});
    	
    	}

    }
});