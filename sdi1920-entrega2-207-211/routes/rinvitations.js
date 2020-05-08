module.exports = function (app, swig, gestorBD) {

    app.get("/send/invitation/:id", function (req, res) {
        let criterio_to = {
            "_id": gestorBD.mongo.ObjectID(req.params.id)
        };

        if (typeof req.session.usuario == "undefined" || req.session.usuario == null) {
            res.send("El usuario no está en sesión");
            //redirect?
        } else {
            let user_from = req.session.usuario;
            gestorBD.getUsers(criterio_to, function (users) {
                if (users == null || users.length == 0) {
                    //redirect?

                } else {
                    let user_to = users[0];
                    let invitation = {
                        userFrom: user_from,
                        userTo: user_to.email
                    }

                    //check if it's the same user
                    if (user_from.email == user_to.email) {
                        res.redirect("/users");
                        // + message -> error

                    } else {
                        gestorBD.insertInvitation(invitation, function (id) {
                            if (id == null) {
                                //there was an error adding?
                            } else {
                                res.redirect("/users");
                            }
                        })
                    }
                }
            })

        }
    });

}
