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
<script type="text/ng-template" id="activities-external-identifier-ng2-template">
<li>
    <span *ngIf="isPartOf" class='italic'><@orcid.msg 'common.part_of'/>{{type}}</span>
    <a *ngIf="value" href="{{link}}" class='truncate-anchor inline' target='orcid.blank' (mouseenter)='showActivityExtIdPopOver(putCode+index)' (mouseleave)='hideActivityExtIdPopOver(putCode+index)'>{{value}}</a> 
    <div *ngIf="link" class="popover-pos">
        <div class="popover-help-container">
            <div class="popover bottom" [ngClass]="{'block' : displayActivityExtIdPopOver[putCode+index] == true}">
                <div class="arrow"></div>
                <div class="popover-content">
                    <a href="{{link}}" target="orcid.blank" class="ng-binding">{{link}}</a>
                </div>
            </div>
        </div>
    </div>
</li>                
</script>