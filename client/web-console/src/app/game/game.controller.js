(function() {

  angular.module('wranklath')
  .controller('MameController', [
    'user.service',
    function(userService) {
      var vm = this;

      vm.user = new Object();
      vm.user.buildings = [];
      vm.user.units = [];

      vm.enemy = new Object();
      vm.enemy.buildings = [];
      vm.enemy.units = [];

      vm.terrains = [];
      vm.unitClicked = null;
      vm.world = new Array(2500);
      vm.queueDownloadMapChunck = new Array();
      vm.panZoom = new Object();

      generateInitialMap();

      function generateInitialMap() {
        // Get initial position.
        var towns = vm.user.buildings;
        var initialPlace = {};
        if (towns.length > 0) {
            var initialPlace = towns[0].place;
        } else {
            var armys = vm.user.units;
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
            mouseWheelZoomEnabled: false,
            fit: false,
            center: false,
            dblClickZoomEnabled: false
        });

        // Draw initial map
        drawMapVisible(initX, initY);
        stalkEmptyWorld();

        // pan to initial position.
        // TODO put initial coordinates on center of map.
        panZoom.pan({
            x: initX * GRID_CELL_SIZE * -1,
            y: initY * GRID_CELL_SIZE * -1
        });

        stalkEntities();
      }


  }]);
})();







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
        var promise = downloadNewEntities();
        $.when(promise).done(function () {
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

    function restExecuteAttack(deque, myUnitId, enemyUnitId) {
        myUnitId = myUnitId.replace('unit', '');
        enemyUnitId = enemyUnitId.replace('unit', '');
        var url = attack;
        url = url.replace('{id}', myUnitId);
        url = url.replace('{enemy}', enemyUnitId);
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
    }

    function restExecuteMovement(deque, unitClickedId) {
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

    function restGetEntitiesVisible() {
        var token = getCookie("token");
        return $.ajax({
            url: allVisible,
            type: 'GET',
            headers: {
                'token': token
            }
        });
    };

    function restGetUnits() {
        var token = getCookie("token");
        return $.ajax({
            url: listUnits,
            type: 'GET',
            headers: {
                'token': token
            },
        });
    };

    function restGetBuildings() {
        var token = getCookie("token");
        return $.ajax({
            url: listBuildings,
            type: 'GET',
            headers: {
                'token': token
            },
        });
    };

    function restDownloadMapChunk(index) {
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

    function clearMapMarks() {
        d3.select("#path").remove();
        d3.select("#selectMapEffect").attr("style", "display:none;");
        d3.select("#attackEffect").attr("style", "display:none;");
    }

    function handleEnemyClick() {
        var enemyEntity = d3.event.target;
        var eId = enemyEntity.getAttribute("id");
        var eX = Number(enemyEntity.getAttribute("x"));
        var eY = Number(enemyEntity.getAttribute("y"));

        // Only works with units, for
        if (unitClicked != null && eId != null && eId.search("unit") >= 0) {

            var units = dataBase.user.units;
            var atkRange = 0;
            for (var i = 0; i < units.length; i++) {
                var u = units[i];
                if (u.id == unitClicked.substr(4)) {
                    atkRange = u.atkRange;
                    break;
                }
            }

            clearMapMarks();

            var effect = d3.select("#attackEffect");
            effect.attr("transform", "translate(" + eX + "," + eY + ")");
            effect.attr("style", "display:true;");

            var unit = d3.select("#" + unitClicked)[0][0];
            var unitX = unit.getAttribute("x") / GRID_CELL_SIZE;
            var unitY = unit.getAttribute("y") / GRID_CELL_SIZE;

            eX /= GRID_CELL_SIZE;
            eY /= GRID_CELL_SIZE;

            var excludePlaces = getVisiblePlaces(eX, eY, atkRange - 1);
            var mapToSendToBackEnd = drawPathTo(unitX, unitY, eX, eY, excludePlaces);

            debugger;
            // var promise = restExecuteMovement(mapToSendToBackEnd, unitClicked);
        }

        d3.event.stopPropagation();
    };

    function handleMyUnitClick() {
        var unit = d3.event.target;
        var id = unit.getAttribute("id");
        var x = Number(unit.getAttribute("x"));
        var y = Number(unit.getAttribute("y"));

        var effect = d3.select("#selectUnitEffect");
        if (id != unitClicked) {
            unitClicked = id;
            var factor = GRID_CELL_SIZE / 2; // Factor is only to centralize.
            effect.attr("transform", "translate(" + (x + factor) + "," + (y + factor) + ")");
            effect.attr("style", "display:true;");
        } else {
            unitClicked = null;
            effect.attr("style", "display:none;");
        }

        clearMapMarks();

        d3.event.stopPropagation();
    };

    function handleMapClick() {
        // if we have a unit selected, this is how we differ buildings to units.
        if (unitClicked != null && unitClicked.search("unit") >= 0) {
            var map = d3.event.target;
            var id = map.getAttribute("id");
            var mapX = Number(map.getAttribute("x"));
            var mapY = Number(map.getAttribute("y"));

            var factor = GRID_CELL_SIZE / 2; // Factor is only to centralize the "X".

            clearMapMarks();

            var effect = d3.select("#selectMapEffect");
            effect.attr("transform", "translate(" + mapX + "," + mapY + ")");
            effect.attr("style", "display:true;");

            var unit = d3.select("#" + unitClicked)[0][0];
            var unitX = unit.getAttribute("x") / GRID_CELL_SIZE;
            var unitY = unit.getAttribute("y") / GRID_CELL_SIZE;
            mapX /= GRID_CELL_SIZE;
            mapY /= GRID_CELL_SIZE;

            var mapToSendToBackEnd = drawPathTo(unitX, unitY, mapX, mapY, null);
            var promise = restExecuteMovement(mapToSendToBackEnd, unitClicked);
        }
        d3.event.stopPropagation();
    };

    function drawPathTo(unitX, unitY, mapX, mapY, excludePlaces) {
        mapX = (mapX - unitX) + 29;
        mapY = (mapY - unitY) + 29;

        var grid = findTerrainWalkableProperties(unitX, unitY);

        if (excludePlaces != null) {
            for (var k in excludePlaces) {
                var exclude = excludePlaces[k];
                var xE = (exclude[0] - unitX) + 29;
                var yE = (exclude[1] - unitY) + 29;
                grid.setWalkableAt(xE, yE, true);
            }
        }

        var finder = new PF.AStarFinder({
            allowDiagonal: true,
            dontCrossCorners: true
        });

        var path = finder.findPath(29, 29, mapX, mapY, grid);

        var pathToBeDrawed = [];
        for (var i = 0; i < path.length; i++) {
            var el = path[i];
            mapX = (el[0] - 29) + unitX;
            mapY = (el[1] - 29) + unitY;

            var flag = false;
            if (excludePlaces != null)
                for (var k in excludePlaces) {
                    var exclude = excludePlaces[k];
                    if (mapX == exclude[0] && mapY == exclude[1]) {
                        flag = true;
                        continue;
                    }
                }
            if (flag) continue;
            var obj = {};
            obj.x = mapX * GRID_CELL_SIZE;
            obj.y = mapY * GRID_CELL_SIZE;
            pathToBeDrawed.push(obj);
        }

        var mapToSendToBackEnd = new Array();
        if (pathToBeDrawed.length > 0) {

            var lineFunction = d3.svg.line()
                .x(function (d) {
                    return d.x + (GRID_CELL_SIZE / 2); // 50 to centralize effect on grid
                })
                .y(function (d) {
                    return d.y + (GRID_CELL_SIZE / 2);
                })
                .interpolate("linear");

            var lineGraph = d3.select('#movementEffects').append("path")
                .attr("id", "path")
                .attr("d", lineFunction(pathToBeDrawed))
                .attr("stroke", "green")
                .attr("stroke-width", 12)
                .attr("stroke-dasharray", "50,25")
                .attr("fill", "none");

            for (var i = 1; i < pathToBeDrawed.length; i++) { // jump first location
                var x = pathToBeDrawed[i].x / 100;
                var y = pathToBeDrawed[i].y / 100;
                mapToSendToBackEnd.push(x);
                mapToSendToBackEnd.push(y);
            }
        }
        return mapToSendToBackEnd;
    }

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

                if (elX > 59 || elY > 59 || elX < 0 || elY < 0)
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
                        var coorX = (x - 29) + i;
                        var coorY = (y - 29) + j;

                        var deltaX = coorX;
                        var deltaY = coorY;
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

                        coorX = coorX - deltaX;
                        coorY = coorY - deltaY;

                        var terrainType = world[index][coorX - 1][coorY - 1];

                        if (terrainType != 4)
                            isPassable = true;

                    }
                }
                // isPassable = isPassable >>> 0;
                grid.setWalkableAt(i, j, isPassable);
            }
        }
        return grid;
    }

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
            restDownloadMapChunk(index).always(function (data) {
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
                        .attr("x", factorX * GRID_CELL_SIZE)
                        .attr("y", factorY * GRID_CELL_SIZE)
                        .attr("width", GRID_CELL_SIZE)
                        .attr("height", GRID_CELL_SIZE)
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
        if (type == 4)
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

    // FIXME this method is very stupid.
    function getVisiblePlaces(x, y, visibility) {

        var visibles = {};
        if (visibility == 0) {
            visibles['x' + x + 'y' + y] = [x, y];
            return visibles;
        }

        var delta = 0;

        if (visibility > 2) {
            if (visibility % 3 == 0) { // divisors
                delta = visibility / 3;
            } else if ((visibility + 1) % 3 == 0) { // adjacents
                delta = (visibility + 1) / 3;
                delta--;
            } else { // fails
                delta = (visibility - 1) / 3;
                delta--;
            }
        }

        // Miau for calc diagonal when visibility is 1.
        if (visibility == 1) {
            visibles['x' + (x + 1) + 'y' + (y + 1)] = [x + 1, y + 1];
            visibles['x' + (x - 1) + 'y' + (y - 1)] = [x - 1, y - 1];
            visibles['x' + (x + 1) + 'y' + (y - 1)] = [x + 1, y - 1];
            visibles['x' + (x - 1) + 'y' + (y + 1)] = [x - 1, y + 1];
        }

        var cont = visibility + delta + 1;
        var x2;
        var y2;
        x2 = x;
        y2 = y + visibility;
        for (var i = 1; i <= cont; i++) {
            for (var j = 0; j <= (y2 - y); j++) {
                var mY = (y + j);
                visibles['x' + x2 + 'y' + mY] = [x2, mY];
            }
            if (x2 < x + visibility) {
                x2++;
            }
            if (i > delta) {
                y2--;
            }
        }

        x2 = x;
        y2 = y + visibility;
        for (var i = 1; i <= cont; i++) {
            for (var j = 0; j <= (y2 - y); j++) {
                var mY = (y + j);
                visibles['x' + x2 + 'y' + mY] = [x2, mY];
            }
            if (x2 > x - visibility) {
                x2--;
            }
            if (i > delta) {
                y2--;
            }
        }

        x2 = x;
        y2 = y - visibility;
        for (var i = 1; i <= cont; i++) {
            for (var j = 0; j <= (y - y2); j++) {
                var mY = (y - j);
                visibles['x' + x2 + 'y' + mY] = [x2, mY];
            }
            if (x2 > x - visibility) {
                x2--;
            }
            if (i > delta) {
                y2++;
            }
        }

        x2 = x;
        y2 = y - visibility;
        for (var i = 1; i <= cont; i++) {
            for (var j = 0; j <= (y - y2); j++) {
                var mY = (y - j);
                visibles['x' + x2 + 'y' + mY] = [x2, mY];
            }
            if (x2 < x + visibility) {
                x2++;
            }
            if (i > delta) {
                y2++;
            }
        }

        return visibles;
    };

    function drawVisibilityRange() {
        var all = new Array();
        var units = dataBase.user.units;
        var buildings = dataBase.user.buildings;

        $.merge($.merge(all, buildings), units);

        var g = d3.select('#visibleEffect');
        g.selectAll("*").remove(); // for simple refresh

        var visibles = {};
        for (var it = 0; it < all.length; it++) {
            el = all[it];
            var x = el.place.x;
            var y = el.place.y;

            var visibility;
            if (el.conclusionDate == null) // differ buildings of unit.
                visibility = el.visibility;
            else
                visibility = 3 // FIXME give visibility to buildings on backend.

            var v = getVisiblePlaces(x, y, visibility);
            for (var k in v) {
                visibles[k] = v[k];
            }
        }

        for (var key in visibles) {
            var place = visibles[key];
            g.append("rect")
                .attr("id", key)
                .attr("filter", "url(#visible)")
                .attr("x", place[0] * GRID_CELL_SIZE)
                .attr("y", place[1] * GRID_CELL_SIZE)
                .attr("width", GRID_CELL_SIZE)
                .attr("height", GRID_CELL_SIZE)
                .attr("fill", "yellow");
        }
    };

    function stalkEntities() {
        setInterval(function () {
            var promise = downloadNewEntities();
            $.when(promise).done(function () {
                drawEntitiesVisible();
                drawVisibilityRange();
            });
        }, 3000); // draw all
    };

    function stalkEmptyWorld() {
        setInterval(function () {
            // top, left corner.
            var initX = panZoom.getPan().x;
            var initY = panZoom.getPan().y;
            initX = Number.parseInt(initX / 100);
            initY = Number.parseInt(initY / 100);

            // Only calc coordinates positives.
            if (initX < 0)
                initX *= -1;

            if (initY < 0)
                initY *= -1;

            drawMapVisible(initX, initY);
        }, 3000);


    }

    function downloadNewEntities() {
        var prom = restGetEntitiesVisible();
        prom.always(function (data) {
            dataBase.user.units = [];
            dataBase.user.buildings = [];
            dataBase.enemy.units = [];
            dataBase.enemy.buildings = [];
            if (data != null && data.length > 0) {
                for (var i = 0; i < data.length; i++) {
                    var el = data[i];

                    var login = el.userLogin;
                    var owner;
                    if (login != getCookie("user"))
                        owner = dataBase.enemy;
                    else
                        owner = dataBase.user;

                    // FIXME find another way to differ builds and units.
                    if (el.conclusionDate == null) {
                        owner.units.push(el);
                    } else {
                        owner.buildings.push(el);
                    }
                }
            }
        });
        return prom;
    };

    function drawEntity(entity) {
        var x = entity.place.x;
        var y = entity.place.y;
        var type = entity.type;
        var id = entity.id;
        var login = entity.userLogin;

        var name;
        if (entity.conclusionDate == null) {
            name = 'unit'
            type = getUnit(type);
        } else {
            name = 'building'
            type = getBuild(type);
        }

        var d3Element = d3.select("#" + name + id);
        var elementDrawed = d3Element[0][0];
        if (elementDrawed != null) {
            var actualX = elementDrawed.getAttribute("x") / GRID_CELL_SIZE;
            var actualY = elementDrawed.getAttribute("y") / GRID_CELL_SIZE;

            if (actualX == x && actualY == y) {
                return;
            } else {
                d3Element.remove();
            }
        }

        var rect = d3.select("#" + name + "Group").append("rect")
        rect.attr("id", name + id)
            .attr("x", x * GRID_CELL_SIZE)
            .attr("y", y * GRID_CELL_SIZE)
            .attr("width", GRID_CELL_SIZE)
            .attr("height", GRID_CELL_SIZE)
            .attr("fill", type);

        if (login != getCookie("user")) {
            rect.attr("filter", "url(#enemy)");
            rect.on("click", handleEnemyClick);
        } else {
            rect.on("click", handleMyUnitClick);
        }
    };

    function drawEntitiesVisible() {

        d3.select("#buildingGroup").selectAll("*").remove();
        d3.select("#unitGroup").selectAll("*").remove();

        // top, left corner.
        var initX = panZoom.getPan().x;
        var initY = panZoom.getPan().y;
        initX = Number.parseInt(initX / 100);
        initY = Number.parseInt(initY / 100);

        // Only calc coordinates positives.
        if (initX < 0)
            initX *= -1;

        if (initY < 0)
            initY *= -1;

        var range = 30;
        var buildings = vm.user.buildings;
        var buildingsE = vm.enemy.buildings;
        var units = vm.user.units;
        var unitsE = vm.enemy.units;

        var all = new Array();
        $.merge($.merge(all, buildings), buildingsE);
        $.merge($.merge(all, units), unitsE);

        for (var i = 0; i < all.length; i++) {
            var entity = all[i];
            var targetX = entity.place.x;
            var targetY = entity.place.y;

            if (((initX - range) <= targetX && (initX + range) >= targetX) &&
                ((initY - range) <= targetY && (initY + range) >= targetY))
                drawEntity(entity);
        }
    };
});
