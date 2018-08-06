<script type="text/ng-template" id="two-fa-setup-ng2-template">  

    <div id="2FASetup">
        <div class="row">        
            <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
                <h1>${springMacroRequestContext.getMessage("2FA.enable")}</h1>
                <hr>
                <div *ngIf="showSetup2FA" >
                    <p>
                        ${springMacroRequestContext.getMessage("2FA.setup.details")}
                        <br />
                        <a href="${knowledgeBaseUri}/articles/1190068"
                            target="2FA.setup.see.knowledgebase">${springMacroRequestContext.getMessage("2FA.setup.see.knowledgebase")}</a>
                    </p>
                    <ul id="2FASetupSteps" class="twoFactorAuthSetup">
                        <li class="bold">${springMacroRequestContext.getMessage("2FA.setup.step1")}</li>
                        <p>${springMacroRequestContext.getMessage("2FA.setup.step1.details")}</p>
                        <p>${springMacroRequestContext.getMessage("2FA.setup.step1.details.part_2")}<a href="https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2" target="2fa-otp">${springMacroRequestContext.getMessage("2FA.setup.step1.details.googleAuthenticator")}</a>${springMacroRequestContext.getMessage("2FA.setup.step1.details.list_separator")}<a href="https://freeotp.github.io" target="2fa-otp">${springMacroRequestContext.getMessage("2FA.setup.step1.details.freeOTP")}</a>${springMacroRequestContext.getMessage("2FA.setup.step1.details.list_separator")}${springMacroRequestContext.getMessage("2FA.setup.step1.details.or")}<a href="https://authy.com" target="2fa-otp">${springMacroRequestContext.getMessage("2FA.setup.step1.details.authy")}</a>${springMacroRequestContext.getMessage("2FA.setup.step1.details.end")}</p>
                        <p><a href="https://support.orcid.org/knowledgebase/articles/1190068">${springMacroRequestContext.getMessage("2FA.setup.step1.details.part_3")}</a></p>
                        <p>${springMacroRequestContext.getMessage("2FA.setup.step1.details.part_4")}</p>
                        <li class="bold">${springMacroRequestContext.getMessage("2FA.setup.step2")}</li>
                        <p>${springMacroRequestContext.getMessage("2FA.setup.step2.details")}</p>
                        <p><span class="bold">${springMacroRequestContext.getMessage("2FA.setup.cannot.scan1")}</span><a id="getTextCode" (click)="getTextCodeClick()">${springMacroRequestContext.getMessage("2FA.setup.cannot.scan2")}</a> ${springMacroRequestContext.getMessage("2FA.setup.cannot.scan3")}</p>
                        <img id="2FA-QR-code" *ngIf="showQRCode" [src]="qrCodeUrl" />
                        <pre *ngIf="showTextCode">{{textCodeFor2FA}}</pre>
                        <p *ngIf="showTextCode">${springMacroRequestContext.getMessage("2FA.setup.step2.prefer_QR")}<a (click)="showQRCodeAgain()">${springMacroRequestContext.getMessage("2FA.setup.step2.prefer_QR.click_here")}</a></p>
                        <li class="bold">${springMacroRequestContext.getMessage("2FA.setup.step3")}</li>
                        <p>${springMacroRequestContext.getMessage("2FA.setup.step3.details")}</p>
                    </ul>
                    <form>
                        <input name="verificationCode" type="text" [(ngModel)]="twoFactorAuthRegistration.verificationCode" />
                        <div id="invalidCodeError" class="orcid-error" *ngIf="showInvalidCodeError">${springMacroRequestContext.getMessage("2FA.setup.invalidCode")}</div>
                        <hr>
                        <button [disabled]="sendVerificationCodeDisabled" id="sendVerificationCode" (click)="sendVerificationCode()" class="btn btn-primary">${springMacroRequestContext.getMessage("2FA.continue")}</button>
                        <a class="leftBuffer" id="cancel2FASetup" (click)="cancel2FASetup()" >${springMacroRequestContext.getMessage("2FA.cancel")}</a>
                    </form>
                 </div>
                 <div *ngIf="show2FARecoveryCodes" >
                    <h2 id="saveRecoveryCodesHeading">${springMacroRequestContext.getMessage("2FA.recoveryCodes.heading")}</h2>
                    <p>
                        ${springMacroRequestContext.getMessage("2FA.recoveryCodes.details")}
                        <br />
                        <a href="${knowledgeBaseUri}/articles/1190068" target="2FA.learn_more_link">${springMacroRequestContext.getMessage("2FA.recoveryCodes.learn_more")}</a>
                    </p>
                    <div id="recoveryCodes">
                        <span *ngFor="let recoveryCode of recoveryCodes">{{recoveryCode}}<br></span>
                    </div>
                    <div class="form-group">
                        <button (click)="downloadRecoveryCodes()" class="btn btn-white"><span class="glyphicon glyphicon-download-alt"></span> ${springMacroRequestContext.getMessage("2FA.recoveryCodes.download")}</button>
                        <button (click)="copyRecoveryCodes()" class="btn btn-white"><span class="glyphicon glyphicon-file"></span> ${springMacroRequestContext.getMessage("2FA.recoveryCodes.copy")}</button>
                        <p>${springMacroRequestContext.getMessage("2FA.recoveryCodes.passwordManager")}</p>
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