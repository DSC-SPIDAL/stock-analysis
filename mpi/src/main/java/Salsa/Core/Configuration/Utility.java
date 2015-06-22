package Salsa.Core.Configuration;

import Salsa.Core.*;

public final class Utility
{
	/** 
	 Finds specified XmlElement and converts it to its .Net object representation
	 
	 @param any List of XmlElements
	 @param type .Net type to convert to
	 @param elementName Name of element to find
	 @param defaultNamespace Namespace of element to find
	 @return .Net object represenation of requested XML element
	 Creation of XmlSerialzier's dynamically can be slow. Consider caching serializers.
	*/
	public static Object GetElementValue(XmlElement[] any, java.lang.Class type, String elementName, String defaultNamespace)
	{
		Object ret = null;

		// Loop through extended elements and look for element
		for (XmlElement xmlElement : any)
		{
			// Check element's name (wo/ ns prefix) and namespace
			if ((0 == String.Compare(xmlElement.LocalName, elementName, true, CultureInfo.InvariantCulture)) && (0 == String.Compare(xmlElement.NamespaceURI, defaultNamespace, true, CultureInfo.InvariantCulture)))
			{
				// If found, deserialize
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//				using (var reader = new XmlTextReader(new StringReader(xmlElement.OuterXml)))
				XmlTextReader reader = new XmlTextReader(new StringReader(xmlElement.OuterXml));
				try
				{
					XmlSerializer serializer = new XmlSerializer(type, new XmlAttributeOverrides(), new java.lang.Class[] {}, new XmlRootAttribute(elementName), defaultNamespace);

					ret = serializer.Deserialize(reader);
				}
				finally
				{
					reader.dispose();
				}

				break;
			}
		}

		return ret;
	}

	/** 
	 Converts .Net object to an XML string
	 
	 @param o .Net object to convert
	 @param elementName Name to give root XML element
	 @param defaultNamespace Namespace for the XML infoset
	 @return Object's XML serialization string
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static string ConvertToXml(this object o, string elementName, string defaultNamespace)
	public static String ConvertToXml(Object o, String elementName, String defaultNamespace)
	{
		// Serialize .Net object to XML string
		StringBuilder sb = new StringBuilder();
		XmlSerializer serializer = new XmlSerializer(o.getClass(), new XmlAttributeOverrides(), new java.lang.Class[] {}, new XmlRootAttribute(elementName), defaultNamespace);

//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (var writer = new StringWriter(sb, NumberFormatInfo.InvariantInfo))
		StringWriter writer = new StringWriter(sb, NumberFormatInfo.InvariantInfo);
		try
		{
			serializer.Serialize(writer, o);
		}
		finally
		{
			writer.dispose();
		}

		return sb.toString();
	}

	/** 
	 Converts .Net object to an XML string
	 
	 @param o .Net object to convert
	 @param defaultNamespace Namespace to use when serializing
	 @return Object's XML serialization string
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static string ConvertToXml(this object o, string defaultNamespace)
	public static String ConvertToXml(Object o, String defaultNamespace)
	{
		return ConvertToXml(o, o.getClass().getName(), defaultNamespace);
	}

	/** 
	 @param o .Net object to convert
	 @param elementName Name to give root XML element
	 @param defaultNamespace Namespace for the XML infoset
	 @return XmlElement containing Xml serialized object
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static XmlElement ConvertToXmlElement(this object o, string elementName, string defaultNamespace)
	public static XmlElement ConvertToXmlElement(Object o, String elementName, String defaultNamespace)
	{
		String xml = ConvertToXml(o, elementName, defaultNamespace);

		// Convert XML string to XmlElement
		XmlDocument document = new XmlDocument();
		document.LoadXml(xml);
		return document.DocumentElement;
	}

	/** 
	 Returns the properties of the given object as XElements.
	 Properties with null values are still returned, but as empty
	 elements. Underscores in property names are replaces with hyphens.
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<XElement> ConvertPropertiesToXmlElements(this object source)
	public static Iterable<XElement> ConvertPropertiesToXmlElements(Object source)
	{
		for (PropertyInfo prop : source.getClass().GetProperties())
		{
			Object value = prop.GetValue(source, null);
//C# TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to the C# 'yield' keyword:
			yield return new XElement(prop.Name.Replace("_", "-"), value);
		}
	}

	/** 
	 Returns the properties of the given object as XElements.
	 Properties with null values are returned as empty attributes.
	 Underscores in property names are replaces with hyphens.
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<XAttribute> ConvertPropertiesToXmlAttributes(this object source)
	public static Iterable<XAttribute> ConvertPropertiesToXmlAttributes(Object source)
	{
		for (PropertyInfo prop : source.getClass().GetProperties())
		{
			Object value = prop.GetValue(source, null);
//C# TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to the C# 'yield' keyword:
			yield return new XAttribute(prop.Name.Replace("_", "-"), (value != null) ? value : "");
		}
	}

	/** 
	 Obtains a delegate to invoke a parameterless constructor
	 
	 <typeparam name="TResult">The base/interface type to yield as the
	 new value; often object except for factory pattern implementations</typeparam>
	 @param type The Type to be created
	 @return A delegate to the constructor if found, else null
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static Func<TResult> Ctor<TResult>(this Type type)
	public static <TResult> Func<TResult> Ctor(java.lang.Class type)
	{
		java.lang.reflect.Constructor ci = GetConstructor(type, java.lang.Class.EmptyTypes);
		return Expression.<Func<TResult>>Lambda(Expression.New(ci)).Compile();
	}

	/** 
	 Obtains a delegate to invoke a constructor which takes a parameter
	 
	 <typeparam name="TArg1">The type of the constructor parameter</typeparam>
	 <typeparam name="TResult">The base/interface type to yield as the
	 new value; often object except for factory pattern implementations</typeparam>
	 @param type The Type to be created
	 @return A delegate to the constructor if found, else null
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static Func<TArg1, TResult> Ctor<TArg1, TResult>(this Type type)
	public static <TArg1, TResult> Func<TArg1, TResult> Ctor(java.lang.Class type)
	{
		java.lang.reflect.Constructor ci = GetConstructor(type, TArg1.class);
		ParameterExpression param1 = Expression.Parameter(TArg1.class, "arg1");

		return Expression.<Func<TArg1, TResult>>Lambda(Expression.New(ci, param1), param1).Compile();
	}

	/** 
	 Obtains a delegate to invoke a constructor with multiple parameters
	 
	 <typeparam name="TArg1">The type of the first constructor parameter</typeparam>
	 <typeparam name="TArg2">The type of the second constructor parameter</typeparam>
	 <typeparam name="TResult">The base/interface type to yield as the
	 new value; often object except for factory pattern implementations</typeparam>
	 @param type The Type to be created
	 @return A delegate to the constructor if found, else null
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static Func<TArg1, TArg2, TResult> Ctor<TArg1, TArg2, TResult>(this Type type)
	public static <TArg1, TArg2, TResult> Func<TArg1, TArg2, TResult> Ctor(java.lang.Class type)
	{
		java.lang.reflect.Constructor ci = GetConstructor(type, TArg1.class, TArg2.class);
		ParameterExpression param1 = Expression.Parameter(TArg1.class, "arg1"), param2 = Expression.Parameter(TArg2.class, "arg2");

		return Expression.<Func<TArg1, TArg2, TResult>>Lambda(Expression.New(ci, param1, param2), param1, param2).Compile();
	}

	/** 
	 Obtains a delegate to invoke a constructor with multiple parameters
	 
	 <typeparam name="TArg1">The type of the first constructor parameter</typeparam>
	 <typeparam name="TArg2">The type of the second constructor parameter</typeparam>
	 <typeparam name="TArg3">The type of the third constructor parameter</typeparam>
	 <typeparam name="TResult">The base/interface type to yield as the
	 new value; often object except for factory pattern implementations</typeparam>
	 @param type The Type to be created
	 @return A delegate to the constructor if found, else null
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static Func<TArg1, TArg2, TArg3, TResult> Ctor<TArg1, TArg2, TArg3, TResult>(this Type type)
	public static <TArg1, TArg2, TArg3, TResult> Func<TArg1, TArg2, TArg3, TResult> Ctor(java.lang.Class type)
	{
		java.lang.reflect.Constructor ci = GetConstructor(type, TArg1.class, TArg2.class, TArg3.class);
		ParameterExpression param1 = Expression.Parameter(TArg1.class, "arg1"), param2 = Expression.Parameter(TArg2.class, "arg2"), param3 = Expression.Parameter(TArg3.class, "arg3");

		return Expression.<Func<TArg1, TArg2, TArg3, TResult>>Lambda(Expression.New(ci, param1, param2, param3), param1, param2, param3).Compile();
	}

	/** 
	 Obtains a delegate to invoke a constructor with multiple parameters
	 
	 <typeparam name="TArg1">The type of the first constructor parameter</typeparam>
	 <typeparam name="TArg2">The type of the second constructor parameter</typeparam>
	 <typeparam name="TArg3">The type of the third constructor parameter</typeparam>
	 <typeparam name="TArg4">The type of the fourth constructor parameter</typeparam>
	 <typeparam name="TResult">The base/interface type to yield as the
	 new value; often object except for factory pattern implementations</typeparam>
	 @param type The Type to be created
	 @return A delegate to the constructor if found, else null
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static Func<TArg1, TArg2, TArg3, TArg4, TResult> Ctor<TArg1, TArg2, TArg3, TArg4, TResult>(this Type type)
	public static <TArg1, TArg2, TArg3, TArg4, TResult> Func<TArg1, TArg2, TArg3, TArg4, TResult> Ctor(java.lang.Class type)
	{
		java.lang.reflect.Constructor ci = GetConstructor(type, TArg1.class, TArg2.class, TArg3.class, TArg4.class);
		ParameterExpression param1 = Expression.Parameter(TArg1.class, "arg1"), param2 = Expression.Parameter(TArg2.class, "arg2"), param3 = Expression.Parameter(TArg3.class, "arg3"), param4 = Expression.Parameter(TArg4.class, "arg4");

		return Expression.<Func<TArg1, TArg2, TArg3, TArg4, TResult>>Lambda(Expression.New(ci, param1, param2, param3, param4), param1, param2, param3, param4).Compile();
	}

	private static java.lang.reflect.Constructor GetConstructor(java.lang.Class type, java.lang.Class... argumentTypes)
	{
		type.ThrowIfNull("type");
		argumentTypes.ThrowIfNull("argumentTypes");

		java.lang.reflect.Constructor ci = type.getConstructor(argumentTypes);
		if (ci == null)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(type.getSimpleName()).append(" has no ctor(");
			for (int i = 0; i < argumentTypes.length; i++)
			{
				if (i > 0)
				{
					sb.append(',');
				}
				sb.append(argumentTypes[i].getSimpleName());
			}
			sb.append(')');
			throw new UnsupportedOperationException(sb.toString());
		}
		return ci;
	}

	/** 
	 Throws an ArgumentNullException if the given data item is null.
	 
	 @param data The item to check for nullity.
	 @param name The name to use when throwing an exception, if necessary
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static void ThrowIfNull<T>(this T data, string name) where T : class
	public static <T> void ThrowIfNull(T data, String name)
	{
		if (data == null)
		{
			throw new IllegalArgumentException(name);
		}
	}

	/** 
	 Throws an ArgumentNullException if the given data item is null.
	 No parameter name is specified.
	 
	 @param data The item to check for nullity.
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static void ThrowIfNull<T>(this T data) where T : class
	public static <T> void ThrowIfNull(T data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException();
		}
	}
}