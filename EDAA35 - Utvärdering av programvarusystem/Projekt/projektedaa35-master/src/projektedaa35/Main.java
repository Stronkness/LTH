package projektedaa35;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Main {

	public static void main(String[] args) throws IOException {
		
		// Okej bois här är koden, den är inte väldigt välgjord men den gör jobbet och skriver ut till filer
		// Notera att txt filerna nog inte dyker upp i eclipse så öppna bara mappen i explorer för att se dem
		// Programmet är till skillnad mot labb5 mer anpassad att köras i eclipse och inte i terminalen
		// Så det kommer att räcka med att man läser filerna i R och inte kör själva programmet där. 
		// Gjorde detta då det knappt gör någon skillnad och R är AIDS
		
		// Viktiga parametrar är den aktuellt valda filen (kan bara köra en fil i taget)
		// Notera och se för adding metoden i Tester att den har O(timesToTest * element i fil) i tidskomplexitet
		// Så om ni kör timesToTest = 1000 och indata med 10^6 kan det nog ta lite lång tid
		// Alltså: Det kan bli aktuellt att ni anpassar antalet gånger ni kör testerna beroende på hur stor filen är 
		
		// Utfilerna är på samma format som i labb5
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
