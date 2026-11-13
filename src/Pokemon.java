public abstract class Pokemon {
    private String nome;
    private final Tipo tipo;
    private int vidaMaxima;
    private int vidaAtual;
    private Ataque[] ataques;
    private int nivel;
    private int experiencia;

    protected Pokemon(String nome, Tipo tipo, int vidaMaxima, Ataque... ataques) {
        this(nome, tipo, vidaMaxima, 5, ataques);
    }

    protected Pokemon(String nome, Tipo tipo, int vidaMaxima, int nivel, Ataque... ataques) {
        if (vidaMaxima <= 0 || nivel < 1 || ataques == null || ataques.length == 0) {
            throw new IllegalArgumentException("Pokemon precisa de vida, nivel e ataques validos.");
        }
        this.nome = nome;
        this.tipo = tipo;
        this.vidaMaxima = vidaMaxima;
        this.vidaAtual = vidaMaxima;
        this.nivel = nivel;
        this.experiencia = 0;
        this.ataques = ataques.clone();
    }

    /** Implementacao especifica de cada subtipo (polimorfismo). */
    public abstract String getEspecialidade();

    public String getNome() { return nome; }
    public Tipo getTipo() { return tipo; }
    public int getVidaAtual() { return vidaAtual; }
    public int getVidaMaxima() { return vidaMaxima; }
    public int getNivel() { return nivel; }
    public int getExperiencia() { return experiencia; }
    public int getExperienciaParaProximoNivel() { return 25 + nivel * 5; }
    public Ataque[] getAtaques() { return ataques.clone(); }
    public boolean estaDerrotado() { return vidaAtual == 0; }

    // Reconstrucao fiel de um Pokemon salvo, incluindo seu ataque evoluido.
    public static Pokemon restaurar(String nome, Tipo tipo, int hpMaximo, int hpAtual,
                                   int nivel, int xp, Ataque[] ataques) {
        if (nome == null || nome.isBlank() || hpMaximo <= 0 || hpAtual < 0 || hpAtual > hpMaximo ||
                nivel < 1 || nivel > 100 || xp < 0 || xp >= 25 + nivel * 5) {
            throw new IllegalArgumentException("Dados de Pokemon invalidos no save.");
        }
        Pokemon pokemon = FabricaPokemon.porTipo(nome, tipo, hpMaximo, nivel, ataques);
        pokemon.vidaAtual = hpAtual;
        pokemon.experiencia = xp;
        return pokemon;
    }

    /** Recebe dano previamente calculado por CalculadoraDano. */
    public int receberDano(int dano) {
        if (dano < 0) throw new IllegalArgumentException("Dano negativo.");
        int danoReal = Math.min(vidaAtual, dano);
        vidaAtual -= danoReal;
        return danoReal;
    }

    public int curar(int quantidade) {
        if (quantidade <= 0) return 0;
        int recuperado = Math.min(quantidade, vidaMaxima - vidaAtual);
        vidaAtual += recuperado;
        return recuperado;
    }

    // Centro Pokemon: restaura inclusive Pokemon desmaiados.
    public void restaurarTotalmente() {
        vidaAtual = vidaMaxima;
    }

    public void ganharExperiencia(int pontos) {
        if (pontos <= 0 || estaDerrotado()) return;
        experiencia += pontos;
        System.out.printf("%s ganhou %d pontos de experiencia!%n", nome, pontos);
        while (experiencia >= getExperienciaParaProximoNivel()) {
            experiencia -= getExperienciaParaProximoNivel();
            subirNivel();
        }
        System.out.printf("Proximo nivel: %d/%d XP%n", experiencia, getExperienciaParaProximoNivel());
    }

    private void subirNivel() {
        nivel++;
        vidaMaxima += 4;
        vidaAtual += 4;
        System.out.printf("*** %s subiu para o nivel %d! HP maximo: %d ***%n", nome, nivel, vidaMaxima);
        verificarEvolucao();
    }

    private void verificarEvolucao() {
        String novoNome = null;
        Ataque especial = null;
        if (nome.equals("Charmander") && nivel >= 16) {
            novoNome = "Charmeleon";
            especial = new Ataque("Lanca-Chamas", Tipo.FOGO, 15);
        } else if (nome.equals("Charmeleon") && nivel >= 36) {
            novoNome = "Charizard";
            especial = new Ataque("Explosao de Fogo", Tipo.FOGO, 20);
        } else if (nome.equals("Squirtle") && nivel >= 16) {
            novoNome = "Wartortle";
            especial = new Ataque("Rajada d'Agua", Tipo.AGUA, 15);
        } else if (nome.equals("Wartortle") && nivel >= 36) {
            novoNome = "Blastoise";
            especial = new Ataque("Hidro Bomba", Tipo.AGUA, 20);
        } else if (nome.equals("Bulbasaur") && nivel >= 16) {
            novoNome = "Ivysaur";
            especial = new Ataque("Folha Navalha", Tipo.PLANTA, 15);
        } else if (nome.equals("Ivysaur") && nivel >= 32) {
            novoNome = "Venusaur";
            especial = new Ataque("Raio Solar", Tipo.PLANTA, 20);
        }
        if (novoNome != null) {
            String anterior = nome;
            nome = novoNome;
            ataques[ataques.length - 1] = especial;
            vidaMaxima += 10;
            vidaAtual += 10;
            System.out.printf("*** EVOLUCAO! %s evoluiu para %s! ***%n", anterior, nome);
            System.out.printf("Novo ataque: %s | +10 HP maximo!%n", especial.getNome());
        }
    }
}
