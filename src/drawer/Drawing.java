package drawer;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.awt.BasicStroke; 
/**
 * This class is used to draw the data 
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class Drawing {
	/**
	 * A drawing is defined by five parameters. This is the constructor 
	 * @param nid node id 
	 * @param title draw title 
	 * @param nData data
	 * @param nrows data rows
	 * @param ncolumns data columns
	 */
	public Drawing(int nid, String title, double [][] nData, int nrows, int ncolumns){
		final XYSeries series = new XYSeries("Cost");//("w=convergencia")
		final XYSeries series1 = new XYSeries("Preventive Cost");//("delta");
		final XYSeries series2 = new XYSeries("Corrective Cost");//("w=0");
//		final XYSeries series3 = new XYSeries("Waiting Cost");
		for(int i=0;i<nrows;i++){
			series.add(nData[i][0], nData[i][3]);
			series1.add(nData[i][0], nData[i][1]);
			series2.add(nData[i][0], nData[i][2]);
//			series3.add(nData[i][0], nData[i][3]);
		}
		XYSeriesCollection my_data = new XYSeriesCollection();
		my_data.addSeries(series2);
		my_data.addSeries(series);
		my_data.addSeries(series1);
//		my_data.addSeries(series3);
		JFreeChart XYLineChart=ChartFactory.createXYLineChart("Cost vs Time","Time (days)","Cost",my_data,PlotOrientation.VERTICAL,true,true,false);
		XYPlot plot = (XYPlot) XYLineChart.getPlot();
		XYItemRenderer r = plot.getRenderer(); 
		BasicStroke wideLine = new BasicStroke(3.0f ); 
		r.setSeriesStroke(0, wideLine); 
		r.setSeriesStroke(1, wideLine); 
		r.setSeriesStroke(2, wideLine); 
		r.setSeriesStroke(3, wideLine); 
		r.setSeriesPaint( 0 , Color.RED );
		r.setSeriesPaint( 1 , Color.BLUE );
		r.setSeriesPaint( 2 , Color.GREEN);
		r.setSeriesPaint( 3 , Color.MAGENTA);
		ChartPanel chartPanel = new ChartPanel(XYLineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 300));
		XYLineChart.setBackgroundPaint(Color.white);
		plot.setBackgroundPaint(Color.white);
//		plot.setRangeGridlinePaint(Color.gray);	
		int width=640; 
		int height=480;
		//	Valuexis domainAxis = plot.getDomainAxis();
		ValueAxis domain = plot.getDomainAxis();
//		domain.setRange(0.00, 500);
		domain.setAutoRangeMinimumSize(20);
		//        domain.setLabel(new NumberTickUnit(20));
		domain.setVerticalTickLabels(true);
		ValueAxis range =  plot.getRangeAxis();
		range.setRange(0,0.6);
		range.setAutoRangeMinimumSize(0.1);
		//range.setTickUnit(new NumberTickUnit(0.1));
		Font font3 = new Font("Dialog", Font.PLAIN, 20); 
		plot.getDomainAxis().setLabelFont(font3);
		plot.getRangeAxis().setLabelFont(font3);
		Font font = new Font("Dialog", Font.PLAIN, 17);
		range.setTickLabelFont(font);
		domain.setTickLabelFont(font);
		File XYlineChart=new File("./data/"+title+".png");                
		try {
			ChartUtilities.saveChartAsPNG(XYlineChart,XYLineChart,width,height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("We have problems, i can´t draw");
		}
	}	
}