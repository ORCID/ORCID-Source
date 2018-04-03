<@public classes=['home']>
<script type="text/ng-template" id="search-ng2-template">
    <div class="row" id="SearchCtrl" data-search-query="${searchQuery?html}">
    <div class="centered">
        <span id="ajax-loader-search" class="orcid-hide"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
    </div>
    <#include "includes/search/search_results_ng2.ftl"/>
</script>
<search-ng2></search-ng2>
</@public>