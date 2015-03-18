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
<div class="row">
    <div class="col-md-3 col-sm-12 col-xs-12 padding-fix">
        <#include "admin_menu.ftl"/>
    </div>
    <div class="col-md-9 col-sm-12 col-xs-12" ng-controller="NotificationsCtrl" >
        <h1>${springMacroRequestContext.getMessage("notifications.title")}</h1>
        <div>
            <label class="checkbox">
                <input type="checkbox" ng-model="notificationsSrvc.showArchived" ng-change="reloadNotifications()"></input>
                ${springMacroRequestContext.getMessage("notifications.showArchived")}
            </label>
        </div>
        <div ng-hide="notifications.length > 0">${springMacroRequestContext.getMessage("notifications.none")}</div>
        <div ng-show="notifications.length > 0">            
            <table class="table table-responsive table-condensed notifications">
           		<thead>					
	                <tr>
	                    <th>${springMacroRequestContext.getMessage("notifications.from")}</th>
	                    <th>${springMacroRequestContext.getMessage("notifications.subject")}</th>
	                    <th>${springMacroRequestContext.getMessage("notifications.date")}</th>
	                    <th></th>
	                </tr>
                </thead>
                <tbody>
	                <tr ng-repeat-start="notification in notifications" ng-class="{unread: !notification.readDate, archived: notification.archivedDate}">
	                    <td ng-click="toggleDisplayBody(notification.putCode)">
	                        <i class="glyphicon-chevron-down glyphicon x0" ng-class="{'glyphicon-chevron-right':!displayBody[notification.putCode]}"></i>
	                        <span ng-show="notification.source" ng-cloak>{{notification.source.sourceName.content}}</span><span ng-hide="notification.source" ng-cloak>ORCID</span>
	                    </td>
	                    <td ng-click="toggleDisplayBody(notification.putCode)"><span ng-cloak>{{notification.subject}}</span></td>
	                    <td ng-click="toggleDisplayBody(notification.putCode)"><span ng-cloak>{{notification.createdDate|humanDate}}</span></td>
	                    <td>
	                        <span ng-hide="notification.archivedDate"><a href="" ng-click="archive(notification.putCode)" class="glyphicon glyphicon-download-alt grey" title="${springMacroRequestContext.getMessage("notifications.archive")}"></a></span>
	                    </td>
	                </tr>
	                <tr ng-repeat-end ng-show="displayBody[notification.putCode]">
	                    <td colspan="4">
	                        <iframe id="{{notification.putCode}}" ng-src="{{ '<@spring.url '/notifications'/>/' + notification.notificationType + '/' + notification.putCode + '/notification.html'}}" frameborder="0" width="100%" scrolling="no"></iframe>
	                    </td>
	                </tr>
                </tbody>

            </table>
            
            
            <div ng-cloak>
                <button ng-show="areMore()" ng-click="showMore()" class="btn" type="submit" id="show-more-button">Show more</button>
                <br></br>
                <br></br>
            </div>
        </div>
    </div>
</div>

</#escape>
</@protected>
