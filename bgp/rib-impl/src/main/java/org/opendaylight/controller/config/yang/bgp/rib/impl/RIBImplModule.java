/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
/**
 * Generated file

 * Generated from: yang module name: bgp-rib-impl  yang module local name: rib-impl
 * Generated by: org.opendaylight.controller.config.yangjmxgenerator.plugin.JMXGenerator
 * Generated at: Wed Nov 06 13:02:32 CET 2013
 *
 * Do not modify this file unless it is present under src/main directory
 */
package org.opendaylight.controller.config.yang.bgp.rib.impl;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.opendaylight.controller.config.api.JmxAttributeValidationException;
import org.opendaylight.controller.md.sal.dom.api.DOMDataBroker;
import org.opendaylight.controller.sal.core.api.model.SchemaService;
import org.opendaylight.protocol.bgp.mode.api.PathSelectionMode;
import org.opendaylight.protocol.bgp.openconfig.spi.BGPConfigModuleTracker;
import org.opendaylight.protocol.bgp.openconfig.spi.BGPOpenconfigMapper;
import org.opendaylight.protocol.bgp.openconfig.spi.InstanceConfigurationIdentifier;
import org.opendaylight.protocol.bgp.openconfig.spi.pojo.BGPRibInstanceConfiguration;
import org.opendaylight.protocol.bgp.rib.impl.RIBImpl;
import org.opendaylight.protocol.bgp.rib.impl.spi.BGPBestPathSelection;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.AsNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.multiprotocol.rev130919.BgpTableType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev130925.rib.TablesKey;
import org.opendaylight.yangtools.sal.binding.generator.impl.GeneratedClassLoadingStrategy;
import org.opendaylight.yangtools.yang.model.api.SchemaContextListener;
import org.osgi.framework.BundleContext;

/**
 *
 */
public final class RIBImplModule extends org.opendaylight.controller.config.yang.bgp.rib.impl.AbstractRIBImplModule {

    private static final String IS_NOT_SET = "is not set.";
    private BundleContext bundleContext;

    public RIBImplModule(final org.opendaylight.controller.config.api.ModuleIdentifier name,
            final org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(name, dependencyResolver);
    }

    public RIBImplModule(final org.opendaylight.controller.config.api.ModuleIdentifier name,
            final org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, final RIBImplModule oldModule,
            final java.lang.AutoCloseable oldInstance) {
        super(name, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        JmxAttributeValidationException.checkNotNull(getExtensions(), IS_NOT_SET, extensionsJmxAttribute);
        JmxAttributeValidationException.checkNotNull(getRibId(), IS_NOT_SET, ribIdJmxAttribute);
        JmxAttributeValidationException.checkNotNull(getLocalAs(), IS_NOT_SET, localAsJmxAttribute);
        JmxAttributeValidationException.checkNotNull(getBgpRibId(), IS_NOT_SET, bgpRibIdJmxAttribute);
        JmxAttributeValidationException.checkNotNull(getTcpReconnectStrategy(), IS_NOT_SET, tcpReconnectStrategyJmxAttribute);
        JmxAttributeValidationException.checkNotNull(getSessionReconnectStrategy(), IS_NOT_SET, sessionReconnectStrategyJmxAttribute);
        JmxAttributeValidationException.checkNotNull(getLocalTable(), IS_NOT_SET, localTableJmxAttribute);
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        final AsNumber asNumber = new AsNumber(getLocalAs());
        final Map<TablesKey, PathSelectionMode> pathSelectionStrategies = mapBestPathSelectionStrategyByFamily(getPathSelectionModeDependency());
        final RIBImpl rib = new RIBImpl(getRibId(), asNumber, getBgpRibId(), getClusterId(), getExtensionsDependency(),
            getBgpDispatcherDependency(), getTcpReconnectStrategyDependency(), getCodecTreeFactoryDependency(), getSessionReconnectStrategyDependency(),
            getDataProviderDependency(), getDomDataProviderDependency(), getLocalTableDependency(), pathSelectionStrategies, classLoadingStrategy(),
            new RIBImplModuleTracker(getGlobalWriter()), getOpenconfigProviderDependency());
        registerSchemaContextListener(rib);
        return rib;
    }

    private GeneratedClassLoadingStrategy classLoadingStrategy() {
        return getExtensionsDependency().getClassLoadingStrategy();
    }

    private void registerSchemaContextListener(final RIBImpl rib) {
        final DOMDataBroker domBroker = getDomDataProviderDependency();
        if(domBroker instanceof SchemaService) {
            ((SchemaService) domBroker).registerSchemaContextListener(rib);
        } else {
            // FIXME:Get bundle context and register global schema service from bundle
            // context.
            bundleContext.registerService(SchemaContextListener.class, rib, new Hashtable<String,String>());
        }
    }

    private Map<TablesKey, PathSelectionMode> mapBestPathSelectionStrategyByFamily(final List<BGPBestPathSelection> bestPathSelectionDependency) {
        return Collections.unmodifiableMap(bestPathSelectionDependency.stream().collect(
            Collectors.toMap(st -> new TablesKey(st.getAfi(), st.getSafi()), st -> st.getStrategy())));
    }

    public void setBundleContext(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private BGPOpenconfigMapper<BGPRibInstanceConfiguration> getGlobalWriter() {
        if (getOpenconfigProviderDependency() != null) {
            return getOpenconfigProviderDependency().getOpenConfigMapper(BGPRibInstanceConfiguration.class);
        }
        return null;
    }

    private final class RIBImplModuleTracker implements BGPConfigModuleTracker {

        private final BGPOpenconfigMapper<BGPRibInstanceConfiguration> globalWriter;
        private final BGPRibInstanceConfiguration bgpRibConfig;

        public RIBImplModuleTracker(final BGPOpenconfigMapper<BGPRibInstanceConfiguration> globalWriter) {
            this.globalWriter = globalWriter;
            final InstanceConfigurationIdentifier identifier = new InstanceConfigurationIdentifier(getIdentifier().getInstanceName());
            final List<BgpTableType> tableDependency = getLocalTableDependency();
            final org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.AsNumber as = Rev130715Util.getASNumber(getLocalAs());
            final Ipv4Address bgpRibId = Rev130715Util.getIpv4Address(getBgpRibId());
            final Ipv4Address clusterId = Rev130715Util.getIpv4Address(getClusterId());
            this.bgpRibConfig = new BGPRibInstanceConfiguration(identifier, as, bgpRibId, clusterId, tableDependency);
        }

        @Override
        public void onInstanceCreate() {
            if (globalWriter != null) {
                globalWriter.writeConfiguration(this.bgpRibConfig);
            }
        }

        @Override
        public void onInstanceClose() {
            if (globalWriter != null) {
                globalWriter.removeConfiguration(this.bgpRibConfig);
            }
        }

    }


}
