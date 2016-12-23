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
            $scope.content = {
                btncancel : om.get("orcid.frontend.freemarker.btncancel"),
                emailValue : "",
                ensure_future_access : om.get("orcid.frontend.workspace.ensure_future_access"),
                ensure_future_access2 : om.get("orcid.frontend.workspace.ensure_future_access2"),
                ensure_future_access3 : om.get("orcid.frontend.workspace.ensure_future_access3"),
                ensure_future_access4 : om.get("orcid.frontend.workspace.ensure_future_access4"),
                ensure_future_access5 : om.get("orcid.frontend.workspace.ensure_future_access5"),
                knowledgebase : om.get("orcid.frontend.link.url.knowledgebase"), 
                send_verification : om.get("orcid.frontend.workspace.send_verification"),
                support : om.get("orcid.frontend.link.email.support"),
                your_primary_email : om.get("orcid.frontend.workspace.your_primary_email")
            };

            var closeModal = function(){
                $.colorbox.remove();
                $('modal-email-un-verified').html('<div id="modal-email-unverified-container"></div>');
            };


            var openModal = function( scope, data ){
                content.emailValue = emailVerifiedObj.emails[0].value;
                emailVerifiedObj = data;

                $.colorbox(
                    {
                        //html : $compile( $('#modal-email-unverified-container').html() )(scope),
                        html : $('#modal-email-unverified-container').html(),
                        escKey: true,
                        overlayClose: true,
                        transition: 'fade',
                        close: '',
                        scrolling: false
                    }
                );
                $.colorbox.resize({height:"250px", width:"500px"});
            };

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
            };

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
            };

            return {
                link: link,
                templateUrl: 'modalEmailUnVerified.html',
                transclude: true
            };
        }
    ]
);