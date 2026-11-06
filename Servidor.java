import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Servidor {

    public static class CalculadoraImpl implements Calculadora {

        public int somar(int a, int b) throws RemoteException {
            return a + b;
        }
    }

    public static void main(String[] args) {

        try {
            CalculadoraImpl objeto = new CalculadoraImpl();

            Calculadora stub = (Calculadora)
                UnicastRemoteObject.exportObject(objeto, 0);

            Registry registro = LocateRegistry.createRegistry(1099);

            registro.rebind("Calculadora", stub);

            System.out.println("Servidor RMI iniciado!");
            System.out.println("Aguardando conexoes...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}