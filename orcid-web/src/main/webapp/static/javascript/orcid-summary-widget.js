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
(function (global) {
	
	var host = window.location.hostname;
	var baseURL = ''; 
	
	
	if (host == 'localhost'){
		baseURL = '//localhost:8443/orcid-web';
	}else if(host == 'qa.orcid.org'){
		baseURL = '//qa.orcid.org';
	}else if(host == 'sandbox.orcid.org'){
		baseURL = '//sandbox.orcid.org';
	}else{
		baseURL = '//orcid.org';
	}	

	//Copyright JSONP function: https://github.com/OscarGodson/JSONP/blob/master/JSONP.js
	var JSONP = function(url, data, method, callback){
	    url = url || '';
	    data = data || {};
	    method = method || '';
	    callback = callback || function(){};
	    var getKeys = function(obj){
	      var keys = [];
	      for(var key in obj){
	        if (obj.hasOwnProperty(key)) {
	          keys.push(key);
	        }
	        
	      }
	      return keys;
	    };
	    if(typeof data == 'object'){
	      var queryString = '';
	      var keys = getKeys(data);
	      for(var i = 0; i < keys.length; i++){
	        queryString += encodeURIComponent(keys[i]) + '=' + encodeURIComponent(data[keys[i]]);
	        if(i != keys.length - 1){ 
	          queryString += '&';
	        }
	      }
	      url += '?' + queryString;
	    } else if(typeof data == 'function'){
	      method = data;
	      callback = method;
	    }
	    if(typeof method == 'function'){
	      callback = method;
	      method = 'callback';
	    }
	    if(!Date.now){
	      Date.now = function() { return new Date().getTime(); };
	    } 
	    var timestamp = Date.now();
	    var generatedFunction = 'jsonp'+Math.round(timestamp+Math.random()*1000001);
	    window[generatedFunction] = function(json){
	      callback(json);
	      delete window[generatedFunction];
	    };
	    if(url.indexOf('?') === -1){ url = url+'?'; }
	    else{ url = url+'&'; }
	    var jsonpScript = document.createElement('script');
	    jsonpScript.async = true;
	    jsonpScript.setAttribute("src", url+method+'='+generatedFunction);
	    document.getElementsByTagName("head")[0].appendChild(jsonpScript);
	  };
	  
	  
    if (!Array.prototype.indexOf) {
	    Array.prototype.indexOf = function(obj, start) {
	      var i, j;
	      i = start || 0;
	      j = this.length;
	      while (i < j) {
	        if (this[i] === obj) {
	          return i;
	        }
	        i++;
	      }
	      return -1;
	    };
    }

  var QueryStringToHash = function QueryStringToHash  (query) {
    var query_string = {};
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
      var pair = vars[i].split("=");
      pair[0] = decodeURIComponent(pair[0]);
      pair[1] = decodeURIComponent(pair[1]);
        // If first entry with this name
      if (typeof query_string[pair[0]] === "undefined") {
        query_string[pair[0]] = pair[1];
        // If second entry with this name
      } else if (typeof query_string[pair[0]] === "string") {
        var arr = [ query_string[pair[0]], pair[1] ];
        query_string[pair[0]] = arr;
        // If third or later entry with this name
      } else {
        query_string[pair[0]].push(pair[1]);
      }
    } 
    return query_string;
  };

  var parseQueryString = function(url) {
    var a = document.createElement('a');
    a.href = url;
    str = a.search.replace(/\?/, '');
    
    return QueryStringToHash(str);
  };
 
  if(!global.OrcidSummaryWidget) { global.OrcidSummaryWidget = {}; };
  var OrcidSummaryWidget = global.OrcidSummaryWidget;
  if(!OrcidSummaryWidget.styleTags) { OrcidSummaryWidget.styleTags = []; };
  var styleTags = OrcidSummaryWidget.styleTags;
  
  var scriptTags = document.getElementsByTagName('script');

  var re = /.*orcid-summary-widget\.([^/]+\.)?js/;
 
  for(var i = 0; i < scriptTags.length; i++) {
	  var el = scriptTags[i];
	  if(el.src.match(re)) {
		  scriptTag = el;
      }
  }
  
  var data = parseQueryString(scriptTag.src);
  
  if (data.locale === undefined){
	  var url = baseURL + '/public_widgets/' + data.orcid + '/' + data.t +'/info.json';
  }else{
	  var url = baseURL + '/public_widgets/' + data.orcid + '/' + data.t +'/info.json?locale=' + data.locale;
  }
  
  JSONP(url, callback);
  
  function callback(json){
	  	var orcid = json.orcid;
	    var name = json.name;
	    var content = '';
	    var count = 0;
	    for(key in json.values){
	    	if (key != 'what_is')
		    	if (json.values[key] > 0){
		    		content += '<div class="orcid-summary-item">' + key + '('+ json.values[key] +')</div>';
		    		count++;
		    	}
	    }
	    if (count == 0){
	    	content = '<div class="orcid-summary-item">' + json.values.no_activities + '</div>';
	    }
	    
	    if(styleTags.length == 0) {	        
	        var styleTag = document.createElement("link");
	        styleTag.rel = "stylesheet";
	        styleTag.type = "text/css";
	        styleTag.href =  baseURL + '/static/css/orcid-summary-widget.css';
	        styleTag.media = "all";
	        document.getElementsByTagName('head')[0].appendChild(styleTag);
	    }
	    
	    var widgetInnerHTML = '<div class="orcid-summary-widget">\
	                                <a href="'+ baseURL+'/'+ orcid + '" target="_blank">\
	                                    <div class="orcid-widget-details">\
	                                        <div class="orcid-logo"></div>\
	                                        <div class="orcid-name">'+ name +'</div>\
	                                        <div class="orcid-id">ORCID: <span class="orcid-mini-logo"></span>'+ orcid +'</div>\
	                                        <div class="orcid-summary-items">';
	    									widgetInnerHTML += content;
	                                        widgetInnerHTML += '</div>\
	                                    </div>\
	                                </a>\
	                                <a href="http://orcid.org/about/what-is-orcid" class="orcid-widget-button" target="_blank">' + json.values.what_is + '</a>\
	                            </div>';
	        
		var div = document.createElement('div');
		    div.id = 'orcid-summary-widget';
		    div.className = 'orcid-widget cleanslate';
		    div.innerHTML = widgetInnerHTML;	        
		    document.getElementById('orcid-summary-widget').innerHTML = widgetInnerHTML;
   }
})(this);