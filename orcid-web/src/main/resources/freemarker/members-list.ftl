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
<@public classes=['home'] nav="members-list">
    <div class="row">        
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <div ng-controller="MembersListController">
                <div ng-hide="membersListSrvc.membersList != null" class="text-center">
                    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                    <!--[if lt IE 8]>
                        <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
                    <![endif]-->
                </div>
                <div ng-show="membersListSrvc.membersList">
                    <div ng-repeat="member in membersListSrvc.membersList">
                        <h2>{{member.name}}</h2>
                        <hr></hr>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@public>