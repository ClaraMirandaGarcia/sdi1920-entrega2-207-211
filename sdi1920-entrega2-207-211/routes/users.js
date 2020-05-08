

module.exports = function (app, swig, gestorBD) {

    app.get("/users", function (req, res) {
        let criterio = {};
        if (req.query.busqueda != null) {
            criterio = {"nombre": req.query.busqueda};
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
                if (total % 5 > 0) { // Sobran decimales
                    ultimaPg = ultimaPg + 1;
                }
                let paginas = []; // paginas mostrar
                for (let i = pg - 2; i <= pg + 2; i++) {
                    if (i > 0 && i <= ultimaPg) {
                        paginas.push(i);
                    }
                }
                let respuesta = swig.renderFile('views/busers.html', {
                    users: users,
                    paginas: paginas,
                    actual: pg
                });
                res.send(respuesta);
            }
        });

    });


    app.get("/signup", function (req, res) {
        let response = swig.renderFile('views/bsignup.html', {});
        res.send(response);
    });

    app.post('/user', function (req, res) {

        let hash = app.get("crypto").createHmac('sha256', app.get('key'))
            .update(req.body.password).digest('hex');
        let confirm = app.get("crypto").createHmac('sha256', app.get('key'))
            .update(req.body.passwordConfirmation).digest('hex');

        if (confirm !== hash) {
            res.redirect("/signup?message=Las contraseñas no coinciden" + "&messageType=alert-danger");
            return;
        }

        let user = {
            email: req.body.email,
            password: hash
        };

        let criteria = {email: user.email};

        gestorBD.getUsers(criteria, function (users) {
            if (users == null || users.length === 0) {
                gestorBD.insertUser(user, function (id) {
                    if (id == null) {
                        res.redirect("/signup?message=Error al registrar usuario")
                    } else {
                        res.redirect("/signup?message=Nuevo usuario registrado");
                    }
                });
            } else {
                res.redirect("/signup?message=Email ya registrado" + "&messageType=alert-danger")
            }
        });
    });
};
