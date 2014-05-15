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
package de.nx42.maps4cim.gui.util.event;

/**
 * <p>This interface may be implemented by the Observers, that want to register
 * to an {@link EventArgs}. If the Event gets fired, the update-Method of this interface
 * will be executed. An Object of Type T will be transferred.</p>
 * 
 * <p>Basically this is an implementation of the Observer-Pattern. No magic here :)</p>
 * 
 * @author Sebastian Straub
 */
public interface ObserverArgs<T> {
    
    /**
     * execute the code in this method, when the corresponding {@code Event} is fired.
     */
    void update(T generic);
    
}
