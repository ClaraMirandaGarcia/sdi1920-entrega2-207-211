module.exports = function (app, gestorBD) {

    app.post("/api/conversation", function (req, res) {
        let users = {
            $or: [{
                emisor: req.body.u1,
                destino: req.body.u2
            }, {
                emisor: req.body.u2,
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
            console.log(messages[0].destino)
            console.log(usuario)
            if (usuario && messages[0].destino === usuario) {
                callback(null)
            } else {
                callback("No tiene permisos para acceder a ese mensaje")
            }
        })
    }
    app.put("/api/message/markRead/:id", function (req, res ) {
        let criterio = {"_id": gestorBD.mongo.ObjectID(req.params.id)};
        checkReceptor(criterio,res.usuario, function (errorMessage) {
            if (errorMessage == null){
                gestorBD.markRead(criterio,function (result) {
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
            }else {
                res.status(500);
                res.json({
                    error: errorMessage
                })
            }
        })
    });
    app.post("/api/message/send", function (req, res) {

        let criterioEmisor = {
            email: res.usuario.email
        }

        let criterioSender = {
            email: res.body.destino
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
                let user1 = users[0];
                let user2 = users[1];

                let user1Id = gestorBD.mongo.ObjectID(user1._id.toString());
                let user2Id = gestorBD.mongo.ObjectID(user2._id.toString());

                //check if they're friends by their ids
                let criterioFrom = {
                    userFrom: user1Id,
                    userTo: user2Id
                }

                let criterioTo = {
                    userFrom: user2Id,
                    userTo: user1Id
                }

                let criterioFriend = {
                    $or: [criterioFrom, criterioTo]
                }

                gestorBD.obtainFriendships(criterioFriend, function (friendships) {
                    if (friendships == null || friendships.length < 1) {
                        //they are not friends
                        res.status(500);
                        res.json({
                            error: "No eres amigo de ese usuario"
                        });
                    } else {
                        //they are friends

                        //create the message
                        let message = {
                            emisor: res.usuario.email, // -> BY EMAIL
                            destino: req.body.destino, // -> BY EMAIL
                            texto: req.body.texto,
                            leido: false,
                        }

                        //insert the message
                        gestorBD.insertMessage(message, (id) => {
                            if (id) {
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
                })
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

                        if(friendships.length == 0){
                            let users = [];

                            res.status(200);
                            res.json(JSON.stringify(users));

                        } else{

                            //check user
                            let criterio = {
                                $or: friendships.map((f) => {
                                    return {_id: gestorBD.mongo.ObjectID(f.userFrom.toString())}
                                })
                            }

                            gestorBD.getUsers(criterio, (users) => {

                                if (users == null) {
                                    //redirect?
                                    res.status(500);
                                    res.json({
                                        error: "Error al listar los amigos "
                                    });
                                } else {


                                    res.status(200);
                                    res.json(JSON.stringify(users));
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
