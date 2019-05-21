app.controller('loginController',function ($scope,$controller,loginService) {
    $scope.showUsername=function () {
        loginService.showUsername($scope).success(
            function (response) {
                $scope.loginName=response.loginName;
            }
        )
    }
})