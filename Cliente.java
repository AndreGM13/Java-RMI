import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente {

    public static void main(String[] args) {

        try {
            Registry registro = LocateRegistry.getRegistry(
                "localhost", 1099
            );

            Calculadora calculadora =
                (Calculadora) registro.lookup("Calculadora");

            int resultado = calculadora.somar(20, 47);

            System.out.println("Resultado: " + resultado);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}