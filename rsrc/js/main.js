$(document).ready(function () {
    var dataBase = new Object();
    dataBase.buildings = [];
    dataBase.units = [];

    var terrains = new Image();
    var world = new Array(2500);
    var paper;

    $(window).on('hashchange', function () {
        hash = window.location.hash;
        switch (hash) {
            case "#main":
                $("#page").load(htmlRepo + '/main.html');
                retrieveBuildingData();
                retrieveUnitData();
                startSchedulers();
                generateInitialMap();
                break;
            case "":
                $("#page").load('index.html');
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

    function getCookie(cookieName) {
        var cookie = document.cookie;
        cookieName += "=";
        var index = cookie.indexOf(cookieName);
        if (index < 0)
            return null;
        index += cookieName.length;
        cookie += ";";
        cookie = cookie.substr(index);
        index = cookie.indexOf(";");
        cookie = cookie.substr(0, index);
        return cookie;
    }

    function retrieveUnitData() {
        var token = getCookie("token");
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
                console.error('Fail on retrieve units of user, error: ' +
                    errorThrown);
            });
    };

    function retrieveBuildingData() {
        var token = getCookie("token");
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
                console.error('Fail on retrieve buildings of user, error: ' + errorThrown);
            });
    };

    function login(userObject) {
        var token = getCookie("token");
        if (token == null) {
            $.ajax({
                    url: loginRest,
                    type: 'POST',
                    data: JSON.stringify(userObject),
                    contentType: 'application/json; charset=utf-8',
                    dataType: 'json'
                })
                .always(function (jqXHR) {
                    if (jqXHR.status == 200) {
                        token = jqXHR.responseText;
                        document.cookie = "token=" + token;
                    } else {
                        console.error("falhou!! codigo retornado: " + jqXHR.status + ", tratar este código devidamente no login.");
                    }
                });
        } else {
            console.log("Already token.");
        }
    }

    function startSchedulers() {
        setInterval(retrieveUnitData, 2000);
        setInterval(retrieveBuildingData, 2000);
    }

    function timeConverter(UNIX_timestamp) {
        var date = new Date(UNIX_timestamp);
        year = date.getFullYear();
        month = date.getMonth() + 1;
        day = date.getDate();
        hour = date.getHours();
        min = date.getMinutes();
        sec = date.getSeconds();
        var time = day + '/' + month + '/' + year + ' ' + hour + ':' + min + ':' + sec;
        return time;
    }

    $('#btnLogin').click(function (e) {
        e.preventDefault();
        var login = $('#inputLogin').val();
        var pass = $('#inputPassword').val();
        var user = new Object();
        user.login = login;
        user.password = pass;
        login(user);
        var token = getCookie("token");
        if (token != null) {
            window.location.hash = "main";
            startSchedulers();
        }
    });

    function drawLocalMapArround(initX, initY) {
        var tileWidth = 100;
        var tileHeight = 78;
        canvas = $('<canvas>');
        var ctx = canvas.getContext("2d");
        terrains.onload = function () {
            // Obtain tiles.
            var imageWidth = terrains.width;
            var imateHeight = terrains.height;
            ctx.drawImage(terrains, 0, 0);
            var tilesX = imageWidth / tileWidth;
            var tilesY = imageHeight / tileHeight;
            var totalTiles = tilesX * tilesY;
            var tileData = new Array();
            for (var i = 0; i < tilesY; i++) {
                for (var j = 0; j < tilesX; j++) {
                    tileData.push(ctx.getImageData(j * tileWidth, i * tileHeight, tileWidth, tileHeight));
                }
            }
            // Obtain tiles.

            var worldIndex = fillWorldOfCoordinate(initX, initY);

            /*
            if(initX<8)
            if(initX>4993)

            for (var i = 0; i < 7; i++) {
                for (var j = 0; j < 5; j++) {
                    xm = initX - i;
                    xp = initX + i;
                    ym = initY - j;
                    yp = initY + j;

                }
            }
            */

            paper.set().forEach(function (e, index) {
                var tileId = e.id;
                // TODO if Id is part of the map.
                console.log(index);

                var pieceOfWorld = world[worldIndex];
                // debugger;
                var placeData = pieceOfWorld[index];

                var terrain = findTerrain(initX, initY);

                e.attr({
                    fill: '#000'
                });
            });
        }
        terrains.src("../img/sprites/map/map1.jpg");
    }

    // TODO validate this function.
    function fillWorldOfCoordinate(initX, initY) {
        if (initX % 100 == 0)
            initX--;
        if (initY % 100 == 0)
            initY--;
        var x = initX / 100;
        var y = initY / 100;
        x = Number.parseInt(x);
        y = Number.parseInt(y);
        var index;
        index += x * 50;
        index += y;
        index++;

        var url;
        url = "./map/map" + index + ".json";
        $.getJSON(url, function (data) {
            world[index] = data;
        });
        return index;
    }

    function findTerrain(code) {
        // some calc;
        return terrains[0];
    }

    function generateInitialMap() {
        var width = 1500;
        var height = 800;
        paper = Raphael($('#map'), 0, width, height);
        for (var x = 0; x < 100; x++) {
            for (var y = 0; y < 100; y++) {
                var rect = paper.rect(x * gridCellSize, y * gridCellSize, gridCellSize, gridCellSize);
                rect.id = x + ":" + y;
            }
        }
        var towns = dataBase.buildings;
        if (towns != null && towns.length > 0) {
            var initialPlace = towns[0].place;
            drawLocalMapArround(initialPlace.x, initialPlace.y);
        }
    }
});
