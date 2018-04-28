package cn.dewro.remex.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;
import cn.dew.remex.context.interfaces.ITurnVal;
import cn.dewro.remex.core.enums.SortOrder;
import cn.dewro.remex.exception.RemexException;

/**
 * 排序工具类
 * 
 * @author Qiaoxin.Hong
 *
 */
public class SortUtils {
	
	/**
	 * 排序
	 * @param list 需排序的集合
	 */
	public static <T,R> void sort(List<T> list) {
		sort(list, null, SortOrder.ASC, true);
	}
	
	/**
	 * 排序
	 * @param list 需排序的集合
	 * @param turnVal 值转换器
	 */
	public static <T,R> void sort(List<T> list, ITurnVal<T, R> turnVal) {
		sort(list, turnVal, SortOrder.ASC, true);
	}
	
	/**
	 * 排序
	 * @param list 需排序的集合
	 * @param turnVal 值转换器
	 * @param order 排序的顺序
	 */
	public static <T,R> void sort(List<T> list, SortOrder order) {
		sort(list, null, order, true);
	}
	
	/**
	 * 排序
	 * @param list 需排序的集合
	 * @param turnVal 值转换器
	 * @param order 排序的顺序
	 */
	public static <T,R> void sort(List<T> list, ITurnVal<T, R> turnVal, SortOrder order) {
		sort(list, turnVal, order, true);
	}
	

	/**
	 * 排序
	 * @param list 需排序的集合
	 * @param field 要排序的字段
	 */
	public static <T> void sort(List<T> list, String field) {
		sort(list, SortOrder.ASC, field, true);
	}
	
	/**
	 * 排序
	 * @param list 需排序的集合
	 * @param order 排序的顺序
	 * @param field 要排序的字段
	 * @param nullMin null作为最小值
	 */
	public static <T> void sort(List<T> list, SortOrder order, String field) {
		sort(list, order, field, true);
	}
	
	/**
	 * 排序
	 * @param list 需排序的集合
	 * @param order 排序的顺序
	 * @param field 要排序的字段
	 * @param nullMin null作为最小值
	 */
	public static <T> void sort(List<T> list, SortOrder order, String field, boolean nullMin) {
		sort(list, new SortDefaultTurnValField<T>(field), order, nullMin);
	}
	
	/**
	 * 排序
	 * @param list 需排序的集合
	 * @param turnVal 值转换器
	 * @param order 排序的顺序
	 * @param nullMin null作为最小值
	 */
	public static <T,R> void sort(List<T> list, ITurnVal<T, R> turnVal, SortOrder order, boolean nullMin) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		
		//是否是降序
		final boolean isDesc = SortOrder.DESC.equals(order);
		
		//排序
		list.sort((o1, o2) -> {
			Object v1 = o1;
			Object v2 = o2;
			
			if (turnVal != null) {
				v1 = turnVal.turn(o1);
				v2 = turnVal.turn(o2);
			}
			
			int cr = compVal(v1, v2, nullMin);
			
			return isDesc ? -cr : cr;
		});
	}
	
	/**
	 * 比较两者值的大小，如果实现了Comparable接口，则以其compareTo()方法进行比较，没实现则比较toString()
	 * 
	 * @see java.lang.Comparable<T>
	 * 
	 * @param v1 值1
	 * @param v2 值2
	 * @return 0：v1 = v2；  -1：v1 < v2；  1：v1 > v2
	 */
	@SuppressWarnings("unchecked")
	public static <T> int compVal(T v1, T v2, boolean nullMin) {
		if (v1 == null) {
			if (v2 == null) {
				return 0;
			}
			return nullMin ? -1 : 1;
		}
		if (v2 == null) {
			return nullMin ? 1 : -1;
		}
		
		if (v1 instanceof Comparable) {
			Comparable<T> comparable1 = (Comparable<T>) v1;
			return comparable1.compareTo(v2);
		} else {
			return v1.toString().compareTo(v2.toString());
		}
	}
}



/**
 * 排序用，用于取对象的某属性的值
 * 
 * @author Qiaoxin.Hong
 *
 * @param <V>
 */
class SortDefaultTurnValField<V> implements ITurnVal<V, Object> {

	/**
	 * 对象的某字段名
	 */
	private String field;

	/**
	 * 读取字段值的方法
	 */
	private Method readMethod = null;

	public SortDefaultTurnValField() {
		super();
	}

	public SortDefaultTurnValField(String field) {
		super();
		this.field = field;
	}

	@Override
	public Object turn(V val) {
		Object r = null;
		if (val != null) {
			try {
				if (readMethod == null) {
					PropertyDescriptor pd = new PropertyDescriptor(field, val.getClass());
					readMethod = pd.getReadMethod();
				}
				r = readMethod.invoke(val);
			} catch (Exception e) {
				throw new RemexException("排序根据对象属性转换值时异常！", e);
			}

		}
		return r;
	}

	public String getField() {
		return field;
	}
	
	public void setField(String field) {
		this.field = field;
	}
}
