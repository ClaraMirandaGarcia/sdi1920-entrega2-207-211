var express = require('express');
var router = express.Router();

/* GET home page. */

router.get("/", function(req, res) {
  let respuesta = swig.renderFile('views/bhome.html', {user : req.session.usuario});
  res.send(respuesta);
});


module.exports = router;
