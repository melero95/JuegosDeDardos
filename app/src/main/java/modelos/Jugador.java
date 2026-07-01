package modelos;

public class Jugador {

    private int id;
    private String nombre;

    private int partidasJugadas;
    private int victorias;
    private int derrotas;

    private int puntuacionMaximaTirada;
    private int puntuacionMaximaTurno;

    private int totalPuntosAnotados;
    private int totalDardosLanzados;

    private String colorPreferido;

    private boolean activo;

    public Jugador() {
    }

    public Jugador(int id, String nombre, String colorPreferido) {
        this.id = id;
        this.nombre = nombre;
        this.colorPreferido = colorPreferido;
        this.partidasJugadas = 0;
        this.victorias = 0;
        this.derrotas = 0;
        this.puntuacionMaximaTirada = 0;
        this.puntuacionMaximaTurno = 0;
        this.totalPuntosAnotados = 0;
        this.totalDardosLanzados = 0;
        this.activo = true;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getColorPreferido() {
        return colorPreferido;
    }

    public void setColorPreferido(String colorPreferido) {
        this.colorPreferido = colorPreferido;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public void setDerrotas(int derrotas) {
        this.derrotas = derrotas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public void setPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }

    public int getPuntuacionMaximaTirada() {
        return puntuacionMaximaTirada;
    }

    public void setPuntuacionMaximaTirada(int puntuacionMaximaTirada) {
        this.puntuacionMaximaTirada = puntuacionMaximaTirada;
    }

    public int getPuntuacionMaximaTurno() {
        return puntuacionMaximaTurno;
    }

    public void setPuntuacionMaximaTurno(int puntuacionMaximaTurno) {
        this.puntuacionMaximaTurno = puntuacionMaximaTurno;
    }

    public int getTotalDardosLanzados() {
        return totalDardosLanzados;
    }

    public void setTotalDardosLanzados(int totalDardosLanzados) {
        this.totalDardosLanzados = totalDardosLanzados;
    }

    public int getTotalPuntosAnotados() {
        return totalPuntosAnotados;
    }

    public void setTotalPuntosAnotados(int totalPuntosAnotados) {
        this.totalPuntosAnotados = totalPuntosAnotados;
    }

    public int getVictorias() {
        return victorias;
    }

    public void setVictorias(int victorias) {
        this.victorias = victorias;
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "nombre='" + nombre + '\'' +
                ", colorPreferido='" + colorPreferido + '\'' +
                ", victorias=" + victorias +
                ", partidasJugadas=" + partidasJugadas +
                '}';
    }
}