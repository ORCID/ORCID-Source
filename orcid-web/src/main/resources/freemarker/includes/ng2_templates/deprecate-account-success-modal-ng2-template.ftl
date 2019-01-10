<div id="modalDeprecateAccountSuccess" class="modal">
    <div class="popover-ng2-bck" (click)="cancelEditModal('modalDeprecateAccountSuccess')"></div>
    <div
        class="popover-ng2-content"
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
                <div id="cboxContent" style="float: left;"
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
                            <!--Begin modal content-->      
                            <div class="row">
                                <div class="col-md-12 col-sm-12 col-xs-12 bottomBuffer">
                                    <h2><@orcid.msg 'deprecate_orcid_confirmation_modal.heading' /></h2> 
                                    <p><@orcid.msg 'deprecate_orcid_confirmation_modal.text_1' />&nbsp;<a href="${baseUri}/{{deprecateProfilePojo.deprecatingOrcid}}" target="deprecatingOrcid">${baseUri}/{{deprecateProfilePojo.deprecatingOrcid}}</a>&nbsp;<@orcid.msg 'deprecate_orcid_confirmation_modal.text_2' />&nbsp;<a href="${baseUri}/{{deprecateProfilePojo.primaryOrcid}}">${baseUri}/{{deprecateProfilePojo.primaryOrcid}}</a></p>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <div class="pull-left">
                                        <button class="btn btn-primary cancel-right" (click)="cancelDeprecateModal('modalDeprecateAccountSuccess')"><@orcid.msg 'deprecate_orcid_confirmation_modal.close' /></button>
                                    </div>
                                </div>
                            </div>
                            <!--End modal content-->
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>             