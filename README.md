<h1 align="center"> 简笔记 - API - 1.0 </h1>

<p align="center">
  <a href="https://github.com/spring-projects/spring-boot">
    <img src="https://img.shields.io/badge/spring--boot-2.1.5-blue" alt="spring-boot">
  </a>
  <a href="https://github.com/mongodb/mongo">
    <img src="https://img.shields.io/badge/mongodb-4.2-blue" alt="mongodb">
  </a>
  <a href="https://github.com/jwtk/jjwt">
    <img src="https://img.shields.io/badge/jwt-0.9.1-blue" alt="jwt">
  </a>
  <a href="https://oauth.net/2/">
    <img src="https://img.shields.io/badge/oauth2-2.0-blue" alt="oauth2">
  </a>
  <a href="https://github.com/Zealon159/light-reading-cloud/blob/master/LICENSE">
    <img src="https://img.shields.io/badge/License-MIT-yellow" alt="license">
  </a>
</p>



## 项目介绍

简笔记（easy notes）打造你的轻便私人笔记。

基于 SpringBoot 生态开发的接口服务，涉及Spring Security、OAuth2、Jwt、MongoDB 等技术的应用。

客户端采用 Vue.js 、Ant Design  开发：[点击进入仓库](https://github.com/Zealon159/easy-notes-client)

### 演示

演示地址：[http://notes.zealon.cn/](http://notes.zealon.cn/) 

部分截图：

![](http://resource.zealon.cn/login.jpg)

![](http://resource.zealon.cn/notes.jpg)

## 工程目录

采用传统的分层模型设计：

```
- src/main/java
  - cn.zealon.notes
    - common / 公共包
      - base / 抽象基类
      - config / 工程配置
      - exception / 自定义异常
      - result / 响应结果封装
      - utils / 工具类
    - controller / 控制器 
    - domain / 领域对象
    - repository / MongoDB存储
    - security / 安全模块
      - config / 配置
      - controller / 认证相关控制器
      - domain / 安全相关领取对象
      - filter / 自定义过滤器
      - jwt / JWT组件
      - service / 接口服务
    - service / 服务层
    - vo / 视图对象
    - security / 安全处理(shiro)
    - Application.java / 项目启动类
- src/main/resources
  - application.yml / 应用配置文件
```

## 核心功能

项目的笔记本、笔记等功能比较简单就不介绍了，重点说明一下 `OAuth2.0` 社交登录模块，代码全在项目的 `cn.zealon.notes.security` 包中。

#### OAuth2授权

> :star: 关于 OAuth2 的知识点可参考官网介绍：https://tools.ietf.org/html/rfc6749 。
>
> :star: 也可以看阮一峰老师的博客进行入门：https://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html

话不多说，先看一下OAuth2.0授权码模式的交互过程：

![](http://resource.zealon.cn/oauth2-login.png)

首先客户端访问社交登录平台（如：微信、Github、微博等），若为登录状态时将获取你的允许授权，同意后返回一个code码并跳转到客户端；这时候我们把授权码和秘钥在服务端去请求访问令牌，最后拿到访问令牌再请求社交平台的用户资源信息，处理登录返回给客户端自定义的Token，这里我们采用JWT（JSON Web Token）方式，产生并返回一个 Token，客户端将Token保存起来。

![](http://resource.zealon.cn/oauth2-login2.png)

当客户端再次访问服务端接口，一般会将 Token 放入请求头中进行传递，如果是单体项目可以在过滤器中校验Token的有效性，如果是微服务则在网关中校验，如果校验成功，则将该请求转发到后端的服务中，在转发时会将 Token 解析出的用户信息也一并带过去，这样在后端的服务中就不用再解析一遍 Token 获取的用户信息，这个操作统一在网关进行的。

如果校验失败，那么就直接返回对应的结果给客户端，不会将请求进行转发。客户端接收到了401状态码则提示用户进行再次登录。

:bangbang: 这里有必要提示一下，其实在客户端拿到code之后，完全可以再前端通过秘钥请求到 `Access Token`，为什么还要再服务端去处理，是不是多次一举？

答案是一定要在服务端处理，因为秘钥暴露在客户端就不安全了，会被坏人恶意利用哦！

:bangbang: jwt生成令牌是不能修改的，如果用户修改了密码等信息，要等到Token过期才生效，所以尽量不要设置Token过长时间，可以通过定时刷新Token的方式来延长到期哦

#### OAuth2 回调处理的登录、注册、绑定功能

我们一个账户名下，可以同时绑定多个OAuth2授权，比如 `Summer` 这个账户名即绑定了QQ又绑定了微博，那么使用任意一个OAuth2登录成功后，都是这一个账户。关于OAuth2的回调处理这里也简单介绍一下。

对于社交登录服务来说，它本身只负责提供授权，不关心我们是绑定还是登录、注册，所以授权成功后的事件是登录、注册还是绑定需要在我们业务接口中处理，得知了这一点，就会少走一些弯路哈，话不多说直接上图：

![](http://resource.zealon.cn/oauth2-callback.png)

整体流程主要处理3个事，登录、注册、绑定（过程中包括了一些必要的逻辑校验部分）。

**获取社交用户信息**

首先通过 `Access Token` 获取 `OAuth2` 用户信息作为切入点，之后就可以开展我们自己的业务处理了。

由于会接入多个社交平台，而且每家的社交平台的接口返回都不一样，但是返回的社交信息是固定的，所以这里使用了策略模式来处理，提升可扩展性，同时避免了冗长的 `if - else` 。

**业务处理**

首先要判断当前是否处于登录状态，可以使用 `Shiro`、`Spring Security` 等安全框架来实现，这里我们用的`Spring Security` 。剩下的就是具体的业务了：

- 登录处理：直接返回JWT加密的Token给客户度。
- 直接注册：注册、绑定后，再调用登录处理的逻辑，在客户端直接跳转到主页面，避免用户手动登录。
- 手动注册：也就是图中的提出处理分支，因为被其它给用了，必须要用户自定义一个名字了，返回给客户端OAuth2信息，并给与友好提示，用户注册提交同时携带OAuth2信息和注册信息，处理注册和绑定。
- 绑定处理：查询OAuth2用户名是否被绑定过，没绑定过直接绑定；已绑定要告诉客户端，然后提示已绑定什么的，告诉用户可以使用社交账户直接登录什么的...... 巴拉巴拉

## 附录

附录1.在线UML编辑工具：https://app.diagrams.net/

附录2.OAuth2协议标准：https://tools.ietf.org/html/rfc6749 

附录3.Github OAuth 应用：https://docs.github.com/en/developers/apps/authorizing-oauth-apps

附录4.JWT协议标准、官网：https://tools.ietf.org/html/rfc7519、https://jwt.io/

## License

[MIT](https://github.com/Zealon159/easy-notes/blob/main/LICENSE)

Copyright (c) 2021 光彩盛年


