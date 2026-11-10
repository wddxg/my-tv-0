# 我的电视·〇（kitkat 简体中文版）

电视视频播放软件，可以自定义视频源。本仓库是 [lizongying/my-tv-0](https://github.com/lizongying/my-tv-0) 的 `kitkat` 分支派生版本，面向安卓 4.4 设备。

## 本版本相对上游的改动

* **信任 HTTP 明文**：OkHttp 的连接规格放开 `CLEARTEXT`，视频源、EPG、远程配置均可直接使用 `http://` 地址；同时保留 `usesCleartextTraffic` 与 `network_security_config` 中的明文许可及用户 CA 信任。
* **界面统一简体中文**：应用强制语言改为简体中文，默认字符串资源与内置远程配置网页（`http://设备IP:34567`）全部改为简体；繁体资源目录也替换为简体内容。
* 应用内「更新应用」指向本仓库的 release，不再跳回上游。
* 构建改用自包含的 GitHub Actions 工作流，无需上游私有签名密钥。

## 使用

* 遥控器中键/触屏单击打开视频列表
* 遥控器右键/触屏双击打开配置
* 遥控器左键/触屏长按打开节目单
* 遥控器返回键关闭视频列表/配置
* 在聚焦视频标题的时候，右键收藏/取消收藏
* 打开配置后，选择远程配置，扫描二维码可以配置视频源等。也可以直接访问远程配置地址 http://0.0.0.0:34567
* 如果视频源地址已配置，并且打开了「应用启动后更新视频源」，应用启动后会自动更新视频源
* 默认遥控器下键/触屏下滑切换到下一个视频。换台反转打开后，逻辑相反

注意：

* 遇到问题可以先考虑重启/恢复默认/清除数据/重新安装等方式自助解决
* 视频源可以设置为本地文件，格式如：`file:///mnt/sdcard/tmp/channels.m3u`、`/channels.m3u`
* 为了方便使用，只支持设置 3 位频道号
* 目前设置代理后，需要重启生效。代理属于全局代理，也就是视频请求及其他请求都会使用代理
* 视频源地址使用 `http://` 即可，不需要改成 https

目前支持的配置格式：

* txt
    ```
    组名,#genre#
    标题,视频地址
    ```
* m3u
    ```
    #EXTM3U x-tvg-url=""
    #EXTINF:-1 tvg-id="" tvg-chno="" tvg-name="标准标题" tvg-logo="图标" group-title="组名",标题
    #EXTVLCOPT:http-user-agent=
    #EXTVLCOPT:http-referrer=
    视频地址
    ```
* json
    ```json
    [
      {
        "group": "组名",
        "name": "标准标题",
        "title": "标题",
        "logo": "图标",
        "number": "频道号",
        "uris": [
          "视频地址"
        ],
        "headers": {
          "user-agent": ""
        }
      }
    ]
    ```

推荐配合使用 [my-tv-server](https://github.com/lizongying/my-tv-server)

## 下载安装

在 [releases](https://github.com/wddxg/my-tv-0/releases/) 下载 `*-kitkat-*` 的 APK。

建议通过 ADB 安装：

```shell
adb install my-tv-0.apk
```

小米电视可以使用小米电视助手进行安装。由于签名与上游官方版不同，从官方版升级时需要先卸载再安装。

## 构建

在仓库的 **Actions → build-kitkat → Run workflow** 手动运行，并在 `ref` 处选择分支或 `v*` 标签：

* 选择标签时，会构建 kitkat 代码、签名、上传 artifact，并自动发布/更新同名 GitHub release。
* 选择分支时，只做构建与 artifact 上传，不发布 release，适合日常验证。

GitHub 对 fork 仓库的 `push` 事件（含推送标签）不自动运行工作流，因此以手动运行为准；工作流里仍保留 `v*` 标签触发配置，便于仓库脱离 fork 关系后直接使用。

如果配置了仓库 Secrets（`KEYSTORE`、`KEYSTORE_PASSWORD`、`ALIAS`、`ALIAS_PASSWORD`），会使用固定密钥签名，后续版本可直接覆盖安装；否则工作流会临时生成签名密钥，仅保证本次 APK 可安装。

![image](./screenshots/Screenshot_20240810_151748.png)
![image](./screenshots/Screenshot_20240813_232847.png)
![image](./screenshots/Screenshot_20240813_232900.png)

## 更新日志

[更新日志](./HISTORY.md)

## 常见问题

* 为什么远程配置视频源文本后，再次打开应用后又恢复到原来的配置？

  如果「应用启动后更新视频源」开启后，且存在视频源地址，则会自动更新，可能会覆盖已保存的视频源文本。

## 赞赏

![image](./screenshots/appreciate.png)

## 感谢

[live](https://github.com/fanmingming/live)
