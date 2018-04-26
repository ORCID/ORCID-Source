<#--
 * orcid.ftl
 *
 * This file consists of a collection of FreeMarker macros used in various places in the ORCID web interface.
 *
 * @author Will Simpson
 -->

<#macro privacyToggle angularModel publicClick limitedClick privateClick popoverStyle="" arrowStyle="" questionClick="alert('no function passed')" clickedClassCheck="{'popover-help-container-show':privacyHelp['work']==true}">	
	<div class="relative" id="privacy-bar">
		<ul class="privacyToggle">
			<li class="publicActive" ng-class="{publicInActive: ${angularModel} != 'PUBLIC'}"><a href="" title="<@orcid.msg 'manage.lipublic' />" ng-click="${publicClick}"></a></li>
			<li class="limitedActive" ng-class="{limitedInActive: ${angularModel} != 'LIMITED'}"><a href="" title="<@orcid.msg 'manage.lilimited' />" ng-click="${limitedClick}"></a></li>
			<li class="privateActive" ng-class="{privateInActive: ${angularModel} != 'PRIVATE'}"><a href="" title="<@orcid.msg 'manage.liprivate' />" ng-click="${privateClick}"></a></li>
		</ul>
		<div class="popover-help-container" ng-class="${clickedClassCheck}"  style="position: absolute; left: 110px; top: 0px;">
        	<a ng-click="${questionClick}"><i class="glyphicon glyphicon-question-sign" style="width: 14px;"></i></a>
            <div class="popover bottom" style="${popoverStyle}">
		        <div class="arrow" style="${arrowStyle}"></div>
		        <div class="popover-content">
		        	<strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
			        <ul class="privacyHelp">
			        	<li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
			        	<li class="limited"style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
			        	<li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
			        </ul>
			        <a href="${knowledgeBaseUri}/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
		        </div>                
		    </div>
    	</div>				   					
	</div>
</#macro>

<#-- This macro is the base to improve the others privacy components into one -->
<#macro privacyComponent angularModel publicClick limitedClick privateClick placement="" popoverStyle="" arrowStyle="">	
	<div id="privacy-bar">
		<div class="relative privacy-component" style="width: 100px; float: left">
			<ul class="privacyToggle">
				<li class="publicActive" ng-class="{publicInActive: ${angularModel} != 'PUBLIC'}"><a href="" title="<@orcid.msg 'manage.lipublic' />" ng-click="${publicClick}"></a></li>
				<li class="limitedActive" ng-class="{limitedInActive: ${angularModel} != 'LIMITED'}"><a href="" title="<@orcid.msg 'manage.lilimited' />" ng-click="${limitedClick}"></a></li>
				<li class="privateActive" ng-class="{privateInActive: ${angularModel} != 'PRIVATE'}"><a href="" title="<@orcid.msg 'manage.liprivate' />" ng-click="${privateClick}"></a></li>
			</ul>
			<div class="popover-help-container">
	            <div class="popover ${placement}" style="${popoverStyle}">
			        <div class="arrow" style="${arrowStyle}"></div>
			        <div class="popover-content">
			        	<strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
				        <ul class="privacyHelp">
				        	<li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
				        	<li class="limited" style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
				        	<li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
				        </ul>
				        <a href="${knowledgeBaseUri}/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
			        </div>
			    </div>
	    	</div>
		</div>		
	</div>
</#macro>

<#macro itemDetails item="" field="" tag="div">
    <#if item != "">
        <${tag}><#if field != ""><b>${field}</b>: </#if>${item}</${tag}>
    </#if>
</#macro>

<#macro contributorList contributors>
    <#list contributors as contributor>
        ${(contributor.creditName.content)!} (${contributor.contributorAttributes.contributorRole?replace('_', ' ')?lower_case?cap_first}) <#if contributor_has_next >, </#if>
    </#list>    
</#macro>

<#macro closeTag>
    <#if xhtmlCompliant?exists && xhtmlCompliant>/><#else>></#if>
</#macro>

<#macro bind path>
    <#if htmlEscape?exists>
        <#assign status = springMacroRequestContext.getBindStatus(path, htmlEscape)>
    <#else>
        <#assign status = springMacroRequestContext.getBindStatus(path)>
    </#if>
<#-- assign a temporary value, forcing a string representation for any
kind of variable. This temp value is only used in this macro lib -->
    <#if status.value?exists && status.value?is_boolean>
        <#assign stringStatusValue=status.value?string>
    <#else>
        <#assign stringStatusValue=status.value?default("")>
    </#if>
</#macro>

<#macro orcidFormRadioButtons path options separator attributes="" labelAttributes="">
    <@bind path/>
    <#list options?keys as value>
        <#assign id="${status.expression}${value_index}">
    <label for="${id}" ${labelAttributes}>${options[value]?html}
            <input type="radio" id="${id}" name="${status.expression}" value="${value?html}"<#if stringStatusValue == value> checked="checked"</#if> ${attributes}<@closeTag/>
    </label>${separator}
    </#list>
</#macro>

<#macro orcidUrl orcid>
    ${request.scheme}://${request.serverName}<#if request.serverPort != 80 && request.serverPort != 443>:${request.serverPort?c}</#if>${request.contextPath}/${orcid}
</#macro>

<#macro absUrl url>
    <#compress>
    <#if (url.value)?? && !url.value?starts_with("http")>
        http://${url.value?html}
    <#else>
        ${url.value}	
    </#if> 
    </#compress>     
</#macro>

<#macro privacy thing selected="protected" btnContainerClass="btn-group privacy-group abs-left-top" helpLink="${knowledgeBaseUri}/articles/124518 ">
    <div class="privacy-tool">
        
        <div class="${btnContainerClass}">
            <#if selected == "" || selected == "public"><button class="btn btn-success dropdown-toggle privacy-toggle">${springMacroRequestContext.getMessage("manage.lipublic")} <span class="caret"></span></button></#if>
            <#if selected == "limited"><button class="btn btn-warning dropdown-toggle privacy-toggle">${springMacroRequestContext.getMessage("manage.lilimited")} <span class="caret"></span></button></#if>
            <#if selected == "private" || selected == "protected"><button class="btn btn-danger dropdown-toggle privacy-toggle">${springMacroRequestContext.getMessage("manage.liprivate")} <span class="caret"></span></button></#if>
            <ul class="privacy-dropdown-menu privacy-menu">
                <li><a class="btn btn-success btn-privacy" href="#public">${springMacroRequestContext.getMessage("manage.lipublic")} <span class="caret"></span></a></li>
                <li><a class="btn btn-warning btn-privacy" href="#limited">${springMacroRequestContext.getMessage("manage.lilimited")} <span class="caret"></span></a></li>
                <li><a class="btn btn-danger btn-privacy" href="#private">${springMacroRequestContext.getMessage("manage.liprivate")} <span class="caret"></span></a></li>	
                <li><a class="btn" href="${helpLink}" target="manage.lihelp">${springMacroRequestContext.getMessage("manage.lihelp")} <span class="caret"></span></a></li>
            </ul>
        </div>
    </div>
</#macro>

<#macro privacyLabel selected="private">
    <#if selected == "" || selected == "public"><span class="label label-success privacy-label">${springMacroRequestContext.getMessage("manage.lipublic")}</span></#if>
    <#if selected == "limited"><span class="label label-warning privacy-label">${springMacroRequestContext.getMessage("manage.lilimited")}</span></#if>
    <#if selected == "private" || selected == "protected"><span class="label label-important privacy-label">${springMacroRequestContext.getMessage("manage.liprivate")}</span></#if>
</#macro>

<#macro month number=0><#compress>
   <#attempt><#local months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'] />
      ${months[number?number-1]}
   <#recover>
      ${number}
   </#attempt>
</#compress></#macro>

<#--
	Make is easy to get properties.
-->
<#macro msg key, htmlEscape=false>${springMacroRequestContext.getMessage(key, [], "", htmlEscape)}</#macro>

<#macro msgUpCase key, htmlEscape=false>${springMacroRequestContext.getMessage(key, [], "", htmlEscape)?upper_case}</#macro>

<#macro msgCapFirst key, htmlEscape=false>${springMacroRequestContext.getMessage(key, [], "", htmlEscape)?lower_case?cap_first}</#macro>

<#--
 * unescapedMessage
 *
 * Macro to translate a message code into a message,
 * but with html escaping
 -->
<#macro unescapedMessage code>${springMacroRequestContext.getMessage(code, null, false)}</#macro>

<#--
 * unescapedMessageText
 *
 * Macro to translate a message code into a message,
 * using the given default text if no message found,
 * but without html escaping
 -->
<#macro unescapedMessageText code, text>${springMacroRequestContext.getMessage(code, text, false)}</#macro>

<#--
 * unescapedMessageArgs
 *
 * Macro to translate a message code with arguments into a message,
 * but without html escaping
 -->
<#macro unescapedMessageArgs code, args>${springMacroRequestContext.getMessage(code, args, false)}</#macro>

<#--
 * urlPath
 *
 * Macro to build urlPath
 -->
 
<#macro rootPath path><@spring.bind "basePath" />${basePath}${path?substring(1)}</#macro>


<#--
 * unescapedMessageArgsText
 *
 * Macro to translate a message code with arguments into a message,
 * using the given default text if no message found.
 -->
<#macro unescapedMessageArgsText code, args, text>${springMacroRequestContext.getMessage(code, args, text, false)}</#macro>

<#--
 * showErrorsUnescaped
 *
 * Show validation errors for the currently bound field, but
 * without html escaping.
 -->
<#macro showErrorsUnescaped>
    <@showErrorsUnescapedForPath spring.status.path/> 
</#macro>

<#--
 * showErrorsUnescapedForPath
 *
 * Show validation errors for the specified binding path, but
 * without html escaping.
 -->
<#macro showErrorsUnescapedForPath path>
    <#list spring.status.errors.getFieldErrors(path)?sort as error> <span class="orcid-error">${springMacroRequestContext.getMessage(error, false)}</span><#if error_has_next><br/></#if></#list>
</#macro>

<#macro showGlobalErrorsUnescaped>
    <#list spring.status.errors.globalErrors?sort as error> <span class="orcid-error">${springMacroRequestContext.getMessage(error, false)}</span><#if error_has_next><br/></#if></#list>
</#macro>

<#macro passwordHelpPopup>
	<div class="popover-help-container" style="display: inline; position: relative;">
		<a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
		<div id="name-help" class="popover bottom">
			<div class="arrow"></div>
			<div class="popover-content">
		    	<p>${springMacroRequestContext.getMessage("password_one_time_reset.labelmust8more")}</p>
		        <ul>
					<li>${springMacroRequestContext.getMessage("password_one_time_reset.labelatleast09")}</li>
					<li>${springMacroRequestContext.getMessage("password_one_time_reset.labelatleast1following")}
						<ul>
							<li>${springMacroRequestContext.getMessage("password_one_time_reset.labelalphacharacter")}</li>
							<li>${springMacroRequestContext.getMessage("password_one_time_reset.labelanyoffollow")}<br /> ! @ # $ % ^ * &#40; &#41; ~ ` &nbsp; &#123; &#125; &#91; &#93; | \ &amp; _</li>
						</ul>
					</li>
					<li>
					   ${springMacroRequestContext.getMessage("password_one_time_reset.labeloptionallyspace_1")}<br/>
					   ${springMacroRequestContext.getMessage("password_one_time_reset.labeloptionallyspace_2")}
					</li>
				</ul>                         
				<p>${springMacroRequestContext.getMessage("password_one_time_reset.commonpasswords")}<a href="https://github.com/danielmiessler/SecLists/blob/master/Passwords/10_million_password_list_top_1000.txt" target="password_one_time_reset.commonpasswordslink">${springMacroRequestContext.getMessage("password_one_time_reset.commonpasswordslink")}</a></p>
				<p><strong>${springMacroRequestContext.getMessage("password_one_time_reset.examplesunmoon")}</strong></p>
			</div>                
		</div>
	</div>
</#macro>

<#macro editActivityIcon activity click toolTipSuffix toolTipClass>	  
    <a ng-show="userIsSource(${activity})" ng-click="${click}" ng-mouseenter="showTooltip(${activity}.putCode.value+'-${toolTipSuffix}')" ng-mouseleave="hideTooltip(${activity}.putCode.value+'-${toolTipSuffix}')">
        <span class="glyphicon glyphicon-pencil"></span>
    </a>
    <a ng-show="!userIsSource(${activity}) && group.hasUserVersion()" ng-click="showSources(group)" ng-mouseenter="showTooltip(${activity}.putCode.value+'-${toolTipSuffix}')" ng-mouseleave="hideTooltip(${activity}.putCode.value+'-${toolTipSuffix}')">
        <span class="glyphicons git_create grey"></span>
    </a>
    <a ng-show="!userIsSource(${activity}) && !group.hasUserVersion() && group.hasKeys()" ng-click="${click}" ng-mouseenter="showTooltip(${activity}.putCode.value+'-${toolTipSuffix}')" ng-mouseleave="hideTooltip(${activity}.putCode.value+'-${toolTipSuffix}')">
        <span class="glyphicons git_create"></span>
    </a>
    <a ng-show="!userIsSource(${activity}) && !group.hasUserVersion() && !group.hasKeys()" ng-mouseenter="showTooltip(${activity}.putCode.value+'-${toolTipSuffix}')" ng-mouseleave="hideTooltip(${activity}.putCode.value+'-${toolTipSuffix}')">
        <span class="glyphicons git_create grey"></span>
    </a>
    <div class="${toolTipClass}" ng-show="showElement[${activity}.putCode.value+'-${toolTipSuffix}'] == true" ng-class="{'two-lines' : (!userIsSource(${activity}) && group.hasUserVersion()) || (!userIsSource(${activity}) && !group.hasUserVersion() && !group.hasKeys())}">
        <div class="arrow"></div>
        <div class="popover-content">        	   
              <span ng-show="userIsSource(${activity})"><@orcid.msg 'groups.common.edit_my' /></span>                            
              <span ng-show="!userIsSource(${activity}) && group.hasUserVersion()">
                <@orcid.msg 'groups.common.open_source_to_1' /><br />
                <@orcid.msg 'groups.common.open_source_to_2' />
              </span>
              <span ng-show="!userIsSource(${activity}) && !group.hasUserVersion() && group.hasKeys()"><@orcid.msg 'groups.common.make_a_copy' /></span>
              <span ng-show="!userIsSource(${activity}) && !group.hasUserVersion() && !group.hasKeys()">
                <@orcid.msg 'groups.common.items_must_have_1' /><br />
                <@orcid.msg 'groups.common.items_must_have_2' />
              </span>
        </div>
    </div>
</#macro>  

<#macro editWorkIcon activity click toolTipSuffix toolTipClass>   
    <a ng-show="userIsSource(${activity})" ng-click="${click}" ng-mouseenter="showTooltip(${activity}.putCode.value+'-${toolTipSuffix}')" ng-mouseleave="hideTooltip(${activity}.putCode.value+'-${toolTipSuffix}')">
        <span class="glyphicon glyphicon-pencil"></span>
    </a>
    <a ng-show="!userIsSource(${activity}) && group.userVersionPresent" ng-click="showSources(group)" ng-mouseenter="showTooltip(${activity}.putCode.value+'-${toolTipSuffix}')" ng-mouseleave="hideTooltip(${activity}.putCode.value+'-${toolTipSuffix}')">
        <span class="glyphicons git_create grey"></span>
    </a>
    <a ng-show="!userIsSource(${activity}) && !group.userVersionPresent && group.workExternalIdentifiers.length > 0" ng-click="${click}" ng-mouseenter="showTooltip(${activity}.putCode.value+'-${toolTipSuffix}')" ng-mouseleave="hideTooltip(${activity}.putCode.value+'-${toolTipSuffix}')">
        <span class="glyphicons git_create"></span>
    </a>
    <a ng-show="!userIsSource(${activity}) && !group.userVersionPresent && group.workExternalIdentifiers.length == 0" ng-mouseenter="showTooltip(${activity}.putCode.value+'-${toolTipSuffix}')" ng-mouseleave="hideTooltip(${activity}.putCode.value+'-${toolTipSuffix}')">
        <span class="glyphicons git_create grey"></span>
    </a>
    <div class="${toolTipClass}" ng-show="showElement[${activity}.putCode.value+'-${toolTipSuffix}'] == true" ng-class="{'two-lines' : (!userIsSource(${activity}) && group.userVersionPresent) || (!userIsSource(${activity}) && !group.userVersionPresent && group.workExternalIdentifiers.length == 0)}">
        <div class="arrow"></div>
        <div class="popover-content">              
              <span ng-show="userIsSource(${activity})"><@orcid.msg 'groups.common.edit_my' /></span>                            
              <span ng-show="!userIsSource(${activity}) && group.userVersionPresent">
                <@orcid.msg 'groups.common.open_source_to_1' /><br />
                <@orcid.msg 'groups.common.open_source_to_2' />
              </span>
              <span ng-show="!userIsSource(${activity}) && !group.userVersionPresent && group.workExternalIdentifiers.length > 0"><@orcid.msg 'groups.common.make_a_copy' /></span>
              <span ng-show="!userIsSource(${activity}) && !group.userVersionPresent && group.workExternalIdentifiers.length == 0">
                <@orcid.msg 'groups.common.items_must_have_1' /><br />
                <@orcid.msg 'groups.common.items_must_have_2' />
              </span>
        </div>
    </div>
</#macro>

<#macro editWorkIconNg2 activity click toolTipSuffix toolTipClass> 
    <!--  
    <a 
        *ngIf="userIsSource(${activity})" 
        (click)="${click}" 
        (mouseenter)="showTooltip(${activity}.putCode.value +'-${toolTipSuffix}')" 
        (mouseleave)="hideTooltip(${activity}.putCode.value +'-${toolTipSuffix}')">
        <span class="glyphicon glyphicon-pencil"></span>
    </a>
    <a 
        *ngIf"!userIsSource(${activity}) && group.userVersionPresent" 
        (click)="showSources(group)" 
        (mouseenter)="showTooltip(${activity}.putCode.value +'-${toolTipSuffix}')" 
        (mouseleave)="hideTooltip(${activity}.putCode.value +'-${toolTipSuffix}')">
        <span class="glyphicons git_create grey"></span>
    </a>
    <a 
        *ngIf="!userIsSource(${activity}) && !group.userVersionPresent && group.workExternalIdentifiers.length > 0" 
        (click)="${click}" 
        (mouseenter)="showTooltip(${activity}.putCode.value+'-${toolTipSuffix}')" 
        (mouseleave)="hideTooltip(${activity}.putCode.value+'-${toolTipSuffix}')">
        <span class="glyphicons git_create"></span>
    </a>
    -->
    <a 
        *ngIf="!userIsSource(${activity}) && !group.userVersionPresent && group.workExternalIdentifiers.length == 0" 
        (mouseenter)="showTooltip(${activity}.putCode.value+'-${toolTipSuffix}')" 
        (mouseleave)="hideTooltip(${activity}.putCode.value+'-${toolTipSuffix}')">
        <span class="glyphicons git_create grey"></span>
    </a>
    <div 
        class="${toolTipClass}" 
        *ngIf="showElement[${activity}.putCode.value+'-${toolTipSuffix}'] == true" 
        [ngClass]="{'two-lines' : (!userIsSource(${activity}) && group.userVersionPresent) || (!userIsSource(${activity}) && !group.userVersionPresent && group.workExternalIdentifiers.length == 0)}">
        <div class="arrow"></div>
        <div class="popover-content">              
            <span *ngIf="userIsSource(${activity})"><@orcid.msg 'groups.common.edit_my' /></span>                            
            <span *ngIf="!userIsSource(${activity}) && group.userVersionPresent"><@orcid.msg 'groups.common.open_source_to' /></span>
            <span *ngIf="!userIsSource(${activity}) && !group.userVersionPresent && group.workExternalIdentifiers.length > 0"><@orcid.msg 'groups.common.make_a_copy' /></span>
            <span *ngIf="!userIsSource(${activity}) && !group.userVersionPresent && group.workExternalIdentifiers.length == 0">
                <@orcid.msg 'groups.common.items_must_have_1' />
                <br />
                <@orcid.msg 'groups.common.items_must_have_2' />
            </span>
        </div>
    </div>
</#macro>  

<#macro privacyToggle2 angularModel publicClick limitedClick privateClick popoverStyle="" arrowStyle="" questionClick="alert('no function passed')" clickedClassCheck="{'popover-help-container-show':privacyHelp['work']==true}">	
	<div class="relative" id="privacy-bar">
		<ul class="privacyToggle" ng-mouseenter="showTooltip(group.groupId+'-privacy')" ng-mouseleave="hideTooltip(group.groupId+'-privacy')">
			<li class="publicActive" ng-class="{publicInActive: ${angularModel} != 'PUBLIC'}"><a ng-click="${publicClick}"></a></li>
			<li class="limitedActive" ng-class="{limitedInActive: ${angularModel} != 'LIMITED'}"><a ng-click="${limitedClick}"></a></li>
			<li class="privateActive" ng-class="{privateInActive: ${angularModel} != 'PRIVATE'}"><a ng-click="${privateClick}"></a></li>
		</ul>
	</div>
	<div class="popover-help-container">
       <div class="popover top privacy-myorcid3" ng-class="showElement[group.groupId+'-privacy'] == true ? 'block' : ''">
			<div class="arrow"></div>
			<div class="popover-content">
		    	<strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
			    <ul class="privacyHelp">
				    <li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
				    <li class="limited" style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
				    <li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
			    </ul>
		       <a href="${knowledgeBaseUri}/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
		    </div>                
	  	</div>    			   				
 	</div>
</#macro>

<#macro privacyToggle2Ng2 angularModel publicClick limitedClick privateClick elementId popoverStyle="" arrowStyle="" questionClick="alert('no function passed')" clickedClassCheck="{'popover-help-container-show':privacyHelp['work']==true}">  
<div [ngClass]="{'relative' : modal == false}" id="privacy-bar">
    <ul class="privacyToggle" (mouseenter)="commonSrvc.showPrivacyHelp(${elementId} +'-privacy', $event, 145)" (mouseleave)="commonSrvc.hideTooltip(${elementId} +'-privacy')">
        <li class="publicActive" [ngClass]="{publicInActive: ${angularModel} != 'PUBLIC'}"><a (click)="${publicClick}"></a></li>
        <li class="limitedActive" [ngClass]="{limitedInActive: ${angularModel} != 'LIMITED'}"><a (click)="${limitedClick}"></a></li>
        <li class="privateActive" [ngClass]="{privateInActive: ${angularModel} != 'PRIVATE'}"><a (click)="${privateClick}"></a></li>
    </ul>
</div>
<div class="popover-help-container">
   <div class="popover top privacy-myorcid3" [ngClass]="commonSrvc.shownElement[${elementId} +'-privacy'] == true ? 'block' : ''">
        <div class="arrow"></div>
        <div class="popover-content">
            <strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
            <ul class="privacyHelp">
                <li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
                <li class="limited" style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
                <li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
            </ul>
           <a href="${knowledgeBaseUri}/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
        </div>                
    </div>                              
</div>
</#macro>

<#macro privacyToggle3 angularModel publicClick limitedClick privateClick elementId publicId="" limitedId="" privateId="" popoverStyle="" arrowStyle="" questionClick="alert('no function passed')" clickedClassCheck="{'popover-help-container-show':privacyHelp['work']==true}">  
<div ng-class="{'relative' : modal == false}" id="privacy-bar">
    <ul class="privacyToggle" ng-mouseenter="commonSrvc.showPrivacyHelp(${elementId} +'-privacy', $event, 145)" ng-mouseleave="commonSrvc.hideTooltip(${elementId} +'-privacy')">
        <li class="publicActive" ng-class="{publicInActive: ${angularModel} != 'PUBLIC'}"><a ng-click="${publicClick}" name="privacy-toggle-3-public" id="${publicId}"></a></li>
        <li class="limitedActive" ng-class="{limitedInActive: ${angularModel} != 'LIMITED'}"><a ng-click="${limitedClick}" name="privacy-toggle-3-limited" id="${limitedId}"></a></li>
        <li class="privateActive" ng-class="{privateInActive: ${angularModel} != 'PRIVATE'}"><a ng-click="${privateClick}"  name="privacy-toggle-3-private" id="${privateId}"></a></li>
    </ul>
</div>
<div class="popover-help-container">
   <div class="popover top privacy-myorcid3" ng-class="commonSrvc.shownElement[${elementId} +'-privacy'] == true ? 'block' : ''">
        <div class="arrow"></div>
        <div class="popover-content">
            <strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
            <ul class="privacyHelp">
                <li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
                <li class="limited" style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
                <li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
            </ul>
           <a href="${knowledgeBaseUri}/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
        </div>                
    </div>                              
</div>
</#macro>

<#macro privacyToggle3Ng2 angularModel publicClick limitedClick privateClick elementId position="top" publicId="" limitedId="" privateId="" popoverStyle="" arrowStyle=""> 
    <div [ngClass]="{'relative' : modal == false}" id="privacy-bar">
        <ul class="privacyToggle" (mouseenter)="commonSrvc.showPrivacyHelp(${elementId} +'-privacy', $event, 145)" (mouseleave)="commonSrvc.hideTooltip(${elementId} +'-privacy')">
            <li class="publicActive" [ngClass]="{publicInActive: ${angularModel} != 'PUBLIC'}"><a (click)="${publicClick}" name="privacy-toggle-3-public" id="${publicId}"></a></li>
            <li class="limitedActive" [ngClass]="{limitedInActive: ${angularModel} != 'LIMITED'}"><a (click)="${limitedClick}" name="privacy-toggle-3-limited" id="${limitedId}"></a></li>
            <li class="privateActive" [ngClass]="{privateInActive: ${angularModel} != 'PRIVATE'}"><a (click)="${privateClick}"  name="privacy-toggle-3-private" id="${privateId}"></a></li>
        </ul>
    </div>
    <div class="popover-help-container" >
       <div class="popover ${position} privacy-myorcid3" [ngClass]="commonSrvc.shownElement[${elementId} +'-privacy'] == true ? 'block' : ''">
            <div class="arrow"></div>
            <div class="popover-content">
                <strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
                <ul class="privacyHelp">
                    <li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
                    <li class="limited"style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
                    <li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
                </ul>
               <a href="${knowledgeBaseUri}/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
            </div>                
        </div>                              
    </div>
</#macro>

<#macro registrationEmailFrequencySelector angularElementName>
<div>	
    <h4 class="dark-label">${springMacroRequestContext.getMessage("claim.notifications")}</h4>                
    <label class="control-label dark-label">
        ${springMacroRequestContext.getMessage("claim.notificationsemailfrequency_1")}<a href="https://support.orcid.org/knowledgebase/articles/665437" target="learn_more">${springMacroRequestContext.getMessage("claim.notificationsemailfrequency_2")}</a>${springMacroRequestContext.getMessage("claim.notificationsemailfrequency_3")}
    </label>
    <select id="sendEmailFrequencyDays" name="sendEmailFrequencyDays"
    	class="input-xlarge"
     	ng-model="${angularElementName}.sendEmailFrequencyDays.value">
		<#list sendEmailFrequencies?keys as key>
			<option value="${key}" ng-selected="${angularElementName}.sendEmailFrequencyDays.value === ${key}">${sendEmailFrequencies[key]}</option>
		</#list>
    </select>        
</div>        
</#macro>

<#macro registrationEmailFrequencySelectorNg2 angularElementName>
<div>   
    <h4 class="dark-label">${springMacroRequestContext.getMessage("claim.notifications")}</h4>                
    <label class="control-label dark-label">
        ${springMacroRequestContext.getMessage("claim.notificationsemailfrequency_1")}<a href="https://support.orcid.org/knowledgebase/articles/665437" target="learn_more">${springMacroRequestContext.getMessage("claim.notificationsemailfrequency_2")}</a>${springMacroRequestContext.getMessage("claim.notificationsemailfrequency_3")}
    </label>
    <select id="sendEmailFrequencyDays" name="sendEmailFrequencyDays"
        class="input-xlarge"
        [(ngModel)]="${angularElementName}.sendEmailFrequencyDays.value">
        <#list sendEmailFrequencies?keys as key>
            <option value="${key}" ng-selected="${angularElementName}.sendEmailFrequencyDays.value === ${key}">${sendEmailFrequencies[key]}</option>
        </#list>
    </select>        
</div>        
</#macro>

<#macro tooltip elementId message>
	<div>	
		<div class="popover popover-tooltip top" ng-class="commonSrvc.shownElement[${elementId}] == true ? 'block' : ''">
	    	<div class="arrow"></div>
	    	<div class="popover-content">
				<span><@spring.message "${message}"/></span>
	    	</div>
	   	</div>                
   	</div>	
</#macro>

<#macro tooltipNg2 elementId message>
    <div>   
        <div class="popover popover-tooltip top" [ngClass]="commonSrvc.shownElement[${elementId}] == true ? 'block' : ''">
            <div class="arrow"></div>
            <div class="popover-content">
                <span><@spring.message "${message}"/></span>
            </div>
        </div>                
    </div>  
</#macro>

<#macro checkFeatureStatus featureName enabled=true>
    <#if enabled>
        <#if RequestParameters[featureName]??>    
            <#nested>
        <#elseif FEATURE[featureName]>     
            <#nested>
        </#if>
    <#else>
        <#if !RequestParameters[featureName]?? && !FEATURE[featureName]>
            <#nested>
        </#if>
    </#if>
</#macro>