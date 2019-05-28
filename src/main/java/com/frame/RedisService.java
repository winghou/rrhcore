package com.frame;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

@Service
public class RedisService
{
    
    // 操作redis客户端
    private static Jedis jedis;

    @Autowired
    @Qualifier("jedisConnectionFactory")
    private JedisConnectionFactory jedisConnectionFactory;

    private static RedisService redisService;
    
    public static void startRedisService(ApplicationContext app)
    {
        redisService = (RedisService)app.getBean("redisService");
    }
    
    public static RedisService getInstance()
    {
            return redisService;
    }

    /**
     * 通过key删除（字节）
     * 
     * @param key
     */
    public void del(byte[] key)
    {
        this.getJedis().del(key);
    }

    /**
     * 通过key删除
     * 
     * @param key
     */
    public void del(String key)
    {
        this.getJedis().del(key);
    }

    /**
     * 添加key value 并且设置存活时间(byte)
     * 
     * @param key
     * @param value
     * @param liveTime
     */
    public void set(byte[] key, byte[] value, int liveTime)
    {
        this.set(key, value);
        this.getJedis().expire(key, liveTime);
    }

    /**
     * 添加key value 并且设置存活时间
     * 
     * @param key
     * @param value
     * @param liveTime
     */
    public void set(String key, String value, int liveTime)
    {
        this.set(key, value);
        this.getJedis().expire(key, liveTime);
    }

    /**
     * 添加key value
     * 
     * @param key
     * @param value
     */
    public void set(String key, String value)
    {
        this.getJedis().set(key, value);
    }
    
    /**
     * 添加key value (字节)(序列化)
     * 
     * @param key
     * @param value
     */
    public void set(byte[] key, byte[] value)
    {
        this.getJedis().set(key, value);
    }

    /**
     * 获取redis value (String)
     * 
     * @param key
     * @return
     */
    public String get(String key)
    {
        String value = this.getJedis().get(key);
        return value;
    }

    /**
     * 获取redis value (byte [] )(反序列化)
     * 
     * @param key
     * @return
     */
    public byte[] get(byte[] key)
    {
        return this.getJedis().get(key);
    }

    /**
     * 通过正则匹配keys
     * 
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern)
    {
        return this.getJedis().keys(pattern);
    }

    /**
     * 检查key是否已经存在
     * 
     * @param key
     * @return
     */
    public boolean exists(String key)
    {
        return this.getJedis().exists(key);
    }

    /**
     * 清空redis 所有数据
     * 
     * @return
     */
    public String flushDB()
    {
        return this.getJedis().flushDB();
    }

    /**
     * 查看redis里有多少数据
     */
    public long dbSize()
    {
        return this.getJedis().dbSize();
    }

    /**
     * 检查是否连接成功
     * 
     * @return
     */
    public String ping()
    {
        return this.getJedis().ping();
    }

    /**
     * 获取一个jedis 客户端
     * 
     * @return
     */
    private Jedis getJedis()
    {
        if (jedis == null)
        {
            return jedisConnectionFactory.getShardInfo().createResource();
        }
        return jedis;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        ApplicationContext app = new ClassPathXmlApplicationContext("classpath:spring-redis.xml");
        // 这里已经配置好,属于一个redis的服务接口
        RedisService redisService = (RedisService)app.getBean("redisService");
        
        String ping = redisService.ping();// 测试是否连接成功,连接成功输出PONG
        System.out.println(ping);
        
        // 首先,我们看下redis服务里是否有数据
        long dbSizeStart = redisService.dbSize();
        System.out.println(dbSizeStart);
        
        redisService.set("username", "oyhk");// 设值(查看了源代码,默认存活时间30分钟)
        String username = redisService.get("username");// 取值
        System.out.println(username);
        redisService.set("username1", "oyhk1", 1);// 设值,并且设置数据的存活时间(这里以秒为单位)
        String username1 = redisService.get("username1");
        System.out.println(username1);
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 我睡眠一会,再去取,这个时间超过了,他的存活时间
        String liveUsername1 = redisService.get("username1");
        System.out.println(liveUsername1);// 输出null
        
        // 是否存在
        boolean exist = redisService.exists("username");
        System.out.println(exist);
        
        // 查看keys
        Set<String> keys = redisService.keys("*");// 这里查看所有的keys
        System.out.println(keys);// 只有username username1(已经清空了)
        
        // 删除
        redisService.set("username2", "oyhk2");
        String username2 = redisService.get("username2");
        System.out.println(username2);
        redisService.del("username2");
        String username2_2 = redisService.get("username2");
        System.out.println(username2_2);// 如果为null,那么就是删除数据了
        
        // dbsize
        long dbSizeEnd = redisService.dbSize();
        System.out.println(dbSizeEnd);
        
        // 清空reids所有数据
        // redisService.flushDB();
    }


    /**
    * 描述：向set集合中插入一个值
    * 创建人：liulei
    * 时间：2017/11/30 16:43
    * @version
    */
    public   void sadd (String setName , String value){
    	getJedis().sadd(setName,value);
    }

    /**
    * 描述：判断set集合有没有value
    * 创建人：liulei
    * 时间：2017/11/30 16:44
    * @version
    */
    public  boolean contiansValue(String setName , String value){
    	boolean  flag= getJedis().sismember(setName ,value);
        return   flag;

    }




}
