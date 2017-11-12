import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;

public class Cliente extends Thread {
	private Socket socketCliente;
	private String nomeJogador;
	private Tanque t;
	
	public Cliente(String ip, int porta, String nomeJogador) throws IOException {
		socketCliente = new Socket(ip, porta); 
		this.nomeJogador = nomeJogador;
	}
	
	public void setTanque(Tanque t) {
		this.t = t;
	}
	
	@Override
	public void run() {
		try {
			//PrintWriter escritor = new PrintWriter(socketCliente.getOutputStream());
			//System.out.println("Enviando...");
			//escritor.write(nomeJogador);
			//escritor.close();
			
			while(true) {
				ObjectOutputStream oos = new ObjectOutputStream(socketCliente.getOutputStream());
				oos.writeObject(t);
			}
			//oos.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
