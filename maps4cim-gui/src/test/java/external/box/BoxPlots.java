package external.box;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.net.URL;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JApplet;
import javax.swing.JPanel;

/**
 * App: http://www.stat.ubc.ca/~ruben/ILAS_2009/ilas_applets/boxplots/BoxPlots.htm
 * Source: http://www.stat.ubc.ca/~ruben/ILAS_2009/ilas_applets/boxplots/
 *
 */
public class BoxPlots extends JApplet implements ComponentListener
{
    public BoxPlots() {
    }
    String boxplot_file_name;
    BoxPlot boxplot;

    @Override
    public void init()
    {
        boxplot_file_name = getParameter("boxplot_file_name");

        getContentPane().setLayout(new BorderLayout());

        boxplot = new BoxPlot(getWidth(), getHeight(), getCodeBase(), boxplot_file_name);

        getContentPane().add(boxplot);

        addComponentListener(this);
    }

    public class BoxPlot extends JPanel implements ItemListener
    {
        String file_name;
        URL codeBase;
        String[] variable_names;
        DataBank dataBank;

        int plot_mode;
        final int PLOT_BY_CATEGORY  = 0;
        final int PLOT_ALL_DATA     = 1;
        final int MAX_NUM_CATEGORIES = 3;

        MainPanel mainPanel;

        final Color backgroundColor = Color.white;
        BasicStroke bs;
        GradientPaint redtowhite;

        TextArea dataTextArea;

        Button update_button;

        Color BUTTON_COLOR = new Color(125, 125, 200);

        SummaryPanel summaryPanel;

        DatasetPicker data_picker;

        int width;
        int height;

        int GAP = 10;

        int data_text_area_width;
        int data_text_area_height;
        int data_text_area_x;
        int data_text_area_y;

        int main_panel_width;
        int main_panel_height;
        int main_panel_x;
        int main_panel_y;

        int summary_panel_width;
        int summary_panel_height;
        int summary_panel_x;
        int summary_panel_y;

        int update_button_width;
        int update_button_height;
        int update_button_x;
        int update_button_y;

        Font font_label = new Font("SansSerif",Font.PLAIN,10);
        FontMetrics fm;

        Color textBackgroundColor = new Color(200,200,200);

        DecimalFormat df;

        BoxPlot( int w, int h, URL url, String file)
        {
            this.width  = w;
            this.height = h;

            main_panel_width  = w - 2*GAP;
            main_panel_height = (int)(h*0.70);
            main_panel_x      = GAP;
            main_panel_y      = GAP;

            data_text_area_width  = (int)((w - 2*GAP)*0.25);
            data_text_area_height = height - 3*GAP - main_panel_height;
            data_text_area_x      = GAP;
            data_text_area_y      = main_panel_y + main_panel_height + GAP;

            update_button_width  = 125;
            update_button_height = 20;
            update_button_x      = data_text_area_x + data_text_area_width + GAP;
            update_button_y      = data_text_area_y;

            summary_panel_width  = width - 3*GAP - data_text_area_width;
            summary_panel_height = data_text_area_height - update_button_height - GAP;
            summary_panel_x      = update_button_x;
            summary_panel_y      = update_button_y + update_button_height + GAP;

            codeBase = url;

            file_name = file;

            data_picker = new DatasetPicker(file_name, codeBase);

            data_picker.dataset_choice.addItemListener(this);
            data_picker.dataset_choice.setBackground(new Color(200, 200, 250));
            data_picker.dataset_choice.setLocation(width - GAP - 125, update_button_y);

            dataTextArea = new TextArea();
            dataTextArea.setSize(data_text_area_width, data_text_area_height);
            dataTextArea.setLocation(data_text_area_x,data_text_area_y);
            dataTextArea.setColumns(2);

            // data_picker.dataset_choice.select(0);
            readFile(data_picker.getItem(0));

            mainPanel = new MainPanel();
            mainPanel.setSize(main_panel_width,main_panel_height);
            mainPanel.setLocation(main_panel_x,main_panel_y);

            update_button = new Button("update box plot");
            update_button.setBackground(BUTTON_COLOR);
            update_button.addActionListener(new ButtonListener());
            update_button.setSize(update_button_width,update_button_height);
            update_button.setLocation(update_button_x, update_button_y);

            summaryPanel = new SummaryPanel(summary_panel_width, summary_panel_height);
            summaryPanel.setLocation(summary_panel_x,summary_panel_y);

            JPanel displayPanel = new JPanel();
            displayPanel.setLayout(null);

            setLayout(new BorderLayout());

            displayPanel.add(mainPanel);
            displayPanel.add(dataTextArea);
            displayPanel.add(update_button);
            displayPanel.add(data_picker.dataset_choice);
            displayPanel.add(summaryPanel);
            displayPanel.setBackground(backgroundColor);

            this.add(displayPanel, BorderLayout.CENTER);
        }

        public void readFile(String file)
        {
            DataReader data_reader = new DataReader(codeBase);

            data_reader.readFile(file);

            variable_names = data_reader.getVariableNames();

            String[][] data = data_reader.getDataArray();

            dataBank = new DataBank(data);

            placeInTextArea(data);
        }

        public void reset()
        {
            DataReader data_reader = new DataReader(codeBase);

            data_picker.dataset_choice.select(0);
            readFile(data_picker.getItem(0));
        }

        public class DataBank
        {
            ExperimentalUnit[] stored_data;
            Hashtable summary_hash;

            String keys[];

            double minimum;
            double maximum;

            String[][] raw_data;

            DataBank(String[][] data_matrix)
            {
                raw_data = data_matrix;

                double value;

                stored_data = new ExperimentalUnit[data_matrix.length];

                for(int i = 0; i < data_matrix.length; i++)
                {
                    try
                    {
                        value = Double.parseDouble(data_matrix[i][0]);

                        stored_data[i] = new ExperimentalUnit(value, data_matrix[i][1]);
                    }
                    catch(NumberFormatException nfe)
                    {
                        System.out.println("ERROR <BoxPlot>: " + data_matrix[i][0] + "is not a number!");
                    }
                }

                summary_hash = new Hashtable();

                Hashtable possible_values = new Hashtable();

                for(int i = 0; i < stored_data.length; i++)
                {
                    possible_values.put(stored_data[i].category, stored_data[i]);
                }

                Enumeration enumer = possible_values.keys();

                keys = new String[possible_values.size()];

                int i = 0;

                while(enumer.hasMoreElements())
                {
                    keys[i] = (String)enumer.nextElement();

                    i++;
                }

                // sorting alphabetically
                Arrays.sort(keys, new StringComparator());

                String[] temp_keys = new String[keys.length + 1];

                for(i = 0; i < keys.length; i++)
                {
                    temp_keys[i] = keys[i];
                }

                temp_keys[i] = "all";

                keys = temp_keys;

                Vector vector = new Vector();;

                for(i = 0; i < keys.length; i++)
                {
                    String key = keys[i];

                    vector.clear();

                    for(int j = 0; j < stored_data.length; j++)
                    {
                        if(stored_data[j].category.equals(key))
                        {
                            vector.add(new Double(stored_data[j].value));
                        }
                        else if(key == "all")
                        {
                            vector.add(new Double(stored_data[j].value));
                        }
                    }

                    double[] item = new double[vector.size()];

                    for(int k = 0; k < item.length; k++)
                    {
                        item[k] = ((Double)vector.elementAt(k)).doubleValue();
                    }

                    summary_hash.put(key, new SummaryStatistics(item));
                }

                SummaryStatistics temp = ((SummaryStatistics)summary_hash.get("all"));

                maximum = temp.maximum;
                minimum = temp.minimum;
            }
        }

        public void placeInTextArea(String[][] data_matrix)
        {
            int num_rows = data_matrix.length;

            String txt = null;

            dataTextArea.setText("");

            for(int i = 0; i < num_rows; i++)
            {
                txt = data_matrix[i][0] + "," + data_matrix[i][1] + "\n";

                dataTextArea.append(txt);
            }
        }

        public String[][] getTextArea()
        {
            StringTokenizer st = new StringTokenizer(dataTextArea.getText().trim(),"\n");

            int m = st.countTokens();
            int n = 2;

            String s;
            String[] temp = null;
            Vector elements = new Vector();

            Hashtable possible_values = new Hashtable();

            for(int i = 0; i < m; i++)
            {
                s = st.nextToken();

                temp = s.split(",");

                if(temp.length == n)
                {
                    String[] tuple = new String[2];

                    tuple[0] = temp[0].trim();
                    tuple[1] = temp[1].trim();

                    if(!isDouble(tuple[0]))
                    {
                        new FrameWindow("(" + tuple[0] + ") is not a number.");

                        return dataBank.raw_data;
                    }

                    possible_values.put(tuple[1], tuple[0]);

                    elements.add(tuple);
                }
                else
                {
                    if(!s.trim().equals(""))
                    {
                        new FrameWindow("I cannot understand (" + s + ").");

                        return dataBank.raw_data;
                    }
                }
            }

            if(possible_values.keySet().size() > MAX_NUM_CATEGORIES)
            {
                new FrameWindow("You can only have up to " + MAX_NUM_CATEGORIES + " types in your data.");

                return dataBank.raw_data;
            }

            String[][] data_matrix = new String[elements.size()][2];

            for(int i = 0; i < elements.size(); i++)
            {
                data_matrix[i][0] = ((String[])elements.elementAt(i))[0];
                data_matrix[i][1] = ((String[])elements.elementAt(i))[1];
            }

            return data_matrix;
        }

        public boolean isDouble(String s)
        {
            char[] ch = s.toCharArray();
            int index = 0;
            boolean seen_decimal = false;
            boolean seen_digit = false;

            if (ch[index] == '-')
            {
                index++;
            }

            while ((index < ch.length) && (((ch[index] >= '0') && (ch[index] <= '9')) ||
                   ((ch[index] == '.') && (!seen_decimal))))
            {
                if (ch[index] == '.') seen_decimal = true; else seen_digit = true;
                index++;
            }

            return ((index == ch.length) && (seen_digit));
        }

        public class ExperimentalUnit
        {
            double value;
            String category;

            ExperimentalUnit(double val, String s)
            {
                this.value   = val;
                this.category = s;
            }
        }

        public class SummaryStatistics
        {
            double minimum;
            double maximum;
            double median;
            double upper_quartile;
            double lower_quartile;
            double IQR;
            double[] data_points;

            double upper_whisker;
            double lower_whisker;

            Statistics stats = new Statistics();

            SummaryStatistics(double[] data)
            {
                maximum = stats.getMax(data);
                minimum = stats.getMin(data);
                median = stats.getMedian(data);
                upper_quartile = stats.getUpperQuartile(data);
                lower_quartile = stats.getLowerQuartile(data);
                IQR = upper_quartile - lower_quartile;
                data_points = data;

                Arrays.sort(data);

                for(int i = 0; i < data.length; i++)
                {
                    // System.out.println("data = " + data[i]);

                    if(data[i] <= 1.5*IQR + upper_quartile)
                    {
                        upper_whisker = data[i];
                    }
                    else
                    {
                        upper_whisker = 1.5*IQR + upper_quartile;
                    }

                    if(data[data.length - i - 1] >= lower_quartile - 1.5*IQR)
                    {
                        lower_whisker = data[data.length - i - 1];
                    }
                    else
                    {
                        lower_whisker = lower_quartile - 1.5*IQR;
                    }
                }
            }

            void print()
            {
                System.out.println("--------------------------");

                System.out.println("maximum = " + maximum);
                System.out.println("minimum = " + minimum);
                System.out.println("median = " + median);
                System.out.println("lower_quartile = " + lower_quartile);
                System.out.println("upper_quartile = " + upper_quartile);
                System.out.println("IQR = " + IQR);

                System.out.println("--------------------------");
            }
        }

        public class MainPanel extends JPanel
        {
            Dimension offDimension;

            static final int LEFT_MARGIN  = 65;
            static final int RIGHT_MARGIN = 25;
            static final int HEAD_MARGIN  = 35;

            @Override
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);

                Dimension d = this.getSize();

                bs = new BasicStroke((float)1.0, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                ((Graphics2D)g).setStroke(bs);

                // Erase the previous image
                g.setColor(getBackground());

                redtowhite = new GradientPaint(0,0,new Color(150, 100, 50),d.width, d.height,Color.white);
                ((Graphics2D)g).setPaint(redtowhite);
                g.fillRect(0, 0, d.width, d.height);

                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                drawPlot(g);
            }

            public void drawPlot(Graphics g)
            {
                Dimension d = this.getSize();

                int x_length = d.width  - LEFT_MARGIN - RIGHT_MARGIN;
                int y_length = d.height - 2*HEAD_MARGIN;

                g.setColor(Color.black);

                g.drawLine(LEFT_MARGIN, HEAD_MARGIN, LEFT_MARGIN, y_length + HEAD_MARGIN);
                g.drawLine(LEFT_MARGIN, y_length + HEAD_MARGIN, x_length + LEFT_MARGIN, y_length + HEAD_MARGIN);

                double maximum = dataBank.maximum + 0.05*(dataBank.maximum - dataBank.minimum);
                double minimum = dataBank.minimum - 0.05*(dataBank.maximum - dataBank.minimum);

                fm = g.getFontMetrics(font_label);
                g.setFont(font_label);

                if(maximum > 10000 || (maximum - minimum) < 0.001)
                {
                    df = new DecimalFormat("0.###E0");
                }
                else if((maximum - minimum) > 10)
                {
                    df = new DecimalFormat("0.00");
                }
                else
                {
                    df = new DecimalFormat("0.0000");
                }

                String s;

                for(int i = 0; i <= 4; i++)
                {
                    g.drawLine(LEFT_MARGIN - 5, HEAD_MARGIN + (int)(y_length*(0.25*i)), LEFT_MARGIN, HEAD_MARGIN + (int)(y_length*(0.25*i)));

                    s = df.format(maximum - (i*0.25)*(maximum - minimum));

                    g.drawString(s, LEFT_MARGIN - fm.stringWidth(s) - 10, HEAD_MARGIN + (int)(y_length*(0.25*i) + 4));
                }

                int n           = dataBank.keys.length;
                int spacing     = 10;
                int rect_length = (int)(((double)(x_length - (n+1)*spacing)) / n);

                int x = spacing + (int)((double)rect_length/2) + LEFT_MARGIN;

                for(int i = 0; i < n; i++)
                {
                    g.setColor(Color.black);

                    g.drawLine(x, y_length + HEAD_MARGIN, x, y_length + HEAD_MARGIN + 5);

                    s = dataBank.keys[i].trim();

                    g.drawString(dataBank.keys[i], x - fm.stringWidth(s)/2, y_length + fm.getHeight() + HEAD_MARGIN + 5);

                    SummaryStatistics summary = ((SummaryStatistics)dataBank.summary_hash.get(dataBank.keys[i]));

                    int top_left_x = x - rect_length/2;
                    int top_left_y = toGraphicsCoordY(summary.upper_quartile);
                    int height     = toGraphicsCoordY(summary.lower_quartile) - top_left_y;
                    int median     = toGraphicsCoordY(summary.median);

                    g.drawRect(top_left_x, top_left_y, rect_length, height);

                    ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

                    if(dataBank.keys[i].equals("all"))
                    {
                        g.setColor(new Color(205, 125, 50));
                    }
                    else
                    {
                        g.setColor(new Color(100, 200, 100));
                    }

                    g.fillRect(top_left_x+1, top_left_y+1, rect_length-1, height-1);

                    ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

                    g.setColor(Color.black);

                    g.drawLine(x - rect_length/2, median, x + rect_length/2, median);

                    int above_IQR = toGraphicsCoordY(summary.upper_whisker);
                    int below_IQR = toGraphicsCoordY(summary.lower_whisker);

                    g.drawLine(x, below_IQR, x, above_IQR);
                    g.drawLine(x - rect_length/2, above_IQR, x + rect_length/2, above_IQR);
                    g.drawLine(x - rect_length/2, below_IQR, x + rect_length/2, below_IQR);

                    Shape outlier = null;

                    g.setColor(new Color(50, 100, 200));

                    for(int j = 0; j < summary.data_points.length; j++)
                    {
                        if(summary.data_points[j] > summary.upper_whisker || summary.data_points[j] < summary.lower_whisker)
                        {
                            int y = toGraphicsCoordY(summary.data_points[j]);

                            outlier = new Ellipse2D.Float(x-2f, y-2f, 5, 5);

                            ((Graphics2D)g).fill(outlier);
                        }
                    }

                    x += rect_length + spacing;
                }
            }

            public int toGraphicsCoordY(double y)
            {
                double maximum = dataBank.maximum + 0.01*(dataBank.maximum - dataBank.minimum);
                double minimum = dataBank.minimum - 0.01*(dataBank.maximum - dataBank.minimum);

                double denominator = maximum - minimum;

                double numerator = maximum - y;

                Dimension d = this.getSize();

                int length = d.height - 2*HEAD_MARGIN;

                return (int)((numerator/denominator)*length + HEAD_MARGIN);
            }
        }

        public void paintAll()
        {
            mainPanel.repaint();
            summaryPanel.repaint();
        }

        private class ButtonListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(e.getSource() == update_button)
                {
                    dataBank = new DataBank(getTextArea());

                    paintAll();
                }
            }
        }

        public class StringComparator implements Comparator
        {
            @Override
            public int compare(Object o1, Object o2)
            {
                  return (((String) o1).toLowerCase().compareTo(((String) o2).toLowerCase()));
            }
        }

        public class SummaryPanel extends JPanel
        {
            int width;
            int height;
            int cell_width;
            int cell_height;

            public SummaryPanel(int w, int h)
            {
                width  = w;
                height = h;

                this.setSize(w, h);

                cell_width  = width/5;
                cell_height = height/(MAX_NUM_CATEGORIES+2);

                width  = 5*cell_width;
                height = 5*cell_height;
            }

            @Override
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);

                ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

                g.setColor(new Color(50, 200, 200));

                g.fillRect(0,0,width + 2,height + 2);

                ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

                g.setColor(Color.black);

                g.drawRect(0,0,width-1,height-1);

                for(int i = 1; i < MAX_NUM_CATEGORIES+2; i++)
                {
                    g.drawLine(0,i*cell_height,width + 2,i*cell_height);
                }

                for(int i = 1; i < 5; i++)
                {
                    g.drawLine(i*cell_width,0,i*cell_width, height + 2);
                }

                g.setColor(Color.red);

                int x_begin = 5;
                int y_begin = (int)(0.75*cell_height);

                g.drawString("Category", x_begin, y_begin);

                g.setColor(Color.blue);

                for(int i = 0; i < MAX_NUM_CATEGORIES+1 && i < dataBank.keys.length; i++)
                {
                    y_begin += cell_height;

                    g.drawString(dataBank.keys[i], x_begin, y_begin);
                }

                g.setColor(Color.red);

                x_begin = 5 + cell_width;
                y_begin = (int)(0.75*cell_height);

                g.drawString("N", x_begin, y_begin);

                x_begin += cell_width;
                g.drawString("MEDIAN", x_begin, y_begin);

                x_begin += cell_width;
                g.drawString("Q1", x_begin, y_begin);

                x_begin += cell_width;
                g.drawString("Q3", x_begin, y_begin);

                x_begin = 5;
                y_begin = (int)(0.75*cell_height);

                SummaryStatistics summary;
                DecimalFormat df = new DecimalFormat("####.#");
                String s;

                g.setColor(Color.black);

                for(int i = 0; i < 4; i++)
                {
                    x_begin += cell_width;

                    for(int j = 0; j < MAX_NUM_CATEGORIES+1 && j < dataBank.keys.length; j++)
                    {
                        summary = ((SummaryStatistics)dataBank.summary_hash.get(dataBank.keys[j]));
                        y_begin += cell_height;

                        switch(i)
                        {
                            case 0:

                                s = df.format(summary.data_points.length);
                                g.drawString(s,x_begin,y_begin);

                                break;

                            case 1:

                                s = df.format(summary.median);
                                g.drawString(s,x_begin,y_begin);

                                break;

                            case 2:

                                s = df.format(summary.lower_quartile);
                                g.drawString(s,x_begin,y_begin);

                                break;

                            case 3:

                                s = df.format(summary.upper_quartile);
                                g.drawString(s,x_begin,y_begin);

                                break;
                        }
                    }

                    y_begin = (int)(0.75*cell_height);
                }
            }
        }

        public class FrameWindow extends WindowAdapter
        {
            Frame name;
            Button OK_button;
            MessagePanel messagePanel;

            final int WIDTH  = 250;
            final int HEIGHT = 200;

            final Color BUTTON_COLOR = new Color(200, 150, 50);

            FrameWindow(String s)
            {
                name = new Frame("Error");
                name.setLayout(null);
                name.setSize(WIDTH,HEIGHT);
                name.setLocation(50, 50);
                name.addWindowListener(this);

                messagePanel = new MessagePanel(s, WIDTH - 50);
                messagePanel.setLocation(25,50);
                messagePanel.setSize(WIDTH - 50, HEIGHT - 150);

                name.add(messagePanel);

                OK_button = new Button("OK");
                OK_button.setSize(50, 25);
                OK_button.setLocation(WIDTH/2 - 25, HEIGHT - 75);
                OK_button.addActionListener(new ButtonListener());
                OK_button.setBackground(BUTTON_COLOR);

                name.add(OK_button);

                name.setVisible(true);
            }

            @Override
            public void windowClosing(WindowEvent e)
            {
                name.setVisible(false);
            }

            class MessagePanel extends Panel
            {
                int width;
                String message;;

                public MessagePanel(String s, int w)
                {
                    this.width = w;
                    this.message = s;
                }

                @Override
                public void paint(Graphics g)
                {
                    drawParagraph((Graphics2D)g, message, (float)width);
                }

                public void drawParagraph(Graphics2D g, String paragraph, float width)
                {
                    LineBreakMeasurer linebreaker = new LineBreakMeasurer(
                  new AttributedString(paragraph).getIterator(), g.getFontRenderContext());

                    float y = 0.0f;
                    while (linebreaker.getPosition() < paragraph.length())
                    {
                        TextLayout tl = linebreaker.nextLayout(width);

                        y += tl.getAscent();
                        tl.draw(g, 0, y);
                        y += tl.getDescent() + tl.getLeading();
                    }
                }
            }

            private class ButtonListener implements ActionListener
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if(e.getSource() == OK_button)
                    {
                        name.setVisible(false);
                    }
                }
            }
        }

        @Override
        public void itemStateChanged(ItemEvent e)
        {
            String w = "Datasets/" + (String)e.getItem();

            readFile(w);

            paintAll();
        }
    }

    @Override
    public void componentHidden(ComponentEvent e)
    {
        System.out.println("componentHidden event from "
                        + e.getComponent().getClass().getName());
    }

    @Override
    public void componentMoved(ComponentEvent e)
    {
        Component c = e.getComponent();
        System.out.println("componentMoved event from "
                       + c.getClass().getName()
                       + "; new location: "
                       + c.getLocation().x
                       + ", "
                       + c.getLocation().y);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        Component c = e.getComponent();
        System.out.println("componentResized event from "
                       + c.getClass().getName()
                       + "; new size: "
                       + c.getSize().width
                       + ", "
                       + c.getSize().height);
    }

    @Override
    public void componentShown(ComponentEvent e)
    {
        System.out.println("componentShown event from "
                        + e.getComponent().getClass().getName());
    }
}
