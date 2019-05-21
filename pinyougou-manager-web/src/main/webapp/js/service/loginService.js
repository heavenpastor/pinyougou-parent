app.service("loginService",function ($http) {

    /*这个位置不需要传入id，因为后端代码中所使用的api并不需要传入id就可以获取到元素
    * String name=SecurityContextHolder.getContext().getAuthentication().getName();*/
    this.showUsername = function () {
        //这个位置需要加上../???
       return $http.get('../login/showUsername.do');
    }
})