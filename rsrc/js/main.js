$(document).ready(function () {

    $(window).on('hashchange', function () {
        hash = window.location.hash;
        switch (hash) {
            case "#main":
                $("#page").load(htmlRepo + '/main.html');
                break;
        }
    });

    $('#page').ajaxStart(function () {
        $('#page').fadeOut('slow');
        $('.page-loader').fadeIn('fast');
    });

    $('#page').ajaxStop(function () {
        $('#page').fadeIn('fast');
        $('.page-loader').fadeOut('slow');
    });

    $('#btnLogin').click(function (e) {
        e.preventDefault();
        var login = $('#inputLogin').val();
        var pass = $('#inputPassword').val();
        var user = new Object();
        user.login = login;
        user.password = pass;
        $.ajax({
                url: loginRest,
                type: 'POST',
                data: JSON.stringify(user),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json'
            })
            .always(function (jqXHR) {
                if (jqXHR.status == 200) {
                    token = jqXHR.responseText;
                    document.cookie = "token=" + token;
                    window.location.hash = "main";
                    console.log(window.location.hash);
                    retrieveUserData();
                } else {
                    console.log("falhou!! codigo retornado: " + jqXHR.status + ", tratar este código devidamente no login.");
                }
            });

    });

    function retrieveUserData() {
        $.ajax({
                url: listUnits,
                type: 'GET',
                headers: {
                    'token': token
                },
            })
            .done(function (data) {
                console.log(data);
            })
            .fail(function (xhr, textStatus, errorThrown) {
                console.log(xhr.responseText);
                console.log(textStatus);
            });

    };

});
