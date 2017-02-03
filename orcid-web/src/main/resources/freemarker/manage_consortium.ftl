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
<@public nav="manage-consortium">
    <div class="row" ng-controller="externalConsortiumCtrl">
        <div class="col-md-3 lhs col-sm-12 col-xs-12 padding-fix">
            <#include "includes/id_banner.ftl"/>
        </div>
        <div class="col-md-9 col-sm-12 col-xs-12">
            <h1 id="manage-consortium-lead"><@spring.message "manage_consortium.manage_consortium"/></h1>
            <p><@spring.message "manage_consortium.manage_consortium_text_1"/>
            	<a href="<@orcid.rootPath '/members'/>" target="_blank"><@spring.message "manage_consortium.member_list_link"/></a>
            	<@spring.message "manage_consortium.manage_consortium_text_2"/>
            	<a href="mailto:<@spring.message "manage_consortium.support_email"/>"><@spring.message "manage_consortium.support_email"/></a></p>
            <div ng-show="consortium != null" ng-cloak>
                <div class="manage-consortium">
                	<h2 id="manage-consortium-lead"><@spring.message "manage_consortium.consortium_lead"/></h2>
                    <!-- Name -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label><@orcid.msg 'manage_consortium.org_name'/></label>
                            <input type="text" ng-model="consortium.name.value" class="full-width-input" />
                            <span class="orcid-error" ng-show="consortium.name.errors.length > 0">
                                <div ng-repeat='error in consortium.name.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- website -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label><@orcid.msg 'manage_consortium.website'/></label>
                            <input type="text" ng-model="consortium.website.value" class="full-width-input" />
                            <span class="orcid-error" ng-show="consortium.website.errors.length > 0">
                                <div ng-repeat='error in consortium.website.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- Buttons -->
                    <div class="row">
                        <div class="controls save-btns col-md-12 col-sm-12 col-xs-12">
                            <span id="bottom-confirm-update-consortium" ng-click="confirmUpdateConsortium()" class="btn btn-primary"><@orcid.msg 'admin.edit_client.btn.update'/></span>
                        </div>
                    </div>                      
                </div>
            </div>
        </div>
    </div>
    <script type="text/ng-template" id="confirm-modal-consortium">
        <div class="lightbox-container">
            <div class="row">
                <div class="col-md-12 col-xs-12 col-sm-12">
                    <h3><@orcid.msg 'manage_member.edit_member.confirm_update.title' /></h3>    
                    <p><@orcid.msg 'manage_member.edit_memeber.confirm_update.text' /></p>          
                    <p><strong>{{member.groupName.value}}</strong></p>                      
                    <div class="btn btn-danger" ng-click="updateConsortium()">
                        <@orcid.msg 'manage_member.edit_member.btn.update' />
                    </div>
                    <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
                </div>
            </div>
        </div>
    </script>
</@public>
