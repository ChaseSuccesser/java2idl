# 使用方式
`IdlGenerator.generateIdl("/Users/lgx/dev/idl-generator-test/demo.thrift", "com.ligx");`

> 第一个参数是thrift文件存储位置。后面的参数是要扫描的包，可以有多个。

# 特点
1. 支持字段类型是复杂的嵌套类型。例如`Map<String, List<List<String>>>`
2. 针对POJO内部引用其他POJO的情况，只需要在最外层加上`@Idl`注解即可。
