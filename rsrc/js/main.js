$(document).ready(function () {

    $(window).on('hashchange', function () {
        hash = window.location.hash;
        switch (hash) {
            case "#/main":
                window.location.replace('http://' + domain + ':' + domainPort + '/rsrc/html/main.html');
                break;
        }
    });


    var userRest = {
        login: "/user/login",
        logout: "/user/logout",
    };

    var unitRest = {
        move: "/unit/move",
    };

    $('#btnLogin').click(function (e) {
        e.preventDefault();
        var login = $('#inputLogin').val();
        var pass = $('#inputPassword').val();

        var user = new Object();

        user.login = login;
        user.password = pass;
        $.ajax({
                url: 'http://' + domain + ':' + domainPort + apiRoute + userRest.login,
                type: 'POST',
                data: JSON.stringify(user),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json'
            })
            .always(function (jqXHR) {
                if (jqXHR.status == 200) {
                    token = jqXHR.responseText;
                    document.cookie = "token=" + token;
                    window.location.hash = "/main";
                } else {
                    alert("falhou!! codigo retornado: " + jqXHR.status + ", tratar este código devidamente.");
                }
            });

    });
});
