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
<@public nav="self-service">
    <div class="row" ng-controller="externalConsortiumCtrl">
        <div class="col-md-3 lhs col-sm-12 col-xs-12 padding-fix">
            <#include "includes/id_banner.ftl"/>
        </div>
        <div class="col-md-9 col-sm-12 col-xs-12 self-service">
            <h1 id="self-service-lead" ng-show="memberDetails.consortiumLead"><@spring.message "manage_consortium.manage_consortium"/></h1>
            <h1 id="self-service-lead" ng-show="!memberDetails.consortiumLead"><@spring.message "manage_consortium.manage_member"/></h1>
            <p><@spring.message "manage_consortium.manage_consortium_text_1"/>
            	<@spring.message "manage_consortium.manage_consortium_text_2"/>
            	<a href="mailto:<@spring.message "manage_consortium.support_email"/>"><@spring.message "manage_consortium.support_email"/></a></p>
            <div ng-show="memberDetails != null" ng-cloak>
                <div class="topBuffer">
                    <h3 class="topBuffer" ng-show="memberDetails.consortiumLead"><@spring.message "manage_consortium.public_display"/></h3>
                    <h3 class="topBuffer" ng-show="!memberDetails.consortiumLead"><@spring.message "self_serve.public_display_heading"/></h3>
                    <p><@spring.message "self_serve.public_display_text"/> <a href="<@orcid.rootPath '/members'/>" target="manage_consortium.member_list_link"><@spring.message "manage_consortium.member_list_link"/></a></p>
                    <!-- Name -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label><@orcid.msg 'manage_consortium.org_name'/></label>
                            <input type="text" ng-model="memberDetails.name.value" ng-change="validateMemberDetailsField('name')" ng-model-onblur class="input-95-width" />
                            <span class="required" ng-class="isValidClass(memberDetails.name)" >*</span>
                            <span class="orcid-error" ng-show="newSubMember.name.errors.length > 0">
                                <div ng-repeat='error in newSubMember.name.errors' ng-bind-html="error"></div>
                            </span>
                            <span class="orcid-error" ng-show="memberDetails.name.errors.length > 0">
                                <div ng-repeat='error in memberDetails.name.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- website -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label><@orcid.msg 'manage_consortium.website'/></label>
                            <input type="text" ng-model="memberDetails.website.value" ng-change="validateMemberDetailsField('website')" ng-model-onblur class="input-95-width" />
                            <span class="orcid-error" ng-show="memberDetails.website.errors.length > 0">
                                <div ng-repeat='error in memberDetails.website.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- Public display email -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label><@orcid.msg 'manage_consortium.email'/></label>
                            <input type="text" ng-model="memberDetails.email.value" ng-change="validateMemberDetailsField('email')" ng-model-onblur class="input-95-width" />
                            <span class="orcid-error" ng-show="memberDetails.email.errors.length > 0">
                                <div ng-repeat='error in memberDetails.email.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- Description -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label><@orcid.msg 'manage_consortium.description'/></label>
                            <textarea ng-model="memberDetails.description.value" ng-change="validateMemberDetailsField('description')" ng-model-onblur class="input-95-width" ></textarea>
                            <span class="orcid-error" ng-show="memberDetails.description.errors.length > 0">
                                <div ng-repeat='error in memberDetails.description.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- Community -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label><@orcid.msg 'manage_consortium.community'/></label>
                             <select id="communities" name="communities"
								    	class="input-xlarge"
								     	ng-model="memberDetails.community.value" ng-change="validateMemberDetailsField('community')" ng-model-onblur >
										<#list communityTypes?keys as key>
											<option value="${key}" ng-selected="contact.community.value === '${key}'">${communityTypes[key]}</option>
										</#list>
								    </select>            
                            <span class="orcid-error" ng-show="memberDetails.community.errors.length > 0">
                                <div ng-repeat='error in memberDetails.community.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- Buttons -->
	                <div class="row">
	                    <div class="controls bottomBuffer col-md-12 col-sm-12 col-xs-12">
	                    	<span id="ajax-loader" class="ng-cloak" ng-show="updateMemberDetailsShowLoader"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span><br>
	                        <button id="bottom-confirm-update-consortium" class="btn btn-primary" ng-click="validateMemberDetails()" ng-disabled="updateMemberDetailsDisabled"><@orcid.msg 'manage_consortium.save_public_info'/></button>
	                        <a href="" class="cancel-right" ng-click="closeModalReload()"><@orcid.msg 'manage_consortium.clear_changes' /></a>
	                    </div>
	                </div> 
                </div>
                <!-- Contacts -->
                <div>
                    <h3 ng-show="memberDetails.consortiumLead"><@spring.message "manage_consortium.contacts_heading"/></h3>
                    <h3 ng-show="!memberDetails.consortiumLead"><@spring.message "self_serve.contacts_heading"/></h3>
                    <p>
                        <@spring.message "manage_consortium.contacts_text"/>
                    </p>
                    <table>
                        <thead>
                            <tr>
                                <th><@spring.message "manage_consortium.contacts_contact"/></th>
                                <th><@spring.message "manage_consortium.contacts_voting_contact"/>
                                    <div class="popover-help-container">
                                        <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                                        <div id="voting-contact-help" class="popover bottom">
                                          <div class="arrow"></div>
                                          <div class="popover-content">
                                            <p><@orcid.msg 'manage_consortium.contacts_voting_contact.help'/></p>
                                          </div>
                                        </div>
                                      </div>
                                </th>
                                <th><@spring.message "manage_consortium.contacts_role"/>
                                    <div class="popover-help-container">
                                        <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                                        <div id="contact-role-help" class="popover bottom">
                                          <div class="arrow"></div>
                                          <div class="popover-content">
                                            <p><@spring.message 'manage_consortium.contacts_role.help'/></p>
                                          </div>
                                        </div>
                                      </div>
                                </th>
                                <th>&nbsp;</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr ng-repeat="contact in contacts.contactsList">
                                <td><b>{{contact.name}}</b><br>
                                    {{contact.email}}<br>
                                    <a ng-if="contact.orcid" href="{{buildOrcidUri(contact.orcid)}}"><img src="${staticCdn}/img/id-icon.svg" width="12" alt="ORCID iD icon"/> {{buildOrcidUri(contact.orcid)}}</a>
                                    <div ng-if="!contact.orcid">
                                        <@spring.message "manage_consortium.no_orcid_id"/></span> 
                                        <div class="popover-help-container">
                                            <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                                            <div id="voting-contact-help" class="popover bottom">
                                              <div class="arrow"></div>
                                              <div class="popover-content">
                                                <p><@spring.message "manage_consortium.this_contact_does_not_1"/></p>
                                                <p><@spring.message "manage_consortium.this_contact_does_not_2"/> <a href="<@orcid.rootPath '/register'/>" target="manage_consortium.this_contact_does_not_3.link"><@spring.message "manage_consortium.this_contact_does_not_3"/></a> <@spring.message "manage_consortium.this_contact_does_not_4"/> <a href="https://support.orcid.org/knowledgebase/articles/148603" target="manage_consortium.this_contact_does_not_5.link"> <@spring.message "manage_consortium.this_contact_does_not_5"/></a></p>
                                              </div>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                                <td><input type="checkbox" ng-model="contact.role.votingContact" ng-change="validateContacts()" ng-disabled="!memberDetails.allowedFullAccess || !contacts.permissionsByContactRoleId[contact.role.id].allowedEdit"></input></td>
                                <td>
								    <select class="input-md" id="contactRoles" name="contactRoles"
								     	ng-model="contact.role.roleType"
								     	ng-change="validateContacts()"
								     	ng-disabled="!memberDetails.allowedFullAccess || !contacts.permissionsByContactRoleId[contact.role.id].allowedEdit">
										<#list contactRoleTypes?keys as key>
											<option value="${key}" ng-selected="contact.role.roleType === '${key}'">${contactRoleTypes[key]}</option>
										</#list>
								    </select>
                                </td>
                                <td class="tooltip-container">
                                    <a 
                                        id="revokeAppBtn" 
                                        name="{{contact.email}}" 
                                        ng-click="confirmRevoke(contact)" 
                                        ng-show="memberDetails.allowedFullAccess 
                                            && contacts.permissionsByContactRoleId[contact.role.id].allowedEdit"
                                        class="glyphicon glyphicon-trash grey">
                                        <div class="popover popover-tooltip top">
                                            <div class="arrow"></div>
                                            <div class="popover-content">
                                                <span><@spring.message "manage_consortium.remove_contact"/></span>
                                            </div>
                                        </div>
                                    </a>
                                </td>
		    				</td>
                            </tr>
                        </tbody>
                    </table>
                    <span class="orcid-error" ng-show="contacts.errors.length > 0">
                        <div ng-repeat='error in contacts.errors' ng-bind-html="error"></div>
                    </span>
                    <!-- Buttons -->
	                <div class="row" ng-show="memberDetails.allowedFullAccess">
	                    <div class="controls bottomBuffer col-md-12 col-sm-12 col-xs-12">
	                    	<span id="ajax-loader" class="ng-cloak" ng-show="updateContactsShowLoader"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span><br>
	                        <button id="bottom-confirm-update-contacts" class="btn btn-primary" ng-click="updateContacts()" ng-disabled="updateContactsDisabled"><@orcid.msg 'manage_consortium.save_contacts'/></button>
	                        <a href="" class="cancel-right" ng-click="getContacts()"><@orcid.msg 'manage_consortium.clear_changes' /></a>
	                    </div>
	                </div>
                    <div class="bottomBuffer" ng-show="memberDetails.allowedFullAccess">
                        <h3 ng-show="memberDetails.consortiumLead"><@spring.message "manage_consortium.add_contacts_heading"/></h3>
                        <h3 ng-show="!memberDetails.consortiumLead"><@spring.message "self_serve.add_contacts_heading"/></h3>
                    	<p>
                    		<@spring.message "manage_consortium.add_contacts_search_for"/>
                    	</p>
                        <form ng-submit="search()">
                            <input type="text" placeholder="Email address" class="inline-input input-xlarge" ng-model="input.text"></input>
                            <button class="btn btn-primary" value="Search"><@orcid.msg 'search_for_delegates.btnSearch'/></button>
                        </form>
                    </div>
                    <div class="bottomBuffer" ng-show="!memberDetails.allowedFullAccess">
                        <p class="italic topBuffer"><@spring.message "manage_consortium.contacts_only_contacts_listed_above"/></p>
                    </div>
                <div id="invalid-email-alert" class="orcid-hide orcid-error"><@spring.message "Email.resetPasswordForm.invalidEmail"/></div>
                </div>
            </div>
            <div class="topBuffer" ng-show="memberDetails.consortiumLead">
                <h2><@spring.message "manage_consortium.submembers_heading"/></h2>
                <p><@spring.message "manage_consortium.submembers_text"/></p>
                <hr></hr>
            	<div ng-cloak ng-repeat="subMember in memberDetails.subMembers | orderBy : 'opportunity.accountName'">
					<span><a ng-href="{{subMember.opportunity.targetAccountId}}">{{subMember.opportunity.accountName}}</a></span>
					<span class="tooltip-container pull-right">
						<a id="revokeAppBtn" name="{{contact.email}}" ng-click="confirmRemoveSubMember(subMember)" ng-show="memberDetails.allowedFullAccess"
	                        class="glyphicon glyphicon-trash grey">
	                        <div class="popover popover-tooltip top">
	                            <div class="arrow"></div>
	                            <div class="popover-content">
	                                <span><@spring.message "manage_consortium.remove_consortium_member"/></span>
	                            </div>
	                        </div>
	                    </a>
                    </span>
					<hr></hr>
            	</div>
                <div ng-hide="memberDetails.subMembers.length"> 
					<p>This consortium does not have any members yet.</p>
					<hr></hr>
                </div>
                <div ng-show="memberDetails.allowedFullAccess">
	                <h3><@spring.message "manage_consortium.add_submember_heading"/></h3>
                    <!-- Name -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label for="new-sub-member-name"><@spring.message "manage_consortium.org_name"/></label>
                            <input id="new-sub-member-name" type="text" placeholder="<@spring.message "manage_consortium.org_name"/>" class="input-95-width" ng-change="validateSubMemberField('name')" ng-model="newSubMember.name.value" ng-model-onblur />
                            <span class="required" ng-class="isValidClass(newSubMember.name)" >*</span>
                            <span class="orcid-error" ng-show="newSubMember.name.errors.length > 0">
                                <div ng-repeat='error in newSubMember.name.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- website -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label for="new-sub-member-website"><@spring.message "manage_consortium.website"/></label>
                            <input id="new-sub-member-website" type="text" placeholder="<@spring.message "manage_consortium.website"/>" class="input-95-width" ng-model="newSubMember.website.value" ng-model-onblur ng-change="validateSubMemberField('website')" />
                            <span class="required" ng-class="isValidClass(newSubMember.website)">*</span>
                            <span class="orcid-error" ng-show="newSubMember.website.errors.length > 0">
                                <div ng-repeat='error in newSubMember.website.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- Buttons -->
	                <div class="row">
	                    <div class="controls col-md-12 col-sm-12 col-xs-12">
                            <span class="orcid-error" ng-show="newSubMember.errors.length > 0">
                                <div ng-repeat='error in newSubMember.errors' ng-bind-html="error"></div>
                            </span>
	                    	<span id="ajax-loader" class="ng-cloak" ng-show="addSubMemberShowLoader"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span><br>
	                        <button class="btn btn-primary" id="bottom-confirm-update-consortium" ng-click="validateSubMember()" ng-disabled="addSubMemberDisabled"><@orcid.msg 'manage.spanadd'/></button>
	                    </div>
	                </div> 
	            </div>
		    </div>
        </div>
    </div>
    <script type="text/ng-template" id="confirm-add-contact-modal">
	    <div class="lightbox-container">	
	       <h3><@orcid.msg 'manage_consortium.add_contacts_confirm_heading'/></h3>
	       <div ng-show="effectiveUserOrcid === contactToAdd">
	          <p class="alert alert-error"><@orcid.msg 'manage_delegation.youcantaddyourself'/></p>
	          <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
	       </div>
	       <div ng-hide="effectiveUserOrcid === contactToAdd">
	       		
	          <p>{{contactNameToAdd}} ({{contactToAdd}})</p>
	          <form ng-submit="addContact()">
	              <button class="btn btn-primary" ><@orcid.msg 'manage.spanadd'/></button>
	              <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel'/></a>
	          </form>
	       </div>
	       <div ng-show="errors.length === 0">
	           <br>
	       </div>
	    </div>
	</script>
	
	<script type="text/ng-template" id="confirm-add-contact-by-email-modal">
	    <div class="lightbox-container">
	        <h3><@orcid.msg 'manage_consortium.add_contacts_confirm_heading'/></h3>
	        <div ng-show="!emailSearchResult.found" >
	            <p class="alert alert-error"><@orcid.msg 'manage_delegation.sorrynoaccount1'/>{{input.text}}<@orcid.msg 'manage_delegation.sorrynoaccount2'/></p>
	            <p><@orcid.msg 'manage_consortium.add_contacts_no_orcid_text1'/> <@spring.message "manage_consortium.add_contacts_no_orcid_text2"/> <a href="<@orcid.rootPath '/register'/>" target="manage_consortium.this_contact_does_not_3.link"><@spring.message "manage_consortium.this_contact_does_not_3"/></a> <@spring.message "manage_consortium.this_contact_does_not_4"/> <a href="https://support.orcid.org/knowledgebase/articles/148603" target="manage_consortium.this_contact_does_not_5.link"> <@spring.message "manage_consortium.this_contact_does_not_5"/></a></p>
                <p><@spring.message "manage_consortium.add_contacts_no_orcid_text3"/></p>
                <p><@spring.message "manage_consortium.add_contacts_no_orcid_text4"/> <a href="mailto:<@spring.message "manage_consortium.support_email"/>"><@spring.message "manage_consortium.support_email"/></a></p>
	            <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
	        </div>
	        <div ng-show="emailSearchResult.found">
	            <p>{{input.text}}</p>
	            <form ng-submit="addContactByEmail(input.text)">
	                <button class="btn btn-primary" type="submit" ng-disabled="addContactDisabled"><@orcid.msg 'manage.spanadd'/></button>
	                <a href="" ng-click="closeModal()" class="cancel-option"><@orcid.msg 'freemarker.btncancel'/></a>
	            </form>
	        </div>
	    </div>
    </script>
    
    <script type="text/ng-template" id="revoke-contact-modal">
	    <div class="lightbox-container">
	        <h3><@orcid.msg 'manage_consortium.remove_contact_confirm_heading'/></h3>
	        <p> {{contactToRevoke.name}} ({{contactToRevoke.id}})</p>
	        <form ng-submit="revoke(contactToRevoke)">
	            <button class="btn btn-danger"><@orcid.msg 'manage_consortium.remove_contact_confirm_btn'/></button>
	            <a href="" ng-click="closeModal()" class="cancel-option"><@orcid.msg 'freemarker.btncancel'/></a>
	        </form>
	        <div ng-show="errors.length === 0">
	            <br>
	        </div>
	    </div>
    </script>

    <script type="text/ng-template" id="remove-sub-member-modal">
        <div class="lightbox-container">
            <h3><@orcid.msg 'manage_consortium.remove_consortium_member_confirm_heading'/></h3>
            <p> {{subMemberToRemove.opportunity.accountName}} ({{subMemberToRemove.opportunity.id}})</p>
            <form ng-submit="removeSubMember(subMemberToRemove)">
                <button class="btn btn-danger"><@orcid.msg 'manage_consortium.remove_consortium_member_confirm_btn'/></button>
                <a href="" ng-click="closeModal()" class="cancel-option"><@orcid.msg 'freemarker.btncancel'/></a>
            </form>
            <div ng-show="errors.length === 0">
                <br>
            </div>
        </div>
    </script>
    
    <script type="text/ng-template" id="add-sub-member-existing-org-modal">
	    <div class="lightbox-container">
	        <h3><@orcid.msg 'manage_consortium.add_submember_existing_org_heading'/></h3>
            <p><@orcid.msg 'manage_consortium.add_submember_existing_org_text1'/></p>
            <p class="bold">{{newSubMemberExistingOrg.member.name}}<br>
            <a href="{{newSubMemberExistingOrg.member.websiteUrl}}" target="newSubMemberExistingOrg.member.websiteUrl">{{newSubMemberExistingOrg.member.websiteUrl}}</a>
            </p>
	        
            <p><@orcid.msg 'manage_consortium.add_submember_existing_org_text2'/></p>
            <p><@orcid.msg 'manage_consortium.add_submember_existing_org_text3'/> <a href="" ng-click="closeModalReload()"><@orcid.msg 'freemarker.btncancel'/></a> <@orcid.msg 'manage_consortium.add_submember_existing_org_text4'/> <a href="mailto:<@spring.message "manage_consortium.support_email"/>"><@spring.message "manage_consortium.support_email"/></a></p>
	        <form ng-submit="addSubMember()">
	            <button class="btn btn-danger"><@orcid.msg 'freemarker.btncontinue'/></button>
	            <a href="" ng-click="closeModalReload()" class="cancel-option"><@orcid.msg 'freemarker.btncancel'/></a>
	        </form>
	        <div ng-show="errors.length === 0">
	            <br>
	        </div>
	    </div>
    </script>

</@public>
