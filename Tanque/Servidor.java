import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class Servidor extends Thread {
	private ServerSocket socketServidor;
	protected ArrayList<Jogador> jogadores;
	
	public Servidor(int porta) throws IOException{
		socketServidor = new ServerSocket(porta);
		jogadores = new ArrayList<Jogador>();
	}
	
	@Override
	public void run() {
		System.out.println("Servidor rodando na " + getName());
		
		while(true) {
			try {
				Jogador j = new Jogador(socketServidor.accept());
				jogadores.add(j);
				j.start();
				System.out.println("Novo jogador conectou, rodando na " + j.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void enviarDados(Tanque t, Socket j) throws IOException{
		
		OutputStreamWriter ouw = new OutputStreamWriter(j.getOutputStream());
		BufferedWriter bfw = new BufferedWriter(ouw);
		
		if(t != null) {
			//System.out.println("Enviando...");
			String s = String.format(Locale.US, "%d;%.3f;%.3f;%d;%.1f;%d;%d", t.id, t.x, t.y, (int)t.angulo, t.velocidade, t.cor.getRGB(), t.pontosDisparos);
			for(int i = 0; i < 3; i++)
				if(t.disparos[i] != null)
					s += String.format(Locale.US, ";%.3f;%.3f;%d", t.disparos[i].x, t.disparos[i].y, (int)t.disparos[i].angulo);
			s += '\n';
			bfw.write(s);
			bfw.flush();
		}
	}
}
