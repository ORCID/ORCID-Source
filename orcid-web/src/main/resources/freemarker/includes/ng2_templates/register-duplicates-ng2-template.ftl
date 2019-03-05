<script type="text/ng-template" id="register-duplicates-ng2-template">
    <div *ngIf="!showRegisterProcessing"  class="lightbox-container" id="duplicates-records">
        <div class="row margin-top-box">      
            <div class="col-md-12 col-sm-12 col-xs-12">
                <h4>
                    <@orcid.msg 'duplicate_researcher.wefoundfollowingrecords'/>
                </h4>
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
                            <td><a href="${baseUri}/{{dup.orcid}}" target="_blank">${baseUri}/{{dup.orcid}}</a></td>
                            <td>{{dup.email}}</td>
                            <td>{{dup.givenNames}}</td>
                            <td>{{dup.familyNames}}</td>
                            <td>{{dup['affiliations'].join(", ")}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>          
        <div class="row margin-top-box">
            <div class="col-md-12 col-sm-12 col-xs-12 left">
                <span><@orcid.msg 'duplicate_researcher.notsure' /> <a href="https://orcid.org/help/contact-us" target="public-layout.contact_us"><@orcid.msg 'public-layout.contact_us' /></a></span>
            </div>
        </div>
        <div class="row margin-top-box">
            <div class="col-md-12 col-sm-12 col-xs-12 right">
                <ul class="inline-list margin-separator pull-right">
                    <li>
                        <a class="cancel-option" href="{{getBaseUri()}}/signin" target="_self"><@orcid.msg 'duplicate_researcher.cancel' /></a>
                    </li>
                    <li>
                        <button class="btn btn-primary" (click)="oauth2ScreensPostRegisterConfirm()">
                            <@orcid.msg 'duplicate_researcher.btncontinuetoregistration'/>
                        </button>                            
                    </li>
                </ul>
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