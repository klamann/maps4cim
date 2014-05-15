package de.nx42.maps4cim.util;

import java.util.LinkedList;
import java.util.List;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.exception.ValidationFailedException;

public class ValidatorUtils {
    
    public static final Validator val = new Validator();
    
    /**
     * validates the field and getter constrains of the given object
     * @param o the object to validate, cannot be null
     * @return a list with the detected constraint violations. if no violations
     *         are detected an empty list is returned
     * @see Validator#validate(Object)
     */
    public static List<ConstraintViolation> validate(Object o)
            throws IllegalArgumentException, ValidationFailedException {
        return val.validate(o);
    }
    
    /**
     * validates the field and getter constrains of the given object. Filters
     * the results using {@link #filterRootCauses(List)}, so only the actual
     * violations are returned, not the objects affected by this violation
     * @param o the object to validate, cannot be null
     * @return a list with the detected constraint violations. if no violations
     *         are detected an empty list is returned
     * @see Validator#validate(Object)
     */
    public static List<ConstraintViolation> validateR(Object o)
            throws IllegalArgumentException, ValidationFailedException {
        return filterRootCauses(val.validate(o));
    }
    
    /**
     * Takes a list of {@link ConstraintViolation}s and returns only the root
     * cause of every violation, thereby ignoring the objects affected by a
     * violation
     * @param cvs the list of violations to filter
     * @return only the root causes of all violations
     */
    public static List<ConstraintViolation> filterRootCauses(List<ConstraintViolation> cvs) {
        if(cvs != null) {
            return filterRootCauses(cvs.toArray(new ConstraintViolation[cvs.size()]), new LinkedList<ConstraintViolation>());
        } else {
            return null;
        }
    }
    
    /**
     * @see #filterRootCauses(List)
     */
    private static List<ConstraintViolation> filterRootCauses(ConstraintViolation[] cvs, List<ConstraintViolation> rootCauses) {
        for (ConstraintViolation cv : cvs) {
            ConstraintViolation[] causes = cv.getCauses();
            if(causes == null) {
                rootCauses.add(cv);
            } else {
                filterRootCauses(causes, rootCauses);
            }
        }
        return rootCauses;
    }

    // String formatting
    
    public static String formatCausesRecursively(ConstraintViolation cv) {
        return formatCausesRecursively(cv, new StringBuilder(256)).toString();
    }
    
    public static StringBuilder formatCausesRecursively(ConstraintViolation cv, StringBuilder sb) {
        if(cv != null) {
            sb.append(cv.toString());
            sb.append('\n');
            formatCausesRecursively(cv.getCauses(), sb);
        }
        return sb;
    }
    
    public static String formatCausesRecursively(ConstraintViolation[] cvs) {
        return formatCausesRecursively(cvs, new StringBuilder(256)).toString();
    }
    
    public static StringBuilder formatCausesRecursively(ConstraintViolation[] cvs, StringBuilder sb) {
        if(cvs != null) {
            for (ConstraintViolation cv : cvs) {
                sb.append(cv.toString());
                sb.append('\n');
                formatCausesRecursively(cv.getCauses(), sb);
            }
        }
        return sb;
    }
    
    public static String formatCausesRecursively(List<ConstraintViolation> cvs) {
        return formatCausesRecursively(cvs.toArray(new ConstraintViolation[cvs.size()]));
    }
    
    public static String formatRootCauses(List<ConstraintViolation> cvs) {
        return formatCausesRecursively(filterRootCauses(cvs));
    }
    
}
