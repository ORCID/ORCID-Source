<div class="workspace-accordion-header clearfix">
    <div class="row">
        
        <div class="col-md-4 col-sm-4 col-xs-12">
            <a href="" ng-click="workspaceSrvc.toggleFunding($event)" class="toggle-text">
                <i class="glyphicon-chevron-down glyphicon x075" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayFunding==false}"></i>
                <@orcid.msg 'workspace.Funding'/> (<span ng-bind="fundingSrvc.groups.length"></span>)
            </a>
            <#if !(isPublicProfile??)> 
                <div class="popover-help-container">
                    <i class="glyphicon glyphicon-question-sign"></i>
                    <div id="funding-help" class="popover bottom">
                        <div class="arrow"></div>
                        <div class="popover-content">
                            <p><@orcid.msg 'manage_funding_settings.helpPopoverFunding'/> <a href="${knowledgeBaseUri}/articles/326033" target="manage_funding_settings.helpPopoverFunding"><@orcid.msg 'common.learn_more'/></a></p>
                        </div>
                    </div>
                </div> 
            </#if> 
        </div>
        <div class="col-md-8 col-sm-8 col-xs-12 action-button-bar" ng-if="workspaceSrvc.displayFunding">
            <!-- Sort -->
            <#include "../workspace/workspace_act_sort_menu.ftl"/>
            <#if !(isPublicProfile??)>
                <ul class="workspace-bar-menu">
                    <!-- Link Manually -->
                    <li class="hidden-xs">
                        <div class="menu-container" id="add-funding-container">
                            <ul class="toggle-menu">
                                <li ng-class="{'green-bg' : fundingImportWizard == true}">       
                                    <span class="glyphicon glyphicon-plus"></span>
                                    <@orcid.msgCapFirst 'manual_funding_form_contents.add_grant' />    
                                    <ul class="menu-options funding">
                                        <!-- Search & Link -->
                                        <#if fundingImportWizards?has_content>
                                            <li>                                                
                                                <a class="action-option manage-button" ng-click="showFundingImportWizard()">
                                                    <span class="glyphicon glyphicon-cloud-upload"></span>
                                                    <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                                </a>
                                            </li>
                                        </#if>                      
                                        <!-- Add Manually -->
                                        <li>
                                            <a id="add-funding" class="action-option manage-button" ng-click="addFundingModal()">
                                                <span class="glyphicon glyphicon-plus"></span>
                                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                            </a>
                                        </li>
                                    </ul>
                                </li>                                   
                            </ul>
                        </div>
                    </li>
                    <!-- Mobile Version Workaround -->
                    <!-- Search & Link -->
                    <#if fundingImportWizards?has_content>
                        <li class="hidden-md hidden-sm visible-xs-inline">                                               
                            <a class="action-option manage-button action-funding-mobile" ng-click="showFundingImportWizard()">
                                <span class="glyphicon glyphicon-cloud-upload"></span>
                                <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                            </a>
                        </li>
                    </#if>                      
                    <!-- Add Manually -->
                    <li class="hidden-md hidden-sm visible-xs-inline">
                        <a class="action-option manage-button action-funding-mobile" ng-click="addFundingModal()">
                            <span class="glyphicon glyphicon-plus"></span>
                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                        </a>
                    </li>
                </ul>
            </#if>
        </div>
    </div>
</div>
