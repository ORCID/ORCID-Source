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
<@public >
<#escape x as x?html>
<div class="row workspace-top public-profile">
    <div class="col-md-3 left-aside">
        <div class="workspace-left workspace-profile" ng-controller="PublicRecordCtrl">
        	<div class="id-banner">
	            <h2 class="full-name">
	            	<#if (locked)?? && locked>
	            		<@orcid.msg 'public_profile.deactivated.given_names' /> <@orcid.msg 'public_profile.deactivated.family_name' />
	            	<#else>
		                ${(displayName)!}		                
	                </#if>
	            </h2>	            	            
	            
	            <div class="oid">
					<div class="id-banner-header">
						<span><@orcid.msg 'common.orcid_id' /></span>
					</div>
					<div class="orcid-id-container">
						<div class="orcid-id-info">
					    	<span class="mini-orcid-icon"></span>
					    	<!-- Reference: orcid.js:removeProtocolString() -->
				       		<span id="orcid-id" class="orcid-id shortURI">${baseDomainRmProtocall}/${(orcidId)!}</span>
						</div>
						<@security.authorize ifAnyGranted="ROLE_USER, ROLE_ADMIN, ROLE_BASIC, ROLE_PREMIUM, ROLE_BASIC_INSTITUTION, ROLE_PREMIUM_INSTITUTION">
							<div class="orcid-id-options">
								<a href="<@orcid.rootPath '/my-orcid'/>" class="gray-button"><@orcid.msg 'public-layout.return' /></a>
							</div>
						</@security.authorize>
					</div>
				</div>
					        
				<#if (locked)?? && !locked>
					<!-- Other Names -->
		            <#if (publicOtherNames)?? && (publicOtherNames.otherNames?size != 0)>
		            	<div class="workspace-section">
		            		<div class="workspace-section-header">
		            			<ul class="inline-list workspace-section-heading">
			            			<li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelAlsoknownas")}</span></li>
			            			<li class="right"><a href="" ng-click="toggleSourcesDisplay('other-names')" class="right"><i ng-class="(showSources['other-names'] || showSources['other-names'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i></a></li>		                		
		                		</ul>
		                	</div>
		                	<div id="public-other-names-div">		                	
				                <#list publicOtherNames.otherNames as otherName>				               
				                	${otherName.content}<#if otherName_has_next><span ng-if="showSources['other-names'] == false || showSources['other-names'] == null">,</span></#if>				                	
				                	<div ng-if="showSources['other-names']" ng-init='source = "${otherName.source.sourceName.content?js_string}"; createdDate = "${otherName.createdDate.value}"' class="source-line separator" ng-cloak>				                		
				                		<p>Sources:<br />
				                		{{source}} ({{createdDate | date:'yyyy-MM-dd'}})
				                		</p>				                						                			                						                			
				                	</div>				                						                	
				                </#list>
			                </div>
		                </div>
		            </#if>
		            <!-- Countries -->    	            	           
		            <#if (countryName)??>
		            	<div class="workspace-section">
		            		<div class="workspace-section-header">
		            			<ul class="inline-list workspace-section-heading">
								    <li><span class="workspace-section-title"><@orcid.msg 'public_profile.labelCountry'/></span></li>
								    <li class="right"><a href="" ng-click="toggleSourcesDisplay('countries')" class="right"><i ng-class="(showSources['countries'] || showSources['countries'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i></a></li>
								</ul>		                		
		                		<div id="public-country-div">
		                			${(countryName.countryName)!}
		                			<div ng-if="showSources['countries']" ng-init='source = "${countryName.sourceName?js_string}"; createdDate = "${countryName.createdDate.year}-${countryName.createdDate.month}-${countryName.createdDate.day}"' class="source-line separator" ng-cloak>				                		
				                		<p>Sources:<br />
				                		{{source}} ({{createdDate}})
				                		</p>				                						                			                						                			
				                	</div>
		                		</div>
		                	</div>
		                </div>
		            </#if>
		            <!-- Keywords -->
		            <#if (publicKeywords)?? && (publicKeywords.keywords?size != 0)>
			            <div class="workspace-section">
		            		<div class="workspace-section-header">
		            			<ul class="inline-list workspace-section-heading">
								    <li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelKeywords")}</span></li>
								    <li class="right"><a ng-click="toggleSourcesDisplay('keywords')" class="right"><i ng-class="(showSources['keywords'] || showSources['keywords'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i></a></li>		                		
								</ul>	
		                		<div id="public-keywords-div">		                    		
	                    			<#list publicKeywords.keywords as keyword>				               
					                	${keyword.content}<#if keyword_has_next><span ng-if="showSources['keywords'] == false || showSources['keywords'] == null">,</span></#if>				                	
					                	<div ng-if="showSources['keywords']" ng-init='source = "${keyword.source.sourceName.content?js_string}"; createdDate = "${keyword.createdDate.value}"' class="source-line separator" ng-cloak>				                		
					                		<p>Sources:<br />
					                		{{source}} ({{createdDate | date:'yyyy-MM-dd'}})
					                		</p>				                						                			                						                			
					                	</div>				                						                	
					                </#list>
	                        	</div>
	                        </div>
	                    </div>
		            </#if>
		            <!-- Websites -->       	            
		            <#if (publicResearcherUrls)?? && (publicResearcherUrls.researcherUrls?size != 0)>
		           		<div class="workspace-section">
		            		<div class="workspace-section-header">
		            			<ul class="inline-list workspace-section-heading">
								    <li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelWebsites")}</span></li>
								    <li class="right"><a href="" ng-click="toggleSourcesDisplay('websites')" class="right"><i ng-class="(showSources['websites'] || showSources['websites'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i></a></li>		                		
								</ul>
				                <div id="public-researcher-urls-div">
				                    <#list publicResearcherUrls.researcherUrls as url>
				                        <a href="<@orcid.absUrl url.url/>" target="_blank" rel="me nofollow">
				                        
				                        	<#if (url.urlName)! != "">
				                        		${url.urlName}
				                        	<#else>
				                        		${url.url.value}
				                        	</#if>
			                        	</a>			                	
					                	<div ng-if="showSources['websites']" ng-init='source = "${url.source.sourceName.content?js_string}"; createdDate = "${url.createdDate.value}"' class="source-line separator" ng-cloak>				                		
					                		<p>Sources:<br />
					                		{{source}} ({{createdDate | date:'yyyy-MM-dd'}})
					                		</p>				                						                			                						                			
					                	</div>	
					                	<#if url_has_next><br/></#if>
				                    </#list>
			                    </div>
			                </div>
	                    </div>
		            </#if>	  
		            <!-- Email -->
		            <#if (publicEmails)?? && (publicEmails.emails)?? && (publicEmails.emails?size != 0)>
		           		<div class="workspace-section">
		            		<div class="workspace-section-header">
		            			<ul class="inline-list workspace-section-heading">
								    <li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelEmail")}</span></li>
								    <li class="right"><a href="" ng-click="toggleSourcesDisplay('emails')" class="right"><i ng-class="(showSources['emails'] || showSources['emails'] == 'null')? 'glyphicons collapse_top' : 'glyphicons expand'"></i></a></li>		                		
								</ul>		            			
		            			<div class="emails-box" id="public-emails-div">
			            			 <#list publicEmails.emails as email>
			        					<#if (email.visibility == 'public')??>    			 				            			 				            			 	
			            					<div name="email">${email.email}</div>
			        					</#if>	
			        					<div ng-if="showSources['emails']" ng-init='source = "${email.source.sourceName.content?js_string}"; createdDate = "${email.createdDate.value}"' class="source-line separator" ng-cloak>				                		
					                		<p>Sources:<br />
					                		{{source}} ({{createdDate | date:'yyyy-MM-dd'}})
					                		</p>				                						                			                						                			
					                	</div>					 		
			            			 </#list>
		            			</div>		            			
			                </div>
	                    </div>
		            </#if>          	            
		            <!-- External Identifiers -->
		            <#if (publicPersonExternalIdentifiers)?? && (publicPersonExternalIdentifiers.externalIdentifier?size != 0)>
						<div class="workspace-section">
		            		<div class="workspace-section-header">
			            		<ul class="inline-list workspace-section-heading">
								    <li><span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelOtherIDs")}</span></li>
								    <li class="right"><a href="" class="right"><i class="glyphicons expand"></i></a></li>		                		
								</ul>				                
				                <div  id="public-external-identifiers-div">
				                    <#list publicPersonExternalIdentifiers.externalIdentifier as external>
				                        <#if (external.url.value)??>
				                            <a href="${external.url.value}" target="_blank">${(external.type)!}: ${(external.value)!}</a><#if external_has_next><br/></#if>
				                        <#else>
				                            ${(external.type)!}: ${(external.value)!}<#if external_has_next><br/></#if>
				                        </#if>    
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
        <div class="workspace-right" ng-controller="PersonalInfoCtrl">
        	<#if (locked)?? && locked>
        		<div class="alert alert-error readme">
		        	<p><b id="error_locked"><@orcid.msg 'public-layout.locked'/></b></p>
		        </div>        		
        	<#else>
	        	<#if (deprecated)??>
		        	<div class="alert alert-error readme">
		        		<p><b><@orcid.msg 'public_profile.deprecated_account.1'/>&nbsp;<a href="${baseUriHttp}/${primaryRecord}">${baseUriHttp}/${primaryRecord}</a>&nbsp;<@orcid.msg 'public_profile.deprecated_account.2'/></b></p>
		        	</div>
	        	</#if>
	        	<div class="workspace-inner-public workspace-public workspace-accordion">
	        		<#if (isProfileEmpty)?? && isProfileEmpty>
	        			<p class="margin-top-box"><b><@orcid.msg 'public_profile.empty_profile'/></b></p>
	        		<#else>	            
		                <#if (profile.orcidBio.biography.content)?? && (profile.orcidBio.biography.content)?has_content>		                	        			
		        			<div class="workspace-accordion-content" ng-show="displayInfo">
		        				<div class="row bottomBuffer">
		        					<div class="col-md-12 col-sm-12 col-xs-12">
		        						<h3 class="workspace-title">${springMacroRequestContext.getMessage("public_profile.labelBiography")}</h3>
		        					</div>
		        				</div>	        
		        				<div class="row bottomBuffer">					
			        				<div class="col-md-12 col-sm-12 col-xs-12">
										<div class="bio-content">${(profile.orcidBio.biography.content)!}</div>		        					
			        				</div>
			        			</div>	        				
		        			</div>
		                </#if>
		                <#assign publicProfile = true />
		                <#include "workspace_preview_activities_v3.ftl"/>	                    	
	        		</#if>
	        	</div>
	        </#if>            
        </div>
    </div>
</div>
</#escape>
</@public>