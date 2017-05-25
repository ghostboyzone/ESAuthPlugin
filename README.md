### 说明

* `elasticsearch`权限插件
* 功能列表
  * 用户名、密码校验
  * 请求ip校验

### 安装

* 下载源码
* 打包： mvn clean package
* 在`$ES_HOME/plugins`下新建目录`auth`，将产出`target/release/xxx.zip`的压缩文件解压到`$ES_HOME/plugins/auth`中

### 配置

* 复制插件根目录下的`auth.conf`文件到es的配置目录（`$ES_HOME/conf/auth.conf`），如果没有这个配置文件，安装插件后es将会启动失败

* 配置项参数含义

  | 参数           | 默认值           | 说明       |
  | ------------ | ------------- | -------- |
  | open_auth    | false         | 是否开启验证   |
  | username     | admin         | 配置的用户名   |
  | password     | admin         | 配置的密码    |
  | open_ip_auth | false         | 是否开启ip授权 |
  | ip_auth_list | ["127.0.0.1"] | ip授权名单   |

### 使用

* 开启授权后，请求es的rest接口，将返回如下json，http状态码是`403`

  ```
  $ curl -XGET 'localhost:9200?pretty'
  {
    "status" : "FORBIDDEN",
    "message" : "You are not login"
  }
  ```

* 开启授权后，如果需要正常请求到结果，需要将用户名(`username`)和密码(`password`)拼接成 `username:password`，然后再进行base64编码得到字符串`auth`，再以参数形式传入进去。

  ```
  $ curl -XGET 'localhost:9200?auth=YWRtaW46YWRtaW4=&pretty'
  {
    "name" : "node-test",
    "cluster_name" : "es-docker",
    "version" : {
      "number" : "2.3.2",
      "build_hash" : "b9e4a6acad4008027e4038f6abed7f7dba346f94",
      "build_timestamp" : "2016-04-21T16:03:47Z",
      "build_snapshot" : false,
      "lucene_version" : "5.5.0"
    },
    "tagline" : "You Know, for Search"
  }
  ```

  > 其中 `YWRtaW46YWRtaW4=` 是 `base64_encode("admin:admin")` 的结果

* 配置文件支持`热加载`，如果修改配置后，需要让配置文件生效，可以请求`/_auth/config_reload`去重载配置文件。

  ```
  // 热更新配置
  $ curl -XGET 'localhost:9200/_auth/config_reload?pretty'
  {
    "status" : "OK",
    "message" : "config reload success"
  }
  // 读取当前配置
  $ curl -XGET 'localhost:9200/_auth/config_show?pretty'
  {
    "open_auth" : false,
    "username" : "admin",
    "password" : "admin",
    "open_ip_auth" : false,
    "ip_auth_list" : [ "127.0.0.1" ]
  }
  ```

  ​