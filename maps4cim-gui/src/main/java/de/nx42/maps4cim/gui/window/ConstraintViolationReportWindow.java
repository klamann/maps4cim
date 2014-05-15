/**
 * maps4cim - a real world map generator for CiM 2
 * Copyright 2013 Sebastian Straub
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
package de.nx42.maps4cim.gui.window;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.JButton;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.OValContext;
import de.nx42.maps4cim.gui.util.event.Event;
import de.nx42.maps4cim.gui.window.template.TextFieldPanel;

public class ConstraintViolationReportWindow extends TextFieldPanel {

	private static final long serialVersionUID = 5583448871617188269L;
	
	protected List<ConstraintViolation> cvs;
	protected Event resetConfigEvent;
	
	public ConstraintViolationReportWindow(Window owner) {
        super(owner, "Configuration-Problems", "");
        setBounds(200, 150, 450, 400);
        
        JButton resetButton = new JButton("Reset All");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetConfigEvent.fire();
                dispose();
            }
        });
        buttonPane.add(resetButton);
    }
	
	public ConstraintViolationReportWindow(Window owner, List<ConstraintViolation> cvs) {
        this(owner);
        setViolations(cvs);
    }
	
	public void setViolations(List<ConstraintViolation> cvs) {
	    this.cvs = cvs;
	    setText(formatViolations(cvs));
	}
	
	public static String formatViolations(List<ConstraintViolation> cvs) {
	    StringBuilder sb = new StringBuilder(256);
	    
	    // intro
	    sb.append("<html>");
	    sb.append("<p>There ").append(cvs.size() == 1 ? "is" : "are")
	      .append(" <b>").append(cvs.size()).append(" problem");
	    if(cvs.size() > 1)
	        sb.append("s");
	    sb.append("</b> with your configuration. You should review the errors below:</p>");
	    
	    // list
	    sb.append("<ul>");
	    for (ConstraintViolation cv : cvs) {
            sb.append("<li>");
            
            // invalid value ?x for field ?f [?type] in context ?c
            sb.append(cv.getMessage());
            sb.append(" <font color=#999999>(invalid value <i>");
            sb.append(cv.getInvalidValue());
            sb.append("</i>");
            
            OValContext context = cv.getContext();
            if(context != null && context instanceof FieldContext) {
                FieldContext fc = (FieldContext) cv.getContext();
                Field field = fc.getField();
                
                sb.append(" for field <i>");
                sb.append(field.getName());
                sb.append("</i> [<i>");
                sb.append(field.getType().getSimpleName());
                sb.append("</i>] in context <i>");
                sb.append(field.getDeclaringClass().getAnnotation(XmlRootElement.class).name());
                sb.append("</i>)</font>");
            } else {
                sb.append(")</font>");
            }

            sb.append("</li>");
        }
	    sb.append("</ul>");
	    
	    // end
	    sb.append("</html>");
	    return sb.toString();
	}

    public Event getResetConfigEvent() {
        return resetConfigEvent;
    }

    public void setResetConfigEvent(Event resetConfigEvent) {
        this.resetConfigEvent = resetConfigEvent;
    }

    /**
     * @return the list of ConstraintViolations
     */
    public List<ConstraintViolation> getViolations() {
        return cvs;
    }
	
	

}