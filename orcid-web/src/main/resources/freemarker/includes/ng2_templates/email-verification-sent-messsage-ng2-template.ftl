<script type="text/ng-template" id="email-verification-sent-messsage-ng2-template">
    <div style="padding: 20px;">
        <h4><@orcid.msg 'manage.email.verificationEmail'/> {{emailPrimary}}</h4>
        <p><@orcid.msg 'workspace.check_your_email'/></p>
        <br />
        <button class="btn" (click)="close()"><@orcid.msg 'freemarker.btnclose'/></button>
    </div>
</script>