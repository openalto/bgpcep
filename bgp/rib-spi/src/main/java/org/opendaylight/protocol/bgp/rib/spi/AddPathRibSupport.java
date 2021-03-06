/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.bgp.rib.spi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.NodeIdentifierWithPredicates;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.PathArgument;

/**
 * Interface implemented to be extended by RibSupport.
 * This interface exposes methods to access to Add Path information
 * By default we implement non supported Multiple Path therefore
 * 0 Path Id is returned and null PathArgument
 * Deprecated: All families should support additional path
 */
@Deprecated
interface AddPathRibSupport {
    /**
     * Construct a PathArgument to an AddPathRoute.
     *
     * @param pathId  The path identifier
     * @param routeId PathArgument leaf path
     * @return routeId PathArgument + pathId or Null in case Add-path is not supported
     */
    @Deprecated
    @Nullable
    default PathArgument getRouteIdAddPath(long pathId, @Nonnull PathArgument routeId) {
        return null;
    }

    /**
     * Create a new Path Argument for route Key removing remove Path Id from key.
     * For extension which do not support Multiple Path this step is not required.
     *
     * @param routeKey routeKey Path Argument
     * @return new route Key
     */
    @Deprecated
    default NodeIdentifierWithPredicates createRouteKeyPathArgument(@Nonnull NodeIdentifierWithPredicates routeKey) {
        return null;
    }
}
