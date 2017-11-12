import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente extends Thread {
	private Socket socketCliente;
	private String nomeJogador;
	private double x, y;
	
	public Cliente(String ip, int porta, String nomeJogador) throws IOException {
		socketCliente = new Socket(ip, porta); 
		this.nomeJogador = nomeJogador;
	}
	
	@Override
	public void run() {
		try {
			OutputStreamWriter ouw = new OutputStreamWriter(socketCliente.getOutputStream());
			BufferedWriter bfw = new BufferedWriter(ouw);
			
			while(true) {
				if(Arena.tanqueAtivo != null) {
					if(Arena.tanqueAtivo.x != x || Arena.tanqueAtivo.y != y) {
						System.out.println("Enviando...");
						bfw.write(String.format("%.3f|%.3f|%.3f", Arena.tanqueAtivo.x, Arena.tanqueAtivo.y, Arena.tanqueAtivo.angulo));
						bfw.flush();
						x = Arena.tanqueAtivo.x;
						y = Arena.tanqueAtivo.y;
					}
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
