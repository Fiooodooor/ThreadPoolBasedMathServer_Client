import org.json.JSONObject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ChartCanvas {
    private BufferedImage theImage;
    private Graphics2D    bg;
    private int width,  xZero;
    private int height, yZero;
    private double xStart, xEnd, dX;
    private double yStart, yEnd, dY, oneByDy;

    ChartCanvas(JSONObject settings) {
        setWidth(settings.optInt("width",  1000));
        setHeight(settings.optInt("height", 1000));

        settheImage(new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_BGR));
        setbg(gettheImage().createGraphics());
        getbg().setColor(Color.white);
        getbg().fillRect(0, 0, getWidth(), getHeight());
        getbg().setColor(Color.gray);
        getbg().drawString("Nicolaus Copernicus University 2019. By Milosz Linkiewicz", 5, getHeight()-getbg().getFontMetrics().getHeight()-3);

        setxStart(settings.optDouble("xStart", -5));
        setyStart(settings.optDouble("yStart", -5));
        setxEnd(settings.optDouble("xEnd", 5));
        setyEnd(settings.optDouble("yEnd", 5));

        setAxlesRange();
    }

    private void setAxlesRange() {
        double tmp;
        if(getxStart() == getxEnd()) { setxEnd(getxEnd()+1); }
        if(getxStart() > getxEnd()) {
            tmp = getxStart();
            setxStart(getxEnd());
            setxEnd(tmp);
        }
        if(getyStart() == getyEnd()) { setyEnd(getyEnd()+1); }
        if(getyStart() > getyEnd()) {
            tmp = getyStart();
            setyStart(getyEnd());
            setyEnd(tmp);
        }
        setXAxis();
        setYAxis();
        drawBothAxles();
    }

    private void setXAxis() {
        double xAxisSize = getxEnd() - getxStart();
        setDx(xAxisSize / getWidth());

        if (getxStart() < 0 && getxEnd() > 0) setxZero(-(getxStart() / getDx()));
        else if (getxEnd() <= 0) setxZero(getWidth() - 1);
        else setxZero(0.0);
    }

    private void setYAxis() {
        double yAxisSize = getyEnd() - getyStart();
        setDy(yAxisSize / getHeight());
        setOneByDy(1 / getDy());

        if(getyStart()<0 && getyEnd()>0) setyZero((getyEnd()/getDy()));
        else if(getyEnd()<=0) setyZero(0.0);
        else setyZero(getHeight()-1);
    }

    private void drawBothAxles() {
        int fontH, temp;
        int pt, xText;
        fontH = bg.getFontMetrics().getHeight();
        bg.setColor(Color.BLACK);

        // x axis write
        if((pt=getxZero()-3) < 0) {
            pt=0;
            xText=pt+6;
        } else if(pt+7 >= getWidth()) {
            pt=getWidth()-7;
            xText=pt-(5*bg.getFontMetrics().charWidth('-'));
        } else xText = pt+6;
        bg.drawLine(0, getyZero(), getWidth()-1, getyZero());
        for(temp = 0; temp < getHeight(); temp += 2*fontH) {
            bg.drawLine(pt, temp, pt+6, temp);
            bg.drawString(String.format("%.2f", getyEnd() - temp * getDy()), xText, temp + fontH);
        }
        if((pt=getyZero()-3) < 0) {
            pt=0;
        } else if(pt+7 >= getHeight()) {
            pt=getHeight()-7;
        }
        bg.drawLine(getxZero(), 0, getxZero(), getHeight()-1);
        for(temp = 0; temp < getWidth(); temp += 3*fontH) {
            bg.drawLine(temp, pt, temp, pt+6);
            bg.drawString(String.format("%.2f", getxStart() + temp * getDx()), temp, pt+6+fontH);
        }
        bg.setColor(Color.RED);
    }

    public int translateY(double yReal) {
        int y;
        y = new Long(Math.round((yReal-getyStart())*getOneByDy())).intValue();

        if(y < 0) {
            y = 0;
        } else if (y >= getHeight()) {
            y = getHeight()-1;
        }
        return getHeight()-y;
    }


    public int getWidth() {
        return width;
    }

    private void setWidth(int width) {
        if(width<500) this.width=500;
        else if(width>15000) this.width=15000;
        else this.width = width;
    }

    public int getHeight() {
        return height;
    }

    private void setHeight(int height) {
        if(height<500) this.height=500;
        else if(height>15000) this.height=15000;
        else this.height = height;
    }

    public double getxStart() {
        return xStart;
    }

    private void setxStart(double xStart) {
        this.xStart = xStart;
    }

    private double getxEnd() {
        return xEnd;
    }

    private void setxEnd(double xEnd) {
        this.xEnd = xEnd;
    }

    public double getyStart() {
        return yStart;
    }

    private void setyStart(double yStart) {
        this.yStart = yStart;
    }

    private double getyEnd() {
        return yEnd;
    }

    private void setyEnd(double yEnd) {
        this.yEnd = yEnd;
    }

    private void setDx(double dX) {
        this.dX = dX;
    }
    public double getDx() {
        return dX;
    }

    private void setDy(double dY) {
        this.dY = dY;
    }
    private double getDy() {
        return dY;
    }

    private void setOneByDy(double oneByDy) {
        this.oneByDy = oneByDy;
    }
    private double getOneByDy() {
        return oneByDy;
    }

    private void settheImage(BufferedImage theImage) {
        this.theImage = theImage;
    }

    public BufferedImage gettheImage() {
        return theImage;
    }

    private void setbg(Graphics2D bg) {
        this.bg = bg;
    }

    public Graphics2D getbg() {
        return bg;
    }

    private int getxZero() {
        return xZero;
    }
    private void setxZero(double xZero) {
        this.xZero = (int)(Math.round(xZero));
    }

    private int getyZero() {
        return yZero;
    }
    private void setyZero(double yZero) {
        this.yZero = (int)(Math.round(yZero));
    }
}
