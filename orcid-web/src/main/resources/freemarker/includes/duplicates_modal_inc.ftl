<@orcid.checkFeatureStatus featureName='SEARCH_RESULTS_AFFILIATIONS'> 
    <script type="text/ng-template" id="duplicates">
        <div class="lightbox-container" id="duplicates-records">
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
                            <tr ng-repeat='dup in duplicates'>
                                <td><a href="${baseUri}/{{dup.orcid}}" target="_blank">${baseUri}/{{dup.orcid}}</a></td>
                                <td>{{dup.email}}</td>
                                <td>{{dup.givenNames}}</td>
                                <td>{{dup.familyNames}}</td>
                                <td ng-bind="getAffiliations(dup)">{{dup['affiliations']}}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>  
            <div class="row margin-top-box">
                <div class="col-md-12 col-sm-12 col-xs-12 left">
                    <span><@orcid.msg 'duplicate_researcher.notsure' /></span>
                </div>
            </div>
            <div class="row margin-top-box">
                <div class="col-md-12 col-sm-12 col-xs-12 right">
                    <ul class="inline-list margin-separator pull-right">
                        <li>
                            <a class="cancel-option" href="${baseUri}/signin" target="_self"><@orcid.msg 'duplicate_researcher.cancel' /></a>
                        </li>
                        <li>
                            <button class="btn btn-primary" ng-click="oauth2ScreensPostRegisterConfirm()">
                                <@orcid.msg 'duplicate_researcher.btncontinuetoregistration'/>
                            </button>                            
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </script>
</@orcid.checkFeatureStatus>
<@orcid.checkFeatureStatus featureName='SEARCH_RESULTS_AFFILIATIONS' enabled=false> 
    <script type="text/ng-template" id="duplicates">
        <div class="lightbox-container" id="duplicates-records">
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
                                <th><@orcid.msg 'duplicate_researcher.thInstitution'/></th>                
                            </tr>
                        </thead>
                        <tbody>
                            <tr ng-repeat='dup in duplicates'>
                                <td><a href="${baseUri}/{{dup.orcid}}" target="_blank">${baseUri}/{{dup.orcid}}</a></td>
                                <td>{{dup.email}}</td>
                                <td>{{dup.givenNames}}</td>
                                <td>{{dup.familyNames}}</td>
                                <td>{{dup.institution}}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>  
            <div class="row margin-top-box">
                <div class="col-md-12 col-sm-12 col-xs-12 left">
                    <span><@orcid.msg 'duplicate_researcher.notsure' /></span>
                </div>
            </div>
            <div class="row margin-top-box">
                <div class="col-md-12 col-sm-12 col-xs-12 right">
                    <ul class="inline-list margin-separator pull-right">
                        <li>
                            <a class="cancel-option" href="${baseUri}/signin" target="_self"><@orcid.msg 'duplicate_researcher.cancel' /></a>
                        </li>
                        <li>
                            <button class="btn btn-primary" ng-click="oauth2ScreensPostRegisterConfirm()">
                                <@orcid.msg 'duplicate_researcher.btncontinuetoregistration'/>
                            </button>                            
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </script>
</@orcid.checkFeatureStatus>          