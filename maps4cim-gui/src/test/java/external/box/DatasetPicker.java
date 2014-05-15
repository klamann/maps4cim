package external.box;

import java.awt.Choice;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;

class DatasetPicker
{
    public Choice dataset_choice;
    public URL code_base;

    Vector file_names;

    DatasetPicker(String file_name, URL url)
    {
        code_base = url;

        dataset_choice = new Choice();
        dataset_choice.setSize(125,15);

        file_names = new Vector(10, 10);

        read(file_name);
    }

    String nameToShow(String string)
    {
        int index = string.lastIndexOf('/');

        int end = string.length();

        return string.substring(index + 1, end);
    }

    String getItem(int index)
    {
        return "Datasets/" + dataset_choice.getItem(index);
    }

    public void read(String file_name)
    {
        try
        {
            URL url = new URL(code_base, file_name);
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String current = "";

            // while there is more text to read from br
            while (br.ready())
            {
                // read the next line from the file
                current = br.readLine();

                if(current != "")
                {
                    // add the file name to the list
                    file_names.add(nameToShow(current));
                }
            }

            // closes the buffered reader and the connected file.
            br.close();

            for(int i = 0; i < file_names.size(); i++)
            {
                dataset_choice.add((String)file_names.elementAt(i));
            }
        }
        catch(Exception e)
        {
            System.out.println("ERROR <DatasetPicker> --> file " + file_name + " was not found.");
        }
    }
}