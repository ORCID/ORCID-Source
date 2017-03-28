/*
 * For modal dispalyed if primary email is changed to an unverified email
 */



angular.module('orcidApp').directive(
    'modalUnverifiedEmailSetPrimary', 
    [
        '$compile',
        '$rootScope',
        '$timeout',
        'initialConfigService',
        'emailSrvc',
        function( $compile, $rootScope, $timeout, initialConfigService, emailSrvc ) {

            var configuration = initialConfigService.getInitialConfiguration();

            var closeModal = function(){
                $.colorbox.remove();
                $('modal-unverified-email-set-primary').html('<div id="modal-unverified-email-set-primary-container"></div>');
            };

            var openModal = function( scope ){
                scope.emailPrimary = emailSrvc.getEmailPrimary().value;

                $.colorbox(
                    {
                        html : $compile($('#modal-unverified-email-set-primary-container').html('<div class="lightbox-container" id="modal-email-unverified"><div class="row"><div class="col-md-12 col-xs-12 col-sm-12"><h4>' + om.get("orcid.frontend.workspace.your_primary_email") + '</h4><p>' + om.get("orcid.frontend.workspace.youve_changed") + '</p><p>' + om.get("orcid.frontend.workspace.you_need_to_verify") + '</p><p>' + om.get("orcid.frontend.workspace.ensure_future_access2") +  '<br /><strong>' + scope.emailPrimary + '</strong></p><p>' + om.get("orcid.frontend.workspace.ensure_future_access3") + ' <a target="_blank" href="' + om.get("orcid.frontend.link.url.knowledgebase") + '">' + om.get("orcid.frontend.workspace.ensure_future_access4") + '</a> ' + om.get("orcid.frontend.workspace.ensure_future_access5") + ' <a target="_blank" href="mailto:' + om.get("orcid.frontend.link.email.support") + '">' + om.get("orcid.frontend.link.email.support") + '</a>.</p><div class="topBuffer"><a class="nner-row" ng-click="closeColorBox()">' + om.get("manage.email.close") + '</a></div></div></div></div>'))(scope),
                        escKey: true,
                        overlayClose: true,
                        transition: 'fade',
                        close: '',
                        scrolling: false
                    }
                );
                $.colorbox.resize({width:"500px"});
            };

            function link( scope, element, attrs ) {

                scope.verifyEmail = function() {
                    verifyEmail( scope );
                };

                scope.closeColorBox = function() {
                    closeModal();
                };

                scope.openModal = function( scope ){
                    openModal( scope );
                }

                scope.$on(
                    'unverifiedSetPrimary',
                    function(event, data){
                        if (data.newValue == true && configuration.showModalManualEditVerificationEnabled == true) {
                            scope.openModal( scope ); 
                        }
                        else {
                            scope.closeColorBox(); 
                        }
                    }

                );
            }

            return {
                link: link,
                template: '<div id="modal-unverified-email-set-primary-container"></div>',
                transclude: true
            };

        }
    ]
);