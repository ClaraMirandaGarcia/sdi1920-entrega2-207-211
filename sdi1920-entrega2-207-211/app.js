let express = require('express');
let app = express();

let crypto = require('crypto');
let path = require('path');
let fs = require('fs');
let https = require('https');
let mongo = require('mongodb');
let swig = require('swig');
let bodyParser = require('body-parser');
// Variables
app.set('port', 8081);
app.set('db', 'mongodb://admin:207sdi@tiendamusica-shard-00-00-lpbsd.mongodb.net:27017,tiendamusica-shard-00-01-lpbsd.mongodb.net:27017,tiendamusica-shard-00-02-lpbsd.mongodb.net:27017/test?ssl=true&replicaSet=tiendamusica-shard-0&authSource=admin&retryWrites=true&w=majority');
app.set('key', 'abcdefg');
app.set('crypto', crypto);

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug') ;
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/users', usersRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

//lanzar el servidor
https.createServer({
  key: fs.readFileSync('certificates/alice.key'),
  cert: fs.readFileSync('certificates/alice.crt')
}, app).listen(app.get('port'), function () {
  console.log("Servidor activo");
});
