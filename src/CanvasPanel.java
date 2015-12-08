import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.*;
import java.io.File;
import java.util.*;

import javax.swing.*;

class Canvas extends JPanel{
	CanvasPanel cp;
	RulerPanel horiz, vert;
	MainPanel mp;
	
	public static final int RULER_WIDTH = 20;
	
	public Canvas(MainPanel m){
		mp=m;
		
		setLayout(new BorderLayout());
		cp = new CanvasPanel(this);
		add(cp, BorderLayout.CENTER);
		vert = new RulerPanel(this, RulerPanel.VERT);
		add(vert, BorderLayout.LINE_START);
		horiz = new RulerPanel(this, RulerPanel.HORIZ);
		add(horiz, BorderLayout.PAGE_START);
	}
	public Dimension getPreferredSize(){
		int width = mp.getPreferredSize().width-325;
		int height = mp.getPreferredSize().height-60;
		return new Dimension(width,height);		
	}
}

public class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener{
	Canvas c;
	int currentTool;
	ArrayList<Drawable> items;
	BrushPanel.Brush brush;
	BufferedImage permLayer, selectedLayer, mouseLayer;
	boolean updatePerm, updateSelected;
	int startx, starty;
	double zoom;
	
	public static final int PAINT_TOOL=6;
	
	public CanvasPanel(Canvas canv){
		c=canv;
		setBorder(BorderFactory.createMatteBorder(0,0,3,3,Color.DARK_GRAY));
		
		items = new ArrayList<Drawable>();
		startx=starty=0;
		zoom=1.0;
		
		updatePerm = updateSelected = false;
		int width = c.getPreferredSize().width - c.RULER_WIDTH;
		int height = c.getPreferredSize().height - c.RULER_WIDTH;
		permLayer = new BufferedImage(width, height,BufferedImage.TYPE_4BYTE_ABGR_PRE);
		selectedLayer = new BufferedImage(width, height,BufferedImage.TYPE_4BYTE_ABGR_PRE);
		mouseLayer = new BufferedImage(width, height,BufferedImage.TYPE_4BYTE_ABGR_PRE);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	public Dimension getPreferredSize(){
		int width = c.getPreferredSize().width - c.RULER_WIDTH;
		int height = c.getPreferredSize().height - c.RULER_WIDTH;
		return new Dimension(width,height);		
	}
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		setBackground(Color.WHITE);
		
		g.drawImage(permLayer, 0, 0, null);
		g.drawImage(selectedLayer, 0, 0, null);
		g.drawImage(mouseLayer, 0, 0, null);
	}
	public void changeZoom(double z){
		zoom=z;
		if(currentTool==PAINT_TOOL){
			Painting p = (Painting) items.get(items.size()-1);
			//change painting buffimage size or zoom or something
		}
	}
	public void setTool(int t){
		if(currentTool!=t){
			if(t==PAINT_TOOL){
				setBrush();
				items.add(new Painting(brush,0,0));
			}
			if(currentTool==PAINT_TOOL){
				Drawable temp = items.remove(items.size()-1);
				items.add(new ImageItem(temp.getImage(), temp.x, temp.y));				
			}
			currentTool = t;
		}
	}
	public void pressEnter(){
		if(currentTool==PAINT_TOOL){
			Drawable temp = items.remove(items.size()-1);
			items.add(new ImageItem(temp.getImage(), temp.x, temp.y));
			items.add(new Painting(brush,0,0));			
		}
	}
	public void setBrush(){
		brush = c.mp.a.ap.bp.bt.br;
	}
	public void importFiles(File [] files){
		
	}
	public void clearMouseLayer(){
		int width = getPreferredSize().width;
		int height = getPreferredSize().height;
		mouseLayer = new BufferedImage(width, height,BufferedImage.TYPE_4BYTE_ABGR_PRE);
	}
		
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {
		clearMouseLayer();
		repaint();
	}
	public void mousePressed(MouseEvent e) {
		c.mp.requestFocusInWindow();
		if(currentTool==PAINT_TOOL){
			if(!(items.get(items.size()-1) instanceof Painting))
				items.add(new Painting(brush,0,0));
			Painting p = (Painting)(items.get(items.size()-1));
			p.addStroke(this);
			p.addPoint(e.getX(), e.getY());
			mouseLayer.getGraphics().drawImage(p.getImage(), 0, 0, null);
		}
		repaint();
	}
	public void mouseReleased(MouseEvent e) {
		if(currentTool==PAINT_TOOL){
			mouseLayer.getGraphics().drawImage(brush.brushImage, e.getX()-75, e.getY()-75, null);
		}
		repaint();
	}
	
	public void mouseDragged(MouseEvent e) {
		clearMouseLayer();
		if(currentTool==PAINT_TOOL){
			Painting p = (Painting)(items.get(items.size()-1));
			p.addPoint(e.getX(), e.getY());
			mouseLayer.getGraphics().drawImage(p.getImage(), 0, 0, null);
		}
		repaint();
	}
	public void mouseMoved(MouseEvent e) {
		clearMouseLayer();
		if(currentTool==PAINT_TOOL){
			Painting p = (Painting)(items.get(items.size()-1));
			permLayer.getGraphics().drawImage(p.getImage(), 0, 0, null);
			mouseLayer.getGraphics().drawImage(brush.brushImage, e.getX()-75, e.getY()-75, null);
		}
		repaint();
	}
	
	public void o(String s){
		System.out.println(s);
	}
}

class RulerPanel extends JPanel{
	Canvas c;
	int type;
	
	public static final int HORIZ=0, VERT=1;
	
	public RulerPanel(Canvas canv, int t){
		c=canv;
		type=t;
		if(type==HORIZ) setBorder(BorderFactory.createMatteBorder(2,2,2,3,Color.DARK_GRAY));
		else			setBorder(BorderFactory.createMatteBorder(0,2,3,2,Color.DARK_GRAY));
	}
	public Dimension getPreferredSize(){
		if(type==HORIZ)
			return new Dimension(c.getWidth()-523, c.RULER_WIDTH);
		return new Dimension(c.RULER_WIDTH, c.getHeight());
	}
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		setBackground(new Color(220,230,240));
		int startx = c.cp.startx;
		int starty = c.cp.starty;
		double zoom = c.cp.zoom;
	}
}

