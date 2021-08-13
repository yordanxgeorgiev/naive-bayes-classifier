public class propTable {
    private double[][] table;

    public propTable()
    {
        table = new double[3][2];
    }

    public propTable(int[] yes, int[] no, int[] other)
    {
        table = new double[3][2];
        table[0][0] = (double) yes[0]/(yes[0] + yes[1]);
        table[0][1] = (double) yes[1]/(yes[0] + yes[1]);
        table[1][0] = (double) no[0]/(no[0] + no[1]);
        table[1][1] = (double) no[1]/(no[0] + no[1]);
        table[2][0] = (double) other[0]/(other[0] + other[1]);
        table[2][1] = (double) other[1]/(other[0] + other[1]);
    }

    public double[][] getTable() {
        return table;
    }
}
