import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LeiloeiroService extends Remote {
    boolean darLance(String nomeParticipante, double valor) throws RemoteException;
    ItemLeilao consultarEstado() throws RemoteException;
    void registrarParticipante(ParticipanteCallback cliente, String nome) throws RemoteException;
}