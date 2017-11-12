import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Servidor extends Thread {
	private ServerSocket socketServidor;
	protected ArrayList<Jogador> jogadores;
	
	public Servidor(int porta) throws IOException{
		socketServidor = new ServerSocket(porta);
		jogadores = new ArrayList<Jogador>();
	}
	
	@Override
	public void run() {
		System.out.println("Executando " + getName());
		try {
			Jogador j = new Jogador(socketServidor.accept());
			jogadores.add(j);
			j.start();
			System.out.println("Executando " + j.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
