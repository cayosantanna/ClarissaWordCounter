package Main; 
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;


public class Benchmark {
 
    private static final int EXECUCOES_TOTAIS = 32;
    private static final int[] THREADS_TESTES = {2, 4, 8};
    
    private static final Set<String> CONJUNTO1 = new HashSet<>(
            Arrays.asList("clarissa", "letter", "lovelace", "virtue", "dear", "miss"));
    
    private static final Set<String> CONJUNTO2 = new HashSet<>(
            Arrays.asList("eita", "bacana", "vixe", "forbidden", "indignation", "oppression"));
    
    private static final String DIRETORIO_RESULTADOS = "resultados/";
    
    /**
     * 01
     * @param texto O texto completo a ser analisado
     */
    public static void executarTestes(String texto) throws Exception {
        criarDiretorioResultados(); //00
        
        System.out.println("\n========== INICIANDO TESTES ==========");
        System.out.println("\n=== TESTES COM CONJUNTO 1 ===");
        //02
        ExecutarTestesConjunto(texto, CONJUNTO1, "conjunto1");
        System.out.println("\n=== TESTES COM CONJUNTO 2 ===");
        //02
        ExecutarTestesConjunto(texto, CONJUNTO2, "conjunto2");
        
        System.out.println("\n========== TESTES FINALIZADOS ==========");
        System.out.println("Os resultados detalhados foram salvos na pasta '" + DIRETORIO_RESULTADOS + "'");
    }
    
    /**
     * 02
     * @param texto O texto a ser analisado
     * @param conjunto O conjunto de palavras a serem contadas
     * @param nomeConjunto Nome do conjunto para identificação nos arquivos
     */
    private static void ExecutarTestesConjunto(String texto, Set<String> conjunto, String nomeConjunto) throws Exception {
        String[] palavrasPreProcessadas = ProcessadorTexto.preprocessarTexto(texto);

        System.out.println("Executando versao sequencial...");
        
        //03
        List<Long> temposSequencial = testarSequencial(palavrasPreProcessadas, conjunto);

        
        Map<String, Integer> resultadoSequencial = ContadorSequencial.contarPalavrasPreProcessadas(palavrasPreProcessadas, conjunto);// metodo classe sequencial
        mostrarResultadoContagem(resultadoSequencial);//04

        List<Long> temposSemWarmup = ProcessadorEstatistico.removerWarmUp(temposSequencial);

        List<Long> temposSemOutliers = ProcessadorEstatistico.removerOutliers(temposSemWarmup);
        
        double mediaSequencial = ProcessadorEstatistico.calcularMedia(temposSemOutliers);
        double desvioSequencial = ProcessadorEstatistico.calcularDesvioPadrao(temposSemOutliers); 
        
        
        salvarResultados(DIRETORIO_RESULTADOS + "sequencial_" + nomeConjunto + ".txt", 
                temposSequencial, temposSemWarmup, temposSemOutliers, mediaSequencial, desvioSequencial);

        Map<Integer, ResultadoTeste> resultadosParalelos = new HashMap<>();
        

        for (int numThreads : THREADS_TESTES) {
            System.out.println("Executando versao paralela com " + numThreads + " threads...");

            //05
            List<Long> temposParalelo = testarParalelo(palavrasPreProcessadas, conjunto, numThreads);

            List<Long> temposParaleloSemWarmup = ProcessadorEstatistico.removerWarmUp(temposParalelo);
            List<Long> temposParaleloSemOutliers = ProcessadorEstatistico.removerOutliers(temposParaleloSemWarmup);
            double mediaParalelo = ProcessadorEstatistico.calcularMedia(temposParaleloSemOutliers);
            double desvioParalelo = ProcessadorEstatistico.calcularDesvioPadrao(temposParaleloSemOutliers);
            
            salvarResultados(DIRETORIO_RESULTADOS + "paralelo_" + numThreads + "t_" + nomeConjunto + ".txt", 
                    temposParalelo, temposParaleloSemWarmup, temposParaleloSemOutliers, 
                    mediaParalelo, desvioParalelo);

            //06 - Armazena o resultado para apresentação posterior
            resultadosParalelos.put(numThreads, new ResultadoTeste(mediaParalelo, desvioParalelo)); 
        }

        //07 tabela comparativa com todos os resultados
        exibirResultados(nomeConjunto, new ResultadoTeste(mediaSequencial, desvioSequencial), resultadosParalelos);
    }
    
    /**
     * 03
     * @param palavrasPreProcessadas Array de palavras já pré-processadas
     * @param conjunto Conjunto de palavras a serem contadas
     * @return Lista com os tempos de cada execução
     */
    private static List<Long> testarSequencial(String[] palavrasPreProcessadas, Set<String> conjunto) {
        List<Long> tempos = new ArrayList<>();
        
        for (int i = 0; i < EXECUCOES_TOTAIS; i++) {

            long inicio = System.nanoTime();

            ContadorSequencial.contarPalavrasPreProcessadas(palavrasPreProcessadas, conjunto);

            long fim = System.nanoTime();
            
            long tempoDecorrido = (fim - inicio) / 1_000;
            tempos.add(tempoDecorrido);
        }
        
        return tempos;
    }

    /**
     * 05
     * @param palavrasPreProcessadas Array de palavras já pré-processadas
     * @param conjunto Conjunto de palavras a serem contadas
     * @param numThreads Número de threads a serem usadas
     * @return Lista com os tempos de cada execução
     */
    private static List<Long> testarParalelo(String[] palavrasPreProcessadas, Set<String> conjunto, int numThreads) {
        List<Long> tempos = new ArrayList<>();

        for (int i = 0; i < EXECUCOES_TOTAIS; i++) {
            ExecutorService executor = null; 
            try {
                executor = ContadorParalelo.iniciarProcesso(numThreads);
                long inicio = System.nanoTime();
                
                ContadorParalelo.contarPalavrasPreProcessadas(palavrasPreProcessadas, conjunto, executor, numThreads);
                
                long fim = System.nanoTime();

                // duração em microssegundos
                tempos.add((fim - inicio) / 1_000);
                
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {

                if (executor != null) {
                    try {
                        ContadorParalelo.encerrarProcesso(executor);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return tempos;
    }
    
    /**
     * 04
     * Exibe a contagem de palavras no console
     * @param resultado Mapa com a contagem de cada palavra
     */
    private static void mostrarResultadoContagem(Map<String, Integer> resultado) {

        List<String> palavras = new ArrayList<>(resultado.keySet());
        Collections.sort(palavras);
        
        for (String palavra : palavras) {
            System.out.printf("Palavras: %-10s %d%n", palavra, resultado.get(palavra));
        }
    }
    
    /*
     * 07
     * @param nomeConjunto Nome do conjunto de palavras
     * @param resultadoSequencial Resultado do teste sequencial
     * @param resultadosParalelos Resultados dos testes paralelos
     */
    private static void exibirResultados(String nomeConjunto, ResultadoTeste resultadoSequencial, 
            Map<Integer, ResultadoTeste> resultadosParalelos) {
        

        String conjuntoPalavras = (nomeConjunto.equals("conjunto1")) 
                ? "clarissa, letter, lovelace, virtue, dear, miss"
                : "eita, bacana, vixe, forbidden, indignation, oppression";
        
        System.out.println("\n====================================== RESULTADOS DE PERFORMANCE =======================================");
        System.out.printf("CONJUNTO DE PALAVRAS %s: %s%n", nomeConjunto.charAt(nomeConjunto.length()-1), conjuntoPalavras); 
        System.out.println("--------------------------------------------------------------------------------------------------");
        
        System.out.printf("| %-12s | %-20s | %-20s |%n", "EXECUCAO", "TEMPO MEDIO (us)", "DESVIO PADRAO (us)");
        System.out.println("|--------------|----------------------|----------------------|---------------|----------------|");
        
        System.out.printf("| %-12s | %-20.2f | %-20.2f |%n", 
                "SEQUENCIAL", resultadoSequencial.getMedia(), resultadoSequencial.getDesvioPadrao());
       
        
        System.out.println("|--------------|----------------------|----------------------|---------------|----------------|");
        
        System.out.printf("| %-12s | %-20s | %-20s | %-13s | %-16s |%n", 
                "PARALELO", "TEMPO MEDIO (us)", "DESVIO PADRAO (us)", "SPEEDUP", "EFICIENCIA (%)");
        System.out.println("|--------------|----------------------|----------------------|---------------|----------------|");
        
        for (int numThreads : THREADS_TESTES) {
            ResultadoTeste resultadoParalelo = resultadosParalelos.get(numThreads);
            
            double speedup = ProcessadorEstatistico.calcularSpeedup(
                    resultadoSequencial.getMedia(), resultadoParalelo.getMedia());
            
            double eficiencia = ProcessadorEstatistico.calcularEficiencia(speedup, numThreads);
            
            System.out.printf("| %-2d %-9s | %-20.2f | %-20.2f | %-11.2f x | %-16.2f |%n",
                    numThreads, "Threads", resultadoParalelo.getMedia(), resultadoParalelo.getDesvioPadrao(), 
                    speedup, eficiencia);
        }
        
 
        System.out.println("--------------------------------------------------------------------------------------------------");
    }
    
    /**
     * 
     * @param arquivoSaida Nome do arquivo a ser criado
     * @param temposTotais Lista com todos os tempos medidos
     * @param temposSemWarmup Lista após remoção do warm-up
     * @param temposSemOutliers Lista após remoção dos outliers
     * @param media Média calculada
     * @param desvioPadrao Desvio padrão calculado
     */
    private static void salvarResultados(String arquivoSaida, 
            List<Long> temposTotais, 
            List<Long> temposSemWarmup,
            List<Long> temposSemOutliers,
            double media, 
            double desvioPadrao) throws IOException {
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(arquivoSaida))) {
            pw.println("# Resultados do Benchmark");
            pw.println("# Tempos em microsegundos (us)");
            pw.println();
            
            pw.println("## Tempos brutos totais (incluindo warm-up)");
            for (Long tempo : temposTotais) {
                pw.println(tempo);
            }
            
            pw.println("\n## Tempos sem warm-up");
            for (Long tempo : temposSemWarmup) {
                pw.println(tempo);
            }
            
            pw.println("\n## Tempos sem outliers");
            for (Long tempo : temposSemOutliers) {
                pw.println(tempo);
            }
            
            pw.println("\n## Estatisticas");
            pw.printf("Média: %.2f us%n", media);
            pw.printf("Desvio padrão: %.2f us%n", desvioPadrao);
        }
    }
    
    //00
    private static void criarDiretorioResultados() {
        File diretorio = new File(DIRETORIO_RESULTADOS);
        if (!diretorio.exists()) {
            diretorio.mkdir();
        }
    }
    
    /**
     * 06
     * armazena os principais resultados estatisticos de um teste
     */
    private static class ResultadoTeste {
        private final double media;       
        private final double desvioPadrao;
        
        public ResultadoTeste(double media, double desvioPadrao) {
            this.media = media;
            this.desvioPadrao = desvioPadrao;
        }
        
        public double getMedia() {
            return media;
        }
        
        public double getDesvioPadrao() {
            return desvioPadrao;
        }
    }
}
