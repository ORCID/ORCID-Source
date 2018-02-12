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

<script type="text/ng-template" id="emails-ng2-template">
    <div class="workspace-section">
        <div class="workspace-section-header">
            <div class="workspace-section-title">                  
                <div class="edit-websites edit-option" (click)="openEditModal()">
                    <div class="glyphicon glyphicon-pencil">
                        <div class="popover popover-tooltip top">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <span><@orcid.msg 'manage_bio_settings.editEmails' /></span>
                            </div>                
                        </div>
                    </div>                  
                </div>
                <div class="workspace-section-label"><@orcid.msg 'manage.emails'/></div>
            </div>
        </div> 
        <div class="workspace-section-content">
            <div *ngFor="let email of formData.emails | orderBy:'value'" class="mobile-box emails-box">
                <span >{{email.value}}</span>
            </div>
        </div>
    </div>
</script>