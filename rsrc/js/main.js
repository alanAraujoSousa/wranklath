$(document).ready(function () {
    $(window).on('hashchange', function () {
        hash = window.location.hash;
        switch (hash) {
            case "/#/main":
                window.location.replace('http://' + domain + ':' + domainPort + '/html/main.html');
            default
                window.location.replace('http://' + domain + ':' + domainPort + '/index.html');
        }
    });


    var userRest = {
        login: "/user/login",
        logout: "/user/logout",
    };

    var unitRest = {
        move: "/unit/move",
    };

    $('#btnLogin').click(function () {
        var login = $('#inputLogin').val();
        var pass = $('#inputPassword').val();

        var user = new Object();

        user.login = loginInformed;
        user.password = passInformed;
        $.ajax({
                url: 'http://' + domain + ':' + domainPort + apiRoute + userRest.login,
                type: 'POST',
                dataType: 'json',
                data: user
            })
            .always(function (jqXHR) {
                if (jqXHR.status == 200) {
                    token = jqXHR.responseText();
                    document.cookie = "token=" + token;
                    alert(token);
                    window.location.hash = "/#/main";
                } else {
                    alert("falhou!! codigo retornado: " + jqXHR.status + ", tratar este código devidamente.");
                }
            });

    });
});
