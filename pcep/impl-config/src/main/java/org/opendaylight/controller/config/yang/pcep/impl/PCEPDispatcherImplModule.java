/**
 * Generated file

 * Generated from: yang module name: pcep-impl  yang module local name: pcep-dispatcher-impl
 * Generated by: org.opendaylight.controller.config.yangjmxgenerator.plugin.JMXGenerator
 * Generated at: Wed Nov 06 13:16:39 CET 2013
 *
 * Do not modify this file unless it is present under src/main directory
 */
package org.opendaylight.controller.config.yang.pcep.impl;

import org.opendaylight.controller.config.api.JmxAttributeValidationException;
import org.opendaylight.protocol.pcep.impl.DefaultPCEPSessionNegotiatorFactory;
import org.opendaylight.protocol.pcep.impl.PCEPDispatcherImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.open.object.Open;

/**
 *
 */
public final class PCEPDispatcherImplModule
extends
org.opendaylight.controller.config.yang.pcep.impl.AbstractPCEPDispatcherImplModule {

	public PCEPDispatcherImplModule(
			final org.opendaylight.controller.config.api.ModuleIdentifier name,
			final org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
		super(name, dependencyResolver);
	}

	public PCEPDispatcherImplModule(
			final org.opendaylight.controller.config.api.ModuleIdentifier name,
			final org.opendaylight.controller.config.api.DependencyResolver dependencyResolver,
			final PCEPDispatcherImplModule oldModule,
			final java.lang.AutoCloseable oldInstance) {
		super(name, dependencyResolver, oldModule, oldInstance);
	}

	@Override
	public void validate() {
		super.validate();
		JmxAttributeValidationException.checkNotNull(getMaxUnknownMessages(),
				"value is not set.", this.maxUnknownMessagesJmxAttribute);
		JmxAttributeValidationException.checkCondition(
				getMaxUnknownMessages() > 0, "Parameter 'maxUnknownMessages' "
						+ "must be greater than 0",
						this.maxUnknownMessagesJmxAttribute);
	}

	@Override
	public java.lang.AutoCloseable createInstance() {
		Open localPrefs = getPcepSessionProposalFactoryDependency()
				.getSessionProposal(null, 0);
		DefaultPCEPSessionNegotiatorFactory negFactory = new DefaultPCEPSessionNegotiatorFactory(
				getTimerDependency(), localPrefs, getMaxUnknownMessages());

		final PCEPDispatcherImpl instance = new PCEPDispatcherImpl(
				getPcepExtensionsDependency().getMessageHandlerRegistry(),
				negFactory, getBossGroupDependency(), getWorkerGroupDependency());
		return instance;
	}
}
