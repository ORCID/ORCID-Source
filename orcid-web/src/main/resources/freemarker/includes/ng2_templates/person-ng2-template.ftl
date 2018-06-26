<script type="text/ng-template" id="person-ng2-template">
    <!--OTHER NAMES-->
    <div class="workspace-section other-names" id="other-names-section">
        <div class="workspace-section-header">
            <div class="workspace-section-title">
                <div class="edit-other-names edit-option" id="open-edit-other-names" (click)="openEditModal('modalAlsoKnownAsForm')">                      
                    <div class="glyphicon glyphicon-pencil">
                        <div class="popover popover-tooltip top"> 
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <span><@orcid.msg 'manage_bio_settings.editOtherNames' /></span>
                            </div>                
                        </div>                  
                    </div>
                </div>
            <div class="workspace-section-label"><@orcid.msg 'workspace.Alsoknownas'/></div>
            </div>                
        </div>
        <div class="workspace-section-content">
            <span *ngFor="let otherName of formData['otherNames']?.otherNames; let index = index; let first = first; let last = last;">
            {{ last?otherName.content:otherName.content + ", "}}
            </span>
        </div>
    </div>
    <!--COUNTRY-->
    <div class="workspace-section country">
        <div class="workspace-section-header">
            <div class="workspace-section-title">
                <div id="country-open-edit-modal" class="edit-country edit-option" (click)="openEditModal('modalCountryForm')" title=""> 
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
            <span *ngFor="let country of formData['addresses'].addresses">
            <span *ngIf="country != null && country?.countryName != null" >{{country.countryName}}</span>
            </span>
        </div>
    </div>
    <!--KEYWORDS-->
    <div class="workspace-section keywords">
        <div class="workspace-section-header">
            <div class="workspace-section-title">
                <div id="open-edit-keywords" class="edit-keywords edit-option" (click)="openEditModal('modalKeywordsForm')">
                    <div class="glyphicon glyphicon-pencil">
                        <div class="popover popover-tooltip top">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <span><@orcid.msg 'manage_bio_settings.editKeywords' /></span>
                            </div>                
                        </div>
                    </div>
                </div>
                <div class="workspace-section-label"><@orcid.msg 'public_profile.labelKeywords'/></div>
            </div>
            <div class="workspace-section-content">
                <span *ngFor="let keyword of formData['keywords']?.keywords; let index = index; let first = first; let last = last;">
                {{ last?keyword.content:keyword.content+ ", "}}
                </span>
            </div>
        </div>
    </div>
    <!-- WEBSITES  -->          
    <div class="workspace-section websites">
        <div class="workspace-section-header">
            <div class="workspace-section-title">
                <div id="open-edit-websites" class="edit-websites edit-option" (click)="openEditModal('modalWebsitesForm')">
                    <div class="glyphicon glyphicon-pencil">
                        <div class="popover popover-tooltip top">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <span><@orcid.msg 'manage_bio_settings.editWebsites' /></span>
                            </div>                
                        </div>
                    </div>         
                </div>
                <div class="workspace-section-label"><@orcid.msg 'public_profile.labelWebsites'/></div>
            </div>
        </div>  
        <div class="workspace-section-content">
            <div *ngFor="let website of formData['websites']?.websites" class="wrap">
                <a href="{{website.url.value}}" target="website.urlName" rel="me nofollow">{{website.urlName != null? website.urlName : website.url.value}}</a>
            </div>
        </div>
    </div>
    <#include "/includes/ng2_templates/also-known-as-form-ng2-template.ftl"> 
    <#include "/includes/ng2_templates/country-form-ng2-template.ftl"> 
    <#include "/includes/ng2_templates/keywords-form-ng2-template.ftl"> 
    <#include "/includes/ng2_templates/websites-form-ng2-template.ftl"> 
</script>