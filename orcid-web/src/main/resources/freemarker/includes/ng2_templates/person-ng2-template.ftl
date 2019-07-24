<script type="text/ng-template" id="person-ng2-template">
    <!--OTHER NAMES-->
    <div class="workspace-section other-names" id="other-names-section">
        <div class="workspace-section-header">
            <div class="workspace-section-title">
                <div   class="edit-other-names edit-option" id="open-edit-other-names" (click)="openEditModal('modalAlsoKnownAsForm')" aria-label="<@orcid.msg 'common.edit' />">                      
                    <div class="glyphicon glyphicon-pencil" aria-label="<@orcid.msg 'common.edit' />">                 
                    </div>
                </div>
            <h3 class="workspace-section-label"><@orcid.msg 'workspace.Alsoknownas'/></h3>
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
                <div id="country-open-edit-modal" class="edit-country edit-option" (click)="openEditModal('modalCountryForm')" title="" aria-label="<@orcid.msg 'common.edit' />"> 
                    <div class="glyphicon glyphicon-pencil" aria-label="<@orcid.msg 'common.edit' />"> 
                    </div>                  
                </div>
                <h3 class="workspace-section-label"><@orcid.msg 'public_profile.labelCountry'/></h3>
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
                <div  id="open-edit-keywords" class="edit-keywords edit-option" (click)="openEditModal('modalKeywordsForm')" aria-label="<@orcid.msg 'common.edit' />">
                    <div class="glyphicon glyphicon-pencil"  aria-label="<@orcid.msg 'common.edit' />">
                    </div>
                </div>
                <h3 class="workspace-section-label"><@orcid.msg 'public_profile.labelKeywords'/></h3>
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
                <div id="open-edit-websites" class="edit-websites edit-option" (click)="openEditModal('modalWebsitesForm')"  aria-label="<@orcid.msg 'common.edit' />">
                    <div class="glyphicon glyphicon-pencil" aria-label="<@orcid.msg 'common.edit' />">
                    </div>         
                </div>
                <h3 class="workspace-section-label"><@orcid.msg 'public_profile.labelWebsites'/></h3>
            </div>
        </div>  
        <div class="workspace-section-content">
            <div *ngFor="let website of formData['websites']?.websites" class="wrap">
                <a href="{{website.url.value}}" target="website.urlName" rel="me nofollow">{{website.urlName != null? website.urlName : website.url.value}}</a>
            </div>
        </div>
    </div>
    <!-- EXT IDS  -->          
    <div *ngIf="formData['externalIdentifiers']?.externalIdentifiers" class="workspace-section extIds">
        <div class="workspace-section-header">
            <div class="workspace-section-title">                 
                <div  id="open-edit-external-identifiers" class="edit-websites edit-option" (click)="openEditModal('modalExtIdsForm')" aria-label="<@orcid.msg 'common.edit' />">
                    <div class="glyphicon glyphicon-pencil" aria-label="<@orcid.msg 'common.edit' />">
                    </div>
                </div>
                <h3 class="workspace-section-label"><@orcid.msg 'public_profile.labelOtherIDs'/></h3>
            </div>
        </div>
        <div class="workspace-section-content">
            <div *ngFor="let externalIdentifier of formData['externalIdentifiers']?.externalIdentifiers">
                <span *ngIf="!(externalIdentifier.url)">{{externalIdentifier.commonName}}: {{externalIdentifier.reference}}</span>
                <span *ngIf="externalIdentifier.url"><a href="{{externalIdentifier.url}}" target="externalIdentifier.commonName">{{externalIdentifier.commonName}}: {{externalIdentifier.reference}}</a></span>
            </div>
        </div>
    </div> 
    <#include "/includes/ng2_templates/also-known-as-form-ng2-template.ftl"> 
    <#include "/includes/ng2_templates/country-form-ng2-template.ftl"> 
    <#include "/includes/ng2_templates/external-identifiers-form-ng2-template.ftl"> 
    <#include "/includes/ng2_templates/keywords-form-ng2-template.ftl"> 
    <#include "/includes/ng2_templates/websites-form-ng2-template.ftl"> 
</script>