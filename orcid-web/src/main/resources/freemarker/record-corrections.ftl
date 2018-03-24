<@public>
	<div class="row">	    
	    <div class="col-md-9 col-md-offset-3">
	    	<h1><@orcid.msg 'record_corrections.heading'/></h1>
	    	<p><@orcid.msg 'record_corrections.a_core'/>&nbsp;<a href="<@orcid.rootPath '/about/trust/home'/>"><@orcid.msg 'record_corrections.orcid_trust'/></a>&nbsp;<@orcid.msg 'record_corrections.principle_is'/></p>
	    	<hr>	    	
	    	<div ng-controller="RecordCorrectionsCtrl">
	    		<div ng-show="currentPage.recordCorrections.length > 0" >
	    			<div class="row heading">
	    				<div class="col-md-3 col-sm-12 col-xs-12">
		    				<p class="italic"><@orcid.msg 'record_corrections.date'/></p>    				
		    			</div>
		    			<div class="col-md-7 col-sm-12 col-xs-12">
		    				<p class="italic"><@orcid.msg 'record_corrections.description'/></p>
		    			</div>
		    			<div class="col-md-2 col-sm-12 col-xs-12">
		    				<p class="italic"><@orcid.msg 'record_corrections.num_modified'/></p>
		    			</div>
	    			</div>	    		
		    		<div ng-repeat="element in currentPage.recordCorrections" class="row">
		    			<div class="col-md-3 col-sm-12 col-xs-12">
		    				<span ng-bind="element.dateCreated | date:'yyyy-MM-dd HH:mm:ss'"></span>	    				
		    			</div>
		    			<div class="col-md-7 col-sm-12 col-xs-12">
		    				<span ng-bind="element.description"></span>
		    			</div>
		    			<div class="col-md-2 col-sm-12 col-xs-12">
		    				<span ng-bind="element.numChanged"></span>
		    			</div>
		    		</div>
		    		<hr>
		    		<div class="row">
		    			<div class="col-md-6 col-sm-6 col-xs-6">
		    				<button id="previous" class="btn left" ng-click="getPreviousPage()" ng-show="currentPage.havePrevious"><@orcid.msg 'record_corrections.previous'/></button>
		    			</div>
		    			<div class="col-md-6 col-sm-6 col-xs-6">
		    				<button id="next" class="btn right" ng-click="getNextPage()" ng-show="currentPage.haveNext"><@orcid.msg 'record_corrections.next'/></button>
		    			</div>
		    		</div>	    		
	    		</div>	    		
	    		<div ng-show="currentPage == null || currentPage.recordCorrections == null || currentPage.recordCorrections.length <= 0"> 
	    			<p class="italic"><@orcid.msg 'record_corrections.no_corrections'/></p>
	    		</div>                               
	    	</div>	    		    	
	    </div>
    </div>
</@public>