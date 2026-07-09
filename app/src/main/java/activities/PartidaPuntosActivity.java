package activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.activity.OnBackPressedCallback;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.productos.juegosdedardos.R;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;

public class PartidaPuntosActivity extends AppCompatActivity {

    //Extras recibidos ------------------------------------------------------------

    public static final String EXTRA_MODO_JUEGO = "modoJuego";
    public static final String EXTRA_MAX_RONDAS = "maxRondas";
    public static final String EXTRA_NOMBRES_JUGADORES = "nombresJugadores";
    public static final String EXTRA_COLOR_JUGADOR = "colorJugador";
    public static final String EXTRA_MOTIVO_FINALIZACION =
            "resultado_motivo_finalizacion";

    public static final String EXTRA_NUMERO_JUGADORES =
            "resultado_numero_jugadores";

    public static final String EXTRA_RONDAS_JUGADAS =
            "resultado_rondas_jugadas";

    public static final String EXTRA_NOMBRE_GANADOR =
            "resultado_nombre_ganador";

    public static final String EXTRA_PUNTUACIONES =
            "resultado_puntuaciones";

    public static final String EXTRA_COLORES =
            "resultado_colores";

    public static final String EXTRA_POSICIONES =
            "resultado_posiciones";

    public static final String EXTRA_INDICES_ORIGINALES =
            "resultado_indices_originales";

    //Motivos de finalización ----------------------------------------------------

    public static final String MOTIVO_GANADOR =
            "ganador";

    public static final String MOTIVO_TODOS_FINALIZADOS =
            "todos_finalizados";

    public static final String MOTIVO_MAX_RONDAS =
            "max_rondas";

    //Constantes generales --------------------------------------------------------

    private static final int MAX_DARDOS_TURNO = 3;
    private static final int MAX_JUGADORES = 6;

    //Configuración de la partida -------------------------------------------------

    private String modoJuego;

    private int numeroJugadores;
    private int puntuacionInicial;
    private int maxRondas;

    private ArrayList<String> nombresJugadores;

    //Estado general de la partida ------------------------------------------------

    private int jugadorActual;
    private int rondaActual;

    private int[] puntuacionesJugadores;
    private int[] coloresJugadores;

    private boolean[] jugadoresFinalizados;

    private boolean partidaFinalizada;

    //Orden de finalización de los jugadores --------------------

    private final ArrayList<Integer> ordenFinalizacion =
            new ArrayList<>();

    //Estado del turno actual -----------------------------------------------------

    private int dardoActual;
    private int puntosTurnoActual;

    private final int[] puntosDardos =
            new int[MAX_DARDOS_TURNO];

    private final String[] textosDardos =
            new String[MAX_DARDOS_TURNO];

    //Historial temporal ----------------------------------------------------------

    private final ArrayList<RegistroTiradaTemporal> historialTiradas =
            new ArrayList<>();

    //Historial completo para deshacer --------------------------------------------

    private final Deque<EstadoPartida> historialEstados =
            new ArrayDeque<>();

    //Información general ---------------------------------------------------------

    private TextView txtModoJuego;
    private TextView txtJugadorActual;

    private TextView txtRondaActual;
    private TextView txtMaxRondas;

    //Información del turno -------------------------------------------------------

    private TextView txtNumeroDardos;

    private TextView txtDardo1;
    private TextView txtDardo2;
    private TextView txtDardo3;

    private ImageView imgDardo1;
    private ImageView imgDardo2;
    private ImageView imgDardo3;

    //Marcador central ------------------------------------------------------------

    private TextView txtNombreJugadorActual;
    private TextView txtPuntuacionJugadorActual;

    //Marcadores laterales --------------------------------------------------------

    private final LinearLayout[] panelesJugadores =
            new LinearLayout[MAX_JUGADORES];

    private final TextView[] txtNombresJugadores =
            new TextView[MAX_JUGADORES];

    private final TextView[] txtPuntuacionesJugadores =
            new TextView[MAX_JUGADORES];

    //Multiplicadores -------------------------------------------------------------

    private RadioGroup grupoMultiplicadores;

    private RadioButton radioX1;
    private RadioButton radioX2;
    private RadioButton radioX3;

    //Botones especiales ----------------------------------------------------------

    private Button btnFuera;
    private Button btnBull;

    private Button btnDeshacerTirada;
    private Button btnSiguienteTurno;
    private Button btnVerMarcador;
    private Button btnSalirPartida;

    //Botones de puntuación -------------------------------------------------------

    private final Button[] botonesPuntuacion =
            new Button[20];

    //Control de animación del cambio de turno --------------------------------------

    private boolean animacionCambioTurnoActiva = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Configuro que la parete superior sea del mismo color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.blue));

        setContentView(R.layout.activity_partida_puntos);

        inicializarVistas();
        recibirDatosPartida();
        prepararPartida();
        configurarListeners();
        configurarBotonAtras();
        actualizarInterfazCompleta();
    }

    //Inicialización de vistas ----------------------------------------------------

    private void inicializarVistas() {

        txtModoJuego = findViewById(R.id.txtModoJuego);
        txtJugadorActual = findViewById(R.id.txtTurnoDe);

        txtRondaActual = findViewById(R.id.txtRondaActual);
        txtMaxRondas = findViewById(R.id.txtMaxRondas);

        txtNumeroDardos = findViewById(R.id.txtNumeroDardos);

        txtDardo1 = findViewById(R.id.txtTiradaDardo1);
        txtDardo2 = findViewById(R.id.txtTiradaDardo2);
        txtDardo3 = findViewById(R.id.txtTiradaDardo3);

        imgDardo1 = findViewById(R.id.imgDardo1);
        imgDardo2 = findViewById(R.id.imgDardo2);
        imgDardo3 = findViewById(R.id.imgDardo3);

        txtNombreJugadorActual =
                findViewById(R.id.txtNombreJugadorActual);

        txtPuntuacionJugadorActual =
                findViewById(R.id.txtPuntuacionJugadorActual);

        //Jugador 1: columna izquierda, primera posición --------------------

        panelesJugadores[0] =
                findViewById(R.id.tarjetaJugador1);

        txtNombresJugadores[0] =
                findViewById(R.id.txtNombreJugador1);

        txtPuntuacionesJugadores[0] =
                findViewById(R.id.txtPuntuacionJugador1);

        //Jugador 2: columna derecha, primera posición --------------------

        panelesJugadores[1] =
                findViewById(R.id.tarjetaJugador3);

        txtNombresJugadores[1] =
                findViewById(R.id.txtNombreJugador3);

        txtPuntuacionesJugadores[1] =
                findViewById(R.id.txtPuntuacionJugador3);

        //Jugador 3: columna izquierda, segunda posición --------------------

        panelesJugadores[2] =
                findViewById(R.id.tarjetaJugador2);

        txtNombresJugadores[2] =
                findViewById(R.id.txtNombreJugador2);

        txtPuntuacionesJugadores[2] =
                findViewById(R.id.txtPuntuacionJugador2);

        //Jugador 4: columna derecha, segunda posición --------------------

        panelesJugadores[3] =
                findViewById(R.id.tarjetaJugador4);

        txtNombresJugadores[3] =
                findViewById(R.id.txtNombreJugador4);

        txtPuntuacionesJugadores[3] =
                findViewById(R.id.txtPuntuacionJugador4);

        //Jugador 5: columna izquierda, tercera posición --------------------

        panelesJugadores[4] =
                findViewById(R.id.tarjetaJugador5);

        txtNombresJugadores[4] =
                findViewById(R.id.txtNombreJugador5);

        txtPuntuacionesJugadores[4] =
                findViewById(R.id.txtPuntuacionJugador5);

        //Jugador 6: columna derecha, tercera posición --------------------

        panelesJugadores[5] =
                findViewById(R.id.tarjetaJugador6);

        txtNombresJugadores[5] =
                findViewById(R.id.txtNombreJugador6);

        txtPuntuacionesJugadores[5] =
                findViewById(R.id.txtPuntuacionJugador6);

        grupoMultiplicadores =
                findViewById(R.id.grupoMultiplicadores);

        radioX1 = findViewById(R.id.radioX1);
        radioX2 = findViewById(R.id.radioX2);
        radioX3 = findViewById(R.id.radioX3);

        btnFuera = findViewById(R.id.btnFuera);
        btnBull = findViewById(R.id.btnBull);

        btnDeshacerTirada =
                findViewById(R.id.btnDeshacerTirada);

        btnSiguienteTurno =
                findViewById(R.id.btnSiguienteTurno);

        btnVerMarcador =
                findViewById(R.id.btnVerMarcador);

        btnSalirPartida =
                findViewById(R.id.btnSalirPartida);

        botonesPuntuacion[0] = findViewById(R.id.btn1);
        botonesPuntuacion[1] = findViewById(R.id.btn2);
        botonesPuntuacion[2] = findViewById(R.id.btn3);
        botonesPuntuacion[3] = findViewById(R.id.btn4);
        botonesPuntuacion[4] = findViewById(R.id.btn5);
        botonesPuntuacion[5] = findViewById(R.id.btn6);
        botonesPuntuacion[6] = findViewById(R.id.btn7);
        botonesPuntuacion[7] = findViewById(R.id.btn8);
        botonesPuntuacion[8] = findViewById(R.id.btn9);
        botonesPuntuacion[9] = findViewById(R.id.btn10);
        botonesPuntuacion[10] = findViewById(R.id.btn11);
        botonesPuntuacion[11] = findViewById(R.id.btn12);
        botonesPuntuacion[12] = findViewById(R.id.btn13);
        botonesPuntuacion[13] = findViewById(R.id.btn14);
        botonesPuntuacion[14] = findViewById(R.id.btn15);
        botonesPuntuacion[15] = findViewById(R.id.btn16);
        botonesPuntuacion[16] = findViewById(R.id.btn17);
        botonesPuntuacion[17] = findViewById(R.id.btn18);
        botonesPuntuacion[18] = findViewById(R.id.btn19);
        botonesPuntuacion[19] = findViewById(R.id.btn20);
    }

    //Recepción de datos ----------------------------------------------------------

    private void recibirDatosPartida() {

        modoJuego = getIntent().getStringExtra(EXTRA_MODO_JUEGO);

        maxRondas = getIntent().getIntExtra(
                EXTRA_MAX_RONDAS,
                15
        );

        nombresJugadores = getIntent().getStringArrayListExtra(
                EXTRA_NOMBRES_JUGADORES
        );

        if (modoJuego == null || modoJuego.trim().isEmpty()) {
            modoJuego = "301";
        }

        if (nombresJugadores == null || nombresJugadores.isEmpty()) {
            nombresJugadores = new ArrayList<>();
            nombresJugadores.add("Jugador 1");
        }

        if (nombresJugadores.size() > MAX_JUGADORES) {
            nombresJugadores = new ArrayList<>(
                    nombresJugadores.subList(0, MAX_JUGADORES)
            );
        }

        numeroJugadores = nombresJugadores.size();

        if (maxRondas <= 0) {
            maxRondas = 15;
        }

        puntuacionInicial =
                obtenerPuntuacionInicial(modoJuego);

        coloresJugadores =
                new int[numeroJugadores];

        for (int i = 0; i < numeroJugadores; i++) {

            coloresJugadores[i] = getIntent().getIntExtra(
                    EXTRA_COLOR_JUGADOR + (i + 1),
                    R.color.jugador_blanco
            );
        }
    }

    //Obtención de puntuación inicial --------------------------------------------

    private int obtenerPuntuacionInicial(String modo) {

        if ("501".equalsIgnoreCase(modo)) {
            return 501;
        }

        return 301;
    }

    //Preparación inicial ---------------------------------------------------------

    private void prepararPartida() {


        jugadorActual = 0;
        rondaActual = 1;

        dardoActual = 0;
        puntosTurnoActual = 0;

        partidaFinalizada = false;

        puntuacionesJugadores =
                new int[numeroJugadores];

        jugadoresFinalizados =
                new boolean[numeroJugadores];

        Arrays.fill(
                puntuacionesJugadores,
                puntuacionInicial
        );

        Arrays.fill(
                jugadoresFinalizados,
                false
        );

        Arrays.fill(puntosDardos, 0);
        Arrays.fill(textosDardos, null);

        ordenFinalizacion.clear();
        historialEstados.clear();
    }

    //Configuración de botones ----------------------------------------------------

    private void configurarListeners() {

        for (int i = 0; i < botonesPuntuacion.length; i++) {

            final int numeroCasilla = i + 1;

            botonesPuntuacion[i].setOnClickListener(view -> {

                int multiplicador =
                        obtenerMultiplicadorSeleccionado();

                registrarDardo(
                        numeroCasilla,
                        multiplicador
                );
            });
        }

        btnFuera.setOnClickListener(view ->
                registrarDardoEspecial(
                        0,
                        "OUT"
                )
        );

        btnBull.setOnClickListener(view -> {

            int multiplicador =
                    obtenerMultiplicadorSeleccionado();

            if (multiplicador == 1) {

                registrarDardoEspecial(
                        25,
                        "25"
                );

            } else if (multiplicador == 2) {

                registrarDardoEspecial(
                        50,
                        "D25"
                );

            } else {

                Toast.makeText(
                        this,
                        "La diana no admite multiplicador x3",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnDeshacerTirada.setOnClickListener(view ->
                deshacerUltimaTirada()
        );

        btnSiguienteTurno.setOnClickListener(view ->
                finalizarTurnoManual()
        );

        btnVerMarcador.setOnClickListener(view ->
                mostrarDialogoMarcador()
        );

        btnSalirPartida.setOnClickListener(view ->
                mostrarDialogoSalirPartida()
        );
    }

    //Confirmación para salir de la partida --------------------

    private void mostrarDialogoSalirPartida() {

        new AlertDialog.Builder(this)
                .setTitle("Salir de la partida")
                .setMessage(
                        "La partida actual se perderá.\n\n"
                                + "¿Seguro que deseas salir?"
                )
                .setPositiveButton(
                        "Salir",
                        (dialog, which) -> volverAlMenuPrincipal()
                )
                .setNegativeButton(
                        "Cancelar",
                        null
                )
                .show();
    }

    //Regreso al menú principal --------------------

    private void volverAlMenuPrincipal() {

        Intent intent = new Intent(
                PartidaPuntosActivity.this,
                MainActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
        finish();
    }

    //Multiplicador seleccionado --------------------------------------------------

    private int obtenerMultiplicadorSeleccionado() {

        int idSeleccionado =
                grupoMultiplicadores.getCheckedRadioButtonId();

        if (idSeleccionado == R.id.radioX2) {
            return 2;
        }

        if (idSeleccionado == R.id.radioX3) {
            return 3;
        }

        return 1;
    }

    //Registro de un número -------------------------------------------------------

    private void registrarDardo(
            int numeroCasilla,
            int multiplicador
    ) {

        if (!sePuedeRegistrarDardo()) {
            return;
        }

        int puntosReales =
                numeroCasilla * multiplicador;

        String textoDardo;

        if (multiplicador == 2) {

            textoDardo =
                    "D" + numeroCasilla;

        } else if (multiplicador == 3) {

            textoDardo =
                    "T" + numeroCasilla;

        } else {

            textoDardo =
                    String.valueOf(numeroCasilla);
        }

        registrarResultadoDardo(
                puntosReales,
                textoDardo
        );
    }

    //Registro de diana o fuera ---------------------------------------------------

    private void registrarDardoEspecial(
            int puntos,
            String textoDardo
    ) {

        if (!sePuedeRegistrarDardo()) {
            return;
        }

        registrarResultadoDardo(
                puntos,
                textoDardo
        );
    }

    //Comprobación antes de registrar --------------------------------------------

    private boolean sePuedeRegistrarDardo() {

        if (partidaFinalizada) {
            return false;
        }

        if (dardoActual >= MAX_DARDOS_TURNO) {

            Toast.makeText(
                    this,
                    "Ya se han lanzado los tres dardos",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }

    //Registro común de la tirada -------------------------------------------------

    private void registrarResultadoDardo(
            int puntos,
            String textoDardo
    ) {

        guardarEstadoActual();

        puntosDardos[dardoActual] = puntos;
        textosDardos[dardoActual] = textoDardo;

        puntosTurnoActual += puntos;
        dardoActual++;

        grupoMultiplicadores.check(R.id.radioX1);

        actualizarInformacionTurno();
        actualizarEstadoBotones();

        int puntuacionAntes =
                puntuacionesJugadores[jugadorActual];

        int puntuacionProvisional =
                puntuacionAntes - puntosTurnoActual;

        if (puntuacionProvisional == 0) {

            finalizarJugadorEnCero(
                    puntuacionAntes
            );

            return;
        }

        if (puntuacionProvisional < 0) {

            finalizarTurnoPasado(
                    puntuacionAntes
            );

            return;
        }

        if (dardoActual >= MAX_DARDOS_TURNO) {
            finalizarTurno();
        }
    }

    //Guardado del estado actual --------------------------------------------------

    private void guardarEstadoActual() {

        historialEstados.push(
                new EstadoPartida(
                        puntuacionesJugadores.clone(),
                        jugadoresFinalizados.clone(),
                        new ArrayList<>(ordenFinalizacion),
                        jugadorActual,
                        rondaActual,
                        dardoActual,
                        puntosTurnoActual,
                        puntosDardos.clone(),
                        textosDardos.clone(),
                        historialTiradas.size()
                )
        );
    }

    //Deshacer última acción ------------------------------------------------------

    private void deshacerUltimaTirada() {

        if (partidaFinalizada
                || animacionCambioTurnoActiva) {

            return;
        }

        if (historialEstados.isEmpty()) {

            Toast.makeText(
                    this,
                    "No hay ninguna tirada para deshacer",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        EstadoPartida estadoAnterior =
                historialEstados.pop();

        puntuacionesJugadores =
                estadoAnterior.puntuacionesJugadores.clone();

        jugadoresFinalizados =
                estadoAnterior.jugadoresFinalizados.clone();

        ordenFinalizacion.clear();
        ordenFinalizacion.addAll(
                estadoAnterior.ordenFinalizacion
        );

        jugadorActual =
                estadoAnterior.jugadorActual;

        rondaActual =
                estadoAnterior.rondaActual;

        dardoActual =
                estadoAnterior.dardoActual;

        puntosTurnoActual =
                estadoAnterior.puntosTurnoActual;

        System.arraycopy(
                estadoAnterior.puntosDardos,
                0,
                puntosDardos,
                0,
                puntosDardos.length
        );

        System.arraycopy(
                estadoAnterior.textosDardos,
                0,
                textosDardos,
                0,
                textosDardos.length
        );

        while (historialTiradas.size()
                > estadoAnterior.tamanoHistorialTiradas) {

            historialTiradas.remove(
                    historialTiradas.size() - 1
            );
        }

        grupoMultiplicadores.check(R.id.radioX1);

        actualizarInterfazCompleta();
    }

    //Finalización manual del turno ----------------------------------------------

    private void finalizarTurnoManual() {

        if (partidaFinalizada
                || animacionCambioTurnoActiva) {

            return;
        }

        guardarEstadoActual();
        finalizarTurno();
    }

    //Finalización normal del turno ----------------------------------------------

    private void finalizarTurno() {

        if (partidaFinalizada || animacionCambioTurnoActiva) {
            return;
        }

        int puntuacionAntes =
                puntuacionesJugadores[jugadorActual];

        //Permitir saltar el turno sin lanzar dardos
        if (dardoActual == 0) {

            guardarTiradaTemporal(
                    puntuacionAntes,
                    puntuacionAntes,
                    false
            );

            cambiarTurnoConAnimacion();
            return;
        }

        int puntuacionDespues =
                puntuacionAntes - puntosTurnoActual;

        if (puntuacionDespues < 0) {

            finalizarTurnoPasado(
                    puntuacionAntes
            );

            return;
        }

        if (puntuacionDespues == 0) {

            finalizarJugadorEnCero(
                    puntuacionAntes
            );

            return;
        }

        puntuacionesJugadores[jugadorActual] =
                puntuacionDespues;

        guardarTiradaTemporal(
                puntuacionAntes,
                puntuacionDespues,
                false
        );

        cambiarTurnoConAnimacion();
    }

    //Finalización del turno por exceso ------------------------------------------

    private void finalizarTurnoPasado(
            int puntuacionAntes
    ) {

        guardarTiradaTemporal(
                puntuacionAntes,
                puntuacionAntes,
                true
        );

        Toast.makeText(
                this,
                nombresJugadores.get(jugadorActual)
                        + " se ha pasado. El turno termina",
                Toast.LENGTH_SHORT
        ).show();

        cambiarTurnoConAnimacion();
    }

    //Finalización de un jugador --------------------------------------------------

    private void finalizarJugadorEnCero(
            int puntuacionAntes
    ) {

        puntuacionesJugadores[jugadorActual] = 0;

        if (!ordenFinalizacion.contains(jugadorActual)) {
            ordenFinalizacion.add(jugadorActual);
        }

        guardarTiradaTemporal(
                puntuacionAntes,
                0,
                false
        );

        actualizarMarcadores();
        actualizarJugadorActivo();

        String nombreJugador =
                nombresJugadores.get(jugadorActual);

        if (numeroJugadores <= 2) {

            Toast.makeText(
                    this,
                    "¡" + nombreJugador + " ha ganado la partida!",
                    Toast.LENGTH_LONG
            ).show();

            finalizarPartida(
                    MOTIVO_GANADOR
            );
            return;
        }

        jugadoresFinalizados[jugadorActual] = true;

        Toast.makeText(
                this,
                nombreJugador + " ha finalizado",
                Toast.LENGTH_SHORT
        ).show();

        if (todosLosJugadoresFinalizados()) {

            Toast.makeText(
                    this,
                    "Todos los jugadores han finalizado",
                    Toast.LENGTH_LONG
            ).show();

            finalizarPartida(
                    MOTIVO_TODOS_FINALIZADOS
            );

            return;
        }

        cambiarTurnoConAnimacion();
    }

    //Cambio de jugador -----------------------------------------------------------

    private void avanzarJugador() {

        if (todosLosJugadoresFinalizados()) {
            return;
        }

        int jugadoresComprobados = 0;

        do {

            jugadorActual++;

            if (jugadorActual >= numeroJugadores) {

                jugadorActual = 0;
                rondaActual++;

                if (rondaActual > maxRondas) {

                    finalizarPartidaPorRondas();
                    return;
                }
            }

            jugadoresComprobados++;

        } while (
                jugadoresFinalizados[jugadorActual]
                        && jugadoresComprobados < numeroJugadores
        );
    }

    //Comprobación de jugadores finalizados --------------------------------------

    private boolean todosLosJugadoresFinalizados() {

        for (boolean finalizado : jugadoresFinalizados) {

            if (!finalizado) {
                return false;
            }
        }

        return true;
    }

    //Preparación del siguiente turno --------------------------------------------

    private void prepararNuevoTurno() {

        dardoActual = 0;
        puntosTurnoActual = 0;

        Arrays.fill(puntosDardos, 0);
        Arrays.fill(textosDardos, null);

        grupoMultiplicadores.check(R.id.radioX1);
    }

    //Finalización por máximo de rondas --------------------

    private void finalizarPartidaPorRondas() {

        rondaActual = maxRondas;

        Toast.makeText(
                this,
                "Se ha alcanzado el máximo de rondas",
                Toast.LENGTH_LONG
        ).show();

        finalizarPartida(
                MOTIVO_MAX_RONDAS
        );
    }

    //Finalización completa -------------------------------------------------------

    private void finalizarPartida(
            String motivoFinalizacion
    ) {

        if (partidaFinalizada) {
            return;
        }

        partidaFinalizada = true;

        bloquearBotonesPartida();

        abrirResultadoActivity(
                motivoFinalizacion
        );
    }

    //Guardado temporal -----------------------------------------------------------

    private void guardarTiradaTemporal(
            int puntuacionAntes,
            int puntuacionDespues,
            boolean turnoPasado
    ) {

        RegistroTiradaTemporal registro =
                new RegistroTiradaTemporal(
                        jugadorActual,
                        nombresJugadores.get(jugadorActual),
                        rondaActual,
                        Arrays.copyOf(
                                puntosDardos,
                                puntosDardos.length
                        ),
                        Arrays.copyOf(
                                textosDardos,
                                textosDardos.length
                        ),
                        puntosTurnoActual,
                        puntuacionAntes,
                        puntuacionDespues,
                        turnoPasado
                );

        historialTiradas.add(registro);
    }

    //Cambio de turno con animación -------------------------------------------------

    private void cambiarTurnoConAnimacion() {

        if (partidaFinalizada || animacionCambioTurnoActiva) {
            return;
        }

        animacionCambioTurnoActiva = true;

        bloquearBotonesPartida();

        //Fade out del nombre y puntuación del jugador actual
        txtNombreJugadorActual.animate()
                .alpha(0f)
                .setDuration(250)
                .start();

        txtPuntuacionJugadorActual.animate()
                .alpha(0f)
                .setDuration(250)
                .withEndAction(() -> {

                    //Cambiar al siguiente jugador
                    avanzarJugador();

                    if (partidaFinalizada) {
                        animacionCambioTurnoActiva = false;
                        return;
                    }

                    prepararNuevoTurno();
                    actualizarInterfazCompleta();

                    //Estado inicial para el fade in
                    txtNombreJugadorActual.setAlpha(0f);
                    txtPuntuacionJugadorActual.setAlpha(0f);

                    //Fade in del nuevo jugador
                    txtNombreJugadorActual.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start();

                    txtPuntuacionJugadorActual.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .withEndAction(() -> {

                                animacionCambioTurnoActiva = false;
                                actualizarEstadoBotones();
                            })
                            .start();
                })
                .start();
    }

    //Actualización completa ------------------------------------------------------

    private void actualizarInterfazCompleta() {

        actualizarModoJuego();
        actualizarPanelesJugadores();
        actualizarMarcadores();
        actualizarJugadorActivo();
        actualizarRonda();
        actualizarInformacionTurno();
        actualizarEstadoBotones();
    }

    //Actualización del modo ------------------------------------------------------

    private void actualizarModoJuego() {

        txtModoJuego.setText(modoJuego);
    }

    //Actualización provisional del marcador central --------------------

    private void actualizarMarcadorCentralTurno() {

        int puntuacionProvisional =
                puntuacionesJugadores[jugadorActual] - puntosTurnoActual;

        txtPuntuacionJugadorActual.setText(
                String.valueOf(Math.max(puntuacionProvisional, 0))
        );
    }

    //Actualización de paneles ----------------------------------------------------

    private void actualizarPanelesJugadores() {

        for (int i = 0; i < MAX_JUGADORES; i++) {

            if (i < numeroJugadores) {

                panelesJugadores[i].setVisibility(View.VISIBLE);

                txtNombresJugadores[i].setText(
                        nombresJugadores.get(i)
                );

                int colorJugador =
                        ContextCompat.getColor(
                                this,
                                coloresJugadores[i]
                        );

                txtNombresJugadores[i].setTextColor(
                        colorJugador
                );

            } else {

                panelesJugadores[i].setVisibility(View.GONE);
            }
        }
    }

    //Actualización de marcadores -------------------------------------------------

    private void actualizarMarcadores() {

        for (int i = 0; i < numeroJugadores; i++) {

            txtPuntuacionesJugadores[i].setText(
                    String.valueOf(
                            puntuacionesJugadores[i]
                    )
            );

            if (jugadoresFinalizados[i]) {

                panelesJugadores[i].setAlpha(0.35f);

            } else {

                panelesJugadores[i].setAlpha(0.65f);
            }
        }
    }

    //Actualización del jugador actual -------------------------------------------

    private void actualizarJugadorActivo() {

        if (numeroJugadores == 0) {
            return;
        }

        String nombreActual =
                nombresJugadores.get(jugadorActual);

        int puntuacionActual =
                puntuacionesJugadores[jugadorActual];

        int colorJugadorActual =
                ContextCompat.getColor(
                        this,
                        coloresJugadores[jugadorActual]
                );

        txtJugadorActual.setText(nombreActual);
        txtJugadorActual.setTextColor(colorJugadorActual);

        txtNombreJugadorActual.setText(nombreActual);
        txtNombreJugadorActual.setTextColor(colorJugadorActual);

        txtPuntuacionJugadorActual.setText(
                String.valueOf(puntuacionActual)
        );

        for (int i = 0; i < numeroJugadores; i++) {

            if (jugadoresFinalizados[i]) {

                panelesJugadores[i].setAlpha(0.35f);

            } else if (i == jugadorActual) {

                panelesJugadores[i].setAlpha(1.0f);

            } else {

                panelesJugadores[i].setAlpha(0.65f);
            }
        }
    }

    //Actualización de ronda ------------------------------------------------------

    private void actualizarRonda() {

        txtRondaActual.setText(
                String.valueOf(rondaActual)
        );

        txtMaxRondas.setText(
                "/" + maxRondas
        );
    }

    //Actualización de la tirada --------------------------------------------------

    private void actualizarInformacionTurno() {

        txtDardo1.setText(
                obtenerTextoDardo(0)
        );

        txtDardo2.setText(
                obtenerTextoDardo(1)
        );

        txtDardo3.setText(
                obtenerTextoDardo(2)
        );

        int numeroDardoMostrado =
                Math.min(
                        dardoActual + 1,
                        MAX_DARDOS_TURNO
                );

        txtNumeroDardos.setText(
                numeroDardoMostrado
                        + " / "
                        + MAX_DARDOS_TURNO
        );

        imgDardo1.setAlpha(
                dardoActual == 0 ? 1.0f : 0.35f
        );

        imgDardo2.setAlpha(
                dardoActual == 1 ? 1.0f : 0.35f
        );

        imgDardo3.setAlpha(
                dardoActual == 2 ? 1.0f : 0.35f
        );

        if (dardoActual >= MAX_DARDOS_TURNO) {

            imgDardo1.setAlpha(1.0f);
            imgDardo2.setAlpha(1.0f);
            imgDardo3.setAlpha(1.0f);
        }

        actualizarMarcadorCentralTurno();
    }

    //Obtención del texto del dardo ----------------------------------------------

    private String obtenerTextoDardo(int posicion) {

        if (posicion < 0 || posicion >= MAX_DARDOS_TURNO) {
            return "";
        }

        if (posicion >= dardoActual) {
            return "";
        }

        String texto =
                textosDardos[posicion];

        if (texto == null) {
            return String.valueOf(
                    puntosDardos[posicion]
            );
        }

        return texto;
    }

    //Estado de los botones -------------------------------------------------------

    private void actualizarEstadoBotones() {

        if (partidaFinalizada
                || animacionCambioTurnoActiva) {

            bloquearBotonesPartida();
            return;
        }

        boolean sePuedeLanzar =
                dardoActual < MAX_DARDOS_TURNO;

        for (Button boton : botonesPuntuacion) {
            boton.setEnabled(sePuedeLanzar);
        }

        btnFuera.setEnabled(sePuedeLanzar);
        btnBull.setEnabled(sePuedeLanzar);

        radioX1.setEnabled(sePuedeLanzar);
        radioX2.setEnabled(sePuedeLanzar);
        radioX3.setEnabled(sePuedeLanzar);

        btnDeshacerTirada.setEnabled(
                !historialEstados.isEmpty()
        );

        btnDeshacerTirada.setAlpha(
                historialEstados.isEmpty()
                        ? 0.45f
                        : 1.0f
        );

        btnSiguienteTurno.setEnabled(true);
    }

    //Bloqueo de la partida -------------------------------------------------------

    private void bloquearBotonesPartida() {

        for (Button boton : botonesPuntuacion) {
            boton.setEnabled(false);
        }

        btnFuera.setEnabled(false);
        btnBull.setEnabled(false);

        radioX1.setEnabled(false);
        radioX2.setEnabled(false);
        radioX3.setEnabled(false);

        btnDeshacerTirada.setEnabled(false);
        btnDeshacerTirada.setAlpha(0.45f);

        btnSiguienteTurno.setEnabled(false);
    }

    //Registro temporal de una tirada --------------------------------------------

    private static class RegistroTiradaTemporal {

        private final int indiceJugador;
        private final String nombreJugador;
        private final int ronda;

        private final int[] dardos;
        private final String[] textosDardos;

        private final int puntosTotales;
        private final int puntuacionAntes;
        private final int puntuacionDespues;

        private final boolean turnoPasado;

        public RegistroTiradaTemporal(
                int indiceJugador,
                String nombreJugador,
                int ronda,
                int[] dardos,
                String[] textosDardos,
                int puntosTotales,
                int puntuacionAntes,
                int puntuacionDespues,
                boolean turnoPasado
        ) {
            this.indiceJugador = indiceJugador;
            this.nombreJugador = nombreJugador;
            this.ronda = ronda;
            this.dardos = dardos;
            this.textosDardos = textosDardos;
            this.puntosTotales = puntosTotales;
            this.puntuacionAntes = puntuacionAntes;
            this.puntuacionDespues = puntuacionDespues;
            this.turnoPasado = turnoPasado;
        }

        public int getIndiceJugador() {
            return indiceJugador;
        }

        public String getNombreJugador() {
            return nombreJugador;
        }

        public int getRonda() {
            return ronda;
        }

        public int[] getDardos() {
            return dardos;
        }

        public String[] getTextosDardos() {
            return textosDardos;
        }

        public int getPuntosTotales() {
            return puntosTotales;
        }

        public int getPuntuacionAntes() {
            return puntuacionAntes;
        }

        public int getPuntuacionDespues() {
            return puntuacionDespues;
        }

        public boolean isTurnoPasado() {
            return turnoPasado;
        }
    }

    //Estado completo para deshacer ------------------------------------------------

    private static class EstadoPartida {

        private final int[] puntuacionesJugadores;
        private final boolean[] jugadoresFinalizados;

        private final ArrayList<Integer> ordenFinalizacion;

        private final int jugadorActual;
        private final int rondaActual;
        private final int dardoActual;
        private final int puntosTurnoActual;

        private final int[] puntosDardos;
        private final String[] textosDardos;

        private final int tamanoHistorialTiradas;

        private EstadoPartida(
                int[] puntuacionesJugadores,
                boolean[] jugadoresFinalizados,
                ArrayList<Integer> ordenFinalizacion,
                int jugadorActual,
                int rondaActual,
                int dardoActual,
                int puntosTurnoActual,
                int[] puntosDardos,
                String[] textosDardos,
                int tamanoHistorialTiradas
        ) {

            this.puntuacionesJugadores =
                    puntuacionesJugadores;

            this.jugadoresFinalizados =
                    jugadoresFinalizados;

            this.ordenFinalizacion =
                    ordenFinalizacion;

            this.jugadorActual =
                    jugadorActual;

            this.rondaActual =
                    rondaActual;

            this.dardoActual =
                    dardoActual;

            this.puntosTurnoActual =
                    puntosTurnoActual;

            this.puntosDardos =
                    puntosDardos;

            this.textosDardos =
                    textosDardos;

            this.tamanoHistorialTiradas =
                    tamanoHistorialTiradas;
        }
    }

    //Marcador emergente ----------------------------------------------------------

    private void mostrarDialogoMarcador() {

        StringBuilder mensaje =
                new StringBuilder();

        ArrayList<Integer> clasificacion =
                construirClasificacionFinal();

        for (int posicion = 0;
             posicion < clasificacion.size();
             posicion++) {

            int indiceJugador =
                    clasificacion.get(posicion);

            mensaje.append(posicion + 1)
                    .append(". ")
                    .append(nombresJugadores.get(indiceJugador))
                    .append(": ")
                    .append(puntuacionesJugadores[indiceJugador])
                    .append(" puntos");

            if (jugadoresFinalizados[indiceJugador]) {
                mensaje.append(" - FINALIZADO");
            }

            if (indiceJugador == jugadorActual
                    && !partidaFinalizada
                    && !jugadoresFinalizados[indiceJugador]) {

                mensaje.append(" - TURNO ACTUAL");
            }

            if (posicion < clasificacion.size() - 1) {
                mensaje.append("\n");
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Marcador")
                .setMessage(mensaje.toString())
                .setPositiveButton("CERRAR", null)
                .show();
    }

    //Construcción de la clasificación final --------------------

    private ArrayList<Integer> construirClasificacionFinal() {

        ArrayList<Integer> clasificacion =
                new ArrayList<>();

        //Añadir primero quienes llegaron a cero --------------------

        for (Integer indice : ordenFinalizacion) {

            if (!clasificacion.contains(indice)) {
                clasificacion.add(indice);
            }
        }

        //Recoger quienes no finalizaron --------------------

        ArrayList<Integer> jugadoresRestantes =
                new ArrayList<>();

        for (int i = 0; i < numeroJugadores; i++) {

            if (!clasificacion.contains(i)) {
                jugadoresRestantes.add(i);
            }
        }

        //Ordenar por menor puntuación restante --------------------

        jugadoresRestantes.sort(
                (indice1, indice2) -> Integer.compare(
                        puntuacionesJugadores[indice1],
                        puntuacionesJugadores[indice2]
                )
        );

        clasificacion.addAll(
                jugadoresRestantes
        );

        return clasificacion;
    }

    //Apertura de la pantalla de resultados --------------------

    //Abrir pantalla de resultados ---------------------------------------------------
    private void abrirResultadoActivity(
            String motivoFinalizacion
    ) {

        //Construir la clasificación final
        ArrayList<Integer> clasificacion =
                construirClasificacionFinal();

        //Listas ordenadas según la posición final
        ArrayList<String> nombresOrdenados =
                new ArrayList<>();

        ArrayList<Integer> puntuacionesOrdenadas =
                new ArrayList<>();

        ArrayList<Integer> coloresOrdenados =
                new ArrayList<>();

        ArrayList<Integer> posiciones =
                new ArrayList<>();

        ArrayList<Integer> indicesOriginales =
                new ArrayList<>();

        //Recorrer los jugadores en el orden de clasificación
        for (int posicion = 0;
             posicion < clasificacion.size();
             posicion++) {

            int indiceJugador =
                    clasificacion.get(posicion);

            nombresOrdenados.add(
                    nombresJugadores.get(indiceJugador)
            );

            puntuacionesOrdenadas.add(
                    puntuacionesJugadores[indiceJugador]
            );

            coloresOrdenados.add(
                    coloresJugadores[indiceJugador]
            );

            posiciones.add(
                    posicion + 1
            );

            indicesOriginales.add(
                    indiceJugador
            );
        }

        //El primer jugador de la clasificación es el ganador
        String nombreGanador =
                nombresOrdenados.isEmpty()
                        ? ""
                        : nombresOrdenados.get(0);

        Intent intent = new Intent(
                PartidaPuntosActivity.this,
                ResultadoActivity.class
        );

        //Datos generales de la partida
        intent.putExtra(
                ResultadoActivity.EXTRA_MODO_JUEGO,
                modoJuego
        );

        intent.putExtra(
                ResultadoActivity.EXTRA_MOTIVO_FINALIZACION,
                motivoFinalizacion
        );

        intent.putExtra(
                ResultadoActivity.EXTRA_NUMERO_JUGADORES,
                numeroJugadores
        );

        intent.putExtra(
                ResultadoActivity.EXTRA_RONDAS_JUGADAS,
                rondaActual
        );

        intent.putExtra(
                ResultadoActivity.EXTRA_MAX_RONDAS,
                maxRondas
        );

        intent.putExtra(
                ResultadoActivity.EXTRA_NOMBRE_GANADOR,
                nombreGanador
        );

        //Datos ordenados de los jugadores
        intent.putStringArrayListExtra(
                ResultadoActivity.EXTRA_NOMBRES_JUGADORES,
                nombresOrdenados
        );

        intent.putIntegerArrayListExtra(
                ResultadoActivity.EXTRA_PUNTUACIONES,
                puntuacionesOrdenadas
        );

        intent.putIntegerArrayListExtra(
                ResultadoActivity.EXTRA_COLORES,
                coloresOrdenados
        );

        intent.putIntegerArrayListExtra(
                ResultadoActivity.EXTRA_POSICIONES,
                posiciones
        );

        intent.putIntegerArrayListExtra(
                ResultadoActivity.EXTRA_INDICES_ORIGINALES,
                indicesOriginales
        );

        startActivity(intent);
        finish();
    }

    //Control del botón atrás ---------------------------------------------------------

    private void configurarBotonAtras() {

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        mostrarDialogoSalirPartida();
                    }
                }
        );
    }
}