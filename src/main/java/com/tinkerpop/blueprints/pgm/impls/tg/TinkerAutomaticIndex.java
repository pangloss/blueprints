package com.tinkerpop.blueprints.pgm.impls.tg;

import com.tinkerpop.blueprints.pgm.AutomaticIndex;
import com.tinkerpop.blueprints.pgm.Edge;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class TinkerAutomaticIndex<T extends TinkerElement> extends TinkerIndex<T> implements AutomaticIndex<T> {

    boolean indexEverything = true;
    Set<String> autoIndexKeys = new HashSet<String>();

    public TinkerAutomaticIndex(String name, Class<T> indexClass) {
        super(name, indexClass);
    }

    public Type getIndexType() {
        return Type.AUTOMATIC;
    }

    public void addAutoIndexKey(final String key) {
        if (null == key)
            this.indexEverything = true;
        else {
            this.indexEverything = false;
            this.autoIndexKeys.add(key);
        }
    }

    public void removeAutoIndexKey(final String key) {
        this.indexEverything = false;
        this.autoIndexKeys.remove(key);
    }

    public Set<String> getAutoIndexKeys() {
        return this.autoIndexKeys;
    }

    public void removeElement(final T element) {
        for (String key : autoIndexKeys) {
            Object value;
            if (Edge.class.isAssignableFrom(this.getIndexClass()) && key == AutomaticIndex.LABEL)
                value = ((TinkerEdge) element).getLabel();
            else
                value = element.getProperty(key);
            if (value != null) {
                this.remove(key, value, element);
            }
        }
    }

    public void addElement(final T element) {
        for (String key: autoIndexKeys) {
            Object value;
            if (Edge.class.isAssignableFrom(this.getIndexClass()) && key == AutomaticIndex.LABEL)
                value = ((TinkerEdge) element).getLabel();
            else
                value = element.getProperty(key);
            if (value != null) {
                this.put(key, value, element);
            }
        }
    }

    protected void autoUpdate(final String key, final Object newValue, final Object oldValue, final T element) {
        if (this.indexEverything && !this.autoIndexKeys.contains(key))
            this.autoIndexKeys.add(key);
        if (this.getIndexClass().isAssignableFrom(element.getClass()) && this.autoIndexKeys.contains(key)) {
            if (oldValue != null)
                this.remove(key, oldValue, element);
            this.put(key, newValue, element);
        }
    }

    protected void autoRemove(final String key, final Object oldValue, final T element) {
        if (this.getIndexClass().isAssignableFrom(element.getClass()) && this.autoIndexKeys.contains(key)) {
            this.remove(key, oldValue, element);
        }
    }
}
