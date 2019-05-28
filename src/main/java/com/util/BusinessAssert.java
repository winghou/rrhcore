package com.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 数据验证的Assert,所有抛出异常均为{@code ServiceException}
 * 
 * @author meiguiyang
 * @date 2014年7月28日 下午2:49:48
 * @version 1.0
 */
public class BusinessAssert
{
    /**
     * <p>
     * 对一个boolean表达式进行断言,如果表达式的值为false则抛出ServiceException
     * </p>
     * 
     * @param expression
     */
    public static void isTrue(boolean expression)
    {
        isTrue(expression, "[Assertion failed] - this expression must be true!");
    }

    /**
     * <p>
     * 对一个boolean表达式进行断言,如果表达式的值为false则抛出ServiceException
     * </p>
     * 
     * @param expression
     * @param message
     */
    public static void isTrue(boolean expression, String message)
    {
        if (!expression)
        {
            throw new ServiceException(message);
        }
    }

    /**
     * <p>
     * 如果object不为null则抛出ServiceException
     * </p>
     * 
     * @param object
     */
    public static void isNull(Object object)
    {
        isNull(object, "[Assertion failed] - the object argument must be null!");
    }

    /**
     * <p>
     * 如果object不为null则抛出ServiceException
     * </p>
     * 
     * @param object
     * @param message
     */
    public static void isNull(Object object, String message)
    {
        if (object != null)
        {
            throw new ServiceException(message);
        }
    }

    /**
     * <p>
     * 如果object为null则抛出ServiceException
     * </p>
     * 
     * @param object
     */
    public static void notNull(Object object)
    {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null!");
    }

    /**
     * <p>
     * 如果object为null则抛出ServiceException
     * </p>
     * 
     * @param object
     * @param message
     */
    public static void notNull(Object object, String message)
    {
        if (object == null)
        {
            throw new ServiceException(message);
        }
    }

    /**
     * <p>
     * 如果object不为空值(null, "", " ", "null", empty collection, empty map, empty array)则抛出ServiceException
     * </p>
     * 
     * @param object
     */
    public static void isEmpty(Object object)
    {
        isEmpty(object, "[Assertion failed] - this object must be null or empty if it is a array、collection!");
    }

    /**
     * <p>
     * 如果object不为空值(null, "", " ", "null", empty collection, empty map, empty array)则抛出ServiceException
     * </p>
     * 
     * @param object
     * @param message
     */
    public static void isEmpty(Object object, String message)
    {
        if (!isEmptyObject(object))
        {
            throw new ServiceException(message);
        }
    }

    /**
     * <p>
     * 如果object为空值(null, "", " ", "null", empty collection, empty map, empty array)则抛出ServiceException
     * </p>
     * 
     * @param object
     */
    public static void notEmpty(Object object)
    {
        notEmpty(object, "[Assertion failed] - this object must be not null or not empty if it is a array、collection!");
    }

    /**
     * <p>
     * 如果object为空值(null, "", " ", "null", empty collection, empty map, empty array)则抛出ServiceException
     * </p>
     * 
     * @param object
     * @param message
     */
    public static void notEmpty(Object object, String message)
    {
        if (isEmptyObject(object))
        {
            throw new ServiceException(message);
        }
    }

    private static boolean isEmptyObject(Object object)
    {
        if (object == null)
        {
            return true;
        }
        else if (object instanceof String)
        {
            return "".equals(((String) object).trim());
        }
        else if (object instanceof Collection<?>)
        {
            return ((Collection<?>) object).isEmpty();
        }
        else if (object instanceof Map<?, ?>)
        {
            return ((Map<?, ?>) object).isEmpty();
        }
        else if (object.getClass().isArray())
        {
            return Array.getLength(object) == 0;
        }
        else
        {
            return false;
        }
    }

    /**
     * <p>
     * 如果数组array中的元素存在null值,则抛出ServiceException
     * </p>
     * 
     * @param array
     */
    public static void noNullElements(Object[] array)
    {
        noNullElements(array, "[Assertion failed] - this array must not contain any null elements!");
    }

    /**
     * <p>
     * 如果数组array中的元素存在null值,则抛出ServiceException
     * </p>
     * 
     * @param array
     * @param message
     */
    public static void noNullElements(Object[] array, String message)
    {
        if (array != null)
        {
            for (Object element : array)
            {
                if (element == null)
                {
                    throw new ServiceException(message);
                }
            }
        }
    }

    /**
     * <p>
     * 如果集合collection中的元素存在null值,则抛出ServiceException
     * </p>
     * 
     * @param array
     */
    public static void noNullElements(Collection<?> collection)
    {
        noNullElements(collection, "[Assertion failed] - this collection must not contain any null elements!");
    }

    /**
     * <p>
     * 如果集合collection中的元素存在null值,则抛出ServiceException
     * </p>
     * 
     * @param array
     * @param message
     */
    public static void noNullElements(Collection<?> collection, String message)
    {
        if (collection != null)
        {
            for (Object element : collection)
            {
                if (element == null)
                {
                    throw new ServiceException(message);
                }
            }
        }
    }

    /**
     * <p>
     * 如果集合map中的元素存在value=null值,则抛出ServiceException
     * </p>
     * 
     * @param array
     */
    public static void noNullValues(Map<?, ?> map)
    {
        noNullValues(map, "[Assertion failed] - this map must not contain any null value!");
    }

    /**
     * <p>
     * 如果集合map中的元素存在value=null值,则抛出ServiceException
     * </p>
     * 
     * @param array
     * @param message
     */
    public static void noNullValues(Map<?, ?> map, String message)
    {
        if (map != null)
        {
            for (Map.Entry<?, ?> entry : map.entrySet())
            {
                if (entry.getValue() == null)
                {
                    throw new ServiceException(message);
                }
            }
        }
    }

    /**
     * <p>
     * 如果集合map中的元素存在key=null值,则抛出ServiceException
     * </p>
     * 
     * @param array
     */
    public static void noNullKeys(Map<?, ?> map)
    {
        noNullKeys(map, "[Assertion failed] - this map must not contain any null key!");
    }

    /**
     * <p>
     * 如果集合map中的元素存在key=null值,则抛出ServiceException
     * </p>
     * 
     * @param array
     * @param message
     */
    public static void noNullKeys(Map<?, ?> map, String message)
    {
        if (map != null)
        {
            for (Map.Entry<?, ?> entry : map.entrySet())
            {
                if (entry.getKey() == null)
                {
                    throw new ServiceException(message);
                }
            }
        }
    }

    /**
     * <p>
     * 如果对象obj不是指定的类型type,则抛出ServiceException
     * </p>
     * 
     * @param type
     * @param obj
     */
    public static void isInstanceOf(Class<?> type, Object obj)
    {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj))
        {
            throw new ServiceException("Object of class [" + (obj != null ? obj.getClass().getName() : "null") + "] must be an instance of " + type);
        }
    }

    /**
     * <p>
     * 如果对象obj不是指定的类型type,则抛出ServiceException
     * </p>
     * 
     * @param type
     * @param obj
     * @param message
     */
    public static void isInstanceOf(Class<?> type, Object obj, String message)
    {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj))
        {
            throw new ServiceException(message);
        }
    }

    /**
     * <p>
     * 如果subType的父类不是superType,则抛出ServiceException
     * </p>
     * 
     * @param superType
     * @param subType
     */
    public static void isAssignable(Class<?> superType, Class<?> subType)
    {
        notNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType))
        {
            throw new ServiceException(subType + " is not assignable to " + superType);
        }
    }

    /**
     * <p>
     * 如果subType的父类不是superType,则抛出ServiceException
     * </p>
     * 
     * @param superType
     * @param subType
     * @param message
     */
    public static void isAssignable(Class<?> superType, Class<?> subType, String message)
    {
        notNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType))
        {
            throw new ServiceException(message);
        }
    }
}
