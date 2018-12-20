<script type="text/ng-template" id="print-id-banner-ng2-template">
        	<div class="id-banner">
	            <h2 class="full-name">	            	
					{{displayName}}	                
	            </h2>	            	            
	            
	            <div class="oid">
					<div class="id-banner-header">
						<span><@orcid.msg 'common.orcid_id' /></span>
					</div>
					<div class="orcid-id-container">
						<div class="orcid-id-info">
	                        <span class="mini-orcid-icon"></span>
	                        <!-- Reference: orcid.js:removeProtocolString() -->
	                        <span id="orcid-id" class="orcid-id">${baseUri}/${(effectiveUserOrcid)!}</span>	
						</div>				
					</div>
				</div>
	        </div>
</script>