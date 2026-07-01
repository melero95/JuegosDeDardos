package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import modelos.ResultadoJugador;
import com.productos.juegosdedardos.R;

public class ClasificacionResultadoAdapter
        extends RecyclerView.Adapter<ClasificacionResultadoAdapter.ResultadoViewHolder> {

    private final ArrayList<ResultadoJugador> listaJugadores;

    //Constructor --------------------
    public ClasificacionResultadoAdapter(ArrayList<ResultadoJugador> listaJugadores) {
        this.listaJugadores = listaJugadores;
    }

    //Crear fila --------------------
    @NonNull
    @Override
    public ResultadoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clasificacion_resultado, parent, false);

        return new ResultadoViewHolder(vista);
    }

    //Rellenar fila --------------------
    @Override
    public void onBindViewHolder(
            @NonNull ResultadoViewHolder holder,
            int position) {

        ResultadoJugador jugador = listaJugadores.get(position);

        holder.txtPosicion.setText(String.valueOf(jugador.getPosicion()));
        holder.txtNombre.setText(jugador.getNombre());

        if (jugador.isMostrarPuntuacion()) {
            holder.txtPuntos.setVisibility(View.VISIBLE);
            holder.txtPuntos.setText(jugador.getPuntuacion() + " puntos");
        } else {
            holder.txtPuntos.setVisibility(View.GONE);
        }
    }

    //Obtener cantidad --------------------
    @Override
    public int getItemCount() {
        return listaJugadores.size();
    }

    //Actualizar clasificación --------------------
    public void actualizarLista(
            ArrayList<ResultadoJugador> nuevaLista
    ) {

        ArrayList<ResultadoJugador> copiaLista =
                new ArrayList<>(nuevaLista);

        listaJugadores.clear();
        listaJugadores.addAll(copiaLista);

        notifyDataSetChanged();
    }

    //ViewHolder --------------------
    static class ResultadoViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtPosicion;
        private final TextView txtNombre;
        private final TextView txtPuntos;

        public ResultadoViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPosicion = itemView.findViewById(
                    R.id.txtPosicionClasificacion
            );

            txtNombre = itemView.findViewById(
                    R.id.txtNombreJugadorClasificacion
            );

            txtPuntos = itemView.findViewById(
                    R.id.txtPuntosJugadorClasificacion
            );
        }
    }
}