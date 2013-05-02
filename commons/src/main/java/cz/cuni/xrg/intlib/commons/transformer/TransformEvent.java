package cz.cuni.xrg.intlib.commons.transformer;

import cz.cuni.xrg.intlib.commons.event.DPUEvent;
/**
 * Base class for {@link Transform} events
 *
 * @see Transform
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public abstract class TransformEvent extends DPUEvent {

    protected final Transform transformer;
    protected final TransformContext transformContext;

    public TransformEvent(Transform transformer, TransformContext transformContext, Object source) {
        super(source);
        this.transformer = transformer;
        this.transformContext = transformContext;
    }

    /**
     * Returns the {@link Transform} associated with this event.
     *
     * @return
     */
    public Transform getTransformer() {
        return transformer;
    }

    /**
     * Returns the {@link TransformContext} of this execution.
     *
     * @return
     */
    public TransformContext getTransformContext() {
        return transformContext;
    }
}
