
### DEMO 总结<br>
#### 1、MediaPlayer编解码、SurfaceTexture渲染<br>
- 高于8.0版本,MediaPlayer.SEEK_CLOSEST的seek模式比exo的要流畅<br>
    
#### 2、Mediacodec编解码、SurfaceTexture渲染，基于[GoldenVideoClip](https://github.com/meiliqin/GoldenVideoClip)思路开发<br>
- 抽帧预览,如果下面seekbar拖动太快了,解码渲染跟不上,会有跳帧处理,seek回退时不做解码预览<br>
- 无法实现基本编辑预览、Golden项目只有concat操作,采用切换播放源方式<br>
- 与当前exo结合预览的话,整体不统一,开发维护也比较麻烦<br>
- 后期对比抽帧速度（ffmpeg、Glide）、替换现在的抽帧列表方案<br>
    
#### 3、MeiShe SDK
- [x] 片段播放过渡卡顿问题、逐帧预览问题<br>
- [x] 基本编辑(concat、clip、speed、split、copy、图片预览)<br> 
- [x] 转场、特效的结合<br>
- [x] 画布编辑操作<br>
- [ ] 收费项目,必剪和splice在用<br>         
