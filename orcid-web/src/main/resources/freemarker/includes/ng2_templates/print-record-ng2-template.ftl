<script type="text/ng-template" id="print-record-ng2-template">
    <div class="print-orcid-record">
        <div *ngIf="isPublicPage">
            <a id="printRecord" (click)="printRecord()">
            <span class="glyphicon glyphicon-print"></span> ${springMacroRequestContext.getMessage("public_record.printView")}</a>
            <div class="popover-help-container">
                <i class="glyphicon glyphicon-question-sign"></i>
                <div id="print-help" class="popover bottom">
                    <div class="arrow"></div>
                    <div class="popover-content">
                        <p>${springMacroRequestContext.getMessage("public_record.printHelpText")}</p>
                    </div>
                </div>
            </div> 
        </div> 
        <div *ngIf="!isPublicPage">
            <a id="printRecord" (click)="printRecord()">
            <span class="glyphicon glyphicon-print"></span> ${springMacroRequestContext.getMessage("workspace.printView")}</a>
            <div class="popover-help-container">
                <i class="glyphicon glyphicon-question-sign"></i>
                <div id="print-help" class="popover bottom">
                    <div class="arrow"></div>
                    <div class="popover-content">
                        <p>${springMacroRequestContext.getMessage("workspace.printHelpText")}</p>
                    </div>
                </div>
            </div>  
        </div>
    </div>
</script>
