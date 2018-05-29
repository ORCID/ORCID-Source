<div class="workspace-accordion-header clearfix">
    <div class="row">
        <div class="col-md-3 col-sm-3 col-xs-12">
            <a name='workspace-employments' />
            <a href="" ng-click="workspaceSrvc.toggleEmployment($event)" ng-click="workspaceSrvc.toggleEmployment($event)" class="toggle-text">
                <i class="glyphicon-chevron-down glyphicon x075" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayEmployment==false}"></i>
                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.employment'/> (<span ng-bind="affiliationsSrvc.employments.length"></span>)
            </a>
            <#if !(isPublicProfile??)> 
                <div class="popover-help-container">
                    <i class="glyphicon glyphicon-question-sign"></i>
                    <div id="employment-help" class="popover bottom">
                        <div class="arrow"></div>
                        <div class="popover-content">
                            <p><@orcid.msg 'manage_affiliations_settings.helpPopoverEmployment'/> <a href="${knowledgeBaseUri}/articles/1807525" target="manage_affiliations_settings.helpPopoverEmployment"><@orcid.msg 'common.learn_more'/></a></p>
                        </div>
                    </div>
                </div>
            </#if>                     
        </div>
        <div class="col-md-9 col-sm-9 col-xs-12 action-button-bar" ng-if="workspaceSrvc.displayEmployment">
            <#include "../workspace/workspace_act_sort_menu.ftl"/>  
            <#if !(isPublicProfile??)>
                <ul class="workspace-bar-menu">                         
                    <!-- Link Manually -->
                    <li class="hidden-xs">                  
                        <div class="menu-container" id="add-employment-container">
                            <ul class="toggle-menu">
                                <li ng-class="{'green-bg' : showBibtexImportWizard == true}">       
                                    <span class="glyphicon glyphicon-plus"></span>
                                    <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_employment' />    
                                    <ul class="menu-options employment">                            
                                        <!-- Add Manually -->
                                        <li>                            
                                            <a id="add-employment" href="" class="action-option manage-button two-options" ng-click="addAffiliationModal('employment')">
                                                <span class="glyphicon glyphicon-plus"></span>
                                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                            </a>                
                                         </li>
                                    </ul>
                                 </li>
                          </ul>
                        </div>         
                    </li>
                    <!-- Mobile workaound -->
                    <li class="hidden-md hidden-sm visible-xs-inline">                            
                        <a href="" class="action-option manage-button two-options" ng-click="addAffiliationModal('employment')">
                            <span class="glyphicon glyphicon-plus"></span>
                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                        </a>                
                    </li>
                    
                    
                    
                    
                    
                </ul>
            </#if>
        </div>
    </div>
</div>
