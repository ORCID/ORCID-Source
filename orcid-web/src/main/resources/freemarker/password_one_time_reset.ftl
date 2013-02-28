<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@public>
<@spring.bind "passwordTypeAndConfirmForm.*" />			
	<div class="row">
        <div class ="span9 offset3">
        	<p>Please type a new password</p>
        	<#if spring.status.error>
    			<div class="alert alert-error">        			
        			<#list spring.status.errorMessages?sort as error>${error}<#if error_has_next><br/></#if></#list>        			
    			</div>
    		</#if>               
            <form id="reset-password-one-time" name="oneTimeResetForm" action="<@spring.url '/one-time-password/${encryptedEmail}'/>" method="post" autocomplete="off">
                    <div class="control-group password-group">
                        <label for="passwordTypeAndConfirmForm.password" class="control-label">Password</label>
                        <div class="controls">
                            <a class="password-info" href="#"><i class="icon-question-sign"></i></a>
                        	<input id="passwordField" type="password" name="password" value="${(passwordTypeAndConfirmForm.password)!}" class="input-xlarge password-strength" data-validate="{required:true}"/>                            
                            <span class="required">*</span>                            
                        </div>
        	 			<div class="popover bottom password-details">
                        	<div class="arrow"></div>
                        	<div class="popover-content">
	                        	<div class="help-block">
                                        <p>Must be 8 or more characters and contain:</p>
                                        <ul>
                                            <li>at least 1 numeral: 0 - 9</li>
                                            <li>at least 1 of the following:</li>
                                            <ul>
                                            	<li>alpha character, case-sensitive a-Z</li>
                                            	<li>any of the following symbols:<br /> ! @ # $ % ^ * ( ) ~ `{ } [ ] | \ &amp; _</li>
                                            </ul>
                                            <li>optionally the space character, i.e ' ' and other punctuation such as . , ;</li>
                                        </ul>                                       
                                        <p>Example: sun% moon2</p>
                                    </div>
                        	</div>
                        </div>
                        <label for="passwordTypeAndConfirmForm.retypedPassword" class="control-label">Confirm password</label>
                         <div class="controls">                         	
                         	<input id="passwordField" type="password" name="retypedPassword" value="${(passwordTypeAndConfirmForm.retypedPassword)!}" class="input-xlarge" data-validate="{required:true}"/>                            
                            <span class="required">*</span>                           
                        </div>    
                        <div class="controls">
        					<button id="bottom-submit-password-change" class="btn btn-primary" type="submit">Save changes</button>    
        				</div>
    				</div>	
	         </form>
        </div>
    </div>                    
</@public>