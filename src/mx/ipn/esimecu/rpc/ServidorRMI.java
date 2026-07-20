package mx.ipn.esimecu.rpc;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

public class ServidorRMI {
    public static void main(String[] args) throws Exception {
        // Factorías para cifrar el tráfico RMI con SSL
        SslRMIClientSocketFactory csf = new SslRMIClientSocketFactory();
        SslRMIServerSocketFactory ssf = new SslRMIServerSocketFactory();

        // El registro ahora arranca con SSL de forma nativa
        Registry registry = LocateRegistry.createRegistry(1099, csf, ssf);
        
        Calculadora servicioCalc = new CalculadoraImpl();
        BitacoraRemota servicioBitacora = new BitacoraRemotaImpl();
        
        // Registramos ambos servicios en el "directorio telefónico"
        registry.rebind("CalculadoraIPN", servicioCalc);
        registry.rebind("BitacoraIPN", servicioBitacora);
        
        System.out.println("Servidor RMI (SSL + SQLite) listo en el puerto 1099...");
    }
}
