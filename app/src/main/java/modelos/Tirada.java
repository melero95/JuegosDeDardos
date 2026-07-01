package modelos;

public class Tirada {

    private int numeroDardo;        // 1, 2 o 3

    private int sector;             // 1-20, 25 para bull, 0 si falla
    private int multiplicador;      // 0, 1, 2 o 3

    private int puntuacion;         // Resultado final de la tirada

    private String zona;            // SIMPLE, DOBLE, TRIPLE, BULL, BULLSEYE, FUERA

    private boolean doble;
    private boolean triple;
    private boolean bull;
    private boolean bullseye;
    private boolean fallo;

    public Tirada() {
    }

    public Tirada(int numeroDardo, int sector, int multiplicador, String zona) {
        this.numeroDardo = numeroDardo;
        this.sector = sector;
        this.multiplicador = multiplicador;
        this.zona = zona;

        calcularPuntuacion();
        actualizarBooleanos();
    }

    private void calcularPuntuacion() {

        if (zona.equals("FUERA")) {
            this.puntuacion = 0;
        } else if (zona.equals("BULL")) {
            this.puntuacion = 25;
        } else if (zona.equals("BULLSEYE")) {
            this.puntuacion = 50;
        } else {
            this.puntuacion = sector * multiplicador;
        }
    }

    private void actualizarBooleanos() {

        this.doble = zona.equals("DOBLE");
        this.triple = zona.equals("TRIPLE");
        this.bull = zona.equals("BULL");
        this.bullseye = zona.equals("BULLSEYE");
        this.fallo = zona.equals("FUERA");
    }

    public int getNumeroDardo() {
        return numeroDardo;
    }

    public void setNumeroDardo(int numeroDardo) {
        this.numeroDardo = numeroDardo;
    }

    public int getSector() {
        return sector;
    }

    public void setSector(int sector) {
        this.sector = sector;
        calcularPuntuacion();
    }

    public int getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(int multiplicador) {
        this.multiplicador = multiplicador;
        calcularPuntuacion();
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
        calcularPuntuacion();
        actualizarBooleanos();
    }

    public boolean isDoble() {
        return doble;
    }

    public void setDoble(boolean doble) {
        this.doble = doble;
    }

    public boolean isTriple() {
        return triple;
    }

    public void setTriple(boolean triple) {
        this.triple = triple;
    }

    public boolean isBull() {
        return bull;
    }

    public void setBull(boolean bull) {
        this.bull = bull;
    }

    public boolean isBullseye() {
        return bullseye;
    }

    public void setBullseye(boolean bullseye) {
        this.bullseye = bullseye;
    }

    public boolean isFallo() {
        return fallo;
    }

    public void setFallo(boolean fallo) {
        this.fallo = fallo;
    }
}