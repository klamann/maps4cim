package external.box;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

class DataReader
{
    final int MAX_SIZE = 1000;
    final int BYTE_SIZE = 16;

    String[][] data;

    int numPoints;
    int numFields;

    URL codeBase;

    String[] variable_names;

    DataReader(URL url)
    {
        data = new String[MAX_SIZE][MAX_SIZE];

        numPoints = 0;
        numFields = 0;

        codeBase = url;
    }

    void readFile(String file_name)
    {
        try
        {
            URL url = new URL(codeBase, file_name);
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String s = new String("");

            int c;

            int i;
            int j;

            do
            {
                s = br.readLine();

                if(s.startsWith("Variable Names:"))
                {
                    s = s.replaceFirst("Variable Names: ", "");

                    variable_names = s.split(", ");
                }
            }
            while(! s.equals("Data:"));

            i = 0;

            while ((s = br.readLine()) != null)
            {
                if(s.length() > 0)
                {
                    String[] values = s.split(" ");

                    for (j = 0; j < values.length; j++)
                    {
                        data[i][j] = values[j];
                    }

                    if(i == 0)
                    {
                        numFields = values.length;
                    }

                    i++;
                }
            }

            numPoints = i;

            br.close();
        }
        catch(Exception e)
        {
            System.out.println("ERROR <DataReader> --> " + e);
        }
    }

    void printData()
    {
        for(int i = 0; i < numPoints; i++)
        {
            System.out.print(i + " : ");

            for(int j = 0; j < numFields; j++)
            {
                System.out.print(data[i][j] + " ");
            }

            System.out.println();
        }
    }

    int[] getDimension()
    {
        int dimension[] = new int[2];

        dimension[0] = numPoints;
        dimension[1] = numFields;

        return dimension;
    }

    String[] getVariableNames()
    {
        return variable_names;
    }

    String[][] getDataArray()
    {
        String data_copy[][] = new String[numPoints][numFields];

        // System.out.println("numFields = " + numFields);
        // System.out.println("numPoints = " + numPoints);

        for(int i = 0; i < numPoints; i++)
        {
            for(int j = 0; j < numFields; j++)
            {
                data_copy[i][j] = data[i][j];
            }
        }

        return data_copy;
    }

    public boolean isCategorical(int column_index)
    {
        try
        {
            double check = Double.parseDouble(data[0][column_index-1]);

            return false;
        }
        catch(NumberFormatException nfe)
        {
            return true;
        }
    }


}