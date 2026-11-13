import java.util.Scanner;

public class Leitor {
    private final Scanner entrada;

    public Leitor(Scanner entrada) { this.entrada = entrada; }

    public String lerNome() {
        System.out.print("Seu nome de treinador: ");
        String nome = entrada.nextLine().trim();
        return nome.isEmpty() ? "Treinador" : nome;
    }

    public int opcao(int minimo, int maximo) {
        while (true) {
            System.out.print("> ");
            String resposta = entrada.nextLine().trim();
            try {
                int valor = Integer.parseInt(resposta);
                if (valor >= minimo && valor <= maximo) return valor;
            } catch (NumberFormatException ignored) { }
            System.out.println("Opcao invalida. Escolha de " + minimo + " a " + maximo + ".");
        }
    }
}
