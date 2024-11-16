package com.dz.coop.common.util;


/** 
* CopyRright                          
* Project: asg                                            
* Comments:      json转换工具             
* JDK version used:      <JDK1.6>                              
* Author：       junlee              
* Create Date：  YYYY-MM-DD
* Modified By：  junlee                                    
* Modified Date:  YYYY-MM-DD    
* Email : firehub@163.com                               
* Why & What is modified   
* Version:  1.0                  
*/


import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;


public class JsonUtils {

	private static ObjectMapper mapper = new ObjectMapper();
	
	static{
		//属性映射失败  抛出JsonMappingException
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		//中文转unicode
		mapper.getJsonFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
	}
	
	/**
	 * java 对象转换为 json 字符串
	 * 
	 * @param obj
	 *            对象
	 * @return json
	 */
	public static String toJSON(Object obj) {
		StringWriter writer = new StringWriter();
		try {
			mapper.writeValue(writer, obj);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String result = writer.toString();
		return (null == result) ? "" : result.replaceAll("null", "\"\"");
	}
	
	/**
	 * java 对象转换为 json 字符串
	 * 	对空对象和“null”都不做处理，只能从数据上进行处理
	 * @param obj
	 *            对象
	 * @return json
	 */
	public static String toJSONV1(Object obj) {
		StringWriter writer = new StringWriter();
		try {
			mapper.writeValue(writer, obj);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}


	/**
	 * 反序列化复杂Collection如List<Bean>, 先使用createCollectionType构造类型,然后调用本函数.
	 * @see #createCollectionType(Class, Class...)
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromJson(String jsonString, JavaType javaType) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			//意思是反序列化的作用是确定是否强制让非数组模式的json字符串与java集合类型相匹配
			mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return (T) mapper.readValue(jsonString, javaType);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T> T fromJson(String jsonString, Class<?> collectionClass, Class<?>... elementClasses) {
		if(StringUtils.isBlank(jsonString) || collectionClass == null 
				|| elementClasses.length < 1){
			return null;
		}
		JavaType javaType = createCollectionType(collectionClass, elementClasses);
		return fromJson(jsonString, javaType);
	}
	
	/**
	 * 构造的Collection Type如:
	 * ArrayList<Bean>, 则调用constructCollectionType(ArrayList.class,Bean.class)
	 * HashMap<String,Bean>, 则调用(HashMap.class,String.class, Bean.class)
	 */
	public static JavaType createCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
		return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}
	
	/**
	 * json字符串转换为对象
	 * 
	 * @param <T>
	 * @param json
	 *            json字符串
	 * @param clazz
	 *            要转换对象的class
	 * @return 对象
	 */
	public static <T> T fromJSON(String json, Class<T> clazz) {
		
		try {
			return mapper.readValue(json, clazz);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * json的字节流转换为对象
	 * 
	 * @param <T>
	 * @param json
	 *            json的字节流
	 * @param clazz
	 *            要转换对象的class
	 * @return 对象
	 */
	public static <T> T fromJSON(InputStream json, Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Object> getJsonList(String jstr, List<Object> li) {
		char[] cstr = jstr.toCharArray();
		boolean bend = false;
		int istart = 0;
		int iend = 0;
		for (int i = 0; i < cstr.length; i++) {
			if ((cstr[i] == '{') && !bend) {
				istart = i;
			}
			if (cstr[i] == '}' && !bend) {
				iend = i;
				bend = true;
			}
		}

		if (istart != 0) {
			String substr = jstr.substring(istart, iend + 1);
			jstr = jstr.substring(0, istart - 1)
					+ jstr.substring(iend + 1, jstr.length());
			substr = substr.replace(",\"children\":", "");
			substr = substr.replace("]", "");
			substr = substr.replace("[", "");
			li.add(substr);
			getJsonList(jstr, li);
		}
		return li;
	}




}
