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

<script type="text/ng-template" id="switch-user-ng2-template">
    <div>
        <div class="dropdown id-banner-container" *ngIf="(me || unfilteredLength > 0)">
            <a (click)="openMenu($event)" class="id-banner-switch">
                <div class="orcid-id-container">
                        ${baseUri}/{{requestInfoForm?.userOrcid}}
                    <span class="glyphicon glyphicon-chevron-down"></span>
                </div>
            </a>
            <div class="dropdown-menu id-banner-dropdown" *ngIf="isDroppedDown">
                <div class="id-banner-header"><@orcid.msg'public-layout.manage_proxy_account'/></div>
                <ul class="id-banner-dropdown">
                    <li>
                        <input id="delegators-search" type="text" [(ngModel)]="searchTerm" (change)="search()" placeholder="<@orcid.msg 'manage_delegators.search.placeholder'/>"/>
                    </li>
                    <li *ngIf="me && !searchTerm">
                        <a (click)="switchUser(me.giverOrcid.path)">
                            <ul>
                                <li><@orcid.msg 'id_banner.switchbacktome'/></li>                                       
                                <li>{{me.giverOrcid.uri}}</li>                                                          
                            </ul>
                        </a>
                    </li>
                    <li *ngFor="let delegationDetails of delegators | orderBy:'giverName.value' | slice:0:10">
                        <a (click)="switchUser(delegationDetails.giverOrcid.path)">
                            <ul>
                                <li>{{delegationDetails.giverName.value}}</li>
                                <li>{{delegationDetails.giverOrcid.uri}}</li>                                       
                            </ul>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
        <div *ngIf="!(me || unfilteredLength > 0)">
            <div class="pull-right">
                    <a href="${baseUri}/{{requestInfoForm?.userOrcid}}" target="userOrcid">${baseUri}/{{requestInfoForm?.userOrcid}}</a>
            </div>
        </div>
    </div>                    
</script>