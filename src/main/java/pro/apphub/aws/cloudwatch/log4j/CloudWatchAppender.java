/*
 * Copyright (C) 2017 Dmitry Kotlyarov.
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pro.apphub.aws.cloudwatch.log4j;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.OnlyOnceErrorHandler;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public final class CloudWatchAppender implements Appender, OptionHandler {
    private String name = null;
    private String group = null;
    private String streamPrefix = null;
    private String streamPostfix = null;
    private String access = null;
    private String secret = null;
    private String capacity = null;
    private String length = null;
    private String span = null;
    private Filter headFilter = null;
    private Filter tailFilter = null;
    private Layout layout = null;
    private ErrorHandler errorHandler = new OnlyOnceErrorHandler();
    private final AtomicReference<CloudWatchAppenderImpl> impl = new AtomicReference<>(null);

    public CloudWatchAppender() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStreamPrefix() {
        return streamPrefix;
    }

    public void setStreamPrefix(String streamPrefix) {
        this.streamPrefix = streamPrefix;
    }

    public String getStreamPostfix() {
        return streamPostfix;
    }

    public void setStreamPostfix(String streamPostfix) {
        this.streamPostfix = streamPostfix;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getSpan() {
        return span;
    }

    public void setSpan(String span) {
        this.span = span;
    }

    @Override
    public Filter getFilter() {
        return headFilter;
    }

    @Override
    public void addFilter(Filter newFilter) {
        if (headFilter == null) {
            headFilter = newFilter;
            tailFilter = newFilter;
        } else {
            tailFilter.setNext(newFilter);
            tailFilter = newFilter;
        }
    }

    @Override
    public void clearFilters() {
        headFilter = null;
        tailFilter = null;
    }

    @Override
    public Layout getLayout() {
        return layout;
    }

    @Override
    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public void close() {
    }

    @Override
    public void doAppend(LoggingEvent event) {
        CloudWatchAppenderImpl impl = this.impl.get();
        if (impl != null) {
            impl.append(event);
        }
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    @Override
    public void activateOptions() {
        CloudWatchAppenderImpl newImpl = CloudWatchAppenderImpl.createAppender(name,
                                                                               group,
                                                                               streamPrefix,
                                                                               streamPostfix,
                                                                               access,
                                                                               secret,
                                                                               capacity,
                                                                               length,
                                                                               span,
                                                                               headFilter,
                                                                               layout);
        newImpl.start();
        CloudWatchAppenderImpl oldImpl = impl.getAndSet(newImpl);
        if (oldImpl == null) {
            Thread t = new Thread(String.format("aws-cloudwatch-log4j-hook-%s", newImpl.getName())) {
                @Override
                public void run() {
                    CloudWatchAppenderImpl impl = CloudWatchAppender.this.impl.get();
                    if (impl != null) {
                        impl.stop();
                    }
                }
            };
            t.setDaemon(false);
            Runtime.getRuntime().addShutdownHook(t);
        } else {
            oldImpl.stop();
        }
    }
}
