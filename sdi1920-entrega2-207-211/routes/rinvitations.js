module.exports = function (app, swig, gestorBD) {

    app.get("/invitation/send/:id", function (req, res) {

        let criterio_to = {
            _id: gestorBD.mongo.ObjectID(req.params.id)
        };
        if (typeof req.session.usuario == "undefined" || req.session.usuario == null) {
            res.send("El usuario no está en sesión");
            //ERROR MANAGEMENT
            //TODO
        } else {

            let user_from = req.session.usuario;
            let user_from_id = gestorBD.mongo.ObjectID(user_from._id.toString());


            gestorBD.getUsers(criterio_to, function (users) {
                if (users == null || users.length == 0) {
                    //ERROR MANAGEMENT, the user does not exist
                    res.redirect("/users" +
                        "?mensaje=Usuario no existe" +
                        "&messageType=alert-danger ");

                } else {
                    //THE USERS EXIST
                    let user_to = users[0];

                    //CRITERIA TO SEARCH AN INVITATION OR A FRIENDSHIP
                    let invitation = {
                        userFrom: user_from_id,
                        userTo: user_to._id
                    }
                    let invitationReverse = {
                        userFrom: user_to._id,
                        userTo: user_from_id
                    }
                    let criterioInvitation = {
                        $or: [invitation, invitationReverse]
                    }

                    //check if there is an invitation already (both ways)
                    gestorBD.obtainInvitations(criterioInvitation, function (invitations) {
                        if (invitations != null && invitations.length > 0) {
                            res.redirect("/users" +
                                "?mensaje=Ya existe una invitación" +
                                "&messageType=alert-danger ");

                        } else {
                            // NO INVITATIONS ALREADY SENT

                            //check if it's the same user
                            if (user_from.email == user_to.email) {
                                res.redirect("/users" +
                                    "?message=No te puedes enviar invitaciones" +
                                    "&messageType=alert-danger ");

                            } else {

                                //check if they are already friends.
                                gestorBD.obtainFriendships(criterioInvitation, function (friendships, total) {
                                    if (friendships != null && friendships.length > 0) {
                                        res.redirect("/users" +
                                            "?message=¡Ya es tu amigo!" +
                                            "&messageType=alert-danger ");
                                    } else {

                                        // NO FRIENDS ALREADY
                                        gestorBD.insertInvitation(invitation, function (id) {
                                            if (id == null) {

                                                //ERROR MANAGEMENT
                                                //TODO
                                                res.redirect("/users" +
                                                    "?message=Hubo un error" +
                                                    "&messageType=alert-danger ");
                                            } else {
                                                res.redirect("/users" +
                                                    "?message=Se envió correctamente" +
                                                    "&messageType=alert-success ");
                                            }
                                        })
                                    }
                                })
                            }
                        }
                    });
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
                    //TODO error
                    res.redirect("/users" +
                        "?mensaje=No hay ninguna invitación"+
                        "&messageType=alert-warning ");
                } else {


                    if(invitations.length==0){
                        let users = [];
                        manageInvitations(users, total, pg, req, res);
                    }
                    else {

                        let criterio = {
                            $or: invitations.map((i) => {
                                return {_id: gestorBD.mongo.ObjectID(i.userFrom.toString())}
                            })
                        }

                        gestorBD.getUsers(criterio, function (users) {
                            if (users == null) {
                                //TODO
                                res.redirect("/users");
                            } else {
                                manageInvitations(users, total, pg, req, res);
                            }
                        });
                    }
                }
            });
        }
    )
    ;


    function manageInvitations(usersInv, total, pg, req, res) {
        let users = usersInv;
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
            actual: pg,
            loggedIn: !!req.session.usuario,
        });
        res.send(respuesta);
    }

    app.get("/invitation/accept/:id", function (req, res) {

        if (typeof req.session.usuario == "undefined" || req.session.usuario == null) {
            res.send("No se ha loggeado");
        } else {
            //erase petition
            let userTo = gestorBD.mongo.ObjectID(req.session.usuario._id.toString());
            let userFrom = gestorBD.mongo.ObjectID(req.params.id.toString());

            let criterioPetition = {
                userTo: userTo,
                userFrom: userFrom
            }

            gestorBD.eraseInvitation(criterioPetition, function (invitation) {
                if (invitation == null) {
                    //TODO
                    res.send(respuesta);
                } else {

                    // insert friendship
                    let friendship = {
                        userFrom: userFrom,
                        userTo: userTo
                    }
                    gestorBD.insertFriendship(friendship, function (id) {
                        if (id == null) {
                            //TODO
                            //there was an error adding?
                        } else {
                            //redirect to list of friends.
                            res.redirect("/users" +
                                "?mensaje=Se ha añadido a un amigo"+
                                "&messageType=alert-success ");
                        }
                    })
                }
            })
        }
    });


}
