
<h1>Juego Zanahoria Asesina</h1>

<h1>Tecnología</h1>
<ul> 
  <li> Android Studio(Java): https://developer.android.com/studio/intro/ </li>
  <li> Firebase: https://firebase.google.com/?hl=es-419 
    <ul>
      <li>Firebase Realtime Database : https://firebase.google.com/docs/database/?hl=es-419</li>
      <li>Firebase Authentication :  https://firebase.google.com/docs/auth/?hl=es-419</li>
      <li>Firebase Storage : https://firebase.google.com/docs/storage/?hl=es-419</li>
      <li>Firebase Cloud Functions : https://firebase.google.com/docs/functions/?hl=es-419</li>
    </ul>
  </li>
</ul>
<h1>Descripción</h1>
<p>Proyecto académico/personal de desarrollo de un prototipo de una aplicación de gestión de un juego del tipo gincana<p>
<p>Zanahoria asesina es un juego en el que un grupo de personas deben ir eliminandose entre ellos hasta que solo quede un ganador.
A cada participante se le asigna aletoriamente un jugador al que debe eliminar. Si este jugador elimina a su victima, este pasa a tene
que eliminar a la que era victima de su victima. Este proceso es una cola circular, por lo que a ningún usuario se le asignará una
victima que tenga otro usuario. El jugador elimina a su victima con el contacto de una zanahoria en el pecho de su victima, siempre y
cuando ningún otro jugador presencie dicha acción, en cuyo caso la acción no tendría valor.</p>
<p>Este juego se realiza de forma física, presencial, no es un juego virtual. El protipo aqui desarrollado permite gestionar el 
desarrollo del juego, mostrarle a los jugadores cuales son sus victimas, marcar a una victima como eliminada, reportar a otros jugadores
por mal comportamiento, mostrar estadísticas del juego y mostrar el ranking de jugadores.<p>
<p>La aplicación utiliza como servidor y base de datos Firebase, y funciona con un conjunto de funciones locales en conjunción con 
funciones que se ejecutan en el propio servidor de Firebase</p>
<p>El prototipo a nivel visual es bajo, y a nivel de funcionamiento es de bajo-medio</p>
