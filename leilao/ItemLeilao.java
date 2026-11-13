import java.io.Serializable;

public class ItemLeilao implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String descricao;
    private double lanceMinimo;
    private double maiorLance;
    private String maiorLicitante;
    private long tempoEncerramentoEpoch; // Timestamp em milissegundos

    public ItemLeilao(String descricao, double lanceMinimo, long duracaoSegundos) {
        this.descricao = descricao;
        this.lanceMinimo = lanceMinimo;
        this.maiorLance = lanceMinimo;
        this.maiorLicitante = "Nenhum";
        this.tempoEncerramentoEpoch = System.currentTimeMillis() + (duracaoSegundos * 1000);
    }

    // Getters e Setters
    public String getDescricao() { return descricao; }
    public double getLanceMinimo() { return lanceMinimo; }
    public double getMaiorLance() { return maiorLance; }
    public void setMaiorLance(double maiorLance) { this.maiorLance = maiorLance; }
    public String getMaiorLicitante() { return maiorLicitante; }
    public void setMaiorLicitante(String maiorLicitante) { this.maiorLicitante = maiorLicitante; }
    public long getTempoEncerramentoEpoch() { return tempoEncerramentoEpoch; }
    
    public long getTempoRestanteSegundos() {
        long restante = (tempoEncerramentoEpoch - System.currentTimeMillis()) / 1000;
        return Math.max(restante, 0);
    }
    
    public boolean estaEncerrado() {
        return System.currentTimeMillis() >= tempoEncerramentoEpoch;
    }
}