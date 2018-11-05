<script type="text/ng-template" id="public-record-ng2-template">    
<#escape x as x?html>                   
    <#if (locked)?? && !locked>
        <!-- Other Names -->
        <#if (publicGroupedOtherNames)?? && (publicGroupedOtherNames?size != 0)>
            <div class="workspace-section">
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
                    <#list publicGroupedOtherNames?keys as otherName>
                        <span name="other-name">${otherName}</span><#if otherName_has_next><span *ngIf="showSources['other-names'] == false || showSources['other-names'] == null">, </span></#if>                                   
                        <div *ngIf="showSources['other-names']" class="source-line separator">
                            <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                <#list publicGroupedOtherNames[otherName] as otherNameSource>
                                    <#if (otherNameSource.source)?? && (otherNameSource.source.sourceName)?? && (otherNameSource.source.sourceName.content)??>${otherNameSource.source.sourceName.content!}<#else>${(effectiveUserOrcid)!}</#if>  <#if (otherNameSource.createdDate)??>(${otherNameSource.createdDate.value?datetime("yyyy-MM-dd")?date!})</#if><#if otherNameSource_has_next>,  </#if>
                                </#list>
                            </p>
                        </div>
                    </#list>
                </div>
            </div>                    
        </#if>
        
        <!-- Websites -->                       
        <#if (publicResearcherUrls)?? && (publicResearcherUrls.researcherUrls?size != 0)>
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
                        <#list publicResearcherUrls.researcherUrls as url>
                            <a href="<@orcid.absUrl url.url/>" target="url.urlName" rel="me nofollow">
                                <#if (url.urlName)! != "">
                                    ${url.urlName}
                                <#else>
                                    ${url.url.value}
                                </#if>
                            </a>                                
                            <div *ngIf="showSources['websites']" class="source-line separator">                                        
                                <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                    <#if (url.source)?? && (url.source.sourceName)?? && (url.source.sourceName.content)??>${url.source.sourceName.content}<#else>${(effectiveUserOrcid)!}</#if> <#if (url.createdDate)??>(${(url.createdDate.value?datetime("yyyy-MM-dd")?date!)})</#if>
                                </p>                                                                                                                                                        
                            </div>  
                            <#if url_has_next><br/></#if>
                        </#list>
                    </div>
                </div>
            </div>
        </#if>  
        <!-- Countries -->                                 
        <#if (publicAddress)?? || (publicGroupedAddresses)??>
            <div class="workspace-section">
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
                        <#list publicGroupedAddresses?keys as address>
                            <span name="country">${countryNames[address]}</span><#if address_has_next><span *ngIf="showSources['countries'] == false || showSources['countries'] == null">, </span></#if>                                    
                            <div *ngIf="showSources['countries']" class="source-line separator">
                                <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                    <#list publicGroupedAddresses[address] as addressSource>
                                        <#if (addressSource.source)?? && (addressSource.source.sourceName)?? && (addressSource.source.sourceName.content)??>${addressSource.source.sourceName.content!}<#else>${(effectiveUserOrcid)!}</#if>  <#if (addressSource.createdDate)??>(${addressSource.createdDate.value?datetime("yyyy-MM-dd")?date!})</#if><#if addressSource_has_next>, </#if>
                                    </#list>
                                </p>
                            </div>
                        </#list>
                    </div>
                </div>
            </div>
        </#if>
        <!-- Keywords -->
        <#if (publicGroupedKeywords)?? && (publicGroupedKeywords?size != 0)>
            <div class="workspace-section">
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
                        <#list publicGroupedKeywords?keys as keyword>                                                              
                            <span name="keyword">${keyword}</span><#if keyword_has_next><span *ngIf="showSources['keywords'] == false || showSources['keywords'] == null">, </span></#if>
                            <div *ngIf="showSources['keywords']" class="source-line separator">
                                <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                    <#list publicGroupedKeywords[keyword] as keywordSource>                 
                                        <#if (keywordSource.source)?? && (keywordSource.source.sourceName)?? && (keywordSource.source.sourceName.content)??>${keywordSource.source.sourceName.content}<#else>${(effectiveUserOrcid)!}</#if> <#if (keywordSource.createdDate)??>(${(keywordSource.createdDate.value?datetime("yyyy-MM-dd")?date!)})</#if><#if keywordSource_has_next>, </#if>
                                    </#list>
                                </p>
                            </div>
                        </#list>
                    </div>
                </div>
            </div>
        </#if>
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
                                      <a href="<@orcid.absUrl researcherUrl.url/>" target="researcherUrl.urlName" rel="me nofollow"><#if (researcherUrl.urlName)! != "">${researcherUrl.urlName}<#else>${researcherUrl.url.value}</#if></a><#if url_has_next><br/></#if>
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
        <#if (publicGroupedEmails)?? && (publicGroupedEmails?size != 0)>
            <div class="workspace-section">
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
                         <#list publicGroupedEmails?keys as email>                                                              
                            <div name="email">${email}</div>    
                            <div *ngIf="showSources['emails']" class="source-line separator">                                      
                                <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                    <#list publicGroupedEmails[email] as emailSource>                                                                                                       
                                        <#if (emailSource.source)?? && (emailSource.source.sourceName)?? && (emailSource.source.sourceName.content)??>${emailSource.source.sourceName.content}<#else>${(effectiveUserOrcid)!}</#if> <#if (emailSource.createdDate)??>(${(emailSource.createdDate.value?datetime("yyyy-MM-dd")?date!)})</#if>
                                    </#list>
                                </p>
                            </div>                          
                         </#list>
                    </div>                              
                </div>
            </div>
        </#if> 
    </#if>
</#escape>
</script>
