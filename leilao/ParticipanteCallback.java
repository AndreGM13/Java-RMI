import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ParticipanteCallback extends Remote {
    void notificarNovoLance(String licitante, double valor) throws RemoteException;
    void notificarEncerramento(String vencedor, double valorFinal) throws RemoteException;
}