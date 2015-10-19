dataBase = new Object();
dataBase.buildings = [];
dataBase.units = [];

terrains = [];
world = new Array(2500);
poolOfDownloadDataChunck = new Array();
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

    function drawMapArround(initX, initY) {
        var info = getInfoWorldDataChunck(initX, initY);
        var index = info.index;
        var deltaX = info.deltaX;
        var deltaY = info.deltaY;

        if (world[index] == null && poolOfDownloadDataChunck.indexOf(index) < 0) {
            console.log("Loading map chunck: " + index);
            poolOfDownloadDataChunck.push(index);

            var promise = retriveWorldDataChunck(index);
            promise.always(function (data) {
                var cont = poolOfDownloadDataChunck.indexOf(index);
                poolOfDownloadDataChunck.splice(cont, 1);
                world[index] = data;

                var isPainted = d3.select("#map" + index)[0][0] != null;
                if (!isPainted) { // if this chunck is already painted.
                    var group = d3.select('#mapGroup').append("g").attr("id", "map" + index);
                    for (var x = 1; x <= 100; x++) {
                        var factorX = x + deltaX;
                        for (var y = 1; y <= 100; y++) {
                            var factorY = y + deltaY;
                            group.append("rect")
                                .attr("id", "x" + factorX + "y" + factorY)
                                .attr("x", factorX * gridCellSize)
                                .attr("y", factorY * gridCellSize)
                                .attr("width", gridCellSize)
                                .attr("height", gridCellSize)
                                .attr("fill", getTile(world[index][x - 1][y - 1])) // ohhay!
                                .style("stroke-width", "1px")
                                .style("stroke", "black");
                        }
                    }
                } else {
                    console.warn("Tried to duplicate on screen the map: " + index);
                }
            });
        }
    };

    // this is for test
    function getInfoWorldDataChunck(initX, initY) {
        var deltaX = initX;
        var deltaY = initY;
        if (deltaX % 100 == 0)
            deltaX--;
        if (deltaY % 100 == 0)
            deltaY--;
        deltaX = Number.parseInt(deltaX / 100);
        deltaY = Number.parseInt(deltaY / 100);
        var index = 1;
        index += deltaX * 50;
        index += deltaY;
        deltaX *= 100;
        deltaY *= 100;

        var info = new Object();
        info.index = index;
        info.deltaX = deltaX;
        info.deltaY = deltaY;
        return info;
    };

    function getTile(code) {
        //some calc;
        return "url(#grassland)";
    };

    function findTerrain(code) {
        // some calc;
        return terrains[12];
    };

    function stalkEmptyTiles() {
        setInterval(
            function () {
                var x = panZoom.getPan().x;
                var y = panZoom.getPan().y;
                x = Number.parseInt(x / 100);
                y = Number.parseInt(y / 100);

                // Only calc coordinates positives.
                if (x < 0)
                    x *= -1;

                if (y < 0)
                    y *= -1;

                drawMapArround(x, y + 50);
                drawMapArround(x, y - 50);
                drawMapArround(x + 50, y);
                drawMapArround(x + 50, y + 50);
                drawMapArround(x + 50, y - 50);
                drawMapArround(x - 50, y);
                drawMapArround(x - 50, y - 50);
                drawMapArround(x - 50, y - 50);
            }, 1000);
    }

    function generateInitialMap() {

        // Obtain tiles of terrains.
        var tileWidth = 32;
        var tileHeight = 32;
        var ctx = $('<canvas>').css("display", "none").appendTo('body')[0].getContext("2d");
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
                zoomEnabled: false,
                fit: false,
                center: false
            });

            drawMapArround(initX, initY);

            // pan to specific point
            panZoom.pan({
                x: initX * gridCellSize * -1,
                y: initY * gridCellSize * -1
            });

            stalkEmptyTiles();
        }
        terrainSpriteSheet.src = "../img/sprites/map/tileset.png";
    };
});
