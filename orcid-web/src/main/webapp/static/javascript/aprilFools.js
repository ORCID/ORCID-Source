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
	       document.cookie=c_name + "=" + c_value;
	    };
	};	
}


// jquery ready
$(function () {
    
    // Aprils fools!
    var today = new Date();
    var aprSecond = new Date(2013,3,01);
    var march31 = new Date(2013,2,26); // coded to march 26 for testing until final release
    var isParent = (location == parent.location);
    var isAprilFools = (march31 < today && today < aprSecond);
    var hasAprilFoolsFlag = (window.location.search.indexOf("aprilFools=true") != -1);
    var aprilFoolsOrcidWeb = window.location.pathname.startsWith("/orcid-web")?"/orcid-web":"";
    
    function pingJavaAppAndDrupal(lang) {
 		//hack in case there are multipule locale cookies
		OrcidCookie.setCookie("locale_v2",lang);

		//set the java side
		$.ajax({
			url: aprilFoolsOrcidWeb + "/lang.json?lang="+lang,
			async: false,
			dataType: 'json',
			success:function(data) {
			    //do nothing
	        }
		});
		
		//set drupal side
		$.ajax({
			url: "/about?lang="+lang, // I would prefer a must shorter faster page (json maybe)
			async: false,
			dataType: 'json',
			success:function(data) {
				// do nothing
	        }
		});    	
    }
    
    if (isParent && (isAprilFools || hasAprilFoolsFlag)){ 

        var locale = 'en';
    	var localeCookie = OrcidCookie.getCookie("locale_v2");
    	if (localeCookie) locale = localeCookie;
    	
    	var afCookie = OrcidCookie.getCookie("aprilFools");
    	console.log(afCookie);
    	if (!afCookie) {
    		//haven't been prank
    		OrcidCookie.setCookie("aprilFools","pranked",14);
    		window.location.href = window.location.pathname + "?lang=orc&aprilFools=true";
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
	    	$('body').append( $(
	    			
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
	    			
//	    			"<div style='width: 960px;margin: 0 auto; position:fixed; bottom: 50px; left: 0px;'>" 
//	    			 +" <img src='"+aprilFoolsOrcidWeb+"/static/img/lang.png'  width='393' height='397' style='text-align:center;display: inline-block;float:left;margin-left: 10px;'>" 
//	    			 + " <div style='background-image:url(\""+ aprilFoolsOrcidWeb +"/static/img/lang-tooltip.png\"); display: inline-block;height: 188px;margin-left: -38px;margin-top: 85px;padding-left: 8px;text-align: center;width: 334px;'>"
//	    			 + " 	<h3 style='font-weight: bold;margin-top: 35px;font-size: 27px;color: #999999;margin-bottom: 0px;'><b style='color: #006600;'>ORC</b>id.org has <br/> been captured!</h3>"
//	    			 + "	<p style='font-size: 22px;color: #999999;'>"
//	    			 + "		Read in:"
//	    			 + "		<select id='orcPreviewSel' style='width: 120px;'>"
//	    			 + "            <option value='en' " + enSelected + ">english</option>"
//		    		 + "            <option value='orc' " + orcSelected +">orc/troll</option>"
//	    			 + "		</select>"
//	    			 + "	</p>"
//	    			 + "     <div style='position: left:320px; bottom: 10px;'>"
//		    		 + "        <a href='' id='orcPreviewGoAway'>I hate tolls, go away!</a>"
//		    		 + "     </div>"
//	    			 + " </div>"
//	    			 + "</div>"));
	    		    	
	    	$('#orcPreviewSel').change(function(e) {
	    		var lang = $('#orcPreviewSel').val();
	    		pingJavaAppAndDrupal(lang);
	    		window.location.href = window.location.pathname + "?lang=" + lang + "&aprilFools=true";
	    	});
	    	
	    	$('#orcPreviewGoAway').click(function(e) {
	    		e.preventDefault();  
	    		OrcidCookie.setCookie("aprilFools","goAway",14);
	    		pingJavaAppAndDrupal('en');
	    		window.location.href = window.location.pathname + "?lang=en";
	    	});
    	
    	}

    }
});