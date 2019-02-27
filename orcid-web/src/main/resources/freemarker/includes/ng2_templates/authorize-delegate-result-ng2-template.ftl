<script type="text/ng-template" id="authorize-delegate-result-ng2-template">
    <div *ngIf="delegate" class="alert alert-success">
        <strong><@orcid.msg 'admin.delegate.success' />{{delegate}}</strong>
    </div>
    <div *ngIf="invalidLink" class="alert alert-success">
        <strong><@orcid.msg 'admin.delegate.error.invalid_link' /></strong>
    </div>
    <div *ngIf="wrongLink" class="alert alert-success">
        <strong><@orcid.msg 'wrong_user.Wronguser' /></strong><a href="{{getBaseUri()}}/signout"><@orcid.msg 'public-layout.sign_out' /></a> <@orcid.msg 'wrong_user.andtryagain' />
    </div>
</script>