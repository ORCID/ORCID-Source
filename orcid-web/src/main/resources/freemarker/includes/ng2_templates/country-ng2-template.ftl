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

<script type="text/ng-template" id="country-ng2-template">
    <div class="workspace-section country">
        <div class="workspace-section-header">
            <div class="workspace-section-title">
                <div id="country-open-edit-modal" class="edit-country edit-option" (click)="openEditModal()" title=""> 
                    <div class="glyphicon glyphicon-pencil"> 
                        <div class="popover popover-tooltip top"> 
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <span><@orcid.msg 'manage_bio_settings.editCountry' /></span>
                            </div>                
                        </div>
                    </div>                  
                </div>
                <div class="workspace-section-label"><@orcid.msg 'public_profile.labelCountry'/></div>
            </div>
        </div>
        <div class="workspace-section-content">
            <span *ngFor="let country of formDataAddresses">
            <span *ngIf="country != null && country.countryName != null" >{{country.countryName}}</span>
            </span>
        </div>
    </div>
</script>