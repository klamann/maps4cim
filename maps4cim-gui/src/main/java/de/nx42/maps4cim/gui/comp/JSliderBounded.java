/**
 * maps4cim - a real world map generator for CiM 2
 * Copyright 2013 - 2014 Sebastian Straub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.nx42.maps4cim.gui.comp;

import java.awt.event.MouseEvent;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JSlider implementation that allows certain boundaries to be enforced,
 * without resizing the actual slider range.
 * An upper and a lower bound can be defined. If the user moves the slider
 * out of the range of these boundaries, the slider jumps back to the closest
 * valid position as soon as it is released. For inputs other than mouse events
 * this rule is enforced immediately.
 */
public class JSliderBounded extends JSlider {

    private static final long serialVersionUID = 3048520725712777110L;
    private static final Logger log = LoggerFactory.getLogger(JSliderBounded.class);

    protected int lowerBound = Integer.MIN_VALUE;
    protected int upperBound = Integer.MAX_VALUE;

    public JSliderBounded() {
        super();
    }

    public JSliderBounded(BoundedRangeModel brm) {
        super(brm);
    }

    public JSliderBounded(int orientation, int min, int max, int value) {
        super(orientation, min, max, value);
    }

    public JSliderBounded(int min, int max, int value) {
        super(min, max, value);
    }

    public JSliderBounded(int min, int max) {
        super(min, max);
    }

    public JSliderBounded(int orientation) {
        super(orientation);
    }

    /* (non-Javadoc)
     * @see javax.swing.JSlider#setValue(int)
     */
    @Override
    public void setValue(int n) {
        final int adjusted = n > upperBound ? upperBound : n < lowerBound ? lowerBound : n;
        super.setValue(adjusted);
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#processMouseEvent(java.awt.event.MouseEvent)
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);

        // make sure the slider is always updated, as soon as it is released
        // yeah, this is a hack, but it works great ;)
        if(e.getID() == MouseEvent.MOUSE_RELEASED) {
            int val = getValue();
            // set to a different valid value first, so an update is enforced
            super.setValue(val+1);
            // reset to original value
            setValue(val);
        }
    }

    /**
     * @return the lowerBound
     */
    public int getLowerBound() {
        return lowerBound;
    }

    /**
     * @param lowerBound the lowerBound to set
     */
    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    /**
     * @return the upperBound
     */
    public int getUpperBound() {
        return upperBound;
    }

    /**
     * @param upperBound the upperBound to set
     */
    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    public boolean isWithinBounds(int n) {
        return n <= upperBound && n <= getMaximum() && n >= lowerBound && n >= getMinimum();
    }

    public int getValidValue(int n) {
        if(n > getMaximum() || n > upperBound) {
            return upperBound >= getMaximum() ? getMaximum() : upperBound;
        } else if(n < getMinimum() || n < lowerBound) {
            return lowerBound <= getMinimum() ? getMinimum() : lowerBound;
        } else {
            return n;
        }
    }


    public static void textFieldChange(final JTextField field, final JSliderBounded slider, String input) {
        if(!Strings.isNullOrEmpty(input)) {
            try {
                final int value = (int) (Double.parseDouble(input) + 0.5);
                if(slider.getValue() != value) {
                    // adjust value (within slider range and within special JSliderBounded limits)
                    int adjusted = slider.getValidValue(value);

                    slider.setValue(adjusted);
                    if(value != adjusted) {
                        field.setText(String.valueOf(adjusted));
                    }
                }
            } catch(NumberFormatException e) {
                log.warn("Cannot parse \"{}\" as number", input);
            }
        }
    }

    public static void sliderChange(final JSliderBounded slider, final JTextField field) {
        final String sliderVal = String.valueOf(slider.getValue());
        final String inputVal = field.getText();
        if(!sliderVal.equals(inputVal)) {
            field.setText(sliderVal);
        }
    }

}
