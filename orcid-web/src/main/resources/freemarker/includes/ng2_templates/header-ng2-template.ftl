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
<#macro nav></#macro>
<script type="text/ng-template" id="header-ng2-template">
    <div class="header center">
        <div class="row">
            <div class="search col-md-11 col-md-offset-1 col-sm-12 col-xs-12"
                id="search" *ngIf="searchVisible == true || settingsVisible == true" >
                <!-- Search Form  -->               
                <form id="form-search" action='<@orcid.rootPath "/search/node" />' method="POST" *ngIf="searchVisible == true" >
                    <div id="search-box">
                        <input type="search" id="search-input" name="keys"
                            (focus)="searchFocus()" (blur)="searchBlur()"
                            placeholder="<@orcid.msg 'public-layout.search'/>" />
                    </div>

                    <div class="bar">
                        <fieldset class="search_options" *ngIf="filterActive == true"
                            >
                            <input type="radio" name="huh_radio" id="filter_registry"
                                value="registry" (click)="focusActive()" checked /> <label
                                for="filter_registry"><@orcid.msg
                                'public-layout.search.choice.registry'/></label> <input type="radio"
                                name="huh_radio" id="filter_website" value="website"
                                (click)="focusActive()" /> <label for="filter_website"><@orcid.msg
                                'public-layout.search.choice.website'/></label>
                        </fieldset>
                    </div>


                    <div class="conditions" *ngIf="conditionsActive == true" >
                        <p>                         
                            <@orcid.msg 'public-layout.search.terms1'/><a
                                href="${aboutUri}/legal"><@orcid.msg
                                'public-layout.search.terms2'/></a><@orcid.msg
                            'public-layout.search.terms3'/>
                        </p>
                    </div>

                    <div class="top-buttons">
                        <button type="submit" class="search-button">
                            <i class="icon-orcid-search"></i>
                        </button>
                        <a href="<@orcid.rootPath "/orcid-search/search" />"
                        class="settings-button" title="<@orcid.msg
                        'public-layout.search.advanced'/>"><i class="glyphicon glyphicon-cog"></i></a>
                    </div>
                </form>
                <div class="language-selector" *ngIf="settingsVisible == true">
                    <!-- Shared component -->
                    <!--
                    <language-ng2></language-ng2>
                    -->
                    <div class="account-settings-mobile-menu">
                        <span class="account-settings-mobile"> 
                            <a ${(nav=="settings")?then('class="active"', '')} href="<@orcid.rootPath '/account'/>">
                                <@orcid.msg 'public-layout.account_setting'/>
                            </a>
                        </span>
                    </div>
                </div>
            </div>


        </div>
    </div><!-- .header -->
</script>