dataBase = new Object();
dataBase.buildings = [];
dataBase.units = [];

terrains = [];
world = new Array(2500);
map = new Object();
paper = new Object();
panZoom = new Object();

$(document).ready(function () {

    $(window).on({
        hashchange: function () {
            var hash = window.location.hash;
            switch (hash) {
                case "#main":
                    var loc = 'http://' + window.location.host + htmlRepo + '/main.html';
                    var actualLoc = window.location.href;
                    actualLoc = actualLoc.split("#")[0];
                    if (actualLoc != loc) {
                        window.location.href = loc;
                    }
                    break;
                case "":
                    window.location.href = 'http://' + window.location.host + htmlRepo + '/index.html';
                    break;
            }
        },
        load: function () {
            var hash = window.location.href;
            switch (hash) {
                case 'http://' + window.location.host + htmlRepo + '/main.html':
                    mapPageWorkflow();
                    break;
                case "":
                    document.cookie = "";
                    break;
            }
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

    function mapPageWorkflow() {
        var promiseBuild = retrieveBuildingData();
        var promiseUnit = retrieveUnitData();

        $.when(promiseBuild, promiseUnit).done(function (towns, armys) {
            dataBase.buildings = towns[0];
            dataBase.units = armys[0];
            generateInitialMap();
        });
    }

    $('#btnLogin').click(function (e) {
        e.preventDefault();
        var login = $('#inputLogin').val();
        var pass = $('#inputPassword').val();
        var user = new Object();
        user.login = login;
        user.password = pass;
        var promiseLogin = loginAjax(user);
        promiseLogin.always(function (data) {
            document.cookie = "token=" + data.responseText;
            window.location.hash = "main";
        });
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
    };

    function retrieveUnitData() {
        var token = getCookie("token");
        return $.ajax({
            url: listUnits,
            type: 'GET',
            headers: {
                'token': token
            },
        });
    };

    function retrieveBuildingData() {
        var token = getCookie("token");
        return $.ajax({
            url: listBuildings,
            type: 'GET',
            headers: {
                'token': token
            },
        });
    };

    function retriveWorldDataChunck(index) {
        var url;
        url = "../js/map/map" + index + ".json";
        return $.ajax({
            url: url,
            type: 'GET',
            dataType: 'json',
        });
    }

    function loginAjax(userObject) {
        return $.ajax({
            url: loginRest,
            type: 'POST',
            data: JSON.stringify(userObject),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json'
        });
    };

    /*
        function startSchedulers() {
            setInterval(retrieveUnitData, 8000);
            setInterval(retrieveBuildingData, 8000);
    }*/

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
    };

    function drawLocalMapArround(initX, initY) {

        var deltaX = initX;
        var deltaY = initY;
        if (deltaX % 100 == 0)
            deltaX--;
        if (deltaY % 100 == 0)
            deltaY--;
        deltaX = deltaX / 100;
        deltaY = deltaY / 100;
        deltaX = Number.parseInt(deltaX);
        deltaY = Number.parseInt(deltaY);
        var index = 0;
        index += deltaX * 50;
        index += deltaY;
        index++;
        deltaX *= 100;
        deltaY *= 100;

        var promise = retriveWorldDataChunck(index);
        promise.always(function (data) {
            world[index] = data;
            debugger;

            for (var x = 1; x <= 100; x++) {
                var factorX = x + deltaX;
                for (var y = 1; y <= 100; y++) {
                    var factorY = y + deltaY;
                    d3.select('#mapGroup').append("rect")
                        .attr("id", factorX + ":" + factorY)
                        .attr("x", factorX * gridCellSize)
                        .attr("y", factorY * gridCellSize)
                        .attr("width", gridCellSize)
                        .attr("height", gridCellSize)
                        .style("stroke-width", "1px")
                        .style("stroke", "black");
                }
            }
        });
    };

    function getIndexWorldDataChunck(initX, initY) {
        var deltaX = initX;
        var deltaY = initY;
        if (deltaX % 100 == 0)
            deltaX--;
        if (deltaY % 100 == 0)
            deltaY--;
        deltaX = deltaX / 100;
        deltaY = deltaY / 100;
        deltaX = Number.parseInt(deltaX);
        deltaY = Number.parseInt(deltaY);
        var index = 0;
        index += deltaX * 50;
        index += deltaY;
        index++;
        return index;
    };

    function findTerrain(code) {
        // some calc;
        return terrains[0];
    };

    function generateInitialMap() {

        // Obtain tiles of terrains.
        var tileWidth = 100;
        var tileHeight = 78;
        var ctx = $('<canvas>').appendTo('body')[0].getContext("2d");
        var terrainSpriteSheet = new Image();
        terrainSpriteSheet.onload = function () {
            var imageWidth = terrainSpriteSheet.width;
            var imageHeight = terrainSpriteSheet.height;
            ctx.drawImage(terrainSpriteSheet, 0, 0);
            var tilesX = imageWidth / tileWidth;
            var tilesY = imageHeight / tileHeight;
            var totalTiles = tilesX * tilesY;
            for (var i = 0; i < tilesY; i++) {
                for (var j = 0; j < tilesX; j++) {
                    terrains.push(ctx.getImageData(j * tileWidth, i * tileHeight, tileWidth, tileHeight));
                }
            }
            // Get initial position.
            var towns = dataBase.buildings;
            var initialPlace = towns[0].place;
            var initX = initialPlace.x;
            var initY = initialPlace.y;

            // Init pan
            panZoom = svgPanZoom('#paper', {
                zoomEnabled: true,
                fit: false,
                center: false
            });

            drawLocalMapArround(initX, initY);
            // pan to specific point

        }
        terrainSpriteSheet.src = "../img/sprites/map/map1.jpg";
    };
});
