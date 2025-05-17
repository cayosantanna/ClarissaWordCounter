package Main; 
import java.util.*;
import org.apache.commons.math3.stat.descriptive.rank.Percentile; 
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation; // Adicionar importação

public class ProcessadorEstatistico {
    public static final int EXECUCOES_WARMUP = 2;
    
    /**
     * @param tempos Lista com todos os tempos medidos
     * @return Lista sem as execuções de warm-up
     */
    public static List<Long> removerWarmUp(List<Long> tempos) {
        // lista for muito pequena, então uma copia da lista original
        if (tempos.size() <= EXECUCOES_WARMUP) {
            return new ArrayList<>(tempos);
        }
        
        // Retorna subconjunto da lista a partir do indice apos o warm-up
        return new ArrayList<>(tempos.subList(EXECUCOES_WARMUP, tempos.size()));
    }
    
    /**
     * Usa o método Intervalo Interquartil utilizando a biblioteca Apache Commons Math
     * @param tempos Lista de tempos já sem o warm-up
     * @return Lista filtrada sem os outliers
     */
    public static List<Long> removerOutliers(List<Long> tempos) {
        // lista for muito pequena, retorna como esta
        if (tempos.size() < 4) {
            return new ArrayList<>(tempos);
        }

        // coversão de Long para array de double é exigido pela API do Commons Math
        double[] temposArray = tempos.stream().mapToDouble(Long::doubleValue).toArray();

        Percentile percentile = new Percentile();
        double q1 = percentile.evaluate(temposArray, 25); 
        double q3 = percentile.evaluate(temposArray, 75); 

        double intervaloquartil = q3 - q1;

        // fórmula padrão:
        double limiteInferior = q1 - (1.5 * intervaloquartil);
        double limiteSuperior = q3 + (1.5 * intervaloquartil);

        
        List<Long> temposSemOutliers = new ArrayList<>();
        for (Long tempo : tempos) {
            if (tempo >= limiteInferior && tempo <= limiteSuperior) {
                temposSemOutliers.add(tempo);
            }
        }

        return temposSemOutliers;
    }
    
    
    /**
     * @param tempos Lista de tempos já limpa (sem warm-up e outliers)
     * @return Média dos tempos
     */
    public static double calcularMedia(List<Long> tempos) {
        if (tempos.isEmpty()) return 0;
        
        double soma = 0;
        for (Long tempo : tempos) {
            soma += tempo;
        }
        
        return soma / tempos.size();
    }
    
    /**
     * Calcula o desvio padrão: dispersão dos valores em relação à média
     * Um desvio baixo indica que os tempos são consistentes
     * @param tempos Lista de tempos já limpa
     * @return Desvio padrão dos tempos
     */
    public static double calcularDesvioPadrao(List<Long> tempos) {
        // lista = menos de 2 elementos, desvio = zero
        if (tempos.size() <= 1) return 0;

        // Conversão de Long para array de double
        double[] temposArray = tempos.stream().mapToDouble(Long::doubleValue).toArray();

        // Usar a classe StandardDeviation da biblioteca Apache Commons Math
        StandardDeviation standardDeviation = new StandardDeviation();
        return standardDeviation.evaluate(temposArray);
    }

    
    /**
     * @param tempoSequencial Tempo médio da versão sequencial
     * @param tempoParalelo Tempo médio da versão paralela
     */
    public static double calcularSpeedup(double tempoSequencial, double tempoParalelo) {
        if (tempoParalelo <= 0) return 0;
        
        return tempoSequencial / tempoParalelo;
    }
    
    /**
     * Speedup por thread
     * @param speedup O speedup calculado
     * @param numThreads Número de threads utilizadas
     */
    public static double calcularEficiencia(double speedup, int numThreads) {
        if (numThreads <= 0) return 0;
        
        return (speedup / numThreads) * 100.0;
    }
}
