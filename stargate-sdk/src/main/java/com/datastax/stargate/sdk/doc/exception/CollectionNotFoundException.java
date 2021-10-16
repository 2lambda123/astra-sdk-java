/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.stargate.sdk.doc.exception;

/**
 * Specialized Error.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class CollectionNotFoundException extends RuntimeException {
    
    /** Serial. */
    private static final long serialVersionUID = -4491748257797687008L;

    /**
     * Constructor with the column name.
     * 
     * @param colName
     *      column name
     */
    public CollectionNotFoundException(String colName) {
        super("Cannot find Collection " + colName);
    }
    
    /**
     * Full constructor.
     *
     * @param colName
     *      column name
     * @param parent
     *      parent exception
     */
    public CollectionNotFoundException(String colName, Throwable parent) {
        super("Cannot find Collection " + colName, parent);
    }

}
