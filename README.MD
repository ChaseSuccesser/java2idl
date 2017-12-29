# 使用方式
- `git clone https://github.com/ChaseSuccesser/java2idl.git`
- `mvn clean install`
- 在项目中引入:
```
<dependency>
    <groupId>com.ligx</groupId>
    <artifactId>java2idl</artifactId>
    <version>1.0.0</version>
</dependency>
```
- 写个main类来使用:
``` java
public class GenerateIdl {
    public static void main(String[] args) {
        IdlGenerator.generateIdl("/Users/lgx/demo.thrift", "com.ligxx");
    }
}
```

> 第一个参数是thrift文件存储位置。后面的参数是要扫描的包，可以有多个。

# 特点
1. 支持字段类型是复杂的嵌套类型。例如`Map<String, List<List<String>>>`
2. 针对POJO内部引用其他POJO的情况，只需要在最外层的类加上`@Idl`注解即可。
