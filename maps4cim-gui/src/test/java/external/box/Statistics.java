package external.box;

import java.util.Arrays;

public class Statistics
{
    // Member funtion that finds the mimum value in a vector of numbers.
    // Parameters:
    //      (1) vector of numbers
    // Returns:
    //      the minimum value of the vector (as a double)
    public double getMin(double[] vec)
    {
        double[] sorted = new double[vec.length];

        for(int i = 0; i < sorted.length; i++)
        {
            sorted[i] = vec[i];
        }

        Arrays.sort(sorted);

        return sorted[0];
    }

    // Member funtion that finds the maximum value in a vector of numbers.
    // Parameters:
    //      (1) vector of numbers
    // Returns:
    //      the maximum value of the vector (as a double)
    public double getMax(double[] vec)
    {
        double[] sorted = new double[vec.length];

        for(int i = 0; i < sorted.length; i++)
        {
            sorted[i] = vec[i];
        }

        Arrays.sort(sorted);

        return sorted[sorted.length - 1];
    }

    // Member funtion that finds the mimum value in a vector of numbers.
    // Parameters:
    //      (1) vector of numbers
    // Returns:
    //      the minimum value of the vector (as a double)
    public int getMin(int[] vec)
    {
        int[] sorted = new int[vec.length];

        for(int i = 0; i < sorted.length; i++)
        {
            sorted[i] = vec[i];
        }

        Arrays.sort(sorted);

        return sorted[0];
    }

    // Member funtion that finds the maximum value in a vector of numbers.
    // Parameters:
    //      (1) vector of numbers
    // Returns:
    //      the maximum value of the vector (as a double)
    public int getMax(int[] vec)
    {
        int[] sorted = new int[vec.length];

        for(int i = 0; i < sorted.length; i++)
        {
            sorted[i] = vec[i];
        }

        Arrays.sort(sorted);

        return sorted[sorted.length - 1];
    }

    public double[] sort(double[] vec)
    {
        double[] sorted = new double[vec.length];

        for(int i = 0; i < sorted.length; i++)
        {
            sorted[i] = vec[i];
        }

        Arrays.sort(sorted);

        return sorted;
    }

    public int[] sort(int[] vec)
    {
        int[] sorted = new int[vec.length];

        for(int i = 0; i < sorted.length; i++)
        {
            sorted[i] = vec[i];
        }

        Arrays.sort(sorted);

        return sorted;
    }

    // Member function that checks to see if a number is even.
    // Parameters:
    //      (1) an integer numbers
    // Returns:
    //      true if the number is even
    //      false otherwise
    public boolean isEven(int num)
    {
        if(num % 2 == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // Member funtion that finds the mean value of a vector of numbers.
    // Parameters:
    //      (1) vector of numbers
    // Returns:
    //      the mean of the vector (as a double)
    public double getMean(double[] vec)
    {
        double mean = 0;

        for(int i = 0; i < vec.length; i++)
        {
            mean += vec[i];
        }

        return ( mean / vec.length );
    }

    // Member funtion that finds the median value of a vector of numbers.
    // Parameters:
    //      (1) vector of numbers
    // Returns:
    //      the median of the vector (as a double)
    public double getMedian(double vec[]) throws NullPointerException
    {
        double[] sorted = new double[vec.length];

        for(int i = 0; i < sorted.length; i++)
        {
            sorted[i] = vec[i];
        }

        Arrays.sort(sorted);

        int index = (int)Math.ceil((double)vec.length / 2);

        if(isEven(vec.length) && vec.length > 1)
        {
            return ( (sorted[index] + sorted[index - 1]) / 2 );
        }
        else
        {
            return sorted[index-1];
        }
    }

    // Member funtion that finds the median value of a vector of numbers.
    // Parameters:
    //      (1) vector of numbers
    // Returns:
    //      the median of the vector (as a double)
    public double getMedian(int vec[]) throws NullPointerException
    {
        int[] sorted = new int[vec.length];

        for(int i = 0; i < sorted.length; i++)
        {
            sorted[i] = vec[i];
        }

        Arrays.sort(sorted);

        int index = (int)Math.ceil((double)vec.length / 2);

        if(isEven(vec.length) && vec.length > 1)
        {
            return ( ((double)(sorted[index] + sorted[index - 1])) / 2 );
        }
        else
        {
            return sorted[index-1];
        }
    }

    // Member funtion that finds the lower quartile value of a vector of numbers.
    // Parameters:
    //      (1) vector of numbers
    // Returns:
    //      the lower quartile of a vector of numbers (as a double)
    public double getLowerQuartile(double vec[]) throws NullPointerException
    {
        double[] sorted = new double[vec.length];

        for(int i = 0; i < sorted.length; i++)
        {
            sorted[i] = vec[i];
        }

        Arrays.sort(sorted);

        int end_index = (int)Math.ceil((double)vec.length / 2) - 1;

        int quartile_index;

        double[] lower_quartile = new double[end_index+1];

        for(int i = 0; i <= end_index; i++)
        {
            lower_quartile[i] = sorted[i];
        }

        return getMedian(lower_quartile);
    }

    // Member funtion that finds the lower quartile value of a vector of numbers.
    // Parameters:
    //      (1) vector of numbers
    // Returns:
    //      the lower quartile of a vector of numbers (as a double)
    public double getUpperQuartile(double vec[]) throws NullPointerException
    {
        double[] sorted = new double[vec.length];

        for(int i = 0; i < sorted.length; i++)
        {
            sorted[i] = vec[i];
        }

        Arrays.sort(sorted);

        int end_index = (int)Math.ceil(vec.length / 2);

        int quartile_index;

        double[] upper_quartile = new double[sorted.length - end_index];

        int j = 0;

        for(int i = end_index; i < sorted.length; i++)
        {
            upper_quartile[j] = sorted[i];

            j++;
        }

        return getMedian(upper_quartile);
    }

    // Member funtion that finds the corrected sum of squares value of a vector
    // of numbers.
    // Parameters:
    //      (1) vector of numbers
    // Returns:
    //      the value of the corrected sum of squares for the vector
    //      (as a double)
    public double getSS(double[] vec)
    {
        double SS = 0;

        double mean = getMean(vec);

        for(int i = 0; i < vec.length; i++)
        {
            SS += Math.pow((vec[i] - mean), 2);
        }

        return SS;
    }

    // Member funtion that finds the value of for the corrected sum of squares
    // between two vectors of numbers
    // Parameters:
    //      (1) first vector of numbers
    //      (2) second vector of numbers
    // Returns:
    //      the value of the corrected sum of squares between the two vectors
    //      (as a double)
    public double getSS(double[] vec1, double[] vec2)
    {
        double SS = 0;

        double mean1 = getMean(vec1);
        double mean2 = getMean(vec2);

        for(int i = 0; i < vec1.length; i++)
        {
            SS = SS + (vec1[i] - mean1)*(vec2[i] - mean2);
        }

        return SS;
    }

    public double sum(double[] vec)
    {
        double sum = 0;

        for(int i = 0; i < vec.length; i++)
        {
            sum += vec[i];
        }

        return sum;
    }

    public int sum(int[] vec)
    {
        int sum = 0;

        for(int i = 0; i < vec.length; i++)
        {
            sum += vec[i];
        }

        return sum;
    }

    public double fDist(int v1, int v2, double f0)
    {
        SpecialFunctions func = new SpecialFunctions();

        return func.fDist(v1, v2, f0);
    }
}

/*  specialFunctions.java
    20-July-1997
    Bryan Lewis
    Department of Mathematics and Computer Science
    Kent State University

    This software is in the public domain and can
    be copied, modified and used without restriction.

    (*) Press, Flannery, Teukolsky, Vetterling, Numerical Recipes,
        Cambridge University Press, 1986

    ****** Contents ******
    logGamma(double x)  Returns ln(gamma(x)) (*)
    gamma(double x)     Returns gamma(x)
    fDist(double v1, double v2, double f)
    Returns F distribution with v1, v2 deg. freedom
    i.e., P(x>f)
    stDist(double v, double t)
    Returned value is P( x > t) for a r.v. x with v deg. freedom.
    ****** End Contents ******

    ****** Dependencies ******
    Java.lang.Math
    ****** End Dependencies ******

    ****** Revision History******
    21.December, 1997: Added betai, betcf, new F-distribution from (*)
    ****** End Revisions ******

*/

class SpecialFunctions {

    public SpecialFunctions() {
    // Constructor
    }

    public double logGamma( double xx) {
        // An approximation to ln(gamma(x))
        // define some constants...
        int j;
        double stp = 2.506628274650;
        double cof[] = new double[6];
        cof[0]=76.18009173;
        cof[1]=-86.50532033;
        cof[2]=24.01409822;
        cof[3]=-1.231739516;
        cof[4]=0.120858003E-02;
        cof[5]=-0.536382E-05;

        double x = xx-1;
        double tmp = x + 5.5;
        tmp = (x + 0.5)*Math.log(tmp) - tmp;
        double ser = 1;
        for(j=0;j<6;j++){
            x++;
            ser = ser + cof[j]/x;
        }
        double retVal = tmp + Math.log(stp*ser);
        return retVal;
    }

    public double gamma( double x) {
        // An approximation of gamma(x)
        double f = 10E99;
        double g = 1;
        if ( x > 0 ) {
            while (x < 3) {
                g = g * x;
                x = x + 1;
            }
            f = (1 - (2/(7*Math.pow(x,2))) * (1 - 2/(3*Math.pow(x,2))))/(30*Math.pow(x,2));
            f = (1-f)/(12*x) + x*(Math.log(x)-1);
            f = (Math.exp(f)/g)*Math.pow(2*Math.PI/x,0.5);
        }
        else {
            Double er = new Double(0);
            f = Double.POSITIVE_INFINITY;
        }
        return f;
    }

    double betacf(double a,double b,double x){
        // A continued fraction representation of the beta function
        int maxIterations = 50, m=1;
        double eps = 3E-5;
        double am = 1;
        double bm = 1;
        double az = 1;
        double qab = a+b;
        double qap = a+1;
        double qam = a-1;
        double bz = 1 - qab*x/qap;
        double aold = 0;
        double em, tem, d, ap, bp, app, bpp;
        while((m<maxIterations)&&(Math.abs(az-aold)>=eps*Math.abs(az))){
            em = m;
            tem = em+em;
            d = em*(b-m)*x/((qam + tem)*(a+tem));
            ap = az+d*am;
            bp = bz+d*bm;
            d = -(a+em)*(qab+em)*x/((a+tem)*(qap+tem));
            app = ap+d*az;
            bpp = bp+d*bz;
            aold = az;
            am = ap/bpp;
            bm = bp/bpp;
            az = app/bpp;
            bz = 1;
            m++;
        }
        return az;
    }

    public double betai(double a, double b, double x) {
        // the incomplete beta function from 0 to x with parameters a, b
        // x must be in (0,1) (else returns error)
        Double er = new Double(0);
        double bt=0, beta=Double.POSITIVE_INFINITY;
        if( x==0 || x==1 ){
            bt = 0; }
        else if((x>0)&&(x<1)) {
            bt = gamma(a+b)*Math.pow(x,a)*Math.pow(1-x,b)/(gamma(a)*gamma(b)); }
        if(x<(a+1)/(a+b+2)){
            beta = bt*betacf(a,b,x)/a; }
        else {
            beta = 1-bt*betacf(b,a,1-x)/b; }
        return beta;
    }

    public double fDist(double v1, double v2, double f) {
        /*  F distribution with v1, v2 deg. freedom
            P(x>f)
        */
        double p =  betai(v1/2, v2/2, v1/(v1 + v2*f));
        return p;
    }

    public double student_c(double v) {
        // Coefficient appearing in Student's t distribution
        return Math.exp(logGamma( (v+1)/2)) / (Math.sqrt(Math.PI*v)*Math.exp(logGamma(v/2)));
    }

    public double student_tDen(double v, double t) {
        /*  Student's t density with v degrees of freedom
            Requires gamma, student_c functions
            Part of Bryan's Java math classes (c) 1997
        */

        return student_c(v)*Math.pow( 1 + (t*t)/v, -0.5*(v+1) );
    }

    public double stDist(double v, double t) {

        /*  Student's t distribution with v degrees of freedom
            Requires gamma, student_c functions
            Part of Bryan's Java math classes (c) 1997
            This only uses compound trapezoid, pending a good integration package
            Returned value is P( x > t) for a r.v. x with v deg. freedom.
            NOTE: With the gamma function supplied here, and the simple trapeziodal
            sum used for integration, the accuracy is only about 5 decimal places.
            Values below 0.00001 are returned as zero.
        */

        double sm = 0.5;
        double u = 0;
        double sign = 1;
        double stepSize = t/5000;
        if ( t < 0) {
         sign = -1;
        }
        for (u = 0; u <= (sign * t) ; u = u + stepSize) {
            sm = sm + stepSize * student_tDen( v, u);
        }
        if ( sign < 0 ) {
         sm = 0.5 - sm;
        }
        else {
         sm = 1 - sm;
        }
        if (sm < 0) {
         sm = 0;        // do not allow probability less than zero from roundoff error
        }
        else if (sm > 1) {
         sm = 1;        // do not allow probability more than one from roundoff error
        }
        return  sm ;
    }
}
