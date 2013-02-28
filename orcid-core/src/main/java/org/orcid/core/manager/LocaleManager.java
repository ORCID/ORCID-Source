/**
 * =============================================================================
 *
 * The MIT License (MIT)
 * Copyright (c) 2012 ORCID, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * The name of ORCID, Inc., ORCID, its marks and logo, may not be used in
 * advertising or publicity pertaining to distribution of the Software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import java.util.Locale;

public interface LocaleManager {

    /**
     * @return The currently active locale
     */
    Locale getLocale();

    /**
     * @param messageCode
     *            The code of the message in the messages properties file
     * @param messageParams
     *            Values to use in {} placeholders in the message
     * @return The localized message (using the locale for the current thread)
     */
    String resolveMessage(String messageCode, Object... messageParams);

}
