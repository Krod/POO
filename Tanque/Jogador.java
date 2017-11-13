import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Jogador extends Thread{
	private Socket socketJogador;
	protected Tanque t;
	private String stringArray[];

	public Jogador(Socket socketJogador) {
		this.socketJogador = socketJogador;
		t = new Tanque(0, 0, 0, Color.BLACK);
		Arena.tanques.add(t);
		stringArray = new String[5];
	}
	
	@Override
	public void run() {
		
		try {
			InputStreamReader streamReader = new InputStreamReader(socketJogador.getInputStream());
			BufferedReader reader = new BufferedReader(streamReader);
			
			while(true) {
				//System.out.println("Recebendo...");
				String s = reader.readLine();
				if(s != null) {
					//System.out.println(s);
					stringArray = s.split(";");
					t.x = Float.valueOf(stringArray[0]);
					t.y = Float.valueOf(stringArray[1]);
					t.angulo = Float.valueOf(stringArray[2]);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
