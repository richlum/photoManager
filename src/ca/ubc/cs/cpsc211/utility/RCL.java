package ca.ubc.cs.cpsc211.utility;

import java.util.Collection;

public class RCL {
	public static <E> String listNames(Collection<E> col){
		String result = "[";
		int i = 0;
		for (E element : col){
			if (i>0){
				result+=",";
			}
			i++;
			result+= element.toString();
		}
		result +="]";
		return result;
	}
}
