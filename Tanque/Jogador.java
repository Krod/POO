import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Jogador extends Thread{
	private Socket socketJogador;
	private Tanque t;

	public Jogador(Socket socketJogador) {
		this.socketJogador = socketJogador;
	}
	
	public Tanque getTanque() {
		return t;
	}
	
	@Override
	public void run() {
		Object resposta;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		
		try {
			while(true) {
				ois = new ObjectInputStream(socketJogador.getInputStream());
				t = (Tanque) ois.readObject();
				
				//if(t != null) {
					//System.out.printf("Jogador x: %f, y: %f\n", t.x,t.y);
					
				//}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
