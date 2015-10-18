domain = "localhost";
domainPort = 8080;
apiRoute = "/wranklath/rest";
htmlRepo = '/rsrc/html';
defaultRoute = 'http://' + domain + ':' + domainPort;
gridCellSize = '100';

restUtil = {
    login: "/user/login",
    logout: "/user/logout",
    move: "/unit/move",
    listUnits: "/user/unit",
    listBuildings: "/user/building",
};

loginRest = defaultRoute + apiRoute + restUtil.login;
listUnits = defaultRoute + apiRoute + restUtil.listUnits;
listBuildings = defaultRoute + apiRoute + restUtil.listBuildings;
