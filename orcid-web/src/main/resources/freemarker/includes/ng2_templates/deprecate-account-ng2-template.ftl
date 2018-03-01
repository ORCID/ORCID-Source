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

<script type="text/ng-template" id="deprecate-account-ng2-template">
    <div class="editTablePadCell35 close-account-container">
        <p>${springMacroRequestContext.getMessage("deprecate_orcid.if_you_have")}</p>
        <p>${springMacroRequestContext.getMessage("deprecate_orcid.information_in")}</p>
        
        <p>${springMacroRequestContext.getMessage("deprecate_orcid.if_you_have_more")}<br />
            <a
                href="${knowledgeBaseUri}/articles/580410"
                target="deprecate_orcid.learn_more_link">${springMacroRequestContext.getMessage("deprecate_orcid.learn_more_link")}</a>
        </p>
        <div>
            <label for="emailOrId" class="">${springMacroRequestContext.getMessage("deprecate_orcid.email_or_id")}</label>
            <div class="relative">
                <input id="emailOrId" type="text" name="emailOrId" (keyup.enter)="deprecateORCID()" 
                    [(ngModel)]="deprecateProfilePojo.deprecatingOrcidOrEmail" class="input-xlarge" />
                <span class="required">*</span>
            </div>
        </div>
        <div>
            <label for="password" class="">${springMacroRequestContext.getMessage("deprecate_orcid.password")}</label>
            <div class="relative">
                <input id="password" type="password"
                    name="password"
                    [(ngModel)]="deprecateProfilePojo.deprecatingPassword" (keyup.enter)="deprecateORCID()" 
                    class="input-xlarge" /> <span class="required">*</span>
            </div>
        </div>
       <span class="orcid-error"
            *ngIf="deprecateProfilePojo?.errors?.length > 0">
            <div *ngFor='let error of deprecateProfilePojo.errors'
                [innerHTML]="error"></div>
        </span>
        <button (click)="deprecateORCID()" class="btn btn-primary">${springMacroRequestContext.getMessage("deprecate_orcid.remove_record")}</button>
    </div>
</script>