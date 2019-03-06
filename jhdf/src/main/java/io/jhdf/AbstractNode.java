package io.jhdf;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jhdf.api.Attribute;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import io.jhdf.api.NodeType;
import io.jhdf.exceptions.HdfException;
import io.jhdf.object.message.AttributeMessage;
import io.jhdf.object.message.Message;

public abstract class AbstractNode implements Node {
	private static final Logger logger = LoggerFactory.getLogger(AbstractNode.class);

	protected final class AttributesLazyInitializer extends LazyInitializer<Map<String, Attribute>> {
		private final LazyInitializer<ObjectHeader> lazyOjbectHeader;

		public AttributesLazyInitializer(LazyInitializer<ObjectHeader> lazyOjbectHeader) {
			this.lazyOjbectHeader = lazyOjbectHeader;
		}

		@Override
		protected Map<String, Attribute> initialize() throws ConcurrentException {
			logger.debug("Lazy initializing attributes for '{}'", getPath());
			final ObjectHeader oh = lazyOjbectHeader.get();
			return oh.getMessagesOfType(AttributeMessage.class).stream()
					.collect(
							toMap(AttributeMessage::getName, message -> new AttributeImpl(AbstractNode.this, message)));
		}
	}

	protected final long address;
	protected final String name;
	protected final Group parent;
	protected final LazyInitializer<ObjectHeader> header;
	protected final AttributesLazyInitializer attributes;

	public AbstractNode(HdfFileChannel hdfFc, long address, String name, Group parent) {
		this.address = address;
		this.name = name;
		this.parent = parent;

		try {
			header = ObjectHeader.lazyReadObjectHeader(hdfFc, address);

			// Attributes
			attributes = new AttributesLazyInitializer(header);
		} catch (Exception e) {
			throw new HdfException("Error reading node '" + getPath() + "' at address " + address, e);
		}
	}

	@Override
	public boolean isGroup() {
		return getType() == NodeType.GROUP;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPath() {
		return parent.getPath() + name;
	}

	@Override
	public Group getParent() {
		return parent;
	}

	@Override
	public long getAddress() {
		return address;
	}

	@Override
	public File getFile() {
		// Recurse back up to the file
		return getParent().getFile();
	}

	@Override
	public HdfFile getHdfFile() {
		return getParent().getHdfFile();
	}

	@Override
	public boolean isLink() {
		return false;
	}

	protected <T extends Message> T getHeaderMessage(Class<T> clazz) {
		try {
			return header.get().getMessageOfType(clazz);
		} catch (ConcurrentException e) {
			throw new HdfException("Failed to get header message of type '" + clazz.hashCode() + "' for '"
					+ getPath() + "' at address '" + getAddress() + "'", e);
		}
	}

	@Override
	public Map<String, Attribute> getAttributes() {
		try {
			return attributes.get();
		} catch (ConcurrentException e) {
			throw new HdfException(
					"Failed to load attributes for '" + getPath() + "' at address '" + getAddress() + "'", e);
		}
	}

	@Override
	public Attribute getAttribute(String name) {
		return getAttributes().get(name);
	}
}