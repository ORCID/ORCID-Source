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
<head>
    <meta charset="utf-8" />
    <title>${title!"ORCID"}</title>
    <meta name="description" content="">
    <meta name="author" content="">

    <link rel="stylesheet" href="<@spring.url '/static/ORCID-Fonts-Dot-Com/style.css?v=${ver}'/>"/>
    <link rel="stylesheet" href="<@spring.url '/static/css/orcid.css?v=${ver}'/>"/>
    <link rel="stylesheet" href="<@spring.url '/static/css/java.css?v=${ver}'/>"/>
    <link rel="stylesheet" href="<@spring.url '/static/css/jquery-ui-1.10.0.custom.min.css?v=${ver}'/>"/>

    <link rel="shortcut icon" href="<@spring.url '/static/img/favicon.ico'/>"/>
    <link rel="apple-touch-icon" href="<@spring.url '/static/img/apple-touch-icon.png'/>" />

	<script src="<@spring.url '/static/javascript/modernizr.js?v=${ver}'/>"></script>
    <#include "/layout/google_analytics.ftl">
    
    <!--[if lte IE 8]>
    	<script src="//cdnjs.cloudflare.com/ajax/libs/json3/3.2.4/json3.min.js"></script>
     <![endif]-->
</head>
