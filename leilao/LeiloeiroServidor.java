import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LeiloeiroServidor extends UnicastRemoteObject implements LeiloeiroService {
    
    private ItemLeilao item;
    private List<ParticipanteInfo> participantes;
    private boolean encerrado = false;

    // Classe auxiliar para armazenar cliente e nome
    private static class ParticipanteInfo {
        String nome;
        ParticipanteCallback callback;
        ParticipanteInfo(String nome, ParticipanteCallback callback) {
            this.nome = nome;
            this.callback = callback;
        }
    }

    public LeiloeiroServidor(String descricaoItem, double lanceMinimo, long duracaoSegundos) throws RemoteException {
        super();
        this.item = new ItemLeilao(descricaoItem, lanceMinimo, duracaoSegundos);
        this.participantes = new ArrayList<>();
        
        // Timer para monitorar o encerramento automático do leilão
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                encerrarLeilao();
                timer.cancel();
            }
        }, duracaoSegundos * 1000);
    }

    @Override
    public synchronized void registrarParticipante(ParticipanteCallback cliente, String nome) throws RemoteException {
        participantes.add(new ParticipanteInfo(nome, cliente));
        System.out.println("[SERVIDOR] Participante conectado: " + nome);
    }

    @Override
    public synchronized boolean darLance(String nomeParticipante, double valor) throws RemoteException {
        if (encerrado || item.estaEncerrado()) {
            System.out.println("[SERVIDOR] Lance de " + nomeParticipante + " recusado: Leilão encerrado.");
            return false;
        }

        // Regra: Deve ser maior que o maior lance atual e maior/igual ao mínimo inicial
        if (valor > item.getMaiorLance()) {
            item.setMaiorLance(valor);
            item.setMaiorLicitante(nomeParticipante);
            System.out.println("[SERVIDOR] Novo maior lance aceito: R$ " + valor + " por " + nomeParticipante);
            
            // Notificar todos os participantes assíncronamente (Push)
            notificarTodosNovoLance(nomeParticipante, valor);
            return true;
        } else {
            System.out.println("[SERVIDOR] Lance de " + nomeParticipante5Format(valor) + " recusado (abaixo do atual).");
            return false;
        }
    }

    @Override
    public synchronized ItemLeilao consultarEstado() throws RemoteException {
        return item;
    }

    private void notificarTodosNovoLance(String licitante, double valor) {
        List<ParticipanteInfo> desconectados = new ArrayList<>();
        for (ParticipanteInfo p : participantes) {
            try {
                p.callback.notificarNovoLance(licitante, valor);
            } catch (RemoteException e) {
                // Se o cliente caiu, marca para remover
                desconectados.add(p);
            }
        }
        participantes.removeAll(desconectados);
    }
    private synchronized void encerrarLeilao() {
        if (encerrado) return;
        encerrado = true;
        System.out.println("\n[SERVIDOR] === LEILÃO ENCERRADO ===");
        System.out.println("Vencedor: " + item.getMaiorLicitante() + " com R$ " + item.getMaiorLance());

        List<ParticipanteInfo> desconectados = new ArrayList<>();
        for (ParticipanteInfo p : participantes) {
            try {
                p.callback.notificarEncerramento(item.getMaiorLicitante(), item.getMaiorLance());
            } catch (RemoteException e) {
                desconectados.add(p);
            }
        }
        participantes.removeAll(desconectados);
    }

    private String nomeParticipante5Format(double valor) {
        return "R$ " + valor;
    }

    public static void main(String[] args) {
        try {
            // Inicializa o registro RMI na porta padrão 1099
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // Cria o servidor (Item: "Notebook Gamer", Lance Mínimo: R$ 1000.00, Duração: n segundos)
            LeiloeiroServidor servidor = new LeiloeiroServidor("Notebook Gamer", 100.00, 6000);
            
            registry.rebind("LeiloeiroService", servidor);
            System.out.println("[SERVIDOR] Leiloeiro iniciado com sucesso. Aguardando participantes...");
        } catch (Exception e) {
            System.err.println("[SERVIDOR] Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}