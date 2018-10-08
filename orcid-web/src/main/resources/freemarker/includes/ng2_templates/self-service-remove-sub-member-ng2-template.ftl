<script type="text/ng-template" id="self-service-remove-sub-member-ng2-template">        
    <div class="lightbox-container">
        <h3><@orcid.msg 'manage_consortium.remove_consortium_member_confirm_heading'/></h3>
        <p><strong>{{subMember?.opportunity.accountPublicDisplayName}}</strong> <@orcid.msg 'manage_consortium.remove_consortium_member_confirm_text1'/></p>
        <p><@orcid.msg 'manage_consortium.remove_consortium_member_confirm_text2'/></p>
        <form (submit)="removeSubMember(subMember)">
            <button class="btn btn-danger"><@orcid.msg 'manage_consortium.remove_consortium_member_confirm_btn'/></button>
            <button (click)="closeModal()" class="btn btn-white-no-border cancel-option"><@orcid.msg 'freemarker.btncancel'/></button>
        </form>
        <div *ngIf="errors?.length === 0">
            <br>
        </div>
    </div>
</script>