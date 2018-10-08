<div id="claimed-record-thanks" class="modal">
    <div class="popover-ng2-bck" (click)="close('claimed-record-thanks')"></div>
    <div class="popover-ng2-content"
    id="colorbox" 
    role="dialog" 
    style="transition: width 2s, height 2s;"
    tabindex="-1" 
    [ngStyle]="{
    'height': this.elementHeight + 'px',
    'left': 'calc(50% - ' + this.elementWidth/2 + 'px)',
    'top': 'calc(50% - ' + this.elementHeight/2 + 'px)',
    'width': this.elementWidth + 'px'
    }"
>
    <div id="cboxWrapper" 
        [ngStyle]="{
        'height': this.elementHeight + 'px',
        'width': this.elementWidth + 'px'
        }"
    >
        <div>
            <div id="cboxTopLeft" style="float: left;"></div>
            <div id="cboxTopCenter" style="float: left;"
                [ngStyle]="{
                'width': this.elementWidth + 'px'
                }"
            ></div>
            <div id="cboxTopRight" style="float: left;"></div>
        </div>
        <div style="clear: left;">
            <div id="cboxMiddleLeft" style="float: left;"
                [ngStyle]="{
                'height': this.elementHeight + 'px'
                }"
            ></div>
            <div id="cboxContent" style="overflow: auto; float: left;"
                [ngStyle]="{
                    'height': this.elementHeight + 'px',
                    'width': this.elementWidth + 'px'
                }"
            >
                <div id="cboxLoadedContent" style=" overflow: auto;"
                    [ngStyle]="{
                    'height': this.elementHeight + 'px',
                    'width': this.elementWidth + 'px'
                    }"
                >
                    <div class="lightbox-container">
                            <!--lightbox content-->      
                            <div *ngIf="!sourceGrantReadWizard?.url" class="row">
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <p class="topBuffer">
                                        <strong><@spring.message "orcid.frontend.web.record_claimed"/></strong>
                                    </p>
                                    <button class="btn btn-primary" (click)="close('claimed-record-thanks')"><@spring.message "freemarker.btnclose"/></button>
                                </div>
                            </div>
                            <div *ngIf="sourceGrantReadWizard?.url" class="row">
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <p class="topBuffer">
                                        <strong><@spring.message "orcid.frontend.web.record_claimed"/></strong><br />
                                        <br />
                                        <strong>{{sourceGrantReadWizard.displayName}}</strong> <@spring.message "orcid.frontend.web.record_claimed.would_like"/>
                                    </p>
                                    <button class="btn btn-primary" (click)="yes()"><@spring.message "orcid.frontend.web.record_claimed.yes_go_to" /></button>
                                    <button class="btn btn-white-no-border cancel-right" (click)="close('claimed-record-thanks')"><@spring.message "orcid.frontend.web.record_claimed.no_thanks" /></button>
                                </div>
                            </div><!--End lightbox content-->
                        </div><!--End lightbox-container-->
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>             
