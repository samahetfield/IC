import java.util.Random;
import java.util.stream.IntStream;

public class NeuronalNetwork {
	
	private MNISTDatabase datos = new MNISTDatabase();
	private Cell cell[] = new Cell[10];
	private int images[][][];
	private int labels[];
	private int[] targetOutput;
	private int errCount = 0;
	
	/*
	 * Constructor de la clase Neuronal Network
	 * Inicializamos los atributos de cada neurona como son los pesos (rand), entrada y salida
	 */
	
	public NeuronalNetwork() {
		for(int i=0; i<10; i++) {
			cell[i] = new Cell();
			cell[i].nombre = i;
			for(int j=0; j<(28*28); j++) {
				cell[i].input[j]=0;
				cell[i].weight[j]= (double) (Math.random()*100)+1;
			}
			
			cell[i].output=0;
		}
	}
	
	/*
	 * Método que llamaremos para comenzar el entrenamiento
	 */
	public void trainingNetwork(int images[][][], int labels[]) {
		this.images = images;
		this.labels = labels;
		int pixel[][];
		int label_train;
		for(int i=0; i<labels.length; i++) { //Por cada imagen de entrada
			pixel = images[i];
			label_train = labels[i];			
			
			
			/*
			 * Cada imagen tendremos que analizarla por todas las neuronas
			 */
			for(int j=0; j<10; j++) { 
				trainCell(cell[j], labels[i], pixel);					
			}
			
			
			int predictedNum = getLayerPredictions(cell); // Obtenemos la predicción de nuestra red neuronal
			if(predictedNum != label_train) {
				errCount++;
			}
						
			//System.out.println("La red neuronal ha predicho un " + predictedNum + " y es un " + label_train);
		}
		
		int tamanio_muestras = labels.length;
		double succesRate = (errCount * 1.0000 / tamanio_muestras * 1.000)*100;
		
		System.out.println("El número de errores encontrados es de: " + errCount);
		System.out.println("El número de aciertos es de: " + (tamanio_muestras-errCount));
		
		System.out.println("La tasa de aciertos de esta red neuronal es del: " + (100-succesRate) + "%");
		System.out.println("La tasa de errores de esta red neuronal es del: " + succesRate + "%");
		
	}
	
	/*
	 * Mñetodo para comenzar el test de la red neuronal
	 */
	
	public void testNetwork(int images[][][], int labels[]) {
		this.images = images;
		this.labels = labels;
		errCount = 0;
		int pixel[][];
		int label_train;
		for(int i=0; i<labels.length; i++) { // Recorremos todas las imágenes de entrada
			pixel = images[i];
			label_train = labels[i];			
			
			/*
			 * Cada imagen tendremos que analizarla por todas las neuronas
			 */
			
			for(int j=0; j<10; j++) {
				testCell(cell[j], labels[i], pixel);					
			}
			
			
			int predictedNum = getLayerPredictions(cell); //Obtenemos la predición de la red neuronal
			if(predictedNum != label_train) {
				errCount++;
			}
						
			//System.out.println("La red neuronal ha predicho un " + predictedNum + " y es un " + label_train);
		}
		
		int tamanio_muestras = labels.length;
		double succesRate = (errCount * 1.0000 / tamanio_muestras * 1.000)*100;
		
		System.out.println("El número de errores encontrados es de: " + errCount);
		System.out.println("El número de aciertos es de: " + (tamanio_muestras-errCount));
		
		System.out.println("La tasa de aciertos de esta red neuronal es del: " + (100-succesRate) + "%");
		System.out.println("La tasa de errores de esta red neuronal es del: " + succesRate + "%");
		
	}
	
	
	public void trainCell(Cell c, int target, int img[][]) {
		setCellInput(c, img); //Establecemos la entrada de cada neurona
		calcCellOutput(c); //Calculamos si la neurona se activa o no
		
		updateCellWeights(c, target); //Actualizamos los pesos
	}
	
	public void testCell(Cell c, int target, int img[][]) {
		setCellInput(c, img); //Establecemos la entrada de la neurona
		calcCellOutput(c); //Calculamos la salida
	}
	
	/*
	 * La entrada de la neurona será la imagen que estamos analizando
	 */
	public void setCellInput(Cell c, int img[][]) {
		for(int i=0; i<28; i++) {
			for(int j=0; j<28; j++) {
				c.input[(28*i) + j] = img[i][j];
			}
		}

	}

	
	/*
	 * La salida se analizará sumando cada uno de los píxeles de la imagen multiplicado por su peso
	 * Si la suma es mayor a cero, diremos que la neurona se ha activado
	 */
	public void calcCellOutput(Cell c) {
		c.output=0;
		
		for(int i=0; i<(28*28); i++) {
			c.output += c.input[i] * c.weight[i];
		}
				
		if(c.output > 0) {
			c.activada = true;
		}
		else {
			c.activada=false;
		}
	}
	
	/*
	 * Método para actualizar los pesos
	 * Si la neurona se ha activado y no debería, tendremos que disminuir los pesos para que en la siguiente no se active
	 * En el caso de que la neurona no se active y debería tendremos que sumar los pesos para que en la siguiente sepa que debe activarse
	 */
	public void updateCellWeights(Cell c, double target) {		
		
		if(c.activada) {
			if(target != c.nombre ) {
				for(int i=0; i<(28*28); i++) {
					c.weight[i] = c.weight[i] + (c.input[i]*(-1));
				}
			}
		}
		else {
			if(c.nombre == target) {
				for(int i=0; i<(28*28); i++) {
					c.weight[i] = c.weight[i] + (c.input[i]*(1));
				}
			}
		}
	}
	
	
	/*
	 * Obtenemos la predicción de la red neuronal
	 * Para saber qué neurona se ha activado, obtendremos aquella cuya salida sea mayor
	 */
	public int getLayerPredictions(Cell c[]) {
		double maxOut = 0;
		int maxInd = 0;
		
		for(int i=0; i<10; i++) {
			if(c[i].output > maxOut && c[i].activada) {
				maxOut = c[i].output;
				maxInd = i;
			}
		}
		
		return maxInd;
	}

}
