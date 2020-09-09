package cn.com.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    /**
     * Regex of special characters
     */
    public static final String REGEX_EXPR = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~��@#��%����&*��������+|{}������������������������]";

    /**
     * Regex of HTML characters
     */

    public static final String REGEX_SCRIPT = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
    public static final String REGEX_STYLE = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
    public static final String REGEX_HTML = "<[^>]+>";
    public static final String PATTERNSTR = "\\s+";

    /**
     * Regex of simple mobile.
     */
    public static final String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";
    /**
     * Regex of exact mobile.
     * <p>china mobile: 134(0-8), 135, 136, 137, 138, 139, 147, 150, 151, 152, 157, 158, 159, 178, 182, 183, 184, 187, 188, 198</p>
     * <p>china unicom: 130, 131, 132, 145, 155, 156, 166, 171, 175, 176, 185, 186</p>
     * <p>china telecom: 133, 153, 173, 177, 180, 181, 189, 199</p>
     * <p>global star: 1349</p>
     * <p>virtual operator: 170</p>
     */
    public static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(16[6])|(17[0,1,3,5-8])|(18[0-9])|(19[8,9]))\\d{8}$";
    /**
     * Regex of telephone number.
     */
    public static final String REGEX_TEL = "^0\\d{2,3}[- ]?\\d{7,8}";
    /**
     * Regex of id card number which length is 15.
     */
    public static final String REGEX_ID_CARD15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
    /**
     * Regex of id card number which length is 18.
     */
    public static final String REGEX_ID_CARD18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
    /**
     * Regex of email.
     */
    public static final String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    /**
     * Regex of url.
     */
    public static final String REGEX_URL = "[a-zA-z]+://[^\\s]*";
    /**
     * Regex of Chinese character.
     */
    public static final String REGEX_ZH = "^[\\u4e00-\\u9fa5]+$";
    /**
     * Regex of username.
     * <p>scope for "a-z", "A-Z", "0-9", "_", "Chinese character"</p>
     * <p>can't end with "_"</p>
     * <p>length is between 6 to 20</p>
     */
    public static final String REGEX_USERNAME = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$";
    /**
     * Regex of date which pattern is "yyyy-MM-dd".
     */
    public static final String REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
    /**
     * Regex of ip address.
     */
    public static final String REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

    /**
     * Regex of double-byte characters.
     */
    public static final String REGEX_DOUBLE_BYTE_CHAR = "[^\\x00-\\xff]";
    /**
     * Regex of blank line.
     */
    public static final String REGEX_BLANK_LINE = "\\n\\s*\\r";
    /**
     * Regex of QQ number.
     */
    public static final String REGEX_QQ_NUM = "[1-9][0-9]{4,}";
    /**
     * Regex of postal code in China.
     */
    public static final String REGEX_CHINA_POSTAL_CODE = "[1-9]\\d{5}(?!\\d)";
    /**
     * Regex of positive integer.
     */
    public static final String REGEX_POSITIVE_INTEGER = "^[1-9]\\d*$";
    /**
     * Regex of negative integer.
     */
    public static final String REGEX_NEGATIVE_INTEGER = "^-[1-9]\\d*$";
    /**
     * Regex of integer.
     */
    public static final String REGEX_INTEGER = "^-?[1-9]\\d*$";
    /**
     * Regex of non-negative integer.
     */
    public static final String REGEX_NOT_NEGATIVE_INTEGER = "^[1-9]\\d*|0$";
    /**
     * Regex of non-positive integer.
     */
    public static final String REGEX_NOT_POSITIVE_INTEGER = "^-[1-9]\\d*|0$";
    /**
     * Regex of positive float.
     */
    public static final String REGEX_POSITIVE_FLOAT = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
    /**
     * Regex of negative float.
     */
    public static final String REGEX_NEGATIVE_FLOAT = "^-[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$";

    private RegexUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return whether input matches regex of simple mobile.
     *
     * @param input The input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isMobileSimple(final CharSequence input) {
        return isMatch(REGEX_MOBILE_SIMPLE, input);
    }

    /**
     * Return whether input matches regex of exact mobile.
     *
     * @param input The input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isMobileExact(final CharSequence input) {
        return isMatch(REGEX_MOBILE_EXACT, input);
    }

    /**
     * Return whether input matches regex of telephone number.
     *
     * @param input The input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isTel(final CharSequence input) {
        return isMatch(REGEX_TEL, input);
    }

    /**
     * Return whether input matches regex of id card number which length is 15.
     *
     * @param input The input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isIDCard15(final CharSequence input) {
        return isMatch(REGEX_ID_CARD15, input);
    }

    /**
     * Return whether input matches regex of id card number which length is 18.
     *
     * @param input The input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isIDCard18(final CharSequence input) {
        return isMatch(REGEX_ID_CARD18, input);
    }

    /**
     * Return whether input matches regex of email.
     *
     * @param input The input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isEmail(final CharSequence input) {
        return isMatch(REGEX_EMAIL, input);
    }

    /**
     * Return whether input matches regex of url.
     *
     * @param input The input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isURL(final CharSequence input) {
        return isMatch(REGEX_URL, input);
    }

    /**
     * Return whether input matches regex of Chinese character.
     *
     * @param input The input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isZh(final CharSequence input) {
        return isMatch(REGEX_ZH, input);
    }

    /**
     * Return whether input matches regex of username.
     * <p>scope for "a-z", "A-Z", "0-9", "_", "Chinese character"</p>
     * <p>can't end with "_"</p>
     * <p>length is between 6 to 20</p>.
     *
     * @param input The input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isUsername(final CharSequence input) {
        return isMatch(REGEX_USERNAME, input);
    }

    /**
     * Return whether input matches regex of date which pattern is "yyyy-MM-dd".
     *
     * @param input The input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isDate(final CharSequence input) {
        return isMatch(REGEX_DATE, input);
    }

    /**
     * Return whether input matches regex of ip address.
     *
     * @param input The input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isIP(final CharSequence input) {
        return isMatch(REGEX_IP, input);
    }

    /**
     * Return whether input matches the regex.
     *
     * @param regex The regex.
     * @param input The input.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isMatch(final String regex, final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

    /**
     * Return the list of input matches the regex.
     *
     * @param regex The regex.
     * @param input The input.
     * @return the list of input matches the regex
     */
    public static List<String> getMatches(final String regex, final CharSequence input) {
        if (input == null) {
            return Collections.emptyList();
        }
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    /**
     * Splits input around matches of the regex.
     *
     * @param input The input.
     * @param regex The regex.
     * @return the array of strings computed by splitting input around matches of regex
     */
    public static String[] getSplits(final String input, final String regex) {
        if (input == null) {
            return new String[0];
        }
        return input.split(regex);
    }

    /**
     * Replace the first subsequence of the input sequence that matches the
     * regex with the given replacement string.
     *
     * @param input       The input.
     * @param regex       The regex.
     * @param replacement The replacement string.
     * @return the string constructed by replacing the first matching
     * subsequence by the replacement string, substituting captured
     * subsequences as needed
     */
    public static String getReplaceFirst(final String input,
                                         final String regex,
                                         final String replacement) {
        if (input == null) {
            return "";
        }
        return Pattern.compile(regex).matcher(input).replaceFirst(replacement);
    }


    /**
     * Replace every subsequence of the input sequence that matches the
     * pattern with the given replacement string.
     *
     * @param input       The input.
     * @param regex       The regex.
     * @param replacement The replacement string.
     * @return the string constructed by replacing each matching subsequence
     * by the replacement string, substituting captured subsequences
     * as needed
     */
    public static String getReplaceAll(final String input,
                                       final String regex,
                                       final String replacement) {
        if (input == null) {
            return "";
        }
        return Pattern.compile(regex).matcher(input).replaceAll(replacement);
    }

    /**
     * �Ƿ������Ӣ�������ַ�����Ӣ��"-_"�ַ���
     *
     * @param text
     * @return
     */
    public static boolean isContainsSpecialChar(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }
        String[] chars = {"[", "`", "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "+", "=", "|", "{", "}", "'",
                ":", ";", "'", ",", "[", "]", ".", "<", ">", "/", "?", "~", "��", "@", "#", "��", "%", "��", "&", "*", "��", "��",
                "��", "+", "|", "{", "}", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "]"};
        for (String ch : chars) {
            if (text.contains(ch)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ������Ӣ�������ַ�����Ӣ��"-_"�ַ���
     *
     * @param text
     * @return
     */
    public static String stringFilter(String text) {
        Pattern p = Pattern.compile(REGEX_EXPR);
        Matcher m = p.matcher(text);
        return m.replaceAll("").trim();
    }

    /**
     * ����html����
     *
     * @param inputString ��html��ǩ���ַ���
     * @return �����ı��ַ���
     */
    public static String htmlFilter(String inputString) {
        // ��html��ǩ���ַ���
        String htmlStr = inputString;
        String textStr = "";
        Pattern pScript;
        Matcher mScript;
        Pattern pStyle;
        Matcher mStyle;
        Pattern pHtml;
        Matcher mHtml;
        Pattern pBa;
        Matcher mBa;
        try {
            pScript = Pattern.compile(REGEX_SCRIPT, Pattern.CASE_INSENSITIVE);
            mScript = pScript.matcher(htmlStr);
            // ����script��ǩ
            htmlStr = mScript.replaceAll("");

            // ����style��ǩ
            pStyle = Pattern.compile(REGEX_STYLE, Pattern.CASE_INSENSITIVE);
            mStyle = pStyle.matcher(htmlStr);
            htmlStr = mStyle.replaceAll("");

            // ����html��ǩ
            pHtml = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE);
            mHtml = pHtml.matcher(htmlStr);
            htmlStr = mHtml.replaceAll("");

            // ���˿ո�
            pBa = Pattern.compile(PATTERNSTR, Pattern.CASE_INSENSITIVE);
            mBa = pBa.matcher(htmlStr);
            htmlStr = mBa.replaceAll("");

            textStr = htmlStr;

        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }
        return textStr;
    }

    // �� �� ���õ��������ʽ��
    // ƥ���ض����֣�
    // ^[1-9]d*$�� �� //ƥ��������
    // ^-[1-9]d*$ �� //ƥ�为����
    // ^-?[1-9]d*$���� //ƥ������
    // ^[1-9]d*|0$�� //ƥ��Ǹ������������� + 0��
    // ^-[1-9]d*|0$���� //ƥ����������������� + 0��
    // ^[1-9]d*.d*|0.d*[1-9]d*$���� //ƥ����������
    // ^-([1-9]d*.d*|0.d*[1-9]d*)$�� //ƥ�为������
    // ^-?([1-9]d*.d*|0.d*[1-9]d*|0?.0+|0)$�� //ƥ�両����
    // ^[1-9]d*.d*|0.d*[1-9]d*|0?.0+|0$���� //ƥ��Ǹ����������������� + 0��
    // ^(-([1-9]d*.d*|0.d*[1-9]d*))|0?.0+|0$����//ƥ��������������������� + 0��
    // ��ע��������������ʱ���ã�����Ӧ��ʱע������
    //
    // ƥ���ض��ַ�����
    // ^[A-Za-z]+$����//ƥ����26��Ӣ����ĸ��ɵ��ַ���
    // ^[A-Z]+$����//ƥ����26��Ӣ����ĸ�Ĵ�д��ɵ��ַ���
    // ^[a-z]+$����//ƥ����26��Ӣ����ĸ��Сд��ɵ��ַ���
    // ^[A-Za-z0-9]+$����//ƥ�������ֺ�26��Ӣ����ĸ��ɵ��ַ���
    // ^w+$����//ƥ�������֡�26��Ӣ����ĸ�����»�����ɵ��ַ���
    //
    // ��ʹ��RegularExpressionValidator��֤�ؼ�ʱ����֤���ܼ�����֤����ʽ��������:
    //
    // ֻ���������֣���^[0-9]*$��
    // ֻ������nλ�����֣���^d{n}$��
    // ֻ����������nλ���֣���^d{n,}$��
    // ֻ������m-nλ�����֣���^d{m,n}$��
    // ֻ��������ͷ��㿪ͷ�����֣���^(0|[1-9][0-9]*)$��
    // ֻ����������λС������ʵ������^[0-9]+(.[0-9]{2})?$��
    // ֻ��������1-3λС������ʵ������^[0-9]+(.[0-9]{1,3})?$��
    // ֻ��������������������^+?[1-9][0-9]*$��
    // ֻ���������ĸ���������^-[1-9][0-9]*$��
    // ֻ�����볤��Ϊ3���ַ�����^.{3}$��
    // ֻ��������26��Ӣ����ĸ��ɵ��ַ�������^[A-Za-z]+$��
    // ֻ��������26����дӢ����ĸ��ɵ��ַ�������^[A-Z]+$��
    // ֻ��������26��СдӢ����ĸ��ɵ��ַ�������^[a-z]+$��
    // ֻ�����������ֺ�26��Ӣ����ĸ��ɵ��ַ�������^[A-Za-z0-9]+$��
    // ֻ�����������֡�26��Ӣ����ĸ�����»�����ɵ��ַ�������^w+$��
    // ��֤�û�����:��^[a-zA-Z]\\w{5,17}$����ȷ��ʽΪ������ĸ��ͷ��������6-18֮�䣬
    //
    // ֻ�ܰ����ַ������ֺ��»��ߡ�
    // ��֤�Ƿ���^%&��,;=?$�����ַ�����[^%&��,;=?$x22]+��
    // ֻ�����뺺�֣���^[u4e00-u9fa5],{0,}$��
    // ��֤Email��ַ����^w+[-+.]w+)*@w+([-.]w+)*.w+([-.]w+)*$��
    // ��֤InternetURL����^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$��
    // ��֤�绰���룺��^((d{3,4})|d{3,4}-)?d{7,8}$��
    //
    // ��ȷ��ʽΪ����XXXX-XXXXXXX������XXXX-XXXXXXXX������XXX-XXXXXXX����
    //
    // ��XXX-XXXXXXXX������XXXXXXX������XXXXXXXX����
    // ��֤����֤�ţ�15λ��18λ���֣�����^d{15}|d{}18$��
    // ��֤һ���12���£���^(0?[1-9]|1[0-2])$����ȷ��ʽΪ����01��-��09���͡�1����12��
    // ��֤һ���µ�31�죺��^((0?[1-9])|((1|2)[0-9])|30|31)$�� ��ȷ��ʽΪ����01����09���͡�1����31����
    //
    // ƥ�������ַ����������ʽ�� [u4e00-u9fa5]
    // ƥ��˫�ֽ��ַ�(������������)��[^x00-xff]
    // ƥ����е��������ʽ��n[s| ]*r
    // ƥ��HTML��ǵ��������ʽ��/< (.*)>.*|< (.*) />/
    // ƥ����β�ո���������ʽ��(^s*)|(s*$)
    // ƥ��Email��ַ���������ʽ��w+([-+.]w+)*@w+([-.]w+)*.w+([-.]w+)*
    // ƥ����ַURL���������ʽ��^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$


}