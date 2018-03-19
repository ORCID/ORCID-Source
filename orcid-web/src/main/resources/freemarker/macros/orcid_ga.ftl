<!-- OrcidGA -->
<script>
	var OrcidGA = function() {
	    this.buildClientString = function(clientGroupName, clientName) {
	        return clientGroupName + ' - ' + clientName
	    };

	    this.gaPush = function(trackArray) {
	        /*
	         * window.ga is blocked by Ghostery and disconnect.me 
	         * window.gaGlobal is blocked by uBlock
	        */
	
	        if(window.ga && window.gaGlobal) {
	            if(typeof trackArray === 'function') {
	                ga(trackArray);
	            } else {
	                //Anonymize IP for each hit per
	                //https://developers.google.com/analytics/devguides/collection/analyticsjs/ip-anonymization
	                if(trackArray[5] == undefined) {
	                    ga(trackArray[0], trackArray[1], trackArray[2], trackArray[3], trackArray[4], {'anonymizeIp': true});
	                } else {
	                    ga(trackArray[0], trackArray[1], trackArray[2], trackArray[3], trackArray[4], trackArray[5], {'anonymizeIp': true});
	                }                
	                //console.log("_gap.push for " + trackArray);
	            }
	            
	            setTimeout(function(){
	                if(!ga.create) {
	                    // if it's a function and _gap isn't available run (typically only
	                    // on dev)
	                    console.log("no _gap.push for " + trackArray);
	                    if (typeof trackArray === 'function')
	                        trackArray();
	                }
	            }, 200); 
	        } else {
	            //console.log("no _gap.push for " + trackArray);
	            if (typeof trackArray === 'function')
	                trackArray();                      
	        }
	    };

	    // Delays are async functions used to make sure event track que has cleared
	    // See
	    // https://developers.google.com/analytics/devguides/collection/gajs/methods/gaJSApi_gaq
	    //
	    // Additionally adding in delay:
	    // http://support.google.com/analytics/answer/1136920?hl=en
	    this.gaFormSumbitDelay = function($el) {
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
	
	    this.windowLocationHrefDelay = function(url) {
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
</script>
<!-- End OrcidGA -->