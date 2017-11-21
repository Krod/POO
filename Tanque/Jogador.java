import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Jogador extends Thread{
	private Socket socketJogador;
	private Tanque t;
	private String stringArray[];
	private int cor, j;
	private double x, y, angulo, velocidade;
	private OutputStreamWriter ouw;
	private BufferedWriter bfw;
	
	public Jogador(Socket socketJogador) throws IOException {
		this.socketJogador = socketJogador;
		t = new Tanque(-200, 0, 0, Color.BLACK);
		Arena.tanques.add(t);
		stringArray = new String[17];
		ouw = new OutputStreamWriter(this.socketJogador.getOutputStream());
		bfw = new BufferedWriter(ouw);
	}

	public Socket getSocketJogador() {
		return socketJogador;
	}
	
	public Tanque getTanque() {
		return t;
	}
	
	public BufferedWriter getBufferedWriter() {
		return bfw;
	}
	
	@Override
	public void run() {
		
		try {
			InputStreamReader streamReader = new InputStreamReader(socketJogador.getInputStream());
			BufferedReader reader = new BufferedReader(streamReader);
			
			while(true) {
				String s = reader.readLine();
				if(s != null) {
					stringArray = s.split(";");
					j = 0;
					t.setNome(stringArray[j++]);
					t.setX(Float.valueOf(stringArray[j++]));
					t.setY(Float.valueOf(stringArray[j++]));
					t.setAngulo(Float.valueOf(stringArray[j++]));
					t.setVelocidade(Float.valueOf(stringArray[j++]));
					t.setCor(new Color(Integer.valueOf(stringArray[j++])));
					t.setPontosDeDisparos(Integer.valueOf(stringArray[j++]));
					t.setPontosDeVida(Integer.valueOf(stringArray[j++]));
					t.setMensagem(stringArray[j++]);
					
					if(!"null".equals(t.getMensagem())) {
						Arena.Chat.addFirst(t.getCor().getRGB() + "|" + t.getNome() + ": " + t.getMensagem());
						
						if(Arena.Chat.size() > Arena.chatMaxLinhas)
							Arena.Chat.removeLast();
					} 
					
					for(int i = 0; i < 3; i++) {
						if(stringArray.length > j) {
							if(t.getDisparos()[i] == null) {
								t.getDisparos()[i] = new Disparo(Float.valueOf(stringArray[j++]), Float.valueOf(stringArray[j++]), Integer.valueOf(stringArray[j++]));
								t.subtraiPontoDeDisparos();
							}
							else {
								t.getDisparos()[i].x = Float.valueOf(stringArray[j++]);
								t.getDisparos()[i].y = Float.valueOf(stringArray[j++]);
								t.getDisparos()[i].angulo = Integer.valueOf(stringArray[j++]);
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
