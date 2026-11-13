import java.util.Random;

public class FabricaPokemon {
    private FabricaPokemon() { }

    public static Pokemon criar(int numero) {
        switch (numero) {
            case 1:
                return new PokemonFogo("Charmander", 45, 5,
                        new Ataque("Arranhao", Tipo.NORMAL, 9), new Ataque("Brasa", Tipo.FOGO, 11));
            case 2:
                return new PokemonAgua("Squirtle", 50, 5,
                        new Ataque("Investida", Tipo.NORMAL, 9), new Ataque("Jato d'Agua", Tipo.AGUA, 11));
            case 3:
                return new PokemonPlanta("Bulbasaur", 50, 5,
                        new Ataque("Investida", Tipo.NORMAL, 9), new Ataque("Chicote de Vinha", Tipo.PLANTA, 11));
            case 4:
                return new PokemonNormal("Pidgey", 38, 5,
                        new Ataque("Bicada", Tipo.NORMAL, 8), new Ataque("Investida", Tipo.NORMAL, 9));
            default:
                return new PokemonNormal("Rattata", 35, 5,
                        new Ataque("Investida", Tipo.NORMAL, 9), new Ataque("Mordida", Tipo.NORMAL, 10));
        }
    }

    public static Pokemon noNivel(int especie, int nivel) {
        Pokemon base = criar(especie);
        return porTipo(base.getNome(), base.getTipo(),
                base.getVidaMaxima() + Math.max(0, nivel - 5) * 4, nivel, base.getAtaques());
    }

    /** Recria tambem evolucoes a partir do tipo e dos dados gravados. */
    public static Pokemon porTipo(String nome, Tipo tipo, int vida, int nivel, Ataque... ataques) {
        if (tipo == null) throw new IllegalArgumentException("Tipo obrigatorio.");
        return switch (tipo) {
            case FOGO -> new PokemonFogo(nome, vida, nivel, ataques);
            case AGUA -> new PokemonAgua(nome, vida, nivel, ataques);
            case PLANTA -> new PokemonPlanta(nome, vida, nivel, ataques);
            case NORMAL -> new PokemonNormal(nome, vida, nivel, ataques);
        };
    }

    public static Pokemon selvagem(Random sorteio) {
        // Selvagens variam entre os niveis 3 e 6.
        int nivel = 3 + sorteio.nextInt(4);
        return noNivel(1 + sorteio.nextInt(5), nivel);
    }
}
