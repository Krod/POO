import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Jogador extends Thread{
	private Socket socketJogador;

	public Jogador(Socket socketJogador) {
		this.socketJogador = socketJogador;
	}
	
	@Override
	public void run() {
		
		try {
			InputStreamReader streamReader = new InputStreamReader(socketJogador.getInputStream());
			BufferedReader reader = new BufferedReader(streamReader);
			
			while(true) {
				System.out.println("Recebendo...");
				System.out.println(reader.readLine());
				//reader.reset();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
