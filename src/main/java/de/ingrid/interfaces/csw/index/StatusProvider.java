/**
 * 
 */
package de.ingrid.interfaces.csw.index;

import java.util.Date;
import java.util.LinkedHashMap;

import org.springframework.stereotype.Service;

/**
 * @author joachim@wemove.com
 * 
 *         Manages status messages. Messages added are sorted chronological.
 * 
 */
@Service
public class StatusProvider {

    private class State {

        protected String value;

        protected Date time;

        State(String value, Date time) {
            this.value = value;
            this.time = time;
        }

    }

    private LinkedHashMap<String, State> states = new LinkedHashMap<String, State>();

    private String msgFormat = "%1$tF %1$tT - %2$s\n";

    /**
     * Add a message to the message list. If the key already exists, the message
     * is updated.
     * 
     * @param key
     * @param value
     */
    public void addState(String key, String value) {
        if (states.containsKey(key)) {
            states.get(key).value = value;
        } else {
            synchronized (this) {
                states.put(key, new State(value, new Date()));
            }
        }
    }

    /**
     * Clear the message list.
     * 
     */
    public void clear() {
        synchronized (this) {
            states.clear();
        }
    }

    /**
     * Get the message list as String. Message entries are formated according to
     * the format. the default format is "%1$tF %1$tT - %2$s\n".
     * 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        synchronized (this) {
            for (State state : states.values()) {
                sb.append(String.format(msgFormat, state.time, state.value));
            }
        }
        return sb.toString();
    }

    public String getMsgFormat() {
        return msgFormat;
    }

    /**
     * Set the message format.
     * 
     * @param msgFormat
     */
    public void setMsgFormat(String msgFormat) {
        this.msgFormat = msgFormat;
    }

}
