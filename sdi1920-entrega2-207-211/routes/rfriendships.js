module.exports = function (app, swig, gestorBD) {


    //amigos de x
    app.get("/friendships", function (req, res) {

            let userSession = gestorBD.mongo.ObjectID(req.session.usuario._id.toString());

            let userSessionId = req.session.usuario._id.toString();

            let criterioFrom = {
                userFrom: userSession
            }
            let criterioTo = {
                userTo: userSession
            }
            let criterioFriend = {
                $or: [criterioFrom, criterioTo]
            }


            let pg = parseInt(req.query.pg);
            if (req.query.pg == null) {
                pg = 1;
            }

            gestorBD.obtainFriendshipsPg(criterioFriend, pg, function (friendships, total) {
                if (friendships == null) {
                    //TODO
                    res.send("Error al listar ");
                } else {


                    if (friendships.length == 0) {

                        let users = [];
                        manageFriendships(users, total, pg, req, res);

                    } else {
                        //tenemos las amistades

                        //criterio -> pillar todos userTo && userFrom
                        let criterioArray = []

                        //para cada amistad ->
                        for(let i = 0; i<friendships.length; i++){
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

                        gestorBD.getUsers(criterio, function (users) {
                            if (users == null) {
                                //TODO
                                //ERROR
                                res.redirect("/users" +
                                    "?message=Error interno" +
                                    "&messageType=alert-warning ");
                            } else {
                                //tenemos todos los usuarios que forman parte de la relación de friendhips
                                //tenemos que filtrarlos, quitar los usuarios que seamos nosotros mismos.


                                //list users name, surname, email

                                let final = users.filter(
                                    (user) =>
                                        user._id.toString() !== userSessionId
                                );
                                manageFriendships(final, total, pg, req, res)
                            }
                        });
                    }
                }
            });
        }
    )
    ;

    function manageFriendships(usersFriends, total, pg, req, res) {
        let final = usersFriends;

        //paginación
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
        let respuesta = swig.renderFile('views/bfriendships.html', {
            users: final,
            paginas: paginas,
            actual: pg,
            loggedIn: !!req.session.usuario,
        });
        res.send(respuesta);
    }


}
