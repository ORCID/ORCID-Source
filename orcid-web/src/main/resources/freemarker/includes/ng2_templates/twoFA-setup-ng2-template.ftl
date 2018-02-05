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

<script type="text/ng-template" id="two-fa-setup-ng2-template">  

    <div id="2FASetup">
        <div class="row">        
            <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
                <h1>${springMacroRequestContext.getMessage("2FA.enable")}</h1>
                <hr>
                <div *nfIf="showSetup2FA" >
                    <p>
                        ${springMacroRequestContext.getMessage("2FA.details")}
                        <br />
                        <a href="${knowledgeBaseUri}/articles/580410"
                            target="2FA.learn_more_link">${springMacroRequestContext.getMessage("2FA.learn_more_link")}</a>
                    </p>
                    <ul id="2FASetupSteps" class="twoFactorAuthSetup">
                        <li class="bold">${springMacroRequestContext.getMessage("2FA.setup.step1")}</li>
                        <p>${springMacroRequestContext.getMessage("2FA.setup.step1.details")}</p>
                        <li class="bold">${springMacroRequestContext.getMessage("2FA.setup.step2")}</li>
                        <p>${springMacroRequestContext.getMessage("2FA.setup.step2.details")}</p>
                        <p><span class="bold">${springMacroRequestContext.getMessage("2FA.setup.cannot.scan1")}</span> <a href="javascript:void(0);" id="getTextCode">${springMacroRequestContext.getMessage("2FA.setup.cannot.scan2")}</a> ${springMacroRequestContext.getMessage("2FA.setup.cannot.scan3")}</p>
                        <img id="2FA-QR-code" *nfIf="showQRCode" />
                        <pre *nfIf="showTextCode">{{textCodeFor2FA}}</pre>
                        <li class="bold">${springMacroRequestContext.getMessage("2FA.setup.step3")}</li>
                        <p>${springMacroRequestContext.getMessage("2FA.setup.step3.details")}</p>
                    </ul>
                    <form>
                        <input type="text" [(ngModel)]="twoFactorAuthRegistration.verificationCode" />
                        <div id="invalidCodeError" class="orcid-error" *nfIf="showInvalidCodeError" ">${springMacroRequestContext.getMessage("2FA.setup.invalidCode")}</div>
                        <hr>
                        <button id="sendVerificationCode" (click)="sendVerificationCode()" class="btn btn-primary">${springMacroRequestContext.getMessage("2FA.continue")}</button>
                        <a class="leftBuffer" id="cancel2FASetup" (click)="cancel2FASetup()" href="javascript:void(0);">${springMacroRequestContext.getMessage("2FA.cancel")}</a>
                    </form>
                 </div>
                 <div *nfIf="show2FARecoveryCodes" >
                    <h2 id="saveRecoveryCodesHeading">${springMacroRequestContext.getMessage("2FA.recoveryCodes.heading")}</h2>
                    <p>
                        ${springMacroRequestContext.getMessage("2FA.recoveryCodes.details")}
                        <br />
                        <a href="${knowledgeBaseUri}/articles/1190068" target="2FA.learn_more_link">${springMacroRequestContext.getMessage("2FA.learn_more_link")}</a>
                    </p>
                    <div id="recoveryCodes">
                        <span ng-repeat="recoveryCode in recoveryCodes">{{recoveryCode}}<br></span>
                    </div>
                    <div class="form-group">
                        <button (click)="downloadRecoveryCodes()" class="btn btn-white"><span class="glyphicon glyphicon-download-alt"></span> ${springMacroRequestContext.getMessage("2FA.recoveryCodes.download")}</button>
                        <button (click)="copyRecoveryCodes()" class="btn btn-white"><span class="glyphicon glyphicon-file"></span> ${springMacroRequestContext.getMessage("2FA.recoveryCodes.copy")}</button>
                    </div>
                    <div class="form-group">
                        <p><span>${springMacroRequestContext.getMessage("2FA.recoveryCodes.warning.heading")}</span>&nbsp;${springMacroRequestContext.getMessage("2FA.recoveryCodes.warning.details")}</p>
                        <button (click)="done()" class="btn btn-primary">${springMacroRequestContext.getMessage("2FA.recoveryCodes.done")}</button>
                    </div>
                 </div>
            </div>
        </div>
    </div>
</script>