<script type="text/ng-template" id="all-consortium-contacts-ng2-template">
    <div class="col-md-9 col-sm-12 col-xs-12 self-service">
        <h1 id="self-service-lead"><@spring.message "manage_consortium.all_consortium_contacts_heading"/></h1>
        <div>
            <a href="<@orcid.rootPath '/self-service'/>"><@spring.message "manage_consortium.back_to_self_service_home"/></a>
        </div>
        <div>
            <a href="all-consortium-contacts-download">
                <button class="btn btn-primary" id="export-all-contacts"><@orcid.msg 'manage_consortium.all_consortium_contacts_export' /></button>
            </a>
        </div>
        <div>
            <table>
                <thead>
                    <tr>
                        <th><@spring.message "manage_consortium.contacts_member_name"/></th>
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
                    <tr *ngFor="let contact of contacts">
                        <td>{{contact.member.publicDisplayName}}</td>
                        <td><b>{{contact.name}}</b><br>
                            {{contact.email}}<br>
                            <a *ngIf="contact.orcid" href="{{buildOrcidUri(contact.orcid)}}"><img src="{{assetsPath}}/img/id-icon.svg" width="12" alt="ORCID iD icon"/> {{buildOrcidUri(contact.orcid)}}</a>
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
                        <td><input type="checkbox" [(ngModel)]="contact.role.votingContact" disabled/></td>
                        <td>
                            <select class="input-md" id="contactRoles" name="contactRoles"
                                [(ngModel)]="contact.role.roleType"
                                disabled>
                                <#list contactRoleTypes?keys as key>
                                    <option value="${key}">${contactRoleTypes[key]}</option>
                                </#list>
                            </select>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</script>