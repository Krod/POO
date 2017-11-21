import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Arena extends JComponent implements MouseListener, ActionListener, MouseMotionListener, KeyListener, MouseWheelListener, ComponentListener{
	protected static int largura,altura;
	protected static HashSet<Tanque> tanques, tanquesRemover, tanquesAux;
	private Timer contador;
	private int mouseX;
	protected volatile static Tanque tanqueAtivo;
    private static Cliente cliente;
    private static Servidor servidor;
    private static String comando;
    protected static int id;
    protected static LinkedList<String> Chat;
    protected static LinkedList<String> auxChat;
    private int linha;
    protected static int chatMaxLinhas;
    protected static long temporizadorDoChat;
    private String stringArray[];
	
	public Arena(int l,int a) throws IOException{
		largura = l; 
		altura = a;
		tanques = new HashSet<Tanque>();
		tanquesRemover = new HashSet<>();
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);
		addKeyListener(this);
		setFocusable(true);
		contador = new Timer(10,this);
		contador.start();
		id = 1;
		Chat = new LinkedList<>();
		auxChat = new LinkedList<>();
		chatMaxLinhas = 7;
		stringArray = new String[20];
	}
	public void adicionaTanque(Tanque t){
		tanques.add(t);
	}
	public Dimension getMaximumSize(){
		return getPreferredSize();
	}
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
	public Dimension getPreferredSize(){
		return new Dimension(largura,altura);
	}
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(new Color(245,245,255));
		g2d.fillRect(0,0,largura,altura);
		g2d.setColor(new Color(220,220,220));
		
		// Desenha a grade
		for(int _largura=0;_largura<=largura;_largura+=20)
			g2d.drawLine(_largura,0,_largura,altura);
		for(int _altura=0;_altura<=altura;_altura+=20) 
			g2d.drawLine(0,_altura,largura,_altura);
		
		// Desenhamos todos os tanques
		for(Tanque t:tanques)
			t.draw(g2d);
		
		tanques.removeAll(tanquesRemover);
		
		if(!Arena.auxChat.equals(Arena.Chat))
			Arena.temporizadorDoChat = System.currentTimeMillis() + 10000;
		
		if(System.currentTimeMillis() < temporizadorDoChat) {
			// Desenha o Chat sobre a Arena
			g2d.setColor(new Color(0, 0, 0, 100));
			g2d.fillRoundRect(10, Arena.altura-120, Arena.largura-25, 110, 10, 10);
			
			g2d.setColor(new Color(0, 0, 0));
			g2d.setStroke(new BasicStroke(1f,BasicStroke.CAP_ROUND,
					  BasicStroke.JOIN_ROUND,0,
					  new float[]{8},0));
			g2d.draw(new RoundRectangle2D.Double(9, Arena.altura-121, Arena.largura-24, 111, 10, 10));
			
			//if(tanqueAtivo != null)
				//g2d.setColor(tanqueAtivo.cor);
			
			// Uma lista auxilixar é utilizada para evitar que a thread faça alteração na lista enquanto ela está sendo acessada;
			auxChat.clear();
			auxChat.addAll(Chat);
			
			g2d.setFont(new Font("default", Font.BOLD, 16));
			
			// Imprime o texto do Chat
			linha = Arena.altura;
			for(String s: auxChat)
				if(s != null) {
					stringArray = s.split("\\|");
					g2d.setColor(new Color(Integer.valueOf(stringArray[0])));
					g2d.drawString( stringArray[1], 15, linha-=15);
				}
		}
	}
	public void mouseClicked(MouseEvent e){	
		if(!comando.equals("cliente") && !comando.equals("servidor")) {
			for(Tanque t:tanques) {
				t.setEstaAtivo(false);
				tanqueAtivo = null;
			}
			for(Tanque t:tanques){
				boolean clicado = t.getRectEnvolvente().contains(e.getX(),e.getY());
				if (clicado){
					t.setEstaAtivo(true);
					tanqueAtivo = t;
					/*switch(e.getButton()){
						case MouseEvent.BUTTON1: 
							t.aumentarVelocidade(); 
							break;
						case MouseEvent.BUTTON2: 
							t.aumentarVelocidade();
							break;
						case MouseEvent.BUTTON3: 
							t.girarHorario(3); 
							break;
					}*/
					break;
				}
			}
		}
		repaint();
	}
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void actionPerformed(ActionEvent e){
		if(comando.equals("cliente"))
			tanqueAtivo.mover();
		else
			for(Tanque t:tanques)
				t.mover();
		
		for(Tanque s: tanques)
			for(Tanque q: tanques) {
				if(q != s && q.acertou(s))
					s.subtraiPontoDeVida();
				if(s.getPontosDeVida() == 0)
					tanquesRemover.add(s);
			}
			
		if(comando.equals("cliente")) {
			try {
				cliente.enviarDados();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		} else if(comando.equals("servidor")) {
			try {
				servidor.enviarDados();
			} catch (IOException e3) {
				e3.printStackTrace();
			}
		}
		
		repaint();
	}
	public void mouseMoved(MouseEvent e) {
		if(tanqueAtivo != null)
			if(e.getX() > mouseX)
				tanqueAtivo.girarHorario(2);
			else if(e.getX() < mouseX)
				tanqueAtivo.girarAntiHorario(2);
		mouseX = e.getX();
	}
	public void mouseDragged(MouseEvent e) { }
	
	@Override
	public void keyPressed(KeyEvent e) {

			if(tanqueAtivo != null){
				switch(e.getKeyCode()){
			    case KeyEvent.VK_LEFT:
			    	tanqueAtivo.girarAntiHorario(3);
			    	break;
		        case KeyEvent.VK_UP: 
		        	tanqueAtivo.aumentarVelocidade(); 
		        	break;
		        case KeyEvent.VK_DOWN :
		        	tanqueAtivo.diminuirVelocidade();
		        	break;
				case KeyEvent.VK_RIGHT: 
					tanqueAtivo.girarHorario(3); 
					break;
				case KeyEvent.VK_SPACE: 
					tanqueAtivo.disparar();
					break;
				}
			}
	}
	
	@Override
	public void keyTyped(KeyEvent e) { }
	@Override
	public void keyReleased(KeyEvent e) { }
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(tanqueAtivo != null) {
			if(e.getWheelRotation() < 0)
				tanqueAtivo.aumentarVelocidade();
			else
				tanqueAtivo.diminuirVelocidade();
		}
	}
	
	public static void main(String args[]) throws IOException{
		Arena arena = new Arena(600,480);
	    Random random = new Random();
	    
	    if(args.length > 0) {
	    	tanqueAtivo = new Tanque(100,200,random.nextInt(360), new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
	    	tanqueAtivo.setEstaAtivo(true);
	    	arena.adicionaTanque(tanqueAtivo);
	    	comando = args[0];
		    if(comando.equals("cliente")) {
		    	System.out.println("Iniciando cliente");
		    	cliente = new Cliente("127.0.0.1", 1234, "Rodrigo");
		    	cliente.start();
		    }
		    else if(comando.equals("servidor")) {
		    	System.out.println("Iniciando servidor");
		    	servidor = new Servidor(1234);
		    	servidor.start();
				arena.adicionaTanque(new Tanque(100,200,random.nextInt(360),Color.BLUE));
				arena.adicionaTanque(new Tanque(200,200,random.nextInt(360),Color.RED));
				arena.adicionaTanque(new Tanque(470,360,random.nextInt(360),Color.GREEN));
				arena.adicionaTanque(new Tanque(450,50,random.nextInt(360),Color.YELLOW));
		    }
	    } else {
			arena.adicionaTanque(new Tanque(100,200,random.nextInt(360),Color.BLUE));
			arena.adicionaTanque(new Tanque(200,200,random.nextInt(360),Color.RED));
			arena.adicionaTanque(new Tanque(470,360,random.nextInt(360),Color.GREEN));
			arena.adicionaTanque(new Tanque(450,50,random.nextInt(360),Color.YELLOW));
	    }
	    
		JFrame janela = new JFrame("Tanques");
		JPanel painelArena = new JPanel(new BorderLayout());
		JPanel painelChat = new JPanel(new BorderLayout());
		JTextField caixaDeMensagem = new JTextField();
		JButton botaoEnviar = new JButton("Enviar");
		
		botaoEnviar.addActionListener(new ActionListener() { 
		    public void actionPerformed(ActionEvent e) { 
		    	adicionaMensagemNoChat(caixaDeMensagem.getText());
		        caixaDeMensagem.setText("");
		        arena.requestFocus();
		    } 
		});
		
		caixaDeMensagem.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
		    	adicionaMensagemNoChat(caixaDeMensagem.getText());
		        caixaDeMensagem.setText("");
		        arena.requestFocus();
		    } 
		});
		
		painelChat.add(caixaDeMensagem, BorderLayout.CENTER);
		painelChat.add(botaoEnviar, BorderLayout.EAST);
		painelArena.add(arena, BorderLayout.CENTER);
		painelArena.add(painelChat, BorderLayout.SOUTH);
		janela.getContentPane().add(painelArena);
		janela.pack();
		janela.setVisible(true);
		janela.setDefaultCloseOperation(3);
	}
	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent e) {
		altura = e.getComponent().getHeight();
		largura = e.getComponent().getWidth();
	}
	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public static void adicionaMensagemNoChat(String s) {
		if(Arena.tanqueAtivo != null) {
    		if(Arena.comando.equals("servidor")) {
    			if(!s.isEmpty())
    				Arena.Chat.addFirst(Arena.tanqueAtivo.getCor().getRGB() + "|" + Arena.tanqueAtivo.getNome() + "(Servidor): " + s);
    			
    			if(Arena.Chat.size() > Arena.chatMaxLinhas)
					Arena.Chat.removeLast();
    		}
    		else {
    			if(!s.isEmpty())
    				Arena.tanqueAtivo.setMensagem(s);
    			else
    				Arena.tanqueAtivo.setMensagem(null);
    		}
    	}
	}
}