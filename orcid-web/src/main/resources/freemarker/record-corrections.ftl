<@public>
	<div class="row">	    
	    <div class="col-md-9 col-md-offset-3">
	    	<h1><@orcid.msg 'record_corrections.heading'/></h1>
	    	<p><@orcid.msg 'record_corrections.a_core'/>&nbsp;<a href="<@orcid.rootPath '/about/trust/home'/>"><@orcid.msg 'record_corrections.orcid_trust'/></a>&nbsp;<@orcid.msg 'record_corrections.principle_is'/></p>
	    	<hr>	 
			<#include "/includes/ng2_templates/record-corrections-ng2-template.ftl">
			<record-corrections-ng2></record-corrections-ng2>
	    </div>
    </div>
</@public>