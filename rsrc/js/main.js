dataBase = new Object();
dataBase.user = new Object();
dataBase.user.buildings = [];
dataBase.user.units = [];

dataBase.enemy = new Object();
dataBase.enemy.buildings = [];
dataBase.enemy.units = [];

terrains = [];
unitClicked = null;
world = new Array(2500);
queueDownloadMapChunck = new Array();
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
            dataBase.user.buildings = towns[0];
            dataBase.user.units = armys[0];
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
            document.cookie = "user=" + login;
            document.cookie = "token=" + data.responseText;
            window.location.hash = "main";
        });
    });

    function getCookie(cookieName) {
        var cookie = document.cookie;
        cookieName += "=";
        var index = cookie.indexOf(cookieName);
        if (index < 0) {
            return null;
        }
        index += cookieName.length;
        cookie += ";";
        cookie = cookie.substr(index);
        index = cookie.indexOf(";");
        cookie = cookie.substr(0, index);
        return cookie;
    };

    function executeMovement(deque, unitClickedId) {
        unitClickedId = unitClickedId.replace('unit', ''); // delete prefix
        var url = move.replace('{id}', unitClickedId); // insert id on uri
        var token = getCookie("token");
        return $.ajax({
            url: url,
            type: 'POST',
            data: JSON.stringify(deque),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            headers: {
                'token': token
            }
        });
    };

    function retrieveEntitiesVisible() {
        var token = getCookie("token");
        return $.ajax({
            url: allVisible,
            type: 'GET',
            headers: {
                'token': token
            }
        });
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

    function downloadMapDataChunck(index) {
        var url;
        url = "../js/map/map" + index + ".json";
        return $.ajax({
            url: url,
            type: 'GET',
            dataType: 'json',
        });
    };

    function loginAjax(userObject) {
        return $.ajax({
            url: loginRest,
            type: 'POST',
            data: JSON.stringify(userObject),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json'
        });
    };

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

    function handleMyUnitClick() {
        var unit = d3.event.target;
        var id = unit.getAttribute("id");
        var x = Number(unit.getAttribute("x"));
        var y = Number(unit.getAttribute("y"));

        var effect = d3.select("#selectUnitEffect");
        if (id != unitClicked) {
            unitClicked = id;
            var factor = gridCellSize / 2; // Factor is only to centralize.
            effect.attr("transform", "translate(" + (x + factor) + "," + (y + factor) + ")");
            effect.attr("style", "display:true;");
        } else {
            unitClicked = null;
            effect.attr("style", "display:none;");
        }
        var mapEffect = d3.select("#selectMapEffect");
        mapEffect.attr("style", "display:none;");
        d3.select("#path").remove();

        d3.event.stopPropagation();
    };

    function handleMapClick() {
        if (unitClicked != null && unitClicked.search("unit") > 0) { // if we have a unit selected.
            var map = d3.event.target;
            var id = map.getAttribute("id");
            var mapX = Number(map.getAttribute("x"));
            var mapY = Number(map.getAttribute("y"));

            var factor = 50; // Factor is only to centralize the "X".

            d3.select("#path").remove();
            var effect = d3.select("#selectMapEffect");
            effect.attr("transform", "translate(" + mapX + "," + mapY + ")");
            effect.attr("style", "display:true;");

            var unit = d3.select("#" + unitClicked)[0][0];
            var unitX = unit.getAttribute("x") / 100;
            var unitY = unit.getAttribute("y") / 100;
            mapX /= 100;
            mapY /= 100;

            mapX = mapX - unitX;
            mapY = mapY - unitY;

            mapX += 29;
            mapY += 29;

            var grid = findTerrainWalkableProperties(unitX, unitY);
            var finder = new PF.AStarFinder({
                allowDiagonal: true,
                dontCrossCorners: true
            });

            var path = finder.findPath(29, 29, mapX, mapY, grid);

            var pathToBeDrawed = [];
            for (var i = 0; i < path.length; i++) {
                var el = path[i];
                mapX = el[0];
                mapY = el[1];

                mapX -= 29;
                mapY -= 29;

                mapX += unitX;
                mapY += unitY;

                var obj = {};
                obj.x = mapX * 100;
                obj.y = mapY * 100;
                pathToBeDrawed.push(obj);
            }

            var lineFunction = d3.svg.line()
                .x(function (d) {
                    return d.x + (gridCellSize / 2); // 50 to centralize effect on grid
                })
                .y(function (d) {
                    return d.y + (gridCellSize / 2);
                })
                .interpolate("linear");

            var lineGraph = d3.select('#movementEffects').append("path")
                .attr("id", "path")
                .attr("d", lineFunction(pathToBeDrawed))
                .attr("stroke", "green")
                .attr("stroke-width", 12)
                .attr("stroke-dasharray", "50,25")
                .attr("fill", "none");

            var mapToSendToBackEnd = new Array();
            for (var i = 1; i < pathToBeDrawed.length; i++) { // jump first location
                var x = pathToBeDrawed[i].x / 100;
                var y = pathToBeDrawed[i].y / 100;
                mapToSendToBackEnd.push(x);
                mapToSendToBackEnd.push(y);
            }

            var promise = executeMovement(mapToSendToBackEnd, unitClicked);
        }
        d3.event.stopPropagation();
    };

    function findTerrainWalkableProperties(x, y) {
        var units = dataBase.user.units;
        var unitsE = dataBase.enemy.units;
        var buildings = dataBase.user.buildings;
        var buildingsE = dataBase.enemy.buildings;

        var allE = new Array();

        // (:
        var allU = $.merge($.merge([], units), unitsE);
        var allB = $.merge($.merge([], buildings), buildingsE);
        $.merge(allE, allB);
        $.merge(allE, allU);

        // Grid array-like,
        var grid = new PF.Grid(60, 60);

        if (allE != null && allE.length > 0) {
            for (var i = 0; i < allE.length; i++) {
                var el = allE[i];
                var elX = el.place.x;
                var elY = el.place.y;

                elX -= x;
                elY -= y;
                elX += 29;
                elY += 29;

                if (elX > 59 || elY > 59)
                    continue;

                grid.setWalkableAt(elX, elY, false);
            }
        }

        for (var i = 0; i < 60; i++) {
            for (var j = 0; j < 60; j++) {
                var isPassable = false
                if (i == 29 && j == 29) {
                    isPassable = true;
                } else {
                    if (grid.isWalkableAt(i, j)) {
                        var map = d3.select('#x' + ((x - 29) + i) + 'y' + ((y - 29) + j))[0][0];
                        if (map != null) {
                            isPassable = isTerrainPassable(map.getAttribute('fill'));
                        }
                    }
                }
                // isPassable = isPassable >>> 0;
                grid.setWalkableAt(i, j, isPassable);
            }
        }
        return grid;
    }

    function drawMap(initX, initY) {
        var info = getInfoMapChunck(initX, initY);
        var index = info.index;
        var deltaX = info.deltaX;
        var deltaY = info.deltaY;

        if (world[index] != null) {
            paint(index, deltaX, deltaY);
        }

        if (world[index] == null && queueDownloadMapChunck.indexOf(index) < 0) {
            console.log("Downloading map chunck: " + index);
            queueDownloadMapChunck.push(index);
            // promise
            downloadMapDataChunck(index).always(function (data) {
                //remove download of queue
                queueDownloadMapChunck.splice(queueDownloadMapChunck.indexOf(index), 1);
                // store data of world
                world[index] = data;
                paint(index, deltaX, deltaY);
            });
        }

    };

    function paint(index, deltaX, deltaY) {
        var isPainted = d3.select("#map" + index)[0][0] != null;
        // if this chunck isn't already painted.
        if (!isPainted) {
            var group = d3.select('#mapGroup').append("g").attr("id", "map" + index);
            for (var x = 1; x <= 100; x++) {
                var factorX = x + deltaX;
                for (var y = 1; y <= 100; y++) {
                    var factorY = y + deltaY;
                    var terrainType = world[index][x - 1][y - 1]; // ohhay!
                    group.append("rect")
                        .attr("id", "x" + factorX + "y" + factorY)
                        .attr("x", factorX * gridCellSize)
                        .attr("y", factorY * gridCellSize)
                        .attr("width", gridCellSize)
                        .attr("height", gridCellSize)
                        .attr("fill", getTerrain(terrainType))
                        .style("stroke-width", "1px")
                        .style("stroke", "black")
                        .on("click", handleMapClick);
                }
            }
        }
    };

    function getInfoMapChunck(initX, initY) {
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

    function isTerrainPassable(type) {
        if (type == "url(#water)")
            return false;
        return true;
    }

    function getBuild(code) {
        switch (code) {
            case 943213:
                return "url(#town)";
            case 194234:
                return "url(#barracks)";
        }
    };

    function getUnit(code) {
        switch (code) {
            case 422322:
                return "url(#cavalary)";
            case 392333:
                return "url(#infantry)";
            case 202356:
                return "url(#archer)";
        }
    };

    function getTerrain(code) {
        switch (code) {
            case 1:
                return "url(#grassland)";
            case 2:
                return "url(#road)";
            case 3:
                return "url(#forest)";
            case 4:
                return "url(#water)";
        }
    };

    function stalkEntities() {
        setInterval(drawEnemiesVisible, 5000);
    };

    function drawEnemiesVisible() {
        var prom = retrieveEntitiesVisible();
        prom.always(function (data) {
            dataBase.enemy.units = [];
            dataBase.enemy.buildings = [];
            if (data != null && data.length > 0) {
                for (var i = 0; i < data.length; i++) {
                    var el = data[i];
                    // FIXME find another way to differ builds and units.
                    if (el.conclusionDate == null)
                        dataBase.enemy.units.push(el);
                    else
                        dataBase.enemy.buildings.push(el);

                }
            }
            var x = panZoom.getPan().x;
            var y = panZoom.getPan().y;
            x = Number.parseInt(x / 100);
            y = Number.parseInt(y / 100);

            // Only calc coordinates positives.
            if (x < 0)
                x *= -1;

            if (y < 0)
                y *= -1;

            drawEntitiesVisible(x, y);
        });
    };

    function drawEntity(entity, name) {
        var x = entity.place.x;
        var y = entity.place.y;
        var type = entity.type;
        var id = entity.id;
        var login = entity.userLogin;

        var d3Element = d3.select("#" + name + id);
        var elementDrawed = d3Element[0][0];
        if (elementDrawed != null) {
            var actualX = elementDrawed.getAttribute("x") / gridCellSize;
            var actualY = elementDrawed.getAttribute("y") / gridCellSize;

            if (actualX == x && actualY == y) {
                return;
            } else {
                d3Element.remove();
            }
        }

        if (name == "unit") {
            type = getUnit(type);
        } else {
            type = getBuild(type);
        }

        var rect = d3.select("#" + name + "Group").append("rect")
        rect.attr("id", name + id)
            .attr("x", x * gridCellSize)
            .attr("y", y * gridCellSize)
            .attr("width", gridCellSize)
            .attr("height", gridCellSize)
            .attr("fill", type);

        if (login != getCookie("user")) {
            rect.attr("filter", "url(#enemy)");
        } else {
            rect.on("click", handleMyUnitClick);
        }
    };

    function drawEntitiesVisible(initX, initY) {
        drawBuildVisible(initX, initY);
        drawUnitVisible(initX, initY);
    };

    // Go horse
    function drawBuildVisible(x, y) {
        var range = 30;
        var buildings = dataBase.user.buildings;
        var buildingsE = dataBase.enemy.buildings;
        var all = new Array();
        $.merge($.merge(all, buildings), buildingsE);
        for (var i = 0; i < all.length; i++) {
            var building = all[i];
            var targetX = building.place.x;
            var targetY = building.place.y;

            if (((x - range) <= targetX && (x + range) >= targetX) &&
                ((y - range) <= targetY && (y + range) >= targetY))
                drawEntity(building, "building");
        }
    };

    // Go go horse
    function drawUnitVisible(x, y) {
        var range = 30;
        var units = dataBase.user.units;
        var unitsE = dataBase.enemy.units;
        var all = new Array();
        $.merge($.merge(all, units), unitsE);
        for (var i = 0; i < all.length; i++) {
            var unit = all[i];
            var targetX = unit.place.x;
            var targetY = unit.place.y;

            if (((x - range) <= targetX && (x + range) >= targetX) &&
                ((y - range) <= targetY && (y + range) >= targetY))
                drawEntity(unit, "unit");
        }
    };

    function drawMapVisible(x, y) {
        drawMap(x, y + 50);
        drawMap(x, y - 50);
        drawMap(x + 50, y);
        drawMap(x + 50, y + 50);
        drawMap(x + 50, y - 50);
        drawMap(x - 50, y);
        drawMap(x - 50, y - 50);
        drawMap(x - 50, y - 50);
    };

    function generateInitialMap() {

        // Get initial position.
        var towns = dataBase.user.buildings;
        var initialPlace = {};
        if (towns.length > 0) {
            var initialPlace = towns[0].place;
        } else {
            var armys = dataBase.user.units;
            if (armys.length > 0) {
                var initialPlace = armys[0].place;
            } else {
                initialPlace.x = 150; // Random number.
                initialPlace.y = 150;
            }
        }
        var initX = initialPlace.x;
        var initY = initialPlace.y;

        // Init pan
        panZoom = svgPanZoom('#paper', {
            mouseWheelZoomEnabled: true,
            fit: false,
            center: false,
            dblClickZoomEnabled: false
        });


        //        d3.select("#paper").on("click", handleMapClick);

        drawMapVisible(initX, initY);
        drawEntitiesVisible(initX, initY);
        drawEnemiesVisible();

        // pan to initial position.
        // TODO put initial coordinates on center of map.
        panZoom.pan({
            x: initX * gridCellSize * -1,
            y: initY * gridCellSize * -1
        });

        stalkEntities();
    };
});
