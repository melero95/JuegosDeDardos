package preferencias;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import modelos.PartidaEnCurso;

public final class GestorPartidaEnCurso {

    //Preferencias ---------------------------------------------------------------

    private static final String NOMBRE_PREFERENCIAS =
            "preferencias_partida_en_curso";

    private static final String CLAVE_PARTIDA =
            "partida_guardada";

    //Gson -----------------------------------------------------------------------

    private static final Gson GSON = new Gson();

    //Constructor privado --------------------------------------------------------

    private GestorPartidaEnCurso() {
        //Evita que se creen objetos de esta clase.
    }

    //Guardar partida ------------------------------------------------------------

    public static boolean guardarPartida(
            Context context,
            PartidaEnCurso partida
    ) {

        if (context == null || partida == null) {
            return false;
        }

        try {

            partida.setVersionGuardado(
                    PartidaEnCurso.VERSION_ACTUAL
            );

            partida.setFechaGuardado(
                    System.currentTimeMillis()
            );

            String jsonPartida =
                    GSON.toJson(partida);

            SharedPreferences preferencias =
                    obtenerPreferencias(context);

            return preferencias.edit()
                    .putString(
                            CLAVE_PARTIDA,
                            jsonPartida
                    )
                    .commit();

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    //Cargar partida -------------------------------------------------------------

    public static PartidaEnCurso cargarPartida(
            Context context
    ) {

        if (context == null) {
            return null;
        }

        SharedPreferences preferencias =
                obtenerPreferencias(context);

        String jsonPartida =
                preferencias.getString(
                        CLAVE_PARTIDA,
                        null
                );

        if (jsonPartida == null
                || jsonPartida.trim().isEmpty()) {

            return null;
        }

        try {

            PartidaEnCurso partida =
                    GSON.fromJson(
                            jsonPartida,
                            PartidaEnCurso.class
                    );

            if (!esPartidaValida(partida)) {

                eliminarPartida(context);
                return null;
            }

            return partida;

        } catch (JsonSyntaxException e) {

            e.printStackTrace();

            eliminarPartida(context);
            return null;

        } catch (Exception e) {

            e.printStackTrace();

            eliminarPartida(context);
            return null;
        }
    }

    //Comprobar si existe una partida -------------------------------------------

    public static boolean existePartida(
            Context context
    ) {

        if (context == null) {
            return false;
        }

        SharedPreferences preferencias =
                obtenerPreferencias(context);

        String jsonPartida =
                preferencias.getString(
                        CLAVE_PARTIDA,
                        null
                );

        return jsonPartida != null
                && !jsonPartida.trim().isEmpty();
    }

    //Eliminar partida -----------------------------------------------------------

    public static boolean eliminarPartida(
            Context context
    ) {

        if (context == null) {
            return false;
        }

        SharedPreferences preferencias =
                obtenerPreferencias(context);

        return preferencias.edit()
                .remove(CLAVE_PARTIDA)
                .commit();
    }

    //Validar partida cargada ----------------------------------------------------

    private static boolean esPartidaValida(
            PartidaEnCurso partida
    ) {

        if (partida == null) {
            return false;
        }

        if (partida.getVersionGuardado()
                != PartidaEnCurso.VERSION_ACTUAL) {

            return false;
        }

        if (partida.getTipoPartida() == null
                || partida.getTipoPartida().trim().isEmpty()) {

            return false;
        }

        if (partida.getModoJuego() == null
                || partida.getModoJuego().trim().isEmpty()) {

            return false;
        }

        if (partida.getNombresJugadores() == null
                || partida.getNombresJugadores().isEmpty()) {

            return false;
        }

        if (partida.getNumeroJugadores() <= 0) {
            return false;
        }

        switch (partida.getTipoPartida()) {

            case PartidaEnCurso.TIPO_PUNTOS:
                return partida.getEstadoPuntos() != null;

            case PartidaEnCurso.TIPO_CRIQUET:
                return partida.getEstadoCriquet() != null;

            case PartidaEnCurso.TIPO_RONDAS:
                return partida.getEstadoRondas() != null;

            default:
                return false;
        }
    }

    //Obtener SharedPreferences --------------------------------------------------

    private static SharedPreferences obtenerPreferencias(
            Context context
    ) {

        return context.getApplicationContext()
                .getSharedPreferences(
                        NOMBRE_PREFERENCIAS,
                        Context.MODE_PRIVATE
                );
    }
}