package de.nx42.maps4cim.gui.comp;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormattedComponents {
    
    private static final Logger log = LoggerFactory.getLogger(FormattedComponents.class);

    protected static final DecimalFormat format;
    
    static {
        format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        format.setMaximumFractionDigits(4);
        format.setGroupingUsed(false);
    }
    
    public static NumberFormatter getNumberFormatter(
            NumberFormat nf,
            Class<? extends Number> valueClass,
            Comparable<? extends Number> min,
            Comparable<? extends Number> max) {
        
        NumberFormatter nfm = new NumberFormatter(nf);
        nfm.setValueClass(valueClass);
        nfm.setCommitsOnValidEdit(true);
        nfm.setMinimum(min);
        nfm.setMaximum(max);
        return nfm;
    }
    
    public static NumberFormatter getDecimalFormatter(double min, double max) {
        return getNumberFormatter(format, Double.class, min, max);
    }

    public static NumberFormatter getIntegerFormatter(int min, int max) {
        return getNumberFormatter(format, Integer.class, min, max);
    }
    
    public static MaskFormatter getHexFormatter(int length) {
        try {
            return new MaskFormatter(Strings.repeat("H", length));
        } catch (ParseException e) {
            log.error("Error creating MaskFormatter", e);
            return null;
        }
    }

}
