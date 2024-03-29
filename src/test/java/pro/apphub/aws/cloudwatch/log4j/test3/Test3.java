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

package pro.apphub.aws.cloudwatch.log4j.test3;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author Dmitry Kotlyarov
 * @since 1.0
 */
public class Test3 {
    private static final Logger log = Logger.getLogger(Test3.class);

    public Test3() {
    }

    @Test
    public void test() throws Exception {
        for (int i = 0; i < 1048576; ++i) {
            log.info(String.format("Hello, World %d", i));
        }
    }
}
