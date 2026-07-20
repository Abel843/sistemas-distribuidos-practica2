package mx.ipn.esimecu.rpc;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.sql.*;

public class CalculadoraImpl extends UnicastRemoteObject implements Calculadora {

    private static final long serialVersionUID = 1L;
    private static final String DB_URL = "jdbc:sqlite:calculadora.db";

    protected CalculadoraImpl() throws RemoteException { 
        // Inyectamos las fábricas de sockets SSL para cifrar la conexión
        super(0, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory()); 
    }

    private void guardarBitacora(String op, double a, double b, double res) {
        String sql = "INSERT INTO operaciones(operacion, a, b, resultado) VALUES(?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, op);
            pstmt.setDouble(2, a);
            pstmt.setDouble(3, b);
            pstmt.setDouble(4, res);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error guardando en bitácora: " + e.getMessage());
        }
    }

    @Override public double sumar(double a, double b)       { double r = a + b; guardarBitacora("SUMA", a, b, r); return r; }
    @Override public double restar(double a, double b)      { double r = a - b; guardarBitacora("RESTA", a, b, r); return r; }
    @Override public double multiplicar(double a, double b) { double r = a * b; guardarBitacora("MULTIPLICA", a, b, r); return r; }

    @Override
    public double dividir(double a, double b) throws RemoteException {
        if (b == 0.0) throw new RemoteException("División entre cero");
        double r = a / b;
        guardarBitacora("DIVIDE", a, b, r);
        return r;
    }

    @Override
    public String quienSoy() throws RemoteException {
        try { return "Calculadora segura RMI en " + InetAddress.getLocalHost(); } 
        catch (Exception e) { throw new RemoteException("Error host", e); }
    }
}
