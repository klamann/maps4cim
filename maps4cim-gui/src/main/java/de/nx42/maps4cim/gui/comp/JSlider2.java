package de.nx42.maps4cim.gui.comp;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

public class JSlider2 extends JSlider {

    private static final long serialVersionUID = -7631045097336863811L;

    public JSlider2() {
        super();
    }

    public JSlider2(BoundedRangeModel brm) {
        super(brm);
    }

    public JSlider2(int orientation, int min, int max, int value) {
        super(orientation, min, max, value);
    }

    public JSlider2(int min, int max, int value) {
        super(min, max, value);
    }

    public JSlider2(int min, int max) {
        super(min, max);
    }

    public JSlider2(int orientation) {
        super(orientation);
    }

    /**
     * Sets the slider's current value to {@code n}.  This method
     * forwards the new value to the model.
     * <p>
     * The data model (an instance of {@code BoundedRangeModel})
     * handles any mathematical
     * issues arising from assigning faulty values.  See the
     * {@code BoundedRangeModel} documentation for details.
     * <p>
     * If the new value is different from the previous value,
     * all change listeners are notified, but only if the parameter
     * notify is set to true!
     *
     * @param   n       the new value
     * @param   notify  only notify listeners, if this is set to true
     * @see     #getValue
     * @see     #addChangeListener
     * @see     BoundedRangeModel#setValue
     * @beaninfo
     *   preferred: true
     * description: The sliders current value.
     */
    public void setValue(int n, boolean notify) {
        if(notify) {
            super.setValue(n);
        } else {
            BoundedRangeModel m = getModel();
            int oldValue = m.getValue();
            if (oldValue == n) {
                return;
            }
            
            // beware of evil hack below...
            // temporarily remove change listeners
            ChangeListener[] listeners = getChangeListeners();
            for (ChangeListener l : listeners) {
                removeChangeListener(l);
            }
            
            // set value
            m.setValue(n);
            
            // add listeners again
            for (ChangeListener l : listeners) {
                addChangeListener(l);
            }
        }
    }
    
    
    
}
