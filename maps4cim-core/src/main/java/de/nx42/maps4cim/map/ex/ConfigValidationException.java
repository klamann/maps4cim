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
package de.nx42.maps4cim.map.ex;

import com.google.common.base.Strings;


/**
 * Exception that indicates that a configuration object could not be validated
 * due to input errors
 */
public class ConfigValidationException extends MapGeneratorException {

    private static final long serialVersionUID = -588250261226704599L;

    protected String errorPopupMessage;

    public ConfigValidationException() {
        super();
    }

    public ConfigValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigValidationException(String message) {
        super(message);
    }

    public ConfigValidationException(Throwable cause) {
        super(cause);
    }


    public ConfigValidationException(String message, String errorPopup) {
        super(message);
        this.errorPopupMessage = errorPopup;
    }

    public ConfigValidationException(String message, Throwable cause, String errorPopup) {
        super(message, cause);
        this.errorPopupMessage = errorPopup;
    }

    public boolean hasErrorPopupMessage() {
        return Strings.isNullOrEmpty(errorPopupMessage);
    }

    public String getErrorPopupMessage() {
        return errorPopupMessage;
    }



}
