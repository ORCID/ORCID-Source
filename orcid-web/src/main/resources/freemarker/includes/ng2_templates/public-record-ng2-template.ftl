<script type="text/ng-template" id="public-record-ng2-template">    
<#escape x as x?html>                   
    <#if (locked)?? && !locked>
    <ng-container *ngIf="personData">
        <!-- Other Names -->
            <div *ngIf="personData.publicGroupedOtherNames && objectKeys(personData.publicGroupedOtherNames).length > 0" class="workspace-section">
                <div class="workspace-section-header">
                    <ul class="inline-list visible workspace-section-heading">
                        <li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelAlsoknownas")}</span></li>
                        <li class="right">                                  
                            <span (click)="toggleSourcesDisplay('other-names')" class="right toggle" (mouseenter)="showPopover('other-names')" (mouseleave)="hidePopover('other-names')">
                                <i [ngClass]="(showSources['other-names'] || showSources['other-names'] == 'null')? 'glyphicons collapse_top relative' : 'glyphicons expand relative'"></i>
                                <div class="popover top" [ngClass]="{'block' : popoverShowing['other-names']}">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span *ngIf="showSources['other-names'] == false  || showSources['other-names'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                        <span *ngIf="showSources['other-names']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
                                    </div>
                                </div>                                          
                            </span>                                                                             
                        </li>                               
                    </ul>
                </div>
                <div id="public-other-names-div" class="public-content">
                        <ng-container  *ngFor="let otherName of objectKeys(personData.publicGroupedOtherNames); let lastName = last;">
                            <span  name="other-name">{{otherName}}</span>
                            <span *ngIf="!lastName && (showSources['other-names'] == false || showSources['other-names'] == null)">, </span>

                            <div *ngIf="showSources['other-names']" class="source-line separator">
                                <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                    <ng-container  *ngFor="let otherNameSource of personData.publicGroupedOtherNames[otherName]; let lastSource = last;">

                                        <ng-container>
                                            {{otherNameSource?.source?.sourceName?.content || otherNameSource?.source?.sourceOrcid?.path}}
                                        </ng-container>

                                         <ng-container  *ngIf="otherNameSource.createdDate">
                                            {{otherNameSource.createdDate.value}}
                                         </ng-container>

                                         <ng-container *ngIf="!lastSource">
                                         , 
                                         </ng-container>

                                    </ng-container>
                                </p>
                            </div>
                        </ng-container>
                </div>
            </div>    
        
        <!-- Websites -->                       
            <div *ngIf="personData && objectKeys(personData.publicGroupedResearcherUrls).length > 0" class="workspace-section">
                <div class="workspace-section-header">
                    <ul class="inline-list visible workspace-section-heading">
                        <li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelWebsites")}</span></li>
                        <li class="right">
                            <span (click)="toggleSourcesDisplay('websites')" class="right toggle" (mouseenter)="showPopover('websites')" (mouseleave)="hidePopover('websites')">
                                <i [ngClass]="(showSources['websites'] || showSources['websites'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i>
                                <div class="popover top" [ngClass]="{'block' : popoverShowing['websites']}">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span *ngIf="showSources['websites'] == false  || showSources['websites'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                        <span *ngIf="showSources['websites']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
                                    </div>
                                </div>
                            </span>
                        </li>                               
                    </ul>
                    <div id="public-researcher-urls-div" class="public-content">
                        <ng-container  *ngFor="let url of objectKeys(personData.publicGroupedResearcherUrls); let lastUrl = last;">
                            <a href="{{personData.publicGroupedResearcherUrls[url].content || url}}" target="{{personData.publicGroupedResearcherUrls[url].urlName || url}}" rel="me nofollow">
                                {{personData.publicGroupedResearcherUrls[url][0].urlName || url}}
                            </a>                                
                            <div *ngIf="showSources['websites']" class="source-line separator">                                        
                                <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                    <ng-container  *ngFor="let urlSource of personData.publicGroupedResearcherUrls[url]; let lastSource = last;">
                                        <ng-container>
                                            {{urlSource?.source?.sourceName?.content || urlSource?.source?.sourceOrcid?.path}}
                                        </ng-container>
                                        <ng-container *ngIf="urlSource.createdDate">
                                            {{urlSource.createdDate.value}}
                                        </ng-container>
                                        <ng-container *ngIf="!lastSource">
                                        , 
                                        </ng-container>
                                    </ng-container>
                                </p>                                                                                                                                                        
                            </div>  
                           <br *ngIf="!lastUrl" />
                        </ng-container>
                    </div>
                </div>
            </div>
       
        <!-- Countries -->                                 
            <div *ngIf="personData.publicGroupedAddresses && objectKeys(personData.publicGroupedAddresses).length > 0" class="workspace-section">
                <div class="workspace-section-header">
                    <ul class="inline-list visible workspace-section-heading">
                        <li><span class="workspace-section-title"><@orcid.msg 'public_profile.labelCountry'/></span></li>                                   
                            <li class="right">                  
                                <span (click)="toggleSourcesDisplay('countries')" class="right toggle" (mouseenter)="showPopover('countries')" (mouseleave)="hidePopover('countries')">
                                    <i [ngClass]="(showSources['countries'] || showSources['countries'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i>
                                    <div class="popover top" [ngClass]="{'block' : popoverShowing['countries']}">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <span *ngIf="showSources['countries'] == false  || showSources['countries'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                            <span *ngIf="showSources['countries']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
                                        </div>
                                    </div>
                                </span>                                     
                            </li>
                    </ul>                               
                    <div id="public-country-div" class="public-content">
                        <ng-container  *ngFor="let address of objectKeys(personData.publicGroupedAddresses); let lastAddress = last;">
                            <span name="country">
                                {{personData.countryNames[address]}}
                            </span>
                            
                            <span *ngIf="!lastAddress && (showSources['countries'] == false || showSources['countries'] == null)">
                                ,
                            </span>   

                            <div *ngIf="showSources['countries']" class="source-line separator">
                                <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />

                                    <ng-container  *ngFor="let addressSource of personData.publicGroupedAddresses[address]; let lastSource = last;">

                                        <ng-container>
                                            {{addressSource?.source?.sourceName?.content || addressSource?.source?.sourceOrcid?.path}}
                                        </ng-container>

                                         <ng-container  *ngIf="addressSource.createdDate">
                                            {{addressSource.createdDate.value}}
                                         </ng-container>

                                         <ng-container *ngIf="!lastSource">
                                         , 
                                         </ng-container>

                                    </ng-container>
                                </p>
                            </div>
                        </ng-container>
                    </div>
                </div>
            </div>
        <!-- Keywords -->
            <div  *ngIf="personData.publicGroupedKeywords && objectKeys(personData.publicGroupedKeywords).length > 0" class="workspace-section">
                <div class="workspace-section-header">
                    <ul class="inline-list visible workspace-section-heading">
                        <li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelKeywords")}</span></li>
                        <li class="right">                                      
                            <span (click)="toggleSourcesDisplay('keywords')" class="right toggle" (mouseenter)="showPopover('keywords')" (mouseleave)="hidePopover('keywords')">
                                <i [ngClass]="(showSources['keywords'] || showSources['keywords'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i>
                                <div class="popover top" [ngClass]="{'block' : popoverShowing['keywords']}">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span *ngIf="showSources['keywords'] == false  || showSources['keywords'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                        <span *ngIf="showSources['keywords']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
                                    </div>
                                </div>
                            </span>
                        </li>                               
                    </ul>   
                    <div id="public-keywords-div" class="public-content">   
                        <ng-container  *ngFor="let keyword of objectKeys(personData.publicGroupedKeywords); let lastkeyword = last;">
                            <span  name="keywords">{{keyword}}</span>
                            <span *ngIf="!lastkeyword && (showSources['keywords'] == false || showSources['keywords'] == null)">, </span>

                            <div *ngIf="showSources['keywords']" class="source-line separator">
                                <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                    <ng-container  *ngFor="let keywordSource of personData.publicGroupedKeywords[keyword]; let lastSource = last;">

                                        <ng-container>
                                            {{keywordSource?.source?.sourceName?.content || keywordSource?.source?.sourceOrcid?.path}}
                                        </ng-container>

                                         <ng-container  *ngIf="keywordSource.createdDate">
                                            {{keywordSource.createdDate.value}}
                                         </ng-container>

                                         <ng-container *ngIf="!lastSource">
                                         , 
                                         </ng-container>

                                    </ng-container>
                                </p>
                            </div>
                        </ng-container>                               
                    </div>
                </div>
            </div>
        <!-- Websites -->                       
        <#if (publicGroupedResearcherUrls)?? && (publicGroupedResearcherUrls?size != 0)>
            <div class="workspace-section">
                <div class="workspace-section-header">
                    <ul class="inline-list visible workspace-section-heading">
                        <li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelWebsites")}</span></li>
                        <li class="right">                                      
                            <span (click)="toggleSourcesDisplay('websites')" class="right toggle" (mouseenter)="showPopover('websites')" (mouseleave)="hidePopover('websites')">
                                <i [ngClass]="(showSources['websites'] || showSources['websites'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i>
                                <div class="popover top" [ngClass]="{'block' : popoverShowing['websites']}">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span *ngIf="showSources['websites'] == false  || showSources['websites'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                        <span *ngIf="showSources['websites']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
                                    </div>
                                </div>
                            </span>
                        </li>                               
                    </ul>
                    <div id="public-researcher-urls-div" class="public-content">                                       
                        <#list publicGroupedResearcherUrls?keys as url>
                            <#assign i = 1>
                            <#list publicGroupedResearcherUrls[url] as researcherUrl>                              
                                <#if (i == 1)>
                                      <a href="<@orcid.absUrl researcherUrl.url/>" target="researcherUrl.urlName" rel="me nofollow"><#if (researcherUrl.urlName)! != "">${researcherUrl.urlName?replace("{","[")?replace("}","]")}<#else>${researcherUrl.url.value?replace("{","[")?replace("}","]")}</#if></a><#if url_has_next><br/></#if>
                                </#if>          
                                <#if (i == 1)>                              
                                    <div *ngIf="showSources['websites']" class="source-line separator">
                                </#if>                                              
                                    <#if (i == 1)>                                                                              
                                        <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                    </#if>                                                                              
                                    <#if (researcherUrl.source)?? && (researcherUrl.source.sourceName)?? && (researcherUrl.source.sourceName.content)??>${researcherUrl.source.sourceName.content}<#else>${(effectiveUserOrcid)!}</#if> <#if (researcherUrl.createdDate)??>(${(researcherUrl.createdDate.value?datetime("yyyy-MM-dd")?date!)})</#if><#if researcherUrl_has_next>, </#if>
                                    <#assign i = i + 1> 
                            </#list>
                            </p>
                            </div>                                      
                        </#list>    
                    </div>
                </div>
            </div>
        </#if>                          
        <!-- External Identifiers -->
        <#if (publicGroupedPersonExternalIdentifiers)?? && (publicGroupedPersonExternalIdentifiers?size != 0)>
            <div class="workspace-section">
                <div class="workspace-section-header">
                    <ul class="inline-list visible workspace-section-heading">
                        <li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelOtherIDs")}</span></li>
                        <li class="right">
                            <span (click)="toggleSourcesDisplay('external-identifiers')" class="right toggle" (mouseenter)="showPopover('external-identifiers')" (mouseleave)="hidePopover('external-identifiers')">
                                <i [ngClass]="(showSources['external-identifiers'] || showSources['external-identifiers'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i>
                                <div class="popover top" [ngClass]="{'block' : popoverShowing['external-identifiers']}">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span *ngIf="showSources['external-identifiers'] == false  || showSources['external-identifiers'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                        <span *ngIf="showSources['external-identifiers']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
                                    </div>
                                </div>
                            </span>                                     
                        </li>                               
                    </ul>                               
                    <div id="public-external-identifiers-div" class="public-content">
                        <#list publicGroupedPersonExternalIdentifiers?keys as external>
                            <#assign i = 1>
                            <#list publicGroupedPersonExternalIdentifiers[external] as externalIdentifier>
                                <#if (i == 1)>
                                    <#if (externalIdentifier.url.value)??>
                                        <a href="${externalIdentifier.url.value}" target="externalIdentifier.value">${(externalIdentifier.type)!}: ${(externalIdentifier.value)!}</a><#if external_has_next><br/><span *ngIf="showSources['external-identifiers'] == false || showSources['external-identifiers'] == null"></span></#if>
                                    <#else>
                                        ${(externalIdentifier.type)!}: ${(externalIdentifier.value)!}<#if external_has_next><br/></#if>
                                    </#if>                                                                  
                                    <div *ngIf="showSources['external-identifiers']" class="source-line separator">                                                                                                                            
                                        <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                </#if>
                                <#if (externalIdentifier.source)?? && (externalIdentifier.source.sourceName)?? && (externalIdentifier.source.sourceName.content)??>${externalIdentifier.source.sourceName.content}<#else>${(effectiveUserOrcid)!}</#if> <#if (externalIdentifier.createdDate)??>(${(externalIdentifier.createdDate.value?datetime("yyyy-MM-dd")?date!)})</#if>
                                    <#assign i = i + 1> 
                           </#list>
                           </p>
                       </div>                                                   
                       </#list>
                    </div>
                 </div>
             </div>                 
        </#if>
        <!-- Email -->
       
            <div   *ngIf="personData.publicGroupedEmails && objectKeys(personData.publicGroupedEmails).length > 0" class="workspace-section">
                <div class="workspace-section-header">
                    <ul class="inline-list visible workspace-section-heading">
                        <li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelEmail")}</span></li>
                        <li class="right">                                      
                            <span (click)="toggleSourcesDisplay('emails')" class="right toggle" (mouseenter)="showPopover('emails')" (mouseleave)="hidePopover('emails')">
                                <i [ngClass]="(showSources['emails'] || showSources['emails'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i>
                                <div class="popover top" [ngClass]="{'block' : popoverShowing['emails']}">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span *ngIf="showSources['emails'] == false  || showSources['emails'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                        <span *ngIf="showSources['emails']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
                                    </div>
                                </div>
                            </span>
                        </li>                               
                    </ul>                               
                    <div class="public-content" id="public-emails-div">
                        <ng-container  *ngFor="let email of objectKeys(personData.publicGroupedEmails); let lastEmail = last;">
                            <a href="{{email}}" target="{{email}}" rel="me nofollow">
                                {{email}}
                            </a>                                
                            <div *ngIf="showSources['emails']" class="source-line separator">                                        
                                <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                    <ng-container  *ngFor="let emailSource of personData.publicGroupedEmails[email]; let lastSource = last;">
                                        <ng-container>
                                            {{emailSource?.source?.sourceName?.content || emailSource?.source?.sourceOrcid?.path}}
                                        </ng-container>
                                        <ng-container *ngIf="emailSource.createdDate">
                                            {{emailSource.createdDate.value}}
                                        </ng-container>
                                        <ng-container *ngIf="!lastSource">
                                        , 
                                        </ng-container>
                                    </ng-container>
                                </p>                                                                                                                                                        
                            </div>  
                           <br *ngIf="!lastEmail" />
                        </ng-container>
                    </div>                              
                </div>
            </div>

        </ng-container>

    </#if>

</#escape>
</script>
