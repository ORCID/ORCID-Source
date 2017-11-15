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

<script type="text/ng-template" id="email-verification-sent-messsage-ng2-template">
    <div style="padding: 20px;">
        <h4><@orcid.msg 'manage.email.verificationEmail'/> {{emailPrimary}}</h4>
        <p><@orcid.msg 'workspace.check_your_email'/></p>
        <br />
        <button class="btn" (click)="close()"><@orcid.msg 'freemarker.btnclose'/></button>
    </div>
</script>