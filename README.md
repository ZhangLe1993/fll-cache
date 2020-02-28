# 项目说明
    # 提供redis缓存注解，以工具类jar包的方式继承到springboot项目中
# 注解说明

##### 1、@CacheItemGet
    # 零拷贝从redis中根据缓存 key, hashKey 获取 hashValue
    # 使用方法
    # @CacheItemGet(key = "biubiu-field-hash", hKey = "#tableName+#colName")
      public String getValue(String tableName, String colName) {
          //省略...
      }
##### 2、@CacheItemPut
    # 缓存存入redis, 支持存储一个元素到hashMap中，避免修改范围，和数据加载到内存中修改
    # 使用方法
    # @CacheItemPut(key = "biubiu-field-hash", hKey = "#tableName+#colName")
      public Object putValue(String tableName, String colName) {
         //省略...
      }   
      
##### 2、@CacheMapGet
    # 缓存存入redis,与@Cacheable的区别是，不直接存储二进制的对象，不便于定位，和使用客户端查看，并且支持自定义缓存失效时间，格式存储为redis中的hashMap
    # 使用方法
    # @CacheMapGet(key = "biubiu-field-hash", expire = 6 * 60 * 60L)
      public Map<String, String> getMap() {
          //省略...
      }
      
##### 2、@CacheMapPut
    # 缓存存入redis,与@CachePut的区别是，不直接存储二进制的对象，不便于定位，和使用客户端查看，并且支持自定义缓存失效时间，格式存储为redis中的hashMap
    # 使用方法
    # @CacheMapPut(key = "biubiu-field-hash", expire = 6 * 60 * 60L)
      public Map<String, String> putMap() {
         //省略...
      }

# set说明
##### RedisSetService类中提供了常用的一些方法。