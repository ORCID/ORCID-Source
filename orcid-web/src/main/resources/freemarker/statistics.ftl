<#-- @ftlvariable name="statistics" type="java.util.Map" -->
<@public >
<#escape x as x?html>
<div id="statistics">
	<div class="row">	    
	    <div class="col-md-9 col-md-offset-3">
	    	<h1><@orcid.msg 'statistics.header'/></h1>
	    </div>
    </div>
    <#if statistics['liveIds']??>
	    <div class="row">    
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">
		        	<span class="stat-name"><@orcid.msg 'statistics.live_ids'/></span>
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right stat-subheader">
		    	<span class="stat-data">${statistics['liveIds']}</span>
		    </div>	  
	    </div>
	</#if>
	<#if statistics['idsWithExternalId']??>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
		    	<span class="stat-name"><@orcid.msg 'statistics.ids_with_external_id'/></span><br>
		    	<span class="stat-name-st"><@orcid.msg 'statistics.ids_with_external_id.subtext'/></span>
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['idsWithExternalId']}</span>
			</div>	  
	    </div>
	</#if>
	<#if statistics['idsWithEducation']??>
	    <div class="row"> 
	    	<div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">
	    		<h2>Education</h2> 
	    	</div>
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
		    	<span class="stat-name"><@orcid.msg 'statistics.ids_with_education'/></span>
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['idsWithEducation']}</span>
			</div>	  
	    </div>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_education'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['education']}</span>
			</div>
	    </div>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_education_unique_org'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['educationUniqueOrg']}</span>
			</div>
	    </div> 
	</#if>
	<#if statistics['idsWithEmployment']??>
	    <div class="row">  
	    	<div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">   
	    		<h2>Employment</h2> 
	    	</div>   
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
		    	<span class="stat-name"><@orcid.msg 'statistics.ids_with_employment'/></span>
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['idsWithEmployment']}</span>
			</div>	  
	    </div>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_employment'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['employment']}</span>
			</div>
	    </div>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_employment_unique_org'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['employmentUniqueOrg']}</span>
			</div>
	    </div>
	</#if>
	<#if statistics['idsWithFunding']??>
	    <div class="row"> 
	    	<div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">
	    		<h2>Funding</h2> 
	    	</div>       
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
		    	<span class="stat-name"><@orcid.msg 'statistics.ids_with_funding'/></span>
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['idsWithFunding']}</span>
			</div>	  
	    </div>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_funding'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['funding']}</span>
			</div>
	    </div>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_funding_unique_org'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['fundingUniqueOrg']}</span>
			</div>
	    </div>
	</#if>
	<#if statistics['idsWithPeerReview']??>
	    <div class="row"> 
	    	<div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">
	    		<h2>Peer review</h2> 
	    	</div>        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
		    	<span class="stat-name"><@orcid.msg 'statistics.ids_with_peer_review'/></span>
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['idsWithPeerReview']}</span>
			</div>	  
	    </div>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_peer_review'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['peerReview']}</span>
			</div>
	    </div>
	</#if>
	<#if statistics['idsWithPersonId']??>
	    <div class="row">    
	    	<div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">
		    	<h2>Person identifiers</h2> 
		   	</div>
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
		    	<span class="stat-name"><@orcid.msg 'statistics.ids_with_person_id'/></span>
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['idsWithPersonId']}</span>
			</div>	  
	    </div>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_person_id'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['personId']}</span>
			</div>
	    </div>
	</#if>
	<#if statistics['idsWithWorks']??>
	    <div class="row">   
	    	<div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7 stat-subheader">
		    	<h2>Works</h2> 
		    </div>     
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
		    	<span class="stat-name"><@orcid.msg 'statistics.ids_with_works'/></span>
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['idsWithWorks']}</span>
			</div>	  
	    </div>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
		    	<span class="stat-name"><@orcid.msg 'statistics.number_of_works'/></span><br>
		    	<span class="stat-name-st"><@orcid.msg 'statistics.number_of_works.subtext' /></span>
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['works']}</span>	
			</div>	  
	    </div>
		<#if statistics['uniqueDois']??>
		    <div class="row">        
			    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
					<span class="stat-name"><@orcid.msg 'statistics.number_of_unique_dois'/></span>	    
			    </div>
			    <div class="col-md-3 col-sm-4 col-xs-5 right">
			    	<span class="stat-data">${statistics['uniqueDois']}</span>
				</div>
		    </div>
		</#if>
	</#if>
	
	
    <div class="row">        
	    <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
			<span class="stat-date"><@orcid.msg 'statistics.statistics_generation_date_label'/>&nbsp;<#if (statistics_date)??>${statistics_date}<#else>NA</#if></span>	    
	    </div>  		
    </div>
</div>
</#escape>
</@public>