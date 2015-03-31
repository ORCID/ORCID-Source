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
<script type="text/ng-template" id="add-peer-review-modal">
	<div class="add-peer-review colorbox-content">
		<fn-form update-fn="">
			<div class="lightbox-container-ie7">		
			<!-- Title -->
			<div class="row">			
				<div class="col-md-9 col-sm-8 col-xs-9">	
					<h1 class="lightbox-title pull-left">
						<!-- 
						<div ng-show="editWork.putCode.value != null">
							<@orcid.msg 'manual_work_form_contents.edit_work'/>
						</div>
						 -->
						<div>
							Add Peer Review
						</div>
					</h1>
				</div>			
			</div>
	
			<!-- Main content -->		
			<div class="row">
				<!-- Left Column -->			
				<div class="col-md-6 col-sm-6 col-xs-12">	
					<!-- ROLE -->
					<div class="control-group">
			    		<label class="relative">Role</label>			    		
			    		<div class="relative">
				    		<!--<select id="peerReviewRole" name="peerReviewRole" class="input-xlarge" ng-model="something" ng-change="">-->
							<select id="peerReviewRole" name="peerReviewRole" class="input-xlarge">
				    			<option value="">Pick a role</option>								
								<option value="">Reviewer</option>
								<option value="">Editor</option>
								<option value="">Member</option>
								<option value="">Chair</option>
								<option value="">Organizer</option>
							</select> 
							<span class="required" ng-class="">*</span>
							<span class="orcid-error" ng-show="">
								<!-- <div ng-repeat='' ng-bind-html="error"></div> -->
							</span>
						</div>
					</div>
					<!-- TYPE -->
					<div class="control-group">
			    		<label class="relative">Type</label>			    		
			    		<div class="relative">
				    		<!--<select id="peerReviewRole" name="peerReviewRole" class="input-xlarge" ng-model="something" ng-change="">-->
							<select id="peerReviewType" name="peerReviewType" class="input-xlarge">
				    			<option value="">Pick a type</option>								
								<option value="">Review</option>
								<option value="">Evaluation</option>
							</select> 
							<span class="required" ng-class="">*</span>
							<span class="orcid-error" ng-show="">
								<!-- <div ng-repeat='' ng-bind-html="error"></div> -->
							</span>
						</div>
					</div>
					<!-- ORGANIZATION -->
					<div class="form-group">
						<span><strong>ORGANIZATION</strong></span>						
						<!-- Institution -->	
						<div class="relative">
							<div class="control-group">
						    	<label class="relative">Institution</label>
								<!--<input name="institution" type="text" class="input-xlarge"  ng-model="something" placeholder="Add institution" ng-change="" ng-model-onblur/>-->
								<input name="institution" type="text" class="input-xlarge" placeholder="Add institution"/>
								<span class="required" ng-class="">*</span>
								<span class="orcid-error" ng-show="">
									<!-- <div ng-repeat='' ng-bind-html="error"></div> -->
								</span>
							</div>
						</div>
						<!-- City -->
						<div class="control-group">
				    		<label class="relative">City</label>
							<input name="city" type="text" class="input-xlarge" placeholder="Add city"/>
							<span class="required" ng-class="">*</span>
							<span class="orcid-error" ng-show="">
								<!-- <div ng-repeat='' ng-bind-html="error"></div> -->
								<div ng-bind-html="error"></div>
							</span>
						</div>					
						<!-- State -->
						<div class="control-group">
				    		<label class="relative">Country</label>
				    		<div class="relative">
								<select id="country" name="country">
		                            <option value=""><@orcid.msg 'org.orcid.persistence.jpa.entities.CountryIsoEntity.empty' /></option>
		                            <#list isoCountries?keys as key>
		                                    <option value="${key}">${isoCountries[key]}</option>
		                            </#list>
		                        </select>
							<span class="required" ng-class="">*</span>
							</div>
							<span class="orcid-error" ng-show="">
								<!-- <div ng-repeat='' ng-bind-html="error"></div> -->
								<div ng-bind-html="error"></div>
							</span>
						</div>
					</div>	
					
					<!-- DATE -->
					<div class="control-group">
			    		<label class="relative" for="">Date</label>
			    		<div class="relative">					    
							<select id="year" name="month" class="col-md-4">
								<#list years?keys as key>
									<option value="${key}">${years[key]}</option>
								</#list>
				    		</select>
						    <select id="month" name="month" class="col-md-3">
								<#list months?keys as key>
									<option value="${key}">${months[key]}</option>
								</#list>
				    		</select>
							<select id="day" name="day"class="col-md-3">
								<#list days?keys as key>
									<option value="${key}">${days[key]}</option>
								</#list>
				    		</select>								    
			    		</div>
					</div>
				</div>
	
				<!-- Right column -->
				<div class="col-md-6 col-sm-6 col-xs-12">
					<!-- External identifiers -->
				    <div class="control-group">
						<span><strong>EXTERNAL IDENTIFIERS</strong></span>
						<div> 
							<div class="control-group">
								<label class="relative">Identifier type</label>
								<div class="relative">
				    				<select id="idType" name="idType" class="input-xlarge">																						 
										<option value=""><@orcid.msg 'org.orcid.jaxb.model.message.WorkExternalIdentifierType.empty' /></option>
										<#list idTypes?keys as key>
											<option value="${idTypes[key]}">${key}</option>
										</#list>
									</select> 
									<a href ng-click="" class="glyphicon glyphicon-trash grey"></a>
									<span class="orcid-error" ng-show="1 == 0">
										<div ng-bind-html="error"></div>
									</span>
								</div>	
							</div>
							<div class="bottomBuffer">
					   			<div class="control-group">
									<label><@orcid.msg 'manual_work_form_contents.labelID'/></label>
							    	<div class="relative">
										<input name="" type="text" class="input-xlarge"  />
										<span class="orcid-error" ng-show="workExternalIdentifier.workExternalIdentifierId.errors.length > 0">
											<div ng-repeat='error in workExternalIdentifier.workExternalIdentifierId.errors' ng-bind-html="error"></div>
										</span>
									</div>
									<div ng-show="$last" class="add-item-link">			
										<span><a href ng-click="addExternalIdentifier()"><i class="glyphicon glyphicon-plus-sign"></i> <@orcid.msg 'manual_work_form_contents.add_external_identifier' /></a></span>
									</div>
								</div>
							</div>
						</div>					
					</div>
					
					<!-- Institution -->	
					<div class="control-group">
				    	<label class="relative">URL</label>
						<!--<input name="institution" type="text" class="input-xlarge"  ng-model="something" placeholder="Add institution" ng-change="" ng-model-onblur/>-->
						<input name="url" type="text" class="input-xlarge" placeholder="Add URL"/>
						<span class="required" ng-class="">*</span>
						<span class="orcid-error" ng-show="">
							<!-- <div ng-repeat='' ng-bind-html="error"></div> -->
						</span>
					</div>
					
					<!-- Subject -->
					
					
				
				
				
				</div>
			</fn-form>			
		</div>		
</script>