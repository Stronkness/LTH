package projektedaa35;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Main {

	public static void main(String[] args) throws IOException {
		
		// Okej bois h�r �r koden, den �r inte v�ldigt v�lgjord men den g�r jobbet och skriver ut till filer
		// Notera att txt filerna nog inte dyker upp i eclipse s� �ppna bara mappen i explorer f�r att se dem
		// Programmet �r till skillnad mot labb5 mer anpassad att k�ras i eclipse och inte i terminalen
		// S� det kommer att r�cka med att man l�ser filerna i R och inte k�r sj�lva programmet d�r. 
		// Gjorde detta d� det knappt g�r n�gon skillnad och R �r AIDS
		
		// Viktiga parametrar �r den aktuellt valda filen (kan bara k�ra en fil i taget)
		// Notera och se f�r adding metoden i Tester att den har O(timesToTest * element i fil) i tidskomplexitet
		// S� om ni k�r timesToTest = 1000 och indata med 10^6 kan det nog ta lite l�ng tid
		// Allts�: Det kan bli aktuellt att ni anpassar antalet g�nger ni k�r testerna beroende p� hur stor filen �r 
		
		// Utfilerna �r p� samma format som i labb5
		String f1Name = "indata10000.txt";
		String f2Name = "indata100000.txt";
		String f3Name = "indata1000000.txt";
		
		File actual = new File(f1Name);
		int timesToTest = 30; 
		
		System.out.println("running");
		
		
		// tar in N, vilken fil vi valt och om vi vill ha en header (Man hade det i labb5)
		Tester tester = new Tester(timesToTest, actual, true);
		
		tester.testAdding();
		tester.testSearching();
				
		System.out.println("completed");
	}

}
