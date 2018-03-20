<@public >
<#escape x as x?html>
<#setting date_format="yyyy-MM-dd">
<div class="row workspace-top public-profile">
    <div class="col-md-3 lhs left-aside">
        <div class="workspace-left workspace-profile" ng-controller="PublicRecordCtrl">
            <div class="id-banner">
                <h2 class="full-name">                  
                    ${(displayName)!}                   
                </h2>                               
                 <div>
                </div>
                <div class="oid">
                    <div class="id-banner-header">
                        <span><@orcid.msg 'common.orcid_id' /></span>
                    </div>
                    <div class="orcid-id-container">
                        <div class="orcid-id-options">
                            <@orcid.checkFeatureStatus featureName='HTTPS_IDS'>
                                <div class="orcid-id-info">
                                    <span class="mini-orcid-icon-16"></span>
                                    <!-- Reference: orcid.js:removeProtocolString() -->
                                    <span id="orcid-id" class="orcid-id-https">${baseUri}/${(effectiveUserOrcid)!}</span>
                                </div>
                            </@orcid.checkFeatureStatus>
                            <@orcid.checkFeatureStatus featureName='HTTPS_IDS' enabled=false>
                                <div class="orcid-id-info">
                                    <span class="mini-orcid-icon"></span>
                                    <!-- Reference: orcid.js:removeProtocolString() -->
                                    <span id="orcid-id" class="orcid-id shortURI">${baseDomainRmProtocall}/${(effectiveUserOrcid)!}</span>
                                </div>
                            </@orcid.checkFeatureStatus>    
                        </div>                  
                    </div>
                </div>
                <#include "includes/print_record.ftl"/>                         

                <#if (locked)?? && !locked>
                    <!-- Other Names -->
                    <#if (publicGroupedOtherNames)?? && (publicGroupedOtherNames?size != 0)>
                        <div class="workspace-section">
                            <div class="workspace-section-header">
                                <ul class="inline-list visible workspace-section-heading">
                                    <li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelAlsoknownas")}</span></li>
                                    <li class="right">                                  
                                        <span ng-click="toggleSourcesDisplay('other-names')" class="right toggle" ng-mouseenter="showPopover('other-names')" ng-mouseleave="hidePopover('other-names')">
                                            <i ng-class="(showSources['other-names'] || showSources['other-names'] == 'null')? 'glyphicons collapse_top relative' : 'glyphicons expand relative'"></i>
                                            <div class="popover top" ng-class="{'block' : showPopover['other-names']}">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <span ng-show="showSources['other-names'] == false  || showSources['other-names'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                                    <span ng-show="showSources['other-names']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
                                                </div>
                                            </div>                                          
                                        </span>                                                                             
                                    </li>                               
                                </ul>
                            </div>
                            <div id="public-other-names-div" class="public-content">
                                <#list publicGroupedOtherNames?keys as otherName>
                                    <span name="other-name">${otherName}</span><#if otherName_has_next><span ng-if="showSources['other-names'] == false || showSources['other-names'] == null">,</span></#if>                                   
                                    <div ng-if="showSources['other-names']" class="source-line separator" ng-cloak>
                                        <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                            <#list publicGroupedOtherNames[otherName] as otherNameSource>
                                                <#if (otherNameSource.source)?? && (otherNameSource.source.sourceName)?? && (otherNameSource.source.sourceName.content)??>${otherNameSource.source.sourceName.content!}<#else>${(effectiveUserOrcid)!}</#if>  <#if (otherNameSource.createdDate)??>(${otherNameSource.createdDate.value?datetime("yyyy-MM-dd")?date!})</#if><#if otherNameSource_has_next>,</#if>
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
                                        <span ng-click="toggleSourcesDisplay('websites')" class="right toggle" ng-mouseenter="showPopover('websites')" ng-mouseleave="hidePopover('websites')">
                                            <i ng-class="(showSources['websites'] || showSources['websites'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i>
                                            <div class="popover top" ng-class="{'block' : showPopover['websites']}">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <span ng-show="showSources['websites'] == false  || showSources['websites'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                                    <span ng-show="showSources['websites']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
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
                                        <div ng-if="showSources['websites']" class="source-line separator" ng-cloak>                                        
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
                                            <span ng-click="toggleSourcesDisplay('countries')" class="right toggle" ng-mouseenter="showPopover('countries')" ng-mouseleave="hidePopover('countries')">
                                                <i ng-class="(showSources['countries'] || showSources['countries'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i>
                                                <div class="popover top" ng-class="{'block' : showPopover['countries']}">
                                                    <div class="arrow"></div>
                                                    <div class="popover-content">
                                                        <span ng-show="showSources['countries'] == false  || showSources['countries'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                                        <span ng-show="showSources['countries']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
                                                    </div>
                                                </div>
                                            </span>                                     
                                        </li>
                                </ul>                               
                                <div id="public-country-div" class="public-content">                                                                                                                
                                    <#list publicGroupedAddresses?keys as address>
                                        <span name="country">${countryNames[address]}</span><#if address_has_next><span ng-if="showSources['countries'] == false || showSources['countries'] == null">,</span></#if>                                    
                                        <div ng-if="showSources['countries']" class="source-line separator" ng-cloak>
                                            <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                                <#list publicGroupedAddresses[address] as addressSource>
                                                    <#if (addressSource.source)?? && (addressSource.source.sourceName)?? && (addressSource.source.sourceName.content)??>${addressSource.source.sourceName.content!}<#else>${(effectiveUserOrcid)!}</#if>  <#if (addressSource.createdDate)??>(${addressSource.createdDate.value?datetime("yyyy-MM-dd")?date!})</#if><#if addressSource_has_next>,</#if>
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
                                        <span ng-click="toggleSourcesDisplay('keywords')" class="right toggle" ng-mouseenter="showPopover('keywords')" ng-mouseleave="hidePopover('keywords')">
                                            <i ng-class="(showSources['keywords'] || showSources['keywords'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i>
                                            <div class="popover top" ng-class="{'block' : showPopover['keywords']}">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <span ng-show="showSources['keywords'] == false  || showSources['keywords'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                                    <span ng-show="showSources['keywords']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
                                                </div>
                                            </div>
                                        </span>
                                    </li>                               
                                </ul>   
                                <div id="public-keywords-div" class="public-content">                                   
                                    <#list publicGroupedKeywords?keys as keyword>                                                              
                                        <span name="keyword">${keyword}</span><#if keyword_has_next><span ng-if="showSources['keywords'] == false || showSources['keywords'] == null">,</span></#if>
                                        <div ng-if="showSources['keywords']" class="source-line separator" ng-cloak>
                                            <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                                <#list publicGroupedKeywords[keyword] as keywordSource>                                                                                                 
                                                    <#if (keywordSource.source)?? && (keywordSource.source.sourceName)?? && (keywordSource.source.sourceName.content)??>${keywordSource.source.sourceName.content}<#else>${(effectiveUserOrcid)!}</#if> <#if (keywordSource.createdDate)??>(${(keywordSource.createdDate.value?datetime("yyyy-MM-dd")?date!)})</#if><#if keywordSource_has_next>,</#if>
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
                                        <span ng-click="toggleSourcesDisplay('websites')" class="right toggle" ng-mouseenter="showPopover('websites')" ng-mouseleave="hidePopover('websites')">
                                            <i ng-class="(showSources['websites'] || showSources['websites'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i>
                                            <div class="popover top" ng-class="{'block' : showPopover['websites']}">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <span ng-show="showSources['websites'] == false  || showSources['websites'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                                    <span ng-show="showSources['websites']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
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
                                                <div ng-if="showSources['websites']" class="source-line separator" ng-cloak>
                                            </#if>                                              
                                                <#if (i == 1)>                                                                              
                                                    <p>${springMacroRequestContext.getMessage("public_record.sources")}:<br />
                                                </#if>                                                                                                                                      
                                                <#if (researcherUrl.source)?? && (researcherUrl.source.sourceName)?? && (researcherUrl.source.sourceName.content)??>${researcherUrl.source.sourceName.content}<#else>${(effectiveUserOrcid)!}</#if> <#if (researcherUrl.createdDate)??>(${(researcherUrl.createdDate.value?datetime("yyyy-MM-dd")?date!)})</#if><#if researcherUrl_has_next>,</#if>
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
                                        <span ng-click="toggleSourcesDisplay('emails')" class="right toggle" ng-mouseenter="showPopover('emails')" ng-mouseleave="hidePopover('emails')">
                                            <i ng-class="(showSources['emails'] || showSources['emails'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i>
                                            <div class="popover top" ng-class="{'block' : showPopover['emails']}">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <span ng-show="showSources['emails'] == false  || showSources['emails'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                                    <span ng-show="showSources['emails']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
                                                </div>
                                            </div>
                                        </span>
                                    </li>                               
                                </ul>                               
                                <div class="public-content" id="public-emails-div">
                                     <#list publicGroupedEmails?keys as email>                                                              
                                        <div name="email">${email}</div>    
                                        <div ng-if="showSources['emails']" class="source-line separator" ng-cloak>                                      
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
                     
                    <!-- External Identifiers -->
                    <#if (publicGroupedPersonExternalIdentifiers)?? && (publicGroupedPersonExternalIdentifiers?size != 0)>
                        <div class="workspace-section">
                            <div class="workspace-section-header">
                                <ul class="inline-list visible workspace-section-heading">
                                    <li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelOtherIDs")}</span></li>
                                    <li class="right">
                                        <span ng-click="toggleSourcesDisplay('external-identifiers')" class="right toggle" ng-mouseenter="showPopover('external-identifiers')" ng-mouseleave="hidePopover('external-identifiers')">
                                            <i ng-class="(showSources['external-identifiers'] || showSources['external-identifiers'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i>
                                            <div class="popover top" ng-class="{'block' : showPopover['external-identifiers']}">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <span ng-show="showSources['external-identifiers'] == false  || showSources['external-identifiers'] == null">${springMacroRequestContext.getMessage("public_record.showDetails")}</span>
                                                    <span ng-show="showSources['external-identifiers']">${springMacroRequestContext.getMessage("public_record.hideDetails")}</span>
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
                                                    <a href="${externalIdentifier.url.value}" target="externalIdentifier.value">${(externalIdentifier.type)!}: ${(externalIdentifier.value)!}</a><#if external_has_next><br/><span ng-if="showSources['external-identifiers'] == false || showSources['external-identifiers'] == null"></span></#if>
                                                <#else>
                                                    ${(externalIdentifier.type)!}: ${(externalIdentifier.value)!}<#if external_has_next><br/></#if>
                                                </#if>                                                                  
                                                <div ng-if="showSources['external-identifiers']" class="source-line separator" ng-cloak>                                                                                                                            
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
                </#if>
            </div>
        </div>
    </div>
    
    <div class="col-md-9 right-aside">
        <@orcid.checkFeatureStatus 'ANGULAR2_QA'>
        <personal-info-ng2></personal-info-ng2>
        </@orcid.checkFeatureStatus>
        <@orcid.checkFeatureStatus featureName='ANGULAR1_LEGACY' enabled=false> 
        <div class="workspace-right" ng-controller="PersonalInfoCtrl">
            <div class="workspace-inner-public workspace-public workspace-accordion">
                <#if (isProfileEmpty)?? && isProfileEmpty>
                    <p class="margin-top-box"><b><@orcid.msg 'public_profile.empty_profile'/></b></p>
                <#else>             
                    <#if (biography.content)?? && (biography.content)?has_content>                                              
                        <div class="workspace-accordion-content" ng-show="displayInfo">
                            <div class="row bottomBuffer">
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <h3 class="workspace-title">${springMacroRequestContext.getMessage("public_profile.labelBiography")}</h3>
                                </div>
                            </div>          
                            <div class="row bottomBuffer">                  
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <div class="bio-content">${(biography.content)!}</div>                                  
                                </div>
                            </div>                          
                        </div>
                    </#if>
                    <#assign publicProfile = true />
                    <#include "workspace_preview_activities_v3.ftl"/>                           
                </#if>
                <@orcid.checkFeatureStatus 'LAST_MOD'>
                    <div id="public-last-modified">
                        <p class="small italic">${springMacroRequestContext.getMessage("public_profile.labelLastModified")} {{lastModifiedDate}}</p>                    
                    </div>
                </@orcid.checkFeatureStatus>                   
            </div>
        </div>
        </@orcid.checkFeatureStatus>
    </div>
</div>
</#escape>
</@public>