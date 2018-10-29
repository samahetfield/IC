import java.io.IOException;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		int images[][][];
		images = MNISTDatabase.readImages("E:\\Máster Ingeniería Informática\\IC\\train-images-idx3-ubyte.gz");
		
		int labels[];
		labels = MNISTDatabase.readLabels("E:\\Máster Ingeniería Informática\\IC\\train-labels-idx1-ubyte.gz");
		System.out.println("Comenzando el entrenamiento .....");
		NeuronalNetwork n = new NeuronalNetwork();
		n.trainingNetwork(images, labels);
		
		
		images = MNISTDatabase.readImages("E:\\Máster Ingeniería Informática\\IC\\t10k-images-idx3-ubyte.gz");
		
		labels = MNISTDatabase.readLabels("E:\\Máster Ingeniería Informática\\IC\\t10k-labels-idx1-ubyte.gz");
		System.out.println("Comenzando el test .....");
		n.testNetwork(images, labels);
		
	}
	
}
