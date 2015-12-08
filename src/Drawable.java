import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;

public abstract class Drawable{
	int x, y;
	public Drawable(int xval, int yval){
		x=xval;
		y=yval;
	}
	public abstract BufferedImage getImage();
}

class Vector extends Drawable{
	VPoint src;
	ArrayList<VPoint> points;
	int boundX, boundY, boundWidth, boundHeight;
	
	public Vector(int xval, int yval){
		super(xval,yval);
		src = new VPoint(0,0);
		points.add(src);
		boundX = boundY = boundWidth = boundHeight = 0;
	}
	public void add(int x, int y){
		points.add(new VPoint(x,y));
	}
	public void bend(int pt, int side, int ang, int size){
		points.get(pt).setBend(side, ang, size);
	}
	public BufferedImage getImage(){
		return new BufferedImage(0,0,BufferedImage.TYPE_4BYTE_ABGR_PRE);
	}
	
	class VPoint{
		int rx, ry, ang1, ang2, s1, s2;
		
		public VPoint(int x, int y){
			rx=x; ry=y;
			ang1 = ang2 = s1 = s2 = 0;
		}
		public void setBend(int side, int ang, int size){
			if(side==1){
				ang1=ang; s1=size;
			}
			else if(side==2){
				ang2=ang; s2=size;
			}
		}
		
	}
}

class ImageItem extends Drawable{
	BufferedImage b;
	
	public ImageItem(int xval, int yval){
		super(xval,yval);		
	}
	public ImageItem(BufferedImage buff, int xval, int yval){
		super(xval,yval);
		b=buff;
	}
	public BufferedImage getImage(){
		return new BufferedImage(0,0,BufferedImage.TYPE_4BYTE_ABGR_PRE);
	}
}


class Painting extends Drawable{
	PaintStroke stroke;
	BrushPanel.Brush brush;
	BufferedImage paintingImage;
	
	public Painting(BrushPanel.Brush br, int xval, int yval){
		super(xval,yval);
		brush = br;
	}
	public void addStroke(CanvasPanel cp){
		stroke = new PaintStroke(cp, brush);
	}
	public void addPoint(int x, int y){
		stroke.addPoint(x, y);
	}
	public void completeStroke(){
		BufferedImage strokeImage = stroke.complete();
		paintingImage = strokeImage;
	}
	public BufferedImage getImage(){
		return stroke.getImage();
	}

	class PaintStroke{
		ArrayList<Point> srcPoints;
		BrushPanel.Brush brush;
		BufferedImage strokeImage;
		
		public PaintStroke(CanvasPanel cp, BrushPanel.Brush br){
			brush=br;
			strokeImage = new BufferedImage(cp.getPreferredSize().width, cp.getPreferredSize().height,
					BufferedImage.TYPE_4BYTE_ABGR);
			srcPoints = new ArrayList<Point>();
		}
		public void addPoint(int x, int y){
			srcPoints.add(new Point(x,y));
			if(srcPoints.size()>1){
				Point p1 = srcPoints.get(srcPoints.size()-2);
				Point p2 = srcPoints.get(srcPoints.size()-1);
				if(p1.x>p2.x){
					p2 = srcPoints.get(srcPoints.size()-2);
					p1 = srcPoints.get(srcPoints.size()-1);
				}
				int offsetX = brush.brushImage.getWidth()/2;
				int offsetY = brush.brushImage.getHeight()/2;
				Graphics2D g = (Graphics2D) strokeImage.getGraphics();
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
				for(int xval=p1.x; xval<p2.x; xval++){
					double xdist = (double)(xval-p1.x) / (p2.x-p1.x);
					int yval = (int) (p1.y + xdist * (p2.y-p1.y));
					g.drawImage(brush.brushImage, xval-offsetX, yval-offsetY, null);
				}
				p1 = srcPoints.get(srcPoints.size()-2);
				p2 = srcPoints.get(srcPoints.size()-1);
				if(p1.y>p2.y){
					p2 = srcPoints.get(srcPoints.size()-2);
					p1 = srcPoints.get(srcPoints.size()-1);
				}
				for(int yval=p1.y; yval<p2.y; yval++){
					double ydist = (double)(yval-p1.y) / (p2.y-p1.y);
					int xval = (int) (p1.x + ydist * (p2.x-p1.x));
					g.drawImage(brush.brushImage, xval-offsetX, yval-offsetY, null);
				}
			}
			else{
				Graphics2D g = (Graphics2D) strokeImage.getGraphics();
				int offsetX = brush.brushImage.getWidth()/2;
				int offsetY = brush.brushImage.getHeight()/2;
				g.drawImage(brush.brushImage, x-offsetX, y-offsetY, null);
			}
				
		}
		public BufferedImage getImage(){
			return strokeImage;
		}
		public BufferedImage complete(){
	/*		int left=0, right=strokeImage.getWidth(),
					top=0, bottom=strokeImage.getHeight();
			boolean done=false;
			int c=0, r=0;
			while(!done && c<strokeImage.getWidth()){
				for(r=0; r<strokeImage.getHeight(); r++)
					if(strokeImage.getRGB(c,r)!=0) done =true;
				if(done) left=c;
				else c++;
			}
			c=0; r=0; done=false;
			System.out.println(left);
			//for(int row=0; row<strokeImage.getWidth(); row++)
			//BufferedImage completedImage = new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR);
	*/		return strokeImage;
		}
	}
}
