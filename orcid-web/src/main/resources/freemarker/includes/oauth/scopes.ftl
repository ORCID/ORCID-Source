<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<span class="dotted-green-bar"></span>
<ul class="oauth-scopes">
	<#list scopes as scope>
		<li>				
			<#if scope.value()?ends_with("/create")>
				<span class="mini-icon glyphicon glyphicon-cloud-download green"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/>
			<#elseif scope.value()?ends_with("/update")>
				<span class="mini-icon glyphicon glyphicon-refresh green"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/>
			<#elseif scope.value()?ends_with("/read-limited")>
				<span class="mini-icon glyphicon glyphicon-eye-open green"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/>
			<#else>
				<span class="mini-orcid-icon oauth-bullet"></span><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/>
			</#if>	
		</li>
   	</#list>				
</ul>
<span class="dotted-green-bar"></span>
