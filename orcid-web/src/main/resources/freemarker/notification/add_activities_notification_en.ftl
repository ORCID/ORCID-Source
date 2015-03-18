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
<!DOCTYPE html>
<html>
<#assign verDateTime = startupDate?datetime>
<#assign ver="${verDateTime?iso_utc}">
<#include "/common/html-head.ftl" />
<style>
	body, html{
		height: auto;
	}
</style>
<body data-baseurl="<@spring.url '/'/>">
<div>
    ${notification.source.sourceName.content} has ${notification.activities.activities?size}
    <#if notification.activities.activities?size == 1>
        activity
    <#else>
        activities
    </#if>
    to add to your record.
</div>
<#list notification.activities.activities as activity>
    <div>
        ${activity.activityType}: ${activity.activityName}
        <#if activity.externalId??>
            (${activity.externalId.externalIdType}: ${activity.externalId.externalIdValue})
        </#if>
    </div>
</#list>
<#if notification.authorizationUrl??>
    <div>Click <a href="${notification.authorizationUrl.uri}" target="_blank">here</a> to continue.</div>
</#if>
<script type="text/javascript" src="${staticCdn}/javascript/iframeResizer.contentWindow.min.js?v=${ver}"></script><!-- required for iframe resizing -->
</body>
</html>