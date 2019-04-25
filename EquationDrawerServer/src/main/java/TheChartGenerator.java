import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TheChartGenerator {
    private PostfixEquation chartEquation;
    private ChartCanvas     chartCanvas;

    public TheChartGenerator(JSONObject chartData) {
        chartEquation = new PostfixEquation(chartData.optString("equation", "x"));
        chartCanvas   = new ChartCanvas(chartData.optJSONObject("canvasSettings"));
        generateGraph();
    }

    private boolean generateGraph() {
        int x0, y0;
        int x1, y1;
        double xReal;
        if(chartEquation.getErrors() == 0) {
            xReal = chartCanvas.getxStart();
            x1 = 0;
            y1 = chartCanvas.translateY(chartEquation.calculateValue(xReal));
            for(x0=1, xReal+=chartCanvas.getDx(); x0<chartCanvas.getWidth()-1; ++x0, xReal+=chartCanvas.getDx())
            {
                y0 = chartCanvas.translateY(chartEquation.calculateValue(xReal));
                 if(chartEquation.getErrors() == 0)
                 {
                    chartCanvas.getbg().drawLine(x1, y1, x0, y0);
                    x1 = x0;
                    y1 = y0;
                 } else {
                     return false;
                 }
            }
            return true;
        }
        return false;
    }

    public BufferedImage getRenderedChart() {
        return chartCanvas.gettheImage();
    }

    public void showChart() {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(chartCanvas.gettheImage())));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
