<script type="text/ng-template" id="self-service-existing-sub-member-ng2-template">        
    <div class="lightbox-container">
    	    <h3><@orcid.msg 'manage_consortium.add_submember_existing_org_heading'/></h3>
        <p><@orcid.msg 'manage_consortium.add_submember_existing_org_text1'/></p>
        <p class="bold">{{newSubMemberExistingOrg?.publicDisplayName}}<br>
        <a href="{{newSubMemberExistingOrg?.websiteUrl}}" target="newSubMemberExistingOrg.member.websiteUrl">{{newSubMemberExistingOrg?.websiteUrl}}</a>
        </p>
        
        <p><@orcid.msg 'manage_consortium.add_submember_existing_org_text2'/></p>
        <p><@orcid.msg 'manage_consortium.add_submember_existing_org_text3'/> <a href="" (click)="closeModalReload()"><@orcid.msg 'freemarker.btncancel'/></a> <@orcid.msg 'manage_consortium.add_submember_existing_org_text4'/> <a href="mailto:<@spring.message "manage_consortium.support_email"/>"><@spring.message "manage_consortium.support_email"/></a></p>
        <form (submit)="addSubMember()">
            <button class="btn btn-danger"><@orcid.msg 'freemarker.btncontinue'/></button>
            <a href="" (click)="closeModal()" class="cancel-option"><@orcid.msg 'freemarker.btncancel'/></a>
        </form>
        <div *ngIf="errors?.length === 0">
            <br>
        </div>
    </div>
</script>