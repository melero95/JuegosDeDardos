package activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import android.content.res.ColorStateList;

import androidx.appcompat.app.AppCompatActivity;

import com.productos.juegosdedardos.R;

import java.util.ArrayList;

public class ConfigurarNuevaPartidaActivity extends AppCompatActivity {

    private Spinner spnModoJuego;

    private TextView txtMaxRondas;
    private TextView btnRestarRonda;
    private TextView btnSumarRonda;

    private LinearLayout contenedorJugadores;

    private Button btnAnadirJugador;
    private Button btnCrearPartida;
    private Button btnCancelarPartida;
    private Button btnUltimaConfiguracion;

    //Modos de juego
    private static final String MODO_301 = "301";
    private static final String MODO_501 = "501";
    private static final String MODO_CRICKET = "Cricket";
    private static final String MODO_CUT_THROAT = "Cut Throat Cricket";
    private static final String MODO_DOUBLE_DOWN = "Double Down";
    private static final String MODO_AROUND_CLOCK = "Around the Clock";

    //constantes para los extras de los intents
    private static final String EXTRA_MODO_JUEGO = "modoJuego";
    private static final String EXTRA_MAX_RONDAS = "maxRondas";
    private static final String EXTRA_NUMERO_JUGADORES = "numeroJugadores";
    private static final String EXTRA_JUGADOR = "jugador";
    private static final String EXTRA_COLOR_JUGADOR = "colorJugador";


    private int maxRondas = 15;

    private ArrayList<View> filasJugadores;
    private ArrayList<String> nombresJugadores;

    private SharedPreferences spUltimaPartida;
    private SharedPreferences.Editor editorUltimaPartida;

    private static final String SP_ULTIMA_PARTIDA = "configuracion_ultima_partida";

    private final int[] coloresJugadores = {
            R.color.jugador_rojo,
            R.color.jugador_verde,
            R.color.jugador_azul,
            R.color.jugador_amarillo,
            R.color.jugador_naranja,
            R.color.jugador_morado,
            R.color.jugador_cyan,
            R.color.jugador_blanco
    };

    private final String[] nombresColores = {
            "Rojo",
            "Verde",
            "Azul",
            "Amarillo",
            "Naranja",
            "Morado",
            "Cyan",
            "Blanco"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Configuro que la parete superior sea del mismo color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.blue));


        setContentView(R.layout.activity_nueva_partida);

        inicializarComponentes();
        inicializarSharedPreferences();
        inicializarListas();
        cargarSpinnerModoJuego();
        configurarBotonesRondas();
        configurarBotonesPrincipales();

        agregarFilaJugador();
    }

    private void inicializarComponentes() {
        spnModoJuego = findViewById(R.id.spnModoJuego);

        txtMaxRondas = findViewById(R.id.txtMaxRondas);
        btnRestarRonda = findViewById(R.id.btnRestarRonda);
        btnSumarRonda = findViewById(R.id.btnSumarRonda);

        contenedorJugadores = findViewById(R.id.contenedorJugadores);

        btnAnadirJugador = findViewById(R.id.btnAnadirJugador);
        btnCrearPartida = findViewById(R.id.btnCrearPartida);
        btnCancelarPartida = findViewById(R.id.btnCancelarPartida);
        btnUltimaConfiguracion = findViewById(R.id.btnUltimaConfiguracion);
    }

    private void inicializarListas() {
        filasJugadores = new ArrayList<>();

        nombresJugadores = new ArrayList<>();
        nombresJugadores.add("Jugador");
        nombresJugadores.add("Luis");
        nombresJugadores.add("Invitado");
    }

    private void cargarSpinnerModoJuego() {
        ArrayList<String> modosJuego = new ArrayList<>();

        modosJuego.add(MODO_301);
        modosJuego.add(MODO_501);
        modosJuego.add(MODO_CRICKET);
        modosJuego.add(MODO_CUT_THROAT);
        modosJuego.add(MODO_DOUBLE_DOWN);
        modosJuego.add(MODO_AROUND_CLOCK);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                modosJuego
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnModoJuego.setAdapter(adapter);
    }

    private void configurarBotonesRondas() {
        txtMaxRondas.setText(String.valueOf(maxRondas));

        btnSumarRonda.setOnClickListener(v -> {
            maxRondas++;
            txtMaxRondas.setText(String.valueOf(maxRondas));
        });

        btnRestarRonda.setOnClickListener(v -> {
            if (maxRondas > 1) {
                maxRondas--;
                txtMaxRondas.setText(String.valueOf(maxRondas));
            }
        });
    }

    private void configurarBotonesPrincipales() {
        btnAnadirJugador.setOnClickListener(v -> agregarFilaJugador());

        btnCancelarPartida.setOnClickListener(v -> finish());

        btnCrearPartida.setOnClickListener(v -> {
            if (maxRondas >= 20) {
                mostrarDialogoMuchasRondas();
            } else {
                crearPartida();
            }
        });

        btnUltimaConfiguracion.setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "Carga de la última configuración pendiente",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void agregarFilaJugador() {
        LayoutInflater inflater = LayoutInflater.from(this);

        View fila = inflater.inflate(
                R.layout.item_jugador_partida,
                contenedorJugadores,
                false
        );

        ImageButton btnColorJugador = fila.findViewById(R.id.btnColorJugador);
        TextView txtNombreJugador = fila.findViewById(R.id.txtNombreJugador);

        btnColorJugador.setImageTintList(
                ColorStateList.valueOf(
                        ContextCompat.getColor(this, coloresJugadores[0])
                )
        );

        txtNombreJugador.setTextColor(
                ContextCompat.getColor(this, coloresJugadores[0])
        );
        btnColorJugador.setTag(coloresJugadores[0]);

        Spinner spnJugador = fila.findViewById(R.id.spnJugador);
        TextView btnEliminarJugador = fila.findViewById(R.id.btnEliminarJugador);

        filasJugadores.add(fila);

        ArrayAdapter<String> adapterJugadores = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombresJugadores
        );

        adapterJugadores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnJugador.setAdapter(adapterJugadores);

        btnColorJugador.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Selecciona un color")
                    .setItems(nombresColores, (dialog, which) -> {

                        int colorSeleccionado =
                                ContextCompat.getColor(this, coloresJugadores[which]);

                        btnColorJugador.setImageTintList(
                                ColorStateList.valueOf(colorSeleccionado)
                        );

                        txtNombreJugador.setTextColor(colorSeleccionado);
                        btnColorJugador.setTag(coloresJugadores[which]);

                    })
                    .show();

        });

        btnEliminarJugador.setOnClickListener(v -> {
            if (filasJugadores.size() > 1) {
                contenedorJugadores.removeView(fila);
                filasJugadores.remove(fila);
                actualizarTextoJugadores();
                actualizarVisibilidadBotonesEliminar();
            } else {
                Toast.makeText(this, "Debe haber al menos un jugador", Toast.LENGTH_SHORT).show();
            }
        });

        contenedorJugadores.addView(fila);

        actualizarTextoJugadores();
        actualizarVisibilidadBotonesEliminar();
    }

    private void actualizarTextoJugadores() {
        for (int i = 0; i < filasJugadores.size(); i++) {
            View fila = filasJugadores.get(i);
            TextView txtNombreJugador = fila.findViewById(R.id.txtNombreJugador);
            txtNombreJugador.setText("Jugador " + (i + 1) + ":");
        }
    }

    private void actualizarVisibilidadBotonesEliminar() {
        for (View fila : filasJugadores) {
            TextView btnEliminarJugador = fila.findViewById(R.id.btnEliminarJugador);

            if (filasJugadores.size() == 1) {
                btnEliminarJugador.setVisibility(View.GONE);
            } else {
                btnEliminarJugador.setVisibility(View.VISIBLE);
            }
        }
    }

    private void inicializarSharedPreferences() {
        spUltimaPartida = getSharedPreferences(SP_ULTIMA_PARTIDA, MODE_PRIVATE);
        editorUltimaPartida = spUltimaPartida.edit();
    }

    private void guardarConfiguracionUltimaPartida() {
        String modoJuego = spnModoJuego.getSelectedItem().toString();

        editorUltimaPartida.putString(EXTRA_MODO_JUEGO, modoJuego);
        editorUltimaPartida.putInt(EXTRA_MAX_RONDAS, maxRondas);
        editorUltimaPartida.putInt(EXTRA_NUMERO_JUGADORES, filasJugadores.size());

        for (int i = 0; i < filasJugadores.size(); i++) {
            View fila = filasJugadores.get(i);

            Spinner spnJugador = fila.findViewById(R.id.spnJugador);

            editorUltimaPartida.putString(
                    EXTRA_JUGADOR + (i + 1),
                    spnJugador.getSelectedItem().toString()
            );

            ImageButton btnColorJugador = fila.findViewById(R.id.btnColorJugador);
            int colorJugador = (int) btnColorJugador.getTag();

            editorUltimaPartida.putInt(
                    EXTRA_COLOR_JUGADOR + (i + 1),
                    colorJugador
            );
        }

        editorUltimaPartida.apply();
    }

    private void mostrarDialogoMuchasRondas() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Muchas rondas")
                .setMessage("Has seleccionado " + maxRondas + " rondas. La partida puede durar bastante tiempo.\n\n¿Deseas continuar?")
                .setPositiveButton("Continuar", (dialog, which) -> crearPartida())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearPartida() {
        String modoJuego = spnModoJuego.getSelectedItem().toString();

        guardarConfiguracionUltimaPartida();

        Intent intent = crearIntentSegunModo(modoJuego);

        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Modo de juego no disponible todavía", Toast.LENGTH_SHORT).show();
        }
    }

    private Intent crearIntentSegunModo(String modoJuego) {

        Intent intent = null;

        switch (modoJuego) {

            case MODO_301:
            case MODO_501:
                intent = new Intent(
                        this,
                        PartidaPuntosActivity.class
                );
                break;

            case MODO_CRICKET:
            case MODO_CUT_THROAT:
                intent = new Intent(
                        this,
                        PartidaCriquetActivity.class
                );
                break;

            case MODO_DOUBLE_DOWN:
            case MODO_AROUND_CLOCK:
                intent = new Intent(
                        this,
                        PartidaRondasActivity.class
                );
                break;
        }

        if (intent != null) {

            // Datos generales de la partida
            intent.putExtra(
                    EXTRA_MODO_JUEGO,
                    modoJuego
            );

            intent.putExtra(
                    EXTRA_MAX_RONDAS,
                    maxRondas
            );

            intent.putExtra(
                    EXTRA_NUMERO_JUGADORES,
                    filasJugadores.size()
            );

            /*
             * Lista con todos los nombres seleccionados.
             *
             * Esta es la lista que recibe PartidaPuntosActivity.
             */
            ArrayList<String> nombresSeleccionados =
                    new ArrayList<>();

            for (int i = 0; i < filasJugadores.size(); i++) {

                View fila = filasJugadores.get(i);

                Spinner spnJugador =
                        fila.findViewById(R.id.spnJugador);

                String nombreJugador =
                        spnJugador.getSelectedItem().toString();

                nombresSeleccionados.add(nombreJugador);

                /*
                 * Conservamos también los extras individuales
                 * porque podrán utilizarlos las otras actividades.
                 */
                intent.putExtra(
                        EXTRA_JUGADOR + (i + 1),
                        nombreJugador
                );

                ImageButton btnColorJugador =
                        fila.findViewById(R.id.btnColorJugador);

                int colorJugador =
                        (int) btnColorJugador.getTag();

                intent.putExtra(
                        EXTRA_COLOR_JUGADOR + (i + 1),
                        colorJugador
                );
            }

            /*
             * Enviamos todos los nombres en una sola lista.
             */
            intent.putStringArrayListExtra(
                    PartidaPuntosActivity.EXTRA_NOMBRES_JUGADORES,
                    nombresSeleccionados
            );
        }

        return intent;
    }
}