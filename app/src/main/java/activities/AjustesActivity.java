package activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.productos.juegosdedardos.R;

/** Pantalla base de preferencias. Sus valores aún no se aplican globalmente. */
public class AjustesActivity extends AppCompatActivity {

    public static final String SP_AJUSTES = "ajustes_partida";
    public static final String PREF_JUGADOR = "jugador_default";
    public static final String PREF_MODO = "modo_default";
    public static final String PREF_DARDOS = "dardos_default";
    public static final String PREF_MANTENER_MULTIPLICADOR = "mantener_multiplicador";
    public static final String PREF_CIERRE_DOBLE = "cierre_doble_default";
    public static final String PREF_ORDEN_ALEATORIO = "orden_aleatorio_default";
    public static final String PREF_CONFIRMAR_SALIDA = "confirmar_salida";
    public static final String PREF_VIBRACION = "vibracion";

    private static final String[] JUGADORES = {"Jugador", "Luis", "Invitado"};
    private static final String[] MODOS = {
            "301", "501", "Cricket", "Cut Throat Cricket", "Double Down", "Around the Clock"
    };
    private static final Integer[] DARDOS = {1, 2, 3, 4};

    private Spinner spnJugadorPredeterminado;
    private Spinner spnModoPredeterminado;
    private Spinner spnDardosPredeterminados;
    private CheckBox chkMantenerMultiplicador;
    private CheckBox chkCierreDoble;
    private CheckBox chkOrdenAleatorio;
    private CheckBox chkConfirmarSalida;
    private CheckBox chkVibracion;
    private SharedPreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.blue));
        setContentView(R.layout.activity_ajustes);

        preferencias = getSharedPreferences(SP_AJUSTES, MODE_PRIVATE);
        inicializarVistas();
        prepararSpinners();
        cargarPreferencias();
        configurarBotones();
    }

    private void inicializarVistas() {
        spnJugadorPredeterminado = findViewById(R.id.spnJugadorPredeterminado);
        spnModoPredeterminado = findViewById(R.id.spnModoPredeterminado);
        spnDardosPredeterminados = findViewById(R.id.spnDardosPredeterminados);
        chkMantenerMultiplicador = findViewById(R.id.chkMantenerMultiplicador);
        chkCierreDoble = findViewById(R.id.chkCierreDoblePredeterminado);
        chkOrdenAleatorio = findViewById(R.id.chkOrdenAleatorioPredeterminado);
        chkConfirmarSalida = findViewById(R.id.chkConfirmarSalida);
        chkVibracion = findViewById(R.id.chkVibracion);
    }

    private void prepararSpinners() {
        configurarSpinner(spnJugadorPredeterminado, JUGADORES);
        configurarSpinner(spnModoPredeterminado, MODOS);
        configurarSpinner(spnDardosPredeterminados, DARDOS);
    }

    private <T> void configurarSpinner(Spinner spinner, T[] elementos) {
        ArrayAdapter<T> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, elementos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void cargarPreferencias() {
        seleccionarValor(spnJugadorPredeterminado,
                preferencias.getString(PREF_JUGADOR, JUGADORES[0]));
        seleccionarValor(spnModoPredeterminado,
                preferencias.getString(PREF_MODO, "501"));
        int dardos = Math.max(1, Math.min(4, preferencias.getInt(PREF_DARDOS, 3)));
        spnDardosPredeterminados.setSelection(dardos - 1);

        chkMantenerMultiplicador.setChecked(
                preferencias.getBoolean(PREF_MANTENER_MULTIPLICADOR, false));
        chkCierreDoble.setChecked(preferencias.getBoolean(PREF_CIERRE_DOBLE, false));
        chkOrdenAleatorio.setChecked(preferencias.getBoolean(PREF_ORDEN_ALEATORIO, false));
        chkConfirmarSalida.setChecked(preferencias.getBoolean(PREF_CONFIRMAR_SALIDA, true));
        chkVibracion.setChecked(preferencias.getBoolean(PREF_VIBRACION, true));
    }

    private void configurarBotones() {
        Button btnGuardar = findViewById(R.id.btnGuardarAjustes);
        Button btnRestablecer = findViewById(R.id.btnRestablecerAjustes);
        Button btnVolver = findViewById(R.id.btnVolverAjustes);

        btnGuardar.setOnClickListener(v -> {
            guardarPreferencias();
            Toast.makeText(this, "Ajustes guardados", Toast.LENGTH_SHORT).show();
        });
        btnRestablecer.setOnClickListener(v -> restablecerPreferencias());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void guardarPreferencias() {
        preferencias.edit()
                .putString(PREF_JUGADOR, valorSeleccionado(spnJugadorPredeterminado))
                .putString(PREF_MODO, valorSeleccionado(spnModoPredeterminado))
                .putInt(PREF_DARDOS, (Integer) spnDardosPredeterminados.getSelectedItem())
                .putBoolean(PREF_MANTENER_MULTIPLICADOR, chkMantenerMultiplicador.isChecked())
                .putBoolean(PREF_CIERRE_DOBLE, chkCierreDoble.isChecked())
                .putBoolean(PREF_ORDEN_ALEATORIO, chkOrdenAleatorio.isChecked())
                .putBoolean(PREF_CONFIRMAR_SALIDA, chkConfirmarSalida.isChecked())
                .putBoolean(PREF_VIBRACION, chkVibracion.isChecked())
                .apply();
    }

    private void restablecerPreferencias() {
        preferencias.edit().clear().apply();
        cargarPreferencias();
        Toast.makeText(this, "Ajustes restablecidos", Toast.LENGTH_SHORT).show();
    }

    private String valorSeleccionado(Spinner spinner) {
        return String.valueOf(spinner.getSelectedItem());
    }

    private void seleccionarValor(Spinner spinner, String valor) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (String.valueOf(spinner.getItemAtPosition(i)).equals(valor)) {
                spinner.setSelection(i);
                return;
            }
        }
    }
}
