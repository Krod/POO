import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

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
			bfw.write("Teste");
			
			while(true) {
				
				if(Arena.tanqueAtivo != null) {
					//System.out.println("Enviando...");
					if(Arena.tanqueAtivo.x != x || Arena.tanqueAtivo.y != y) {
						bfw.write(String.format("%.3f;%.3f;%d;%.1f;%d\n", Arena.tanqueAtivo.x, Arena.tanqueAtivo.y, (int)Arena.tanqueAtivo.angulo, Arena.tanqueAtivo.velocidade, Arena.tanqueAtivo.cor.getRGB()));
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
