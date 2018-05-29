<div class="workspace-accordion-header clearfix">
    <div class="row">
        <div class="col-md-4 col-sm-3 col-xs-12">
            <div ng-controller="WorkspaceSummaryCtrl">
                <a href="" ng-click="workspaceSrvc.toggleWorks($event)" class="toggle-text">
                   <i class="glyphicon-chevron-down glyphicon x075" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayWorks==false}"></i>
                   <@orcid.msg 'workspace.Works'/> (<span ng-bind="worksSrvc.groupsLabel"></span>)
                </a>
                <#if !(isPublicProfile??)> 
                    <div class="popover-help-container">
                        <i class="glyphicon glyphicon-question-sign"></i>
                        <div id="works-help" class="popover bottom">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <p><@orcid.msg 'manage_works_settings.helpPopoverWorks'/> <a href="${knowledgeBaseUri}/articles/462032" target="manage_works_settings.helpPopoverWorks"><@orcid.msg 'common.learn_more'/></a></p>
                            </div>
                        </div>
                    </div> 
                </#if>
            </div>
        </div>
        <div class="col-md-8 col-sm-9 col-xs-12 action-button-bar" ng-show="workspaceSrvc.displayWorks">
            <#include "../workspace/workspace_act_sort_menu.ftl"/>
            <#if !(isPublicProfile??)>
                <ul class="workspace-bar-menu">
                    <!-- Bulk Edit -->
                    <li ng-show="worksSrvc.groups.length > 1" ng-cloak>
                        <a class="action-option works manage-button" ng-class="{'green-bg' : bulkEditShow == true}" ng-click="toggleBulkEdit()">
                            <span class="glyphicon glyphicon-pencil"></span><@orcid.msg 'groups.common.bulk_edit'/>
                        </a>
                    </li>

                    <li ng-show="worksSrvc.groups.length > 0" ng-cloak>
                        <a class="action-option works manage-button" ng-class="{'green-bg' : showBibtexExport}" ng-click="toggleBibtexExport()">
                            <span class="glyphicon glyphicon-save"></span>
                            <@orcid.msg 'groups.common.export_works'/>
                        </a>
                    </li>

                    <li class="hidden-xs"><!-- Workaround for mobile view -->
                        <div class="menu-container" id="add-work-container">
                            <ul class="toggle-menu">
                                <li ng-class="{'green-bg' : showBibtexImportWizard == true || workImportWizard == true}"> 
                                    <span class="glyphicon glyphicon-plus"></span>
                                    <@orcid.msg 'groups.common.add_works'/>
                                    <ul class="menu-options works">
                                        <!-- Search & Link -->
                                        <li ng-show="noLinkFlag">
                                            <a ng-show="noLinkFlag" class="action-option manage-button" ng-click="showWorkImportWizard()">
                                                <span class="glyphicon glyphicon-cloud-upload"></span>
                                                <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                            </a>
                                        </li>
                                        <!-- Bibtex -->
                                        <![if gt IE 9]>
                                            <li>
                                                <a class="action-option manage-button" ng-click="openBibTextWizard()">
                                                    <span class="glyphicons file_import bibtex-wizard"></span>
                                                    <@orcid.msg 'workspace.bibtexImporter.link_bibtex'/>
                                                </a>
                                            </li>
                                        <![endif]>
                                        <!-- Add Manually -->
                                        <li>
                                            <a id="add-work" class="action-option manage-button" ng-click="addWorkModal()">
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
                        <a ng-show="noLinkFlag" class="action-option manage-button" ng-click="showWorkImportWizard()">
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
