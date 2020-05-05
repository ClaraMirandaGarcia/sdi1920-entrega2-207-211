var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function(req, res, next) {
  res.send('respond with a resource');
});

module.exports = router;

module.exports = function (app, swig, gestorBD) {
  app.get('/logout', function (req, res) {
    req.session.usuario = null;
    res.redirect("/login?Desconectado con exito");
  });
  app.get("/signup", function (req, res) {
    let response = swig.renderFile('views/bsignup.html',{});
    res.send(response);
  });
  app.post('/user', function (req, res) {
    let hash = app.get("crypto").createHmac('sha256', app.get('key'))
        .update(req.body.password).digest('hex');
    let confirm = app.get("crypto").createHmac('sha256', app.get('key'))
        .update(req.body.passwordConfirmation).digest('hex');
    if (confirm !== hash){
      res.redirect("/signup?message=Las contraseñas no coinciden" + "&messageType=alert-danger");
      return;
    }
    let user = {
      email: req.body.email,
      password: hash
    };
    let criteria = { email: user.email};
    gestorBD.getUsers(criteria, function (users) {
      if (users == null || users.length === 0){
        gestorBD.insertUser(user, function (id) {
          if (id == null) {
            res.redirect("/signup?message=Error al registrar usuario")
          } else {
            res.redirect("/signup?message=Nuevo usuario registrado");
          }
        });
      }else{
        res.redirect("/signup?message=Email ya registrado" + "&messageType=alert-danger")
      }
    });
  });
};
