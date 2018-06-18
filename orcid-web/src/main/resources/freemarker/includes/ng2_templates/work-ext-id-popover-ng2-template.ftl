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
<script type="text/ng-template" id="work-ext-id-popover-ng2-template">
    <span *ngIf="extID.relationship && extID.relationship.value == 'part-of'" class='italic'><@orcid.msg 'common.part_of'/> </span><span>{{extID.workExternalIdentifierType.value | uppercase}}:</span>
    <a *ngIf="extID.value" href="{{extID.url.value}}" class="truncate-anchor inline" target="orcid.blank" (mouseenter)="showAffiliationExtIdPopOver(putCode)" (mouseleave)="hideAffiliationExtIdPopOver(putCode)">{{extID.workExternalIdentifierId.value}}</a> 
    <div *ngIf="extID.url" class="popover-pos">
        <div class="popover-help-container">
            <div class="popover bottom" [ngClass]="{'block' : displayAffiliationExtIdPopOver[putCode] == true}">
                <div class="arrow"></div>
                <div class="popover-content">
                    <a href="{{extID.url.value}}" target="orcid.blank" class="ng-binding">{{extID.url.value}}</a>
                </div>
            </div>
        </div>
    </div>             
</script>