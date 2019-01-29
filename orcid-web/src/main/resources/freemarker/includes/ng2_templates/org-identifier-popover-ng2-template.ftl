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
    {{displayType}}: <a href="{{link}}" class="truncate-anchor" target="orcid.blank" rel="noopener norefferer" (mouseenter)="showURLPopOver(putCode)" (mouseleave)="hideURLPopOver(putCode)">{{value}}</a>
    <div class="popover-pos">
        <div class="popover-help-container">
            <div class="popover bottom" [ngClass]="{'block' : displayURLPopOver[putCode] == true}">
                <div class="arrow"></div>
                <div class="popover-content">
                    <a href="{{link}}" target="orcid.blank" class="ng-binding">{{link}}</a>
                </div>
            </div>
        </div>
  </div>
</span>
<div *ngIf="commonSrvc.orgDisambiguatedDetails[type+value]" class="col-md-11 bottomBuffer info-detail leftBuffer clearfix">
    <!--Org disambiguated name-->
    <span *ngIf="commonSrvc.orgDisambiguatedDetails[type+value]?.value">{{commonSrvc.orgDisambiguatedDetails[type+value]?.value}}</span>

    <!--Org disambiguated city-->
    <span *ngIf="commonSrvc.orgDisambiguatedDetails[type+value]?.city || commonSrvc.orgDisambiguatedDetails[type+value]?.region || commonSrvc.orgDisambiguatedDetails[type+value]?.country">: </span>

    
    <span *ngIf="commonSrvc.orgDisambiguatedDetails[type+value]?.city">{{commonSrvc.orgDisambiguatedDetails[type+value]?.city}}</span>

    <!--Org disambiguated region-->
    <span *ngIf="commonSrvc.orgDisambiguatedDetails[type+value]?.city && commonSrvc.orgDisambiguatedDetails[type+value]?.region">, </span>

    
    <span *ngIf="commonSrvc.orgDisambiguatedDetails[type+value]?.region">{{commonSrvc.orgDisambiguatedDetails[type+value]?.region}}</span>

    <!--Org disambiguated country-->
    <span *ngIf="commonSrvc.orgDisambiguatedDetails[type+value]?.country && (commonSrvc.orgDisambiguatedDetails[type+value]?.city || commonSrvc.orgDisambiguatedDetails[type+value]?.region)">, </span>
    <span *ngIf="commonSrvc.orgDisambiguatedDetails[type+value]?.country">{{commonSrvc.orgDisambiguatedDetails[type+value]?.country}}</span>

    <!--Org disambiguated URL-->
    <span *ngIf="commonSrvc.orgDisambiguatedDetails[type+value]?.url"><br>
        <a href="{{commonSrvc.orgDisambiguatedDetails[type+value]?.url}}" target="url">
        <span>{{commonSrvc.orgDisambiguatedDetails[type+value]?.url}}</span></a>
    </span> 

    <!--Org disambiguated ext ids-->
    <div *ngIf="commonSrvc.orgDisambiguatedDetails[type+value]?.orgDisambiguatedExternalIdentifiers">
        <strong><@orcid.msg 'workspace_affiliations.external_ids'/> {{displayType}}</strong><br>
        <ul class="reset">
            <li *ngFor="let orgDisambiguatedExternalIdentifier of commonSrvc.orgDisambiguatedDetails[type+value]?.orgDisambiguatedExternalIdentifiers">
                {{orgDisambiguatedExternalIdentifier.identifierType}}:
                <span *ngIf="orgDisambiguatedExternalIdentifier.preferred && !isUrl(orgDisambiguatedExternalIdentifier.preferred)">{{orgDisambiguatedExternalIdentifier.preferred}} <@orcid.msg 'workspace_affiliations.external_ids_preferred'/></span>
                <span *ngIf="orgDisambiguatedExternalIdentifier.preferred && isUrl(orgDisambiguatedExternalIdentifier.preferred)"> <a  target="orcid.blank" href="{{orgDisambiguatedExternalIdentifier.preferred}}">{{orgDisambiguatedExternalIdentifier.preferred}} </a>  <@orcid.msg 'workspace_affiliations.external_ids_preferred'/></span>
                <!-- Put the ',' only if there is more than one ext id or if the only one is not the same as the preferred one -->
                <span *ngIf="orgDisambiguatedExternalIdentifier.all && (orgDisambiguatedExternalIdentifier.all.length > 1 || (orgDisambiguatedExternalIdentifier.preferred && (orgDisambiguatedExternalIdentifier.all[0] != orgDisambiguatedExternalIdentifier.preferred)))">,</span>   
                <span *ngIf="orgDisambiguatedExternalIdentifier.all">
                    <span *ngFor="let orgDisambiguatedExternalIdentifierAll of orgDisambiguatedExternalIdentifier.all;let last = last">
                        <span *ngIf="orgDisambiguatedExternalIdentifierAll != orgDisambiguatedExternalIdentifier.preferred">
                        <ng-container *ngIf="isUrl(orgDisambiguatedExternalIdentifierAll)">
                            <a target="orcid.blank" href="{{orgDisambiguatedExternalIdentifierAll}}">{{orgDisambiguatedExternalIdentifierAll}} </a>{{last ? '' : ', '}}
                        </ng-container>
                        <ng-container *ngIf="!isUrl(orgDisambiguatedExternalIdentifierAll)">
                            {{orgDisambiguatedExternalIdentifierAll}}{{last ? '' : ', '}}
                        </ng-container>
                        </span>                                        
                    </span>
                </span>
            </li>
        </ul>
    </div>  
</div>             
</script>
