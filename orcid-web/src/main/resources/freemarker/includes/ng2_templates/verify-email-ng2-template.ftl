<div id="verify-email-modal" class="modal">
    <div class="popover-ng2-bck" (click)="close('verify-email-modal')"></div>
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
                            <div class="row">
                                <div class="col-md-12 col-xs-12 col-sm-12">
                                    <!-- New -->
                                    <h4><@orcid.msg 'workspace.your_primary_email_new'/></h4>
                                    <p><@orcid.msg 'workspace.ensure_future_access1'/></p>
                                    <p><@orcid.msg 'workspace.ensure_future_access2'/> <strong>{{primaryEmail}}</strong></p>
                                    <p><@orcid.msg 'workspace.ensure_future_access3'/> <a target="workspace.ensure_future_access4" href="<@orcid.msg 'workspace.link.url.knowledgebase'/>"><@orcid.msg 'workspace.ensure_future_access4'/></a> <@orcid.msg 'workspace.ensure_future_access5'/> <a target="workspace.link.email.support" href="mailto:<@orcid.msg 'workspace.link.email.support'/>"><@orcid.msg 'workspace.link.email.support'/></a>.</p>
                                    <div class="topBuffer">
                                        <button class="btn btn-primary" id="modal-close" (click)="verifyEmail()"><@orcid.msg 'workspace.send_verification_new'/></button>        
                                        <button class="btn btn-white-no-border cancel-right" (click)="close('verify-email-modal')"><@orcid.msg 'freemarker.btncancel'/></button>
                                    </div>
                                </div>
                            </div><!--End lightbox content-->
                        </div><!--End lightbox-container-->
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>             
