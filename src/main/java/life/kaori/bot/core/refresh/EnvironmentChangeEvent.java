package life.kaori.bot.core.refresh;

import org.springframework.context.ApplicationEvent;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 *
 * author: origin 2023/7/7 14:39
 */
@SuppressWarnings("serial")
public class EnvironmentChangeEvent extends ApplicationEvent {

    private Set<String> keys;

    public EnvironmentChangeEvent(Set<String> keys) {
        // Backwards compatible constructor with less utility (practically no use at all)
        this(keys, keys);
    }

    public EnvironmentChangeEvent(Object context, Set<String> keys) {
        super(context);
        this.keys = keys;
    }

    /**
     * @return The keys.
     */
    public Set<String> getKeys() {
        return this.keys;
    }

}
