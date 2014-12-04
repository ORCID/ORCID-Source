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
<#-- @ftlvariable name="profile" type="org.orcid.jaxb.model.message.OrcidProfile" -->
<@protected nav="notifications">
<#escape x as x?html>
<div class="col-md-3 lhs left-aside">
    <div class="workspace-profile">
        <#include "includes/id_banner.ftl"/>
    </div>
</div>
<div class="col-md-9 right-aside" ng-controller="NotificationsCtrl" >
    <h1>${springMacroRequestContext.getMessage("notifications.title")}</h1>
    <div ng-hide="notifications.length > 0">${springMacroRequestContext.getMessage("notifications.none")}</div>
    <div ng-show="notifications.length > 0">
        <table class="notifications" width="100%" >
            <tr>
                <th>${springMacroRequestContext.getMessage("notifications.from")}</th>
                <th>${springMacroRequestContext.getMessage("notifications.subject")}</th>
                <th>${springMacroRequestContext.getMessage("notifications.date")}</th>
            </tr>
            <tr ng-repeat-start="notification in notifications" ng-class="{unread: !notification.readDate}" class="header">
                <td ng-click="toggleDisplayBody(notification.putCode.path)">
                    <i class="glyphicon-chevron-down glyphicon x0" ng-class="{'glyphicon-chevron-right':!displayBody[notification.putCode.path]}"></i>
                    <span ng-show="notification.source" ng-cloak>{{notification.source.sourceName}}</span><span ng-hide="notification.source" ng-cloak>ORCID</span>
                </td>
                <td ng-click="toggleDisplayBody(notification.putCode.path)"><span ng-cloak>{{notification.subject}}</span></td>
                <td ng-click="toggleDisplayBody(notification.putCode.path)"><span ng-cloak>{{notification.createdDate|date:'yyyy-MM-ddTHH:mm'}}</span></td>
                <td>
                    <span><a href="" ng-click="archive(notification.putCode.path)" class="glyphicon glyphicon-folder-open grey" title="${springMacroRequestContext.getMessage("notifications.archive")}"></a></span>
                </td>
            </tr>
            <tr ng-repeat-end ng-show="displayBody[notification.putCode.path]">
                <td colspan="4">
                    <iframe ng-src="{{ '<@spring.url '/notifications'/>/' + notification.notificationType + '/' + notification.putCode.path + '/notification.html'}}" frameborder="0" width="100%" height="300"></iframe>
                </td>
            </tr>
        </table>
        <div ng-cloak>
            <button ng-show="areMore()" ng-click="showMore()" class="btn" type="submit" id="show-more-button">Show more</button>
            <br></br>
            <br></br>
        </div>
    </div>
</div>
</#escape>
</@protected>