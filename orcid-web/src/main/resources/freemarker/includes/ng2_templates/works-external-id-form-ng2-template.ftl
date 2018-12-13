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

<script type="text/ng-template" id="works-external-id-form-ng2-template">
    <div class="add-work colorbox-content" *ngIf="externalIdType">
            <div class="lightbox-container-ie7"> 
                <div class="row">           
                    <div class="col-md-9 col-sm-8 col-xs-9">    
                        <h1 class="lightbox-title pull-left">
                            <div>
                                <@orcid.msg 'manual_work_form_contents.add_work'/>
                            </div>
                        </h1>
                    </div>          
                </div>
                <div class="row">           
                    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"  *ngIf="loading"></i> 
                    <div class="col-md-9 col-sm-8 col-xs-9" *ngIf="!loading">    
                        <strong> Add work from {{externalIdType}}</strong>
                        <div> Type or paste the full {{externalIdType}} URL or just the identifier value</div>
                        <input id="work-title" name="familyNames" type="text" class="form-control" [(ngModel)]="externalId[externalIdType].value" placeholder="{{externalId[externalIdType].placeHolder}}"/>
                        <div class="orcid-error" *ngIf="serverError">
                            The value you entered is not a valid {{externalIdType}}. Pleas enter a valid {{externalIdType}}.
                        </div>
                        <div class="buttons-container">
                            <button class="btn btn-primary" (click)="addWork()" [disabled]="addingWork" [ngClass]="{disabled:addingWork}">
                               Retrive work details 
                                <!--  <@orcid.msg 'freemarker.btnsave'/>  -->
                            </button>
                            <a class="cancel-option" (click)="cancelEdit()"><@orcid.msg 'freemarker.btncancel' /></a>
                        </div>
                    </div>          
                </div>
            </div>
    </div> 
</script>