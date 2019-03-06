<script type="text/ng-template" id="widget-ng2-template">
    <div class="widget-container">
        <div class="widget-header">
            <a (click)="toggleCopyWidget();"><span class="glyphicon glyphicon-phone"></span> <@orcid.msg 'orcid_widget.header'/></a>
            <div class="popover-help-container">
                <i class="glyphicon glyphicon-question-sign"></i>
                <div id="widget-help" class="popover bottom">
                    <div class="arrow"></div>
                    <div class="popover-content">
                        <p><@orcid.msg 'orcid_widget.tooltip'/></p>
                    </div>
                </div>
            </div>
        </div>
        <div *ngIf="showCode" class="widget-code-container">
            <p class="widget-instructions"><@orcid.msg 'orcid_widget.copy_message'/></p>
            <textarea id="widget-code-nd" name="widget-code" class="form-control widget-code" (click)="inputTextAreaSelectAll($event)" readonly="readonly">{{widgetURLND}}</textarea>
            <p class="bold"><@orcid.msg 'orcid_widget.widget_preview'/></p>
            <div class="orcid-summary-widget">
                <a id="widget-sample" href="{{getBaseUri()}}/${(effectiveUserOrcid)!}" target="effectiveUserOrcid" rel="noopener noreferrer" style="vertical-align:top;">
                <img src="https://orcid.org/sites/default/files/images/orcid_16x16.png" style="width:1em;margin-right:.5em;" alt="ORCID iD icon">{{domain}}/${(effectiveUserOrcid)!}</a>
            </div> 
            <p><small class="italic"><@orcid.msg 'orcid_widget.widget_preview_text'/></small></p>
            <a (click)="hideWidgetCode()"><@orcid.msg 'orcid_widget.hide_code'/></a>
        </div>
    </div>
</script>