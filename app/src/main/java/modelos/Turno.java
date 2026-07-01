package modelos;

public class Turno {

    private int numeroTurno;

    private String nombreJugador;

    private int dardo1;
    private int dardo2;
    private int dardo3;

    private int puntuacionTurno;

    private int puntuacionAntes;
    private int puntuacionDespues;

    private int ronda;

    public Turno() {
    }

    public Turno(int numeroTurno, String nombreJugador, int ronda) {

        this.numeroTurno = numeroTurno;
        this.nombreJugador = nombreJugador;

        this.ronda = ronda;

        this.dardo1 = 0;
        this.dardo2 = 0;
        this.dardo3 = 0;

        this.puntuacionTurno = 0;

        this.puntuacionAntes = 0;
        this.puntuacionDespues = 0;
    }

    // Getters y setters

}