package mx.ipn.esimecu.rpc;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BitacoraRemotaImpl extends UnicastRemoteObject implements BitacoraRemota {
    
    private static final String DB_URL = "jdbc:sqlite:calculadora.db";

    protected BitacoraRemotaImpl() throws RemoteException {
        // Inicializa el objeto remoto obligándolo a usar SSL
        super(0, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        crearTabla();
    }

    private void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS operaciones (id INTEGER PRIMARY KEY AUTOINCREMENT, operacion TEXT, a REAL, b REAL, resultado REAL)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creando tabla: " + e.getMessage());
        }
    }

    @Override
    public List<String> consultarHistorial() throws RemoteException {
        List<String> historial = new ArrayList<>();
        String sql = "SELECT * FROM operaciones";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                historial.add(rs.getString("operacion") + ": " + rs.getDouble("a") + 
                              ", " + rs.getDouble("b") + " = " + rs.getDouble("resultado"));
            }
        } catch (SQLException e) {
            throw new RemoteException("Error al consultar BD", e);
        }
        return historial;
    }
}
