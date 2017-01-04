angular.module('orcidApp').controller(
    'NameCtrl', 
    [
        '$scope', 
        '$compile', 
        function NameCtrl($scope, $compile) {
            var vm = this<
            vm.showEdit = false;
            vm.nameForm = null;
            vm.privacyHelp = false;

            vm.toggleEdit = function() {
                vm.showEdit = !vm.showEdit;
            };

            vm.close = function() {
                vm.getNameForm();
                vm.showEdit = false;
            };

            vm.getNameForm = function(){
                $.ajax({
                    url: getBaseUri() + '/account/nameForm.json',
                    dataType: 'json',
                    success: function(data) {
                        vm.nameForm = data;
                        vm.$apply();
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error fetching otherNames");
                });
            };

            vm.setNameForm = function(){
                $.ajax({
                    url: getBaseUri() + '/account/nameForm.json',
                    type: 'POST',
                    data:  angular.toJson($scope.nameForm),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        vm.nameForm = data;
                        if(data.errors.length == 0)
                           vm.close();
                        vm.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("OtherNames.serverValidate() error");
                });
            };

            vm.setNamesVisibility = function(priv, $event) {
                $event.preventDefault();
                vm.nameForm.namesVisibility.visibility = priv;
            };

            vm.getNameForm();
        }
    ]
);
