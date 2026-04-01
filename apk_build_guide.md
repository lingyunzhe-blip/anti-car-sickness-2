# 防晕车助手 APK 构建指南

由于当前环境缺少 Gradle 构建工具，无法直接生成 APK 文件。以下是几种获取可安装 APK 的方法：

## 方法一：使用在线构建服务（推荐）

### 使用 GitHub Actions 构建
1. 将项目上传到 GitHub 仓库
2. 在仓库中创建 `.github/workflows/build.yml` 文件：

```yaml
name: Build APK

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
      - name: Grant execute permission for gradlew
        run: chmod +x ./gradlew
      - name: Build with Gradle
        run: ./gradlew assembleDebug
      - name: Upload APK
        uses: actions/upload-artifact@v3
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk
```

3. 手动触发构建工作流
4. 下载构建好的 APK 文件

### 使用 GitLab CI/CD
类似 GitHub Actions，配置 CI/CD 流水线自动构建 APK。

## 方法二：使用 Android Studio 本地构建

1. 下载并安装 [Android Studio](https://developer.android.com/studio)
2. 打开下载的项目文件夹
3. 等待 Gradle 同步完成
4. 点击菜单 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
5. 构建完成后，点击 `locate` 找到 APK 文件

## 方法三：使用其他构建工具

### 使用 Gradle 命令行（本地安装 Gradle）
```bash
# 安装 Gradle（Ubuntu/Debian）
sudo apt update
sudo apt install gradle

# 或使用 SDKMAN
curl -s "https://get.sdkman.io" | bash
sdk install gradle 7.5

# 构建 APK
cd anti_car_sickness_app
chmod +x ./gradlew
./gradlew assembleDebug
```

### 使用 Docker 构建
```bash
# 创建 Dockerfile
cat > Dockerfile << 'EOF'
FROM gradle:7.5-jdk11 AS build
WORKDIR /app
COPY . .
RUN ./gradlew assembleDebug

FROM alpine:latest
WORKDIR /app
COPY --from=build /app/app/build/outputs/apk/debug/app-debug.apk .
EOF

# 构建 Docker 镜像并提取 APK
docker build -t anti-carsickness-builder .
docker run --rm -v $(pwd):/output anti-carsickness-builder cp /app/app-debug.apk /output/
```

## 方法四：使用在线 APK 构建服务

有一些在线服务可以直接上传源代码并构建 APK：

1. [Appcircle](https://appcircle.io/)
2. [Bitrise](https://www.bitrise.io/)
3. [Codemagic](https://codemagic.io/)

## 方法五：使用简化版构建脚本

如果您有 Java 环境但没有 Gradle，可以使用以下简化脚本：

```bash
#!/bin/bash
# 简化的 APK 构建脚本（需要 Java 环境）

# 设置变量
APP_NAME="AntiCarSickness"
PACKAGE_NAME="com.anticarsickness"
VERSION_CODE="1"
VERSION_NAME="1.0"

# 创建输出目录
mkdir -p build/apk

echo "警告：这是一个简化脚本，实际构建需要完整的 Android 构建工具链。"
echo "请使用 Android Studio 或 Gradle 进行完整构建。"
echo ""
echo "您可以参考以下步骤手动构建："
echo "1. 安装 Android Studio"
echo "2. 打开项目文件夹"
echo "3. 点击 Build > Build APK(s)"
echo "4. 在 app/build/outputs/apk/debug/ 目录找到 APK 文件"
```

## 验证 APK 文件

构建完成后，可以通过以下方式验证 APK 文件：

```bash
# 检查 APK 文件信息
file app-debug.apk

# 使用 ADB 安装到设备
adb install app-debug.apk

# 查看应用是否安装成功
adb shell pm list packages | grep $PACKAGE_NAME
```

## 故障排除

### 常见构建错误
1. **Gradle 版本不兼容**：检查 build.gradle 文件中的 Gradle 版本
2. **SDK 版本问题**：确保安装了正确版本的 Android SDK
3. **依赖下载失败**：检查网络连接，或使用国内镜像源

### 安装问题
1. **安装被阻止**：在设置中允许"安装来自未知来源的应用"
2. **签名验证失败**：使用调试签名重新构建
3. **版本冲突**：卸载旧版本后重新安装

如果您遇到任何构建问题，建议使用 Android Studio 进行构建，它会提供更详细的错误信息和解决方案。