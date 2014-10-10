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
<span class="dotted-green-bar"></span>
<#assign show_create_icon = false >
<#assign show_update_icon = false >
<#assign show_read_limited_icon = false >
<#assign show_bullet_icon = false >
	
<#list scopes as scope>
	<#if scope.value()?ends_with("/create")>
		<#assign show_create_icon = true>
	<#elseif scope.value()?ends_with("/update")>
		<#assign show_update_icon = true>
	<#elseif scope.value()?ends_with("/read-limited")>
		<#assign show_read_limited_icon = true>		
	<#else>
		<#assign show_bullet_icon = true>
	</#if>	
</#list>
	
<div class="row">	
	<div class="col-md-12 col-sm-12 col-xs-12">
		<ul class="oauth-icons">
			<#if show_bullet_icon>
				<li><span class="mini-orcid-icon oauth-bullet"></span></li>
			</#if>
			<#if show_read_limited_icon>
				<li><span class="mini-icon glyphicon glyphicon-eye-open green"></span></li>
			</#if>
			<#if show_create_icon>
				<li><span class="mini-icon glyphicon glyphicon-cloud-upload green"></span></li>
			</#if>
			<#if show_update_icon>
				<li><span class="mini-icon glyphicon glyphicon-repeat green"></span></li>
			</#if>					
		</ul>
	</div>
	<div class="col-md-12 col-sm-12 col-xs-12">
		<ul class="oauth-scopes" id="scopes-ul">
			<#list scopes as scope>
				<li>
					<span ng-mouseenter="toggleLongDescription('${scope.name()}')" ng-mouseleave="toggleLongDescription('${scope.name()}')"><@orcid.msg '${scope.declaringClass.name}.${scope.name()}'/></span>
					<div class="popover bottom scopeLongDesc" ng-class="{'popover bottom inline':showLongDescription['${scope.name()}'] == true}">
						<div class="arrow"></div>	
						<div class="lightbox-container">
							<@orcid.msg '${scope.declaringClass.name}.${scope.name()+".longDesc"}'/>
						</div>
					</div>	
				</li>
		   	</#list>				
		</ul>
	</div>
</div>
<span class="dotted-green-bar"></span>
<#if usePersistentTokens?? && usePersistentTokens>
	<div class="row">
		<div class="col-md-1 col-sm-1 col-xs-1">
			<input type="checkbox" name="enablePersistentToken" id="enablePersistentToken" ng-model="enablePersistentToken"/>
		</div>
		<div class="col-md-11 col-sm-11 col-xs-11">
			<@orcid.msg 'oauth.persistent_token_description'/><br>
			<@orcid.msg 'oauth.persistent_token_description.note'/>
		</div> 	
	</div>
</#if>
