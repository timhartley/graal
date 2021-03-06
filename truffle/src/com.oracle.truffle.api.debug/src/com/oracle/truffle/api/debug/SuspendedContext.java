/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.truffle.api.debug;

import com.oracle.truffle.api.instrumentation.EventBinding;
import com.oracle.truffle.api.instrumentation.EventContext;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

/**
 * Debugger abstraction of EventContext.
 */
interface SuspendedContext {

    static SuspendedContext create(EventContext eventContext) {
        return new SuspendedEventContext(eventContext);
    }

    static SuspendedContext create(Node node, ThreadDeath unwind) {
        return new CallerEventContext(node, unwind);
    }

    /**
     * Stack depth of this context.
     */
    int getStackDepth();

    SourceSection getInstrumentedSourceSection();

    Node getInstrumentedNode();

    boolean isLanguageContextInitialized();

    ThreadDeath createUnwind(Object info, EventBinding<?> unwindBinding);

    final class SuspendedEventContext implements SuspendedContext {

        private final EventContext eventContext;

        private SuspendedEventContext(EventContext eventContext) {
            this.eventContext = eventContext;
        }

        @Override
        public int getStackDepth() {
            return 0;
        }

        public SourceSection getInstrumentedSourceSection() {
            return eventContext.getInstrumentedSourceSection();
        }

        public Node getInstrumentedNode() {
            return eventContext.getInstrumentedNode();
        }

        public boolean isLanguageContextInitialized() {
            return eventContext.isLanguageContextInitialized();
        }

        public ThreadDeath createUnwind(Object info, EventBinding<?> unwindBinding) {
            return eventContext.createUnwind(info, unwindBinding);
        }

        @Override
        public String toString() {
            return eventContext.toString();
        }

    }

    final class CallerEventContext implements SuspendedContext {

        private final Node node;
        private final ThreadDeath unwind;

        private CallerEventContext(Node node, ThreadDeath unwind) {
            this.node = node;
            this.unwind = unwind;
        }

        @Override
        public int getStackDepth() {
            return 1;
        }

        @Override
        public SourceSection getInstrumentedSourceSection() {
            if (node == null) {
                return null;
            } else {
                return node.getEncapsulatingSourceSection();
            }
        }

        @Override
        public Node getInstrumentedNode() {
            return node;
        }

        @Override
        public boolean isLanguageContextInitialized() {
            return true;
        }

        @Override
        public ThreadDeath createUnwind(Object info, EventBinding<?> unwindBinding) {
            return unwind;
        }

        @Override
        public String toString() {
            return "CallerContext[source=" + getInstrumentedSourceSection() + "]";
        }
    }
}
