<script type="text/ng-template" id="privacy-toggle-ng2-template">
    <div class="relative" class="privacy-bar-impr">
        <ul id="privacy-toggle" *ngIf="privacyNodeName" class="privacyToggle" (mouseenter)="showTooltip(name)" (mouseleave)="hideTooltip(name)" >
            <li class="publicActive" [ngClass]="{publicInActive: dataPrivacyObj[privacyNodeName]?.visibility != 'PUBLIC'}">
                <a (click)="setPrivacy('PUBLIC')"></a>
            </li>
            <li class="limitedActive limitedInActive" [ngClass]="{limitedInActive: dataPrivacyObj[privacyNodeName]?.visibility != 'LIMITED'}">
                <a (click)="setPrivacy('LIMITED')"></a>
            </li>
            <li class="privateActive privateInActive" [ngClass]="{privateInActive: dataPrivacyObj[privacyNodeName]?.visibility != 'PRIVATE'}">
                <a (click)="setPrivacy('PRIVATE')"></a>
            </li>
        </ul>
        <ul id="privacy-toggle" *ngIf="!privacyNodeName" class="privacyToggle" (mouseenter)="showTooltip(name)" (mouseleave)="hideTooltip(name)" >
            <li class="publicActive" [ngClass]="{publicInActive: dataPrivacyObj.visibility != 'PUBLIC'}">
                <a (click)="setPrivacy('PUBLIC')"></a>
            </li>
            <li class="limitedActive limitedInActive" [ngClass]="{limitedInActive: dataPrivacyObj.visibility != 'LIMITED'}">
                <a (click)="setPrivacy('LIMITED')"></a>
            </li>
            <li class="privateActive privateInActive" [ngClass]="{privateInActive: dataPrivacyObj.visibility != 'PRIVATE'}">
                <a (click)="setPrivacy('PRIVATE')"></a>
            </li>
        </ul>

        <div class="popover-help-container">
            <div class="popover top privacy-myorcid3" [ngClass]="showElement[name] == true ? 'block' : ''">
                <div class="arrow"></div>
                <div class="popover-content">
                    <strong><@orcid.msg 'privacyToggle.help.who_can_see' /></strong>
                    <ul class="privacyHelp">
                        <li class="public" style="color: #009900;"><@orcid.msg 'privacyToggle.help.everyone' /></li>
                        <li class="limited" style="color: #ffb027;"><@orcid.msg 'privacyToggle.help.trusted_parties' /></li>
                        <li class="private" style="color: #990000;"><@orcid.msg 'privacyToggle.help.only_me' /></li>
                    </ul>
                    <a href="https://support.orcid.org/knowledgebase/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information"><@orcid.msg 'privacyToggle.help.more_information' /></a>
                </div>                
            </div>                              
        </div>
    </div>
</script>