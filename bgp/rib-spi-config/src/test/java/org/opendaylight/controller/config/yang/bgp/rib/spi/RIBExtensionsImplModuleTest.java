/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.config.yang.bgp.rib.spi;

import java.util.List;
import javax.management.ObjectName;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.config.api.jmx.CommitStatus;
import org.opendaylight.controller.config.manager.impl.AbstractConfigTest;
import org.opendaylight.controller.config.manager.impl.factoriesresolver.HardcodedModuleFactoriesResolver;
import org.opendaylight.controller.config.util.ConfigTransactionJMXClient;

public class RIBExtensionsImplModuleTest extends AbstractConfigTest {

    private static final String FACTORY_NAME = RIBExtensionsImplModuleFactory.NAME;
    private static final String INSTANCE_NAME = "bgp-rib-extensions-impl";

    @Before
    public void setUp() throws Exception {
        super.initConfigTransactionManagerImpl(new HardcodedModuleFactoriesResolver(mockedContext, new RIBExtensionsImplModuleFactory()));
    }

    @Test
    public void testCreateBean() throws Exception {
        CommitStatus status = createInstance();
        assertBeanCount(1, FACTORY_NAME);
        assertStatus(status, 1, 0, 0);
    }

    @Test
    public void testReusingOldInstance() throws Exception {
        createInstance();
        ConfigTransactionJMXClient transaction = configRegistryClient.createTransaction();
        assertBeanCount(1, FACTORY_NAME);
        CommitStatus status = transaction.commit();
        assertBeanCount(1, FACTORY_NAME);
        assertStatus(status, 0, 0, 1);
    }

    private CommitStatus createInstance() throws Exception {
        ConfigTransactionJMXClient transaction = configRegistryClient.createTransaction();
        transaction.createModule(FACTORY_NAME, INSTANCE_NAME);
        return transaction.commit();
    }

    public static ObjectName createRIBExtensionsModuleInstance(final ConfigTransactionJMXClient transaction, final List<ObjectName> extensions) throws Exception {
        final ObjectName objectName = transaction.createModule(FACTORY_NAME, INSTANCE_NAME);
        RIBExtensionsImplModuleMXBean mxBean = transaction.newMXBeanProxy(objectName, RIBExtensionsImplModuleMXBean.class);
        mxBean.setExtension(extensions);
        return objectName;
    }
}
