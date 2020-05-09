module.exports = function (app, swig, gestorBD) {

    app.get("/invitation/send/:id", function (req, res) {
        let criterio_to = {
            _id: gestorBD.mongo.ObjectID(req.params.id)
        };

        if (typeof req.session.usuario == "undefined" || req.session.usuario == null) {
            res.send("El usuario no está en sesión");
            //redirect?
        } else {
            let user_from = req.session.usuario;
            let user_from_id = gestorBD.mongo.ObjectID(user_from._id.toString());


            gestorBD.getUsers(criterio_to, function (users) {
                if (users == null || users.length == 0) {
                    //redirect?

                } else {
                    let user_to = users[0];

                    console.log(user_from._id);

                    let invitation = {
                        userFrom: user_from_id,
                        userTo: user_to._id
                    }

                    //check if there is an invitation already (both ways)

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


    //invitaciones para x
    app.get("/invitations", function (req, res) {

            let criterio = {
                userTo: gestorBD.mongo.ObjectID(req.session.usuario._id.toString())
            };

            let pg = parseInt(req.query.pg);
            if (req.query.pg == null) {
                pg = 1;
            }


            gestorBD.obtainInvitationsPg(criterio, pg, function (invitations, total) {
                if (invitations == null) {
                    //manejo error
                    res.send("Error al listar ");
                } else {


                    let criterio = {
                        $or: invitations.map((i) => {
                            return {_id: gestorBD.mongo.ObjectID(i.userFrom.toString())}
                        })
                    }

                    gestorBD.getUsers(criterio, function (users) {
                        if (users == null || users.length == 0) {
                            //redirect?
                        } else {
                            if (invitations.length != 0) {
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

                                let respuesta = swig.renderFile('views/binvitations.html', {
                                    users: users,
                                    paginas: paginas,
                                    actual: pg
                                });
                                res.send(respuesta);

                            } else {
                                console.log('Este usuario no tiene invitaciones de amistad');
                            }

                        }
                    });
                }
            });
        }
    )
    ;



    app.get("/invitation/accept/:id", function (req, res) {

        if (typeof req.session.usuario == "undefined" || req.session.usuario == null) {
            res.send("El usuario no está en sesión");
            //redirect?
        } else{
            //erase petition
            console.log(req.session.usuario);
            console.log(req.session.usuario._id);
            let userTo= gestorBD.mongo.ObjectID(req.session.usuario._id.toString());
            let userFrom = gestorBD.mongo.ObjectID(req.params.id.toString());

            let criterioPetition = {
                userTo: userTo,
                userFrom: userFrom
            }

            gestorBD.eraseInvitation(criterioPetition, function(invitation){
                if(invitation == null){
                    res.send(respuesta);
                } else{

                    // insert friendship
                    let friendship = {
                        userFrom: userFrom,
                        userTo: userTo
                    }

                    console.log(friendship);

                    gestorBD.insertFriendship(friendship, function (id) {
                        if (id == null) {
                            //there was an error adding?
                        } else {
                            //redirect to list of friends.
                            res.redirect("/users");
                        }
                    })

                    // redirect
                }
            })
        }

    });


}
