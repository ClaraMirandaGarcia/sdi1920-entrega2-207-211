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
                    //manejo error
                    res.send("Error al listar ");
                } else {

                    //check user
                    let criterio = {
                        $or: friendships.map((f) => {
                            return {_id: gestorBD.mongo.ObjectID(f.userFrom.toString())}
                        })
                    }
                    gestorBD.getUsers(criterio, function (users) {

                        if (users == null || users.length == 0) {
                            //redirect?
                        } else {
                            //tenemos todos los usuarios que forman parte de la relación de friendhips
                            //tenemos que filtrarlos, quitar los usuarios que seamos nosotros mismos.

                            //list users name, surname, email
                            let final = users.filter(
                                (user) =>
                                    user._id !== userSession
                            );

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

                    });


                }
            });
        }
    )
    ;


}
