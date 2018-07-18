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
<script type="text/ng-template" id="ext-id-popover-ng2-template">
    <span *ngIf="relationship && relationship == 'part-of'" class='italic'><@orcid.msg 'common.part_of'/> </span><span>{{type | uppercase}}: </span>
    <span *ngIf="value && !url">{{value}}</span> 
    <a *ngIf="value && url" href="{{url}}" class="truncate-anchor inline" target="orcid.blank" (mouseenter)="showAffiliationExtIdPopOver(putCode)" (mouseleave)="hideAffiliationExtIdPopOver(putCode)">{{value}}</a> 
    <div *ngIf="extID?.url" class="popover-pos">
        <div class="popover-help-container">
            <div class="popover bottom" [ngClass]="{'block' : displayAffiliationExtIdPopOver[putCode] == true}">
                <div class="arrow"></div>
                <div class="popover-content">
                    <a href="{{url}}" target="orcid.blank" class="ng-binding">{{url}}</a>
                </div>
            </div>
        </div>
    </div>             
</script>