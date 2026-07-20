package mx.ipn.esimecu.rpc;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.rmi.ssl.SslRMIClientSocketFactory;

public class ClienteRMI {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        
        // Conexión segura al registro usando la factoría SSL
        Registry registry = LocateRegistry.getRegistry(host, 1099, new SslRMIClientSocketFactory());
        
        Calculadora c = (Calculadora) registry.lookup("CalculadoraIPN");
        System.out.println("Conectado a: " + c.quienSoy());
        System.out.println("3 + 4 = " + c.sumar(3, 4));
        System.out.println("10 - 6 = " + c.restar(10, 6));

        System.out.println("\n--- Consultando Bitácora Remota ---");
        BitacoraRemota b = (BitacoraRemota) registry.lookup("BitacoraIPN");
        
        for(String registro : b.consultarHistorial()) {
            System.out.println(registro);
        }
    }
}
