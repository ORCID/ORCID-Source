<script type="text/ng-template" id="authorize-delegate-result-ng2">
    <div *ngIf="authorizeDelegateResult.approved" class="alert alert-success">
        <strong><@orcid.msg 'admin.delegate.success' /></strong>
    </div>
    <div *ngIf="authorizeDelegateResult.failed" class="alert alert-success">
        <strong>><@orcid.msg 'admin.delegate.error.invalid_link' /></strong>
    </div>
    <div *ngIf="authorizeDelegateResult.notYou" class="alert alert-success">
        <strong><@orcid.msg 'wrong_user.Wronguser' /></strong> <a href="<@orcid.rootPath '/signout'/>"><@orcid.msg 'public-layout.sign_out' /></a> <@orcid.msg 'wrong_user.andtryagain' />
    </div>
</script>