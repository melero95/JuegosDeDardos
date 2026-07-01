# Juegos de Dardos

Aplicación móvil para Android destinada a gestionar partidas de distintos juegos de dardos.

El proyecto permite configurar jugadores, seleccionar diferentes modalidades de juego, registrar las tiradas y mostrar el resultado final de cada partida.

Este repositorio se utiliza también para documentar y controlar el desarrollo progresivo de la aplicación.

## Estado del proyecto

Proyecto actualmente en desarrollo.

En este momento se está trabajando en la implementación de las partidas, el registro de resultados, las estadísticas y el almacenamiento persistente de los datos.

## Funcionalidades previstas

* Creación de nuevas partidas.
* Configuración del número de jugadores.
* Personalización del nombre y color de cada jugador.
* Selección del modo de juego.
* Registro de los puntos obtenidos con cada dardo.
* Control de turnos y rondas.
* Posibilidad de deshacer la última tirada.
* Confirmación antes de abandonar una partida.
* Continuación de partidas guardadas.
* Clasificación final de los jugadores.
* Historial de partidas.
* Estadísticas generales y por jugador.
* Opción de revancha al finalizar una partida.
* Gestión de ajustes de la aplicación.

## Modos de juego

La aplicación incluirá los siguientes modos:

* 301
* 501
* Cricket
* Cut Throat Cricket
* Double Down
* Around the Clock

## Tecnologías utilizadas

* Java
* Android Studio
* XML
* SQLite
* SharedPreferences
* Git
* GitHub

## Estructura del proyecto

El código está organizado en diferentes paquetes según su responsabilidad:

```text
activities/
    Pantallas y actividades de la aplicación.

adapters/
    Adaptadores utilizados para mostrar listas y clasificaciones.

modelos/
    Clases que representan los datos de la aplicación.

sqlite/
    Clases relacionadas con la base de datos SQLite.
```

Entre los principales modelos de datos se encuentran:

* Jugador
* Partida
* Turno
* Tirada

## Pantallas principales

La aplicación contará con las siguientes pantallas:

* Pantalla principal.
* Configuración de nueva partida.
* Partida de puntuación.
* Partida de Cricket.
* Partida por rondas.
* Pantalla de resultados.
* Historial de partidas.
* Estadísticas.
* Ajustes.

## Funcionamiento general

1. El usuario crea una nueva partida.
2. Selecciona el modo de juego.
3. Configura el número de jugadores y sus datos.
4. La aplicación inicia la pantalla correspondiente al modo seleccionado.
5. Se registran los dardos lanzados en cada turno.
6. Al finalizar la partida, se calcula la clasificación.
7. Los resultados y estadísticas se almacenan en la base de datos.
8. Se muestra la pantalla final con el ganador y el resto de participantes.

## Persistencia de datos

La aplicación utilizará distintos mecanismos de almacenamiento:

* `SharedPreferences` para guardar configuraciones y partidas temporales.
* SQLite para almacenar jugadores, partidas, tiradas y estadísticas.
* Objetos enviados mediante `Intent` para comunicar datos entre actividades.

## Objetivos del proyecto

Los principales objetivos de la aplicación son:

* Facilitar el seguimiento de partidas de dardos.
* Automatizar el cálculo de puntuaciones.
* Evitar errores al controlar turnos y rondas.
* Almacenar un historial completo de partidas.
* Proporcionar estadísticas útiles a los jugadores.
* Aplicar buenas prácticas de programación y organización de proyectos Android.

## Desarrollo

El proyecto se está desarrollando de forma incremental, registrando los avances mediante commits en Git.

Cada nueva funcionalidad se implementa, prueba y documenta antes de continuar con la siguiente parte del proyecto.

## Próximas tareas

* Completar las estadísticas de la pantalla de resultados.
* Implementar la opción de revancha.
* Finalizar el almacenamiento de partidas en SQLite.
* Implementar el historial de partidas.
* Desarrollar los modos de juego pendientes.
* Mejorar las animaciones y transiciones.
* Añadir pruebas de funcionamiento.

## Autor

Proyecto desarrollado por Luis como aplicación Android y proyecto académico.
