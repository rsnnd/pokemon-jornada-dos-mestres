public enum Tipo {
    FOGO, AGUA, PLANTA, NORMAL;

    public double multiplicadorContra(Tipo defensor) {
        if (this == FOGO && defensor == PLANTA ||
            this == AGUA && defensor == FOGO ||
            this == PLANTA && defensor == AGUA) {
            return 2.0;
        }
        if (this == PLANTA && defensor == FOGO ||
            this == FOGO && defensor == AGUA ||
            this == AGUA && defensor == PLANTA) {
            return 0.5;
        }
        return 1.0;
    }
}
