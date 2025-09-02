package com.pax.market.api.sdk.java.base.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.dto.ParamsVariableObject;
import com.pax.market.api.sdk.java.base.exception.ParseXMLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by zhangchenyang on 2018/2/26.
 */
public class ReplaceUtils {


    private static final Logger logger = LoggerFactory.getLogger(ReplaceUtils.class.getSimpleName());

    public static boolean replaceParams(String filePath, String paramVariables) {
        List<ParamsVariableObject> paramList = exchangeValues(paramVariables);
        if (paramList == null || paramList.isEmpty()) {
            return true;
        }
        File dic = new File(filePath);
        if (dic == null || !dic.isDirectory()) {
            logger.error("Cannot find file folder " + filePath + ">>>> replace paramVariables failed");
            return false;
        }

        if (dic.listFiles() == null) {
            logger.error("There is no file under downloadFolder");
            return false;
        }

        for (File file : dic.listFiles()) {
            String s = readFile(file);
            if (s == null) {
                logger.warn(file.getName() + " is empty, skipped");
                continue;
            }

            if (s.startsWith(Constants.XML_FILE_PREFIX)) {
                if (doXmlReplacementFailed(paramList, file))  { //If replace failed, stop the replacement
                    return false;
                }
            } else if (s.startsWith(Constants.JSON_FILE_PREFIX)) {
                if (doJsonReplacementFailed(paramList, file)) { //If replace failed, stop the replacement
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean doJsonReplacementFailed(List<ParamsVariableObject> paramList, File file) {
        try {
            String fullFile = FileUtils.readFileToString(file);
            String replaceResult = fullFile;


            if (isJsonValidate(fullFile)) {

                Gson gson = new GsonBuilder().create();
                replaceResult = simpleReplaceSafe(gson, fullFile, paramList);


                replaceResult = processJsonReplacement(gson, replaceResult, paramList);

                //rewrite file
                if (!replaceResult.equals(fullFile)) {
                    logger.debug(file.getName() + " replaced");
                    FileUtils.writeStringToFile(file, replaceResult);
                }
            }
        } catch (IOException e) {
            logger.error(" replaceParams failed ", e);
            return true;
        }
        return false;
    }

    private static boolean doXmlReplacementFailed(List<ParamsVariableObject> paramList, File file) {
        try {
            String fullFile = FileUtils.readFileToString(file);
            String replaceResult = fullFile;
            for (ParamsVariableObject paramsVariableObject : paramList) {
                String key = escapeExprSpecialWord(paramsVariableObject.getKey());
                String value = escapeXml(paramsVariableObject.getValue());
                if (paramsVariableObject.getKey().matches("#\\{([A-Za-z0-9-_.]+)\\}")) {
                    replaceResult = replaceResult.replaceAll(String.format("(?i)%s", key), value);
                } else {
                    replaceResult = replaceResult.replaceAll(
                            String.format("(?i)<%s>.*</%s>", key, key),
                            String.format("<%s>%s</%s>", key, value, key));
                }
            }
            //rewrite file
            if (!replaceResult.equals(fullFile)) {
                logger.debug(file.getName() + " replaced");
                FileUtils.writeStringToFile(file, replaceResult);
            }
        } catch (IOException e) {
            logger.error(" replaceParams failed ", e);
            return true;
        }
        return false;
    }

    private static String processJsonReplacement(Gson gson, String oriJsonStr, List<ParamsVariableObject> paramList) {
        JsonObject jsonObject;
        try {
            jsonObject = new JsonParser().parse(oriJsonStr).getAsJsonObject();
        } catch (Exception e) {
            logger.warn("Invalid json parameter string: %s", oriJsonStr);
            return oriJsonStr;
        }
        if (jsonObject == null || jsonObject.isJsonNull()) {
            return oriJsonStr;
        }
        Iterator<String> keyIter = jsonObject.keySet().iterator();
        String key;
        JsonElement jsonElement;
        List<ParamsVariableObject> toBeUpdatedParamList = new ArrayList<>();
        while (keyIter.hasNext()) {
            key = keyIter.next();
            jsonElement = jsonObject.get(key);

            if (jsonElement.isJsonPrimitive()) {
                for (ParamsVariableObject paramsVariableDto : paramList) {
                    if (key.equals(paramsVariableDto.getKey())) {
                        ParamsVariableObject paramsVariableObject = new ParamsVariableObject();
                        paramsVariableObject.setKey(key);
                        paramsVariableObject.setValue(paramsVariableDto.getValue());
                        toBeUpdatedParamList.add(paramsVariableObject);
                    }
                }
            }
        }

        if (!toBeUpdatedParamList.isEmpty()) {
            for (ParamsVariableObject paramsVariableInfo : toBeUpdatedParamList) {
                jsonObject.remove(paramsVariableInfo.getKey());
                jsonObject.addProperty(paramsVariableInfo.getKey(), paramsVariableInfo.getValue());
            }
        }
        return jsonObject.toString();
    }

    public static boolean isHashMapJson(String json) {
        if (json == null || json.length() < 1) { //
            return true;
        }
        Gson gson = new Gson();
        try {
            HashMap object = gson.fromJson(json, HashMap.class);
            return true;
        } catch (Exception e) {
            logger.error("ReplaceUtils error: >> " + "paramVariables json:" + json + " > ", e);
        }
        return false;
    }

    private static List<ParamsVariableObject> exchangeValues(String json) {
        if (json != null) {
            List<ParamsVariableObject> list = new ArrayList<ParamsVariableObject>();
            Gson gson = new Gson();
            HashMap object = gson.fromJson(json, HashMap.class);
            if (object != null && object.size() > 0) {
                Iterator iterator = object.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    ParamsVariableObject dto = new ParamsVariableObject();
                    dto.setKey((String) entry.getKey());
                    dto.setValue((String) entry.getValue());
                    list.add(dto);
                }
            }
            return list;
        }
        return null;
    }

    private static String readFile(File fin) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fin);
            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            if ((line = br.readLine()) != null) {
                return line;
            }
            br.close();
        } catch (FileNotFoundException e) {
            logger.error("read file first line failed, file is null", e);
        } catch (IOException e) {
            logger.error("read file first line failed, IOException", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static boolean isJsonValidate(String jsonStr) {
        JsonElement jsonElement;
        try {
            jsonElement = new JsonParser().parse(jsonStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @param gson the gson
     * @param jsonInString the string
     * @return the result
     */
    public final static boolean isJSONValid(Gson gson, String jsonInString) {
        try {
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch (JsonSyntaxException ex) {
            return false;
        }
    }

    /**
     * ignore regex in string
     *
     * @param keyword input
     * @return result
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (!StringUtils.isEmpty(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    private static String escapeJson(String input) {
        if (!StringUtils.isEmpty(input)) {
            String[] fbsArr = {"\\", "\"", "\\/", "$"};
            for (String key : fbsArr) {
                if (input.contains(key)) {
                    input = input.replace(key, "\\\\\\" + key);
                }
            }
        }
        return input;
    }

    private static String getEscape(char c) {
        switch (c) {
            case '&':
                return "&amp;";
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '"':
                return "&quot;";
            case '\'':
                return "&apos;";
        }

        return String.valueOf(c);
    }

    public static String escapeXml(String src) {
        if (src == null || src.length() == 0) {
            return src;
        }

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            buf.append(getEscape(src.charAt(i)));
        }
        return Matcher.quoteReplacement(buf.toString());
    }

    /**
     * parse the downloaded parameter xml file, convert the xml elements to HashMap String,String
     * this method will not keep the xml fields order. HashMap will have a better performance.
     *
     * @param transMessage the message
     * @return HashMap with key/value of xml elements
     * @throws ParseXMLException the exception
     */
    public HashMap<String, String> parseDownloadParamXml(String transMessage) throws ParseXMLException {
        HashMap<String, String> resultMap = new HashMap<>();
        if (transMessage == null || transMessage.isEmpty()) {
            logger.info("parseDownloadParamXml: file is null, please make sure the file is correct.");
            return resultMap;
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(transMessage.getBytes(StandardCharsets.UTF_8));
            Document document = builder.parse(input);
            Element root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i) instanceof Element) {
                    Element element = (Element) nodeList.item(i);
                    resultMap.put(element.getNodeName(), element.getTextContent());
                }
            }
        } catch (Exception e) {
            throw new ParseXMLException(e);
        }
        return resultMap;
    }

    //A simpler yet functionally explicit version assumes that placeholders are only present in the top-level string values
    public static String simpleReplaceSafe(Gson gson, String jsonString, List<ParamsVariableObject> paramList) {
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class).getAsJsonObject();


        for (ParamsVariableObject paramsVariableObject : paramList) {

            String placeholder =  paramsVariableObject.getKey() ;
            if(!paramsVariableObject.getKey().matches("#\\{([A-Za-z0-9-_.]+)\\}")) {
                logger.warn("skip in #{xxx} key : "+ paramsVariableObject.getKey() + "> value:" + paramsVariableObject.getValue());
                continue;
            }
            String value = paramsVariableObject.getValue();

            logger.warn("key: " + paramsVariableObject.getKey() + "> value:" + paramsVariableObject.getValue());
            // Traverse all the properties of a JSON object
            for (Map.Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
                if (jsonEntry.getValue().isJsonPrimitive() && jsonEntry.getValue().getAsJsonPrimitive().isString()) {
                    String currentValue = jsonEntry.getValue().getAsString();

                    if (currentValue.equals(placeholder)) {
                        // Replace the entire string value directly
                        jsonObject.addProperty(jsonEntry.getKey(), value);
                    } else if (currentValue.contains(placeholder)) {
                        // combo type variables
                        placeholder = escapeExprSpecialWord(placeholder);
                        currentValue = currentValue.replaceAll(String.format("(?i)%s", placeholder), Matcher.quoteReplacement(value));
                        jsonObject.addProperty(jsonEntry.getKey(), currentValue);
                    }
                }
            }
        }

        return gson.toJson(jsonObject);
    }

}
