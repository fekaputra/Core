package cz.cuni.xrg.intlib.backend.dpu.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecordType;
import cz.cuni.xrg.intlib.commons.message.MessageType;

/**
 * Class for representing DPURecord messages send by ProcessingContext
 * sendMessage.
 * 
 * @author Petyr
 * 
 */
public final class DPUMessage extends DPUEvent {

	private static final Logger LOG = LoggerFactory.getLogger(DPUMessage.class);
	
	public DPUMessage(String shortMessage,
			String longMessage,
			MessageType type,
			Context context,
			Object source) {
		super(context, source, MessageRecordType.fromMessageType(type),
				shortMessage, longMessage);
		// log based on type of message
		switch(type) {
		case DEBUG:
			LOG.debug("DPU '{}' publish message Type:{} Short:{} Long:{}", 
					context.getDpuInstance().getName(),
					type.toString(), 
					shortMessage, 
					longMessage);
			break;
		case ERROR:
			LOG.error("DPU '{}' publish message Type:{} Short:{} Long:{}", 
					context.getDpuInstance().getName(),
					type.toString(), 
					shortMessage, 
					longMessage);
			break;
		case INFO:
			LOG.info("DPU '{}' publish message Type:{} Short:{} Long:{}", 
					context.getDpuInstance().getName(),
					type.toString(), 
					shortMessage, 
					longMessage);
			break;
		case WARNING:
			LOG.warn("DPU '{}' publish message Type:{} Short:{} Long:{}", 
					context.getDpuInstance().getName(),
					type.toString(), 
					shortMessage, 
					longMessage);
			break;
		}
	}

}
