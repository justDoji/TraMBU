/**
 * MIT License
 *
 * Copyright (c) 2019 Stijn Dejongh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package be.doji.productivity.trambu.infrastructure.parser;

import be.doji.productivity.trambu.infrastructure.parser.Property.Indicator;
import be.doji.productivity.trambu.infrastructure.parser.Property.Regex;
import be.doji.productivity.trambu.infrastructure.transfer.ActivityData;
import org.apache.commons.lang3.StringUtils;

public final class ActivityParser {

  private static final String DEFAULT_TITLE = "Unnamed activity";

  /* Utility classes should not have a public or default constructor */
  private ActivityParser() {
  }

  public static ActivityData parse(String line) {
    if (StringUtils.isBlank(line)) {
      throw new IllegalArgumentException(
          "Failure during parsing: empty String or null value not allowed");
    }
    ActivityData activity = new ActivityData();

    activity.setCompleted(ParserUtils.matches(Regex.COMPLETED, line));
    activity
        .setTitle(
            stripIndicators(
                ParserUtils.findFirstMatch(Regex.TITLE, line).orElse(DEFAULT_TITLE)));
    activity.setDeadline(findAndStripIndicators(Indicator.DEADLINE, Regex.DEADLINE, line));

    return activity;
  }

  private static String stripIndicators(String toStrip) {
    String strippedMatch = ParserUtils
        .replaceFirst(regexEscape(Indicator.TITLE_START), toStrip, "");
    return ParserUtils.replaceLast(regexEscape(Indicator.TITLE_END), strippedMatch, "");
  }

  private static String findAndStripIndicators(String escapedIndicator, String regex,
      String line) {
    return ParserUtils.findFirstMatch(regex, line)
        .map(s -> stripIndicators(s, escapedIndicator).trim()).orElse(null);
  }

  private static String stripIndicators(String toStrip, String escapedIndicator) {
    return ParserUtils
        .replaceFirst(escapedIndicator, toStrip, "");
  }

  private static String regexEscape(String toEscape) {
    return "\\" + toEscape;
  }
}
