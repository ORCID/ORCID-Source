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
<div id="logo">
    <h1>${springMacroRequestContext.getMessage("header.Openresearchercontributor")}</h1>
</div>
<div id="login-nav">
    <ul class="nav nav-pills">
        <li class=""><a href="<@spring.url '/signin'/>">${springMacroRequestContext.getMessage("header.signin")}</a></li>
    </ul>
</div>
<p class="lead"></p>
<div class="navbar">
    <div class="navbar-inner">
        <div style="width: auto;" class="container">
            <a data-target=".nav-collapse" data-toggle="collapse" class="btn btn-navbar">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <div class="nav-collapse">
                <ul class="nav">
                    <li class="active"><a href="<@spring.url '/'/>">${springMacroRequestContext.getMessage("header.Home")}</a></li>
                    <li><a href="<@spring.url '/register'/>" id='regLink'>${springMacroRequestContext.getMessage("header.register")}</a></li>
                </ul>
                <form action="<@spring.url '/search/quick'/>" class="navbar-search pull-right" method="get">
                    <input type="text" placeholder="Search" class="search-query span2">
                </form>
            </div><!-- /.nav-collapse -->
        </div>
    </div><!-- /navbar-inner -->
</div>