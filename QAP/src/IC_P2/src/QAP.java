import java.util.ArrayList;
import java.util.Collections;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import javax.swing.JFrame;

import javafx.util.Pair;

public class QAP {

	private int n;
	private ArrayList<ArrayList<Integer>> P = new ArrayList();
	private ArrayList<Integer> costos_Lamarck = new ArrayList();
	private ArrayList<ArrayList<Integer>> matriz_distancias;
	private ArrayList<ArrayList<Integer>> matriz_flujo;
	private int contador = 0;
	private int indice = 0;
	private int i = 0;
	private int generacion_indice = 1;
	
	
	private ArrayList<Integer> individuoLamarck = new ArrayList();
	private ArrayList<Integer> individuoBaldwin = new ArrayList();
	private ArrayList<Integer> individuoClassic = new ArrayList();
	
	
	/**
	 * @return array con los fitness de cada generación
	 */
	public ArrayList<Integer> getCostesClassic(){
		return individuoClassic;
	}
	
	/**
	 * @return array con los fitness de cada generación
	 */
	public ArrayList<Integer> getCostesLamarck(){
		return individuoLamarck;
	}
	
	
	/**
	 * @return array con los fitness de cada generación
	 */
	public ArrayList<Integer> getCostesBaldwin(){
		return individuoBaldwin;
	}
	
	
	/**
	 * @param f
	 * @param d
	 */
	public QAP(ArrayList<ArrayList<Integer>> f, ArrayList<ArrayList<Integer>> d) {
		matriz_flujo = f;
		matriz_distancias = d;
		
		n = matriz_flujo.size();
	}
	
	
	/**
	 * @param max_eval
	 * @return solución de la última generación y su coste
	 */
	public Pair<ArrayList<Integer>, Integer> solAG(int max_eval){
		int tamanio_poblacion = 50;
		float prob_cruce = (float) 0.5;
		float prob_mutacion = (float) 0.001;
		
		int n_esperado_cruces = Math.round(prob_cruce * (tamanio_poblacion/2));
		int n_esperado_mutaciones = Math.round(prob_mutacion * tamanio_poblacion * n  );
		ArrayList<ArrayList<Integer>> P_actual = new ArrayList();
		ArrayList<Integer> mejor_solucion = new ArrayList();
		ArrayList<Integer> peor_solucion = new ArrayList();
		
		for(int i=0; i<tamanio_poblacion; i++) {
			P_actual.add(new ArrayList());
		}
		
		for(int i=0; i<n; i++) {
			mejor_solucion.add(0);
			peor_solucion.add(0);
		}
		
		int menor_costo = 0, mayor_costo=0;
		
	
		P_actual = generaPoblacionInicial(P_actual);
		mejor_solucion = mejorSolucion(P_actual, indice, contador); //obtenemos la solución con menor coste
		menor_costo = evaluaSolucion(mejor_solucion); // Obtenemos el coste de la mejor solución
		contador++;
		
			ArrayList<ArrayList<Integer>> P_padres = new ArrayList();
			ArrayList<ArrayList<Integer>> P_intermedia = new ArrayList();
			ArrayList<ArrayList<Integer>> P_hijos = new ArrayList();
			
			for(int k=0; k<tamanio_poblacion; k++) {
				P_padres.add(new ArrayList());
				P_intermedia.add(new ArrayList());
				P_hijos.add(new ArrayList());

			}
			
			while(contador < max_eval) {
				P_padres = seleccionPadres(P_actual, contador);  //Seleccionamos los padres
				P_intermedia = crucePadres(P_padres, n_esperado_cruces); //Hacemos el cruce entre los padres
				P_hijos = mutacion(P_intermedia, n_esperado_mutaciones); // 
				P_actual = new ArrayList<>(P_hijos);
				
				peor_solucion = peorSolucion(P_actual, i, contador);
				mayor_costo = evaluaSolucion(peor_solucion);
							
				contador++;
				
				// Si la mejor solución de la generación anterior tiene menor coste que la peor de esta generación se sustituye
				if(menor_costo < mayor_costo) {
					P_actual.set(i, mejor_solucion);
				}
				
				System.out.println("Fitness de generación "+ generacion_indice +" es ----> "+menor_costo);

				individuoClassic.add(menor_costo);
				

				//Calculamos mejor solución para esta generación
				mejor_solucion = mejorSolucion(P_actual, indice, contador);
				menor_costo = evaluaSolucion(mejor_solucion);
				contador++;
				generacion_indice++;
			}		
			
			
		return new Pair<ArrayList<Integer>, Integer> (mejor_solucion, menor_costo);
	}

	/**
	 * @param p_actual
	 * @param indice
	 * @param contador
	 * @return peor solución de la población
	 */
	private ArrayList<Integer> peorSolucion(ArrayList<ArrayList<Integer>> p_actual, int indice, int contador) {
		ArrayList<Integer> resultado = new ArrayList(n);
		resultado = new ArrayList<>(p_actual.get(0));
		
		int peor_costo = evaluaSolucion(resultado), costo_actual=0;
		contador++;
		
		for(int i=0; i<p_actual.size(); i++) {
			costo_actual = evaluaSolucion(p_actual.get(i));
			contador++;
			
			if(costo_actual > peor_costo) {
				resultado = new ArrayList<>(p_actual.get(i));
				peor_costo = costo_actual;
				indice = i;
			}
		}
		
		this.i = indice;
		
		
		return resultado;
	}

	/**
	 * @param p_intermedia
	 * @param n_esperado_mutaciones
	 * @return población mutada
	 */
	private ArrayList<ArrayList<Integer>> mutacion(ArrayList<ArrayList<Integer>> p_intermedia,
			int n_esperado_mutaciones) {
		
		ArrayList<ArrayList<Integer>> resultado = new ArrayList<>(p_intermedia);
		int aleatorio1, aleatorio2, aleatorio3;
		
		for(int i=0; i<n_esperado_mutaciones; i++) {
			aleatorio1 = (int) (Math.random() * p_intermedia.size());
			aleatorio2 = (int) (Math.random() * n);
			aleatorio3 = (int) (Math.random() * n);
			
			// El individuo con índice "aleatorio1" se va a mutar
			resultado.set(aleatorio1, generaVecino(resultado.get(aleatorio1), aleatorio2, aleatorio3));
		}
		
		return resultado;
		
	}

	/**
	 * @param p_padres
	 * @param n_esperado_cruces
	 * @return Resultado del cruce de los padres
	 */
	private ArrayList<ArrayList<Integer>> crucePadres(ArrayList<ArrayList<Integer>> p_padres, int n_esperado_cruces) {
		
		ArrayList<ArrayList<Integer>> resultado = new ArrayList();
		int k = 0;
		
		for(int i=0; i<p_padres.size(); i++) {
			resultado.add(new ArrayList());
		}
		
		
			for(int i=0; i<n_esperado_cruces; i+=2) {
				resultado.set(k, crucePosicion(p_padres.get(i), p_padres.get(i+1))); //Obtenemos un individuo como cruce de ambos padres
				k++;
				
				//Seleccionamos al padre mejor
				if(evaluaSolucion(p_padres.get(i)) < evaluaSolucion(p_padres.get(i+1))) {
					resultado.set(k, p_padres.get(i));
				}
				else {
					resultado.set(k, p_padres.get(i+1));
				}
				
				k++;
			} 
		for(int i=k; i < p_padres.size(); i++) {
			resultado.set(i, p_padres.get(i));
		}
		
		return resultado;
	}

	/**
	 * @param array1
	 * @param array2
	 * @return Individuo cruzado por ambos padres
	 */
	private ArrayList<Integer> crucePosicion(ArrayList<Integer> array1, ArrayList<Integer> array2) {
		
		ArrayList<Integer> resultado = new ArrayList();
		ArrayList<Integer> aux = new ArrayList();
		ArrayList<Boolean> coinciden = new ArrayList();
		
		for(int i=0; i<n; i++) {
			resultado.add(0);
			coinciden.add(false);
		}
		
		// Si ambos coinciden se añade directamente al resultado, en la posición que está
		// En caso contrario se añade a un auxiliar que posteriormente reordenamos
		for(int i=0; i<n; i++) {
			if(array1.get(i) == array2.get(i)) {
				resultado.set(i, array1.get(i));
				coinciden.set(i, true);
			}
			else {
				aux.add(array1.get(i));
			}
		}
		
		Collections.shuffle(aux);
		int i=0;
		
		//Añadimos a resultado los valores que no coincidían
		for(int j=0; j<n; j++) {
			if(!coinciden.get(j)) {
				resultado.set(j, aux.get(i));
				i++;
			}
		}
		
		return resultado;
		
	}

	/**
	 * @param p_actual que es la población que tenemos
	 * @param contador
	 * @return Devolvemos los padres seleccionados
	 */
	private ArrayList<ArrayList<Integer>> seleccionPadres(ArrayList<ArrayList<Integer>> p_actual, int contador) {
		ArrayList<ArrayList<Integer>> resultado = new ArrayList<>(p_actual);
		int aleatorio1, aleatorio2, aleatorio3;
		
		for(int i=0; i < contador; i++) {
			aleatorio1 = (int) (Math.random() * p_actual.size());
			aleatorio2 = (int) (Math.random() * n);
			aleatorio3 = (int) (Math.random() * n);
			
			resultado.set(aleatorio1, generaVecino(resultado.get(aleatorio1), aleatorio2, aleatorio3));
		}
		
		return resultado;
		
	}

	/**
	 * @param arrayList
	 * @param aleatorio2
	 * @param aleatorio3
	 * @return Modificamos el individuo pasado intercambiando los valores de los índices pasados como parámetros
	 */
	private ArrayList<Integer> generaVecino(ArrayList<Integer> arrayList, int aleatorio2, int aleatorio3) {
		ArrayList<Integer> resultado = new ArrayList(n);
		int al1 = arrayList.get(aleatorio3);
		int al2 =  arrayList.get(aleatorio2);
		resultado = new ArrayList<>(arrayList);
		
		resultado.set(aleatorio2, al1);
		resultado.set(aleatorio3, al2);
		
		return resultado;
	}

	/**
	 * @param mejor_solucion2
	 * @return costo de la solución
	 */
	private int evaluaSolucion(ArrayList<Integer> mejor_solucion2) {
		int costo = 0;
		
		//Evaluamos la solución teniendo en cuenta las matrices de distancias y flujo y la función de coste
		
		for(int i=0; i<n; i++) {
			for(int j=0; j<n; j++) {
				int ind1 = mejor_solucion2.get(i);
				int ind2 = mejor_solucion2.get(j);
				costo += ((matriz_flujo.get(i)).get(j)) * ((matriz_distancias.get(ind1)).get(ind2));
			}
		}
		
		return costo;
	}

	/**
	 * @param p_actual
	 * @param indice
	 * @param contador
	 * @return Obtenemos la solución con menor coste
	 */
	private ArrayList<Integer> mejorSolucion(ArrayList<ArrayList<Integer>> p_actual, int indice, int contador) {
		ArrayList<Integer> resultado = new ArrayList();
		resultado = new ArrayList<>(p_actual.get(0));
		int mejor_costo = evaluaSolucion(resultado), costo_actual=0;
		this.contador++;
		
		//Evaluamos cada individuo y nos quedamos con el que tenga menor coste
		for(int i=1; i< p_actual.size(); i++) {
			costo_actual = evaluaSolucion(p_actual.get(i));
			this.contador++;
			
			if(costo_actual < mejor_costo) {
				resultado = new ArrayList<>(p_actual.get(i));
				mejor_costo = costo_actual;
				indice = i;
			}
		}
		
		
		return resultado;
		
	}


	/**
	 * @param p_actual
	 * @return población inicial
	 */
	private ArrayList<ArrayList<Integer>> generaPoblacionInicial(ArrayList<ArrayList<Integer>> p_actual) {
		ArrayList<ArrayList<Integer>> salida;
		for(int i=0; i < p_actual.size(); i++) {
			P.add(generaSolucionAleatoria());
		}
		
		salida = P;
		
		return salida;
	}

	/**
	 * @return Array con una solución aleatoria
	 */
	private ArrayList<Integer> generaSolucionAleatoria() {
		ArrayList<Integer> resultado = new ArrayList();
		
		for(int i=0; i<n; i++) {
			resultado.add(i);
			
			Collections.shuffle(resultado);
		}
		
		return resultado;
	}
	
	
	/**
	 * @param max_eval
	 * @return par con mejor solución y su coste
	 */
	public Pair<ArrayList<Integer>, Integer> solucionLamarck(int max_eval){
		this.contador = 0;
		this.i=0;
		this.generacion_indice = 0;
		int tamanio_poblacion = 50;
		float prob_cruce = (float) 0.5;
		float prob_mutacion = (float) 0.001;
		
		int n_esperado_cruces = Math.round(prob_cruce * (tamanio_poblacion/2));
		int n_esperado_mutaciones = Math.round(prob_mutacion * tamanio_poblacion * n  );
		ArrayList<ArrayList<Integer>> P_actual = new ArrayList();
		ArrayList<Integer> mejor_solucion = new ArrayList();
		ArrayList<Integer> peor_solucion = new ArrayList();
		
		for(int i=0; i<tamanio_poblacion; i++) {
			P_actual.add(new ArrayList());
		}
		
		for(int i=0; i<n; i++) {
			mejor_solucion.add(0);
			peor_solucion.add(0);
		}
		
		int menor_costo = 0, mayor_costo=0;
		
	
		P_actual = generaPoblacionInicial(P_actual);
		mejor_solucion = mejorSolucionLamarck(P_actual, indice, contador);
		menor_costo = evaluaSolucion(mejor_solucion);
		contador++;
		
			ArrayList<ArrayList<Integer>> P_padres = new ArrayList();
			ArrayList<ArrayList<Integer>> P_intermedia = new ArrayList();
			ArrayList<ArrayList<Integer>> P_hijos = new ArrayList();
			
			for(int k=0; k<tamanio_poblacion; k++) {
				P_padres.add(new ArrayList());
				P_intermedia.add(new ArrayList());
				P_hijos.add(new ArrayList());

			}
					
			while(contador < max_eval) {
				P_padres = seleccionPadres(P_actual, contador);
				P_intermedia = crucePadres(P_padres, n_esperado_cruces);
				P_hijos = mutacion(P_intermedia, n_esperado_mutaciones);
				P_actual = new ArrayList<>(P_hijos);
				
				peor_solucion = peorSolucion(P_actual, i, contador);
				mayor_costo = evaluaSolucion(peor_solucion);
				contador++;
				
				if(menor_costo < mayor_costo) {
					P_actual.set(i, mejor_solucion);
				}
				
				System.out.println("Fitness de generación "+ generacion_indice +" es ----> "+menor_costo);
				individuoLamarck.add(menor_costo);	
				
				
				mejor_solucion = mejorSolucionLamarck(P_actual, indice, contador);
				menor_costo = evaluaSolucion(mejor_solucion);
				contador++;
				generacion_indice++;
			}		


		return new Pair<ArrayList<Integer>, Integer> (mejor_solucion, menor_costo);	
 	
	}


	/**
	 * @param p_actual
	 * @param indice2
	 * @param contador2
	 * @return
	 */
	private ArrayList<Integer> mejorSolucionLamarck(ArrayList<ArrayList<Integer>> p_actual, int indice2,
			int contador2) {

		ArrayList<Integer> aux = new ArrayList();
		ArrayList<Integer> resultado = new ArrayList();
		resultado = new ArrayList<>(p_actual.get(0));
		int mejor_costo = evaluaSolucion(resultado), costo_actual=0;
		int cont = 0;
		this.contador++;
		
		for(int i=0; i< p_actual.size(); i++) {
			aux = new ArrayList<>(p_actual.get(i));
			costo_actual = evaluaSolucion(p_actual.get(i));
			this.contador++;
			
			while(costo_actual > mejor_costo && cont < resultado.size()) {
				aux = new ArrayList<>(p_actual.get(i) );
				Collections.shuffle(aux);
				
				costo_actual = evaluaSolucion(aux);
				cont++;
			}
			
			if(costo_actual < mejor_costo) {
				resultado = new ArrayList<>(aux);
				mejor_costo = costo_actual;
				indice = i;
			}
		}
		
		
		return resultado;	
	}
	
	
	/**
	 * @param max_eval
	 * @return
	 */
	public Pair<ArrayList<Integer>, Integer> solucionBaldwin(int max_eval){
		this.contador = 0;
		this.i=0;
		this.generacion_indice = 0;
		int tamanio_poblacion =50;
		float prob_cruce = (float) 0.5;
		float prob_mutacion = (float) 0.001;
		
		int n_esperado_cruces = Math.round(prob_cruce * (tamanio_poblacion/2));
		int n_esperado_mutaciones = Math.round(prob_mutacion * tamanio_poblacion * n  );
		ArrayList<ArrayList<Integer>> P_actual = new ArrayList();
		ArrayList<Integer> mejor_solucion = new ArrayList();
		ArrayList<Integer> peor_solucion = new ArrayList();
		
		for(int i=0; i<tamanio_poblacion; i++) {
			P_actual.add(new ArrayList());
		}
		
		for(int i=0; i<n; i++) {
			mejor_solucion.add(0);
			peor_solucion.add(0);
		}
		
		int menor_costo = 0, mayor_costo=0;
		
	
		P_actual = generaPoblacionInicial(P_actual);
		mejor_solucion = mejorSolucionBaldwin(P_actual, indice, contador);
		menor_costo = evaluaSolucion(mejor_solucion);
		contador++;
		
			ArrayList<ArrayList<Integer>> P_padres = new ArrayList();
			ArrayList<ArrayList<Integer>> P_intermedia = new ArrayList();
			ArrayList<ArrayList<Integer>> P_hijos = new ArrayList();
			
			for(int k=0; k<tamanio_poblacion; k++) {
				P_padres.add(new ArrayList());
				P_intermedia.add(new ArrayList());
				P_hijos.add(new ArrayList());

			}
			
	
			while(contador < max_eval) {
				P_padres = seleccionPadres(P_actual, contador);
				P_intermedia = crucePadres(P_padres, n_esperado_cruces);
				P_hijos = mutacion(P_intermedia, n_esperado_mutaciones);
				P_actual = new ArrayList<>(P_hijos);
				
				peor_solucion = peorSolucion(P_actual, i, contador);
				mayor_costo = evaluaSolucion(peor_solucion);
				contador++;
				
				if(menor_costo < mayor_costo) {
					P_actual.set(i, mejor_solucion);
				}
				
				System.out.println("Fitness de generación "+ generacion_indice +" es ----> "+menor_costo);
				individuoBaldwin.add(menor_costo);
				
				
				mejor_solucion = mejorSolucionBaldwin(P_actual, indice, contador);
				menor_costo = evaluaSolucion(mejor_solucion);
				contador++;
				generacion_indice++;
			}		

			
		return new Pair<ArrayList<Integer>, Integer> (mejor_solucion, menor_costo);	
 	
	}


	/**
	 * @param p_actual
	 * @param indice2
	 * @param contador2
	 * @return mejor solución sin incluir mejoras
	 */
	private ArrayList<Integer> mejorSolucionBaldwin(ArrayList<ArrayList<Integer>> p_actual, int indice2,
			int contador2) {

		ArrayList<Integer> resultado = new ArrayList();
		resultado = new ArrayList<>(p_actual.get(0));
		int mejor_costo = evaluaSolucion(resultado), costo_actual=0;
		int cont = 0;
		int index = 0;
		this.contador++;
		
		for(int i=1; i< p_actual.size(); i++) {
			costo_actual = evaluaSolucion(p_actual.get(i));
			this.contador++;
			
			while(costo_actual > mejor_costo && cont < resultado.size()) {
				resultado = new ArrayList<>(p_actual.get(i) );
				Collections.shuffle(resultado);
				
				costo_actual = evaluaSolucion(resultado);
				cont++;
			}
			
			if(costo_actual < mejor_costo) {
				mejor_costo = costo_actual;
				index = i;
			}
		}
		
		
		return p_actual.get(index);			
	}
	
	
}
