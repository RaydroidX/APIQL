# 一、背景
目前向前端提供数据聚合的服务端API场景普遍有如下痛点：
1. 服务端需要根据前端的需求不停的开发新版本接口，增加了开发与维护的成本；
2. 服务端对返回契约做灵活动态切割成本较大，且不同终端平台要求存在差异；
3. 类似GraphQL的框架可解决上述问题，但有较高的学习和掌握难度，而且实际应用中大多数场景前端只希望服务端能封装用于首屏的若干接口的串行或者并行调用顺序的处理逻辑。

因此，如果有一个针对数据聚合场景的类 GraphQL引擎接口，解决上述痛点，能够极大降低前端数据聚合接口(BFF API)的开发和运维成本。

# 二、设计思路
基于上述背景，我们考虑设计一个自定义SQL-Like DSL语言的引擎框架APIQL，能实现如下要求：
1. 零成本 BFF 开发：服务端一次实现，后续无需迭代；
2. 前端接口调用标准化：可定义前端接口调用方式和规范，以适配服务端的执行机制；
3. 灵活的接口调用能力：可以由前端来决定对若干接口的串行、并行混合调用，并支持裁剪 JSON 结果；
4. 安全性：对前端一次性请求API的数量上限做整体限制；
5. 异常处理：合理的体现接口实际的返回结果。

# 三、APIQL语法定义
<table>

<tr>
<td>APIQL语句</td>
<td>语法设计</td>
<td>备注</td>
</tr>

<tr>
<td>set table1 as /soa/10225/getHealth;
<br>select *, (name, age) post from /soa/10225/getHealth where id = 15</td>
<td><ul><li>*是查所有字段，括号内表示排除的字段</li> <li>from后面跟的支持完整的地址和相对路径的path</li>
<li>where 字段后面是传的参数</li> <li>set设置别名，这样在sql中就可以直接写别名就好，改起来也方便</li></ul></td>
<td>普通的APIQL</td>
</tr>

<tr>
<td>select name, age post from /soa/10225/getHealth where id = 15</td>
<td>select指定字段</td>
<td> </td>
</tr>

<tr>
<td>select user.name, age post from /soa/10225/getHealth where id = 15</td>
<td>支持select多级字段</td>
<td>user.name</td>
</tr>

<tr>
<td>select name, age post from /soa/10225/getHealth as api1 where id = api2.id

join select id post from /soa/10225/getHealth as api2 where name = 'Ray'</td>
<td>使用子查询的方式，表示api的参数依赖<br>参数依赖关系使用join</td>
<td></td>
</tr>

<tr>
    <td>
    select name, age post from /soa/10225/getHealth where id = 15;
    <br>
    select name, age post from /soa/10225/getHealth where id = 15
    </td>
    <td>支持多个接口并发查询，接口以分号隔开</td>
    <td>结果聚合</td>
</tr>

<tr>
<td>复杂的参数使用APIQLService的Post请求参数来承载，json形式</td>
<td></td>
<td></td>
</tr>

<tr>
<td>如果多个并发的task依赖同一个task，中间层做一层缓存</td>
<td></td>
<td></td>
</tr>

</table>

# 四、Task的数据结构的设计
|task字段	|说明	|备注|
| ---- | ---- | ---- |
|List<String> conditions|	where后面的条件，解析成k=value的形式|	value可以是一个json字符串|
|List<Task> dependencyTasks	|依赖的task的字段，应该以成员的形式放在task中，不依赖的默认并行| |	
|requestType|	httpMethod|	post还是get|
|url|	就是接口地址| |	
|name|	接口的别名| |	
|includeFields|需要的字段| |	
|excludeFields|不需要的字段| |	
|result	|最终的json	| |

