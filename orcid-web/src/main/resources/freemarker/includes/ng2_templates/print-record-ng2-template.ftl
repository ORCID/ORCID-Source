<script type="text/ng-template" id="print-record-ng2-template">
    <div class="print-orcid-record">
        <#if ((isPublicProfile)?? && isPublicProfile == true | (locked)?? && locked | (deprecated)?? && deprecated)>
            <a id="printRecord" (click)="printRecord('{{getBaseUri()}}/${(effectiveUserOrcid)!}/print')">
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
        <#else>
            <a id="printRecord" (click)="printRecord('{{getBaseUri()}}/${(effectiveUserOrcid)!}/print')">
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
        </#if>
    </div>
</script>
