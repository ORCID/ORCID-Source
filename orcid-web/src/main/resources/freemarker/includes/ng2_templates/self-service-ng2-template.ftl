<script type="text/ng-template" id="self-service-ng2-template">
    <div class="col-md-9 col-sm-12 col-xs-12 self-service">
        <h1 id="self-service-lead" *ngIf="memberDetails?.consortiumLead"><@spring.message "manage_consortium.manage_consortium"/></h1>
        <h1 id="self-service-lead" *ngIf="!memberDetails?.consortiumLead"><@spring.message "manage_consortium.manage_member"/></h1>
        <p><@spring.message "manage_consortium.manage_consortium_text_1"/>
            <@spring.message "manage_consortium.manage_consortium_text_2"/>
            <a href="mailto:<@spring.message "manage_consortium.support_email"/>"><@spring.message "manage_consortium.support_email"/></a></p>
        <div *ngIf="memberDetails != null">
            <div class="topBuffer">
                <h3 class="topBuffer" *ngIf="memberDetails?.consortiumLead"><@spring.message "manage_consortium.public_display"/></h3>
                <h3 class="topBuffer" *ngIf="!memberDetails?.consortiumLead"><@spring.message "self_serve.public_display_heading"/></h3>
                <p><@spring.message "self_serve.public_display_text"/> <a href="<@orcid.rootPath '/members'/>" target="manage_consortium.member_list_link"><@spring.message "manage_consortium.member_list_link"/></a></p>
                <!-- Name -->
                <div class="row">
                    <div class="col-md-9 col-sm-12 col-xs-12">
                        <label><@orcid.msg 'manage_consortium.org_name'/></label>
                        <input type="text" [(ngModel)]="memberDetails.name.value" (blur)="validateMemberDetailsField('name')" class="input-95-width" />
                        <span class="required" [ngClass]="isValidClass(memberDetails.name)" >*</span>
                        <span class="orcid-error" *ngIf="newSubMember?.name?.errors?.length > 0">
                            <div *ngFor='let error of newSubMember.name.errors' [innerHtml]="error"></div>
                        </span>
                        <span class="orcid-error" *ngIf="memberDetails?.name?.errors?.length > 0">
                            <div *ngFor='let error of memberDetails.name.errors' [innerHtml]="error"></div>
                        </span>
                    </div>
                </div>
                <!-- website -->
                <div class="row">
                    <div class="col-md-9 col-sm-12 col-xs-12">
                        <label><@orcid.msg 'manage_consortium.website'/></label>
                        <input type="text" [(ngModel)]="memberDetails.website.value" (blur)="validateMemberDetailsField('website')" class="input-95-width" />
                        <span class="orcid-error" *ngIf="memberDetails?.website?.errors?.length > 0">
                            <div *ngFor='let error of memberDetails.website.errors' [innerHtml]="error"></div>
                        </span>
                    </div>
                </div>
                <!-- Public display email -->
                <div class="row">
                    <div class="col-md-9 col-sm-12 col-xs-12">
                        <label><@orcid.msg 'manage_consortium.email'/></label>
                        <input type="text" [(ngModel)]="memberDetails.email.value" (blur)="validateMemberDetailsField('email')" class="input-95-width" />
                        <span class="orcid-error" *ngIf="memberDetails?.email?.errors?.length > 0">
                            <div *ngFor='let error of memberDetails.email.errors' [innerHtml]="error"></div>
                        </span>
                    </div>
                </div>
                <!-- Description -->
                <div class="row">
                    <div class="col-md-9 col-sm-12 col-xs-12">
                        <label><@orcid.msg 'manage_consortium.description'/></label>
                        <textarea [(ngModel)]="memberDetails.description.value" (blur)="validateMemberDetailsField('description')" class="input-95-width" ></textarea>
                        <span class="orcid-error" *ngIf="memberDetails?.description?.errors?.length > 0">
                            <div *ngFor='let error of memberDetails.description.errors' [innerHtml]="error"></div>
                        </span>
                    </div>
                </div>
                <!-- Community -->
                <div class="row">
                    <div class="col-md-9 col-sm-12 col-xs-12">
                        <label><@orcid.msg 'manage_consortium.community'/></label>
                         <select id="communities" name="communities"
                                    class="input-xlarge"
                                    [(ngModel)]="memberDetails.community.value" (blur)="validateMemberDetailsField('community')">
                                    <#list communityTypes?keys as key>
                                        <option value="${key}">${communityTypes[key]}</option>
                                    </#list>
                                </select>            
                        <span class="orcid-error" *ngIf="memberDetails?.community?.errors?.length > 0">
                            <div *ngFor='let error of memberDetails.community.errors' [innerHtml]="error"></div>
                        </span>
                    </div>
                </div>
                <div class="row" style="margin-top: 10px;">
                    <div class="col-md-9 col-sm-12 col-xs-12">
                    
                    <label><@orcid.msg 'manage_consortium.logo'/></label>
                    <img *ngIf="memberDetails.logo" src="{{memberDetails.logo}}" width="100" alt="Member logo"/>
                    <p *ngIf="!memberDetails.logo"><@orcid.msg 'manage_consortium.no_logo'/></p>
                    <p><@orcid.msg 'manage_consortium.please_send'/></p>
                    </div>
                </div>
                <!-- Buttons -->
                <div class="row">
                    <div class="controls bottomBuffer col-md-12 col-sm-12 col-xs-12">
                        <span id="ajax-loader" *ngIf="updateMemberDetailsShowLoader"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span><br>
                        <button id="bottom-confirm-update-consortium" class="btn btn-primary" (click)="validateMemberDetails()" [disabled]="updateMemberDetailsDisabled"><@orcid.msg 'manage_consortium.save_public_info'/></button>
                        <button class="btn btn-white-no-border cancel-right" (click)="getMemberDetails()"><@orcid.msg 'manage_consortium.clear_changes' /></button>
                    </div>
                </div>
            </div>
            <!-- Org IDS -->
            <div *ngIf="orgIdsFeatureEnabled">
                <h3><@spring.message "manage_consortium.org_ids_heading"/></h3>
                <div>
                    <table>
                        <tr>
                            <th><@spring.message "manage_consortium.org_id_value"/></th>
                            <th><@spring.message "manage_consortium.org_id_type"/></th>
                        </tr>
                        <tr *ngFor="let orgId of orgIds">
                            <td>{{orgId.orgIdValue}}</td>
                            <td>{{orgId.orgIdType}}</td>
                            <td class="tooltip-container">
                                <a
                                    *ngIf="memberDetails.allowedFullAccess"
                                    (click)="removeOrgId(orgId)" 
                                    class="glyphicon glyphicon-trash grey">
                                    <div class="popover popover-tooltip top">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <span><@spring.message "manage_consortium.remove_org_id"/></span>
                                        </div>
                                    </div>
                                </a>
                            </td>
                        </tr>
                    </table>
                </div>
                <div *ngIf="memberDetails.allowedFullAccess">
                    <h3><@spring.message "manage_consortium.add_org_ids_heading"/></h3>
                    <form (submit)="searchOrgIds()">
                         <input type="text" name="search" placeholder="Org ID / Org name" class="inline-input input-xlarge" [(ngModel)]="orgIdInput.text"/>
                         <button class="btn btn-primary" value="Search"><@orcid.msg 'search_for_delegates.btnSearch'/></button>
                    </form>
                    <div *ngIf="orgIdSearchResults?.length > 0">
                        <table>
                            <tr><th><@spring.message "manage_consortium.org_name"/></th><th><@spring.message "manage_consortium.org_id_value"/></th><th><@spring.message "manage_consortium.org_id_type"/></th></tr>
                            <tr *ngFor="let org of orgIdSearchResults">
                                <td>{{org.value}}</td><td>{{org.sourceId}}</td><td>{{org.sourceType}}</td><td (click)="addOrgId(org)"><button class="btn btn-primary" value="Add"><@spring.message "manage_consortium.add_org_ids_add"/></button></td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
            <!-- Contacts -->
            <div>
                <h3 *ngIf="memberDetails.consortiumLead"><@spring.message "manage_consortium.contacts_heading"/></h3>
                <h3 *ngIf="!memberDetails.consortiumLead"><@spring.message "self_serve.contacts_heading"/></h3>
                <p>
                    <@spring.message "manage_consortium.contacts_text"/>
                </p>
                <table>
                    <thead>
                        <tr>
                            <th><@spring.message "manage_consortium.contacts_contact"/></th>
                            <th><@spring.message "manage_consortium.contacts_voting_contact"/>
                                <div class="popover-help-container">
                                    <i class="glyphicon glyphicon-question-sign"></i>
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
                                    <i class="glyphicon glyphicon-question-sign"></i>
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
                        <tr *ngFor="let contact of contacts?.contactsList">
                            <td><b>{{contact.name}}</b><br>
                                {{contact.email}}<br>
                                <a *ngIf="contact.orcid" href="{{buildOrcidUri(contact.orcid)}}"><img src="${staticCdn}/img/id-icon.svg" width="12" alt="ORCID iD icon"/> {{buildOrcidUri(contact.orcid)}}</a>
                                <div *ngIf="!contact.orcid">
                                    <span><@spring.message "manage_consortium.no_orcid_id"/></span> 
                                    <div class="popover-help-container">
                                        <i class="glyphicon glyphicon-question-sign"></i>
                                        <div id="voting-contact-help" class="popover bottom">
                                            <div class="arrow"></div>
                                            <div class="popover-content">
                                                <p><@spring.message "manage_consortium.this_contact_does_not_1"/></p>
                                                <p><@spring.message "manage_consortium.this_contact_does_not_2"/> <a href="<@orcid.rootPath '/register'/>" target="manage_consortium.this_contact_does_not_3.link"><@spring.message "manage_consortium.this_contact_does_not_3"/></a> <@spring.message "manage_consortium.this_contact_does_not_4"/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006897554" target="manage_consortium.this_contact_does_not_5.link"> <@spring.message "manage_consortium.this_contact_does_not_5"/></a></p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </td>
                            <td><input type="checkbox" [(ngModel)]="contact.role.votingContact" (ngModelChange)="validateContacts()" [disabled]="!memberDetails.allowedFullAccess || !contacts.permissionsByContactRoleId[contact.role.id].allowedEdit"/></td>
                            <td>
                                <select class="input-md" id="contactRoles" name="contactRoles"
                                    [(ngModel)]="contact.role.roleType"
                                    (ngModelChange)="validateContacts()"
                                    [disabled]="!memberDetails.allowedFullAccess || !contacts.permissionsByContactRoleId[contact.role.id].allowedEdit">
                                    <#list contactRoleTypes?keys as key>
                                        <option value="${key}">${contactRoleTypes[key]}</option>
                                    </#list>
                                </select>
                            </td>
                            <td class="tooltip-container">
                                <a 
                                    id="removeContactBtn"
                                    name="{{contact.email}}" 
                                    (click)="confirmRemoveContact(contact)" 
                                    *ngIf="memberDetails.allowedFullAccess 
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
                        </tr>
                    </tbody>
                </table>
                <span class="orcid-error" *ngIf="contacts?.errors?.length > 0">
                    <div *ngFor='let error of contacts?.errors' [innerHtml]="error"></div>
                </span>
                <!-- Buttons -->
                <div class="row" *ngIf="memberDetails.allowedFullAccess">
                    <div class="controls bottomBuffer col-md-12 col-sm-12 col-xs-12">
                            <span id="ajax-loader" *ngIf="updateContactsShowLoader"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span><br>
                        <button id="bottom-confirm-update-contacts" class="btn btn-primary" (click)="updateContacts()" [disabled]="updateContactsDisabled"><@orcid.msg 'manage_consortium.save_contacts'/></button>
                        <button class="btn btn-white-no-border cancel-right" (click)="getContacts()"><@orcid.msg 'manage_consortium.clear_changes' /></button>
                    </div>
                </div>
                <div class="bottomBuffer" *ngIf="memberDetails.allowedFullAccess">
                    <h3 *ngIf="memberDetails.consortiumLead"><@spring.message "manage_consortium.add_contacts_heading"/></h3>
                    <h3 *ngIf="!memberDetails.consortiumLead"><@spring.message "self_serve.add_contacts_heading"/></h3>
                        <p>
                            <@spring.message "manage_consortium.add_contacts_search_for"/>
                        </p>
                     <form (submit)="search()">
                         <input type="text" name="search" placeholder="Email address" class="inline-input input-xlarge" [(ngModel)]="input.text"/>
                         <button class="btn btn-primary" value="Search"><@orcid.msg 'search_for_delegates.btnSearch'/></button>
                    </form>
                </div>
                <div class="bottomBuffer" *ngIf="!memberDetails.allowedFullAccess">
                    <p class="italic topBuffer"><@spring.message "manage_consortium.contacts_only_contacts_listed_above"/></p>
                </div>
                <div id="invalid-email-alert" class="orcid-hide orcid-error"><@spring.message "Email.resetPasswordForm.invalidEmail"/></div>
            </div>
            <!-- Consortium members-->
            <div class="topBuffer" *ngIf="memberDetails.consortiumLead">
                <h2><@spring.message "manage_consortium.submembers_heading"/></h2>
                <p><@spring.message "manage_consortium.submembers_text"/></p>
                <hr>
                <div *ngFor="let subMember of memberDetails.subMembers">
                    <span><a [ngClass]="{'disabled': isPendingAddition(subMember)}" [href]="subMember.opportunity.targetAccountId">{{subMember.opportunity.accountPublicDisplayName}}</a></span>
                    <!-- Pending addition -->
                    <span class="tooltip-container pull-right pending-addition" *ngIf="isPendingAddition(subMember)"><@orcid.msg 'manage_consortium.add_submember_pending_addition'/>
                        <a id="cancelAddSubmember" name="{{subMember.opportunity.accountPublicDisplayName}}" (click)="cancelSubMemberAddition(subMember)" class="glyphicon glyphicon-remove-circle">
                            <div class="popover popover-tooltip top">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <span><@orcid.msg "manage_consortium.add_submember_pending_addition_cancel"/></span>
                                </div>
                            </div>
                        </a>
                    </span>
                    <!-- Pending removal -->
                    <span class="tooltip-container pull-right pending-removal" *ngIf="isPendingRemoval(subMember)"><@orcid.msg 'manage_consortium.add_submember_pending_removal'/> 
                        <a id="cancelRemoveSubmember" name="{{subMember.opportunity.accountPublicDisplayName}}" (click)="cancelSubMemberRemoval(subMember)" class="glyphicon glyphicon-remove-circle">
                            <div class="popover popover-tooltip top">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <span><@orcid.msg "manage_consortium.add_submember_pending_removal_cancel"/></span>
                                </div>
                            </div>
                        </a>
                    </span>
                    <!-- Request removal -->
                    <span class="tooltip-container pull-right" *ngIf="canRemoveSubMember(subMember)">
                        <a id="revokeAppBtn" name="{{subMember.opportunity.accountPublicDisplayName}}" (click)="confirmRemoveSubMember(subMember)"
                            class="glyphicon glyphicon-trash grey">
                            <div class="popover popover-tooltip top">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <span><@spring.message "manage_consortium.remove_consortium_member"/></span>
                                </div>
                            </div>
                        </a>
                    </span>
                    <hr>
                </div>
                <div *ngIf="memberDetails?.subMembers?.length">
                    <h3><@spring.message "manage_consortium.all_consortium_contacts_heading"/></h3>
                    <a [href]="memberDetails.accountId + '/all-consortium-contacts'"><@spring.message "manage_consortium.all_consortium_contacts_link"/></a>
                </div>
                <div *ngIf="!memberDetails?.subMembers?.length"> 
                    <p>This consortium does not have any members yet.</p>
                    <hr>
                </div>
                <!-- Add consortium member-->
                <div *ngIf="memberDetails.allowedFullAccess">
                    <h3><@spring.message "manage_consortium.add_submember_heading"/></h3>
                    <p><@orcid.msg "manage_consortium.add_submember_text"/></p>
                    <!-- Name -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label for="new-sub-member-name"><@spring.message "manage_consortium.org_name"/></label>
                            <input id="new-sub-member-name" type="text" placeholder="<@spring.message "manage_consortium.org_name"/>" class="input-95-width" [(ngModel)]="newSubMember.name.value" (blur)="validateSubMemberField('name')" />
                            <span class="required" [ngClass]="isValidClass(newSubMember.name)" >*</span>
                            <span class="orcid-error" *ngIf="newSubMember?.name?.errors?.length > 0">
                                <div *ngFor='let error of newSubMember.name.errors' [innerHtml]="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- website -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label for="new-sub-member-website"><@spring.message "manage_consortium.website"/></label>
                            <input id="new-sub-member-website" type="text" placeholder="<@spring.message "manage_consortium.website"/>" class="input-95-width" [(ngModel)]="newSubMember.website.value" (blur)="validateSubMemberField('website')" />
                            <span class="required" [ngClass]="isValidClass(newSubMember.website)">*</span>
                            <span class="orcid-error" *ngIf="newSubMember?.website?.errors?.length > 0">
                                <div *ngFor='let error of newSubMember.website.errors' [innerHtml]="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- initial contact -->
                    <div class="row">
                        <div class="col-md-9 col-sm-12 col-xs-12">
                            <label for="new-sub-member-initial-contact"><@spring.message "manage_consortium.initial_contact_heading"/></label>
                            <div><@spring.message "manage_consortium.initial_contact_description"/></div>
                            <input id="initial-contact-first-name" type="text" placeholder="<@spring.message "manage_consortium.initial_contact_first_name"/>" class="input-95-width" [(ngModel)]="newSubMember.initialContactFirstName.value" (blur)="validateSubMemberField('initial-contact-first-name')" />
                            <span class="required" [ngClass]="isValidClass(newSubMember.initialContactFirstName)">*</span>
                            <span class="orcid-error" *ngIf="newSubMember?.initialContactFirstName?.errors?.length > 0">
                                <div *ngFor='let error of newSubMember.initialContactFirstName.errors' [innerHtml]="error"></div>
                            </span>
                            <input id="initial-contact-last-name" type="text" placeholder="<@spring.message "manage_consortium.initial_contact_last_name"/>" class="input-95-width" [(ngModel)]="newSubMember.initialContactLastName.value" (blur)="validateSubMemberField('initial-contact-last-name')" />
                            <span class="required" [ngClass]="isValidClass(newSubMember.initialContactLastName)">*</span>
                            <span class="orcid-error" *ngIf="newSubMember.initialContactLastName.errors.length > 0">
                                <div *ngFor='let error of newSubMember.initialContactLastName.errors' [innerHtml]="error"></div>
                            </span>
                            <input id="initial-contact-email" type="text" placeholder="<@spring.message "manage_consortium.initial_contact_email"/>" class="input-95-width" [(ngModel)]="newSubMember.initialContactEmail.value" (blur)="validateSubMemberField('initial-contact-email')" />
                            <span class="required" [ngClass]="isValidClass(newSubMember.initialContactEmail)">*</span>
                            <span class="orcid-error" *ngIf="newSubMember?.initialContactEmail?.errors?.length > 0">
                                <div *ngFor='let error of newSubMember.initialContactEmail.errors' [innerHtml]="error"></div>
                            </span>
                        </div>
                    </div>
                    <!-- Buttons -->
                    <div class="row">
                        <div class="controls col-md-12 col-sm-12 col-xs-12">
                            <span id="ajax-loader" *ngIf="addSubMemberShowLoader"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span><br>
                            <button class="btn btn-primary" id="bottom-confirm-update-consortium" (click)="validateSubMember()" [disabled]="addSubMemberDisabled"><@orcid.msg 'manage.spanadd'/></button>
                            <button class="btn btn-white-no-border cancel-right" (click)="addSubMemberClear()"><@orcid.msg 'manual_work_form_contents.btnclear' /></button>
                            <span class="orcid-error" *ngIf="errorSubMemberExists">
                                <div><@orcid.msg 'manage_consortium.add_submember_member_exists'/></div>
                            </span>
                            <span class="orcid-error" *ngIf="errorAddingSubMember">
                                <div><@orcid.msg 'manage_consortium.add_submember_error'/></div>
                            </span>
                        </div>
                    </div> 
                </div>
                </div>
        </div>
    </div>
</script>