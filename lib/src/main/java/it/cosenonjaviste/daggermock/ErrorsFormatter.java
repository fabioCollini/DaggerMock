/*
 *  Copyright 2016 Fabio Collini.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.cosenonjaviste.daggermock;

import java.util.Collection;

class ErrorsFormatter {
    private static String message(String message1, Collection<String> errors, String message2) {
        StringBuilder b = new StringBuilder(message1).append(":");
        for (String error : errors) {
            b.append("\n").append(error);
        }
        if (message2 != null) {
            b.append("\n").append(message2);
        }
        return b.toString();
    }

    static void throwExceptionOnErrors(String message1, Collection<String> errors) {
        throwExceptionOnErrors(message1, errors, null);
    }

    static void throwExceptionOnErrors(String message1, Collection<String> errors, String message2) {
        if (!errors.isEmpty()) {
            throw new RuntimeException(message(message1, errors, message2));
        }
    }
}
