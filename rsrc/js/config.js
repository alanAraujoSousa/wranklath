domain = "localhost";
domainPort = 8080;
apiRoute = "/wranklath/rest";
htmlRepo = '/rsrc/html';
defaultRoute = 'http://' + domain + ':' + domainPort + apiRoute;

restUtil = {
    login: "/user/login",
    logout: "/user/logout",
    move: "/unit/move",
    listUnits: "/user/unit",
    listBuildings: "/user/building",
};

loginRest = defaultRoute + restUtil.login;
listUnits = defaultRoute + restUtil.listUnits;
listBuildings = defaultRoute + restUtil.listBuildings;
