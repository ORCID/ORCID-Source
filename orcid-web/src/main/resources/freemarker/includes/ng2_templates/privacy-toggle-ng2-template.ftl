<script type="text/ng-template" id="privacy-toggle-ng2-template">
    <div class="relative" class="privacy-bar-impr" role="presentation">
        <ul aria-label="<@orcid.msg 'aria.privacy_menu' />" aria-describedby="tool-tip-who_can_see" id="privacy-toggle" *ngIf="privacyNodeName" class="privacyToggle" (mouseenter)="showTooltip(name)" (mouseleave)="hideTooltip(name)" >
            <li class="publicActive" [ngClass]="{publicInActive: dataPrivacyObj[privacyNodeName]?.visibility != 'PUBLIC'}">
                <a (click)="setPrivacy('PUBLIC')" role="Button" aria-label="<@orcid.msg 'manage.lipublic' />"></a>
            </li>
            <li class="limitedActive limitedInActive" [ngClass]="{limitedInActive: dataPrivacyObj[privacyNodeName]?.visibility != 'LIMITED'}">
                <a (click)="setPrivacy('LIMITED')" role="Button" aria-label="<@orcid.msg 'manage.lilimited' />"></a>
            </li>
            <li class="privateActive privateInActive" [ngClass]="{privateInActive: dataPrivacyObj[privacyNodeName]?.visibility != 'PRIVATE'}">
                <a (click)="setPrivacy('PRIVATE')" role="Button" aria-label="<@orcid.msg 'manage.liprivate' />"></a>
            </li>
        </ul>
        <ul  role="Menu" aria-label="<@orcid.msg 'aria.privacy_menu' />" id="privacy-toggle" *ngIf="!privacyNodeName" class="privacyToggle" (mouseenter)="showTooltip(name)" (mouseleave)="hideTooltip(name)" >
            <li class="publicActive" [ngClass]="{publicInActive: dataPrivacyObj.visibility != 'PUBLIC'}">
                <a (click)="setPrivacy('PUBLIC')" role="Button" aria-label="<@orcid.msg 'manage.lipublic' />"></a>
            </li>
            <li class="limitedActive limitedInActive" [ngClass]="{limitedInActive: dataPrivacyObj.visibility != 'LIMITED'}">
                <a (click)="setPrivacy('LIMITED')" role="Button" aria-label="<@orcid.msg 'manage.lilimited' />"></a>
            </li>
            <li class="privateActive privateInActive" [ngClass]="{privateInActive: dataPrivacyObj.visibility != 'PRIVATE'}">
                <a (click)="setPrivacy('PRIVATE')" role="Button" aria-label="<@orcid.msg 'manage.liprivate' />"></a>
            </li>
        </ul>

        <div role="presentation" class="popover-help-container">
            <div role="presentation" class="popover top privacy-myorcid3" [ngClass]="showElement[name] == true ? 'block' : ''">
                <div role="presentation" class="arrow"></div>
                <div role="tooltip" id="tool-tip-who_can_see" class="popover-content">
                    <strong><@orcid.msg 'privacyToggle.help.who_can_see' /></strong>
                    <ul class="privacyHelp">
                        <li class="public" style="color: #009900;"><@orcid.msg 'privacyToggle.help.everyone' /></li>
                        <li class="limited" style="color: #ffb027;"><@orcid.msg 'privacyToggle.help.trusted_parties' /></li>
                        <li class="private" style="color: #990000;"><@orcid.msg 'privacyToggle.help.only_me' /></li>
                    </ul>
                    <a href="<@orcid.msg 'common.kb_uri_default'/>360006897614" target="privacyToggle.help.more_information"><@orcid.msg 'privacyToggle.help.more_information' /></a>
                </div>                
            </div>                              
        </div>
    </div>
</script>