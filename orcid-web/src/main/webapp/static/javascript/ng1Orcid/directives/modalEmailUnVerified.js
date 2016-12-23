/*
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */

/*
 * For modal with email verification validation
 */

orcidNgModule.directive(
    'modalEmailUnVerified', 
    [
        '$compile',
        '$rootScope',
        '$timeout',
        function( $compile, $rootScope, $timeout ) {

            var closeModal = function(){
                $.colorbox.remove();
                $('modal-email-un-verified').html('<div id="modal-email-unverified-container"></div>');
            }

            var openModal = function( scope, data ){
                emailVerifiedObj = data;
                $.colorbox(
                    {
                        html : $compile($('#modal-email-unverified-container').html('<div class="lightbox-container" id="modal-email-unverified"><div class="row"><div class="col-md-12 col-xs-12 col-sm-12"><h4>' + om.get("orcid.frontend.workspace.your_primary_email") + '</h4><p>' + om.get("orcid.frontend.workspace.ensure_future_access") + '</p><p>' + om.get("orcid.frontend.workspace.ensure_future_access2") + '<br /><a href="mailto:' + data.emails[0].value + '" target="_blank">' + data.emails[0].value + '</a></p><p>' + om.get("orcid.frontend.workspace.ensure_future_access3") + ' <a target="_blank" href="' + om.get("orcid.frontend.link.url.knowledgebase") + '">' + om.get("orcid.frontend.workspace.ensure_future_access4") + '</a> ' + om.get("orcid.frontend.workspace.ensure_future_access5") + ' <a target="_blank" href="mailto:' + om.get("orcid.frontend.link.email.support") + '">' + om.get("orcid.frontend.link.email.support") + '</a>.</p><button class="btn btn-primary" id="modal-close" ng-click="verifyEmail()">' + om.get("orcid.frontend.workspace.send_verification") + '</button><a class="cancel-option inner-row" ng-click="closeColorBox()">' + om.get("orcid.frontend.freemarker.btncancel") + '</a></div></div></div>'))(scope),
                        escKey: true,
                        overlayClose: true,
                        transition: 'fade',
                        close: '',
                        scrolling: false
                    }
                );
                $.colorbox.resize({height:"250px", width:"500px"});
            }

            var verifyEmail = function( scope ){
                var colorboxHtml = null;
                $.ajax({
                    url: getBaseUri() + '/account/verifyEmail.json',
                    type: 'get',
                    data:  { "email": emailVerifiedObj.emails[0].value },
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        //alert( "Verification Email Send To: " + $scope.emailsPojo.emails[idx].value);
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with multi email");
                });
                
                colorboxHtml = $compile($('#verify-email-modal-sent').html())(scope);

                $.colorbox({
                    html : colorboxHtml,
                    escKey: true,
                    overlayClose: true,
                    transition: 'fade',
                    close: '',
                    scrolling: false
                });
                $.colorbox.resize({height:"200px", width:"500px"});
            }

            function link( scope, element, attrs ) {

                scope.verifyEmail = function() {
                    verifyEmail( scope );
                };

                scope.closeColorBox = function() {
                    closeModal();
                };

                scope.openModal = function( scope, data ){
                    openModal( scope, data );
                }

                scope.$on(
                    'emailVerifiedObj',
                    function(event, data){
                        if (data.flag == false ) {
                            scope.openModal( scope, data ); 
                        }
                        else {
                            scope.closeColorBox(); 
                        }
                    }

                );
            }

            return {
                link: link,
                template: '<div id="modal-email-unverified-container"></div>',
                transclude: true
            };
        }
    ]
);