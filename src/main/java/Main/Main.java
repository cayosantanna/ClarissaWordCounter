package Main; 
public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== CONTADOR DE PALAVRAS - BENCHMARK ===");
            String caminho = "src/main/java/arquivo/Clarissa_Harlowe.txt";
            String texto = LeitorArquivo.ler(caminho); 
            Benchmark.executarTestes(texto);
            
        } catch (Exception e) {
            System.err.println("Erro durante a execucao: " + e.getMessage());
        }
    }
}


