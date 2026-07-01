package activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.productos.juegosdedardos.R;

public class MainActivity extends AppCompatActivity {

    // SharedPreferences
    private static final String PREF_PARTIDA_GUARDADA = "partida_guardada";
    private static final String CLAVE_EXISTE_PARTIDA = "existe_partida";

    // Elementos de la pantalla principal
    Button btnContinuar;
    Button btnNuevaPartida;
    Button btnHistorialPartidas;
    Button btnAjustes;

    // Toolbar y menú lateral
    Toolbar toolbarMain;
    DrawerLayout drawerMain;
    NavigationView navigationMain;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // No usamos EdgeToEdge para evitar que el Toolbar se meta en la barra superior
        setContentView(R.layout.activity_main);

        iniciarComponentes();
        configurarToolbar();
        configurarMenuLateral();
        configurarListeners();
        configurarBotonAtras();
        comprobarPartidaGuardada();
    }

    private void iniciarComponentes() {
        toolbarMain = findViewById(R.id.toolbarMain);
        drawerMain = findViewById(R.id.drawerMain);
        navigationMain = findViewById(R.id.navigationMain);

        btnContinuar = findViewById(R.id.btnContinuar);
        btnNuevaPartida = findViewById(R.id.btnNuevaPartida);
        btnHistorialPartidas = findViewById(R.id.btnHistorialPartidas);
        btnAjustes = findViewById(R.id.btnAjustes);
    }

    private void configurarToolbar() {
        setSupportActionBar(toolbarMain);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void configurarMenuLateral() {

        // Mantiene los colores originales de los iconos del menú lateral
        navigationMain.setItemIconTintList(null);

        toggle = new ActionBarDrawerToggle(
                this,
                drawerMain,
                toolbarMain,
                R.string.abrir_menu,
                R.string.cerrar_menu
        );

        drawerMain.addDrawerListener(toggle);
        toggle.syncState();

        navigationMain.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menuEstadisticas) {

                Intent intent = new Intent(MainActivity.this, EstadisticasActivity.class);
                startActivity(intent);

            } else if (id == R.id.menuRecords) {

                // Pendiente de crear RecordsActivity
                // Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
                // startActivity(intent);

            } else if (id == R.id.menuReglas) {

                // Pendiente de crear ReglasActivity o ComoJugarActivity
                // Intent intent = new Intent(MainActivity.this, ReglasActivity.class);
                // startActivity(intent);

            } else if (id == R.id.menuAyuda) {

                Intent intent = new Intent(MainActivity.this, AyudaActivity.class);
                startActivity(intent);

            } else if (id == R.id.menuAcercaDe) {

                // Pendiente de crear AcercaDeActivity
                // Intent intent = new Intent(MainActivity.this, AcercaDeActivity.class);
                // startActivity(intent);

            }

            drawerMain.closeDrawers();
            return true;
        });
    }

    private void configurarListeners() {

        btnContinuar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PartidaPuntosActivity.class);
            intent.putExtra("modo_apertura", "continuar");
            startActivity(intent);
        });

        btnNuevaPartida.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConfigurarNuevaPartidaActivity.class);
            startActivity(intent);
        });

        btnHistorialPartidas.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistorialPartidasActivity.class);
            startActivity(intent);
        });

        btnAjustes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AjustesActivity.class);
            startActivity(intent);
        });
    }

    private void comprobarPartidaGuardada() {

        SharedPreferences preferences = getSharedPreferences(PREF_PARTIDA_GUARDADA, MODE_PRIVATE);

        boolean existePartida = preferences.getBoolean(CLAVE_EXISTE_PARTIDA, false);

        if (existePartida) {
            btnContinuar.setVisibility(View.VISIBLE);
        } else {
            btnContinuar.setVisibility(View.GONE);
        }
    }

    private void configurarBotonAtras() {

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (drawerMain.isDrawerOpen(GravityCompat.START)) {
                    drawerMain.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }
}