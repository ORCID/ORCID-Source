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

<script type="text/ng-template" id="affiliation-delete-ng2-template">
    <div class="row">
        <div class="col-md-12 col-xs-12 col-sm-12">
            <h3><@orcid.msg 'manage.deleteAffiliation.pleaseConfirm' /></h3>
            <p>{{deleteAffiliationObj.affiliationName.value}}</p>       
            <div id="confirm_delete_affiliation" class="btn btn-danger" (click)="deleteAffiliation()">
                <@orcid.msg 'freemarker.btnDelete' />
            </div>
            <a href="" (click)="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
        </div>
    </div>
</script>