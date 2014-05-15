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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>This is a simple Event, that can be fired by the Subject/Observable to notify
 * all Observers. No Objects can be received the Observers.</p>
 * 
 * <p>This is a slight modification of the original Observer-Pattern, where the
 * subject extends the abstract administration logic. Here the Observable (Event)
 * is an Object that needs to be referenced by the Observers through a getter.</p>
 * 
 * <p>The advantage is that each class can hold any amount of Events</p>
 * 
 * @author Sebastian Straub
 */
public class Event {

    /**
     * List of all Observers registered to this Event.
     */
    protected List<Observer> observers = new CopyOnWriteArrayList<Observer>();

    /**
     * Adds an Observer to this event, which will be notified when the event
     * is fired.
     * @param observer Observer to be notified by this Event
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Removes an Observer from this Event. This Observer will not be notified
     * anymore. If this Event holds multiple instances of the same observer, all
     * of them will be removed
     * @param observer Observer to remove from this Event.
     */
    public void removeObserver(Observer observer) {
        Collection<Observer> remove = new ArrayList<Observer>();
        remove.add(observer);
        observers.removeAll(remove);
    }

    /**
     * Clears the list of Observers
     */
    public void removeAllObservers() {
        observers.clear();
    }

    /**
     * Fires the event and notifies all Observers that were registered in the
     * moment the event was fired.
     * 
     * Implementation: create a local copy of the list and iterate over all
     * observers, that were registered when the copy was created.
     * Problem: observers that were removed while iteration is in progress
     * will be notified anyway!
     */
    public void fire() {
        ListIterator<Observer> it = observers.listIterator();
        while (it.hasNext()) {
            it.next().update();
        }
    }

    /**
     * Checks, if there are any Observers registered to this Event.
     * @return true, if at least one Observer is registered
     */
    public boolean hasListeners() {
        return !observers.isEmpty();
    }
    
    /**
     * Returns the amount of Observers registered to this Event.
     * @return amount of currently registered Observers.
     */
    public int numberOfListeners() {
        return observers.size();
    }
}
