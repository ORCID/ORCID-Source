/**
 * External consortium controller
 */
angular.module('orcidApp').controller('externalConsortiumCtrl',['$scope', '$compile', function manageConsortiumCtrl($scope, $compile) {    
   $scope.consortium = null;

   $scope.toggleFindConsortiumModal = function() {
       $scope.showFindModal = !$scope.showFindModal;
   };
   
   /**
    * GET
    * */
   $scope.getConsortium = function() {
       $.ajax({
           url: getBaseUri()+'/manage-consortium/get-consortium.json',
           type: 'GET',
           dataType: 'json',
           success: function(data){
               $scope.consortium = data;
               $scope.$apply();
           }
       }).fail(function(error) {
           // something bad is happening!
           console.log("Error getting the consortium");
       });
   };
   
   $scope.confirmUpdateConsortium = function() {
       $.colorbox({
           html : $compile($('#confirm-modal-consortium').html())($scope),
               scrolling: true,
               onLoad: function() {
               $('#cboxClose').remove();
           },
           scrolling: true
       });

       $.colorbox.resize({width:"450px" , height:"175px"});
   };
   
   $scope.updateConsortium = function() {
       $.ajax({
           url: getBaseUri()+'/manage-consortium/update-consortium.json',
           contentType: 'application/json;charset=UTF-8',
           type: 'POST',
           dataType: 'json',
           data: angular.toJson($scope.consortium),
           success: function(data){
               $scope.$apply(function(){
                   if(data.errors.length == 0){
                       $scope.success_edit_member_message = om.get('manage_member.edit_member.success');
                   } else {
                       $scope.consortium = data;
                   }
               });
               $scope.closeModal();
           }
       }).fail(function(error) {
           // something bad is happening!
           console.log("Error updating the consortium");
       });
   };
   
   $scope.closeModal = function() {
       $.colorbox.close();
   };
   
   // Init
   $scope.getConsortium();
   
}]);