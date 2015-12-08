import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.metal.MetalScrollButton;
import javax.swing.text.MaskFormatter;	

class AttributeScrollPane extends JScrollPane{
	AttributePanel ap;
	MainPanel mp;
	
	public AttributeScrollPane(MainPanel m){
		mp=m;
		setOpaque(true);
		ap = new AttributePanel(mp);
		setViewportView(ap);
		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
		getVerticalScrollBar().setUnitIncrement(12);
		setBorder(BorderFactory.createMatteBorder(0,5,5,5,Color.DARK_GRAY));
	}
	public Dimension getPreferredSize(){
		return new Dimension(325,840);
	}
	public void paintComponent(Graphics gr){
		Graphics2D  g = (Graphics2D) gr;
	}
}

public class AttributePanel extends JPanel implements MouseListener, ChangeListener{
	MainPanel mp;
	BrushPanel bp;
	
	public AttributePanel(MainPanel m){
		setOpaque(true);
		addMouseListener(this);
		mp=m;
		setPanels();
	}
	public void setPanels(){
		bp = new BrushPanel(mp);
		add(bp);
	}
	public Dimension getPreferredSize(){
		int height = 0;
		for(Component c: getComponents())
			height += c.getPreferredSize().getHeight() + 10;
		if(height!=0) height-=10;
		return new Dimension(300,height);
	}
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		mp.requestFocusInWindow();
	}
	public void mouseReleased(MouseEvent e) {}
	
	public void stateChanged(ChangeEvent e) {}
}

class BrushPanel extends JPanel{
	MainPanel mp;
	JTabbedPane tabPane;
	ColorPanel cp;
	BrushTab bt;
	
	public BrushPanel(MainPanel m){
		setOpaque(true);
		setPreferredSize(new Dimension(282,400));
		mp=m;
		cp = new ColorPanel(mp);
		bt = new BrushTab();
		
		tabPane = new JTabbedPane();		
		tabPane.addTab("Size & Shape", bt);
		tabPane.addTab("Color", cp);
		ChangeListener changeListener = new ChangeListener() {
		      public void stateChanged(ChangeEvent changeEvent) {
		        ((BrushTab)tabPane.getComponentAt(0)).updateBrush();
		      }
		};
		tabPane.addChangeListener(changeListener);
		tabPane.setPreferredSize(new Dimension(280,345));
		
		add(new JPanel(){
			public Dimension getPreferredSize(){
				return new Dimension(280,30);
			}
			public void paintComponent(Graphics gr){
				super.paintComponent(gr);
				Graphics2D g = (Graphics2D) gr;
				setBackground(Color.LIGHT_GRAY);
				
				g.setRenderingHint(
				        RenderingHints.KEY_TEXT_ANTIALIASING,
				        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setFont(new Font("Trebuchet MS",Font.BOLD, 25));
				g.drawString("Brush Attributes", 45, 25);
			}
		});
		add(tabPane);
	}
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		
		g.drawRoundRect(0,0, getPreferredSize().width-1, getPreferredSize().height-1, 10, 10);
		setBackground(Color.LIGHT_GRAY);
	}
	
	class BrushTab extends JPanel implements ChangeListener, MouseListener, MouseMotionListener{
		Slider roundnessSlider, hardnessSlider, sizeSlider, renderBackgroundSlider;
		Rectangle twRect;
		Point twPoint, prev, brushCenter;
		boolean draggingTW;
		BufferedImage twSqrImage;
		Brush br;
		int restrictHV;
		public final int RESTRICT_NONE=0, RESTRICT_HORIZONTAL=1, RESTRICT_VERTICAL=2;
		
		public BrushTab(){
			setPreferredSize(new Dimension(270,310));
			setOpaque(false);
			setLayout(null);
			addMouseListener(this);
			addMouseMotionListener(this);
			
			twRect = new Rectangle(40,20,130,130);
			twPoint = prev = new Point(105,85);
			brushCenter = new Point(17,17);
			draggingTW = false;
			restrictHV = RESTRICT_NONE;
			try{
				twSqrImage = ImageIO.read(new File("resources/TWplot.png"));
			}catch(Exception e){}
			
			br = new Brush();
			
			sizeSlider = new Slider(220,36,Slider.CIRCLE_SLIDER,true,this);
			add(sizeSlider);
			sizeSlider.setBounds(20,200,230,10);
			hardnessSlider = new Slider(220,360,Slider.CIRCLE_SLIDER,true,this);
			add(hardnessSlider);
			hardnessSlider.setBounds(20,250,230,10);
			roundnessSlider = new Slider(220,360,Slider.CIRCLE_SLIDER,true,this);
			add(roundnessSlider);
			roundnessSlider.setBounds(20,300,230,10);
			renderBackgroundSlider = new Slider(72,360,Slider.CIRCLE_SLIDER,true,this);
			add(renderBackgroundSlider);
			renderBackgroundSlider.setBounds(185,120,86,10);
			
			updateBrush();
		}
		public void updateBrush(){
			int size = (int) (sizeSlider.getModel().getValue()/3.6);
			double hardness = hardnessSlider.getModel().getValue()/800.0 + .55;
			int kernelSize = ((int) (size-hardness*size));
				if(kernelSize==0) kernelSize=1;			
			double widthRatio = (((twPoint.getX()-40)-65)/65.0);
				widthRatio = widthRatio<0 ? -1/(widthRatio*2-1) : widthRatio*2+1;
			double rotate = (twPoint.getY()-20-65)/65;
				rotate*= Math.PI/4;
			double roundness = roundnessSlider.getModel().getValue() / 360.0;
			
			br.update(size, hardness, roundness, kernelSize, widthRatio, rotate);
			
			try{
				mp.c.cp.setBrush();
			}catch(Exception e){}
		}
		public void paintComponent(Graphics gr){
			super.paintComponent(gr);
			Graphics2D g = (Graphics2D) gr;
			g.setRenderingHint(
			        RenderingHints.KEY_TEXT_ANTIALIASING,
			        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(
			        RenderingHints.KEY_ANTIALIASING,
			        RenderingHints.VALUE_ANTIALIAS_ON);

			g.drawImage(twSqrImage, 26, 20, 145, 151, null);
			g.drawRect(40, 20, 130, 130);
			g.setColor(new Color(40,100,120));
			g.drawOval(twPoint.x-4, twPoint.y-4, 8, 8);
			g.drawOval(twPoint.x-3, twPoint.y-3, 6, 6);
			
			
			double backgroundValue = renderBackgroundSlider.getModel().getValue()/360.0;
			int cval = (int) (backgroundValue * 255);
			Color backgroundColor = new Color(cval,cval,cval);
			g.setColor(backgroundColor);
			g.fillRect(202, 62, 46, 46);
			g.setColor(Color.BLACK);
			g.drawRect(202, 62, 45, 45);
			g.drawImage(br.brushRender, 202, 62, null);
			
			g.setColor(Color.BLACK);
			g.setFont(new Font("Trebuchet MS",Font.PLAIN, 16));
			g.drawString("Size", 30,  195);
			g.drawString("Hardness", 30,  245);
			g.drawString("Roundness", 30,  295);
		}
		
		public void stateChanged(ChangeEvent e) {
			if(e.getSource() instanceof BoundedRangeModel){
				updateBrush();
			}
			repaint();
		}
		public void mouseClicked(MouseEvent arg0) {}
		public void mouseEntered(MouseEvent arg0) {	}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent e) {
			mp.requestFocusInWindow();
			if(twRect.contains(e.getPoint())){
				if(!mp.shiftKey)
					restrictHV=RESTRICT_NONE;
				if(mp.shiftKey){
					if (Math.abs(twPoint.x-e.getPoint().x) > Math.abs(twPoint.y-e.getPoint().y))
						restrictHV=RESTRICT_VERTICAL;
					else
						restrictHV = RESTRICT_HORIZONTAL;
				}
				prev = e.getPoint();
				draggingTW=true;
				if(restrictHV==RESTRICT_NONE){
					twPoint = e.getPoint();
				}
				else{
					int x = restrictHV==RESTRICT_HORIZONTAL ? e.getX() : twPoint.x;
					int y = restrictHV==RESTRICT_VERTICAL ? e.getY() : twPoint.y;
					twPoint = new Point(x,y);
				}
				if(mp.ctrlKey)
					twPoint.setLocation(105, 85);
				updateBrush();
			}
			repaint();
		}
		public void mouseReleased(MouseEvent arg0) {
			draggingTW=false;
		}
		
		public void mouseDragged(MouseEvent e) {
			if(draggingTW && !mp.ctrlKey){
				int x = twPoint.x;
				int y = twPoint.y;
				if(restrictHV==RESTRICT_NONE){
					x = twPoint.x + (e.getX() - prev.x);
					y = twPoint.y + (e.getY() - prev.y);
				}
				else if (restrictHV==RESTRICT_HORIZONTAL){
					x = twPoint.x + (e.getX() - prev.x);
					y = twPoint.y;
				}
				else if(restrictHV==RESTRICT_VERTICAL){
					x = twPoint.x;
					y = twPoint.y + (e.getY() - prev.y);
				}
				if(x<40) x=40; if(x>170) x=170;
				if(y<20) y=20; if(y>150) y=150;
				twPoint.setLocation(x,y);
				prev = e.getPoint();
			}
			updateBrush();
			repaint();
		}
		public void mouseMoved(MouseEvent arg0) {}
		
	}
	class Brush{
		BufferedImage brushRender, brushImage;
		RoundRectangle2D.Double brushPath;
		Kernel kernel;
		int opacity;//0-255
		
		public Brush(){
			brushRender = new BufferedImage(46,46,BufferedImage.TYPE_4BYTE_ABGR_PRE);
			brushImage = new BufferedImage(150,150,BufferedImage.TYPE_4BYTE_ABGR_PRE);
			brushPath = new RoundRectangle2D.Double(18,18,10,10,10,10);
			kernel = new Kernel(1,1,new float[]{1});
		}
		public void update(int size, double hardness, double roundness,
				int kernelSize, double widthRatio, double rotate){
			
			float k[] = new float[kernelSize*kernelSize];
			for(int i=0; i<k.length; i++)
				k[i] = (float) (1.0 / (k.length));
			kernel = new Kernel(kernelSize, kernelSize, k); 
			
			if(widthRatio<1){
				brushPath.height=size*hardness<2 ? 2 : size*hardness;
				brushPath.width=size*widthRatio*hardness<2 ? 2 : size*widthRatio*hardness;
			}
			else{
				brushPath.height=(size/widthRatio * hardness)<2 ? 2 : (size/widthRatio * hardness);
				brushPath.width=size*hardness<2 ? 2 : size*hardness;
			}
			brushPath.x = 75 - brushPath.width/2;
			brushPath.y = 75 - brushPath.height/2;
			brushPath.archeight = roundness * brushPath.height; 
			brushPath.arcwidth = roundness * brushPath.width;
			
			brushImage = new BufferedImage(150,150,BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = (Graphics2D)(brushImage.getGraphics());
			
			if(rotate!=0){
				AffineTransform at = new AffineTransform();
				at.rotate(rotate, 75, 75);
				g.transform(at);
			}			
			g.setRenderingHint(
			        RenderingHints.KEY_ANTIALIASING,
			        RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(new Color(cp.c.getRed(),cp.c.getGreen(),cp.c.getBlue(), 255));
			opacity = cp.opac;
			g.fill(brushPath);
			
			updateRender(size,hardness,roundness,kernelSize,widthRatio,rotate);
		}
		private void updateRender(int size, double hardness, double roundness,
				int kernelSize, double widthRatio, double rotate){
			brushRender = new BufferedImage(46,46,BufferedImage.TYPE_4BYTE_ABGR_PRE);
						
			size = (int) (Math.sqrt(size*10));
			kernelSize = ((int) (size-hardness*size));
			if(kernelSize==0) kernelSize=1;
			float k[] = new float[kernelSize*kernelSize];
			for(int i=0; i<k.length; i++)
				k[i] = (float) (1.0 / (k.length));
			Kernel renderKernel = new Kernel(kernelSize, kernelSize, k); 
			
			if(widthRatio<1){
				brushPath.height=size*hardness<2 ? 2 : size*hardness;
				brushPath.width=size*widthRatio*hardness<2 ? 2 : size*widthRatio*hardness;
			}
			else{
				brushPath.height=(size/widthRatio * hardness)<2 ? 2 : (size/widthRatio * hardness);
				brushPath.width=size*hardness<2 ? 2 : size*hardness;
			}
			brushPath.x = 23 - brushPath.width/2;
			brushPath.y = 23 - brushPath.height/2;
			brushPath.archeight = roundness * brushPath.height; 
			brushPath.arcwidth = roundness * brushPath.width;
			
			brushRender = new BufferedImage(46,46,BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = (Graphics2D)(brushRender.getGraphics());
						
			if(rotate!=0){
				AffineTransform at = new AffineTransform();
				at.rotate(rotate, 23, 23);
				g.transform(at);
			}			
			g.setRenderingHint(
			        RenderingHints.KEY_ANTIALIASING,
			        RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(new Color(cp.c.getRed(),cp.c.getGreen(),cp.c.getBlue(), cp.opac));
			g.fill(brushPath);
			
			ConvolveOp convolver = new ConvolveOp(renderKernel);
			brushRender = convolver.filter(brushRender, null);
		}
	}
}

class ColorPanel extends JPanel implements ChangeListener, PropertyChangeListener, MouseListener, MouseMotionListener{
	MainPanel mp;
	BufferedImage colorsqr, huescale;
	Color baseColor, c;
	double hueAng, saturation, value;
	Rectangle colorSqrRect, hueScaleRect;
	Point satVal, prev;
	boolean draggingSatVal, draggingHue, updatingRGB, updatingHSV, updatingHex, updatingOpac;
	int red, green, blue, opac;			//0-255 val
	Slider hueSlider, opacitySlider;
	JFormattedTextField hexCode;
	JSpinner rSpin, gSpin, bSpin;
		
	public ColorPanel(MainPanel m){
		setPreferredSize(new Dimension(270,310));
		setOpaque(false);
		setLayout(null);
		mp = m;
		hueAng=0; saturation=1; value=1;
		opac = 255;
		colorSqrRect = new Rectangle(10,15,175,175);
		hueScaleRect = new Rectangle(10,215,250,25);
		satVal = new Point(185,15);
		draggingSatVal = draggingHue = false;
		updatingRGB = updatingHSV = updatingHex = updatingOpac = false;
		
		try{
			colorsqr = ImageIO.read(new File("resources/colorsquare.png"));
			huescale = ImageIO.read(new File("resources/huescale1.png"));
		}catch(Exception e){}
		
		hueSlider = new Slider(250,0,Slider.TRIANGLE_SLIDER, true,this);
		add(hueSlider);
		hueSlider.setBounds(5, 240, 260, 10);
		opacitySlider = new Slider(190,360,Slider.CIRCLE_SLIDER, true,this);
		add(opacitySlider);
		opacitySlider.setBounds(60,268,200,20);
		
		try{
			hexCode = new JFormattedTextField( new MaskFormatter("HHHHHHHH") );
		}catch(Exception e){
			hexCode = new JFormattedTextField();
		}
		hexCode.setValue("FFFF0000");
		hexCode.setColumns(10);
		hexCode.addPropertyChangeListener("value", this);
		hexCode.setHorizontalAlignment(JTextField.CENTER);
		add(hexCode);
		hexCode.setBounds(190,65,80,25);
		
		SpinnerModel sm = new SpinnerNumberModel(0,0,255,1);
		rSpin = new JSpinner(sm);
		rSpin.addChangeListener(this);
		add(rSpin);
		rSpin.setBounds(215, 105, 55, 25);
		sm = new SpinnerNumberModel(0,0,255,1);
		gSpin = new JSpinner(sm);
		gSpin.addChangeListener(this);
		add(gSpin);
		gSpin.setBounds(215, 135, 55, 25);
		sm = new SpinnerNumberModel(0,0,255,1);
		bSpin = new JSpinner(sm);	
		bSpin.addChangeListener(this);
		add(bSpin);
		bSpin.setBounds(215, 165, 55, 25);
		
		updateRGB();
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	public void updateRGB(){
		updatingRGB = true;
		
		hueAng = (double)hueSlider.getModel().getValue() / hueSlider.getModel().getMaximum() * 360;
		saturation = (satVal.getX() - 10) / 175.0;
		value = (190 - satVal.getY()) / 175.0;
		double r,g,b;
		double chroma = value * saturation;
		double hue1 = hueAng/60;
		double x = (1- Math.abs(hue1%2 - 1));
		r=g=b=0;
		if(hue1<1){
			r=chroma; g=chroma*x; b=0;
			baseColor = new Color(255, (int)(x*255), 0);
		}
		else if(hue1<2){
			r=chroma*x; g=chroma; b=0;
			baseColor = new Color((int)(x*255), 255, 0);
		}
		else if(hue1<3){
			r=0; g=chroma; b=chroma*x;
			baseColor = new Color(0, 255, (int)(x*255));
		}
		else if(hue1<4){
			r=0; g=chroma*x; b=chroma;
			baseColor = new Color(0, (int)(x*255), 255);
		}
		else if(hue1<5){
			r=chroma*x; g=0; b=chroma;
			baseColor = new Color((int)(x*255), 0, 255);
		}
		else if(hue1<=6){
			r=chroma; g=0; b=chroma*x;
			baseColor = new Color(255, 0, (int)(x*255));
		}
		double m = value-chroma;
		r+=m; g+=m; b+=m;
		red = (int)(r*255); green = (int)(g*255); blue = (int)(b*255);
		c = new Color(red, green, blue);
		
		rSpin.setValue(red);
		gSpin.setValue(green);
		bSpin.setValue(blue);
		hexCode.setValue(String.format("%02X%02X%02X%02X", red, green, blue, opac));
		
		updatingRGB=false;
	}
	public void updateHSV(){
		updatingHSV=true;
		
		c = new Color(red, green, blue);
		if(!updatingHex)
			hexCode.setValue(String.format("%02X%02X%02X%02X", red, green, blue, opac));
		
		double r = red/255.0; double g = green/255.0; double b = blue/255.0;
		value = Math.max(r, Math.max(g, b));
		double min = Math.min(r, Math.min(g, b));
		double chroma = value - min;
		double hue1 = hueAng/60.0;
		if(chroma!=0){
			if(value==r) hue1 = ((g-b)/chroma) % 6;
			if(value==g) hue1 = ((b-r)/chroma) + 2;
			if(value==b) hue1 = ((r-g)/chroma) + 4;
			if(hue1<0) hue1 = 6 + hue1;
		}
		
		double x = (1- Math.abs(hue1%2 - 1));
		if(hue1<1)
			baseColor = new Color(255, (int)(x*255), 0);
		else if(hue1<2)
			baseColor = new Color((int)(x*255), 255, 0);
		else if(hue1<3)
			baseColor = new Color(0, 255, (int)(x*255));
		else if(hue1<4)
			baseColor = new Color(0, (int)(x*255), 255);
		else if(hue1<5)
			baseColor = new Color((int)(x*255), 0, 255);
		else if(hue1<=6)
			baseColor = new Color(255, 0, (int)(x*255));
		
		hueAng = hue1 * 60;
		saturation = 0;
		if(chroma!=0) saturation = chroma/value;
		
		hueSlider.getModel().setValue((int)(hueAng * hueSlider.getModel().getMaximum() / 360.0));
		int satx = (int) (saturation * 175 +10);
		int saty = (int) (190 - value*175);
		satVal.setLocation(satx, saty);
		
		updatingHSV=false;
	}
	public static Color hexToColor(String hex){
		Color opaqueColor = Color.decode("0x"+hex.substring(0,6));
		int opacity = Integer.decode("0x" + hex.substring(6));
		return new Color(opaqueColor.getRed(), opaqueColor.getGreen(),
				opaqueColor.getBlue(), opacity);
	}
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(
		        RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(Color.BLACK);
		g.drawRect(9,14,176,176);
		g.setColor(baseColor);
		g.fillRect(	10, 15, 175, 175);		
		g.drawImage(colorsqr,10,15,175,175,null);
		
		g.setColor(Color.BLACK);
		g.drawOval(satVal.x-5, satVal.y-5, 10, 10);
		g.setColor(Color.WHITE);
		g.drawOval(satVal.x-4, satVal.y-4, 8, 8);
		
		g.setColor(Color.BLACK);
		g.drawRect(9, 214, 251, 26);
		g.drawImage(huescale,10,215,250,25,null);
				
		g.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(new Font("Arial",  Font.PLAIN, 14));
	    g.drawString("Opacity", 7, 278);
	    g.drawString("R:", 200, 122);
	    g.drawString("G:", 200, 152);
	    g.drawString("B:", 200, 182);
		
	    g.drawRect(209,15,41,41);
		g.setColor(c);
		g.fillRect(210,16,40,40);
	}
	
	
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() instanceof BoundedRangeModel){
			BoundedRangeModel m = (BoundedRangeModel) e.getSource();
			if(m.equals(hueSlider.getModel()) && !updatingHSV){
				updateRGB();
			}
			if(m.equals(opacitySlider.getModel()) && !updatingHex){
				updatingOpac = true;
				opac = (int) (255.0 * opacitySlider.getModel().getValue()
						/ opacitySlider.getModel().getMaximum());
				hexCode.setValue(String.format("%02X%02X%02X%02X", red, green, blue, opac));
				updatingOpac = false;
			}
		}
		else if(e.getSource() instanceof JSpinner && !updatingRGB && !updatingHex){
			red = (Integer) rSpin.getValue();
			green = (Integer) gSpin.getValue();
			blue = (Integer) bSpin.getValue();
			updateHSV();
		}
	}
	
	
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent e) {
		mp.requestFocusInWindow();
		if(colorSqrRect.contains(e.getPoint())){
			prev = e.getPoint();
			draggingSatVal=true;
			satVal = e.getPoint();
			updateRGB();
		}
		else if(hueScaleRect.contains(e.getPoint())){
			draggingHue=true;
			int x = e.getX()-10;
			hueSlider.updateSlider(x);
			updateRGB();
		}
		repaint();
	}
	public void mouseReleased(MouseEvent arg0) {
		draggingSatVal=false;
		draggingHue=false;
	}
	
	public void mouseDragged(MouseEvent e) {
		if(draggingSatVal){
			int x = satVal.x + (e.getX() - prev.x);
			if(x<10) x=10; if(x>185) x=185;
			int y = satVal.y + (e.getY() - prev.y);
			if(y<15) y=15; if(y>190) y=190;
			satVal.setLocation(x,y);
			prev = e.getPoint();
			updateRGB();
		}
		if(draggingHue){
			int x = e.getX()-10;
			hueSlider.updateSlider(x);
		}
		repaint();
	}
	public void mouseMoved(MouseEvent arg0) {}
	
	public void propertyChange(PropertyChangeEvent e) {
		if(e.getSource() instanceof JFormattedTextField 
				&& !updatingRGB && !updatingHSV && !updatingOpac){
			updatingHex=true;
			Color hexColor = hexToColor(hexCode.getText());
			red = hexColor.getRed(); green = hexColor.getGreen(); blue = hexColor.getBlue();
			rSpin.setValue(red);
			gSpin.setValue(green);
			bSpin.setValue(blue);
			opac = hexColor.getAlpha();
			opacitySlider.getModel().setValue((int) (opac / 255.0
					* opacitySlider.getModel().getMaximum()));
			updateHSV();
			updatingHex=false;
		}
		repaint();
	}
}

class Inheritance{
	public static void sygnusSygnus(Comparable 
			c, Point q, ColorPanel j, byte bite)
	{
		ColorPanel Order66 = new ColorPanel(new MainPanel(new JFrame()));
		if(7>6)
			System.out.println(execute(Order66));
		Vampire vladimir = new Vampire();
		Byte Bite = new Byte((byte)('b'+'y'+'t'+'e'));
				vladimir.bite((byte) Bite);
			System.out.println(Bite);
		
		j.prev = q;
		if(c.compareTo(j)==q.getX())
				sygnusSygnus(c, q, j, Bite);
			
	}
	protected static boolean execute(ColorPanel seven)
	{
		String eggsAreCute = new String ("");
		if(eggsAreCute==seven.hexCode.getText())
			return true;
		else
		{
			return new BasicInternalFrameTitlePane(new JInternalFrame()).isOptimizedDrawingEnabled(); 
			
		}
			
	}
}
class Vampire extends Inheritance{
	public MetalScrollButton bite(byte b)
	{
		Boolean yearsASlave = new Boolean(true);
		return new MetalScrollButton((int)b, 12, yearsASlave);
		
	}
}


class Slider extends JPanel implements MouseListener, MouseMotionListener{
	int length, type;
	boolean horizontal, dragging;
	
	BoundedRangeModel model;
	Rectangle sliderRect;
	Point prev;
	public static final int TRIANGLE_SLIDER=0, RECTANGLE_SLIDER=1, CIRCLE_SLIDER=2;
	
	public Slider(int l, int init, int t, boolean h, ChangeListener cl){
		length=l;  horizontal=h;
		type = t;
		setOpaque(false);
		model = new DefaultBoundedRangeModel(init,0,0,360);
		model.addChangeListener(cl);
		if(horizontal) sliderRect = new Rectangle(init,0,10,10);
		else sliderRect = new Rectangle(0,init,10,10);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	public Dimension getPreferredSize(){
		if(horizontal)
			return new Dimension(length+10,10);
		else
			return new Dimension(10,length+10);
	}
	public void updateSlider(int d){
		Point p;
		if(d>length) d=length;
		if(d<0) d=0;
		if(horizontal)
			p = new Point(d, sliderRect.getLocation().y);
		else
			p = new Point(sliderRect.getLocation().x, d);
		sliderRect.setLocation(p);
		double frac = (double)d/length;
		model.setValue((int) (frac*360));
	}
	public BoundedRangeModel getModel(){
		return model;
	}
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(
		        RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		
		int d= (int) ((model.getValue()/360.0)*length) + 5;
		g.setColor(Color.BLACK);
		Polygon p = new Polygon();
		if(horizontal){
			if(type==Slider.TRIANGLE_SLIDER){
				p.addPoint(d-5,10);
				p.addPoint(d, 0);
				p.addPoint(d+5, 10);
				g.fillPolygon(p);
			}
			else if(type==Slider.RECTANGLE_SLIDER){
				g.setColor(Color.GRAY);
				g.fillRect(5, 4, length, 2);
				g.setColor(Color.BLACK);
				g.fillRect(d-2,0,5,10);
			}
			else if(type==Slider.CIRCLE_SLIDER){
				g.setColor(new Color(212,212,212));
				g.fillRect(5, 4, length, 2);
				g.setColor(new Color(84,84,84));
				g.drawRect(4, 3, length+1, 3);
				g.setColor(new Color(132,159,173));
				g.fillOval(d-5,0,10,10);
				g.setColor(Color.BLACK);
				g.drawOval(d-5,0,9,9);
			}
		}
		else{
			if(type==Slider.TRIANGLE_SLIDER){
				p.addPoint(10, d-5);
				p.addPoint(0, d);
				p.addPoint(10, d+5);
				g.fillPolygon(p);
			}
			else{
				g.setColor(Color.GRAY);
				g.fillRect(4, 5, 2, length);
				g.setColor(Color.BLACK);
				g.fillRect(0,d-2,10,5);
			}
		}
	}

	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent e) {
		dragging=true;
		int d;
		if(horizontal)
			d = e.getX()-5;
		else
			d =  e.getY()-5;
		updateSlider(d);
		prev = e.getPoint();
		repaint();
	}
	public void mouseReleased(MouseEvent arg0) {
		dragging=false;
		prev=null;
	}

	public void mouseDragged(MouseEvent e) {
		if(dragging){
			int d;
			if(horizontal)
				d = sliderRect.getLocation().x + (e.getX() - prev.x);
			else
				d = sliderRect.getLocation().y + (e.getY() - prev.y);
			updateSlider(d);
			prev = e.getPoint();
			repaint();
		}
	}
	public void mouseMoved(MouseEvent arg0) {}
}





