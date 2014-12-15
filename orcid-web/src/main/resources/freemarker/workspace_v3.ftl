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
<#-- @ftlvariable name="profile" type="org.orcid.jaxb.model.message.OrcidProfile" -->
<@protected nav="record">
<#escape x as x?html>
<#if emailVerified?? && emailVerified>
    <div class="alert alert-success">
        <strong><@spring.message "orcid.frontend.web.email_verified"/></strong>
    </div>
</#if>

<#if invalidVerifyUrl?? && invalidVerifyUrl>
    <div class="alert alert-success">
        <strong><@spring.message "orcid.frontend.web.invalid_verify_link"/></strong>
    </div>
</#if>


<#if invalidOrcid?? && invalidOrcid>
    <div class="alert alert-success">
        <strong><@spring.message "orcid.frontend.web.invalid_switch_orcid"/></strong>
    </div>
</#if>

<div class="row workspace-top public-profile">

	<#-- hidden divs that trigger angular -->
	<#if RequestParameters['recordClaimed']??>
	    <div ng-controller="ClaimThanks" style="display: hidden;"></div>	    
	<#elseif !Session.CHECK_EMAIL_VALIDATED?exists && !inDelegationMode>
    	<div ng-controller="VerifyEmailCtrl" style="display: hidden;" orcid-loading="{{loading}}"></div>
	</#if>
	<!-- ID Banner and other account information -->
    <div class="col-md-3 lhs left-aside">
    	<div class="workspace-profile">
            
            <#include "includes/id_banner.ftl"/>
            
            <div class="qrcode-container">
                <a href="http://qrcode.orcid.org" target="_blank"><span class="glyphicons qrcode orcid-qr"></span><@orcid.msg 'workspace.qrcode.link.text'/></a>
                <div class="popover-help-container">
                    <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                    <div id="qrcode-help" class="popover bottom">
                        <div class="arrow"></div>
                        <div class="popover-content">
                            <p><@orcid.msg 'workspace.qrcode.help'/></p>
                        </div>
                    </div>
                </div>
            </div>
            
	       	<div class="other-names-box">
		       	<div ng-controller="OtherNamesCtrl" class="other-names-controller">
		        	<div>
		        	   <strong><@orcid.msg 'workspace.Alsoknownas'/></strong>
		        	   <span ng-hide="showEdit == true" ng-click="openEdit()">		        	   	  
		        	      	<span class="glyphicon glyphicon-pencil edit-other-names edit-option pull-right" title=""></span>
		        	      <br />
		        	      <span ng-repeat="otherNames in otherNamesForm.otherNames" ng-cloak>
		        	         {{ $last?otherNames.value:otherNames.value+ ", "}}
		        	      </span>
		        	   </span>
		        	   <div ng-show="showEdit == true" ng-cloak class="other-names-edit">
		        	      <@orcid.privacyToggle  angularModel="otherNamesForm.visibility.visibility"
				             questionClick="toggleClickPrivacyHelp()"
				             clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				             publicClick="setPrivacy('PUBLIC', $event)" 
	                 	     limitedClick="setPrivacy('LIMITED', $event)" 
	                 	     privateClick="setPrivacy('PRIVATE', $event)" />
		        	   
		        	      <div ng-repeat="otherNames in otherNamesForm.otherNames">
		        	          <input type="text" ng-model="otherNames.value"></input
		        	          <a ng-click="deleteKeyword(otherNames)" class="glyphicon glyphicon-trash grey"></a>
		        	          <br />
		        	          <span class="orcid-error" ng-show="otherNames.url.errors.length > 0">
							     <div ng-repeat='error in otherNames.url.errors' ng-bind-html="error"></div>
						      </span>
		        	          <span class="orcid-error" ng-show="otherNames.name.errors.length > 0">
							     <div ng-repeat='error in otherNames.name.errors' ng-bind-html="error"></div>
						      </span>
		        	      </div>
		        	      <a class="glyphicon glyphicon-plus" ng-click="addNew()"></a><br />
		        	      <button class="btn btn-primary" ng-click="setOtherNamesForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		        	      <button class="btn" ng-click="close()"><@spring.message "freemarker.btncancel"/></button>
		        	   </div> 
		           </div>
		       	</div>
	       	</div>
            
            
            
            
            <div ng-controller="CountryCtrl" class="country-controller">
            	<div class="profile-header">
		        	<strong><@orcid.msg 'public_profile.labelCountry'/></strong>
		            <span class="glyphicon glyphicon-pencil edit-country edit-option pull-right" ng-click="openEdit()" title="" ng-hide="showEdit == true"></span>
		            <span ng-hide="showEdit == true" ng-click="toggleEdit()">	                
	                <span ng-show="countryForm != null && countryForm.countryName != null" ng-bind="countryForm.countryName" ng-hide="showEdit == true"></span>
	            </span>		            
	            </div>
                
               <div ng-show="showEdit == true" ng-cloak class="country-edit">
               	  <@orcid.privacyToggle  angularModel="countryForm.profileAddressVisibility.visibility"
			         questionClick="toggleClickPrivacyHelp()"
			         clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
			         publicClick="setPrivacy('PUBLIC', $event)" 
                 	     limitedClick="setPrivacy('LIMITED', $event)" 
                 	     privateClick="setPrivacy('PRIVATE', $event)" />
                  
                  <select id="country" name="country" ng-model="countryForm.iso2Country.value">
		    			<option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
						<#list isoCountries?keys as key>
							<option value="${key}">${isoCountries[key]}</option>
						</#list>
				  </select>				  
				  
	             <button class="btn btn-primary" ng-click="setCountryForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		         <button class="btn" ng-click="close()"><@spring.message "freemarker.btncancel"/></button>
	              			  
				</div>
				
            </div>
            
	       	<div class="keyword-box">
		       	<div ng-controller="KeywordsCtrl" class="keywords-controller">
		        	<div class="profile-header">
		        	   
		        	   <strong><@orcid.msg 'public_profile.labelKeywords'/></strong>
		        	   
		        	   
		        	   <span ng-hide="showEdit">
		        	   	  <span class="glyphicon glyphicon-pencil edit-keywords edit-option pull-right" ng-click="openEdit()" title=""></span>	
		        	      <span ng-repeat="keyword in keywordsForm.keywords" ng-cloak>
		        	         {{ $last?keyword.value:keyword.value+ ", "}}
		        	      </span>
		        	   </span>
		        	 </div>   
		        	   
		        	   
		        	   <div ng-show="showEdit == true" ng-cloak class="keywords-edit">
		        	      <@orcid.privacyToggle  angularModel="keywordsForm.visibility.visibility"
				             questionClick="toggleClickPrivacyHelp()"
				             clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				             publicClick="setPrivacy('PUBLIC', $event)" 
	                 	     limitedClick="setPrivacy('LIMITED', $event)" 
	                 	     privateClick="setPrivacy('PRIVATE', $event)" />
		        	   
		        	      <div ng-repeat="keyword in keywordsForm.keywords">
		        	          <input type="text" ng-model="keyword.value"></input>
		        	          <a ng-click="deleteKeyword(keyword)" class="glyphicon glyphicon-trash grey"></a>
		        	          <br />
		        	          <span class="orcid-error" ng-show="keyword.url.errors.length > 0">
							     <div ng-repeat='error in keyword.url.errors' ng-bind-html="error"></div>
						      </span>
		        	          <span class="orcid-error" ng-show="keyword.name.errors.length > 0">
							     <div ng-repeat='error in keyword.name.errors' ng-bind-html="error"></div>
						      </span>
		        	      </div>
		        	      <a class="glyphicon glyphicon-plus" ng-click="addNew()"></a><br />
		        	      <button class="btn btn-primary" ng-click="setKeywordsForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		        	      <button class="btn" ng-click="close()"><@spring.message "freemarker.btncancel"/></button>
		        	   </div> 
		         
		       	</div>
	       	</div>
	       	
	       	<div class="websites-box">
		       	<div ng-controller="WebsitesCtrl" class="websites-controller">
		        	<div>
		        	   <strong><@orcid.msg 'public_profile.labelWebsites'/></strong>
		        	   <span ng-hide="showEdit == true">
		        	      <span class="glyphicon glyphicon-pencil edit-websites edit-option pull-right" ng-click="openEdit()" title=""></span><br />
		        	      <div ng-repeat="website in websitesForm.websites" ng-cloak class="wrap">
		        	         <a href="{{website.url.value}}" target="_blank" rel="nofollow">{{website.name.value != null? website.name.value : website.url.value}}</a>
		        	      </div>
		        	   </span>		        	   
		        	   <div ng-show="showEdit == true" ng-cloak class="websites-edit">
		        	      <@orcid.privacyToggle  angularModel="websitesForm.visibility.visibility"
				             questionClick="toggleClickPrivacyHelp()"
				             clickedClassCheck="{'popover-help-container-show':privacyHelp==true}" 
				             publicClick="setPrivacy('PUBLIC', $event)" 
	                 	     limitedClick="setPrivacy('LIMITED', $event)" 
	                 	     privateClick="setPrivacy('PRIVATE', $event)" />
		        	   
		        	      <div ng-repeat="website in websitesForm.websites" class="mobile-box">
		        	          <input type="text" ng-model="website.name.value" placeholder="${springMacroRequestContext.getMessage("manual_work_form_contents.labeldescription")}"></input>
		        	          <input type="text" ng-model="website.url.value" placeholder="${springMacroRequestContext.getMessage("common.url")}"></input>
		        	          <a ng-click="deleteWebsite(website)" class="glyphicon glyphicon-trash grey"></a>
		        	          <br />
		        	          <span class="orcid-error" ng-show="website.url.errors.length > 0">
							     <div ng-repeat='error in website.url.errors' ng-bind-html="error"></div>
						      </span>
		        	          <span class="orcid-error" ng-show="website.name.errors.length > 0">
							     <div ng-repeat='error in website.name.errors' ng-bind-html="error"></div>
						      </span>
		        	      </div>
		        	      <a class="glyphicon glyphicon-plus" ng-click="addNew()"></a><br />
		        	      <button class="btn btn-primary" ng-click="setWebsitesForm()"><@spring.message "freemarker.btnsavechanges"/></button>
		        	      <button class="btn" ng-click="close()"><@spring.message "freemarker.btncancel"/></button>
		        	   </div> 
		           </div>
		       	</div>
	       	</div>
       		<div ng-controller="ExternalIdentifierCtrl" ng-hide="!externalIdentifiersPojo.externalIdentifiers.length" ng-cloak>	       			
       			<p><strong><@orcid.msg 'public_profile.labelOtherIDs'/></strong></p>
       			<div ng-repeat='externalIdentifier in externalIdentifiersPojo.externalIdentifiers'>
		        	<span ng-hide="externalIdentifier.externalIdUrl">{{externalIdentifier.externalIdCommonName.content}} {{externalIdentifier.externalIdReference.content}}</span>
		        	<span ng-show="externalIdentifier.externalIdUrl"><a href="{{externalIdentifier.externalIdUrl.value}}" target="_blank">{{externalIdentifier.externalIdCommonName.content}} {{externalIdentifier.externalIdReference.content}}</a></span>
			   		<a ng-click="deleteExternalIdentifier($index)" class="glyphicon glyphicon-trash grey"></a>       			
       			</div>
			</div>													    
	        <#if RequestParameters['OldPersonal']??>	        
				<p class="hoover-white-fonts">	       
		       		<a href="<@spring.url '/account/manage-bio-settings'/>" id="update-personal-modal-link" class="label btn-primary"><@orcid.msg 'workspace.Update'/></a>
		        </p>
	        </#if>
		</div>
    </div>
    
    <div class="col-md-9 right-aside">
        <div class="workspace-right">
        	<div class="workspace-inner workspace-header" ng-controller="WorkspaceSummaryCtrl">
                <div class="grey-box" ng-show="showAddAlert()" ng-cloak>
                	<strong><@orcid.msg 'workspace.addinformationaboutyou'/></strong>
              	</div>                
        	</div>
        	<div class="workspace-accordion" id="workspace-accordion">        		
        		<!-- Personal Information -->
				<div id="workspace-personal" class="workspace-accordion-item workspace-accordion-active" ng-controller="PersonalInfoCtrl">        			
       				<div class="row">
       					<div class="col-md-12 col-sm-12 col-xs-12">	 			   			
 			   				<#if RequestParameters['OldPersonal']??>	        
      			   		   		<a href="<@spring.url '/account/manage-bio-settings'/>" id="update-personal-modal-link" class="label btn-primary"><@orcid.msg 'workspace.Update'/></a>        			   		
      			        	</#if>	 			   			
 			   			</div>
  			   		</div>        			
            		<div class="workspace-accordion-content" ng-show="displayInfo">
            			<#include "workspace_personal_v3.ftl"/>
        			</div>
            	</div>
            	<!-- Affiliations / Education / Employment -->
                <#include "workspace_affiliations_body_list_v3.ftl"/>
                <!-- Fundings -->
               	<#include "workspace_fundings_body_list_v3.ftl"/>
		        <!-- Works -->                
                <div id="workspace-publications" class="workspace-accordion-item workspace-accordion-active" ng-controller="WorkCtrl" orcid-loaded="{{worksSrvc.worksToAddIds != null && worksSrvc.loading != true}}">
                    <#include "includes/work/work_section_header_inc_v3.ftl"/>
					
					<!-- Bulk Edit -->					
					<div ng-show="bulkEditShow && workspaceSrvc.displayWorks" ng-cloak>						
						<div class="bulk-edit">
							<div class="row">
								<div class="col-md-7 col-sm-7 col-xs-6">
									<h4>Bulk edit</h4><span class="hide-bulk" ng-click="toggleBulkEdit()">Hide bulk edit</span>
									<ol>
										<li>Select works: Click the checkbox beside each work. Use the checkbox to the right to select or deselect all.</li>
										<li>Select editing action: Click the trash can to delete all selected works or click a privacy setting to apply that setting to all selected works.</li>
									</ol>
								</div>
								<div class="col-md-5 col-sm-5 col-xs-6">
									<ul class="bulk-edit-toolbar">
																			
										<li class="bulk-edit-toolbar-item work-multiple-selector"><!-- Select all -->
											<label>SELECT</label>											
											<div id="custom-control-x">
												<div class="custom-control-x" >	
													<div class="dropdown-custom-menu" id="dropdown-custom-menu" ng-click="toggleSelectMenu()">										
														<span class="custom-checkbox-parent">
															<div class="custom-checkbox" id="custom-checkbox" ng-click="swapbulkChangeAll();$event.stopPropagation();" ng-class="{'custom-checkbox-active':bulkChecked == true}"></div>
														</span>										
														<div class="custom-control-arrow" ng-click="toggleSelectMenu(); $event.stopPropagation();"></div>
													</div>
													<div>
														<ul class="dropdown-menu" role="menu" id="special-menu" ng-class="{'block': bulkDisplayToggle == true}">
												          <li><a href="" ng-click="bulkChangeAll(true); bulkDisplayToggle = false;">All selected</a></li>
												          <li><a href="" ng-click="bulkChangeAll(false); bulkDisplayToggle = false;">None selected</a></li>							          							          
												        </ul>			
													</div>
												</div>
											</div>
										</li>
										<li class="bulk-edit-toolbar-item"><!-- Privacy control -->
											<label>EDIT</label>											
											<div class="bulk-edit-privacy-control">
												<@orcid.privacyToggle2 angularModel="groupPrivacy()" 
													    questionClick=""
													    clickedClassCheck=""
														publicClick="setBulkGroupPrivacy('PUBLIC', $event)" 
									                	limitedClick="setBulkGroupPrivacy('LIMITED', $event)" 
									                	privateClick="setBulkGroupPrivacy('PRIVATE', $event)"/>
								 			</div>
								 			<div class="bulk-edit-delete pull-right">
											    <div class="centered">
													<a ng-click="deleteBulkConfirm()" class="ignore toolbar-button edit-item-button" ng-mouseenter="showTooltip('Bulk-Edit')" ng-mouseleave="hideTooltip('Bulk-Edit')">
														<span class="edit-option-toolbar glyphicon glyphicon-trash"></span>
													</a>
													<div class="popover popover-tooltip top bulk-edit-popover" ng-show="showElement['Bulk-Edit'] == true">
		                                             <div class="arrow"></div>
			                                            <div class="popover-content">
			                                                <span>Delete selected works</span>
			                                            </div>
			                                        </div>
												</div>
											</div>
										</li>
									</ul>
								</div>							
							</div>						  
					   </div>
					</div>
					
					<!-- Bibtex Importer Wizard -->
					<div ng-show="showBibtexImportWizard && workspaceSrvc.displayWorks" ng-cloak class="bibtex-box">
						<div class=box-border" ng-show="canReadFiles" ng-cloak>
						   <h4>Link BibTeX</h4><span ng-click="openBibTextWizard()" class="hide-importer">Hide link BibTeX</span>
						   <div class="row full-height-row">
						   	   <div class="col-md-9 col-sm-9 col-xs-8">
								   <p>
								   		<@orcid.msg 'workspace.bibtexImporter.instructions'/>  <a href="http://support.orcid.org/knowledgebase/articles/390530" target="_blank"><@orcid.msg 'workspace.bibtexImporter.learnMore'/></a>.
								   </p> 
							   </div>
							   <div class="col-md-3 col-sm-3 col-xs-4">
							   		<span class="bibtext-options">							   									   		
									    <a class="bibtex-cancel" ng-click="openBibTextWizard()"><@orcid.msg 'workspace.bibtexImporter.cancel'/></a>			       
									    <span class="import-label" ng-click="openFileDialog()"><@orcid.msg 'workspace.bibtexImporter.fileUpload'/></span>							           
										<input id="inputBibtex" type="file" class="upload-button" ng-model="textFiles" accept="*" update-fn="loadBibtexJs()"  app-file-text-reader multiple />
									</span>								    
							   </div>
						   </div>
						</div>						
						<div class="alert alert-block" ng-show="bibtexParsingError">
							<strong><@orcid.msg 'workspace.bibtexImporter.parsingError'/></strong>
						</div>
						<span class="dotted-bar" ng-show="worksFromBibtex.length > 0"></span>
					   	<div ng-repeat="work in worksFromBibtex" ng-cloak class="bottomBuffer">
					   		  <div class="row full-height-row">	  
			        	       	  <div class="col-md-9">
			        	          	<h3 class="workspace-title bibtex-work-title"><span>{{work.title.value}}</span></h3>
			        	          </div>
			        	          <div class="col-md-3 bibtex-options-menu">
			        	          	<ul>
			        	          		<li><a ng-click="rmWorkFromBibtex(work)" class="ignore glyphicon glyphicon-trash" title="Ignore"></a></li>
			        	          		<li><a ng-click="addWorkFromBibtex(work)" class="save glyphicon glyphicon-floppy-disk" title="Save"></a></li>
			        	          	</ul>
		        	          	 </div>
	        	          	 </div>
		        	  	</div>
					</div>
      	            <div ng-show="workspaceSrvc.displayWorks" class="workspace-accordion-content">
	            		<#include "includes/work/add_work_modal_inc.ftl"/>
						<#include "includes/work/del_work_modal_inc.ftl"/>
						<#include "includes/work/body_work_inc_v3.ftl"/>						
	            	</div>
            	</div>
            </div>
        </div>
    </div>    
</div>
</#escape>

<script type="text/ng-template" id="verify-email-modal">	
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h4><@orcid.msg 'workspace.your_primary_email'/></h4>
				<@orcid.msg 'workspace.ensure_future_access'/>
				<br />
				<br />						
				<span class="btn btn-primary" id="modal-close" ng-click="verifyEmail()"><@orcid.msg 'workspace.send_verification'/></span>
				<span class="btn" id="modal-close" ng-click="closeColorBox()"><@orcid.msg 'freemarker.btncancel'/></span>								
			</div>
		</div>		
	</div>		
</script>

<script type="text/ng-template" id="combine-work-template">
	<div class="lightbox-container">
		<div class="row combine-work">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3>Selected work "{{combineWork.title.value}}"				
					<span ng-show="hasCombineableEIs(combineWork)">
						(<span ng-repeat='ie in combineWork.workExternalIdentifiers'>
							<span ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:combineWork.workExternalIdentifiers.length'></span>
					 	</span>)
					</span>				
				</h3>
				<p>Combine with (select one):</p>
				<ul class="list-group">
  					<li class="list-group-item" ng-repeat="group in worksSrvc.groups | orderBy:sortState.predicate:sortState.reverse" ng-show="combineWork.putCode.value != group.getDefault().putCode.value && validCombineSel(combineWork,group.getDefault())">
						<strong>{{group.getDefault().title.value}}</strong>
						<a ng-click="combined(combineWork,group.getDefault())" class="btn btn-primary pull-right bottomBuffer">Combine</a>

					</li>  					
				</ul>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<span class="btn close-button pull-right" id="modal-close" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel'/></span>
			</div>
		</div>
	</div>
	
</script>






<script type="text/ng-template" id="verify-email-modal-sent">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<h4><@orcid.msg 'workspace.sent'/></h4>
				<@orcid.msg 'workspace.check_your_email'/><br />
				<br />
				<span class="btn" ng-click="closeColorBox()"><@orcid.msg 'freemarker.btnclose'/></span>
			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" id="claimed-record-thanks">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<strong><@spring.message "orcid.frontend.web.record_claimed"/></strong><br />
				<br />
				<button class="btn" ng-click="close()"><@spring.message "freemarker.btnclose"/></button>
			</div>
		</div>
	</div>
</script>
	
<script type="text/ng-template" id="claimed-record-thanks-source-grand-read">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<strong><@spring.message "orcid.frontend.web.record_claimed"/></strong><br />
				<br />
				<strong ng-bind="sourceGrantReadWizard.displayName"></strong> <@spring.message "orcid.frontend.web.record_claimed.would_like"/><br />
				<br />
				<button class="btn btn-primary" ng-click="yes()"><@spring.message "orcid.frontend.web.record_claimed.yes_go_to" /></button>
				<button class="btn" ng-click="close()"><@spring.message "orcid.frontend.web.record_claimed.no_thanks" /></button>
			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" id="delete-external-id-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<h3><@orcid.msg 'manage.deleteExternalIdentifier.pleaseConfirm'/> {{removeExternalModalText}} </h3>
				<button class="btn btn-danger" ng-click="removeExternalIdentifier()"><@orcid.msg 'freemarker.btnDelete'/></button> 
				<a ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel'/></a>
			<div>
		<div>
	<div>	
</script>

<script type="text/ng-template" id="bulk-delete-modal">
	<div class="lightbox-container bulk-delete-modal">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<h3><@orcid.msg 'groups.bulk_delete.confirm.header'/></h3>
				<div class="orcid-error">
					<p>
						<@orcid.msg 'groups.bulk_delete.confirm.line_1'/>
					</p>
					<p>
						<@orcid.msg 'groups.bulk_delete.confirm.line_2'/>
					</p>
					<p ng-class="{'red-error':bulkDeleteSubmit == true}">
    	            	<@orcid.msg 'groups.bulk_delete.confirm.line_3'/> <input ng-class="{'red-border-error':bulkDeleteSubmit == true}" type="text" size="3" ng-init="delCountVerify=0" ng-model="delCountVerify"/>
					</p>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">	
				<div class="right">			
					<a ng-click="closeModal()">
						<@orcid.msg 'freemarker.btncancel'/>
					</a>  <button class="btn blue" ng-click="bulkDeleteFunction()"><@orcid.msg 'freemarker.btnDelete'/></button>
				</div>				
			</div>
		</div>		
	</div>	
</script>


<script type="text/ng-template" id="import-wizard-modal">
    <#if ((workImportWizards)??)>		
    	<div id="third-parties">
			<div class="ie7fix-inner">
			<div class="row">	
				<div class="col-md-12 col-sm-12 col-xs-12">					
					<a class="btn pull-right close-button" ng-click="closeModal()">X</a>
	           		<h1 class="lightbox-title" style="text-transform: uppercase;"><@orcid.msg 'workspace.link_works'/></h1>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
	    	    	<div class="justify">
						<p><@orcid.msg 'workspace.LinkResearchActivities.description'/></p>
					</div>            	    	           	
    		    	<#list workImportWizards?sort_by("displayName") as thirdPartyDetails>
	        	       	<#assign redirect = (thirdPartyDetails.redirectUris.redirectUri[0].value) >
            	   		<#assign predefScopes = (thirdPartyDetails.redirectUris.redirectUri[0].scopeAsSingleString) >
                   		<strong><a ng-click="openImportWizardUrl('<@spring.url '/oauth/authorize?client_id=${thirdPartyDetails.clientId}&response_type=code&scope=${predefScopes}&redirect_uri=${redirect}'/>')">${thirdPartyDetails.displayName}</a></strong><br />
                 		<div class="justify">
							<p>
								${(thirdPartyDetails.shortDescription)!}
							</p>
						</div>
                   		<#if (thirdPartyDetails_has_next)>
	                      	<hr/>
						</#if>
                		</#list>
				</div>
			</div>                 
            <div class="row footer">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<p>
				   		<strong><@orcid.msg 'workspace.LinkResearchActivities.footer.title'/></strong>	    
	        			<@orcid.msg 'workspace.LinkResearchActivities.footer.description1'/> <a href="<@orcid.msg 'workspace.LinkResearchActivities.footer.description.url'/>"><@orcid.msg 'workspace.LinkResearchActivities.footer.description.link'/></a> <@orcid.msg 'workspace.LinkResearchActivities.footer.description2'/>
			    	</p>
				</div>
	        </div>
		</div>
		</div>
	</#if>
</script>

<script type="text/ng-template" id="import-funding-modal">
    <#if ((fundingImportWizards)??)>		
    	<div id="third-parties">
			<div class="ie7fix-inner">
			<div class="row">	
				<div class="col-md-12 col-sm-12 col-xs-12">					
					<a class="btn pull-right close-button" ng-click="closeModal()">X</a>
	           		<h1 class="lightbox-title" style="text-transform: uppercase;"><@orcid.msg 'workspace.link_funding'/></h1>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
	    	    	<div class="justify">
						<p><@orcid.msg 'workspace.LinkResearchActivities.description'/></p>
					</div>            	    	           	
    		    	<#list fundingImportWizards?sort_by("displayName") as thirdPartyDetails>
	        	       	<#assign redirect = (thirdPartyDetails.redirectUris.redirectUri[0].value) >
            	   		<#assign predefScopes = (thirdPartyDetails.redirectUris.redirectUri[0].scopeAsSingleString) >
                   		<strong><a ng-click="openImportWizardUrl('<@spring.url '/oauth/authorize?client_id=${thirdPartyDetails.clientId}&response_type=code&scope=${predefScopes}&redirect_uri=${redirect}'/>')">${thirdPartyDetails.displayName}</a></strong><br />
                 		<div class="justify">
							<p>
								${(thirdPartyDetails.shortDescription)!}
							</p>
						</div>
                   		<#if (thirdPartyDetails_has_next)>
	                      	<hr/>
						</#if>
                		</#list>
				</div>
			</div>                 
            <div class="row footer">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<p>
				   		<strong><@orcid.msg 'workspace.LinkResearchActivities.footer.title'/></strong>	    
	        			<@orcid.msg 'workspace.LinkResearchActivities.footer.description1'/> <a href="<@orcid.msg 'workspace.LinkResearchActivities.footer.description.url'/>"><@orcid.msg 'workspace.LinkResearchActivities.footer.description.link'/></a> <@orcid.msg 'workspace.LinkResearchActivities.footer.description2'/>
			    	</p>
				</div>
	        </div>
		</div>
		</div>
	</#if>
</script>	
</@protected>
