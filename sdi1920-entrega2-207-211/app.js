//Modules
let express = require('express');
let app = express();

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Credentials", "true");
  res.header("Access-Control-Allow-Methods", "POST, GET, DELETE, UPDATE, PUT");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
  // Debemos especificar todas las headers que se aceptan. Content-Type , token
  next();
});

let expressSession = require('express-session');
app.use(expressSession({
  secret: 'abcdefg',
  resave: true,
  saveUninitialized: true
}));


let crypto = require('crypto');
let path = require('path');
let fs = require('fs');
let https = require('https');
let swig = require('swig');
let bodyParser = require('body-parser');
let mongo = require('mongodb');
let gestorBD = require("./modules/gestorBD");



//BodyParser
{
  app.use(bodyParser.json());
  app.use(bodyParser.urlencoded({extended: true}));
}

//Mongodb
{
  gestorBD.init(app, mongo);
}

// Variables
{
  app.set('port', 8081);
  app.set('db', 'mongodb://admin:207sdi@tiendamusica-shard-00-00-lpbsd.mongodb.net:27017,tiendamusica-shard-00-01-lpbsd.mongodb.net:27017,tiendamusica-shard-00-02-lpbsd.mongodb.net:27017/test?ssl=true&replicaSet=tiendamusica-shard-0&authSource=admin&retryWrites=true&w=majority');
  app.set('key', 'abcdefg');
  app.set('crypto', crypto);
}

//Routers
{
  // routerUserSession
  {
    let routerUserSession = express.Router();
    routerUserSession.use(function(req, res, next) {
      console.log("routerUserSession");
      if (req.session.usuario) {
        next();
      } else {
        res.redirect("/login");
      }
    });
    app.use('/users', routerUserSession);
    app.use('/invitations', routerUserSession);
    app.use('/invitation', routerUserSession);
    app.use('/friendships', routerUserSession);
  }
  // routerFriendRequest
  {
    
  }
}

//default -> index
app.get("/", function(req, res) {
  let respuesta = swig.renderFile('views/bhome.html', {user : req.session.usuario});
  res.send(respuesta);
});


//Controllers
{
  require("./routes/users.js")(app, swig, gestorBD);
  require("./routes/rinvitations.js")(app, swig, gestorBD);
  require("./routes/rfriendships.js")(app, swig, gestorBD);
}



//lanzar el servidor
https.createServer({
  key: fs.readFileSync('certificates/alice.key'),
  cert: fs.readFileSync('certificates/alice.crt')
}, app).listen(app.get('port'), function () {
  console.log("Servidor activo");
});




module.exports = app;
