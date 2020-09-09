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
 * @description Dom4j工具类封装
 * @date 2019/8/9 15:53
 */
public class Dom4jUtil {

    /**
     * XML格式化输出默认缩进量
     */
    private final static int INDENT_DEFAULT = 2;

    /**
     * 在XML中无效的字符 正则
     */
    private final static String INVALID_REGEX = "[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]";

    /**
     * 创建XML文档<br>
     * 创建的XML默认是utf8编码，修改编码的过程是在toStr和toFile方法里，既XML在转为文本的时候才定义编码
     *
     * @return XML文档
     * @since 4.0.8
     */
    public static Document createXml() {
        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding(Const.UTF8);
        return document;
    }


    /**
     * 在已有节点上创建子节点
     *
     * @param node    节点
     * @param tagName 标签名
     * @return 子节点
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
     * 节点添加属性
     *
     * @param node  节点
     * @param name  属性名称
     * @param value 属性值
     * @return 子节点
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
     * 节点添加文本
     *
     * @param node 节点
     * @param text 节点文本
     * @return 子节点
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
     * 将XML文档转换为String<br>
     * 字符编码使用XML文档中的编码，获取不到则使用UTF-8
     *
     * @param doc XML文档
     * @return XML字符串
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
     * 将XML文档转换为String<br>
     * 字符编码使用XML文档中的编码，获取不到则使用UTF-8
     *
     * 不美化格式
     *
     * @param doc XML文档
     * @return XML字符串
     * @since 3.0.9
     */
    public static String toStr2(Document doc) {
        return toStr(doc,Const.UTF8,false);
    }

    /**
     * 将XML文档转换为String<br>
     * 字符编码使用XML文档中的编码，获取不到则使用UTF-8
     *
     * @param doc      XML文档
     * @param charset  编码
     * @param isPretty 是否格式化输出
     * @return XML字符串
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
     * 将XML文档写出
     *
     * @param doc      {@link org.w3c.dom.Node} XML文档节点或文档本身
     * @param writer   写出的Writer，Writer决定了输出XML的编码
     * @param charset  编码
     * @param isPretty 是否美化输出
     * @since 3.0.9
     */
    public static void write(Document doc, Writer writer, String charset, boolean isPretty) {
        XMLWriter xmlWriter;
        OutputFormat format;
        try {
            if (isPretty) {
                // 美化格式
                format = OutputFormat.createPrettyPrint();
                format.setIndentSize(INDENT_DEFAULT);
                format.setEncoding(charset);
                xmlWriter = new XMLWriter(writer, format);
                xmlWriter.write(doc);
            } else {
                // 缩减格式
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
     * 读取解析XML文件<br>
     * 如果给定内容以“&lt;”开头，表示这是一个XML内容，直接读取，否则按照路径处理<br>
     * 路径可以为相对路径，也可以是绝对路径，相对路径相对于ClassPath
     *
     * @param pathOrContent 内容或路径
     * @return XML文档对象
     */
    public static Document readXML(String pathOrContent) {
        if (StrUtil.startWith(pathOrContent, '<')) {
            return parseXml(pathOrContent);
        }
        return readXML(FileUtil.file(pathOrContent));
    }

    /**
     * 将String类型的XML转换为XML文档
     *
     * @param xmlStr XML字符串
     * @return XML文档
     */
    public static Document parseXml(String xmlStr) {
        if (StrUtil.isBlank(xmlStr)) {
            throw new IllegalArgumentException("XML content string is empty !");
        }
        xmlStr = cleanInvalid(xmlStr);
        return readXML(new InputSource(StrUtil.getReader(xmlStr)));
    }

    /**
     * 去除XML文本中的无效字符
     *
     * @param xmlContent XML文本
     * @return 当传入为null时返回null
     */
    public static String cleanInvalid(String xmlContent) {
        if (xmlContent == null) {
            return null;
        }
        return xmlContent.replaceAll(INVALID_REGEX, "");
    }


    /**
     * 读取解析XML文件
     *
     * @param file XML文件
     * @return XML文档对象
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
     * 读取解析XML文件<br>
     * 编码在XML中定义
     *
     * @param inputStream XML流
     * @return XML文档对象
     * @throws BizException IO异常或转换异常
     * @since 3.0.9
     */
    public static Document readXML(InputStream inputStream) throws BizException {
        return readXML(new InputSource(inputStream));
    }

    /**
     * 读取解析XML文件<br>
     * 编码在XML中定义
     *
     * @param source {@link InputSource}
     * @return XML文档对象
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
     * 获得XML文档根节点
     *
     * @param doc {@link Document}
     * @return 根节点
     * @see Document#
     * @since 3.0.8
     **/
    public static Element getRootElement(Document doc) {
        return (null == doc) ? null : doc.getRootElement();
    }

    /**
     * 通过XPath方式读取XML节点等信息
     * 默认获取第一个节点
     * Xpath相关文章：https://www.ibm.com/developerworks/cn/xml/x-javaxpathapi.html
     *
     * @param expression XPath表达式
     * @param source     资源，可以是Docunent、Node节点等
     * @return 匹配返回类型的值
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
     * 通过XPath方式读取XML节点等信息
     * 默认获取第一个节点
     * Xpath相关文章：https://www.ibm.com/developerworks/cn/xml/x-javaxpathapi.html
     *
     * @param expression XPath表达式
     * @param source     资源，可以是Docunent、Node节点等
     * @return 匹配返回类型的值
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
     * 将XML文档写入到文件<br>
     *
     * @param doc     XML文档
     * @param path    文件路径绝对路径或相对ClassPath路径，不存在会自动创建
     * @param charset 自定义XML文件的编码，如果为{@code null} 读取XML文档中的编码，否则默认UTF-8
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
     * 将XML文档写入到文件<br>
     * 使用Document中的编码
     *
     * @param doc          XML文档
     * @param absolutePath 文件绝对路径，不存在会自动创建
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
     * 格式化xml
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
                // 格式化，每一级前的空格
                OutputFormat format = new OutputFormat(" ", true);
                // xml声明与内容是否添加空行
                format.setNewLineAfterDeclaration(false);
                // 是否设置xml声明头部 false：添加
                format.setSuppressDeclaration(false);
                // 设置分行
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
     * xml转map 不带属性
     *
     * @param root
     * @param needRootKey 是否需要在返回的map里加根节点键
     * @return
     */
    public static Map<String, Object> xml2map(Element root, boolean needRootKey) {
        Map<String, Object> map = (Map<String, Object>) xml2map(root);
        if (root.elements().size() == 0 && root.attributes().size() == 0) {
            return map;
        }
        if (needRootKey) {
            //在返回的map里加根节点键（如果需要）
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }

    /**
     * xml转map 不带属性
     *
     * @param doc
     * @param needRootKey 是否需要在返回的map里加根节点键
     * @return
     * @throws DocumentException
     */
    public static Map<String, Object> xml2map(Document doc, boolean needRootKey) {
        Element root = doc.getRootElement();
        return xml2map(root, needRootKey);
    }

    /**
     * xml转map 带属性
     *
     * @param xmlStr
     * @param needRootKey 是否需要在返回的map里加根节点键
     * @return
     * @throws DocumentException
     */
    public static Map<String, Object> xml2mapWithAttr(String xmlStr, boolean needRootKey) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        Map<String, Object> map = xml2mapWithAttr(root);
        if (root.elements().size() == 0 && root.attributes().size() == 0) {
            //根节点只有一个文本内容
            return map;
        }
        if (needRootKey) {
            //在返回的map里加根节点键（如果需要）
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }

    /**
     * xml转map 带属性
     *
     * @param element
     * @return
     */
    private static Map<String, Object> xml2mapWithAttr(Element element) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        List<Element> list = element.elements();
        // 当前节点的所有属性的list
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
                    // 当前节点的所有属性的list
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
            // 根节点的
            if (listAttr0.size() > 0) {
                map.put("#text", element.getText());
            } else {
                map.put(element.getName(), element.getText());
            }
        }
        return map;
    }

    /**
     * xml转map 不带属性
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
