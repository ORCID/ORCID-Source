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

<script type="text/ng-template" id="admin-delegates-ng2-template">
    <div class="workspace-accordion-item">
        <p>
            <a *ngIf="showSection" (click)="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.delegate' /></a>
            <a *ngIf="!showSection" (click)="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.delegate' /></a>
        </p>
        
        <div class="collapsible bottom-margin-small admin-modal" id="delegates_section" style="display:none;">
            <div *ngIf="success">
                <span class="orcid-error" [innerHTML]="request.successMessage"></span>
            </div>
            <div *ngIf="request?.errors?.length > 0">
                <span class="orcid-error" *ngFor='let error of request.errors' [innerHTML]="error"></span><br />
            </div>
            <!-- Managed -->
            <div class="form-group">
                <label for="managed"><@orcid.msg 'admin.delegate.managed.label' /></label>
                <input type="text" id="managed" (keyup.enter)="confirmDelegatesProcess()" placeholder="<@orcid.msg 'admin.delegate.managed.placeholder' />" class="input-xlarge" [(ngModel)]="request.managed.value" (ngModelChange)="checkClaimedStatus('managed')">             
                <a href class="glyphicon glyphicon-ok green" *ngIf="managed_verified"></a>                    
                <div id="invalid-managed" *ngIf="request?.managed?.errors?.length > 0" >
                    <span class="orcid-error" *ngFor='let error of request.managed.errors' [innerHTML]="error"></span><br />
                </div>                          
            </div>              
            <!-- Trusted -->
            <div class="form-group">
                <label for="trusted"><@orcid.msg 'admin.delegate.trusted.label' /></label>
                <input type="text" id="trusted" (keyup.enter)="confirmDelegatesProcess()" placeholder="<@orcid.msg 'admin.delegate.trusted.placeholder' />" class="input-xlarge" [(ngModel)]="request.trusted.value" (ngModelChange)="checkClaimedStatus('trusted')">             
                <a href class="glyphicon glyphicon-ok green" *ngIf="trusted_verified"></a>                    
                <div id="invalid-trusted" *ngIf="request?.trusted?.errors?.length > 0" >
                    <span class="orcid-error" *ngFor='let error of request.trusted.errors' [innerHTML]="error"></span><br />
                </div>                          
            </div>
            <!-- Buttons -->
            <div class="controls save-btns pull-left">
                <span id="bottom-confirm-delegate-profile" (click)="confirmDelegatesProcess()" class="btn btn-primary"><@orcid.msg 'admin.delegate.button'/></span>
            </div>
        </div>
    </div>
</script>