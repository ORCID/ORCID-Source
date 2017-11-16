<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->

<script type="text/ng-template" id="modal-ng2-template">
    <div [hidden]="!showModal" >
        <div class="popover-ng2-bck" (click)="closeModal()"></div>
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

                                <ng-content></ng-content>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div> 
</script>