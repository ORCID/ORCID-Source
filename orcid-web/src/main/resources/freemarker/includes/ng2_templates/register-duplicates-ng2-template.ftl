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
<script type="text/ng-template" id="register-duplicates-ng2-template">
    <div *ngIf="!showRegisterProcessing"  class="lightbox-container" id="duplicates-records">
        <div class="row margin-top-box">      
            <div class="col-md-6 col-sm-6 col-xs-12">
                <h4><@orcid.msg 'duplicate_researcher.wefoundfollowingrecords'/>
                    <@orcid.msg 'duplicate_researcher.to_access.1'/><a href="<@orcid.rootPath "/signin" />" target="signin"><@orcid.msg 'duplicate_researcher.to_access.2'/></a><@orcid.msg 'duplicate_researcher.to_access.3'/>
                </h4>
            </div>
            <div class="col-md-6 col-sm-6 col-xs-12 right margin-top-box">
                <button class="btn btn-primary" (click)="oauth2ScreensPostRegisterConfirm()"><@orcid.msg 'duplicate_researcher.btncontinuetoregistration'/></button>
            </div>
        </div>        
        <div class="row">
            <div class="col-sm-12">
                <table class="table">
                    <thead>
                        <tr>                      
                            <th><@orcid.msg 'search_results.thORCIDID'/></th>
                            <th><@orcid.msg 'duplicate_researcher.thEmail'/></th>
                            <th><@orcid.msg 'duplicate_researcher.thgivennames'/></th>
                            <th><@orcid.msg 'duplicate_researcher.thFamilyName'/></th>
                            <th><@orcid.msg 'workspace_bio.Affiliations'/></th>              
                        </tr>
                    </thead>
                    <tbody>
                        <tr *ngFor="let dup of duplicates">
                            <td><a href="${baseUri}/{{dup.orcid}}" target="dup.orcid">${baseUri}/{{dup.orcid}}</a></td>
                            <td>{{dup.email}}</td>
                            <td>{{dup.givenNames}}</td>
                            <td>{{dup.familyNames}}</td>
                            <!--<td ng-bind="getAffiliations(dup)">{{dup['affiliations']}}</td>-->
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>  
        <div class="row margin-top-box">
            <div class="col-md-12 col-sm-12 col-xs-12 right">
                <button class="btn btn-primary" (click)="oauth2ScreensPostRegisterConfirm()"><@orcid.msg 'duplicate_researcher.btncontinuetoregistration'/></button>
            </div>
        </div>
    </div>
    <div *ngIf="showRegisterProcessing"  class="lightbox-container" id="duplicates-records">
        <div style="font-size: 50px; line-height: 300px; text-align:center">
            <@orcid.msg 'common.processing'/>&nbsp;
            <i id="ajax-loader" class="glyphicon glyphicon-refresh spin green"></i>
        </div>
    </div>
</script>      