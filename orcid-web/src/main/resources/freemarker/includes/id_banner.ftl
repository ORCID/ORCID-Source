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
<#escape x as x?html>
<script type="text/ng-template" id="name-ng2-template">
    <!-- Name -->    
    <div class="workspace-section" id="names-section"> 
        <div *ngIf="!showEdit" (click)="toggleEdit()">
            <div class="row">               
                <div class="col-md-12">
                    <div class="workspace-section-title">
                        <div class="edit-name edit-option" *ngIf="!showEdit" id="open-edit-names">
                            <div class="glyphicon glyphicon-pencil">
                                <div class="popover popover-tooltip top">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span><@orcid.msg 'manage_bio_settings.editName'/></span>
                                    </div>                
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <h2 class="full-name">
                        <span *ngIf="displayFullName()">{{nameForm?.creditName?.value}}
                        </span>
                        <span *ngIf="displayPublishedName()">
                            {{nameForm?.givenNames?.value}} <span *ngIf="nameForm?.familyName?.value != null" >{{nameForm?.familyName?.value}}</span>
                        </span>
                    </h2>
                </div>
            </div>
        </div>
        <!-- Edit Mode -->
        <div class="names-edit" *ngIf="showEdit == true">

            <label for="firstName">${springMacroRequestContext.getMessage("manage_bio_settings.labelfirstname")}</label>
           
            <input type="text" [(ngModel)]="nameForm.givenNames.value" (keydown)="setNameFormEnter($event)" class="full-width-input" />
           
            <span class="orcid-error" *ngIf="nameForm.givenNames.errors.length > 0">
                <div *ngFor='let error of nameForm.givenNames.errors'>{{error}}</div>
            </span>
            <label for="lastName">${springMacroRequestContext.getMessage("manage_bio_settings.labellastname")}</label>
           
            <input type="text" [(ngModel)]="nameForm.familyName.value" (keydown)="setNameFormEnter($event)" class="full-width-input" />
           
            <label for="creditName">${springMacroRequestContext.getMessage("manage_bio_settings.labelpublishedname")}</label>                               
            <input type="text" [(ngModel)]="nameForm.creditName.value" (keydown)="setNameFormEnter($event)" class="full-width-input" />
           
            <div>
                <privacy-toggle-ng2 
                    [dataPrivacyObj]="nameForm" 
                    (privacyUpdate)="privacyChange($event)"
                    elementId="name-privacy-toggle" 
                    privacyNodeName="namesVisibility" 
                ></privacy-toggle-ng2>

                <a href="${knowledgeBaseUri}/articles/142948-names-in-the-orcid-registry" target="142948-names-in-the-orcid-registry"><i class="glyphicon glyphicon-question-sign help-glyphicon" style="width: 14px;"></i></a>
            </div>
            <ul class="workspace-section-toolbar clearfix">
                <li class="pull-right">
                    <button class="btn btn-primary" (click)="setNameForm( true )"><@spring.message "freemarker.btnsavechanges"/></button>
                </li>
                <li class="pull-right">
                    <a class="cancel-option" (click)="cancel()"><@spring.message "freemarker.btncancel"/></a>
                </li>
            </ul>
        </div>
    </div>
</script>


<div class="id-banner <#if inDelegationMode>delegation-mode</#if>"> 
    
    <#if inDelegationMode><span class="delegation-mode-warning">${springMacroRequestContext.getMessage("delegate.managing_record")}</span></#if>
    
    <!-- Name -->
    
    <name-ng2 class="clearfix"></name-ng2>

    <div class="oid">
        <div class="id-banner-header">
            <span><@orcid.msg 'common.orcid_id' /></span>
        </div>
        <div class="orcid-id-container">
            <div class="orcid-id-info">
                <span class="mini-orcid-icon"></span>
                <!-- Reference: orcid.js:removeProtocolString() -->
                <span id="orcid-id" class="orcid-id shortURI">${baseDomainRmProtocall}/${(effectiveUserOrcid)!}</span>
            </div>
            <div class="orcid-id-options">
                <@orcid.checkFeatureStatus featureName='HTTPS_IDS'>
                    <a href="${baseUri}/${(effectiveUserOrcid)!}" class="gray-button" target="id_banner.viewpublicprofile"><@orcid.msg 'id_banner.viewpublicprofile'/></a>
                </@orcid.checkFeatureStatus>
                <@orcid.checkFeatureStatus featureName='HTTPS_IDS' enabled=false>
                    <a href="${baseUriHttp}/${(effectiveUserOrcid)!}" class="gray-button" target="id_banner.viewpublicprofile"><@orcid.msg 'id_banner.viewpublicprofile'/></a>
                </@orcid.checkFeatureStatus>
            </div>
        </div>
    </div>
    <#if (locked)?? && !locked>
        <div ng-controller="SwitchUserCtrl" class="dropdown id-banner-container" ng-show="unfilteredLength" ng-cloak>
            <a ng-click="openMenu($event)" class="id-banner-switch"><@orcid.msg 'public-layout.manage_proxy_account'/><span class="glyphicon glyphicon-chevron-right"></span></a>
            <ul class="dropdown-menu id-banner-dropdown" ng-show="isDroppedDown" ng-cloak>
                <li>
                    <input id="delegators-search" type="text" ng-model="searchTerm" ng-change="search()" placeholder="<@orcid.msg 'manage_delegators.search.placeholder'/>"></input>
                </li>
                <li ng-show="me && !searchTerm">
                    <a href="<@orcid.rootPath '/switch-user?username='/>{{me.delegateSummary.orcidIdentifier.path}}">
                        <ul>
                            <li><@orcid.msg 'id_banner.switchbacktome'/></li>
                            <li>{{me.delegateSummary.orcidIdentifier.uri}}</li>
                        </ul>
                    </a>
                </li>
                <li ng-repeat="delegationDetails in delegators.delegationDetails | orderBy:'delegateSummary.creditName.content' | limitTo:10">
                    <a href="<@orcid.rootPath '/switch-user?username='/>{{delegationDetails.delegateSummary.orcidIdentifier.path}}">
                        <ul>
                            <li>{{delegationDetails.delegateSummary.creditName.content}}</li>
                            <li>{{delegationDetails.delegateSummary.orcidIdentifier.uri}}</li>
                        </ul>
                    </a>
                </li>
                <li><a href="<@orcid.rootPath '/delegators?delegates'/>"><@orcid.msg 'id_banner.more'/></a></li>
            </ul>
        </div>  
    </#if>
</div>
</#escape>
