module.exports = function (app, gestorBD) {

    ///message/unread"
    app.post("/api/message/unread", function (req, res) {
        //get friend
        let friend = req.body.friend;

        //criteria
        let criteria = {
            emisor: friend.email,
            destino: res.usuario,
            leido: false
        }

        gestorBD.obtainNonreadMessages(criteria, function(messagesNum){
            //return a friend

            res.status(200);
            friend.numNonReadMessages = messagesNum;
            res.json(JSON.stringify(friend));
        });
        //criteria
    });
    app.post("/api/conversation", function (req, res) {
        let users = {
            $or: [{
                emisor: req.body.u1,
                destino: res.usuario
            }, {
                emisor: res.usuario,
                destino: req.body.u1
            }]
        };
        gestorBD.obtainMessage(users, function (messages) {
            if (messages == null) {
                res.status(500);
                res.json({error: "conversación no encontrada"})
            } else {
                res.status(200);
                res.send(JSON.stringify(messages))
            }
        })
    });

    function checkReceptor(criterio, usuario, callback) {
        gestorBD.obtainMessage(criterio, function (messages) {
            if (usuario && messages[0].destino === usuario) {
                callback(null)
            } else {
                callback("No tiene permisos para acceder a ese mensaje")
            }
        })
    }

    app.put("/api/message/markRead/:id", function (req, res) {
        let criterio = {"_id": gestorBD.mongo.ObjectID(req.params.id)};
        checkReceptor(criterio, res.usuario, function (errorMessage) {
            if (errorMessage == null) {
                gestorBD.markRead(criterio, function (result) {
                    if (result == null) {
                        res.status(500);
                        res.json({
                            error: "se ha producido un error"
                        })
                    } else {
                        res.status(200);
                        res.json({
                            mensaje: "mensaje marcado como leido",
                            _id: req.params.id
                        })
                    }
                })
            } else {
                res.status(500);
                res.json({
                    error: errorMessage
                })
            }
        })
    });


    app.post("/api/message/send", function (req, res) {
        let criterioEmisor = {
            email: res.usuario
        }

        let criterioSender = {
            email: req.body.destino
        }

        let criterioUsers = {
            $or: [criterioEmisor, criterioSender]
        }

        gestorBD.getUsers(criterioUsers, function (users, total) {
            if (users == null || users.length < 2) {

                res.status(500);
                res.json({
                    error: "Error al obtener los usuarios del mensaje "
                });
            } else {
                //both users were found

                //create the message
                let message = {
                    emisor: res.usuario, // -> BY EMAIL
                    destino: req.body.destino, // -> BY EMAIL
                    texto: req.body.texto,
                    leido: false,
                }

                //insert the message
                gestorBD.insertMessage(message, (id) => {
                    if (id) {
                        let criterio = {
                            $or: [{
                                userFrom: gestorBD.mongo.ObjectID(users[0]._id),
                                userTo: gestorBD.mongo.ObjectID(users[1]._id)
                            }, {
                                userFrom: gestorBD.mongo.ObjectID(users[1]._id),
                                userTo: gestorBD.mongo.ObjectID(users[0]._id)
                            }]
                        };
                        gestorBD.updateDate(criterio, function (result) {

                        });
                        res.status(200);
                        res.json({
                            mensaje: "Enviado correctamente",
                            _id: id
                        });
                    } else {
                        res.status(500);
                        res.json({
                            error: "Error: no se pudo mandar el mensaje"
                        });
                    }
                });
            }
        });

    });


    app.get("/api/friendships", function (req, res) {
        let criterio = {email: res.usuario};

        gestorBD.getUsers(criterio, function (users, total) {
            if (users == null) {
                console.log('NULL');

            } else {

                let userSessionCompl = users[0];
                let userSessionId = userSessionCompl._id.toString();
                let userSession = gestorBD.mongo.ObjectID(userSessionCompl._id.toString());

                let criterioFrom = {
                    userFrom: userSession
                }
                let criterioTo = {
                    userTo: userSession
                }
                let criterioFriend = {
                    $or: [criterioFrom, criterioTo]
                }

                gestorBD.obtainFriendships(criterioFriend, function (friendships) {

                    if (friendships == null) {
                        res.status(500);
                        res.json({
                            error: "Error al obtener los amigos "
                        });
                    } else {
                        if (friendships.length == 0) {
                            let users = [];
                            res.status(200);
                            res.json(JSON.stringify(users));

                        } else {

                            //check user
                            //criterio -> pillar todos userTo && userFrom
                            let criterioArray = []
                            let sorted = friendships.sort((a, b) => b.dateUpdate - a.dateUpdate);
                            //para cada amistad ->
                            for (let i = 0; i < friendships.length; i++) {
                                criterioArray.push({_id: gestorBD.mongo.ObjectID(friendships[i].userFrom.toString())});
                                criterioArray.push({_id: gestorBD.mongo.ObjectID(friendships[i].userTo.toString())});
                            }
                            let final = criterioArray.filter(
                                (c) =>
                                    c._id.toString() !== userSessionId
                            );

                            let criterio = {
                                $or: final
                            }
                            gestorBD.getUsers(criterio, (users) => {

                                if (users == null) {
                                    //redirect?
                                    res.status(500);
                                    res.json({
                                        error: "Error al listar los amigos "
                                    });
                                } else {

                                    let usersAux = [];
                                    //eliminar de users al usuario en sesión
                                    let sortedUsers = [];
                                    sorted.forEach(function (key) {
                                        let found = false;
                                        users = users.filter(function (user) {
                                            if (!found && (user._id.toString() === key.userFrom.toString() || user._id.toString() === key.userTo.toString())){
                                                sortedUsers.push(user);
                                                found = true;
                                                return false;
                                            } else
                                                return true;
                                        })
                                    });

                                    for (let i = 0; i < sortedUsers.length; i++) {
                                        let userSpecific = sortedUsers[i]._id.toString();

                                        if (userSpecific != userSessionId) {
                                            usersAux.push(sortedUsers[i]);
                                        }
                                    }
                                    res.status(200);
                                    res.json(JSON.stringify(usersAux));
                                }
                            });

                        }


                    }
                });

            }
        });
    });


    app.post("/api/autenticar/", function (req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        let criterio = {
            email: req.body.email,
            password: seguro
        }

        gestorBD.getUsers(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                res.status(401);
                res.json({
                    autenticado: false
                })
            } else {
                var token = app.get('jwt').sign(
                    {usuario: criterio.email, tiempo: Date.now() / 1000},
                    "secreto");
                res.status(200);
                res.json({
                    autenticado: true,
                    token: token
                });
            }
        });
    });


}
