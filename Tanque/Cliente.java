import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Locale;

public class Cliente extends Thread {
	private Socket socketCliente;
	private String nomeJogador;
	private double x, y, angulo, velocidade;
	private OutputStreamWriter ouw;
	private BufferedWriter bfw;
	private InputStreamReader streamReader;
	private BufferedReader reader;
	private String stringArray[];
	private Tanque t;
	private int cor;
	boolean flag;
	
	
	public Cliente(String ip, int porta, String nomeJogador) throws IOException {
		socketCliente = new Socket(ip, porta); 
		this.nomeJogador = nomeJogador;
		ouw = new OutputStreamWriter(socketCliente.getOutputStream());
		bfw = new BufferedWriter(ouw);
		
		InputStreamReader streamReader = new InputStreamReader(socketCliente.getInputStream());
		BufferedReader reader = new BufferedReader(streamReader);
		stringArray = new String[16];
	}
	
	@Override
	public void run() {
		try {
			InputStreamReader streamReader = new InputStreamReader(socketCliente.getInputStream());
			BufferedReader reader = new BufferedReader(streamReader);
			
			while(true) {
				String s = reader.readLine();
				//System.out.println(s);
				
				if(s != null) {
					t = new Tanque(-200, 0, 0, Color.BLACK);
					stringArray = s.split(";");
					t.id = Integer.valueOf(stringArray[0]);
					t.x = Float.valueOf(stringArray[1]);
					t.y = Float.valueOf(stringArray[2]);
					t.angulo = Float.valueOf(stringArray[3]);
					t.velocidade = Float.valueOf(stringArray[4]);
					cor = Integer.valueOf(stringArray[5]);
					if(t.cor.getRGB() != cor)
						t.cor = new Color(cor);

					t.pontosDisparos = Integer.valueOf(stringArray[6]);
					
					for(int i = 0, j = 7; i < 3; i++) {
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
				
					flag = false;
					for(Tanque q: Arena.tanques)
						if(q.id == t.id && q != Arena.tanqueAtivo) {
							q.copia(t);
							flag = true;
						}
					
					if(flag == false)
						Arena.tanques.add(t);
							
					
				}
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void enviarDados() throws IOException{
			
			if(Arena.tanqueAtivo != null) {
				//System.out.println("Enviando...");
				if(Arena.tanqueAtivo.x != x || Arena.tanqueAtivo.y != y || Arena.tanqueAtivo.angulo != angulo || Arena.tanqueAtivo.velocidade != velocidade || Arena.tanqueAtivo.pontosDisparos < 3) {
					String s = String.format(Locale.US, "%.3f;%.3f;%d;%.1f;%d;%d", Arena.tanqueAtivo.x, Arena.tanqueAtivo.y, (int)Arena.tanqueAtivo.angulo, Arena.tanqueAtivo.velocidade, Arena.tanqueAtivo.cor.getRGB(), Arena.tanqueAtivo.pontosDisparos);
					for(int i = 0; i < 3; i++)
						if(Arena.tanqueAtivo.disparos[i] != null)
							s += String.format(Locale.US, ";%.3f;%.3f;%d", Arena.tanqueAtivo.disparos[i].x, Arena.tanqueAtivo.disparos[i].y, (int)Arena.tanqueAtivo.disparos[i].angulo);
					s += '\n';
					bfw.write(s);
					bfw.flush();
					x = Arena.tanqueAtivo.x;
					y = Arena.tanqueAtivo.y;
					angulo = Arena.tanqueAtivo.angulo;
					velocidade = Arena.tanqueAtivo.velocidade;
				}
			}
	}
}
