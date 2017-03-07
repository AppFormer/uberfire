/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;
import org.uberfire.backend.server.util.TextUtil;

public class TextUtilTest extends TestCase {

    private static final String BASE = "dUMmY";
    String userNameIllegalChars = "ªº\\!|\"@·#$%&¬/()=?'¿¡€^`[*+]¨´{}<>,;:_~ .-";
    String repoNameIllegalChars = "ªº\\!|\"@·#$%&¬/()=?'¿¡€^`[*+]¨´{}<>,;:_~ ";
    // key: illegal char, value: expected char after normalization
    Map<Character, Character> additionalIllegal = new HashMap<Character, Character>();
    private StringBuilder sb = new StringBuilder(BASE);

    @Override
    protected void setUp() throws Exception {
        additionalIllegal.put('ñ',
                              'n');
        additionalIllegal.put('Ñ',
                              'N');
        additionalIllegal.put('ç',
                              'c');
        additionalIllegal.put('Ç',
                              'C');
        additionalIllegal.put('á',
                              'a');
        additionalIllegal.put('à',
                              'a');
        additionalIllegal.put('ä',
                              'a');
        additionalIllegal.put('Á',
                              'A');
        additionalIllegal.put('À',
                              'A');
        additionalIllegal.put('Ä',
                              'A');
    }

    public void testNormalizeUserName() {
        for (int i = 0; i < userNameIllegalChars.length(); i++) {
            int index = 0;
            assertEquals(BASE,
                         TextUtil.normalizeUserName(sb.insert(index,
                                                              userNameIllegalChars.charAt(i)).toString()));
            sb.deleteCharAt(index);

            index = BASE.length() - 3;
            assertEquals(BASE,
                         TextUtil.normalizeUserName(sb.insert(index,
                                                              userNameIllegalChars.charAt(i)).toString()));
            sb.deleteCharAt(index);

            index = BASE.length() - 1;
            assertEquals(BASE,
                         TextUtil.normalizeUserName(sb.insert(index,
                                                              userNameIllegalChars.charAt(i)).toString()));
            sb.deleteCharAt(index);
        }

        StringBuilder expected = new StringBuilder(BASE);
        for (Iterator<Map.Entry<Character, Character>> it = additionalIllegal.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Character, Character> entry = it.next();

            int index = 0;
            assertEquals(expected.insert(index,
                                         entry.getValue()).toString(),
                         TextUtil.normalizeUserName(sb.insert(index,
                                                              entry.getKey()).toString()));
            sb.deleteCharAt(index);
            expected.deleteCharAt(index);

            index = BASE.length() - 3;
            assertEquals(expected.insert(index,
                                         entry.getValue()).toString(),
                         TextUtil.normalizeUserName(sb.insert(index,
                                                              entry.getKey()).toString()));
            sb.deleteCharAt(index);
            expected.deleteCharAt(index);

            index = BASE.length() - 1;
            assertEquals(expected.insert(index,
                                         entry.getValue()).toString(),
                         TextUtil.normalizeUserName(sb.insert(index,
                                                              entry.getKey()).toString()));
            sb.deleteCharAt(index);
            expected.deleteCharAt(index);
        }
    }

    public void testNormalizeRepositoryName() {
        for (int i = 0; i < repoNameIllegalChars.length(); i++) {
            int index = 0;
            assertEquals(BASE,
                         TextUtil.normalizeRepositoryName(sb.insert(index,
                                                                    repoNameIllegalChars.charAt(i)).toString()));
            sb.deleteCharAt(index);

            index = BASE.length() - 3;
            assertEquals(BASE,
                         TextUtil.normalizeRepositoryName(sb.insert(index,
                                                                    repoNameIllegalChars.charAt(i)).toString()));
            sb.deleteCharAt(index);

            index = BASE.length() - 1;
            assertEquals(BASE,
                         TextUtil.normalizeRepositoryName(sb.insert(index,
                                                                    repoNameIllegalChars.charAt(i)).toString()));
            sb.deleteCharAt(index);
        }

        StringBuilder expected = new StringBuilder(BASE);
        for (Iterator<Map.Entry<Character, Character>> it = additionalIllegal.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Character, Character> entry = it.next();

            int index = 0;
            assertEquals(expected.insert(index,
                                         entry.getValue()).toString(),
                         TextUtil.normalizeRepositoryName(sb.insert(index,
                                                                    entry.getKey()).toString()));
            sb.deleteCharAt(index);
            expected.deleteCharAt(index);

            index = BASE.length() - 3;
            assertEquals(expected.insert(index,
                                         entry.getValue()).toString(),
                         TextUtil.normalizeRepositoryName(sb.insert(index,
                                                                    entry.getKey()).toString()));
            sb.deleteCharAt(index);
            expected.deleteCharAt(index);

            index = BASE.length() - 1;
            assertEquals(expected.insert(index,
                                         entry.getValue()).toString(),
                         TextUtil.normalizeRepositoryName(sb.insert(index,
                                                                    entry.getKey()).toString()));
            sb.deleteCharAt(index);
            expected.deleteCharAt(index);
        }

        String strInput = "";
        String strExpected = "";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "_{`";
        strExpected = "";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = ".";
        strExpected = "";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "-";
        strExpected = "";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "...";
        strExpected = "";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "---";
        strExpected = "";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "..d.";
        strExpected = "d";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = ".d..";
        strExpected = "d";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "--d-";
        strExpected = "d";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "-d--";
        strExpected = "d";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "..d..";
        strExpected = "d";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = ".-d..";
        strExpected = "d";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = ".d-..";
        strExpected = "d";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "--d--.";
        strExpected = "d";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));

        strInput = "d---f";
        strExpected = "df";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "d...f";
        strExpected = "df";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "d----f";
        strExpected = "df";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "d-f";
        strExpected = "d-f";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "d.f";
        strExpected = "d.f";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "d.-f";
        strExpected = "df";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "d.-.f";
        strExpected = "df";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "d-.-f";
        strExpected = "df";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "d-.f";
        strExpected = "df";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "d..-.-B'.--..+-_--ç-..f";
        strExpected = "dBcf";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));

        strInput = "m\"y-t@ës#t'.re{p'o-n:a;m,e";
        strExpected = "my-test.repo-name";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = "-m\"y-t@ës#t'.re{p'o-n:a;m,e-";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
        strInput = ".m\"y-t@ës#t'.re{p'o-n:a;m,e.";
        assertEquals(strExpected,
                     TextUtil.normalizeRepositoryName(strInput));
    }
}
