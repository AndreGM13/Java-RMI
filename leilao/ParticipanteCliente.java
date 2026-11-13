import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ParticipanteCliente extends UnicastRemoteObject implements ParticipanteCallback {

    private String nome;
    private LeiloeiroService leiloeiro;

    protected ParticipanteCliente(String nome, LeiloeiroService leiloeiro) throws RemoteException {
        super();
        this.nome = nome;
        this.leiloeiro = leiloeiro;
    }

    @Override
    public void notificarNovoLance(String licitante, double valor) throws RemoteException {
        System.out.println("\n[NOTIFICAÇÃO PUSH] Novo maior lance: R$ " + valor + " dado por " + licitante);
        System.out.print(">");
    }

    @Override
    public void notificarEncerramento(String vencedor, double valorFinal) throws RemoteException {
        System.out.println("\n[NOTIFICAÇÃO PUSH] === LEILÃO ENCERRADO ===");
        System.out.println("O vencedor foi " + vencedor + " com o lance de R$ " + valorFinal);
        System.exit(0);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java cliente.ParticipanteCliente <NomeDoParticipante> <IP_Do_Servidor>");
            return;
        }

        String nomeParticipante = args[0];
        String hostServidor = args[1];

        try {
            Registry registry = LocateRegistry.getRegistry(hostServidor, 1099);
            LeiloeiroService leiloeiro = (LeiloeiroService) registry.lookup("LeiloeiroService");

            ParticipanteCliente cliente = new ParticipanteCliente(nomeParticipante, leiloeiro);
            
            // Registra o cliente para receber notificações push
            leiloeiro.registrarParticipante(cliente, nomeParticipante);
            System.out.println("[CLIENTE] Conectado ao leilão com sucesso como: " + nomeParticipante);

            Scanner scanner = new Scanner(System.in);
            boolean executando = true;

            while (executando) {
                System.out.println("\n--- MENU ---");
                System.out.println("1. Consultar Estado do Leilão");
                System.out.println("2. Dar Lance");
                System.out.println("3. Sair");
                System.out.print("Escolha uma opção: ");
                
                int opcao = scanner.nextInt();

                switch (opcao) {
                    case 1:
                        ItemLeilao estado = leiloeiro.consultarEstado();
                        System.out.println("\n--- ESTADO DO LEILÃO ---");
                        System.out.println("Item: " + estado.getDescricao());
                        System.out.println("Lance Mínimo Inicial: R$ " + estado.getLanceMinimo());
                        System.out.println("Maior Lance Atual: R$ " + estado.getMaiorLance());
                        System.out.println("Licitante Atual: " + estado.getMaiorLicitante());
                        System.out.println("Tempo Restante: " + estado.getTempoRestanteSegundos() + " segundos");
                        break;
                    
                    case 2:
                        System.out.print("Digite o valor do seu lance (R$): ");
                        double valor = scanner.nextDouble();
                        boolean aceito = leiloeiro.darLance(nomeParticipante, valor);
                        if (aceito) {
                            System.out.println("[RESPOSTA] Parabéns! Seu lance foi ACEITO.");
                        } else {
                            System.out.println("[RESPOSTA] Lance RECUSADO (abaixo do lance atual ou leilão encerrado).");
                        }
                        break;

                    case 3:
                        executando = false;
                        System.out.println("Saindo...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("[CLIENTE] Erro no cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}