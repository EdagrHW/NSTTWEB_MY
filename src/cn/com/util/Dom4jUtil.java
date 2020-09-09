package cn.com.util;

import cn.com.common.Const;
import cn.com.exception.BizException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.*;

/**
 * @author zhaoxin
 * @description Dom4j�������װ
 * @date 2019/8/9 15:53
 */
public class Dom4jUtil {

    /**
     * XML��ʽ�����Ĭ��������
     */
    private final static int INDENT_DEFAULT = 2;

    /**
     * ��XML����Ч���ַ� ����
     */
    private final static String INVALID_REGEX = "[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]";

    /**
     * ����XML�ĵ�<br>
     * ������XMLĬ����utf8���룬�޸ı���Ĺ�������toStr��toFile�������XML��תΪ�ı���ʱ��Ŷ������
     *
     * @return XML�ĵ�
     * @since 4.0.8
     */
    public static Document createXml() {
        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding(Const.UTF8);
        return document;
    }


    /**
     * �����нڵ��ϴ����ӽڵ�
     *
     * @param node    �ڵ�
     * @param tagName ��ǩ��
     * @return �ӽڵ�
     * @since 4.0.9
     */
    public static Element addChild(Node node, String tagName) {
        if (StrUtil.isBlank(tagName)) {
            throw new BizException("add child error:tagName is null");
        }
        if (node instanceof Document) {
            Document doc = (Document) node;
            return doc.addElement(tagName);
        }
        if (node instanceof Element) {
            Element parent = (Element) node;
            return parent.addElement(tagName);
        }
        throw new BizException("add child error: node type error");
    }


    /**
     * �ڵ��������
     *
     * @param node  �ڵ�
     * @param name  ��������
     * @param value ����ֵ
     * @return �ӽڵ�
     */
    public static void addAttribute(Element node, String name, String value, String defaultValue) {
        if (node == null) {
            throw new BizException("add attribute error: node is null");
        }
        if (value != null) {
            node.addAttribute(name, value);
        } else {
            node.addAttribute(name, defaultValue);
        }
    }

    /**
     * �ڵ�����ı�
     *
     * @param node �ڵ�
     * @param text �ڵ��ı�
     * @return �ӽڵ�
     */
    public static void addText(Element node, String text, String defaultText) {
        Element element;
        if (node == null) {
            throw new BizException("add attribute error: node is null");
        }
        if (StrUtil.isNotBlank(text)) {
            node.addText(text);
        } else {
            node.addText(defaultText);
        }
    }

    public static void addCompone(Element node,String originValue,String defaultValue){
          if(StrUtil.isBlank(originValue)){
              node.addComment(defaultValue);
          }else{
              node.addComment(originValue);
          }
    }

    public static void addElement(Element node,String newElementName,String originValue,String defaultValue){
        if(StrUtil.isBlank(originValue)){
            node.addElement(newElementName).addText(defaultValue);
        }else{
            node.addElement(newElementName).addText(originValue);
        }
    }

    /**
     * ��XML�ĵ�ת��ΪString<br>
     * �ַ�����ʹ��XML�ĵ��еı��룬��ȡ������ʹ��UTF-8
     *
     * @param doc XML�ĵ�
     * @return XML�ַ���
     * @since 3.0.9
     */
    public static String toStr(Document doc) {
        final StringWriter writer = StrUtil.getWriter();
        try {
            write(doc, writer, Const.UTF8, true);
        } catch (Exception e) {
            throw new BizException(e, "trans xml document to string error");
        }
        return writer.toString();
    }
    /**
     * ��XML�ĵ�ת��ΪString<br>
     * �ַ�����ʹ��XML�ĵ��еı��룬��ȡ������ʹ��UTF-8
     *
     * ��������ʽ
     *
     * @param doc XML�ĵ�
     * @return XML�ַ���
     * @since 3.0.9
     */
    public static String toStr2(Document doc) {
        return toStr(doc,Const.UTF8,false);
    }

    /**
     * ��XML�ĵ�ת��ΪString<br>
     * �ַ�����ʹ��XML�ĵ��еı��룬��ȡ������ʹ��UTF-8
     *
     * @param doc      XML�ĵ�
     * @param charset  ����
     * @param isPretty �Ƿ��ʽ�����
     * @return XML�ַ���
     * @since 3.0.9
     */
    public static String toStr(Document doc, String charset, boolean isPretty) {
        final StringWriter writer = StrUtil.getWriter();
        try {
            write(doc, writer, charset, isPretty);
        } catch (Exception e) {
            throw new BizException(e, "trans xml document to string error");
        }
        return writer.toString();
    }

    /**
     * ��XML�ĵ�д��
     *
     * @param doc      {@link org.w3c.dom.Node} XML�ĵ��ڵ���ĵ�����
     * @param writer   д����Writer��Writer���������XML�ı���
     * @param charset  ����
     * @param isPretty �Ƿ��������
     * @since 3.0.9
     */
    public static void write(Document doc, Writer writer, String charset, boolean isPretty) {
        XMLWriter xmlWriter;
        OutputFormat format;
        try {
            if (isPretty) {
                // ������ʽ
                format = OutputFormat.createPrettyPrint();
                format.setIndentSize(INDENT_DEFAULT);
                format.setEncoding(charset);
                xmlWriter = new XMLWriter(writer, format);
                xmlWriter.write(doc);
            } else {
                // ������ʽ
                format = OutputFormat.createCompactFormat();
                xmlWriter = new XMLWriter(writer, format);
                xmlWriter.write(doc);
            }
        } catch (Exception ex) {

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    /**
     * ��ȡ����XML�ļ�<br>
     * ������������ԡ�&lt;����ͷ����ʾ����һ��XML���ݣ�ֱ�Ӷ�ȡ��������·������<br>
     * ·������Ϊ���·����Ҳ�����Ǿ���·�������·�������ClassPath
     *
     * @param pathOrContent ���ݻ�·��
     * @return XML�ĵ�����
     */
    public static Document readXML(String pathOrContent) {
        if (StrUtil.startWith(pathOrContent, '<')) {
            return parseXml(pathOrContent);
        }
        return readXML(FileUtil.file(pathOrContent));
    }

    /**
     * ��String���͵�XMLת��ΪXML�ĵ�
     *
     * @param xmlStr XML�ַ���
     * @return XML�ĵ�
     */
    public static Document parseXml(String xmlStr) {
        if (StrUtil.isBlank(xmlStr)) {
            throw new IllegalArgumentException("XML content string is empty !");
        }
        xmlStr = cleanInvalid(xmlStr);
        return readXML(new InputSource(StrUtil.getReader(xmlStr)));
    }

    /**
     * ȥ��XML�ı��е���Ч�ַ�
     *
     * @param xmlContent XML�ı�
     * @return ������Ϊnullʱ����null
     */
    public static String cleanInvalid(String xmlContent) {
        if (xmlContent == null) {
            return null;
        }
        return xmlContent.replaceAll(INVALID_REGEX, "");
    }


    /**
     * ��ȡ����XML�ļ�
     *
     * @param file XML�ļ�
     * @return XML�ĵ�����
     */
    public static Document readXML(File file) {
        if (file == null) {
            throw new BizException("xml file is null");
        }
        if (!file.exists()) {
            throw new BizException("File [{}] not a exist!", file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new BizException("[{}] not a file!", file.getAbsolutePath());
        }

        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
            // ignore
        }
        BufferedInputStream in = null;
        try {
            in = FileUtil.getInputStream(file);
            return readXML(in);
        } finally {
            IoUtil.close(in);
        }
    }

    /**
     * ��ȡ����XML�ļ�<br>
     * ������XML�ж���
     *
     * @param inputStream XML��
     * @return XML�ĵ�����
     * @throws BizException IO�쳣��ת���쳣
     * @since 3.0.9
     */
    public static Document readXML(InputStream inputStream) throws BizException {
        return readXML(new InputSource(inputStream));
    }

    /**
     * ��ȡ����XML�ļ�<br>
     * ������XML�ж���
     *
     * @param source {@link InputSource}
     * @return XML�ĵ�����
     * @since 3.0.9
     */
    public static Document readXML(InputSource source) {
        try {
            SAXReader reader = new SAXReader();
            return reader.read(source);
        } catch (Exception e) {
            throw new BizException(e, "parse xml from stream error");
        }
    }

    /**
     * ���XML�ĵ����ڵ�
     *
     * @param doc {@link Document}
     * @return ���ڵ�
     * @see Document#
     * @since 3.0.8
     **/
    public static Element getRootElement(Document doc) {
        return (null == doc) ? null : doc.getRootElement();
    }

    /**
     * ͨ��XPath��ʽ��ȡXML�ڵ����Ϣ
     * Ĭ�ϻ�ȡ��һ���ڵ�
     * Xpath������£�https://www.ibm.com/developerworks/cn/xml/x-javaxpathapi.html
     *
     * @param expression XPath���ʽ
     * @param source     ��Դ��������Docunent��Node�ڵ��
     * @return ƥ�䷵�����͵�ֵ
     * @since 4.0.9
     */
    public static Node getNodeByXPath(String expression, Object source) {
        if (source instanceof Document) {
            Document doc = (Document) source;
            return (Node) doc.selectSingleNode(expression);
        }
        if (source instanceof Element) {
            Element element = (Element) source;
            return (Node) element.selectSingleNode(expression);
        }
        throw new BizException("source class type is error");
    }

    /**
     * ͨ��XPath��ʽ��ȡXML�ڵ����Ϣ
     * Ĭ�ϻ�ȡ��һ���ڵ�
     * Xpath������£�https://www.ibm.com/developerworks/cn/xml/x-javaxpathapi.html
     *
     * @param expression XPath���ʽ
     * @param source     ��Դ��������Docunent��Node�ڵ��
     * @return ƥ�䷵�����͵�ֵ
     * @since 4.0.9
     */
    public static List<Node> getNodeListByXPath(String expression, Object source) {
        if (source instanceof Document) {
            Document doc = (Document) source;
            return (List<Node>) doc.selectNodes(expression);
        }
        if (source instanceof Element) {
            Element element = (Element) source;
            return (List<Node>) element.selectNodes(expression);
        }
        throw new BizException("source class type is error");
    }

    /**
     * ��XML�ĵ�д�뵽�ļ�<br>
     *
     * @param doc     XML�ĵ�
     * @param path    �ļ�·������·�������ClassPath·���������ڻ��Զ�����
     * @param charset �Զ���XML�ļ��ı��룬���Ϊ{@code null} ��ȡXML�ĵ��еı��룬����Ĭ��UTF-8
     */
    public static void toFile(Document doc, String path, String charset) {
        if (StrUtil.isBlank(charset)) {
            charset = doc.getXMLEncoding();
        }
        if (StrUtil.isBlank(charset)) {
            charset = CharsetUtil.UTF_8;
        }

        BufferedWriter writer = null;
        try {
            writer = FileUtil.getWriter(path, charset, false);
            write(doc, writer, charset, true);
        } finally {
            IoUtil.close(writer);
        }
    }

    /**
     * ��XML�ĵ�д�뵽�ļ�<br>
     * ʹ��Document�еı���
     *
     * @param doc          XML�ĵ�
     * @param absolutePath �ļ�����·���������ڻ��Զ�����
     */
    public static void toFile(Document doc, String absolutePath) {
        toFile(doc, absolutePath, null);
    }


    public static void main(String[] args) {

        Document doc = createXml();
        Element operation = addChild(doc, "operation");
        addAttribute(operation, "name", "init", "");
        operation.addComment("jjjkjkdjkasldjfklas");
        Element protocol = addChild(operation, "protocol");
        protocol.addText("3");
        Element protocol1 = addChild(operation, "protocol");
        protocol1.addText("4");
        operation.addComment("jjjjjjj");
        System.out.println(toStr(doc));
        Document document = readXML(toStr(doc));
        System.out.println(document);
        System.out.println("----------------------");
        System.out.println(toStr(document));
        Document parentXml = createXml();
        Element config = addChild(parentXml, "config");
        config.add(getRootElement(doc));
        System.out.println(toStr(parentXml));

        Node object = doc.selectSingleNode("operation/protocol");

        System.out.println(object.getText());

        /*Node numNode = getNodeByXPath("@name", operation);

        System.out.println(numNode.getText());*/


    }

    /**
     * ��ʽ��xml
     *
     * @param xml
     * @return
     */
    public static String formatXML(String xml) {
        String requestXML = null;
        XMLWriter writer = null;
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(new StringReader(xml));
            if (document != null) {
                StringWriter stringWriter = new StringWriter();
                // ��ʽ����ÿһ��ǰ�Ŀո�
                OutputFormat format = new OutputFormat(" ", true);
                // xml�����������Ƿ���ӿ���
                format.setNewLineAfterDeclaration(false);
                // �Ƿ�����xml����ͷ�� false�����
                format.setSuppressDeclaration(false);
                // ���÷���
                format.setNewlines(true);
                writer = new XMLWriter(stringWriter, format);
                writer.write(document);
                writer.flush();
                requestXML = stringWriter.getBuffer().toString();
            }
            return requestXML;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {

                }
            }
        }
    }


    /**
     * xmlתmap ��������
     *
     * @param root
     * @param needRootKey �Ƿ���Ҫ�ڷ��ص�map��Ӹ��ڵ��
     * @return
     */
    public static Map<String, Object> xml2map(Element root, boolean needRootKey) {
        Map<String, Object> map = (Map<String, Object>) xml2map(root);
        if (root.elements().size() == 0 && root.attributes().size() == 0) {
            return map;
        }
        if (needRootKey) {
            //�ڷ��ص�map��Ӹ��ڵ���������Ҫ��
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }

    /**
     * xmlתmap ��������
     *
     * @param doc
     * @param needRootKey �Ƿ���Ҫ�ڷ��ص�map��Ӹ��ڵ��
     * @return
     * @throws DocumentException
     */
    public static Map<String, Object> xml2map(Document doc, boolean needRootKey) {
        Element root = doc.getRootElement();
        return xml2map(root, needRootKey);
    }

    /**
     * xmlתmap ������
     *
     * @param xmlStr
     * @param needRootKey �Ƿ���Ҫ�ڷ��ص�map��Ӹ��ڵ��
     * @return
     * @throws DocumentException
     */
    public static Map<String, Object> xml2mapWithAttr(String xmlStr, boolean needRootKey) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        Map<String, Object> map = xml2mapWithAttr(root);
        if (root.elements().size() == 0 && root.attributes().size() == 0) {
            //���ڵ�ֻ��һ���ı�����
            return map;
        }
        if (needRootKey) {
            //�ڷ��ص�map��Ӹ��ڵ���������Ҫ��
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }

    /**
     * xmlתmap ������
     *
     * @param element
     * @return
     */
    private static Map<String, Object> xml2mapWithAttr(Element element) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        List<Element> list = element.elements();
        // ��ǰ�ڵ���������Ե�list
        List<Attribute> listAttr0 = element.attributes();
        for (Attribute attr : listAttr0) {
            map.put("@" + attr.getName(), attr.getValue());
        }
        if (list.size() > 0) {
            for (Element iter : list) {
                List mapList = new ArrayList();
                if (iter.elements().size() > 0) {
                    Map<String, Object> m = xml2mapWithAttr(iter);
                    creatMapList(map, iter, mapList, m);
                } else {
                    // ��ǰ�ڵ���������Ե�list
                    List<Attribute> listAttr = iter.attributes();
                    Map<String, Object> attrMap = null;
                    boolean hasAttributes = false;
                    if (listAttr.size() > 0) {
                        hasAttributes = true;
                        attrMap = new LinkedHashMap<String, Object>();
                        for (Attribute attr : listAttr) {
                            attrMap.put("@" + attr.getName(), attr.getValue());
                        }
                    }
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            // mapList.add(iter.getText());
                            if (hasAttributes) {
                                attrMap.put("#text", iter.getText());
                                mapList.add(attrMap);
                            } else {
                                mapList.add(iter.getText());
                            }
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            // mapList.add(iter.getText());
                            if (hasAttributes) {
                                attrMap.put("#text", iter.getText());
                                mapList.add(attrMap);
                            } else {
                                mapList.add(iter.getText());
                            }
                        }
                        map.put(iter.getName(), mapList);
                    } else {
                        // map.put(iter.getName(), iter.getText());
                        if (hasAttributes) {
                            attrMap.put("#text", iter.getText());
                            map.put(iter.getName(), attrMap);
                        } else {
                            map.put(iter.getName(), iter.getText());
                        }
                    }
                }
            }
        } else {
            // ���ڵ��
            if (listAttr0.size() > 0) {
                map.put("#text", element.getText());
            } else {
                map.put(element.getName(), element.getText());
            }
        }
        return map;
    }

    /**
     * xmlתmap ��������
     *
     * @param e
     * @return
     */
    private static Map xml2map(Element e) {
        Map map = new LinkedHashMap();
        List list = e.elements();
        if (list.size() > 0) {
            for (Object o : list) {
                Element iter = (Element) o;
                List mapList = new ArrayList();
                if (iter.elements().size() > 0) {
                    Map m = xml2map(iter);
                    creatMapList(map, iter, mapList, m);
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else {
                        map.put(iter.getName(), iter.getText());
                    }
                }
            }
        } else {
            map.put(e.getName(), e.getText());
        }
        return map;
    }

    private static void creatMapList(Map map, Element iter, List mapList, Map m) {
        if (map.get(iter.getName()) != null) {
            Object obj = map.get(iter.getName());
            if (!(obj instanceof List)) {
                mapList = new ArrayList();
                mapList.add(obj);
                mapList.add(m);
            }
            if (obj instanceof List) {
                mapList = (List) obj;
                mapList.add(m);
            }
            map.put(iter.getName(), mapList);
        } else {
            map.put(iter.getName(), m);
        }
    }
}
