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
package de.nx42.maps4cim.util;

import static java.lang.String.format;

import java.util.LinkedList;
import java.util.List;

/**
 * Stores the result of an arbitrary operation.
 * The result is a simple distinction of success and failure, but in case
 * of failure it is possible to append any amount of error messages.
 * 
 * It is possible at any time to retrieve the result and to print a detailed
 * report, which is especially interesting in the case of failure
 * 
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class Result {

    /** the name of the task this result is for */
    protected String task;
    /** the messages stored for this task */
    protected List<String> messages = new LinkedList<String>();
    /** the state of this result
        (null = undecided, false = failure, true = success) */
    protected Boolean success;

    /**
     * Initializes a new Result for a task with the specified name in an
     * undecided state
     * @param task the name of the associated task
     */
    public Result(String task) {
        this.task = task;
    }

    /**
     * Initializes a new Result for a task and sets the initial result
     * (can be changed later)
     * @param task the name of the associated task
     * @param success true, if the result shall be set to successful, else false
     */
    public Result(String task, boolean success) {
        this.task = task;
        this.success = success;
    }

    /**
     * Sets the result to "Success"
     * @return the current result
     */
    public Result success() {
        this.success = true;
        return this;
    }

    /**
     * Sets the result to "Failure"
     * @param message the failure message to append
     * @return the current result
     */
    public Result failure(String message) {
        this.success = false;
        messages.add(message);
        return this;
    }

    /**
     * @return true, iff the result is a success (not null and not a failure)
     */
    public boolean isSuccess() {
        return (success != null) && success;
    }

    /**
     * @return true, if the result is a failure, or still undecided
     */
    public boolean isFailure() {
        return success == null || !success;
    }

    /**
     * Generates a detailed report containing the name of the task, the state
     * of this Result and any error messages, if applicable
     * @return the report for the current state of this result
     */
    public String getReport() {
        if(success == null) {
            return format("The result of task \"%s\" is not yet decided", task);
        } else if(success) {
            return format("The task \"%s\" was successfully completed", task);
        } else {
            StringBuilder sb = new StringBuilder(256);
            sb.append("The task \"");
            sb.append(task);
            sb.append("\" finished with errors:");
            for (String message : messages) {
                sb.append('\n');
                sb.append(message);
            }
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        return getReport();
    }

}
