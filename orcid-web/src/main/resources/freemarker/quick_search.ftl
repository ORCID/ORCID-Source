<@public classes=['home']>
<script type="text/ng-template" id="search-ng2-template">
    <div class="row" id="SearchCtrl">
    <div class="centered">
        <span *ngIf="searchResultsLoading"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
    </div>
    <#include "includes/search/search_results_ng2.ftl"/>
</script>
<search-ng2></search-ng2>
</@public>