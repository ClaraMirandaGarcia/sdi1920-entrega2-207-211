module.exports = function (app, swig, gestorBD) {
  app.get("/logout", function (req, res) {
    req.session.usuario = null;
    res.redirect("/login?Desconectado con exito");
  });

  app.get("/users", function (req, res) {
    let criterio = {};

    if (req.query.busqueda != null) {
      criterio = {
        $or: [
          { name: { $regex: ".*" + req.query.busqueda + ".*", $options: "i" } },
          {
            surname: {
              $regex: ".*" + req.query.busqueda + ".*",
              $options: "i",
            },
          },
          {
            email: { $regex: ".*" + req.query.busqueda + ".*", $options: "i" },
          },
        ],
      };
    }
    let pg = parseInt(req.query.pg);
    if (req.query.pg == null) {
      pg = 1;
    }

    gestorBD.obtainUsersPg(criterio, pg, function (users, total) {
      if (users == null) {
        res.send("Error al listar ");
      } else {
        let ultimaPg = total / 5;
        if (total % 5 > 0) {
          // Sobran decimales
          ultimaPg = ultimaPg + 1;
        }
        let paginas = []; // paginas mostrar
        for (let i = pg - 2; i <= pg + 2; i++) {
          if (i > 0 && i <= ultimaPg) {
            paginas.push(i);
          }
        }
        let respuesta = swig.renderFile("views/busers.html", {
          users: users,
          paginas: paginas,
          actual: pg,
        });
        res.send(respuesta);
      }
    });
  });

  app.get("/signup", function (req, res) {
    let response = swig.renderFile("views/bsignup.html", {});
    res.send(response);
  });

  app.get("/login", function (req, res) {
    let respuesta = swig.renderFile("views/blogin.html", {});
    res.send(respuesta);
  });

  app.post("/login", function (req, res) {
    let hash = app
      .get("crypto")
      .createHmac("sha256", app.get("key"))
      .update(req.body.password)
      .digest("hex");
    let criteria = {
      email: req.body.email,
      password: hash,
    };
    gestorBD.getUsers(criteria, function (users) {
      if (users == null || users.length == 0) {
        req.session.usuario = null;
        res.redirect(
          "/login" +
            "?message=Email o password incorrecto" +
            "&messageType=alert-danger "
        );
      } else {
        req.session.usuario = users[0];
        res.redirect("/users");
      }
    });
  });

  app.post("/user", function (req, res) {
    let hash = app
      .get("crypto")
      .createHmac("sha256", app.get("key"))
      .update(req.body.password)
      .digest("hex");
    let confirm = app
      .get("crypto")
      .createHmac("sha256", app.get("key"))
      .update(req.body.passwordConfirmation)
      .digest("hex");
    if (confirm !== hash) {
      res.redirect(
        "/signup?message=Las contraseñas no coinciden" +
          "&messageType=alert-danger"
      );
      return;
    }
    let user = {
      email: req.body.email,
      name: req.body.nombre,
      surname: req.body.apellidos,
      password: hash,
    };
    let criteria = { email: user.email };
    gestorBD.getUsers(criteria, function (users) {
      if (users == null || users.length === 0) {
        gestorBD.insertUser(user, function (id) {
          if (id == null) {
            res.redirect("/signup?message=Error al registrar usuario");
          } else {
            res.redirect("/signup?message=Nuevo usuario registrado");
          }
        });
      }
    });
  });
};
