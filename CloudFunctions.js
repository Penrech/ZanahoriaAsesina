/* eslint-disable */
const functions = require('firebase-functions');
// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);




exports.eliminateUser = functions.database.ref("/jugadores/{pushID}/active")
    .onUpdate((change,context) =>{
        if(change.after.val() === "ELIMINATING"){
            const juegoRef = admin.database().ref("juego/activos");
            juegoRef.transaction(function(active){
              if(active == 1){
                 return;
              }
              active--;
              return active;
            }, function(error,committed,snapshot){
  
                
                if(error != null){
                console.log('Transaction failed abnormally!'+ error);
                return change.after.ref.parent.child("active").set(change.before.val());
                //TODO: mandar mensaje transferencia error
                }
                
                else if(committed){
                    var jugadorId = change.after.ref.parent.key;
                    var jugadorData = null;
                    var killerData = null;
                    var killerId = null;
                    getKillerAndUserData(jugadorId).then(values =>{
                        jugadorData = values[0].val();
                        values[1].forEach(item =>{
                            killerId = item.key;
                            killerData = item.val();
                        })
                        if(jugadorData == null || killerData == null){
                            console.log("error borrando jugador");
                            return change.after.ref.parent.child("active").set(change.before.val());
                        }
                        else{
                        updateAllTheData(jugadorId,killerId,jugadorData,killerData,snapshot.val())
                        .then(success =>{
                            console.log("operacion realizada con exito.");
                            return null;
                        }).catch(error => {
                            console.log("error al actualizar los datos");
                            return change.after.ref.parent.child("active").set(change.before.val());
                        })     
                        }
                    }).catch(e => {
                        console.log("Error promise 1 and 2" + e);
                        return change.after.ref.parent.child("active").set(change.before.val());
                    })
       
                }
            });
        }
        else if(change.after.val() === "LIMBO"){
            console.log("valor cambiado a limbo");
             if(change.before.val() === "ELIMINATING"){
                const juegoRef = admin.database().ref("juego/activos");
                juegoRef.transaction(function(active){
                  active++;
                  return active;
                });
                console.log("Error transacción cambio active");
                return null;
            }
             else{
                return null;
            }
        }
        else if(change.after.val() == "WINNER"){
            return admin.database().ref("juego").child("estado").set("OVER");
        }
        else if(change.after.val() === "ACTIVE"){
            console.log("valor cambiado a active");
             if(change.before.val() === "ELIMINATING"){
                const juegoRef = admin.database().ref("juego/activos");
                juegoRef.transaction(function(active){
                  active++;
                  return active;
                });
                console.log("Error transacción cambio active");
                return null;
            }
             else{
                return null;
            }
        }
        else if(change.after.val() == "ELIMINATED"){
            var lookForReports1 = admin.database().ref("reportes").orderByChild("ReportedId").equalTo(change.after.ref.parent.key).once("value");
            var lookForReports2 = admin.database().ref("reportes").orderByChild("ReporterId").equalTo(change.after.ref.parent.key).once("value");
            Promise.all([lookForReports1,lookForReports2]).then(result =>{
                var updates = {};
                result[0].forEach(item=>{
                    if(item.val() != null){
                        updates["reportes/"+item.key]=null;
                    }
                });
                result[1].forEach(item=>{
                    if(item.val() != null){
                        updates["reportes/"+item.key]=null;
                    }
                });
                
                if(Object.keys(updates).length <1){
                    return null;
                }
                else{
                    admin.database().ref().update(updates)
                    .then(result =>{
                        return null;
                    }).catch(error=>{
                        console.log("Error borrando reportes de usuario eliminado");
                        return null;
                    })
                }
                
            })
        }
        else{
            return null;
        }
    
});

function getKillerAndUserData(idJugador){
        const getUserData = admin.database().ref("jugadores").child(idJugador).once("value");
        const getKillerData = admin.database().ref("/jugadores").orderByChild("target").equalTo(idJugador).once("value");
        return Promise.all([getUserData,getKillerData]);   
    
}
    
    
function updateAllTheData(idJugador,idKiller,dataJugador,dataKiller,activeNum){
                            var updates ={};
                            var dataFinalJugador = dataJugador;
                            var dataFinalKiller = dataKiller;
                            var date = new Date();
                            var dateTime = date.getTime();
                            var killerName = dataKiller.nom + " " + dataKiller.cognom;
                            var victimName = dataJugador.nom + " " + dataJugador.cognom;
                            var newKey = admin.database().ref().child("estadisticas").push().key;
                            var statics = {
                                    date: dateTime,
                                    nomApKiller: killerName,
                                    nomApVictim: victimName
                                };
                             updates["estadisticas/"+newKey]= statics;
                            if (activeNum == 1){
                                console.log(dataFinalJugador.nom + "Entra en active 1");
                                dataFinalKiller.target = null;
                                dataFinalJugador.target = null;
                                dataFinalJugador.active = "ELIMINATED";
                                dataFinalKiller.ranking = activeNum;
                                dataFinalKiller.active = "WINNER";
                                dataFinalJugador.ranking = activeNum+1;
                                
                            }else{
                                console.log(dataFinalJugador.nom + "Entra en active mayor que 1");
                                dataFinalKiller.target = dataFinalJugador.target;
                                dataFinalJugador.target = null;
                                dataFinalJugador.active = "ELIMINATED";
                                dataFinalJugador.ranking = activeNum+1;
                            }
                            updates["jugadores/"+idJugador]=dataFinalJugador;
                            updates["jugadores/"+idKiller]= dataFinalKiller;
                            return admin.database().ref().update(updates);

                        
}



exports.gameChange = functions.database.ref("/juego/estado")
    .onUpdate((change,context) =>{
        if(change.after.val() == "ACTIVATING"){
            var allPlayers =[];
                admin.database().ref("jugadores").once("value")
            .then(data =>{
                data.forEach(item =>{
                    var temp = item.val();
                    temp.key = item.key;
                    allPlayers.push(temp);
                })
                assignPlayers(allPlayers)
                    .then(result =>{
                    return change.after.ref.parent.child("estado").set("ACTIVE");
                }).catch(e =>{
                    console.log("Error asignando jugadores: "+e);
                    return change.after.ref.parent.child("estado").set("INACTIVE");
                })
            }).catch(error =>{
                console.log("error recibiendo lista de jugadores: "+error);
                return change.after.ref.parent.child("estado").set("INACTIVE");
            });
            
        }
        else if(change.after.val() == "REESTABLISHING"){
             var allPlayers =[];
                admin.database().ref("jugadores").once("value")
            .then(data =>{
                data.forEach(item =>{
                    var temp = item.val();
                    temp.key = item.key;
                    allPlayers.push(temp);
                })
                restartGameData(allPlayers)
                    .then(result =>{
                    return change.after.ref.parent.child("estado").set("INACTIVE");
                }).catch(e =>{
                    console.log("Error asignando jugadores: "+e);
                    return change.after.ref.parent.child("estado").set("OVER");
                })
            }).catch(error =>{
                console.log("error recibiendo lista de jugadores: "+error);
                return change.after.ref.parent.child("estado").set("OVER");
            });
        }
        else if(change.after.val() == "OVER"){
            if(change.before.val() == "REESTABLISHING"){
                //todo: error restableciendo juego
                console.log("Error restableciendo los datos del juego");
            }
        }
        else if (change.after.val() == "INACTIVE"){
            if (change.before.val() == "ACTIVATING"){
                //todo: error iniciando juego
                console.log("Error iniciando el juego");
            }
            
        }
        
    
});

function restartGameData(playersArray){
    var updates = {};
    var cont = playersArray.length;
    playersArray.forEach(player =>{
        updates["jugadores/"+player.key+"/target"] = null;
        updates["jugadores/"+player.key+"/ranking"]= -1;
        updates["jugadores/"+player.key+"/active"]= "ACTIVE";
    });
    updates["juego/activos"]= cont;
    updates["estadisticas"]=null;
    return admin.database().ref().update(updates);
}

function assignPlayers(playersArray){
    var updates = {};
    for (var i = playersArray.length - 1; i > 0; i--) {
    var j = Math.floor(Math.random() * (i + 1));
    var temp = playersArray[i];
    playersArray[i] = playersArray[j];
    playersArray[j] = temp;
    }
    playersArray.forEach((player,index) =>{
        if(index == playersArray.length-1){
            updates["jugadores/"+player.key+"/target"] = playersArray[0].key;
        }
        else{
            updates["jugadores/"+player.key+"/target"] = playersArray[index+1].key;
        }
        
    });
    return admin.database().ref().update(updates);
    
    
    
    
}

