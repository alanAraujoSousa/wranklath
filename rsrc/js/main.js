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

    function retrieveUnitData() {
        var token = document.cookie.substring(6);
        $.ajax({
                url: listUnits,
                type: 'GET',
                headers: {
                    'token': token
                },
            })
            .done(function (units) {
                dataBase.units = units;
                return units;
            })
            .fail(function (xhr, textStatus, errorThrown) {
                console.error('Fail on retrieve userData: ' +
                    textStatus);
            });
    };

    function retrieveBuildingData() {
        var token = document.cookie.substring(6);
        $.ajax({
                url: listBuildings,
                type: 'GET',
                headers: {
                    'token': token
                },
            })
            .done(function (buildings) {
                dataBase.buildings = buildings;
                return buildings;
            })
            .fail(function (xhr, textStatus, errorThrown) {
                console.error('Fail on retrieve userData: ' + textStatus);
            });
    };

    function appendResources() {
        appendUnits(dataBase.units);
        appendBuildings(dataBase.buildings);
    }

    function appendBuildings(buildings) {
        if (buildings != null && buildings.length > 0) {
            var table = $("#buildingsTable").find('tbody');
            table.empty();
            for (var i = 0; i < buildings.length; i++) {
                town = buildings[i];
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

    function appendUnits(units) {
        if (units != null && units.length > 0) {
            var table = $("#unitsTable").find('tbody');
            table.empty();
            for (var i = 0; i < units.length; i++) {
                army = units[i];
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

    function startSchedulers() {
        setInterval(retrieveUnitData, 2000);
        setInterval(retrieveBuildingData, 2000);
        setInterval(appendResources, 1000);
    }

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
                    startSchedulers();
                } else {
                    console.log("falhou!! codigo retornado: " + jqXHR.status + ", tratar este código devidamente no login.");
                }
            });

    });
});
