

/** Controla os desafios, suas recompensas e o acesso a Liga. */
public class Jornada {
    private final boolean[] treinadoresVencidos = new boolean[2];
    private final boolean[] insignias = new boolean[4];
    private boolean campeao;
    private final Combate batalha;
    private final Leitor leitor;

    private static final String[] TREINADORES = {"Lia", "Rafael"};
    private static final int[] TREINADOR_ESPECIE = {4, 5};
    private static final int[] TREINADOR_NIVEL = {6, 8};
    private static final String[] GINASIOS = {"Aurora", "Brasa", "Bosque", "Oceano"};
    private static final String[] LIDERES = {"Lider Aurora", "Lider Ignis", "Lider Flora", "Lider Marina"};
    private static final String[] INSIGNIAS = {"Aurora", "Chama", "Folha", "Mar"};
    private static final int[] GINASIO_ESPECIE = {2, 1, 3, 2};
    private static final int[] GINASIO_NIVEL = {10, 14, 18, 22};
    private static final String[] LIGA_NOMES = {"Elite Kael", "Elite Iris", "Elite Theo", "Elite Luna", "Campeao Orion"};
    private static final int[] LIGA_ESPECIES = {5, 3, 1, 2, 3};
    private static final int[] LIGA_NIVEIS = {24, 26, 28, 30, 34};

    public Jornada(Combate batalha, Leitor leitor) {
        this.batalha = batalha;
        this.leitor = leitor;
    }

    public void desafiarTreinadores(Treinador treinador) {
        System.out.println("\n=== TREINADORES ===");
        for (int i = 0; i < TREINADORES.length; i++) {
            System.out.printf("%d - %s (Nv.%d) %s%n", i + 1, TREINADORES[i], TREINADOR_NIVEL[i],
                    treinadoresVencidos[i] ? "[VENCIDO]" : "[DISPONIVEL]");
        }
        System.out.println("0 - Voltar");
        int opcao = leitor.opcao(0, TREINADORES.length);
        if (opcao == 0) return;
        int i = opcao - 1;
        if (treinadoresVencidos[i]) {
            System.out.println("Voce ja derrotou este treinador.");
            return;
        }
        if (batalha.iniciar(treinador, FabricaPokemon.noNivel(TREINADOR_ESPECIE[i], TREINADOR_NIVEL[i]), TREINADORES[i])
                == ResultadoBatalha.VITORIA) {
            treinadoresVencidos[i] = true;
            System.out.println("Treinador " + TREINADORES[i] + " derrotado!");
        }
    }

    public void desafiarGinasio(Treinador treinador) {
        System.out.println("\n=== GINASIOS ===");
        for (int i = 0; i < GINASIOS.length; i++) {
            String situacao = insignias[i] ? "[INSIGNIA CONQUISTADA]" : podeEntrarGinasio(i) ? "[DISPONIVEL]" : "[BLOQUEADO]";
            System.out.printf("%d - %s | %s | Pokemon Nv.%d %s%n", i + 1, GINASIOS[i], LIDERES[i], GINASIO_NIVEL[i], situacao);
        }
        System.out.println("0 - Voltar");
        int opcao = leitor.opcao(0, GINASIOS.length);
        if (opcao == 0) return;
        int i = opcao - 1;
        if (insignias[i]) {
            System.out.println("Voce ja conquistou esta insignia!");
        } else if (!podeEntrarGinasio(i)) {
            System.out.println(i == 0 ? "Derrote Lia e Rafael antes do primeiro ginasio." :
                    "Conquiste a insignia do ginasio anterior para liberar este desafio.");
        } else {
            System.out.println("Desafio iniciado contra " + LIDERES[i] + "!");
            ResultadoBatalha resultado = batalha.iniciar(treinador,
                    FabricaPokemon.noNivel(GINASIO_ESPECIE[i], GINASIO_NIVEL[i]), LIDERES[i]);
            if (resultado == ResultadoBatalha.VITORIA) {
                insignias[i] = true;
                System.out.println("*** INSIGNIA " + INSIGNIAS[i].toUpperCase() + " CONQUISTADA! ***");
                treinador.getInventario().adicionarItens(2, 2);
                System.out.println("Premio: +2 Pokebolas e +2 Pocoes!");
            }
        }
    }

    private boolean podeEntrarGinasio(int indice) {
        return indice == 0 ? treinadoresVencidos[0] && treinadoresVencidos[1] : insignias[indice - 1];
    }

    public void desafiarLiga(Treinador treinador) {
        if (campeao) {
            System.out.println("Voce ja e o CAMPEAO DA LIGA POKEMON! Parabens!");
            return;
        }
        if (totalInsignias() < insignias.length) {
            System.out.println("Liga bloqueada! Conquiste as quatro insignias antes de entrar. (" + totalInsignias() + "/4)");
            return;
        }
        System.out.println("\n=== LIGA POKEMON ===");
        System.out.println("Sao 4 membros da Elite e o Campeao, em sequencia.");
        System.out.println("O HP da equipe e mantido entre as partidas. Use pocoes com cuidado!");
        System.out.println("Uma derrota encerra a tentativa. Visite o Centro Pokemon antes de tentar novamente.");
        System.out.println("1 - Entrar na Liga\n0 - Voltar");
        if (leitor.opcao(0, 1) == 0) return;
        for (int i = 0; i < LIGA_NOMES.length; i++) {
            if (!treinador.temPokemonDisponivel()) {
                System.out.println("Sua equipe precisa de recuperacao no Centro Pokemon.");
                return;
            }
            System.out.printf("\n=== DESAFIO %d/5: %s ===%n", i + 1, LIGA_NOMES[i]);
            ResultadoBatalha resultado = batalha.iniciar(treinador,
                    FabricaPokemon.noNivel(LIGA_ESPECIES[i], LIGA_NIVEIS[i]), LIGA_NOMES[i], false);
            if (resultado != ResultadoBatalha.VITORIA) {
                System.out.println("Sua tentativa terminou! A Liga deve ser reiniciada desde o primeiro desafio.");
                return;
            }
            System.out.println("Voce venceu " + LIGA_NOMES[i] + "!");
        }
        campeao = true;
        System.out.println("\n===========================================");
        System.out.println("  PARABENS! VOCE E O NOVO CAMPEAO POKEMON!");
        System.out.println("  JORNADA DOS MESTRES - HISTORIA CONCLUIDA");
        System.out.println("===========================================");
    }

    public boolean[] getTreinadoresVencidos() { return treinadoresVencidos.clone(); }
    public boolean[] getInsignias() { return insignias.clone(); }
    public boolean isCampeao() { return campeao; }

    public void restaurarProgresso(boolean[] treinadores, boolean[] ins, boolean ganhouLiga) {
        if (treinadores == null || treinadores.length != 2 || ins == null || ins.length != 4) {
            throw new IllegalArgumentException("Progresso do jogo invalido.");
        }
        if (ins[0] && (!treinadores[0] || !treinadores[1])) {
            throw new IllegalArgumentException("Progresso do primeiro ginasio inconsistente.");
        }
        for (int i = 1; i < ins.length; i++) {
            if (ins[i] && !ins[i - 1]) throw new IllegalArgumentException("Insignias fora de ordem.");
        }
        if (ganhouLiga && !ins[3]) throw new IllegalArgumentException("Liga sem quatro insignias.");
        System.arraycopy(treinadores, 0, treinadoresVencidos, 0, 2);
        System.arraycopy(ins, 0, insignias, 0, 4);
        campeao = ganhouLiga;
    }

    private int totalInsignias() {
        int total = 0;
        for (boolean insignia : insignias) if (insignia) total++;
        return total;
    }

    public void mostrarProgresso() {
        System.out.println("\n=== PROGRESSO DA JORNADA ===");
        for (int i = 0; i < TREINADORES.length; i++) {
            System.out.println("Treinador " + TREINADORES[i] + ": " + (treinadoresVencidos[i] ? "VENCIDO" : "pendente"));
        }
        for (int i = 0; i < INSIGNIAS.length; i++) {
            System.out.println("Insignia " + INSIGNIAS[i] + ": " + (insignias[i] ? "CONQUISTADA" : "pendente"));
        }
        System.out.println("Total de insignias: " + totalInsignias() + "/4");
        System.out.println("Liga Pokemon: " + (campeao ? "CAMPEAO!" : totalInsignias() == 4 ? "LIBERADA" : "bloqueada"));
    }
}
