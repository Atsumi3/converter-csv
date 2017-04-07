# converter-csv
Retrofitを使ってCSVをパースしたかった。

## 使用例

こんなPOJOを作成して、
```java
class Human {
   int id;
   String name;
   int age;
}
```

こんな感じのCSVを読み込んだら
```csv
id,name,age
1,たなか,20
2,山本,34
3,杉下,24
```

```java
List <Human>
```
に変換してくれる感じ

## 注意  
基本的な型 しか対応してません  

一応Data型へのパースをしますが、 "yyyy-MM-dd HH:mm:ss" のフォーマットにしか対応してません

## 使用方法
jitpackを使っています  
Root の build.gradle に以下を追加して、
``` java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Projectのbuild.gradle に以下を追加してください
dependencies {
   ...
	 compile 'com.github.Atsumi3:converter-csv:0.0.1'
}
