import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ToolPanel extends JPanel implements MouseListener, ComponentListener, ActionListener{
	MainPanel mp;
	JPanel menus, tools, gap;
	JButton importer, exporter;
	JLabel logo, handPic, scissorPic, polyPic, shapePic, textPic, drawPic, selected, hovered;
	JFileChooser fc;
	
	public ToolPanel(MainPanel m){
		mp=m;
		setLayout( new FlowLayout(FlowLayout.LEFT,0,0));
		int width = mp.getPreferredSize().width;
		setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.DARK_GRAY));
		
		menus = new JPanel(new FlowLayout( FlowLayout.LEFT,5,5 ));
		menus.setPreferredSize(new Dimension(500,60));
		menus.setOpaque(false);
		gap = new JPanel();
		gap.setPreferredSize(new Dimension(width-1250, 60));
		gap.setOpaque(false);
		tools = new JPanel(new FlowLayout( FlowLayout.RIGHT,0,5 ));
		tools.setPreferredSize(new Dimension(700,60));
		tools.setOpaque(false);
		
		try {
			setIcons();
		} catch (IOException e) {e.printStackTrace();}
		
		add(menus);
		add(gap);
		add(tools);

		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("Images",
				"jpg", "jpeg", "png"));
		fc.setAcceptAllFileFilterUsed(false);
		fc.setMultiSelectionEnabled(true);
				
		addMouseListener(this);
		addComponentListener(this);
		importer.addActionListener(this);
		fc.addActionListener(this);
	}
	public Dimension getPreferredSize(){
		int width = mp.getPreferredSize().width;
		return new Dimension(width,60);
	}
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		setBackground(Color.LIGHT_GRAY);
		
		int offset = tools.getX();
		g.setColor(Color.WHITE);
		g.fillRect(selected.getX()+offset, 0,
				selected.getWidth(), 60);
		if(hovered!=null){
			g.setColor(Color.DARK_GRAY);
			g.drawRect(hovered.getX()+offset, 4,
				hovered.getWidth(), 51);
		}
	}
	public void setIcons() throws IOException{
		BufferedImage myPicture = ImageIO.read(new File("resources/hand.png"));
		handPic = new JLabel(new ImageIcon(myPicture));
		handPic.setPreferredSize(new Dimension(100,40));
		handPic.addMouseListener(this);
		tools.add(handPic);
		selected=handPic;
		
		myPicture = ImageIO.read(new File("resources/scissors.png"));
		scissorPic = new JLabel(new ImageIcon(myPicture));
		scissorPic.setPreferredSize(new Dimension(100,40));
		scissorPic.addMouseListener(this);
		tools.add(scissorPic);
		
		myPicture = ImageIO.read(new File("resources/node.png"));
		polyPic = new JLabel(new ImageIcon(myPicture));
		polyPic.setPreferredSize(new Dimension(100,30));
		polyPic.addMouseListener(this);
		tools.add(polyPic);
		
		myPicture = ImageIO.read(new File("resources/penta.png"));
		shapePic = new JLabel(new ImageIcon(myPicture));
		shapePic.setPreferredSize(new Dimension(100,40));
		shapePic.addMouseListener(this);
		tools.add(shapePic);
		
		myPicture = ImageIO.read(new File("resources/text.png"));
		textPic = new JLabel(new ImageIcon(myPicture));
		textPic.setPreferredSize(new Dimension(100,40));
		textPic.addMouseListener(this);
		tools.add(textPic);
		
		myPicture = ImageIO.read(new File("resources/pencil.png"));
		drawPic = new JLabel(new ImageIcon(myPicture));
		drawPic.setPreferredSize(new Dimension(100,40));
		drawPic.addMouseListener(this);
		tools.add(drawPic);
		
		myPicture = ImageIO.read(new File("resources/logo.png"));
		logo = new JLabel(new ImageIcon(myPicture));
		logo.setPreferredSize(new Dimension(200,50));
		//menus.add(logo);
		
		importer = new JButton("Import image");
		importer.setFont(new Font("Arial", Font.BOLD, 20));
		importer.setFocusPainted(false);
		menus.add(importer);
		
		exporter = new JButton("Export");
		exporter.setFont(new Font("Arial", Font.BOLD, 20));
		exporter.setFocusPainted(false);
		menus.add(exporter);		
	}
	
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {
		Object o = e.getSource();
		if (o instanceof JLabel){
			hovered=(JLabel) o;
		}
		repaint();
	}
	public void mouseExited(MouseEvent e) {
		hovered=null;
		repaint();
	}
	public void mousePressed(MouseEvent e) {
		mp.requestFocusInWindow();
		Object o = e.getSource();
		if (o instanceof JLabel){
			selected=(JLabel) o;
			if(o.equals(handPic)) mp.setTool(1);
			if(o.equals(scissorPic)) mp.setTool(2);
			if(o.equals(polyPic)) mp.setTool(3);
			if(o.equals(shapePic)) mp.setTool(4);
			if(o.equals(textPic)) mp.setTool(5);
			if(o.equals(drawPic)) mp.setTool(6);
		}
		repaint();
	}
	public void mouseReleased(MouseEvent e) {}
	
	public void componentHidden(ComponentEvent arg0) {}
	public void componentMoved(ComponentEvent arg0) {}
	public void componentResized(ComponentEvent e) {
		int width = getWidth()-1250;
		if (width<0) width=0;  
		gap.setPreferredSize(new Dimension(width, 60));
		validate();
	}
	public void componentShown(ComponentEvent arg0) {}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(importer))
			fc.showOpenDialog(mp.jf);
		if(e.getSource().equals(fc)){
			mp.c.cp.importFiles(fc.getSelectedFiles());
		}
	}
}




