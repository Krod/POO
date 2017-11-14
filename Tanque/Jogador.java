import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Locale;

public class Jogador extends Thread{
	protected Socket socketJogador;
	protected Tanque t;
	private String stringArray[];
	private int cor;
	private double x, y, angulo, velocidade;

	public Jogador(Socket socketJogador) throws IOException {
		this.socketJogador = socketJogador;
		t = new Tanque(-200, 0, 0, Color.BLACK);
		Arena.tanques.add(t);
		stringArray = new String[16];
	}
	
	@Override
	public void run() {
		
		try {
			InputStreamReader streamReader = new InputStreamReader(socketJogador.getInputStream());
			BufferedReader reader = new BufferedReader(streamReader);
			
			while(true) {
				String s = reader.readLine();
				//System.out.println(s);
				if(s != null) {
					stringArray = s.split(";");
					t.x = Float.valueOf(stringArray[0]);
					t.y = Float.valueOf(stringArray[1]);
					t.angulo = Float.valueOf(stringArray[2]);
					t.velocidade = Float.valueOf(stringArray[3]);
					
					cor = Integer.valueOf(stringArray[4]);
					if(t.cor.getRGB() != cor)
						t.cor = new Color(cor);
					
					t.pontosDisparos = Integer.valueOf(stringArray[5]);
					
					for(int i = 0, j = 6; i < 3; i++) {
						if(stringArray.length > j) {
							if(t.disparos[i] == null) {
								t.disparos[i] = new Disparo(Float.valueOf(stringArray[j++]), Float.valueOf(stringArray[j++]), Integer.valueOf(stringArray[j++]));
								t.pontosDisparos--;
							}
							else {
								t.disparos[i].x = Float.valueOf(stringArray[j++]);
								t.disparos[i].y = Float.valueOf(stringArray[j++]);
								t.disparos[i].angulo = Integer.valueOf(stringArray[j++]);
							}
							
						}
					}			
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
