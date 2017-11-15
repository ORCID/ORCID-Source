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

<script type="text/ng-template" id="email-unverified-warning-ng2-template">
    <div class="row">
        <div class="col-md-12 col-xs-12 col-sm-12">
            <h4><@orcid.msg 'orcid.frontend.workspace.your_primary_email'/></h4>
            <p><@orcid.msg 'orcid.frontend.workspace.ensure_future_access'/></p>
            <p><@orcid.msg 'orcid.frontend.workspace.ensure_future_access2'/><br /><strong>{{emailPrimary}}</strong></p>
            <p><@orcid.msg 'orcid.frontend.workspace.ensure_future_access3'/> <a target="orcid.frontend.link.url.knowledgebase" href="<@orcid.msg 'orcid.frontend.link.url.knowledgebase'/>"><@orcid.msg 'orcid.frontend.workspace.ensure_future_access4'/></a> <@orcid.msg 'orcid.frontend.workspace.ensure_future_access5'/> <a target="orcid.frontend.link.email.support" href="mailto:<@orcid.msg 'orcid.frontend.link.email.support'/>"><@orcid.msg 'orcid.frontend.link.email.support'/></a>.</p>
            <div class="topBuffer">
                <button class="btn btn-primary" id="modal-close" (click)="verifyEmail()"><@orcid.msg 'orcid.frontend.workspace.send_verification'/></button>
                <a class="cancel-option inner-row" (click)="close()"><@orcid.msg 'orcid.frontend.freemarker.btncancel'/></a>
            </div>
        </div>
    </div>
</script>