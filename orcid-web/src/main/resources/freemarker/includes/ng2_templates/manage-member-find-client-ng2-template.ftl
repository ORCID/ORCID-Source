<script type="text/ng-template" id="manage-member-find-client-ng2-template">
				<!-- Edit client -->
				<div *ngIf="_client?.clientId">	
					<div class="admin-edit-client">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<h3><@orcid.msg 'admin.edit_client.general'/></h3>
							</div>
						</div>
						<!-- Member id -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<span><@orcid.msg 'manage_groups.group_id'/></span><br />
								<i>{{_client.memberId.value}}</i><br />								
							</div>
						</div>
						<!-- Member name -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<span><@orcid.msg 'manage_groups.group_name'/></span><br />
								<i>{{_client.memberName.value}}</i><br />							
							</div>
						</div>
						<!-- Name -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<span><@orcid.msg 'manage.developer_tools.group.display_name'/></span><br />
								<input type="text" [(ngModel)]="_client.displayName.value" name="displayName" class="full-width-input" />
								<span class="orcid-error" *ngIf="_client.displayName.errors.length > 0">
									<div *ngFor='let error of _client.displayName.errors'>{{error}}</div>
								</span>	
							</div>
						</div>
						<!-- Website -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<span><@orcid.msg 'manage.developer_tools.group.website'/></span><br />
								<input type="text" [(ngModel)]="_client.website.value" name="website" class="full-width-input" />
								<span class="orcid-error" *ngIf="_client.website.errors.length > 0">
									<div *ngFor='let error of _client.website.errors'>{{error}}</div>
								</span>	
							</div>
						</div>
						<!-- IdP-->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<span><@orcid.msg 'manage.developer_tools.client.idp'/></span><br />
								<input type="text" [(ngModel)]="_client.authenticationProviderId.value" class="full-width-input" name="authenticationProviderId" />
								<span class="orcid-error" *ngIf="_client.authenticationProviderId.errors.length > 0">
									<div *ngFor='let error of _client.authenticationProviderId.errors'>{{error}}</div>
								</span>	
							</div>
						</div>
						<!-- Description -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12 dt-description">
								<span><@orcid.msg 'manage.developer_tools.group.description'/></span><br />
								<textarea [(ngModel)]="_client.shortDescription.value"></textarea>
								<span class="orcid-error" *ngIf="_client.shortDescription.errors.length > 0">
									<div *ngFor='let error of _client.shortDescription.errors'>{{error}}</div>
								</span>	
							</div>
						</div>												
						<!-- Client secret -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12 dt-description">
								<span><@orcid.msg 'manage.developer_tools.group.secret'/></span><br />
								<input type="text" [(ngModel)]="_client.clientSecret.value" class="full-width-input" readonly="readonly" #clientSecret (click)="clientSecret.select()"/>								
							</div>
						</div>
						<!-- Persistent tokens -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<input type="checkbox" name="persistentToken" class="small-element middle" [(ngModel)]="_client.persistentTokenEnabled.value" />
								<span class="middle"><@orcid.msg 'manage_member.edit_client.use_pesistent_tokens'/></span>								
								<span class="orcid-error" *ngIf="_client.persistentTokenEnabled.errors.length > 0">
									<div *ngFor='let error of _client.persistentTokenEnabled.errors'>{{error}}</div>
								</span>	
							</div>
						</div>
						

						<div class="row">
						   <div class="col-md-12 col-sm-12 col-xs-12">
								<input type="checkbox" name="oboEnabled" class="small-element middle" [(ngModel)]="_client.oboEnabled.value" />
								<span class="middle"><@orcid.msg 'manage_member.edit_client.use_OBO'/></span>								
								<span class="orcid-error" *ngIf="_client.oboEnabled.errors.length > 0">
									<div *ngFor='let error of _client.oboEnabled.errors'>{{error}}</div>
								</span>	
							</div>
						</div>
						
						<!-- Allow auto deprecate -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<input type="checkbox" name="allowAutoDeprecate" class="small-element middle" [(ngModel)]="_client.allowAutoDeprecate.value" />
								<span class="middle"><@orcid.msg 'manage.developer_tools.group.allow_auto_deprecate'/></span>
							</div>
						</div>
						
						<!-- Redirect uris -->
						<div class="row">			
							<div class="col-md-12 col-sm-12 col-xs-12">
								<h3><@orcid.msg 'admin.edit_client.redirect_uris'/></h3>
							</div>			
						</div>
						<div *ngFor="let rUri of _client.redirectUris; let i = index">
							<div class="admin-edit-client-redirect-uris">
								<div class="row">						
									<!-- URI -->
									<div class="col-md-12 col-sm-12 col-xs-12">
										<input type="text" [(ngModel)]="rUri.value.value" class="input-xlarge">
										<a *ngIf="_client.type.value == 'public-client'" href="" id="delete-redirect-uri" (click)="deleteRedirectUri(i)" class="glyphicon glyphicon-trash grey"></a>
										<a *ngIf="(i===_client.redirectUris.length-1) && _client.type.value == 'public-client'" href="" id="load-empty-redirect-uri" (click)="addRedirectUri()" class="glyphicon glyphicon-plus grey"></a>										
									</div>
								</div>
								<div class="row">
								    <table class="edit-client-table" *ngIf="_client.type.value != 'public-client'">
										<tr>
											<td class="edit-client-table-col">
												<!-- Type -->
												<div class="col-md-6 col-sm-6 col-xs-12">
													<span class="edit-client-labels"><@orcid.msg 'manage_members.label.type'/></span><br>
													<select name="type" class="input-large input-xlarge-full" [(ngModel)]="rUri.type.value" (ngModelChange)="loadDefaultScopes(rUri)">
														<#list redirectUriTypes?keys as key>
															<option [attr.value]="$key">${redirectUriTypes[key]}</option>
														</#list>
													</select>
												</div>
											</td>
											<td class="edit-client-table-col">
												<!-- Scopes -->
												<div class="col-md-4 col-sm-4 col-xs-12">
													<div *ngIf="rUri.type.value != 'default' && scopes">
														<span class="edit-client-labels"><@orcid.msg 'manage_members.label.scope'/></span><br>
														<p-multiSelect maxSelectedLabels="1" [filter]="false" [options]="scopes" [(ngModel)]="rUri.scopes" optionLabel="name">
														</p-multiSelect>
													</div>															
												</div>
											</td>
											<td>
												<!-- Delete button -->
												<div class="col-md-1 col-sm-1 col-xs-12">
													<br>
								    				<a id="delete-redirect-uri" (click)="deleteRedirectUri(i)" class="glyphicon glyphicon-trash grey"></a>
												</div>
											</td>
										</tr>
										<tr>
											<td class="edit-client-table-col">
												<!-- Activity Type-->
												<div class="col-md-6 col-sm-6 col-xs-12">
													<div *ngIf="rUri.type.value == 'import-works-wizard' && rUri.actType.actType">
														<span class="edit-client-labels"><@orcid.msg 'manage_members.label.work_type'/></span><br>
														<p-multiSelect maxSelectedLabels="1" [filter]="false" [options]="actTypeList" [(ngModel)]="rUri.actType.actType.value[rUri.type.value]" optionLabel="name">
														</p-multiSelect>
														<!--  <multiselect multiple="true" [(ngModel)]="rUri.actType.value[rUri.type.value]" options="actType as actType for actType in importWorkWizard['actTypeList']"></multiselect>  -->
													</div>
												</div>
											</td>
											<td class="edit-client-table-col">
												<!-- Geographical Area-->
												<div class="col-md-4 col-sm-4 col-xs-12">
													<div *ngIf="rUri.type.value == 'import-works-wizard'">
														<span class="edit-client-labels"><@orcid.msg 'manage_members.label.geo_area'/></span><br>
														<p-multiSelect maxSelectedLabels="1" [filter]="false" [options]="geoAreaList" [(ngModel)]="rUri.geoArea.geoArea.value[rUri.type.value]" optionLabel="name">
														</p-multiSelect>
														<!--  <multiselect multiple="true" [(ngModel)]="rUri.geoArea.value[rUri.type.value]" options="geoArea as geoArea for geoArea in importWorkWizard['geoAreaList']"></multiselect>  -->
													</div>
												</div>
											</td>
										</tr>
										<tr>
											<td class="edit-client-table-col">
												<!-- Status -->
												<div class="col-md-6 col-sm-6 col-xs-12">
													<span class="edit-client-labels"><@orcid.msg 'manage_members.label.status'/></span><br>
													<select name="type" class="input-large input-xlarge-full" [(ngModel)]="rUri.status">
														<option value="OK"><@orcid.msg 'manage_members.label.status.ok'/></option>
														<option value="RETIRED"><@orcid.msg 'manage_members.label.status.retired'/></option>
													</select>
												</div>
											</td>
										</tr>
										<tr>
											<td class="edit-client-table-col">
												<div *ngIf="isRetiredWizard(rUri)" class="alert alert-info">
													<@orcid.msg 'manage_members.label.status.retiredWizardWarning'/>
												</div>
											</td>
										</tr>
									</table>
									<!-- Add button -->
									<div class="col-md-1 col-sm-1 col-xs-12">
					    				<a  id="load-empty-redirect-uri" (click)="addRedirectUri()" class="glyphicon glyphicon-plus grey" *ngIf="(i===_client.redirectUris.length-1)"></a>
									</div>
								</div>
								<div class="row">
									<!-- Errors -->
									<div class="col-md-12 col-sm-12 col-xs-12">
										<span class="orcid-error" *ngIf="rUri.errors.length > 0">
											<div *ngFor='let error of rUri.errors'>{{error}}</div>
										</span>									
									</div>
								</div>
							</div>						
						</div>
						<div class="row" *ngIf="_client.redirectUris.length == 0 || _client.redirectUris == null">
							<div class="controls save-btns col-md-12 col-sm-12 col-xs-12 margin-top-box margin-bottom-box">
								<a (click)="addRedirectUri()"><@orcid.msg 'manage.developer_tools.edit.add_redirect_uri' /></a>
							</div>
						</div>
						<div class="row">
							<div class="controls save-btns col-md-12 col-sm-12 col-xs-12">
			    				<span id="bottom-confirm-update-client" (click)="confirmUpdateClient()" class="btn btn-primary"><@orcid.msg 'admin.edit_client.btn.update'/></span>
							</div>
						</div>					
					</div>							
				</div>
</script>

