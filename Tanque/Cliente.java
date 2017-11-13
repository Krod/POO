import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Locale;

public class Cliente extends Thread {
	private Socket socketCliente;
	private String nomeJogador;
	private double x, y;
	
	public Cliente(String ip, int porta, String nomeJogador) throws IOException {
		socketCliente = new Socket(ip, porta); 
		this.nomeJogador = nomeJogador;
	}
	
	@Override
	public void run() {
		try {
			OutputStreamWriter ouw = new OutputStreamWriter(socketCliente.getOutputStream());
			BufferedWriter bfw = new BufferedWriter(ouw);
			
			while(true) {
				
				if(Arena.tanqueAtivo != null) {
					//System.out.println("Enviando...");
					if(Arena.tanqueAtivo.x != x || Arena.tanqueAtivo.y != y) {
						String s = String.format(Locale.US, "%.3f;%.3f;%d;%d;%d", Arena.tanqueAtivo.x, Arena.tanqueAtivo.y, (int)Arena.tanqueAtivo.angulo, Arena.tanqueAtivo.cor.getRGB(), Arena.tanqueAtivo.pontosDisparos);
						for(int i = 0; i < 3; i++)
							if(Arena.tanqueAtivo.disparos[i] != null)
								s += String.format(Locale.US, ";%.3f;%.3f;%d", Arena.tanqueAtivo.disparos[i].x, Arena.tanqueAtivo.disparos[i].y, (int)Arena.tanqueAtivo.disparos[i].angulo);
						s += '\n';
						bfw.write(s);
						bfw.flush();
						x = Arena.tanqueAtivo.x;
						y = Arena.tanqueAtivo.y;
					}
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
