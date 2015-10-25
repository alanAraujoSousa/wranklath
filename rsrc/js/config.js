domain = "localhost";
domainPort = 8080;
apiRoute = "/wranklath/rest";
htmlRepo = '/rsrc/html';
defaultRoute = 'http://' + domain + ':' + domainPort;
gridCellSize = '100';
ds = 'd';

restUtil = {
    login: "/user/login",
    logout: "/user/logout",
    move: "/unit/move",
    listUnits: "/user/unit",
    listBuildings: "/user/building",
    allVisible: "/user/allvisible"
};

loginRest = defaultRoute + apiRoute + restUtil.login;
listUnits = defaultRoute + apiRoute + restUtil.listUnits;
listBuildings = defaultRoute + apiRoute + restUtil.listBuildings;
allVisible = defaultRoute + apiRoute + restUtil.allVisible;
