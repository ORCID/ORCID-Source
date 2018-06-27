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

<script type="text/ng-template" id="deprecate-account-modal-ng2-template">

    <@orcid.checkFeatureStatus featureName='HTTPS_IDS'>  

    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12 bottomBuffer">       
            <h2><@orcid.msg 'deprecate_orcid_modal.heading' /></h2>     
            <span class="orcid-error italic"><@orcid.msg 'deprecate_orcid_modal.warning_1' /><br /><strong class="italic"><@orcid.msg 'deprecate_orcid_modal.warning_2' /></strong></span>
            <strong><@orcid.msg 'deprecate_orcid_modal.remove_this' /></strong><br />
            <a href="${baseUri}/{{deprecateProfilePojo.deprecatingOrcid}}" target="deprecatingOrcid">${baseUri}/<span [innerHtml]="deprecateProfilePojo.deprecatingOrcid"></span></a><br />
            <span><@orcid.msg 'deprecate_orcid_modal.name_label' /></span><span [innerHtml]="deprecateProfilePojo.deprecatingAccountName"></span><br />
            <span><@orcid.msg 'deprecate_orcid_modal.emails_label' /></span><ul class="inline comma"><li *ngFor="let email of deprecateProfilePojo.deprecatingEmails" [innerHtml]="email"></li></ul><br /><br />
            <strong><@orcid.msg 'deprecate_orcid_modal.keep_this' /></strong><br />
            <a href="${baseUri}/{{deprecateProfilePojo.primaryOrcid}}" target="primaryOrcid">${baseUri}/<span [innerHtml]="deprecateProfilePojo.primaryOrcid"></a></span><br />
            <span><@orcid.msg 'deprecate_orcid_modal.name_label' /></span><span [innerHtml]="deprecateProfilePojo.primaryAccountName"></span><br />
            <span><@orcid.msg 'deprecate_orcid_modal.emails_label' /></span><ul class="inline comma"><li *ngFor="let email of deprecateProfilePojo.primaryEmails" [innerHtml]="email" ></li></ul><br /><br />
        </div>          
    </div>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="pull-left">
                <button id="bottom-submit" class="btn btn-primary" (click)="submitModal()"><@orcid.msg 'deprecate_orcid_modal.confirm'/></button><a href="" class="cancel-right" (click)="closeModal()"><@orcid.msg 'deprecate_orcid_modal.cancel' /></a>
            </div>
        </div>
    </div>

    </@orcid.checkFeatureStatus>   

    <@orcid.checkFeatureStatus featureName='HTTPS_IDS' enabled=false>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12 bottomBuffer">       
            <h2><@orcid.msg 'deprecate_orcid_modal.heading' /></h2> 
            <span class="orcid-error italic"><@orcid.msg 'deprecate_orcid_modal.warning_1' /><br /><strong class="italic"><@orcid.msg 'deprecate_orcid_modal.warning_2' /></strong></span>
            <strong><@orcid.msg 'deprecate_orcid_modal.remove_this' /></strong><br />
            ${baseUriHttp}/<span [innerHtml]="deprecateProfilePojo.deprecatingOrcid"></span><br />
            <span><@orcid.msg 'deprecate_orcid_modal.name_label' /></span><span [innerHtml]="deprecateProfilePojo.deprecatingAccountName"></span><br />
            <span><@orcid.msg 'deprecate_orcid_modal.emails_label' /></span><ul class="inline comma"><li *ngFor="let email of deprecateProfilePojo.deprecatingEmails" [innerHtml]="email"></li></ul><br /><br />
            <strong><@orcid.msg 'deprecate_orcid_modal.keep_this' /></strong><br />
            ${baseUriHttp}/<span [innerHtml]="deprecateProfilePojo.primaryOrcid"></span><br />
            <span><@orcid.msg 'deprecate_orcid_modal.name_label' /></span><span [innerHtml]="deprecateProfilePojo.primaryAccountName"></span><br />
            <span><@orcid.msg 'deprecate_orcid_modal.emails_label' /></span><ul class="inline comma"><li *ngFor="let email of deprecateProfilePojo.primaryEmails" [innerHtml]="email" ></li></ul><br /><br />
        </div>          
    </div>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="pull-left">
                <button id="bottom-submit" class="btn btn-primary" (click)="submitModal()"><@orcid.msg 'deprecate_orcid_modal.confirm'/></button><a href="" class="cancel-right" (click)="closeModal()"><@orcid.msg 'deprecate_orcid_modal.cancel' /></a>
            </div>
        </div>
    </div>
    </@orcid.checkFeatureStatus> 
</script>

<modalngcomponent elementHeight="645" elementId="deprecateAccountModal" elementWidth="700">
    <deprecate-account-modal-ng2></deprecate-account-modal-ng2>
</modalngcomponent><!-- Ng2 component -->