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
        <div class="col-md-9 col-sm-12 col-xs-12 manage-consortium">
            <h1 id="manage-consortium-lead"><@spring.message "manage_consortium.manage_consortium"/></h1>
            <p><@spring.message "manage_consortium.manage_consortium_text_1"/>
            	<a href="<@orcid.rootPath '/members'/>" target="_blank"><@spring.message "manage_consortium.member_list_link"/></a>
            	<@spring.message "manage_consortium.manage_consortium_text_2"/>
            	<a href="mailto:<@spring.message "manage_consortium.support_email"/>"><@spring.message "manage_consortium.support_email"/></a></p>
            <div ng-show="consortium != null" ng-cloak>
                <div class="topBuffer">
                	<h2 id="manage-consortium-lead"><@spring.message "manage_consortium.consortium_lead"/></h2>
                    <h3 class="topBuffer"><@spring.message "manage_consortium.public_display"/></h3>
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
                    <!-- Public display email -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label><@orcid.msg 'manage_consortium.email'/></label>
                            <input type="text" ng-model="consortium.email.value" class="full-width-input" />
                            <span class="orcid-error" ng-show="consortium.email.errors.length > 0">
                                <div ng-repeat='error in consortium.email.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- Description -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label><@orcid.msg 'manage_consortium.description'/></label>
                            <textarea ng-model="consortium.description.value" class="full-width-input" ></textarea>
                            <span class="orcid-error" ng-show="consortium.description.errors.length > 0">
                                <div ng-repeat='error in consortium.description.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- Community -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label><@orcid.msg 'manage_consortium.community'/></label>
                             <select id="communities" name="communities"
								    	class="input-xlarge"
								     	ng-model="consortium.community.value">
										<#list communityTypes?keys as key>
											<option value="${key}" ng-selected="contact.community.value === '${key}'">${communityTypes[key]}</option>
										</#list>
								    </select>            
                            <span class="orcid-error" ng-show="consortium.community.errors.length > 0">
                                <div ng-repeat='error in consortium.community.errors' ng-bind-html="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- Buttons -->
	                <div class="row">
	                    <div class="controls bottomBuffer col-md-12 col-sm-12 col-xs-12">
	                    	<span id="ajax-loader" class="ng-cloak" ng-show="updateConsortiumShowLoader"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span><br>
	                        <button id="bottom-confirm-update-consortium" class="btn btn-primary" ng-click="updateConsortium()" ng-disabled="updateConsortiumDisabled"><@orcid.msg 'manage_consortium.save_public_info'/></button>
	                        <a href="" class="cancel-right" ng-click="closeModalReload()"><@orcid.msg 'manage_consortium.clear_changes' /></a>
	                    </div>
	                </div> 
                </div>
                <!-- Contacts -->
                <div>
                    <h3>
                        <@spring.message "manage_consortium.contacts_heading"/>
                    </h3>
                    <p>
                        <@spring.message "manage_consortium.contacts_text"/>
                    </p>
                    <table>
                        <thead>
                            <tr>
                                <th>Name</th><th>Email</th><th>ORCID iD</th><th>Role</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr ng-repeat="contact in consortium.contactsList">
                                <td>{{contact.name}}</td>
                                <td>{{contact.email}}</td>
                                <td><a href="{{buildOrcidUri(contact.orcid)}}">{{contact.orcid}}</a></td>
                                <td>
								    <select class="input-md" id="contactRoles" name="contactRoles"
								     	ng-model="contact.role.roleType"
								     	ng-change="update(contact)">
										<#list contactRoleTypes?keys as key>
											<option value="${key}" ng-selected="contact.role.roleType === '${key}'">${contactRoleTypes[key]}</option>
										</#list>
								    </select>
                                </td>
                                <td class="tooltip-container">
                                    <a id="revokeAppBtn" name="{{contact.email}}" ng-click="confirmRevoke(contact)"
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
                    <div class="bottomBuffer">
                    	<h3>
                        	<@spring.message "manage_consortium.add_contacts_heading"/>
                    	</h3>
                    	<p>
                    		<@spring.message "manage_consortium.add_contacts_search_for"/>
                    	</p>
                        <form ng-submit="search()">
                            <input type="text" placeholder="Email address" class="inline-input input-xlarge" ng-model="input.text"></input>
                            <button class="btn btn-primary" value="Search"><@orcid.msg 'search_for_delegates.btnSearch'/></button>
                        </form>
                    </div>
                <div id="invalid-email-alert" class="orcid-hide orcid-error"><@spring.message "Email.resetPasswordForm.invalidEmail"/></div>
                </div>
            </div>
            <div class="topBuffer">
                <h2>Consortium Members</h2>
                <hr></hr>
            	<div ng-repeat="subMember in consortium.subMembers | orderBy : 'opportunity.accountName'">
					<span><a ng-href="{{membersListSrvc.getMemberPageUrl(subMember.slug)}}">{{subMember.opportunity.accountName}}</a></span>
					<span class="tooltip-container">
						<a id="revokeAppBtn" name="{{contact.email}}" ng-click="confirmRemoveSubMember(subMember)"
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
                <div ng-hide="consortium.subMembers.length"> 
					<p>This consortium does not have any members yet.</p>
					<hr></hr>
                </div>
                <h3>New consortium member</h3>
                <form>
                    <label for="new-sub-member-name">Name</label><input id="new-sub-member-name" type="text" placeholder="Name" class="input-xlarge inline-input" ng-model="newSubMember.name"></input>
                    <label for="new-sub-member-website">Website</label><input id="new-sub-member-website" type="text" placeholder="Website" class="input-xlarge inline-input" ng-model="newSubMember.website"></input>
                    <!-- Buttons -->
	                <div class="row">
	                    <div class="controls col-md-12 col-sm-12 col-xs-12">
	                    	<span id="ajax-loader" class="ng-cloak" ng-show="addSubMemberShowLoader"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span><br>
	                        <button class="btn btn-primary" id="bottom-confirm-update-consortium" ng-click="addSubMember()" ng-disabled="addSubMemberDisabled"><@orcid.msg 'manage.spanadd'/></button>
	                    </div>
	                </div> 
                </form>
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
	            <p><@orcid.msg 'manage_consortium.add_contacts_must_have_account'/></p>
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

</@public>
