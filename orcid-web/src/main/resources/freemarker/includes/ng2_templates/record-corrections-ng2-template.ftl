<script type="text/ng-template" id="record-corrections-ng2-template">
    <div class="record-corrections-table" *ngIf="currentPage?.recordCorrections?.length > 0" >
        <div class="row heading">
            <div class="col-md-3 col-sm-3 col-xs-3">
                <p class="italic"><@orcid.msg 'record_corrections.date'/></p>    				
            </div>
            <div class="col-md-7 col-sm-6 col-xs-6">
                <p class="italic"><@orcid.msg 'record_corrections.description'/></p>
            </div>
            <div class="col-md-2 col-sm-3 col-xs-3">
                <p class="italic"><@orcid.msg 'record_corrections.num_modified'/></p>
            </div>
        </div>	    		
        <div *ngFor="let element of currentPage.recordCorrections" class="row">
            <div class="col-md-3 col-sm-3 col-xs-3">
                <span>{{element.dateCreated | date:'yyyy-MM-dd HH:mm:ss'}}</span>	    				
            </div>
            <div class="col-md-7 col-sm-6 col-xs-6">
                <span>{{element.description}}</span>
            </div>
            <div class="col-md-2 col-sm-3 col-xs-3">
                <span>{{element.numChanged}}</span>
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-md-6 col-sm-6 col-xs-6">
                <button id="previous" class="btn left" (click)="previousPage()" *ngIf="currentPage.havePrevious"><@orcid.msg 'record_corrections.previous'/></button>
            </div>
            <div class="col-md-6 col-sm-6 col-xs-6">
                <button id="next" class="btn right" (click)="nextPage()" *ngIf="currentPage.haveNext"><@orcid.msg 'record_corrections.next'/></button>
            </div>
        </div>	    		
    </div>	    		
    <div *ngIf="!currentPage?.recordCorrections?.length"> 
        <p class="italic"><@orcid.msg 'record_corrections.no_corrections'/></p>
    </div>     
</script>