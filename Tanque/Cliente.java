import java.awt.Color;
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
	private int cor, id, j;
	private boolean flag;
	
	
	public Cliente(String ip, int porta, String nomeJogador) throws IOException {
		socketCliente = new Socket(ip, porta); 
		this.nomeJogador = nomeJogador;
		ouw = new OutputStreamWriter(socketCliente.getOutputStream());
		bfw = new BufferedWriter(ouw);
		
		stringArray = new String[16];
		id = 0;
	}
	
	@Override
	public void run() {
		try {
			InputStreamReader streamReader = new InputStreamReader(socketCliente.getInputStream());
			BufferedReader reader = new BufferedReader(streamReader);
			
			while(true) {
				String s = reader.readLine();
				
				if(s != null) {
					stringArray = s.split(";");
					j = 0;
					id = Integer.valueOf(stringArray[j++]);
					if(id > -1) {
						t = new Tanque(-200, 0, 0, Color.BLACK);
						t.setId(id);
						t.setX(Float.valueOf(stringArray[j++]));
						t.setY(Float.valueOf(stringArray[j++]));
						t.setAngulo(Float.valueOf(stringArray[j++]));
						t.setVelocidade(Float.valueOf(stringArray[j++]));
						t.setCor(new Color(Integer.valueOf(stringArray[j++])));
						t.setPontosDeDisparos(Integer.valueOf(stringArray[j++]));
						t.setPontosDeVida(Integer.valueOf(stringArray[j++]));
						
						for(int i = 0; i < 3; i++) {
							if(stringArray.length > j) {
								if(t.getDisparos()[i] == null) {
									t.getDisparos()[i] = new Disparo(Float.valueOf(stringArray[j++]), Float.valueOf(stringArray[j++]), Integer.valueOf(stringArray[j++]));
									t.subtraiPontoDeDisparos();;
								}
								else {
									t.getDisparos()[i].x = Float.valueOf(stringArray[j++]);
									t.getDisparos()[i].y = Float.valueOf(stringArray[j++]);
									t.getDisparos()[i].angulo = Integer.valueOf(stringArray[j++]);
								}
							}
						}
						
						flag = false;
						for(Tanque q: Arena.tanques)
							if(q.getId() == t.getId() && q != Arena.tanqueAtivo) {
								q.copia(t);
								flag = true;
							}
						
						if(flag == false)
							Arena.tanques.add(t);
						
					} else {
						Arena.Chat.clear();
						for(int i = 1; i <= (id * -1); i++)
							Arena.Chat.addLast(stringArray[i]);
					}
					
				}
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void enviarDados() throws IOException{
			
			if(Arena.tanqueAtivo != null) {
				if(Arena.tanqueAtivo.getX() != x || Arena.tanqueAtivo.getY() != y || Arena.tanqueAtivo.getAngulo() != angulo || Arena.tanqueAtivo.getVelocidade() != velocidade || Arena.tanqueAtivo.getPontosDeDisparos() < 3) {
					String s = String.format(Locale.US, "%s;%.3f;%.3f;%d;%.1f;%d;%d;%d;%s", Arena.tanqueAtivo.getNome(), Arena.tanqueAtivo.getX(), Arena.tanqueAtivo.getY(), (int)Arena.tanqueAtivo.getAngulo(), Arena.tanqueAtivo.getVelocidade(), Arena.tanqueAtivo.getCor().getRGB(), Arena.tanqueAtivo.getPontosDeDisparos(), Arena.tanqueAtivo.getPontosDeVida(),Arena.tanqueAtivo.getMensagem());
					for(int i = 0; i < 3; i++)
						if(Arena.tanqueAtivo.getDisparos()[i] != null)
							s += String.format(Locale.US, ";%.3f;%.3f;%d", Arena.tanqueAtivo.getDisparos()[i].x, Arena.tanqueAtivo.getDisparos()[i].y, (int)Arena.tanqueAtivo.getDisparos()[i].angulo);
					s += '\n';

					bfw.write(s);
					bfw.flush();
					x = Arena.tanqueAtivo.getX();
					y = Arena.tanqueAtivo.getY();
					angulo = Arena.tanqueAtivo.getAngulo();
					velocidade = Arena.tanqueAtivo.getVelocidade();
					Arena.tanqueAtivo.setMensagem(null);
				}
			}
	}
}
