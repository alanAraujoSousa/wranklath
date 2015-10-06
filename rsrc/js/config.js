domain = "localhost";
domainPort = 8080;
apiRoute = "/wranklath/rest";
htmlRepo = '/rsrc/html';
restUtil = {
    login: "/user/login",
    logout: "/user/logout",
    move: "/unit/move",
    listUnits: "/user/unit",
    listBuildings: "/user/unit",
};

loginRest = 'http://' + domain + ':' + domainPort + apiRoute + restUtil.login;
listUnits = 'http://' + domain + ':' + domainPort + apiRoute + restUtil.listUnits;
