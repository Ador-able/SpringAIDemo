如果在容器中删除了一些文件, 但是你看到ext4.vhdx占用空间大小无变化, 可以用这个方法优化已释放的空间.

1. 退出docker desktop

2. cmd运行 wsl --shutdown

3. optimize-vhd -Path .\ext4.vhdx -Mode full

4. 等待执行完成, 然后启动docker desktop

#docker


## 问题

>optimize-vhd : 无法将“optimize-vhd”项识别为 cmdlet、函数、脚本文件或可运行程序的名称。请检查名称的拼写，如果包括路径，请确保路径正确，然后再试一次。

这个错误是因为在普通的 PowerShell 中无法直接使用 `optimize-vhd` 命令。这个命令是 Hyper-V 模块的一部分，需要以管理员权限运行 PowerShell 并确保已安装 Hyper-V 功能。

解决步骤：

1. 首先以管理员身份运行 PowerShell（右键点击 PowerShell，选择"以管理员身份运行"）

2. 确保 Hyper-V 功能已安装，运行以下命令：
```powershell
Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V -All
```

3. 导入 Hyper-V 模块（可选）：
```powershell
Import-Module Hyper-V
```

4. 然后再运行优化命令：
```powershell
optimize-vhd -Path .\ext4.vhdx -Mode full
```

注意：
- 必须使用管理员权限
- 确保路径中的 VHDX 文件没有被其他进程占用
- 优化过程可能需要一些时间，请耐心等待
- 建议在优化前备份重要数据

如果你的系统中没有 Hyper-V 功能，需要先在 Windows 功能中启用它（需要重启系统）：
1. 打开控制面板
2. 程序和功能
3. 打开或关闭 Windows 功能
4. 勾选 Hyper-V 并安装