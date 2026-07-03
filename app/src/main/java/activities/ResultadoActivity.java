package activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.productos.juegosdedardos.R;

import java.util.ArrayList;

import adapters.ClasificacionResultadoAdapter;
import modelos.ResultadoJugador;

public class ResultadoActivity extends AppCompatActivity {

    //Claves del Intent ------------------------------------------------------------

    public static final String EXTRA_MODO_JUEGO =
            "resultado_modo_juego";

    public static final String EXTRA_MOTIVO_FINALIZACION =
            "resultado_motivo_finalizacion";

    public static final String EXTRA_NUMERO_JUGADORES =
            "resultado_numero_jugadores";

    public static final String EXTRA_RONDAS_JUGADAS =
            "resultado_rondas_jugadas";

    public static final String EXTRA_MAX_RONDAS =
            "resultado_max_rondas";

    public static final String EXTRA_NOMBRE_GANADOR =
            "resultado_nombre_ganador";

    public static final String EXTRA_NOMBRES_JUGADORES =
            "resultado_nombres_jugadores";

    public static final String EXTRA_PUNTUACIONES =
            "resultado_puntuaciones";

    public static final String EXTRA_COLORES =
            "resultado_colores";

    public static final String EXTRA_POSICIONES =
            "resultado_posiciones";

    public static final String EXTRA_INDICES_ORIGINALES =
            "resultado_indices_originales";

    //Datos generales recibidos ----------------------------------------------------

    private String modoJuego;
    private String motivoFinalizacion;
    private String nombreGanador;

    private int numeroJugadores;
    private int rondasJugadas;
    private int maxRondas;

    //Listas recibidas -------------------------------------------------------------

    private ArrayList<String> nombresJugadores;
    private ArrayList<Integer> puntuacionesJugadores;
    private ArrayList<Integer> coloresJugadores;
    private ArrayList<Integer> posicionesJugadores;
    private ArrayList<Integer> indicesOriginales;

    //Datos preparados para la pantalla --------------------------------------------

    private ArrayList<ResultadoJugador> jugadoresResultado;
    private ArrayList<ResultadoJugador> jugadoresClasificacion;

    private ResultadoJugador jugadorGanador;

    //Elementos principales --------------------------------------------------------

    private ImageView imgTrofeo;

    private TextView txtTituloResultado;
    private TextView txtNombreGanador;
    private TextView txtPuntuacionGanador;
    private TextView txtTituloClasificacion;

    private LinearLayout contenedorGanador;
    private LinearLayout filaGanador;
    private LinearLayout contenedorClasificacion;
    private LinearLayout contenedorBotonesResultado;

    private RecyclerView recyclerClasificacion;

    private Button btnEstadisticas;
    private Button btnRevancha;
    private Button btnMenuPrincipal;

    //Adaptador --------------------------------------------------------------------

    private ClasificacionResultadoAdapter clasificacionAdapter;

    //Crear Activity ---------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resultado);

        iniciarElementos();
        recibirDatosIntent();

        if (!validarDatosRecibidos()) {
            mostrarErrorDatos();
            return;
        }

        prepararDatosResultado();
        prepararRecyclerView();
        configurarListeners();
        actualizarPantalla();

        //Ejecutar la animación cuando ya se han cargado los datos
        mostrarAnimacionResultado();
    }

    //Iniciar elementos ------------------------------------------------------------

    private void iniciarElementos() {

        imgTrofeo = findViewById(
                R.id.imgTrofeo
        );

        txtTituloResultado = findViewById(
                R.id.txtTituloResultado
        );

        txtNombreGanador = findViewById(
                R.id.txtNombreGanador
        );

        txtPuntuacionGanador = findViewById(
                R.id.txtPuntuacionGanador
        );

        txtTituloClasificacion = findViewById(
                R.id.txtTituloClasificacion
        );

        contenedorGanador = findViewById(
                R.id.contenedorGanador
        );

        filaGanador = findViewById(
                R.id.filaGanador
        );

        contenedorClasificacion = findViewById(
                R.id.contenedorClasificacion
        );

        contenedorBotonesResultado = findViewById(
                R.id.contenedorBotonesResultado
        );

        recyclerClasificacion = findViewById(
                R.id.recyclerClasificacion
        );

        btnEstadisticas = findViewById(
                R.id.btnEstadisticas
        );

        btnRevancha = findViewById(
                R.id.btnRevancha
        );

        btnMenuPrincipal = findViewById(
                R.id.btnMenuPrincipal
        );
    }

    //Recibir datos del Intent ------------------------------------------------------

    private void recibirDatosIntent() {

        Intent intent = getIntent();

        if (intent == null) {
            inicializarDatosVacios();
            return;
        }

        modoJuego = intent.getStringExtra(
                EXTRA_MODO_JUEGO
        );

        motivoFinalizacion = intent.getStringExtra(
                EXTRA_MOTIVO_FINALIZACION
        );

        numeroJugadores = intent.getIntExtra(
                EXTRA_NUMERO_JUGADORES,
                0
        );

        rondasJugadas = intent.getIntExtra(
                EXTRA_RONDAS_JUGADAS,
                0
        );

        maxRondas = intent.getIntExtra(
                EXTRA_MAX_RONDAS,
                0
        );

        nombreGanador = intent.getStringExtra(
                EXTRA_NOMBRE_GANADOR
        );

        nombresJugadores =
                intent.getStringArrayListExtra(
                        EXTRA_NOMBRES_JUGADORES
                );

        puntuacionesJugadores =
                intent.getIntegerArrayListExtra(
                        EXTRA_PUNTUACIONES
                );

        coloresJugadores =
                intent.getIntegerArrayListExtra(
                        EXTRA_COLORES
                );

        posicionesJugadores =
                intent.getIntegerArrayListExtra(
                        EXTRA_POSICIONES
                );

        indicesOriginales =
                intent.getIntegerArrayListExtra(
                        EXTRA_INDICES_ORIGINALES
                );

        corregirDatosNulos();
    }

    //Inicializar datos vacíos ------------------------------------------------------

    private void inicializarDatosVacios() {

        modoJuego = "";
        motivoFinalizacion = "";
        nombreGanador = "";

        numeroJugadores = 0;
        rondasJugadas = 0;
        maxRondas = 0;

        nombresJugadores = new ArrayList<>();
        puntuacionesJugadores = new ArrayList<>();
        coloresJugadores = new ArrayList<>();
        posicionesJugadores = new ArrayList<>();
        indicesOriginales = new ArrayList<>();

        jugadoresResultado = new ArrayList<>();
        jugadoresClasificacion = new ArrayList<>();
    }

    //Corregir valores nulos --------------------------------------------------------

    private void corregirDatosNulos() {

        if (modoJuego == null) {
            modoJuego = "";
        }

        if (motivoFinalizacion == null) {
            motivoFinalizacion = "";
        }

        if (nombreGanador == null) {
            nombreGanador = "";
        }

        if (nombresJugadores == null) {
            nombresJugadores = new ArrayList<>();
        }

        if (puntuacionesJugadores == null) {
            puntuacionesJugadores = new ArrayList<>();
        }

        if (coloresJugadores == null) {
            coloresJugadores = new ArrayList<>();
        }

        if (posicionesJugadores == null) {
            posicionesJugadores = new ArrayList<>();
        }

        if (indicesOriginales == null) {
            indicesOriginales = new ArrayList<>();
        }
    }

    //Validar datos recibidos -------------------------------------------------------

    private boolean validarDatosRecibidos() {

        if (nombresJugadores.isEmpty()) {
            return false;
        }

        int cantidadJugadores =
                nombresJugadores.size();

        if (puntuacionesJugadores.size()
                != cantidadJugadores) {

            return false;
        }

        if (coloresJugadores.size()
                != cantidadJugadores) {

            return false;
        }

        if (posicionesJugadores.size()
                != cantidadJugadores) {

            return false;
        }

        if (indicesOriginales.size()
                != cantidadJugadores) {

            return false;
        }

        numeroJugadores = cantidadJugadores;

        if (nombreGanador.isEmpty()) {
            nombreGanador =
                    nombresJugadores.get(0);
        }

        return true;
    }

    //Preparar objetos ResultadoJugador --------------------------------------------

    private void prepararDatosResultado() {

        jugadoresResultado = new ArrayList<>();
        jugadoresClasificacion = new ArrayList<>();

        jugadorGanador = null;

        for (int i = 0;
             i < nombresJugadores.size();
             i++) {

            String nombre =
                    nombresJugadores.get(i);

            int puntuacion =
                    puntuacionesJugadores.get(i);

            int posicion =
                    posicionesJugadores.get(i);

            boolean esGanador =
                    posicion == 1;

            ResultadoJugador resultadoJugador =
                    new ResultadoJugador(
                            nombre,
                            puntuacion,
                            posicion,
                            esGanador,
                            true
                    );

            jugadoresResultado.add(
                    resultadoJugador
            );

            if (esGanador) {

                jugadorGanador =
                        resultadoJugador;

            } else {

                jugadoresClasificacion.add(
                        resultadoJugador
                );
            }
        }

        //Seguridad por si no llegó la posición 1
        if (jugadorGanador == null
                && !jugadoresResultado.isEmpty()) {

            jugadorGanador =
                    jugadoresResultado.get(0);

            jugadoresClasificacion.clear();

            for (int i = 1;
                 i < jugadoresResultado.size();
                 i++) {

                jugadoresClasificacion.add(
                        jugadoresResultado.get(i)
                );
            }
        }
    }

    //Preparar RecyclerView ---------------------------------------------------------

    private void prepararRecyclerView() {

        recyclerClasificacion.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerClasificacion.setNestedScrollingEnabled(
                false
        );

        clasificacionAdapter =
                new ClasificacionResultadoAdapter(
                        jugadoresClasificacion
                );

        recyclerClasificacion.setAdapter(
                clasificacionAdapter
        );
    }

    //Actualizar pantalla completa -------------------------------------------------

    private void actualizarPantalla() {

        actualizarTitulo();
        actualizarGanador();
        actualizarClasificacion();
        actualizarBotones();
    }

    //Actualizar título -------------------------------------------------------------

    private void actualizarTitulo() {

        if (modoJuego.trim().isEmpty()) {

            txtTituloResultado.setText(
                    "RESULTADO"
            );

        } else {

            txtTituloResultado.setText(
                    "RESULTADO - "
                            + modoJuego.toUpperCase()
            );
        }
    }

    //Actualizar ganador ------------------------------------------------------------

    private void actualizarGanador() {

        if (jugadorGanador == null) {

            contenedorGanador.setVisibility(
                    View.GONE
            );

            return;
        }

        contenedorGanador.setVisibility(
                View.VISIBLE
        );

        txtNombreGanador.setText(
                jugadorGanador.getNombre()
        );

        if (jugadorGanador.isMostrarPuntuacion()) {

            txtPuntuacionGanador.setVisibility(
                    View.VISIBLE
            );

            txtPuntuacionGanador.setText(
                    jugadorGanador.getPuntuacion()
                            + " puntos"
            );

        } else {

            txtPuntuacionGanador.setVisibility(
                    View.GONE
            );
        }
    }

    //Actualizar clasificación -----------------------------------------------------

    private void actualizarClasificacion() {

        if (jugadoresClasificacion.isEmpty()) {

            contenedorClasificacion.setVisibility(
                    View.GONE
            );

            return;
        }

        contenedorClasificacion.setVisibility(
                View.VISIBLE
        );

        clasificacionAdapter.notifyDataSetChanged();
    }

    //Actualizar botones ------------------------------------------------------------

    private void actualizarBotones() {

        boolean hayResultados =
                !jugadoresResultado.isEmpty();

        btnEstadisticas.setEnabled(
                hayResultados
        );

        btnRevancha.setEnabled(
                hayResultados
        );
    }

//Mostrar animación del resultado ----------------------------------------------

    private void mostrarAnimacionResultado() {

        //Estado inicial del trofeo
        imgTrofeo.setScaleX(0f);
        imgTrofeo.setScaleY(0f);
        imgTrofeo.setAlpha(0f);

        //Estado inicial de la información del ganador
        filaGanador.setAlpha(0f);
        filaGanador.setTranslationY(30f);

        //Estado inicial de la clasificación
        contenedorClasificacion.setAlpha(0f);
        contenedorClasificacion.setTranslationY(30f);

        //Estado inicial de los botones
        contenedorBotonesResultado.setAlpha(0f);
        contenedorBotonesResultado.setTranslationY(30f);

        //Animación del trofeo
        imgTrofeo.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(1200)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .withEndAction(() -> {

                    //Después aparece el ganador
                    filaGanador.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(800)
                            .setInterpolator(
                                    new DecelerateInterpolator()
                            )
                            .withEndAction(
                                    this::mostrarClasificacionAnimada
                            )
                            .start();
                })
                .start();
    }

//Mostrar clasificación y botones de forma animada -----------------------------

    private void mostrarClasificacionAnimada() {

        if (contenedorClasificacion.getVisibility()
                == View.VISIBLE) {

            contenedorClasificacion.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(700)
                    .setInterpolator(
                            new DecelerateInterpolator()
                    )
                    .withEndAction(
                            this::mostrarBotonesAnimados
                    )
                    .start();

        } else {

            mostrarBotonesAnimados();
        }
    }

//Mostrar botones de forma animada ---------------------------------------------

    private void mostrarBotonesAnimados() {

        contenedorBotonesResultado.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();
    }

    //Configurar botones ------------------------------------------------------------

    private void configurarListeners() {

        btnEstadisticas.setOnClickListener(
                view -> abrirEstadisticas()
        );

        btnRevancha.setOnClickListener(
                view -> realizarRevancha()
        );

        btnMenuPrincipal.setOnClickListener(
                view -> volverMenuPrincipal()
        );
    }

    //Abrir estadísticas ------------------------------------------------------------

    private void abrirEstadisticas() {

        Toast.makeText(
                this,
                "Las estadísticas se implementarán más adelante",
                Toast.LENGTH_SHORT
        ).show();
    }

    //Realizar revancha -------------------------------------------------------------

    private void realizarRevancha() {

        Toast.makeText(
                this,
                "La revancha se implementará más adelante",
                Toast.LENGTH_SHORT
        ).show();
    }

    //Mostrar error al recibir datos ------------------------------------------------

    private void mostrarErrorDatos() {

        Toast.makeText(
                this,
                "No se han podido cargar los resultados de la partida",
                Toast.LENGTH_LONG
        ).show();

        volverMenuPrincipal();
    }

    //Volver al menú principal ------------------------------------------------------

    private void volverMenuPrincipal() {

        Intent intent = new Intent(
                ResultadoActivity.this,
                MainActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    //Evitar regresar a una partida terminada --------------------------------------

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        volverMenuPrincipal();
    }
}