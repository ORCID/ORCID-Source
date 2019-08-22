<script type="text/ng-template" id="statistics-ng2-template">
<div id="statistics">
    <div class="row">       
        <div class="col-md-9 col-md-offset-3">
            <h1><@orcid.msg 'statistics.header'/></h1>
            <div *ngIf="stats?.liveIds"> 
                <table class="table table-bordered settings-table normal-width">
                    <tbody>
                        <!--LIVE IDS-->
                        <tr *ngIf="stats?.liveIds">
                            <td><@orcid.msg 'statistics.live_ids'/></td>
                            <td class="stat-data">{{stats?.liveIds}}</td>
                        </tr>
                        <!--EXTERNAL IDS-->
                        <tr *ngIf="stats?.idsWithExternalId">
                            <td><@orcid.msg 'statistics.ids_with_external_id'/><br><@orcid.msg 'statistics.ids_with_external_id.subtext'/></td>
                            <td class="stat-data">{{stats?.idsWithExternalId}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!--PERSON ID-->
            <div *ngIf="stats?.idsWithPersonId"> 
                <h2><@orcid.msg 'statistics.person_identifiers'/></h2>
                <table class="table table-bordered settings-table normal-width">
                    <tbody>
                        <tr>
                            <td><@orcid.msg 'statistics.ids_with_person_id'/></td>
                            <td class="stat-data">{{stats?.idsWithPersonId}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_person_id'/></td>
                            <td class="stat-data">{{stats?.numPersonIds}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!--EMPLOYMENT-->
            <div *ngIf="stats?.idsWithEmployment"> 
                <h2><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.employment'/></h2>
                <table class="table table-bordered settings-table normal-width">
                    <tbody>
                        <tr>
                            <td><@orcid.msg 'statistics.ids_with_employment'/></td>
                            <td class="stat-data">{{stats?.idsWithEmployment}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_employment'/></td>
                            <td class="stat-data">{{stats?.numEmployments}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_employment_unique_org'/></td>
                            <td class="stat-data">{{stats?.employmentUniqueOrgs}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!--EDUCATION AND QUALIFICATIONS-->
            <div *ngIf="stats?.idsWithEducationQualification"> 
                <h2><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.education_qualification'/></h2>
                <table class="table table-bordered settings-table normal-width">
                    <tbody>
                        <tr>
                            <td><@orcid.msg 'statistics.ids_with_education_qualification'/></td>
                            <td class="stat-data">{{stats?.idsWithEducationQualification}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_education_qualification'/></td>
                            <td class="stat-data">{{stats?.numEducationQualifications}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_education_qualification_unique_org'/></td>
                            <td class="stat-data">{{stats?.educationQualificationUniqueOrgs}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!--INVITED POSITIONS AND DISTINCTIONS-->
            <div *ngIf="stats?.idsWithInvitedPositionDistinction"> 
                <h2><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.distinction_invited_position'/></h2>
                <table class="table table-bordered settings-table normal-width">
                    <tbody>
                        <tr>
                            <td><@orcid.msg 'statistics.ids_with_invited_position_distinction'/></td>
                            <td class="stat-data">{{stats?.idsWithEducationQualification}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_invited_position_distinction'/></td>
                            <td class="stat-data">{{stats?.numInvitedPositionDistinctions}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_invited_position_distinction_unique_org'/></td>
                            <td class="stat-data">{{stats?.invitedPositionDistinctionUniqueOrgs}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!--MEMBERSHIP AND SERVICE-->
            <div *ngIf="stats?.idsWithMembershipService"> 
                <h2><@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.membership_service'/></h2>
                <table class="table table-bordered settings-table normal-width">
                    <tbody>
                        <tr>
                            <td><@orcid.msg 'statistics.ids_with_membership_service'/></td>
                            <td class="stat-data">{{stats?.idsWithMembershipService}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_membership_service'/></td>
                            <td class="stat-data">{{stats?.numMembershipServices}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_membership_service_unique_org'/></td>
                            <td class="stat-data">{{stats?.membershipServiceUniqueOrgs}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!--FUNDING-->
            <div *ngIf="stats?.idsWithFunding"> 
                <h2><@orcid.msg 'workspace.Funding'/></h2>
                <table class="table table-bordered settings-table normal-width">
                    <tbody>
                        <tr>
                            <td><@orcid.msg 'statistics.ids_with_funding'/></td>
                            <td class="stat-data">{{stats?.idsWithFunding}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_funding'/></td>
                            <td class="stat-data">{{stats?.numFundings}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_funding_unique_org'/></td>
                            <td class="stat-data">{{stats?.fundingUniqueOrgs}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!--PEER REVIEW-->
            <div *ngIf="stats?.idsWithPeerReview"> 
                <h2><@orcid.msg 'workspace_peer_review_body_list.peerReview'/></h2>
                <table class="table table-bordered settings-table normal-width">
                    <tbody>
                        <tr>
                            <td><@orcid.msg 'statistics.ids_with_peer_review'/></td>
                            <td class="stat-data">{{stats?.idsWithPeerReview}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_peer_review'/></td>
                            <td class="stat-data">{{stats?.numPeerReviews}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!--RESEARCH RESOURCES-->
            <div *ngIf="stats?.idsWithResearchResource"> 
                <h2><@orcid.msg 'manage.research_resources'/></h2>
                <table class="table table-bordered settings-table normal-width">
                    <tbody>
                        <tr>
                            <td><@orcid.msg 'statistics.ids_with_research_resource'/></td>
                            <td class="stat-data">{{stats?.idsWithResearchResource}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_research_resource'/></td>
                            <td class="stat-data">{{stats?.numResearchResources}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!--WORKS-->
            <div *ngIf="stats?.idsWithResearchResource"> 
                <h2><@orcid.msg 'workspace.Works'/></h2>
                <table class="table table-bordered settings-table normal-width">
                    <tbody>
                        <tr>
                            <td><@orcid.msg 'statistics.ids_with_works'/></td>
                            <td class="stat-data">{{stats?.idsWithWork}}</td>
                        </tr>
                        <tr>
                            <td><@orcid.msg 'statistics.number_of_works'/></td>
                            <td class="stat-data">{{stats?.numWorks}}</td>
                        </tr>
                        <tr *ngIf="stats?.uniqueDois">
                            <td><@orcid.msg 'statistics.number_of_unique_dois'/></td>
                            <td class="stat-data">{{stats?.uniqueDois}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!--DATE-->
            <div *ngIf="stats?.statisticsDate">
                <p><@orcid.msg 'statistics.statistics_generation_date_label'/>&nbsp;{{stats?.statisticsDate}}<p>
            </div> 
        </div>
    </div>
</div>
</script>