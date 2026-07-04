package activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.productos.juegosdedardos.R;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

/**
 * Partida de Criquet y Cut Throat Criquet.
 *
 * Características:
 * - De 2 a 6 jugadores.
 * - Objetivos siempre situados en el centro del marcador.
 * - Con dos jugadores se muestra uno a cada lado.
 * - Cambio automático después del tercer dardo.
 * - El botón SIGUIENTE TURNO permite terminar el turno antes.
 * - X1, X2 y X3 muestran visualmente cuál está seleccionado.
 * - Finalización inmediata cuando un jugador cierra todos los objetivos
 *   y cumple la condición de puntuación de su modo.
 */
public class PartidaCriquetActivity extends AppCompatActivity {

    // Extras recibidos ---------------------------------------------------------

    public static final String EXTRA_MODO_JUEGO = "modoJuego";
    public static final String EXTRA_MAX_RONDAS = "maxRondas";
    public static final String EXTRA_NUMERO_JUGADORES_CONFIG = "numeroJugadores";
    public static final String EXTRA_JUGADOR = "jugador";
    public static final String EXTRA_NOMBRES_JUGADORES = "nombresJugadores";
    public static final String EXTRA_COLOR_JUGADOR = "colorJugador";

    // Extras enviados a ResultadoActivity --------------------------------------

    public static final String EXTRA_MOTIVO_FINALIZACION =
            "resultado_motivo_finalizacion";

    public static final String EXTRA_NUMERO_JUGADORES_RESULTADO =
            "resultado_numero_jugadores";

    public static final String EXTRA_RONDAS_JUGADAS =
            "resultado_rondas_jugadas";

    public static final String EXTRA_NOMBRE_GANADOR =
            "resultado_nombre_ganador";

    public static final String EXTRA_PUNTUACIONES =
            "resultado_puntuaciones";

    public static final String EXTRA_MARCAS_CRIQUET =
            "resultado_marcas_criquet";

    // Constantes ---------------------------------------------------------------

    private static final int MIN_JUGADORES = 2;
    private static final int MAX_JUGADORES = 6;

    private static final int DARDOS_POR_TURNO = 3;
    private static final int MARCAS_PARA_CERRAR = 3;

    /*
     * Índices:
     * 0 = 15
     * 1 = 16
     * 2 = 17
     * 3 = 18
     * 4 = 19
     * 5 = 20
     * 6 = Diana
     */
    private static final int[] OBJETIVOS = {
            15, 16, 17, 18, 19, 20, 25
    };

    private static final String[] ETIQUETAS_OBJETIVOS = {
            "15", "16", "17", "18", "19", "20", "D"
    };

    private enum ModoCriquet {
        CRIQUET,
        CUT_THROAT
    }

    // Datos de la partida ------------------------------------------------------

    private ModoCriquet modoCriquet;

    private String modoJuego;
    private int maxRondas;

    private String[] nombresJugadores;
    private int[] coloresJugadores;

    private int[][] marcasJugadores;
    private int[] puntuacionesJugadores;

    private int jugadorActual;
    private int rondaActual;
    private int numeroDardo;

    private int multiplicadorSeleccionado;

    private boolean partidaFinalizada;

    private final String[] tiradasTurno = {"", "", ""};

    /*
     * Se guarda el estado anterior a cada dardo.
     * Si el tercer dardo provoca un cambio automático, deshacer devuelve
     * correctamente al turno y jugador anteriores.
     */
    private final Deque<EstadoPartida> historialEstados =
            new ArrayDeque<>();

    // Vistas generales ---------------------------------------------------------

    private TextView txtModoJuego;
    private TextView txtJugadorActual;

    private TextView txtRondaActual;
    private TextView txtMaxRondas;
    private TextView txtNumeroDardo;

    private TextView txtTirada1;
    private TextView txtTirada2;
    private TextView txtTirada3;

    private ImageView imgDardo1;
    private ImageView imgDardo2;
    private ImageView imgDardo3;

    // Marcador dinámico --------------------------------------------------------

    private LinearLayout contenedorJugadoresIzquierda;
    private LinearLayout contenedorJugadoresDerecha;

    private TextView[] txtNombresJugadores;
    private TextView[] txtPuntosJugadores;
    private TextView[][] txtMarcasJugadores;

    // Botonera -----------------------------------------------------------------

    private Button btnFuera;

    private Button btnX1;
    private Button btnX2;
    private Button btnX3;

    private Button btnNumero15;
    private Button btnNumero16;
    private Button btnNumero17;
    private Button btnNumero18;
    private Button btnNumero19;
    private Button btnNumero20;
    private Button btnBull;

    private Button btnDeshacerTirada;
    private Button btnVerMarcador;
    private Button btnSiguienteTurno;
    private Button btnSalirPartida;

    private final List<Button> botonesPuntuacion =
            new ArrayList<>();

    private final List<Button> botonesMultiplicadores =
            new ArrayList<>();

    private final ColorStateList[] tintesOriginalesMultiplicadores =
            new ColorStateList[3];

    private final ColorStateList[] coloresTextoOriginalesMultiplicadores =
            new ColorStateList[3];

    // Ciclo de vida ------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getColor(R.color.blue));
        getWindow().setNavigationBarColor(getColor(R.color.blue));

        setContentView(R.layout.activity_partida_criquet);

        inicializarVistas();
        recibirDatosPartida();
        inicializarPartida();
        crearMarcadorJugadores();
        configurarListeners();
        configurarBotonAtras();

        actualizarInterfazCompleta();
    }

    // Inicialización de vistas -------------------------------------------------

    private void inicializarVistas() {

        txtModoJuego = findViewById(R.id.txtModoJuego);
        txtJugadorActual = findViewById(R.id.txtJugadorActual);

        txtRondaActual = findViewById(R.id.txtRondaActual);
        txtMaxRondas = findViewById(R.id.txtMaxRondas);
        txtNumeroDardo = findViewById(R.id.txtNumeroDardo);

        txtTirada1 = findViewById(R.id.txtTirada1);
        txtTirada2 = findViewById(R.id.txtTirada2);
        txtTirada3 = findViewById(R.id.txtTirada3);

        imgDardo1 = findViewById(R.id.imgDardo1);
        imgDardo2 = findViewById(R.id.imgDardo2);
        imgDardo3 = findViewById(R.id.imgDardo3);

        contenedorJugadoresIzquierda =
                findViewById(R.id.contenedorJugadoresIzquierda);

        contenedorJugadoresDerecha =
                findViewById(R.id.contenedorJugadoresDerecha);

        btnFuera = findViewById(R.id.btnFuera);

        btnX1 = findViewById(R.id.btnX1);
        btnX2 = findViewById(R.id.btnX2);
        btnX3 = findViewById(R.id.btnX3);

        btnNumero15 = findViewById(R.id.btnNumero15);
        btnNumero16 = findViewById(R.id.btnNumero16);
        btnNumero17 = findViewById(R.id.btnNumero17);
        btnNumero18 = findViewById(R.id.btnNumero18);
        btnNumero19 = findViewById(R.id.btnNumero19);
        btnNumero20 = findViewById(R.id.btnNumero20);
        btnBull = findViewById(R.id.btnBull);

        btnDeshacerTirada =
                findViewById(R.id.btnDeshacerTirada);

        btnVerMarcador =
                findViewById(R.id.btnVerMarcador);

        btnSiguienteTurno =
                findViewById(R.id.btnSiguienteTurno);

        btnSalirPartida =
                findViewById(R.id.btnSalirPartida);

        botonesPuntuacion.clear();
        botonesPuntuacion.add(btnNumero15);
        botonesPuntuacion.add(btnNumero16);
        botonesPuntuacion.add(btnNumero17);
        botonesPuntuacion.add(btnNumero18);
        botonesPuntuacion.add(btnNumero19);
        botonesPuntuacion.add(btnNumero20);
        botonesPuntuacion.add(btnBull);

        botonesMultiplicadores.clear();
        botonesMultiplicadores.add(btnX1);
        botonesMultiplicadores.add(btnX2);
        botonesMultiplicadores.add(btnX3);

        for (int i = 0; i < botonesMultiplicadores.size(); i++) {

            Button boton = botonesMultiplicadores.get(i);

            tintesOriginalesMultiplicadores[i] =
                    boton.getBackgroundTintList();

            coloresTextoOriginalesMultiplicadores[i] =
                    boton.getTextColors();
        }
    }

    // Recepción de datos -------------------------------------------------------

    private void recibirDatosPartida() {

        Intent intent = getIntent();

        modoJuego =
                intent.getStringExtra(EXTRA_MODO_JUEGO);

        maxRondas =
                intent.getIntExtra(EXTRA_MAX_RONDAS, 15);

        if (modoJuego == null || modoJuego.trim().isEmpty()) {
            modoJuego = "Cricket";
        }

        modoCriquet =
                interpretarModoJuego(modoJuego);

        recibirNombresJugadores(intent);
        recibirColoresJugadores(intent);

        normalizarNombresJugadores();
    }

    private void recibirNombresJugadores(Intent intent) {

        /*
         * ConfigurarNuevaPartidaActivity envía los nombres
         * mediante putStringArrayListExtra().
         */
        ArrayList<String> nombresRecibidos =
                intent.getStringArrayListExtra(
                        EXTRA_NOMBRES_JUGADORES
                );

        if (nombresRecibidos != null
                && !nombresRecibidos.isEmpty()) {

            nombresJugadores =
                    nombresRecibidos.toArray(
                            new String[0]
                    );

        } else {

            /*
             * Respaldo usando los extras individuales:
             * jugador1, jugador2, jugador3...
             */
            int numeroJugadores =
                    intent.getIntExtra(
                            EXTRA_NUMERO_JUGADORES_CONFIG,
                            MIN_JUGADORES
                    );

            numeroJugadores =
                    Math.max(
                            MIN_JUGADORES,
                            Math.min(
                                    numeroJugadores,
                                    MAX_JUGADORES
                            )
                    );

            nombresJugadores =
                    new String[numeroJugadores];

            for (int i = 0;
                 i < numeroJugadores;
                 i++) {

                String nombre =
                        intent.getStringExtra(
                                EXTRA_JUGADOR + (i + 1)
                        );

                if (nombre == null
                        || nombre.trim().isEmpty()) {

                    nombre = "Jugador " + (i + 1);
                }

                nombresJugadores[i] = nombre;
            }
        }

        if (nombresJugadores.length < MIN_JUGADORES) {

            String primerNombre =
                    nombresJugadores.length > 0
                            ? nombresJugadores[0]
                            : "Jugador 1";

            nombresJugadores =
                    new String[]{
                            primerNombre,
                            "Jugador 2"
                    };
        }

        if (nombresJugadores.length > MAX_JUGADORES) {

            nombresJugadores =
                    Arrays.copyOf(
                            nombresJugadores,
                            MAX_JUGADORES
                    );
        }
    }

    private void recibirColoresJugadores(Intent intent) {

        int[] coloresPorDefecto = {
                R.color.jugador_rojo,
                R.color.jugador_verde,
                R.color.jugador_azul,
                R.color.jugador_amarillo,
                R.color.jugador_naranja,
                R.color.jugador_morado
        };

        coloresJugadores =
                new int[nombresJugadores.length];

        for (int i = 0;
             i < nombresJugadores.length;
             i++) {

            /*
             * ConfigurarNuevaPartidaActivity guarda en cada extra
             * un ID de recurso, por ejemplo R.color.jugador_rojo.
             */
            int recursoColor =
                    intent.getIntExtra(
                            EXTRA_COLOR_JUGADOR + (i + 1),
                            coloresPorDefecto[
                                    i % coloresPorDefecto.length
                                    ]
                    );

            coloresJugadores[i] =
                    ContextCompat.getColor(
                            this,
                            recursoColor
                    );
        }
    }

    private ModoCriquet interpretarModoJuego(String modoRecibido) {

        String modoNormalizado = modoRecibido
                .trim()
                .toUpperCase(Locale.ROOT)
                .replace("_", " ")
                .replace("-", " ");

        if (modoNormalizado.contains("CUT")) {
            return ModoCriquet.CUT_THROAT;
        }

        return ModoCriquet.CRIQUET;
    }

    private void normalizarNombresJugadores() {

        for (int i = 0; i < nombresJugadores.length; i++) {

            if (nombresJugadores[i] == null
                    || nombresJugadores[i].trim().isEmpty()) {

                nombresJugadores[i] = "Jugador " + (i + 1);

            } else {

                nombresJugadores[i] =
                        nombresJugadores[i].trim();
            }
        }
    }

    // Inicialización de partida ------------------------------------------------

    private void inicializarPartida() {

        marcasJugadores =
                new int[nombresJugadores.length][OBJETIVOS.length];

        puntuacionesJugadores =
                new int[nombresJugadores.length];

        jugadorActual = 0;
        rondaActual = 1;
        numeroDardo = 1;

        multiplicadorSeleccionado = 1;
        partidaFinalizada = false;

        Arrays.fill(tiradasTurno, "");
        historialEstados.clear();

        txtNombresJugadores =
                new TextView[nombresJugadores.length];

        txtPuntosJugadores =
                new TextView[nombresJugadores.length];

        txtMarcasJugadores =
                new TextView[
                        nombresJugadores.length
                        ][OBJETIVOS.length];
    }

    // Creación del marcador ----------------------------------------------------

    private void crearMarcadorJugadores() {

        contenedorJugadoresIzquierda.removeAllViews();
        contenedorJugadoresDerecha.removeAllViews();

        /*
         * Distribución:
         * 2 jugadores -> 1 izquierda y 1 derecha.
         * 3 jugadores -> 2 izquierda y 1 derecha.
         * 4 jugadores -> 2 y 2.
         * 5 jugadores -> 3 y 2.
         * 6 jugadores -> 3 y 3.
         */
        int jugadoresIzquierda =
                (nombresJugadores.length + 1) / 2;

        for (int jugador = 0;
             jugador < nombresJugadores.length;
             jugador++) {

            LinearLayout columna =
                    crearColumnaJugador(jugador);

            if (jugador < jugadoresIzquierda) {

                contenedorJugadoresIzquierda.addView(columna);

            } else {

                contenedorJugadoresDerecha.addView(columna);
            }
        }
    }

    private LinearLayout crearColumnaJugador(int indiceJugador) {

        LinearLayout columna = new LinearLayout(this);

        columna.setOrientation(LinearLayout.VERTICAL);

        columna.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                )
        );

        TextView txtNombre = new TextView(this);

        txtNombre.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        convertirDp(22)
                )
        );

        txtNombre.setGravity(Gravity.CENTER);
        txtNombre.setIncludeFontPadding(false);
        txtNombre.setMaxLines(1);
        txtNombre.setTextSize(
                nombresJugadores.length >= 5 ? 8 : 10
        );

        txtNombre.setTypeface(
                txtNombre.getTypeface(),
                android.graphics.Typeface.BOLD
        );

        columna.addView(txtNombre);
        txtNombresJugadores[indiceJugador] = txtNombre;

        /*
         * El XML muestra de arriba abajo:
         * 20, 19, 18, 17, 16, 15 y Diana.
         */
        int[] ordenVisual = {5, 4, 3, 2, 1, 0, 6};

        for (int indiceObjetivo : ordenVisual) {

            TextView txtMarca = new TextView(this);

            txtMarca.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            convertirDp(27)
                    )
            );

            txtMarca.setGravity(Gravity.CENTER);
            txtMarca.setIncludeFontPadding(false);
            txtMarca.setTextSize(
                    nombresJugadores.length >= 5 ? 8 : 10
            );

            txtMarca.setText("○ ○ ○");
            txtMarca.setTextColor(Color.WHITE);

            columna.addView(txtMarca);

            txtMarcasJugadores[indiceJugador][indiceObjetivo] =
                    txtMarca;
        }

        TextView txtPuntos = new TextView(this);

        txtPuntos.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        convertirDp(27)
                )
        );

        txtPuntos.setGravity(Gravity.CENTER);
        txtPuntos.setIncludeFontPadding(false);
        txtPuntos.setTextSize(
                nombresJugadores.length >= 5 ? 9 : 10
        );

        txtPuntos.setTypeface(
                txtPuntos.getTypeface(),
                android.graphics.Typeface.BOLD
        );

        txtPuntos.setText("0 PTS");
        txtPuntos.setTextColor(Color.WHITE);

        columna.addView(txtPuntos);
        txtPuntosJugadores[indiceJugador] = txtPuntos;

        return columna;
    }

    // Configuración de listeners -----------------------------------------------

    private void configurarListeners() {

        btnFuera.setOnClickListener(
                view -> registrarFuera()
        );

        btnX1.setOnClickListener(
                view -> seleccionarMultiplicador(1)
        );

        btnX2.setOnClickListener(
                view -> seleccionarMultiplicador(2)
        );

        btnX3.setOnClickListener(
                view -> seleccionarMultiplicador(3)
        );

        btnNumero15.setOnClickListener(
                view -> registrarObjetivo(0)
        );

        btnNumero16.setOnClickListener(
                view -> registrarObjetivo(1)
        );

        btnNumero17.setOnClickListener(
                view -> registrarObjetivo(2)
        );

        btnNumero18.setOnClickListener(
                view -> registrarObjetivo(3)
        );

        btnNumero19.setOnClickListener(
                view -> registrarObjetivo(4)
        );

        btnNumero20.setOnClickListener(
                view -> registrarObjetivo(5)
        );

        btnBull.setOnClickListener(
                view -> registrarObjetivo(6)
        );

        btnDeshacerTirada.setOnClickListener(
                view -> deshacerUltimaTirada()
        );

        btnVerMarcador.setOnClickListener(
                view -> mostrarDialogoMarcador()
        );

        /*
         * Permite pasar turno aunque no se hayan usado los tres dardos.
         * Es útil cuando el jugador falla el resto o no desea registrarlos.
         */
        btnSiguienteTurno.setOnClickListener(
                view -> pasarSiguienteTurnoManual()
        );

        btnSalirPartida.setOnClickListener(
                view -> mostrarDialogoSalir()
        );
    }

    private void configurarBotonAtras() {

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        mostrarDialogoSalir();
                    }
                }
        );
    }

    // Selección del multiplicador ----------------------------------------------

    private void seleccionarMultiplicador(int multiplicador) {

        if (partidaFinalizada) {
            return;
        }

        multiplicadorSeleccionado = multiplicador;
        actualizarMultiplicadores();
    }

    // Registro de dardos -------------------------------------------------------

    private void registrarFuera() {

        if (!puedeRegistrarDardo()) {
            return;
        }

        guardarEstadoActual();

        tiradasTurno[numeroDardo - 1] = "0";

        finalizarRegistroDardo();
    }

    private void registrarObjetivo(int indiceObjetivo) {

        if (!puedeRegistrarDardo()) {
            return;
        }

        if (indiceObjetivo == 6
                && multiplicadorSeleccionado == 3) {

            Toast.makeText(
                    this,
                    "La Diana solo puede ser simple o doble",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        guardarEstadoActual();

        aplicarImpactos(
                jugadorActual,
                indiceObjetivo,
                multiplicadorSeleccionado
        );

        tiradasTurno[numeroDardo - 1] =
                crearTextoTirada(
                        indiceObjetivo,
                        multiplicadorSeleccionado
                );

        multiplicadorSeleccionado = 1;

        /*
         * Se comprueba después de cada dardo y antes de cambiar de turno.
         * buscarGanador() revisa a todos los jugadores.
         */
        int indiceGanador = buscarGanador();

        if (indiceGanador != -1) {

            partidaFinalizada = true;
            actualizarInterfazCompleta();
            abrirResultadoActivity(indiceGanador);

            return;
        }

        finalizarRegistroDardo();
    }

    private boolean puedeRegistrarDardo() {

        return !partidaFinalizada
                && numeroDardo >= 1
                && numeroDardo <= DARDOS_POR_TURNO;
    }

    private String crearTextoTirada(
            int indiceObjetivo,
            int multiplicador) {

        if (multiplicador == 1) {
            return ETIQUETAS_OBJETIVOS[indiceObjetivo];
        }

        return "X"
                + multiplicador
                + " "
                + ETIQUETAS_OBJETIVOS[indiceObjetivo];
    }

    private void finalizarRegistroDardo() {

        if (numeroDardo < DARDOS_POR_TURNO) {

            numeroDardo++;
            actualizarInterfazCompleta();

        } else {

            pasarSiguienteTurnoAutomatico();
        }
    }

    // Lógica de puntuación -----------------------------------------------------

    private void aplicarImpactos(
            int indiceJugador,
            int indiceObjetivo,
            int impactos) {

        int marcasAnteriores =
                marcasJugadores[indiceJugador][indiceObjetivo];

        int totalMarcas =
                marcasAnteriores + impactos;

        marcasJugadores[indiceJugador][indiceObjetivo] =
                Math.min(
                        MARCAS_PARA_CERRAR,
                        totalMarcas
                );

        int impactosSobrantes =
                Math.max(
                        0,
                        totalMarcas - MARCAS_PARA_CERRAR
                );

        if (impactosSobrantes == 0) {
            return;
        }

        if (modoCriquet == ModoCriquet.CRIQUET) {

            aplicarPuntosCriquet(
                    indiceJugador,
                    indiceObjetivo,
                    impactosSobrantes
            );

        } else {

            aplicarPuntosCutThroat(
                    indiceJugador,
                    indiceObjetivo,
                    impactosSobrantes
            );
        }
    }

    private void aplicarPuntosCriquet(
            int indiceJugador,
            int indiceObjetivo,
            int impactosSobrantes) {

        if (!existeRivalConObjetivoAbierto(
                indiceJugador,
                indiceObjetivo)) {

            return;
        }

        puntuacionesJugadores[indiceJugador] +=
                impactosSobrantes
                        * OBJETIVOS[indiceObjetivo];
    }

    private void aplicarPuntosCutThroat(
            int indiceJugador,
            int indiceObjetivo,
            int impactosSobrantes) {

        int puntos =
                impactosSobrantes
                        * OBJETIVOS[indiceObjetivo];

        for (int rival = 0;
             rival < nombresJugadores.length;
             rival++) {

            if (rival == indiceJugador) {
                continue;
            }

            if (marcasJugadores[rival][indiceObjetivo]
                    < MARCAS_PARA_CERRAR) {

                puntuacionesJugadores[rival] += puntos;
            }
        }
    }

    private boolean existeRivalConObjetivoAbierto(
            int indiceJugador,
            int indiceObjetivo) {

        for (int rival = 0;
             rival < nombresJugadores.length;
             rival++) {

            if (rival != indiceJugador
                    && marcasJugadores[rival][indiceObjetivo]
                    < MARCAS_PARA_CERRAR) {

                return true;
            }
        }

        return false;
    }

    // Cambio de turno ----------------------------------------------------------

    private void pasarSiguienteTurnoAutomatico() {

        pasarAlSiguienteJugador();
    }

    private void pasarSiguienteTurnoManual() {

        if (partidaFinalizada) {
            return;
        }

        pasarAlSiguienteJugador();
    }

    private void pasarAlSiguienteJugador() {

        jugadorActual++;

        if (jugadorActual >= nombresJugadores.length) {

            jugadorActual = 0;
            rondaActual++;
        }

        numeroDardo = 1;
        multiplicadorSeleccionado = 1;

        Arrays.fill(tiradasTurno, "");

        if (maxRondas > 0 && rondaActual > maxRondas) {

            int indiceGanador =
                    obtenerGanadorPorLimiteRondas();

            partidaFinalizada = true;
            abrirResultadoActivity(indiceGanador);

            return;
        }

        actualizarInterfazCompleta();
    }

    private int obtenerGanadorPorLimiteRondas() {

        int indiceGanador = 0;

        for (int i = 1;
             i < puntuacionesJugadores.length;
             i++) {

            if (modoCriquet == ModoCriquet.CRIQUET) {

                if (puntuacionesJugadores[i]
                        > puntuacionesJugadores[indiceGanador]) {

                    indiceGanador = i;
                }

            } else {

                if (puntuacionesJugadores[i]
                        < puntuacionesJugadores[indiceGanador]) {

                    indiceGanador = i;
                }
            }
        }

        return indiceGanador;
    }

    // Condición de victoria ----------------------------------------------------

    private int buscarGanador() {

        for (int jugador = 0;
             jugador < nombresJugadores.length;
             jugador++) {

            if (comprobarGanador(jugador)) {
                return jugador;
            }
        }

        return -1;
    }

    private boolean comprobarGanador(int indiceJugador) {

        if (!haCerradoTodosLosObjetivos(indiceJugador)) {
            return false;
        }

        if (modoCriquet == ModoCriquet.CRIQUET) {

            return tienePuntuacionMayorOIgual(
                    indiceJugador
            );
        }

        return tienePuntuacionMenorOIgual(
                indiceJugador
        );
    }

    private boolean haCerradoTodosLosObjetivos(
            int indiceJugador) {

        for (int marcas :
                marcasJugadores[indiceJugador]) {

            if (marcas < MARCAS_PARA_CERRAR) {
                return false;
            }
        }

        return true;
    }

    private boolean tienePuntuacionMayorOIgual(
            int indiceJugador) {

        int puntuacion =
                puntuacionesJugadores[indiceJugador];

        for (int i = 0;
             i < puntuacionesJugadores.length;
             i++) {

            if (i != indiceJugador
                    && puntuacion
                    < puntuacionesJugadores[i]) {

                return false;
            }
        }

        return true;
    }

    private boolean tienePuntuacionMenorOIgual(
            int indiceJugador) {

        int puntuacion =
                puntuacionesJugadores[indiceJugador];

        for (int i = 0;
             i < puntuacionesJugadores.length;
             i++) {

            if (i != indiceJugador
                    && puntuacion
                    > puntuacionesJugadores[i]) {

                return false;
            }
        }

        return true;
    }

    // Deshacer -----------------------------------------------------------------

    private void guardarEstadoActual() {

        historialEstados.push(
                new EstadoPartida(
                        copiarMatriz(marcasJugadores),
                        puntuacionesJugadores.clone(),
                        jugadorActual,
                        rondaActual,
                        numeroDardo,
                        multiplicadorSeleccionado,
                        tiradasTurno.clone()
                )
        );
    }

    private void deshacerUltimaTirada() {

        if (historialEstados.isEmpty()
                || partidaFinalizada) {

            Toast.makeText(
                    this,
                    "No hay ninguna tirada para deshacer",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        EstadoPartida estadoAnterior =
                historialEstados.pop();

        marcasJugadores =
                copiarMatriz(
                        estadoAnterior.marcasJugadores
                );

        puntuacionesJugadores =
                estadoAnterior
                        .puntuacionesJugadores
                        .clone();

        jugadorActual =
                estadoAnterior.jugadorActual;

        rondaActual =
                estadoAnterior.rondaActual;

        numeroDardo =
                estadoAnterior.numeroDardo;

        multiplicadorSeleccionado =
                estadoAnterior.multiplicadorSeleccionado;

        System.arraycopy(
                estadoAnterior.tiradasTurno,
                0,
                tiradasTurno,
                0,
                tiradasTurno.length
        );

        actualizarInterfazCompleta();
    }

    private int[][] copiarMatriz(int[][] matrizOriginal) {

        int[][] copia =
                new int[matrizOriginal.length][];

        for (int i = 0;
             i < matrizOriginal.length;
             i++) {

            copia[i] =
                    matrizOriginal[i].clone();
        }

        return copia;
    }

    // Actualización de interfaz ------------------------------------------------

    private void actualizarInterfazCompleta() {

        actualizarCabecera();
        actualizarInformacionTirada();
        actualizarMarcador();
        actualizarMultiplicadores();
        actualizarEstadoBotones();
        actualizarDardosVisuales();
    }

    private void actualizarCabecera() {

        txtModoJuego.setText(
                obtenerNombreModoMostrado()
        );

        txtJugadorActual.setText(
                nombresJugadores[jugadorActual]
                        .toUpperCase(Locale.ROOT)
        );

        txtJugadorActual.setTextColor(
                coloresJugadores[jugadorActual]
        );
    }

    private void actualizarInformacionTirada() {

        txtRondaActual.setText(
                String.valueOf(rondaActual)
        );

        if (maxRondas > 0) {

            txtMaxRondas.setVisibility(View.VISIBLE);
            txtMaxRondas.setText("/" + maxRondas);

        } else {

            txtMaxRondas.setVisibility(View.GONE);
        }

        txtNumeroDardo.setText(
                numeroDardo + " / " + DARDOS_POR_TURNO
        );

        txtTirada1.setText(tiradasTurno[0]);
        txtTirada2.setText(tiradasTurno[1]);
        txtTirada3.setText(tiradasTurno[2]);
    }

    private void actualizarMarcador() {

        for (int jugador = 0;
             jugador < nombresJugadores.length;
             jugador++) {

            txtNombresJugadores[jugador].setText(
                    abreviarNombre(
                            nombresJugadores[jugador]
                                    .toUpperCase(Locale.ROOT)
                    )
            );

            txtNombresJugadores[jugador].setTextColor(
                    coloresJugadores[jugador]
            );

            txtPuntosJugadores[jugador].setText(
                    puntuacionesJugadores[jugador]
                            + " PTS"
            );

            txtPuntosJugadores[jugador].setTextColor(
                    jugador == jugadorActual
                            ? coloresJugadores[jugador]
                            : Color.WHITE
            );

            for (int objetivo = 0;
                 objetivo < OBJETIVOS.length;
                 objetivo++) {

                txtMarcasJugadores[jugador][objetivo]
                        .setText(
                                obtenerTextoMarcas(
                                        marcasJugadores[jugador][objetivo]
                                )
                        );

                txtMarcasJugadores[jugador][objetivo]
                        .setTextColor(
                                jugador == jugadorActual
                                        ? coloresJugadores[jugador]
                                        : Color.WHITE
                        );
            }
        }
    }

    private String obtenerTextoMarcas(int numeroMarcas) {

        switch (numeroMarcas) {

            case 1:
                return "● ○ ○";

            case 2:
                return "● ● ○";

            case 3:
                return "● ● ●";

            default:
                return "○ ○ ○";
        }
    }

    private String abreviarNombre(String nombre) {

        int maximo;

        if (nombresJugadores.length >= 5) {
            maximo = 5;
        } else if (nombresJugadores.length == 4) {
            maximo = 7;
        } else {
            maximo = 10;
        }

        if (nombre.length() <= maximo) {
            return nombre;
        }

        return nombre.substring(0, maximo - 1) + ".";
    }

    private void actualizarMultiplicadores() {

        for (int i = 0;
             i < botonesMultiplicadores.size();
             i++) {

            Button boton =
                    botonesMultiplicadores.get(i);

            boolean seleccionado =
                    i + 1 == multiplicadorSeleccionado;

            boton.setSelected(seleccionado);

            if (seleccionado) {

                boton.setBackgroundTintList(
                        ColorStateList.valueOf(
                                getColor(R.color.gold)
                        )
                );

                boton.setTextColor(Color.BLACK);

            } else {

                boton.setBackgroundTintList(
                        tintesOriginalesMultiplicadores[i]
                );

                boton.setTextColor(
                        coloresTextoOriginalesMultiplicadores[i]
                );
            }

            boton.setAlpha(
                    seleccionado ? 1.0f : 0.82f
            );
        }
    }

    private void actualizarEstadoBotones() {

        boolean puedeJugar =
                !partidaFinalizada;

        btnFuera.setEnabled(puedeJugar);

        for (Button boton : botonesPuntuacion) {

            boton.setEnabled(puedeJugar);
            boton.setAlpha(
                    puedeJugar ? 1.0f : 0.45f
            );
        }

        for (Button boton : botonesMultiplicadores) {
            boton.setEnabled(puedeJugar);
        }

        btnFuera.setAlpha(
                puedeJugar ? 1.0f : 0.45f
        );

        boolean puedeDeshacer =
                !historialEstados.isEmpty()
                        && !partidaFinalizada;

        btnDeshacerTirada.setEnabled(
                puedeDeshacer
        );

        btnDeshacerTirada.setAlpha(
                puedeDeshacer ? 1.0f : 0.45f
        );

        /*
         * Permanece visible y disponible durante todo el turno.
         */
        btnSiguienteTurno.setVisibility(View.VISIBLE);
        btnSiguienteTurno.setEnabled(puedeJugar);
        btnSiguienteTurno.setAlpha(
                puedeJugar ? 1.0f : 0.45f
        );
    }

    private void actualizarDardosVisuales() {

        int dardosRegistrados =
                numeroDardo - 1;

        imgDardo1.setAlpha(
                dardosRegistrados >= 1
                        ? 0.35f
                        : 1.0f
        );

        imgDardo2.setAlpha(
                dardosRegistrados >= 2
                        ? 0.35f
                        : 1.0f
        );

        imgDardo3.setAlpha(
                dardosRegistrados >= 3
                        ? 0.35f
                        : 1.0f
        );
    }

    // Marcador emergente -------------------------------------------------------

    private void mostrarDialogoMarcador() {

        StringBuilder mensaje =
                new StringBuilder();

        for (int i = 0;
             i < nombresJugadores.length;
             i++) {

            mensaje.append(
                    nombresJugadores[i]
            );

            mensaje.append(": ");

            mensaje.append(
                    puntuacionesJugadores[i]
            );

            mensaje.append(" puntos");

            if (i < nombresJugadores.length - 1) {
                mensaje.append("\n");
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Marcador")
                .setMessage(mensaje.toString())
                .setPositiveButton("CERRAR", null)
                .show();
    }

    // ResultadoActivity --------------------------------------------------------

    private void abrirResultadoActivity(
            int indiceGanador) {

        int[] ordenClasificacion =
                obtenerOrdenClasificacion(
                        indiceGanador
                );

        String[] nombresOrdenados =
                ordenarNombres(
                        ordenClasificacion
                );

        int[] puntuacionesOrdenadas =
                ordenarPuntuaciones(
                        ordenClasificacion
                );

        int[] coloresOrdenados =
                ordenarColores(
                        ordenClasificacion
                );

        int[][] marcasOrdenadas =
                ordenarMarcas(
                        ordenClasificacion
                );

        Intent intent =
                new Intent(
                        this,
                        ResultadoActivity.class
                );

        intent.putExtra(
                EXTRA_MODO_JUEGO,
                obtenerNombreModoMostrado()
        );

        intent.putExtra(
                EXTRA_MAX_RONDAS,
                maxRondas
        );

        intent.putExtra(
                EXTRA_NOMBRES_JUGADORES,
                nombresOrdenados
        );

        intent.putExtra(
                EXTRA_COLOR_JUGADOR,
                coloresOrdenados
        );

        intent.putExtra(
                EXTRA_MOTIVO_FINALIZACION,
                maxRondas > 0 && rondaActual > maxRondas
                        ? "LIMITE_RONDAS"
                        : "VICTORIA"
        );

        intent.putExtra(
                EXTRA_NUMERO_JUGADORES_RESULTADO,
                nombresJugadores.length
        );

        intent.putExtra(
                EXTRA_RONDAS_JUGADAS,
                Math.min(
                        rondaActual,
                        maxRondas > 0
                                ? maxRondas
                                : rondaActual
                )
        );

        intent.putExtra(
                EXTRA_NOMBRE_GANADOR,
                nombresJugadores[indiceGanador]
        );

        intent.putExtra(
                EXTRA_PUNTUACIONES,
                puntuacionesOrdenadas
        );

        intent.putExtra(
                EXTRA_MARCAS_CRIQUET,
                aplanarMatriz(marcasOrdenadas)
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        startActivity(intent);
        finish();
    }

    private String obtenerNombreModoMostrado() {

        if (modoCriquet == ModoCriquet.CUT_THROAT) {
            return "CUT THROAT CRIQUET";
        }

        return "CRIQUET";
    }

    private int[] obtenerOrdenClasificacion(
            int indiceGanador) {

        List<Integer> indices =
                new ArrayList<>();

        for (int i = 0;
             i < nombresJugadores.length;
             i++) {

            indices.add(i);
        }

        Comparator<Integer> comparador;

        if (modoCriquet == ModoCriquet.CRIQUET) {

            comparador =
                    (a, b) -> Integer.compare(
                            puntuacionesJugadores[b],
                            puntuacionesJugadores[a]
                    );

        } else {

            comparador =
                    (a, b) -> Integer.compare(
                            puntuacionesJugadores[a],
                            puntuacionesJugadores[b]
                    );
        }

        indices.sort(comparador);

        indices.remove(
                Integer.valueOf(indiceGanador)
        );

        indices.add(0, indiceGanador);

        int[] orden =
                new int[indices.size()];

        for (int i = 0;
             i < indices.size();
             i++) {

            orden[i] = indices.get(i);
        }

        return orden;
    }

    private String[] ordenarNombres(int[] orden) {

        String[] resultado =
                new String[orden.length];

        for (int i = 0;
             i < orden.length;
             i++) {

            resultado[i] =
                    nombresJugadores[orden[i]];
        }

        return resultado;
    }

    private int[] ordenarPuntuaciones(int[] orden) {

        int[] resultado =
                new int[orden.length];

        for (int i = 0;
             i < orden.length;
             i++) {

            resultado[i] =
                    puntuacionesJugadores[orden[i]];
        }

        return resultado;
    }

    private int[] ordenarColores(int[] orden) {

        int[] resultado =
                new int[orden.length];

        for (int i = 0;
             i < orden.length;
             i++) {

            resultado[i] =
                    coloresJugadores[orden[i]];
        }

        return resultado;
    }

    private int[][] ordenarMarcas(int[] orden) {

        int[][] resultado =
                new int[orden.length][OBJETIVOS.length];

        for (int i = 0;
             i < orden.length;
             i++) {

            resultado[i] =
                    marcasJugadores[orden[i]].clone();
        }

        return resultado;
    }

    private int[] aplanarMatriz(int[][] matriz) {

        int[] resultado =
                new int[
                        matriz.length
                                * OBJETIVOS.length
                        ];

        int posicion = 0;

        for (int[] fila : matriz) {

            for (int valor : fila) {

                resultado[posicion] = valor;
                posicion++;
            }
        }

        return resultado;
    }

    // Salida -------------------------------------------------------------------

    private void mostrarDialogoSalir() {

        new AlertDialog.Builder(this)
                .setTitle("Salir de la partida")
                .setMessage(
                        "¿Quieres abandonar la partida actual?"
                )
                .setPositiveButton(
                        "SALIR",
                        (dialog, which) ->
                                volverMainActivity()
                )
                .setNegativeButton(
                        "CANCELAR",
                        null
                )
                .show();
    }

    private void volverMainActivity() {

        Intent intent =
                new Intent(
                        this,
                        MainActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
        finish();
    }

    private int convertirDp(int dp) {

        float densidad =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return Math.round(dp * densidad);
    }

    // Estado para deshacer -----------------------------------------------------

    private static class EstadoPartida {

        private final int[][] marcasJugadores;
        private final int[] puntuacionesJugadores;

        private final int jugadorActual;
        private final int rondaActual;
        private final int numeroDardo;

        private final int multiplicadorSeleccionado;
        private final String[] tiradasTurno;

        private EstadoPartida(
                int[][] marcasJugadores,
                int[] puntuacionesJugadores,
                int jugadorActual,
                int rondaActual,
                int numeroDardo,
                int multiplicadorSeleccionado,
                String[] tiradasTurno) {

            this.marcasJugadores =
                    marcasJugadores;

            this.puntuacionesJugadores =
                    puntuacionesJugadores;

            this.jugadorActual =
                    jugadorActual;

            this.rondaActual =
                    rondaActual;

            this.numeroDardo =
                    numeroDardo;

            this.multiplicadorSeleccionado =
                    multiplicadorSeleccionado;

            this.tiradasTurno =
                    tiradasTurno;
        }
    }
}
