import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
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
				Jogador jogador = new Jogador(socketServidor.accept());
				jogadores.add(jogador);
				jogador.start();
				System.out.println("Novo jogador conectou, rodando na " + jogador.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void enviarDados() throws IOException{
		
		for(Jogador jogador: this.jogadores) {
			for(Tanque tanque:Arena.tanques)
				if(tanque.getId() != jogador.getTanque().getId())
					if(tanque != null) {
						String s = String.format(Locale.US, "%d;%.3f;%.3f;%d;%.1f;%d;%d;%d", tanque.getId(), tanque.getX(), tanque.getY(), (int)tanque.getAngulo(), tanque.getVelocidade(), tanque.getCor().getRGB(), tanque.getPontosDeDisparos(), tanque.getPontosDeVida());
						for(int i = 0; i < 3; i++)
							if(tanque.getDisparos()[i] != null)
								s += String.format(Locale.US, ";%.3f;%.3f;%d", tanque.getDisparos()[i].x, tanque.getDisparos()[i].y, (int)tanque.getDisparos()[i].angulo);
						s += '\n';
						jogador.getBufferedWriter().write(s);
						jogador.getBufferedWriter().flush();
					}
			
			// Envia as mensagens do Chat para os jogadores
			if(Arena.Chat.size() > 0) {
				String s = "-" + Arena.Chat.size();
				for(String r: Arena.Chat)
					s += ";" + r;
				s += "\n";
				jogador.getBufferedWriter().write(s);
				jogador.getBufferedWriter().flush();
			}
		}
		
	}
}
