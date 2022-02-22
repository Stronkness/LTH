import java.util.HashMap;
import java.util.Scanner;

public class labb5 {
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        String letters = scan.nextLine();
        String[] parts = letters.split(" ");

        //Switched from ArrayList as the call "queryOne.charAt(i)" gave the ASCII value of the character
        HashMap<Character, Integer> chars = new HashMap<Character, Integer>();

        for(int i = 0; i < parts.length; i++) chars.put(parts[i].charAt(0),i);

        int[][] costs = new int[parts.length][parts.length];
        for (int i = 0; i < chars.size(); i++) {
            for (int j = 0; j < chars.size(); j++) {
                costs[i][j] = scan.nextInt();
            }
        }

        int queries = scan.nextInt();
        scan.nextLine();
        String[] DNA = new String[queries];
        int cost = -4; //Delta

        /*Begin the "algorithm", takes the two queries and splits them into two values.
        the loop goes through one line at a time and returns it into DNA with the backtrackingAlgorithm() */
        for(int i = 0; i < queries; i++){
            String queriesString = scan.nextLine();
            String[] queriesSeq = queriesString.split(" ");
            String queryOne = queriesSeq[0];
            String queryTwo = queriesSeq[1];

            //Contains the values of different positions
            int[][] optValues = new int[queryOne.length() + 1][queryTwo.length() + 1];

            //Base case, fill the rows at column position 0
            for(int j = 0; j <= queryOne.length(); j++){
                optValues[j][0] = cost*j;
            }
            //Base case, fill the columns at row position 0
            for(int k = 0; k <= queryTwo.length(); k++){
                optValues[0][k] = cost*k;
            }

            //Starts at 1 because the positions 0 in optValues are already occupied
            for (int l = 1; l <= queryOne.length(); l++) {
                for (int m = 1; m <= queryTwo.length(); m++) {

                    int match = optValues[l-1][m-1] + costs[chars.get(queryOne.charAt(l-1))][chars.get(queryTwo.charAt(m-1))];
                    int delete = optValues[l-1][m] + cost;
                    int insert = optValues[l][m-1] + cost;
                    optValues[l][m] = max(match, insert, delete);
                }
            }

            DNA[i] = backtrackingAlgorithm(queryOne, queryTwo, costs, cost, chars, optValues);

        }
        //Printer for the result
        for(String result : DNA) System.out.println(result);
    }

    private static String backtrackingAlgorithm(String queryOne, String queryTwo, int[][] costs, int cost, HashMap<Character, Integer> chars, int[][] optValues){
        StringBuilder alignmentA = new StringBuilder();
        StringBuilder alignmentB = new StringBuilder();
        int i = queryOne.length();
        int j = queryTwo.length();

        while(i > 0 && j > 0){
            if(i > 0 && j > 0 && (optValues[i][j] == (optValues[i-1][j-1] + costs[chars.get(queryOne.charAt(i-1))][chars.get(queryTwo.charAt(j-1))]))){
                alignmentA.append(queryOne.charAt(i-1));
                alignmentB.append(queryTwo.charAt(j-1));
                i -= 1;
                j -= 1;
            }else if(i > 0 && (optValues[i][j] == optValues[i-1][j] + cost)){
                alignmentA.append(queryOne.charAt(i-1));
                alignmentB.append("*");
                i -= 1;
            }else{
                alignmentA.append("*");
                alignmentB.append(queryTwo.charAt(j-1));
                j -= 1;
            }
        }

        while(i > 0) {
            alignmentA.append(queryOne.charAt(i-1));
            alignmentB.append("*");
            i--;
        }

        while(j > 0) {
            alignmentA.append("*");
            alignmentB.append(queryTwo.charAt(j-1));
            j -= 1;
        }

        String firstDNA = alignmentA.reverse().toString();
        String secondDNA = alignmentB.reverse().toString();

        return (firstDNA + " " + secondDNA);
    }



    private static int max(int first, int second, int third){
        int max = first;
        if (second > max) max = second;
        if (third > max) max = third;
        return max;
    }
}
