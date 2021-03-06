/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.bgp.rib.impl.config;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.opendaylight.protocol.bgp.rib.impl.config.BgpPeerTest.AFI_SAFI;
import static org.opendaylight.protocol.bgp.rib.impl.config.BgpPeerTest.AFI_SAFI_IPV4;
import static org.opendaylight.protocol.bgp.rib.impl.config.BgpPeerTest.MD5_PASSWORD;
import static org.opendaylight.protocol.bgp.rib.impl.config.BgpPeerTest.NEIGHBOR_ADDRESS;
import static org.opendaylight.protocol.bgp.rib.impl.config.BgpPeerTest.PORT;
import static org.opendaylight.protocol.bgp.rib.impl.config.BgpPeerTest.SHORT;
import static org.opendaylight.protocol.bgp.rib.impl.config.BgpPeerTest.createAfiSafi;
import static org.opendaylight.protocol.bgp.rib.impl.config.BgpPeerTest.createNeighborExpected;
import static org.opendaylight.protocol.bgp.rib.impl.config.OpenConfigMappingUtil.HOLDTIMER;
import static org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IetfInetUtil.INSTANCE;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Shorts;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opendaylight.protocol.bgp.mode.api.PathSelectionMode;
import org.opendaylight.protocol.bgp.mode.impl.add.all.paths.AllPathSelection;
import org.opendaylight.protocol.bgp.mode.impl.add.n.paths.AddPathBestNPathSelection;
import org.opendaylight.protocol.bgp.openconfig.spi.BGPTableTypeRegistryConsumer;
import org.opendaylight.protocol.bgp.parser.BgpTableTypeImpl;
import org.opendaylight.protocol.bgp.rib.impl.BGPPeerTrackerImpl;
import org.opendaylight.protocol.bgp.rib.impl.spi.RIB;
import org.opendaylight.protocol.bgp.rib.spi.BGPPeerTracker;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.multiprotocol.rev151009.bgp.common.afi.safi.list.AfiSafi;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.multiprotocol.rev151009.bgp.common.afi.safi.list.AfiSafiBuilder;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.BgpNeighborTransportConfig;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor.group.AfiSafis;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor.group.AfiSafisBuilder;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor.group.ConfigBuilder;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor.group.RouteReflectorBuilder;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor.group.TimersBuilder;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor.group.Transport;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor.group.TransportBuilder;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor.group.transport.Config;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbors.Neighbor;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbors.NeighborBuilder;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbors.NeighborKey;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.peer.group.PeerGroup;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.peer.group.PeerGroupBuilder;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.top.Bgp;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.top.bgp.Neighbors;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.types.rev151009.IPV4UNICAST;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.types.rev151009.IPV6LABELLEDUNICAST;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.types.rev151009.IPV6UNICAST;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.types.rev151009.PeerType;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.types.rev151009.RrClusterIdType;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.network.instance.rev151018.network.instance.top.NetworkInstances;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.network.instance.rev151018.network.instance.top.network.instances.NetworkInstance;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.network.instance.rev151018.network.instance.top.network.instances.NetworkInstanceKey;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.network.instance.rev151018.network.instance.top.network.instances.network.instance.Protocols;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.network.instance.rev151018.network.instance.top.network.instances.network.instance.protocols.Protocol;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.network.instance.rev151018.network.instance.top.network.instances.network.instance.protocols.ProtocolKey;
import org.opendaylight.yang.gen.v1.http.openconfig.net.yang.policy.types.rev151009.BGP;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.AsNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.multiprotocol.rev180329.BgpTableType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.multiprotocol.rev180329.SendReceive;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.multiprotocol.rev180329.mp.capabilities.add.path.capability.AddressFamilies;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.multiprotocol.rev180329.mp.capabilities.add.path.capability.AddressFamiliesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.GlobalAddPathsConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.GlobalAddPathsConfigBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.GlobalConfigAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.GlobalConfigAugmentationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.NeighborAddPathsConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.NeighborAddPathsConfigBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.NeighborPeerGroupConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.NeighborPeerGroupConfigBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.NeighborTransportConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.NeighborTransportConfigBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.NetworkInstanceProtocol;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.PeerGroupTransportConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.openconfig.extensions.rev180329.PeerGroupTransportConfigBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev180329.PeerRole;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev180329.Ipv4AddressFamily;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev180329.Ipv6AddressFamily;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev180329.MplsLabeledVpnSubsequentAddressFamily;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev180329.UnicastSubsequentAddressFamily;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class OpenConfigMappingUtilTest {
    private static final Neighbor NEIGHBOR = createNeighborExpected(NEIGHBOR_ADDRESS);
    private static final String KEY = "bgp";
    private static final InstanceIdentifier<Bgp> BGP_II = InstanceIdentifier.create(NetworkInstances.class)
        .child(NetworkInstance.class, new NetworkInstanceKey("identifier-test")).child(Protocols.class)
        .child(Protocol.class, new ProtocolKey(BGP.class, KEY)).augmentation(NetworkInstanceProtocol.class)
            .child(Bgp.class);
    private static final NeighborKey NEIGHBOR_KEY = new NeighborKey(NEIGHBOR_ADDRESS);
    private static final Ipv4Address ROUTER_ID = new Ipv4Address("1.2.3.4");
    private static final Ipv4Address CLUSTER_ID = new Ipv4Address("4.3.2.1");
    private static final Ipv4Address LOCAL_HOST = new Ipv4Address("127.0.0.1");

    private static final Long ALL_PATHS = 0L;
    private static final Long N_PATHS = 2L;
    private static final BGPPeerTracker PEER_TRACKER = new BGPPeerTrackerImpl();
    private static final PathSelectionMode ADD_PATH_BEST_N_PATH_SELECTION = new AddPathBestNPathSelection(N_PATHS,
            PEER_TRACKER);
    private static final PathSelectionMode ADD_PATH_BEST_ALL_PATH_SELECTION = new AllPathSelection(PEER_TRACKER);
    private static final BgpTableType BGP_TABLE_TYPE_IPV4 = new BgpTableTypeImpl(Ipv4AddressFamily.class,
            UnicastSubsequentAddressFamily.class);
    private static final BgpTableType BGP_TABLE_TYPE_IPV6
            = new BgpTableTypeImpl(Ipv6AddressFamily.class, UnicastSubsequentAddressFamily.class);

    private static final AsNumber AS = new AsNumber(72L);
    private static final AsNumber GLOBAL_AS = new AsNumber(73L);
    private static final List<AddressFamilies> FAMILIES;
    private static final List<BgpTableType> TABLE_TYPES;
    private static final List<AfiSafi> AFISAFIS = new ArrayList<>();
    private static final BigDecimal DEFAULT_TIMERS = BigDecimal.valueOf(30);

    static {
        FAMILIES = new ArrayList<>();
        FAMILIES.add(new AddressFamiliesBuilder()
                .setAfi(Ipv4AddressFamily.class).setSafi(UnicastSubsequentAddressFamily.class)
                .setSendReceive(SendReceive.Both).build());
        FAMILIES.add(new AddressFamiliesBuilder()
                .setAfi(Ipv6AddressFamily.class).setSafi(UnicastSubsequentAddressFamily.class)
                .setSendReceive(SendReceive.Send).build());
        FAMILIES.add(new AddressFamiliesBuilder()
                .setAfi(Ipv6AddressFamily.class).setSafi(MplsLabeledVpnSubsequentAddressFamily.class)
                .setSendReceive(SendReceive.Receive).build());
        TABLE_TYPES = new ArrayList<>();
        TABLE_TYPES.add(new BgpTableTypeImpl(Ipv4AddressFamily.class, UnicastSubsequentAddressFamily.class));
        TABLE_TYPES.add(new BgpTableTypeImpl(Ipv6AddressFamily.class, UnicastSubsequentAddressFamily.class));
        AFISAFIS.add(new AfiSafiBuilder().setAfiSafiName(IPV4UNICAST.class)
                .addAugmentation(GlobalAddPathsConfig.class, new GlobalAddPathsConfigBuilder().setReceive(true)
                        .setSendMax(Shorts.checkedCast(N_PATHS)).build()).build());
        AFISAFIS.add(new AfiSafiBuilder().setAfiSafiName(IPV6UNICAST.class)
                .addAugmentation(GlobalAddPathsConfig.class, new GlobalAddPathsConfigBuilder().setReceive(true)
                        .setSendMax(Shorts.checkedCast(ALL_PATHS)).build()).build());
    }

    @Mock
    private BGPTableTypeRegistryConsumer tableTypeRegistry;

    @Mock
    private RIB rib;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.doReturn(java.util.Optional.of(BGP_TABLE_TYPE_IPV4))
            .when(this.tableTypeRegistry).getTableType(IPV4UNICAST.class);
        Mockito.doReturn(java.util.Optional.of(BGP_TABLE_TYPE_IPV6))
            .when(this.tableTypeRegistry).getTableType(IPV6UNICAST.class);
        Mockito.doReturn(java.util.Optional.of(new BgpTableTypeImpl(Ipv6AddressFamily.class,
                MplsLabeledVpnSubsequentAddressFamily.class)))
            .when(this.tableTypeRegistry).getTableType(IPV6LABELLEDUNICAST.class);
        Mockito.doReturn(java.util.Optional.of(IPV4UNICAST.class))
            .when(this.tableTypeRegistry).getAfiSafiType(BGP_TABLE_TYPE_IPV4);
        Mockito.doReturn(java.util.Optional.of(IPV6UNICAST.class))
            .when(this.tableTypeRegistry).getAfiSafiType(BGP_TABLE_TYPE_IPV6);
        Mockito.doReturn(AS).when(this.rib).getLocalAs();
    }

    @Test
    public void testGetRibInstanceName() {
        assertEquals(KEY, OpenConfigMappingUtil.getRibInstanceName(BGP_II));
    }

    @Test
    public void testGetHoldTimer() {
        TimersBuilder builder = new TimersBuilder().setConfig(new org.opendaylight.yang.gen.v1.http.openconfig.net
                .yang.bgp.rev151009.bgp.neighbor.group.timers.ConfigBuilder().setHoldTime(BigDecimal.TEN).build());

        assertEquals(DEFAULT_TIMERS.toBigInteger().intValue(),
                OpenConfigMappingUtil.getHoldTimer(NEIGHBOR, null));
        assertEquals(HOLDTIMER,
                OpenConfigMappingUtil.getHoldTimer(new NeighborBuilder().build(), null));

        assertEquals(DEFAULT_TIMERS.toBigInteger().intValue(),
                OpenConfigMappingUtil.getHoldTimer(NEIGHBOR, new PeerGroupBuilder().build()));
        assertEquals(BigDecimal.TEN.intValue(), OpenConfigMappingUtil.getHoldTimer(NEIGHBOR, new PeerGroupBuilder()
                .setTimers(builder.build()).build()));
    }

    @Test
    public void testGetRemotePeerAs() {
        final ConfigBuilder configBuilder = new ConfigBuilder();
        assertEquals(AS, OpenConfigMappingUtil.getRemotePeerAs(NEIGHBOR.getConfig(), null, null));
        assertEquals(AS, OpenConfigMappingUtil.getRemotePeerAs(configBuilder.build(), null,
                this.rib.getLocalAs()));

        assertEquals(AS, OpenConfigMappingUtil.getRemotePeerAs(NEIGHBOR.getConfig(),
                new PeerGroupBuilder().build(), null));
        assertEquals(AS, OpenConfigMappingUtil.getRemotePeerAs(configBuilder.build(), new PeerGroupBuilder().build(),
                this.rib.getLocalAs()));

        assertEquals(AS, OpenConfigMappingUtil.getRemotePeerAs(null, new PeerGroupBuilder()
                        .setConfig(new ConfigBuilder().setPeerAs(AS).build()).build(), null));
    }

    @Test
    public void testGetLocalPeerAs() {
        final ConfigBuilder configBuilder = new ConfigBuilder();
        assertEquals(GLOBAL_AS,OpenConfigMappingUtil.getLocalPeerAs(null, GLOBAL_AS));
        assertEquals(AS, OpenConfigMappingUtil.getLocalPeerAs(configBuilder.setLocalAs(AS).build(), GLOBAL_AS));
    }

    @Test
    public void testIsActive() {
        final TransportBuilder builder = new TransportBuilder();
        assertTrue(OpenConfigMappingUtil.isActive(new NeighborBuilder().build(), null));
        assertTrue(OpenConfigMappingUtil.isActive(new NeighborBuilder()
                .setTransport(builder.build()).build(), null));

        final Transport activeFalse = builder.setConfig(new org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp
                .rev151009.bgp.neighbor.group.transport.ConfigBuilder().setPassiveMode(true).build()).build();
        assertFalse(OpenConfigMappingUtil.isActive(new NeighborBuilder().setTransport(activeFalse).build(),
                null));

        assertTrue(OpenConfigMappingUtil.isActive(new NeighborBuilder().build(),
                new PeerGroupBuilder().build()));
        assertFalse(OpenConfigMappingUtil.isActive(new NeighborBuilder().build(),
                new PeerGroupBuilder().setTransport(activeFalse).build()));
    }

    @Test
    public void testGetRetryTimer() {
        assertEquals(DEFAULT_TIMERS.toBigInteger().intValue(),
                OpenConfigMappingUtil.getRetryTimer(NEIGHBOR, null));
        assertEquals(DEFAULT_TIMERS.toBigInteger().intValue(),
                OpenConfigMappingUtil.getRetryTimer(new NeighborBuilder().build(), null));
        TimersBuilder builder = new TimersBuilder().setConfig(new org.opendaylight.yang.gen.v1.http.openconfig.net
                .yang.bgp.rev151009.bgp.neighbor.group.timers.ConfigBuilder().setConnectRetry(BigDecimal.TEN).build());
        assertEquals(BigDecimal.TEN.intValue(), OpenConfigMappingUtil.getRetryTimer(new NeighborBuilder()
                .setTimers(builder.build()).build(), null));

        assertEquals(DEFAULT_TIMERS.toBigInteger().intValue(),
                OpenConfigMappingUtil.getRetryTimer(NEIGHBOR, new PeerGroupBuilder().build()));
        assertEquals(BigDecimal.TEN.intValue(), OpenConfigMappingUtil.getRetryTimer(NEIGHBOR,
                new PeerGroupBuilder().setTimers(builder.build()).build()));
    }

    @Test
    public void testGetNeighborKey() {
        assertArrayEquals(MD5_PASSWORD.getBytes(StandardCharsets.US_ASCII),
            OpenConfigMappingUtil.getNeighborKey(NEIGHBOR).get(INSTANCE.inetAddressFor(NEIGHBOR_ADDRESS)));
        assertNull(OpenConfigMappingUtil.getNeighborKey(new NeighborBuilder().build()));
        assertNull(OpenConfigMappingUtil.getNeighborKey(new NeighborBuilder().setConfig(new ConfigBuilder()
                .build()).build()));
    }

    @Test
    public void testGetNeighborInstanceIdentifier() {
        assertEquals(BGP_II.child(Neighbors.class).child(Neighbor.class, NEIGHBOR_KEY),
            OpenConfigMappingUtil.getNeighborInstanceIdentifier(BGP_II, NEIGHBOR_KEY));

    }

    @Test
    public void testGetNeighborInstanceName() {
        assertEquals(NEIGHBOR_ADDRESS.getIpv4Address().getValue(), OpenConfigMappingUtil
                .getNeighborInstanceName(BGP_II.child(Neighbors.class).child(Neighbor.class, NEIGHBOR_KEY)));
    }

    @Test
    public void testGetPort() {
        final TransportBuilder transport = new TransportBuilder();
        assertEquals(PORT, OpenConfigMappingUtil.getPort(NEIGHBOR, null));
        assertEquals(PORT, OpenConfigMappingUtil.getPort(new NeighborBuilder()
                .setTransport(transport.build()).build(), null));
        assertEquals(PORT, OpenConfigMappingUtil.getPort(new NeighborBuilder().setTransport(
                transport.setConfig(new org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor
                        .group.transport.ConfigBuilder().build()).build()).build(), null));
        final PortNumber newPort = new PortNumber(111);
        final Config portConfig = new org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor
                .group.transport.ConfigBuilder().addAugmentation(NeighborTransportConfig.class,
                new NeighborTransportConfigBuilder().setRemotePort(newPort).build()).build();
        assertEquals(newPort, OpenConfigMappingUtil.getPort(new NeighborBuilder().setTransport(
                transport.setConfig(portConfig).build()).build(), null));

        assertEquals(newPort, OpenConfigMappingUtil.getPort(new NeighborBuilder()
                .setTransport(transport.setConfig(portConfig).build()).build(), new PeerGroupBuilder().build()));

        final Config portConfigGroup = new org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp
                .neighbor.group.transport.ConfigBuilder().addAugmentation(PeerGroupTransportConfig.class,
                new PeerGroupTransportConfigBuilder().setRemotePort(newPort).build()).build();
        assertEquals(newPort, OpenConfigMappingUtil.getPort(new NeighborBuilder().build(), new PeerGroupBuilder()
                .setTransport(transport.setConfig(portConfigGroup).build()).build()));
    }

    @Test
    public void testGetLocalAddress() {
        assertNull(OpenConfigMappingUtil.getLocalAddress(null));
        final TransportBuilder transport = new TransportBuilder();
        assertNull(OpenConfigMappingUtil.getLocalAddress(transport.build()));
        assertNull(OpenConfigMappingUtil.getLocalAddress(transport.setConfig(
                new org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor.group.transport
                        .ConfigBuilder().build()).build()));
        assertEquals(new IpAddress(LOCAL_HOST), OpenConfigMappingUtil.getLocalAddress(transport.setConfig(
                new org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor.group.transport
                        .ConfigBuilder().setLocalAddress(new BgpNeighborTransportConfig
                        .LocalAddress(new IpAddress(new Ipv4Address(LOCAL_HOST.getValue())))).build()).build()));
    }

    @Test
    public void testGetAfiSafiWithDefault() {
        final ImmutableList<AfiSafi> defaultValue
                = ImmutableList.of(new AfiSafiBuilder().setAfiSafiName(IPV4UNICAST.class).build());
        assertEquals(defaultValue, OpenConfigMappingUtil.getAfiSafiWithDefault(null, true));
        final AfiSafis afiSafi = new AfiSafisBuilder().build();
        assertEquals(defaultValue, OpenConfigMappingUtil.getAfiSafiWithDefault(afiSafi, true));

        final AfiSafi afiSafiIpv6 = new AfiSafiBuilder().setAfiSafiName(IPV6UNICAST.class)
                .addAugmentation(NeighborAddPathsConfig.class,
            new NeighborAddPathsConfigBuilder().setReceive(true).setSendMax(SHORT).build()).build();
        final List<AfiSafi> afiSafiIpv6List = new ArrayList<>();
        afiSafiIpv6List.add(afiSafiIpv6);

        final List<AfiSafi> expected = new ArrayList<>(afiSafiIpv6List);
        expected.add(AFI_SAFI_IPV4);
        assertEquals(afiSafiIpv6, OpenConfigMappingUtil.getAfiSafiWithDefault(new AfiSafisBuilder()
                .setAfiSafi(afiSafiIpv6List).build(), true).get(0));
        assertEquals(new AfiSafiBuilder().setAfiSafiName(IPV4UNICAST.class).build(),
                OpenConfigMappingUtil.getAfiSafiWithDefault(new AfiSafisBuilder().setAfiSafi(afiSafiIpv6List).build(),
                        true).get(1));
        assertEquals(AFI_SAFI, OpenConfigMappingUtil.getAfiSafiWithDefault(createAfiSafi(), true));

        assertTrue(OpenConfigMappingUtil.getAfiSafiWithDefault(null, false).isEmpty());
        assertTrue(OpenConfigMappingUtil.getAfiSafiWithDefault(afiSafi, false).isEmpty());
        assertEquals(afiSafiIpv6, OpenConfigMappingUtil.getAfiSafiWithDefault(new AfiSafisBuilder()
                .setAfiSafi(afiSafiIpv6List).build(), false).get(0));
        assertEquals(new AfiSafiBuilder().setAfiSafiName(IPV4UNICAST.class).build(),
            OpenConfigMappingUtil.getAfiSafiWithDefault(new AfiSafisBuilder().setAfiSafi(afiSafiIpv6List).build(),
                    false).get(1));
        assertEquals(AFI_SAFI, OpenConfigMappingUtil.getAfiSafiWithDefault(createAfiSafi(), false));
    }

    @Test
    public void testGetGlobalClusterIdentifier() {
        final org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.global.base
                .ConfigBuilder configBuilder = new org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009
                .bgp.global.base.ConfigBuilder();
        configBuilder.setRouterId(ROUTER_ID);
        assertEquals(ROUTER_ID.getValue(),
                OpenConfigMappingUtil.getGlobalClusterIdentifier(configBuilder.build()).getValue());

        configBuilder.addAugmentation(GlobalConfigAugmentation.class, new GlobalConfigAugmentationBuilder()
                .setRouteReflectorClusterId(new RrClusterIdType(CLUSTER_ID)).build()).build();
        assertEquals(CLUSTER_ID.getValue(),
                OpenConfigMappingUtil.getGlobalClusterIdentifier(configBuilder.build()).getValue());
    }

    @Test
    public void testGetNeighborClusterIdentifier() {

        assertNull(OpenConfigMappingUtil.getNeighborClusterIdentifier(null, null));

        final PeerGroupBuilder peerGroup = new PeerGroupBuilder();
        assertNull(OpenConfigMappingUtil.getNeighborClusterIdentifier(null, peerGroup.build()));

        final RouteReflectorBuilder configBuilder = new RouteReflectorBuilder();
        assertNull(OpenConfigMappingUtil.getNeighborClusterIdentifier(configBuilder.build(), peerGroup.build()));

        configBuilder.setConfig(new org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp.neighbor
                .group.route.reflector.ConfigBuilder()
                .setRouteReflectorClusterId(new RrClusterIdType(CLUSTER_ID)).build()).build();
        assertEquals(CLUSTER_ID.getValue(), OpenConfigMappingUtil.getNeighborClusterIdentifier(configBuilder.build(),
                peerGroup.build()).getValue());

        assertEquals(CLUSTER_ID.getValue(), OpenConfigMappingUtil.getNeighborClusterIdentifier(null,
                peerGroup.setRouteReflector(configBuilder.build()).build()).getValue());
    }

    @Test
    public void isAppNeighbor() {
        assertFalse(OpenConfigMappingUtil.isApplicationPeer(new NeighborBuilder()
                .setConfig(new ConfigBuilder().build()).build()));
        final Neighbor neighbor = new NeighborBuilder().setConfig(new ConfigBuilder()
                .addAugmentation(NeighborPeerGroupConfig.class, new NeighborPeerGroupConfigBuilder()
                        .setPeerGroup(OpenConfigMappingUtil.APPLICATION_PEER_GROUP_NAME).build()).build()).build();
        assertTrue(OpenConfigMappingUtil.isApplicationPeer(neighbor));
    }

    @Test
    public void toPathSelectionMode() {
        final List<AfiSafi> families = new ArrayList<>();
        families.add(new AfiSafiBuilder().setAfiSafiName(IPV4UNICAST.class)
            .addAugmentation(GlobalAddPathsConfig.class, new GlobalAddPathsConfigBuilder()
                    .setSendMax(Shorts.checkedCast(N_PATHS)).build()).build());
        families.add(new AfiSafiBuilder().setAfiSafiName(IPV6UNICAST.class)
            .addAugmentation(GlobalAddPathsConfig.class, new GlobalAddPathsConfigBuilder()
                    .setSendMax(Shorts.checkedCast(ALL_PATHS)).build()).build());
        final Map<BgpTableType, PathSelectionMode> result = OpenConfigMappingUtil
                .toPathSelectionMode(families, this.tableTypeRegistry, PEER_TRACKER);
        final Map<BgpTableType, PathSelectionMode> expected = new HashMap<>();
        expected.put(new BgpTableTypeImpl(Ipv4AddressFamily.class, UnicastSubsequentAddressFamily.class),
                ADD_PATH_BEST_N_PATH_SELECTION);
        expected.put(new BgpTableTypeImpl(Ipv6AddressFamily.class, UnicastSubsequentAddressFamily.class),
                ADD_PATH_BEST_ALL_PATH_SELECTION);
        assertEquals(expected.get(0), result.get(0));
        assertEquals(expected.get(1), result.get(1));
    }

    @Test
    public void toPeerRole() {
        Neighbor neighbor = new NeighborBuilder().setConfig(new ConfigBuilder()
                .setPeerType(PeerType.EXTERNAL).build()).build();
        PeerRole peerRoleResult = OpenConfigMappingUtil.toPeerRole(neighbor);
        Assert.assertEquals(PeerRole.Ebgp, peerRoleResult);

        neighbor = new NeighborBuilder().setConfig(new ConfigBuilder().setPeerType(PeerType.INTERNAL).build()).build();
        peerRoleResult = OpenConfigMappingUtil.toPeerRole(neighbor);
        Assert.assertEquals(PeerRole.Ibgp, peerRoleResult);

        neighbor = new NeighborBuilder()
            .setRouteReflector(new RouteReflectorBuilder().setConfig(
                    new org.opendaylight.yang.gen.v1.http.openconfig.net.yang.bgp.rev151009.bgp
                .neighbor.group.route.reflector.ConfigBuilder().setRouteReflectorClient(true).build()).build()).build();
        peerRoleResult = OpenConfigMappingUtil.toPeerRole(neighbor);
        Assert.assertEquals(PeerRole.RrClient, peerRoleResult);
    }

    @Test
    public void toAddPathCapability() {
        final List<AfiSafi> families = new ArrayList<>();
        families.add(new AfiSafiBuilder().setAfiSafiName(IPV4UNICAST.class)
            .addAugmentation(NeighborAddPathsConfig.class,
                new NeighborAddPathsConfigBuilder()
                    .setReceive(true).setSendMax(Shorts.checkedCast(ALL_PATHS)).build()).build());
        families.add(new AfiSafiBuilder().setAfiSafiName(IPV6UNICAST.class)
            .addAugmentation(NeighborAddPathsConfig.class,
                new NeighborAddPathsConfigBuilder()
                    .setReceive(false).setSendMax(Shorts.checkedCast(N_PATHS)).build()).build());
        families.add(new AfiSafiBuilder().setAfiSafiName(IPV6LABELLEDUNICAST.class)
            .addAugmentation(NeighborAddPathsConfig.class,
                new NeighborAddPathsConfigBuilder()
                    .setReceive(false).build()).build());
        final List<AddressFamilies> result = OpenConfigMappingUtil
                .toAddPathCapability(families, this.tableTypeRegistry);
        assertEquals(FAMILIES, result);
    }
}