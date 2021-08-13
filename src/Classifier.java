import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Classifier {

    private ArrayList<String[]> rows;
    private final int size;

    private final propTable[] table;
    private final int FOLD_K = 10;
    private final int testSize;
    private static final String dataPath = new File("data/house-votes-84.data").getAbsolutePath();

    public Classifier(){
        readData();
        size = rows.size();
        testSize = size/FOLD_K;
        table = new propTable[size];

        classify();
    }

    public void classify()
    {
        double sum = 0; // sum of the accuracies
        for(int i = 0 ; i < FOLD_K; i++)
        {
            int testStart = i*testSize;
            int testEnd = (i+1)*testSize-1;
            if(i == FOLD_K - 1) testEnd = size-1;

            teach(testStart, testEnd);
            double accuracy = test(testStart, testEnd);
            System.out.println((i+1) + " --> " + accuracy);

            sum+=accuracy;
        }
        sum /= FOLD_K;
        System.out.println("Average accuracy: " + sum);
    }

    private void teach(int testStart, int testEnd)
    {
        for(int i = 0; i < 17; i++)
        {
            // variables for counting the answers for each attribute
            int[] yes = new int[]{0,0}; // first for republicans, second for democrats
            int[] no = new int[]{0,0};
            int[] other = new int[]{0,0};

            // filling the prop tables
            for(int j = 0; j < size; j++)
            {
                if(j < testStart || j > testEnd) {
                    if(rows.get(j)[i].equals("y"))
                    {
                        if(rows.get(j)[0].equals("republican"))
                        {
                            yes[0]++;
                        }
                        else if(rows.get(j)[0].equals("democrat"))
                        {
                            yes[1]++;
                        }
                    }
                    else if(rows.get(j)[i].equals("n"))
                    {
                        if(rows.get(j)[0].equals("republican"))
                        {
                            no[0]++;
                        }
                        else if(rows.get(j)[0].equals("democrat"))
                        {
                            no[1]++;
                        }
                    }
                    else // answer is "?"
                    {
                        if(rows.get(j)[0].equals("republican"))
                        {
                            other[0]++;
                        }
                        else if(rows.get(j)[0].equals("democrat"))
                        {
                            other[1]++;
                        }
                    }
                }
            }
            table[i] = new propTable(yes, no, other);
        }
    }

    private double test(int testStart, int testEnd)
    {
        int correct = 0;
        int wrong = 0;

        for(int i = testStart; i <= testEnd; i++)
        {
            String[] test = rows.get(i);
            int[] intTest = new int[test.length]; // the test row in int to match indexes in the prop table
            for(int j = 0; j < test.length; j++)
            {
                if(test[j].equals("y"))
                {
                    intTest[j] = 0;
                }
                else if(test[j].equals("n"))
                {
                    intTest[j] = 1;
                }
                else
                {
                    intTest[j] = 2;
                }
            }

            double rep = 0; // chance for republican
            double dem = 0; // chance for democrat

            for(int j = 0; j < test.length; j++)
            {
                // use logarithms to avoid multiplication by zero ( log(a)*log(b)
                rep += Math.log(table[j].getTable()[intTest[j]][0]);
                dem += Math.log(table[j].getTable()[intTest[j]][1]);
            }

            String result;
            if(rep > dem) result = "republican";
            else result = "democrat";

            if(result.equals(test[0])) correct++;
            else wrong++;
        }
        return (double) correct/(correct + wrong);
    }

    private void readData()
    {
        rows = new ArrayList<>();
        try
        {
            // read data from file
            BufferedReader br = new BufferedReader(new FileReader(dataPath));

            String line;
            while ((line = br.readLine()) != null) {

                // split by commas
                String[] row = line.split(",");
                rows.add(row);
            }
        }
        catch (Exception e){e.printStackTrace();}
    }

}
