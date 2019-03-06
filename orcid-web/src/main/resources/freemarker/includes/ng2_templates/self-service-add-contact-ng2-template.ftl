<script type="text/ng-template" id="self-service-add-contact-ng2-template">        
    <div class="lightbox-container">
	    <h3><@orcid.msg 'manage_consortium.add_contacts_confirm_heading'/></h3>
        <div *ngIf="!emailSearchResult?.found" >
            <p class="alert alert-error"><@orcid.msg 'manage_delegation.sorrynoaccount1'/>{{input?.text}}<@orcid.msg 'manage_delegation.sorrynoaccount2'/></p>
            <p><@orcid.msg 'manage_consortium.add_contacts_no_orcid_text1'/> <@spring.message "manage_consortium.add_contacts_no_orcid_text2"/> <a href="{{getBaseUri()}}/register" target="manage_consortium.this_contact_does_not_3.link"><@spring.message "manage_consortium.this_contact_does_not_3"/></a> <@spring.message "manage_consortium.this_contact_does_not_4"/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006897554" target="manage_consortium.this_contact_does_not_5.link"></a></p>
            <p><@spring.message "manage_consortium.add_contacts_no_orcid_text3"/></p>
            <p><@spring.message "manage_consortium.add_contacts_no_orcid_text4"/> <a href="mailto:<@spring.message "manage_consortium.support_email"/>"><@spring.message "manage_consortium.support_email"/></a></p>
            <button class="btn btn-white-no-border" (click)="closeModal()"><@orcid.msg 'freemarker.btnclose'/></button>
        </div>
        <div *ngIf="emailSearchResult?.found">
            <p>{{input?.text}}</p>
            <form (submit)="addContact(input.text)">
                <button class="btn btn-primary" type="submit" [disabled]="addContactDisabled"><@orcid.msg 'manage.spanadd'/></button>
                <button (click)="closeModal()" class="btn btn-white-no-border cancel-option"><@orcid.msg 'freemarker.btncancel'/></button>
            </form>
        </div>
	</div>
</script>