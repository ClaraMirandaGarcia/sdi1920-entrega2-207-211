module.exports = function (app, swig, gestorBD) {


    //amigos de x
    app.get("/friendships", function (req, res) {

            let userSession = gestorBD.mongo.ObjectID(req.session.usuario._id.toString());

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
                    //check user
                    let criterio = {
                        $or: friendships.map((f) => {
                            return {_id: gestorBD.mongo.ObjectID(f.userFrom.toString())}
                        })
                    }

                    if (friendships.length == 0) {
                        let users = [];
                        manageFriendships(users, total, pg, req, res);

                    } else {
                        gestorBD.getUsers(criterio, function (users) {
                            if (users == null) {
                                //TODO
                                //ERROR
                                res.redirect("/usuarios" +
                                    "?mensaje=Error interno" +
                                    "&tipoMensaje=alert-warning ");
                            } else {
                                //tenemos todos los usuarios que forman parte de la relación de friendhips
                                //tenemos que filtrarlos, quitar los usuarios que seamos nosotros mismos.


                                //list users name, surname, email
                                let final = users.filter(
                                    (user) =>
                                        user._id !== userSession
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
