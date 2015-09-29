<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#-- @ftlvariable name="statistics" type="java.util.Map" -->
<@public >
<#escape x as x?html>
<div id="statistics">
	<div class="row">	    
	    <div class="col-md-9 col-md-offset-3">
	    	<span class="page-header"><@orcid.msg 'statistics.header'/></span>
	    </div>
    </div>
    <#if statistics['liveIds']??>
	    <div class="row">    
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
		        	<span class="stat-name"><@orcid.msg 'statistics.live_ids'/></span>
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['liveIds']}</span>
		    </div>	  
	    </div>
	</#if>
	<#if statistics['idsWithWorks']??>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
		    	<span class="stat-name"><@orcid.msg 'statistics.ids_with_works'/></span>
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['idsWithWorks']}</span>
			</div>	  
	    </div>
	</#if>
	<#if statistics['works']??>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
		    	<span class="stat-name"><@orcid.msg 'statistics.number_of_works'/></span><br>
		    	<span class="stat-name-st"><@orcid.msg 'statistics.number_of_works.subtext' /></span>
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['works']}</span>	
			</div>	  
	    </div>
	</#if>
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
	<#if statistics['employment']??>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_employment'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['employment']}</span>
			</div>
	    </div>
	</#if>
	<#if statistics['employmentUniqueOrg']??>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_employment_unique_org'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['employmentUniqueOrg']}</span>
			</div>
	    </div>
	</#if>
	<#if statistics['education']??>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_education'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['education']}</span>
			</div>
	    </div>
	</#if>
	<#if statistics['educationUniqueOrg']??>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_education_unique_org'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['educationUniqueOrg']}</span>
			</div>
	    </div> 
	</#if>
	<#if statistics['funding']??>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_funding'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['funding']}</span>
			</div>
	    </div>
	</#if>
	<#if statistics['fundingUniqueOrg']??>
	    <div class="row">        
		    <div class="col-md-6 col-md-offset-3 col-sm-8 col-xs-7">
				<span class="stat-name"><@orcid.msg 'statistics.number_of_funding_unique_org'/></span>	    
		    </div>
		    <div class="col-md-3 col-sm-4 col-xs-5 right">
		    	<span class="stat-data">${statistics['fundingUniqueOrg']}</span>
			</div>
	    </div>
	</#if>
    <div class="row">        
	    <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
			<span class="stat-date"><@orcid.msg 'statistics.statistics_generation_date_label'/>&nbsp;<#if (statistics_date)??>${statistics_date}<#else>NA</#if></span>	    
	    </div>  		
    </div>
</div>
</#escape>
</@public>