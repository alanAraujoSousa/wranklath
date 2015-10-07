var dataBase = new Object();
dataBase.buildings = [];
dataBase.units = [];

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
        $('#page').show();
        $('#page').fadeOut('slow');
        $('.page-loader').fadeIn('fast');
    });

    $('#page').ajaxStop(function () {
        $('#page').fadeIn('fast');
        $('.page-loader').fadeOut('slow');
    });

    $('#refreshEntities').click(function (e) {
        e.preventDefault();
        appendBuildings();
        appendArmys();
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
            .done(function (units) {
                dataBase.units = units;
            })
            .fail(function (xhr, textStatus, errorThrown) {
                console.error('Fail on retrieve userData: ' +
                    textStatus);
            });
    };

    function retrieveBuildingData() {
        $.ajax({
                url: listBuildings,
                type: 'GET',
                headers: {
                    'token': token
                },
            })
            .done(function (buildings) {
                dataBase.buildings = buildings;
            })
            .fail(function (xhr, textStatus, errorThrown) {
                console.error('Fail on retrieve userData: ' + textStatus);
            });
    };

    function appendBuildings() {
        if (dataBase.buildings != null && dataBase.buildings > 0) {
            var table = $("#buildingsTable").find('tbody');
            table.empty();
            for (var town in dataBase.buildings) {
                var row = $('<tr>');
                var id = $('<td>');
                var type = $('<td>');
                var place = $('<td>');
                var date = $('<td>');
                id.append(town.id);
                type.append(town.type);
                place.append(town.place);
                date.append(town.date);
                row.append(id);
                row.append(type);
                row.append(place);
                row.append(date);
                table.append(row);
            }
        }
    }

    Function appendArmys() {
        if (dataBase.units != null && dataBase.units > 0) {
            var table = $("#unitsTables").find('tbody');
            table.empty();
            for (var army in dataBase.units) {
                var row = $('<tr>');
                var id = $('<td>');
                var type = $('<td>');
                var place = $('<td>');
                var qtt = $('<td>');
                id.append(army.id);
                type.append(army.type);
                place.append(army.place);
                qtt.append(army.quantity);
                row.append(id);
                row.append(type);
                row.append(place);
                row.append(qtt);
                table.append(row);
            }
        }
    }
});
