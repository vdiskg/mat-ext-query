# mat-ext-query

## 说明
1. 安装 eclipse
使用 eclipse installer 安装 `Eclipse IDE for Eclipse Committers`

2. 打开 Target Platform 配置
菜单 -> Window -> Preferences -> Plug-in Development -> Target Platform

3. 添加 MAT Target Platform
Add -> 选择 Nothing -> Next -> Name: 填写 `MAT Target Platform` -> Add -> 选择 Installation -> Next -> Location: 选择 mat 安装目录

4. 设置 Target Platform
勾选 `MAT Target Platform` -> Apply and Close

5. 设置 Compiler jdk 版本
菜单 -> Window -> Preferences -> Java -> Compiler -> Compiler compliance level: 17

6. 新建 Debug Configuration
新建 Eclipse Application 配置
Run a product: `org.eclipse.mat.ui.rcp.MemoryAnalyzer`
设置运行 jdk 为 jdk17

6. 运行 Debug Configuration
点击 Debug 运行

7. 执行扩展查询
点击 Open Query Browser -> ExtQuery 执行
