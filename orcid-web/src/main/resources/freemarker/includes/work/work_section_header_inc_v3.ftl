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
<div class="workspace-accordion-header">
    <div class="row">
        <div class="col-md-3 col-sm-2 col-xs-12">
            <div class="workspace-title" ng-controller="WorkspaceSummaryCtrl">
                <a href="" ng-click="workspaceSrvc.toggleWorks($event)" class="toggle-text">
                   <i class="glyphicon-chevron-down glyphicon x075" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayWorks==false}"></i>
                   <@orcid.msg 'workspace.Works'/> (<span ng-bind="worksSrvc.groups.length"></span>)
                </a>
            </div>
        </div>
        <div class="col-md-9 col-sm-10 col-xs-12 action-button-bar" ng-show="workspaceSrvc.displayWorks">
            <#include "../workspace/workspace_act_sort_menu.ftl"/>
            <#if !(isPublicProfile??)>
                <ul class="workspace-bar-menu">
                    <!-- Bulk Edit -->
                    <li>
                        <a class="action-option works manage-button" ng-class="{'green-bg' : bulkEditShow == true}" ng-click="toggleBulkEdit()">
                            <span class="glyphicon glyphicon-pencil"></span><@orcid.msg 'groups.common.bulk_edit'/>
                        </a>
                    </li>
                    <li class="hidden-xs"><!-- Workaround for mobile view -->
                        <div class="menu-container">
                            <ul class="toggle-menu">
                                <li ng-class="{'green-bg' : showBibtexImportWizard == true}"> 
                                    <span class="glyphicon glyphicon-plus"></span>
                                    <@orcid.msg 'groups.common.add_works'/>
                                    <ul class="menu-options works">
                                    	<!-- Search & Link -->
                                        <li>
                                            <a class="action-option manage-button" ng-click="showWorkImportWizard()">
                                                <span class="glyphicon glyphicon-cloud-upload"></span>
                                                <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                            </a>
                                        </li>
                                        <!-- Bibtex -->
                                        <li>
                                            <a class="action-option manage-button" ng-click="openBibTextWizard()">
                                                <span class="glyphicons file_import bibtex-wizard"></span>
                                                <@orcid.msg 'workspace.bibtexImporter.link_bibtex'/>
                                            </a>
                                        </li>                            
                                        <!-- Add Manually -->
                                        <li>
                                            <a class="action-option manage-button" ng-click="addWorkModal()">
                                                <span class="glyphicon glyphicon-plus"></span>
                                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                            </a>
                                        </li>                                                                                
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </li>
                    <!-- Search & Link -->
                    <li class="hidden-md hidden-sm visible-xs-inline">
                        <a class="action-option manage-button" ng-click="showWorkImportWizard()">
                            <span class="glyphicon glyphicon-cloud-upload"></span>
                            <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                        </a>
                    </li>
                    <!-- Bibtex -->
                    <li class="hidden-md hidden-sm visible-xs-inline">
                        <a class="action-option manage-button" ng-click="openBibTextWizard()">
                            <span class="glyphicons file_import bibtex-wizard"></span>
                            <@orcid.msg 'workspace.bibtexImporter.link_bibtex'/>
                        </a>
                    </li>
                    <!-- Mobile Workaround -->
                    <li class="hidden-md hidden-sm visible-xs-inline">
                        <a class="action-option manage-button" ng-click="addWorkModal()">
                            <span class="glyphicon glyphicon-plus"></span>
                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                        </a>
                    </li>                                        
                </ul>
            </#if>
        </div>
    </div>
</div>
