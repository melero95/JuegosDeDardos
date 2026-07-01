package modelos;

import java.util.ArrayList;

public class Partida {

    private int id;

    // Datos de configuración
    private String modoJuego;
    private int maxRondas;
    private int numeroJugadores;

    // Desarrollo de la partida
    private int rondasJugadas;
    private boolean finalizada;

    // Clasificación final
    private String ganador;
    private String segundoPuesto;
    private String tercerPuesto;

    // Fechas
    private String fechaInicio;
    private String fechaFin;

    // Participantes
    private ArrayList<Jugador> jugadores;

    public Partida() {
        jugadores = new ArrayList<>();
    }

    public Partida(int id, String modoJuego, int maxRondas) {

        this.id = id;
        this.modoJuego = modoJuego;
        this.maxRondas = maxRondas;

        this.numeroJugadores = 0;
        this.rondasJugadas = 0;

        this.finalizada = false;

        this.ganador = "";
        this.segundoPuesto = "";
        this.tercerPuesto = "";

        this.fechaInicio = "";
        this.fechaFin = "";

        this.jugadores = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getModoJuego() {
        return modoJuego;
    }

    public void setModoJuego(String modoJuego) {
        this.modoJuego = modoJuego;
    }

    public int getMaxRondas() {
        return maxRondas;
    }

    public void setMaxRondas(int maxRondas) {
        this.maxRondas = maxRondas;
    }

    public int getNumeroJugadores() {
        return numeroJugadores;
    }

    public void setNumeroJugadores(int numeroJugadores) {
        this.numeroJugadores = numeroJugadores;
    }

    public int getRondasJugadas() {
        return rondasJugadas;
    }

    public void setRondasJugadas(int rondasJugadas) {
        this.rondasJugadas = rondasJugadas;
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public void setFinalizada(boolean finalizada) {
        this.finalizada = finalizada;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    public String getSegundoPuesto() {
        return segundoPuesto;
    }

    public void setSegundoPuesto(String segundoPuesto) {
        this.segundoPuesto = segundoPuesto;
    }

    public String getTercerPuesto() {
        return tercerPuesto;
    }

    public void setTercerPuesto(String tercerPuesto) {
        this.tercerPuesto = tercerPuesto;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(ArrayList<Jugador> jugadores) {
        this.jugadores = jugadores;
    }
}