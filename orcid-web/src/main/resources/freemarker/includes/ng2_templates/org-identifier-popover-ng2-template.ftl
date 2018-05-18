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
<script type="text/ng-template" id="org-identifier-popover-ng2-template">
<span *ngIf="!link" class="url-popover">
    {{displayType}}: {{value}}
</span>
<span *ngIf="link" class="url-popover">
    {{displayType}}: <a href="link" class="truncate-anchor" target="orcid.blank" rel="noopener norefferer" (mouseenter)="showURLPopOver(putCode)" (mouseleave)="hideURLPopOver(putCode)">{{value}}</a>
    <div class="popover-pos">
        <div class="popover-help-container">
            <div class="popover bottom" [ngClass]="{'block' : displayURLPopOver[putCode] == true}">
                <div class="arrow"></div>
                <div class="popover-content">
                    <a href="link" target="orcid.blank" class="ng-binding">{{link}}</a>
                </div>
            </div>
        </div>
  </div>
</span>             
</script>