import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainClass extends Application {
	
	static ArrayList<ArrayList<Integer>> fich;
	static ArrayList<ArrayList<Integer>> dist;

	private static void cargafichero(String archivo) throws IOException {
		FileInputStream fstream = new FileInputStream(archivo);
		
		 // Get the object of DataInputStream
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    
	    strLine = br.readLine();
	    int n = Integer.parseInt(strLine.trim());
	    
	    
	    ArrayList<ArrayList<Integer>> f = new ArrayList();
	    ArrayList<ArrayList<Integer>> d = new ArrayList();
	    
		for(int i=0; i<n; i++) {
			f.add(new ArrayList(n));
			d.add(new ArrayList(n));
		}
		
		/*
		while ((strLine=br.readLine()) != null) {
			System.out.println(strLine);
		}
		*/
		strLine = br.readLine();
		
		for(int i=0; i<n; i++) {
				strLine = br.readLine();
				strLine = strLine.substring(1, strLine.length());
				String[] parts = (strLine.trim()).split("\\s+");				
				ArrayList<Integer> nuevo = new ArrayList();
				
				for(int k=0; k<parts.length; k++) {
					nuevo.add(Integer.parseInt(parts[k]));
				}
				
				f.set(i, nuevo);
		}
		
		
		for(int i=0; i<n; i++) {
				strLine = br.readLine();
				strLine = strLine.trim();
				String[] parts = strLine.split("\\s+");				
				ArrayList<Integer> nuevo = new ArrayList();
				
				for(int k=0; k<parts.length; k++) {
					nuevo.add(Integer.parseInt(parts[k]));
				}
				
				d.set(i, nuevo);
		}
		
		fich = f;
		dist = d;
		
		
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// TODO Auto-generated method stub
				Pair<ArrayList<Integer>, Integer> solucion;
				Pair<ArrayList<Integer>, Integer> solucionLamarck;
				Pair<ArrayList<Integer>, Integer> solucionBaldwin;


				final int MAX_EVALUACIONES = 50000;
				String archivo = "E:\\Máster Ingeniería Informática\\IC\\qap.datos\\tai256c.dat";
				
				cargafichero(archivo);
				
				QAP qap = new QAP(fich,dist);
			
				long ini1 = System.currentTimeMillis();
				
			    solucion = qap.solAG(MAX_EVALUACIONES);
			    
			    long fin1 = System.currentTimeMillis();
			    
			    long ini2 = System.currentTimeMillis();
			    solucionLamarck = qap.solucionLamarck(MAX_EVALUACIONES*2);
			   
			    long fin2 = System.currentTimeMillis();
			    
			    long ini3 = System.currentTimeMillis();
			    solucionBaldwin = qap.solucionBaldwin(MAX_EVALUACIONES*2);
			   
			    long fin3 = System.currentTimeMillis();
			    
			    
			    
			    System.out.println("---------------------------------------------------------------------------");
			    
			    System.out.println("La última generación con el algoritmo Clásico es " + solucion.getKey() + " con un fitness de = " + solucion.getValue());
			    
			    System.out.println("Tiempo transcurrido -- > "+ (fin1-ini1)/1000.0);
			    System.out.println("---------------------------------------------------------------------------");
			    System.out.println("---------------------------------------------------------------------------");
			       
			   
			    System.out.println("La última generación con el algoritmo Lamarck es " + solucionLamarck.getKey() + " con un fitness de = " + solucionLamarck.getValue());
			    System.out.println("Tiempo transcurrido -- > "+ (fin2-ini2)/1000.0);

			    System.out.println("---------------------------------------------------------------------------");
			    System.out.println("---------------------------------------------------------------------------");
			    
			    System.out.println("La última generación con el algoritmo Baldwin es " + solucionBaldwin.getKey() + " con un fitness de = " + solucionBaldwin.getValue());
			    System.out.println("Tiempo transcurrido -- > "+ (fin3-ini3)/1000.0);

			    System.out.println("---------------------------------------------------------------------------");
			    
			    ArrayList<Integer> datos = qap.getCostesClassic();
			    ArrayList<Integer> datosL = qap.getCostesLamarck();
			    ArrayList<Integer> datosB = qap.getCostesBaldwin();
			    
			    Stage stage = primaryStage;
				stage.setTitle("Basic Solutions");
				
				 //defining the axes
		        final NumberAxis xAxis = new NumberAxis();
		        final NumberAxis yAxis = new NumberAxis(45000000, 57000000, 500000);
		        xAxis.setLabel("Generaciones");
		        yAxis.setLabel("Fitness");
		        //creating the chart
		        final LineChart<Number,Number> lineChart = 
		                new LineChart<Number,Number>(xAxis,yAxis);
		                
		        lineChart.setTitle("Soluciones variante básica");
		        //defining a series
		        XYChart.Series series = new XYChart.Series();
		        series.setName("Classic");
			    
		        XYChart.Series series2 = new XYChart.Series();
		        series2.setName("Lamarck");
		        
		        XYChart.Series series3 = new XYChart.Series();
		        series3.setName("Baldwin");
		        
		        for(int i=0; i<datos.size(); i++)
					series.getData().add(new XYChart.Data(i, datos.get(i)));
		        
		        for(int i=0; i<datosL.size(); i++)
					series2.getData().add(new XYChart.Data(i, datosL.get(i)));
		        
		        for(int i=0; i<datosB.size(); i++)
					series3.getData().add(new XYChart.Data(i, datosB.get(i)));

				 Scene scene  = new Scene(lineChart, 1920, 1080);
			     lineChart.getData().addAll(series, series2, series3);
			       
			     stage.setScene(scene);
			     stage.show();
	}
}
